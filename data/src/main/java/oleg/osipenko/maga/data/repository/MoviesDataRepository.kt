package oleg.osipenko.maga.data.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.DataState
import oleg.osipenko.domain.states.NetworkState
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.entities.GenreRecord
import oleg.osipenko.maga.data.entities.MovieRecord
import oleg.osipenko.maga.data.entities.NowPlaying
import oleg.osipenko.maga.data.entities.Upcoming
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import org.threeten.bp.LocalDate
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Implementation of [MoviesRepository].
 */
class MoviesDataRepository(
    private val db: MoviesDb, private val api: TMDBApi
) : MoviesRepository {

  private var currentDate: LocalDate = LocalDate.now()

  init {
    GlobalScope.launch(Dispatchers.IO) {
      loadGenres()
    }
  }

  private suspend fun loadGenres() {
    try {
      val genresResponse = api.getGenres().await()
      genresResponse.genres?.let {
        db.genresDao().insertGenres(
          it + GenreRecord(
            Int.MIN_VALUE, ""
          )
        )
      }
    } catch (e: HttpException) {
      Timber.e(e)
    } catch (e: UnknownHostException) {
      Timber.e(e)
    }
  }

  override fun nowPlaying(): DataState<List<Movie>> {
    val networkState = MutableLiveData<NetworkState>()
    networkState.value = NetworkState.LOADING

    refreshNowPlaying(networkState)

    return DataState(
      Transformations.map(db.nowPlayingDao().nowPlaying) {
        it?.map(MovieMapper) ?: emptyList()
      }, networkState
    )
  }

  private fun refreshNowPlaying(networkState: MutableLiveData<NetworkState>) {
    GlobalScope.launch(Dispatchers.IO) {
      loadNowPlaying(networkState)
    }
  }

  private suspend fun loadNowPlaying(
      networkState: MutableLiveData<NetworkState>, page: Int = DEF_START_PAGE,
      loadedMovies: List<MovieRecord> = emptyList()
  ) {
    try {
      val nowPlayingResponse = api.getNowPlaying(page).await()

      val currentPage = nowPlayingResponse.page ?: DEF_START_PAGE
      val hasMore = hasMorePages(nowPlayingResponse)
      val combinedResults =
          loadedMovies + (nowPlayingResponse.results ?: emptyList())
      if (hasMore) {
        val nextPage = currentPage + 1
        loadNowPlaying(
          networkState, nextPage, combinedResults
        )
      } else {
        // filter old movies
        val filteredMovies = combinedResults.filter { movieRecord ->
          nowPlayingDateFilter(currentDate, movieRecord)
        }

        // get genres
        val movieGenres = MovieGenresExtractor(filteredMovies)

        val nowPlaying =
            filteredMovies.map { NowPlaying(it.id ?: Int.MIN_VALUE) }

        db.runInTransaction {
          db.moviesDao().insertMovies(filteredMovies)
          db.movieGenresDao().insertMovieGenres(movieGenres)
          if (currentPage == DEF_START_PAGE) {
            db.nowPlayingDao().deleteAll()
          }
          db.nowPlayingDao().saveNowPlaying(nowPlaying)
        }
        networkState.postValue(NetworkState.LOADED)
      }
    } catch (e: HttpException) {
      networkState.postValue(NetworkState.error(e.message))
    } catch (e: UnknownHostException) {
      networkState.postValue(NetworkState.error(e.message))
    }
  }

  override fun comingSoon(): DataState<List<Movie>> {
    val networkState = MutableLiveData<NetworkState>()
    networkState.value = NetworkState.LOADING

    refreshComingSoon(networkState)

    return DataState(
      Transformations.map(db.upcomingDao().upcoming) {
        it?.map(MovieMapper) ?: emptyList()
      }, networkState
    )
  }

  private fun refreshComingSoon(networkState: MutableLiveData<NetworkState>) {
    GlobalScope.launch(Dispatchers.IO) {
      loadComingSoon(networkState)
    }
  }

  private suspend fun loadComingSoon(
      networkState: MutableLiveData<NetworkState>, page: Int = DEF_START_PAGE,
      loadedMovies: List<MovieRecord> = emptyList()
  ) {
    try {
      val upcomingResponse = api.getUpcoming(page).await()

      // load next pages if more
      val currentPage = upcomingResponse.page ?: DEF_START_PAGE
      val hasMore = hasMorePages(upcomingResponse)
      val combinedResults =
          loadedMovies + (upcomingResponse.results ?: emptyList())

      if (hasMore) {
        val nextPage = currentPage + 1
        loadComingSoon(
          networkState, nextPage, combinedResults
        )
      } else {
        // filter old movies
        val filteredMovies = combinedResults.filter { movieRecord ->
          upcomingDateFilter(currentDate, movieRecord)
        }

        // get genres
        val movieGenres = MovieGenresExtractor(filteredMovies)

        val upcoming = filteredMovies.map { Upcoming(it.id ?: Int.MIN_VALUE) }

        db.runInTransaction {
          db.moviesDao().insertMovies(filteredMovies)
          db.movieGenresDao().insertMovieGenres(movieGenres)
          if (currentPage == DEF_START_PAGE) {
            db.upcomingDao().deleteAll()
          }
          db.upcomingDao().saveUpcoming(upcoming)
        }
        networkState.postValue(NetworkState.LOADED)
      }
    } catch (e: HttpException) {
      networkState.postValue(NetworkState.error(e.message))
    } catch (e: UnknownHostException) {
      networkState.postValue(NetworkState.error(e.message))
    }
  }

  private fun hasMorePages(response: MoviesResponse): Boolean {
    val currentPage = response.page ?: DEF_START_PAGE
    val totalPages = response.totalPages ?: DEF_START_PAGE
    return currentPage < totalPages
  }

  companion object {
    const val MONTH = -30
    const val DEF_START_PAGE = 1
  }
}
