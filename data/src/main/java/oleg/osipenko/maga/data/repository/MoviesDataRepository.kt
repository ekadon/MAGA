package oleg.osipenko.maga.data.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.MoviesDataState
import oleg.osipenko.domain.states.NetworkState
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.entities.MovieGenreRecord
import oleg.osipenko.maga.data.entities.NowPlaying
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class MoviesDataRepository(
        private val db: MoviesDb,
        private val api: TMDBApi,
        private val ioExecutor: Executor) : MoviesRepository {

    override fun nowPlaying(): MoviesDataState<List<Movie>> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        api.getNowPlaying(
                TMDBApi.KEY,
                Locale.getDefault().language,
                Locale.getDefault().country,
                1).enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>?, t: Throwable?) {
                networkState.value = NetworkState.error(t?.message)
            }

            override fun onResponse(call: Call<MoviesResponse>?, response: Response<MoviesResponse>?) {
                if (isLoaded(response)) {
                    handleSuccessfulResponse(networkState, response)
                } else {
                    networkState.value = NetworkState.error(extractErrorMessage(response))
                }
            }
        })

        return MoviesDataState(
                Transformations.map(db.nowPlayingDao().nowPlaying) { it?.map {
                        Movie(
                                it.posterPath ?: "",
                                it.adult ?: false,
                                it.overview ?: "",
                                it.releaseDate ?: "",
                                it.genres?.split(",") ?: emptyList<String>(),
                                it.id ?: Int.MIN_VALUE,
                                it.originalTitle ?: "",
                                it.originalLanguage ?: "",
                                it.title ?: "",
                                it.backdropPath ?: "",
                                it.popularity ?: Float.MIN_VALUE,
                                it.voteCount ?: Int.MIN_VALUE,
                                it.video ?: "",
                                it.voteAverage ?: Float.MIN_VALUE
                        )} ?: emptyList()
                }, networkState)
    }

    private fun isLoaded(response: Response<out Any>?): Boolean {
        return response?.isSuccessful == true && response.errorBody() == null
    }

    private fun handleSuccessfulResponse(networkState: MutableLiveData<NetworkState>, response: Response<MoviesResponse>?) {
        networkState.value = NetworkState.LOADED
        response?.body()?.results?.let {
            val movieGenres: MutableList<MovieGenreRecord> = ArrayList()
            for (movie in it) {
                val genreMovies: List<MovieGenreRecord> = movie.genreIds?.map {
                    MovieGenreRecord(movieId = movie.id ?: Int.MIN_VALUE, genreId = it)
                }?.toList() ?: emptyList()
                movieGenres.addAll(genreMovies)
            }

            val nowPlaying = it.map { NowPlaying(it.id ?: Int.MIN_VALUE) }

            db.runInTransaction {
                db.moviesDao().insertMovies(it)
                db.movieGenresDao().insertMovieGenres(movieGenres)
                db.nowPlayingDao().saveNowPlaying(nowPlaying)
            }
        }
    }

    private fun extractErrorMessage(response: Response<out Any>?): String {
        return if (response?.errorBody() != null) {
            val errorMessage = response.errorBody()?.string()!!
            errorMessage
        } else {
            UNKNOWN_ERROR
        }
    }

    override fun comingSoon(): MoviesDataState<List<Movie>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val UNKNOWN_ERROR = "unknown error happened"
    }
}
