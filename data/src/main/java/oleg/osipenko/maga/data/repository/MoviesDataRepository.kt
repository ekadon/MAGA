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
import org.threeten.bp.temporal.ChronoUnit
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

        loadNowPlaying(networkState) { state, callback -> handleSuccessfulNowPlayingResponse(state, callback) }

        return MoviesDataState(
                Transformations.map(db.nowPlayingDao().nowPlaying) { it?.map(movieMapper) ?: emptyList() },
                networkState)
    }

    private fun loadNowPlaying(networkState: MutableLiveData<NetworkState>, page: Int = DEFAULT_START_PAGE, successfulCallback: (MutableLiveData<NetworkState>, Response<MoviesResponse>?) -> Unit) {
        loadMovies(api.getNowPlaying(page), MoviesCallback(networkState, successfulCallback))
    }

    private fun loadMovies(call: Call<MoviesResponse>, callback: MoviesCallback) {
        call.enqueue(callback)
    }

    private fun handleSuccessfulNowPlayingResponse(networkState: MutableLiveData<NetworkState>, response: Response<MoviesResponse>?) {
        val currentPage = response?.body()?.page ?: DEFAULT_START_PAGE
        val hasMore = hasMorePages(response)
        if (hasMore) {
            val nextPage = currentPage + 1
            loadNowPlaying(networkState, nextPage) { state, callback -> handleSuccessfulNowPlayingResponse(state, callback) }
        }
        val filteredMovies = response?.body()?.results?.filter { nowPlayingDateFilter(it) }
        val movieGenres = getMovieGenres(filteredMovies)
        handleResponse(networkState, hasMore, filteredMovies, { movies ->

            val nowPlaying = movies.map { NowPlaying(it.id ?: Int.MIN_VALUE) }

            saveDataToDb(object : ResponseSaveAction(movies, movieGenres) {
                override fun run() {
                    super.run()
                    if (currentPage == DEFAULT_START_PAGE) {
                        db.nowPlayingDao().deleteAll()
                    }
                    db.nowPlayingDao().saveNowPlaying(nowPlaying)
                }
            })
        })
    }

    override fun comingSoon(): MoviesDataState<List<Movie>> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        loadComingSoon(networkState) { state, callback -> handleSuccessfulUpcomingResponse(state, callback) }

        return MoviesDataState(
                Transformations.map(db.upcomingDao().upcoming) { it?.map(movieMapper) ?: emptyList() },
                networkState)
    }

    private fun loadComingSoon(networkState: MutableLiveData<NetworkState>, page: Int = DEFAULT_START_PAGE, successfulCallback: (MutableLiveData<NetworkState>, Response<MoviesResponse>?) -> Unit) {
        loadMovies(api.getUpcoming(page), MoviesCallback(networkState, successfulCallback))
    }

    private fun handleSuccessfulUpcomingResponse(networkState: MutableLiveData<NetworkState>, response: Response<MoviesResponse>?) {
        val currentPage = response?.body()?.page ?: DEFAULT_START_PAGE
        val hasMore = hasMorePages(response)
        if (hasMore) {
            val nextPage = currentPage + 1
            loadComingSoon(networkState, nextPage) { state, callback -> handleSuccessfulUpcomingResponse(state, callback) }
        }
        val filteredMovies = response?.body()?.results?.filter { upcomingDateFilter(it) }
        val movieGenres = getMovieGenres(filteredMovies)
        handleResponse(networkState, hasMore, filteredMovies, { movies ->

            val upcoming = movies.map { Upcoming(it.id ?: Int.MIN_VALUE) }

            saveDataToDb(object : ResponseSaveAction(movies, movieGenres) {
                override fun run() {
                    super.run()
                    if (currentPage == DEFAULT_START_PAGE) {
                        db.upcomingDao().deleteAll()
                    }
                    db.upcomingDao().saveUpcoming(upcoming)
                }
            })
        })
    }

    private fun handleResponse(networkState: MutableLiveData<NetworkState>, hasMore: Boolean, movies: List<MovieRecord>?, block: (List<MovieRecord>) -> Unit) {
        if (!hasMore) {
            networkState.postValue(NetworkState.LOADED)
        }
        if (movies != null && movies.isNotEmpty()) {
            block.invoke(movies)
        }
    }

    private fun hasMorePages(response: Response<MoviesResponse>?): Boolean {
        return response?.body()?.page ?: DEFAULT_START_PAGE < response?.body()?.totalPages ?: DEFAULT_START_PAGE
    }

    private fun nowPlayingDateFilter(movieRecord: MovieRecord): Boolean {
        val movieDate = LocalDate.parse(movieRecord.releaseDate)
        val daysSinceRelease = ChronoUnit.DAYS.between(currentDate, movieDate)
        return daysSinceRelease in (MONTH + 1)..0;
    }

    private fun upcomingDateFilter(movieRecord: MovieRecord): Boolean {
        val movieDate = LocalDate.parse(movieRecord.releaseDate)
        val dateDelta = Period.between(currentDate, movieDate)
        return !dateDelta.isNegative
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

    private fun getMovieGenres(movies: List<MovieRecord>?): List<MovieGenreRecord> {
        return movies?.flatMap { movieRecord ->
            if (movieRecord.genreIds?.isNotEmpty() != false) {
                movieRecord.genreIds?.map { MovieGenreRecord(movieId = movieRecord.id ?: Int.MIN_VALUE, genreId = it) }?.toList() ?: emptyList()
            } else {
                listOf(MovieGenreRecord(movieId = movieRecord.id ?: Int.MIN_VALUE, genreId = Int.MIN_VALUE))
            }
        }?.toList() ?: emptyList()
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
                successfulCallback(networkState, response)
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
        const val MONTH = -30
        const val DEFAULT_START_PAGE = 1
    }
}
