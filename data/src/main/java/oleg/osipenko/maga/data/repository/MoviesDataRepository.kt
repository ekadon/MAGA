package oleg.osipenko.maga.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.MoviesDataState
import oleg.osipenko.domain.states.NetworkState
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.entities.*
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.temporal.ChronoUnit

class MoviesDataRepository(
    private val db: MoviesDb, private val api: TMDBApi
) : MoviesRepository {

  private var currentDate: LocalDate = LocalDate.now()

  init {
    GlobalScope.launch(Dispatchers.IO) {
      loadGenres()
      loadConfiguration()
    }
  }

  private suspend fun loadGenres() {
    val genresResponse = api.getGenres().await()
    genresResponse.genres?.let {
      db.genresDao().insertGenres(it + GenreRecord(Int.MIN_VALUE, ""))
    }
  }

  private suspend fun loadConfiguration() {
    val configResponse = api.getConfig().await()
    configResponse.images?.let { imagesResponse ->
      db.runInTransaction {
        db.configDao().deleteAll()
        db.configDao()
          .insertConfiguration(ConfigurationRecord(1, imagesResponse.secureBaseUrl, imagesResponse.posterSizes, imagesResponse.backdropSizes))
      }
    }
  }

  override fun configuration(): LiveData<Configuration> {
    return Transformations.map(db.configDao().configuration) {
      Configuration(it?.baseUrl ?: "", it?.posterSizes ?: emptyList(), it?.backdropSizes ?: emptyList())
    }
  }

  override fun nowPlaying(): MoviesDataState<List<Movie>> {
    val networkState = MutableLiveData<NetworkState>()
    networkState.value = NetworkState.LOADING

    refreshNowPlaying(networkState)

    return MoviesDataState(Transformations.map(db.nowPlayingDao().nowPlaying) { it?.map(movieMapper) ?: emptyList() }, networkState)
  }

  private fun refreshNowPlaying(networkState: MutableLiveData<NetworkState>) {
    GlobalScope.launch(Dispatchers.IO) {
      loadNowPlaying(networkState)
    }
  }

  private suspend fun loadNowPlaying(
      networkState: MutableLiveData<NetworkState>, page: Int = DEFAULT_START_PAGE, loadedMovies: List<MovieRecord> = emptyList()
  ) {
    try {
      val nowPlayingResponse = api.getNowPlaying(page).await()

      val currentPage = nowPlayingResponse.page ?: DEFAULT_START_PAGE
      val hasMore = hasMorePages(nowPlayingResponse)
      val combinedResults = loadedMovies + (nowPlayingResponse.results ?: emptyList())
      if (hasMore) {
        val nextPage = currentPage + 1
        loadNowPlaying(networkState, nextPage, combinedResults)
      } else {
        // filter old movies
        val filteredMovies = combinedResults.filter { nowPlayingDateFilter(it) }

        // get genres
        val movieGenres = getMovieGenres(filteredMovies)

        val nowPlaying = filteredMovies.map { NowPlaying(it.id ?: Int.MIN_VALUE) }

        db.runInTransaction {
          db.moviesDao().insertMovies(filteredMovies)
          db.movieGenresDao().insertMovieGenres(movieGenres)
          if (currentPage == DEFAULT_START_PAGE) {
            db.nowPlayingDao().deleteAll()
          }
          db.nowPlayingDao().saveNowPlaying(nowPlaying)
        }
        networkState.postValue(NetworkState.LOADED)
      }
    } catch (e: Exception) {
      networkState.postValue(NetworkState.error(e.message))
    }
  }

  override fun comingSoon(): MoviesDataState<List<Movie>> {
    val networkState = MutableLiveData<NetworkState>()
    networkState.value = NetworkState.LOADING

    refreshComingSoon(networkState)

    return MoviesDataState(
        Transformations.map(db.upcomingDao().upcoming) { it?.map(movieMapper) ?: emptyList() }, networkState
    )
  }

  private fun refreshComingSoon(networkState: MutableLiveData<NetworkState>) {
    GlobalScope.launch(Dispatchers.IO) {
      loadComingSoon(networkState)
    }
  }

  private suspend fun loadComingSoon(
      networkState: MutableLiveData<NetworkState>, page: Int = DEFAULT_START_PAGE, loadedMovies: List<MovieRecord> = emptyList()
  ) {
    try {
      val upcomingResponse = api.getUpcoming(page).await()

      // load next pages if more
      val currentPage = upcomingResponse.page ?: DEFAULT_START_PAGE
      val hasMore = hasMorePages(upcomingResponse)
      val combinedResults = loadedMovies + (upcomingResponse.results ?: emptyList())

      if (hasMore) {
        val nextPage = currentPage + 1
        loadComingSoon(networkState, nextPage, combinedResults)
      } else {
        // filter old movies
        val filteredMovies = combinedResults.filter { upcomingDateFilter(it) }

        // get genres
        val movieGenres = getMovieGenres(filteredMovies)

        val upcoming = filteredMovies.map { Upcoming(it.id ?: Int.MIN_VALUE) }

        db.runInTransaction {
          db.moviesDao().insertMovies(filteredMovies)
          db.movieGenresDao().insertMovieGenres(movieGenres)
          if (currentPage == DEFAULT_START_PAGE) {
            db.upcomingDao().deleteAll()
          }
          db.upcomingDao().saveUpcoming(upcoming)
        }
        networkState.postValue(NetworkState.LOADED)
      }
    } catch (e: Exception) {
      networkState.postValue(NetworkState.error(e.message))
    }
  }

  private fun hasMorePages(response: MoviesResponse): Boolean {
    return response.page ?: DEFAULT_START_PAGE < response.totalPages ?: DEFAULT_START_PAGE
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

  private fun getMovieGenres(movies: List<MovieRecord>?): List<MovieGenreRecord> {
    return movies?.flatMap { movieRecord ->
      if (movieRecord.genreIds?.isNotEmpty() != false) {
        movieRecord.genreIds?.map { MovieGenreRecord(movieId = movieRecord.id ?: Int.MIN_VALUE, genreId = it) }?.toList() ?: emptyList()
      } else {
        listOf(MovieGenreRecord(movieId = movieRecord.id ?: Int.MIN_VALUE, genreId = Int.MIN_VALUE))
      }
    }?.toList() ?: emptyList()
  }

  private val movieMapper = { movieRecord: MovieRecord ->
    Movie(
        movieRecord.posterPath ?: "",
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
        movieRecord.voteAverage ?: Float.MIN_VALUE
    )
  }

  companion object {
    const val MONTH = -30
    const val DEFAULT_START_PAGE = 1
  }
}
