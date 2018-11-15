package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.MoviesDataState
import oleg.osipenko.domain.states.Status

/**
 * View model for [MainActivity]
 */
class MainActivityViewModel(private val repository: MoviesRepository) : ViewModel() {

  private val nowPlayingObservable: MoviesDataState<List<Movie>> = repository.nowPlaying()
  private val nowPlayingNetworkState = nowPlayingObservable.networkState
  private val comingSoonObservable: MoviesDataState<List<Movie>> = repository.comingSoon()
  private val comingSoonNetworkState = comingSoonObservable.networkState

  val nowPlayingMovies = nowPlayingObservable.movies
  val nowPlayingShowProgressBar = Transformations.map(comingSoonNetworkState) { it?.status == Status.RUNNING }
  val nowPlayingErrorMessage = Transformations.map(comingSoonNetworkState) { it.throwableMessage }
  val comingSoonMovies = comingSoonObservable.movies
  val comingSoonShowProgressBar = Transformations.map(comingSoonNetworkState) { it?.status == Status.RUNNING }
  val comingSoonErrorMessage = Transformations.map(comingSoonNetworkState) { it.throwableMessage }
  val configObservable = repository.configuration()
}