package oleg.osipenko.maga.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.MoviesDataState
import oleg.osipenko.domain.states.NetworkState
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.entities.*
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.network.dto.ApiConfigurationResponse
import oleg.osipenko.maga.data.network.dto.GenresResponse
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class MoviesDataRepository(
        private val db: MoviesDb,
        private val api: TMDBApi,
        private val ioExecutor: Executor) : MoviesRepository {

    private var currentDate: LocalDate = LocalDate.now()

    init {
        loadGenres()
        loadConfiguration()
    }

    private fun loadGenres() {
        api.getGenres().enqueue(object : Callback<GenresResponse> {
            override fun onFailure(call: Call<GenresResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<GenresResponse>?, response: Response<GenresResponse>?) {
                if (isLoaded(response)) {
                    response?.body()?.genres?.let {
                        ioExecutor.execute {
                            db.genresDao().insertGenres(it + GenreRecord(Int.MIN_VALUE, ""))
                        }
                    }
                }
            }
        })
    }

    private fun loadConfiguration() {
        api.getConfig().enqueue(object : Callback<ApiConfigurationResponse> {
            override fun onFailure(call: Call<ApiConfigurationResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ApiConfigurationResponse>?, response: Response<ApiConfigurationResponse>?) {
                response?.body()?.images?.let {
                    ioExecutor.execute {
                        db.runInTransaction {
                            db.configDao().deleteAll()
                            db.configDao().insertConfiguration(ConfigurationRecord(1, it.secureBaseUrl, it.posterSizes, it.backdropSizes))
                        }
                    }
                }
            }
        })
    }

    override fun configuration(): LiveData<Configuration> {
        return Transformations.map(db.configDao().configuration) {
            Configuration(it?.baseUrl ?: "", it?.posterSizes ?: emptyList(), it?.backdropSizes ?: emptyList())
        }
    }

    override fun nowPlaying(): MoviesDataState<List<Movie>> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        api.getNowPlaying(1)
                .enqueue(MoviesCallback(networkState, handleSuccessfulNowPlayingResponse))

        return MoviesDataState(
                Transformations.map(db.nowPlayingDao().nowPlaying) { it?.map(movieMapper) ?: emptyList() },
                networkState)
    }

    override fun comingSoon(): MoviesDataState<List<Movie>> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        api.getUpcoming(1)
                .enqueue(MoviesCallback(networkState, handleSuccessfulUpcomingResponse))

        return MoviesDataState(
                Transformations.map(db.upcomingDao().upcoming) { it?.map(movieMapper) ?: emptyList() },
                networkState)
    }

    private val handleSuccessfulNowPlayingResponse = { networkState: MutableLiveData<NetworkState>, response: Response<MoviesResponse>? ->
        handleResponse(networkState, response, { movies ->
            val movieGenres = getMovieGenres(movies)

            val nowPlaying = movies.map { NowPlaying(it.id ?: Int.MIN_VALUE) }

            saveDataToDb(object : ResponseSaveAction(movies, movieGenres) {
                override fun run() {
                    super.run()
                    db.nowPlayingDao().deleteAll()
                    db.nowPlayingDao().saveNowPlaying(nowPlaying)
                }
            })
        })
    }

    private val handleSuccessfulUpcomingResponse = { networkState: MutableLiveData<NetworkState>, response: Response<MoviesResponse>? ->
        handleResponse(networkState, response, { movies ->
            val movieGenres = getMovieGenres(movies)

            val upcoming = movies.map { Upcoming(it.id ?: Int.MIN_VALUE) }

            saveDataToDb(object : ResponseSaveAction(movies, movieGenres) {
                override fun run() {
                    super.run()
                    db.upcomingDao().deleteAll()
                    db.upcomingDao().saveUpcoming(upcoming)
                }
            })
        })
    }

    private fun handleResponse(networkState: MutableLiveData<NetworkState>, response: Response<MoviesResponse>?, block: (List<MovieRecord>) -> Unit) {
        networkState.value = NetworkState.LOADED

        val movies = response?.body()?.results?.filter(dateFilter)

        movies?.let(block)
    }

    private val dateFilter = { movieRecord: MovieRecord ->
        val movieDate = LocalDate.parse(movieRecord.releaseDate)
            val dateDelta = Period.between(currentDate, movieDate)
            if (dateDelta.isNegative) {
                dateDelta.days < TWO_WEEKS && Math.abs(dateDelta.toTotalMonths()) < 1
            } else {
                true
            }
    }

    private fun saveDataToDb(saveAction: ResponseSaveAction) {
        ioExecutor.execute {
            db.runInTransaction(saveAction)
        }
    }

    abstract inner class ResponseSaveAction(private val movies: List<MovieRecord>, private val movieGenres: List<MovieGenreRecord>) : Runnable {
        override fun run() {
            db.moviesDao().insertMovies(movies)
            db.movieGenresDao().insertMovieGenres(movieGenres)
        }
    }

    private fun getMovieGenres(movies: List<MovieRecord>): List<MovieGenreRecord> {
        return movies.flatMap { movieRecord ->
            if (movieRecord.genreIds?.isNotEmpty() != false) {
                movieRecord.genreIds?.map { MovieGenreRecord(movieId = movieRecord.id ?: Int.MIN_VALUE, genreId = it) }?.toList() ?: emptyList()
            } else {
                listOf(MovieGenreRecord(movieId = movieRecord.id ?: Int.MIN_VALUE, genreId = Int.MIN_VALUE))
            }
        }.toList()
    }

    private fun extractErrorMessage(response: Response<out Any>?): String {
        return if (response?.errorBody() != null) {
            val errorMessage = response.errorBody()?.string()!!
            errorMessage
        } else {
            UNKNOWN_ERROR
        }
    }

    private inner class MoviesCallback(private val networkState: MutableLiveData<NetworkState>, private val successfulCallback: (MutableLiveData<NetworkState>, Response<MoviesResponse>?) -> Unit) : Callback<MoviesResponse> {
        override fun onFailure(call: Call<MoviesResponse>?, t: Throwable?) {
            networkState.value = NetworkState.error(t?.message)
        }

        override fun onResponse(call: Call<MoviesResponse>?, response: Response<MoviesResponse>?) {
            if (isLoaded(response)) {
                successfulCallback.invoke(networkState, response)
            } else {
                networkState.value = NetworkState.error(extractErrorMessage(response))
            }
        }
    }

    private fun isLoaded(response: Response<out Any>?): Boolean {
        return response?.isSuccessful == true && response.errorBody() == null
    }

    private val movieMapper = { movieRecord: MovieRecord ->
        Movie(movieRecord.posterPath ?: "",
                movieRecord.adult ?: false,
                movieRecord.overview ?: "",
                movieRecord.releaseDate ?: "",
                movieRecord.genres?.split(",") ?: emptyList<String>(),
                movieRecord.id ?: Int.MIN_VALUE,
                movieRecord.originalTitle ?: "",
                movieRecord.originalLanguage ?: "",
                movieRecord.title ?: "",
                movieRecord.backdropPath ?: "",
                movieRecord.popularity ?: Float.MIN_VALUE,
                movieRecord.voteCount ?: Int.MIN_VALUE,
                movieRecord.video ?: "",
                movieRecord.voteAverage ?: Float.MIN_VALUE)
    }

    companion object {
        const val UNKNOWN_ERROR = "unknown error happened"
        const val TWO_WEEKS = 14
    }
}
