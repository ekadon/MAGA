package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.ConfigRepository
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.DataState
import oleg.osipenko.domain.states.Status

/**
 * View model for [MainActivity].
 */
class MainFragmentViewModel(
    moviesRepository: MoviesRepository, configRepository: ConfigRepository
) : ViewModel() {

  private val nowPlayingObservable: DataState<List<Movie>> =
      moviesRepository.nowPlaying()
  private val nowPlayingNetworkState = nowPlayingObservable.networkState
  private val comingSoonObservable: DataState<List<Movie>> =
      moviesRepository.comingSoon()
  private val comingSoonNetworkState = comingSoonObservable.networkState

  val nowPlayingMovies = nowPlayingObservable.movies
  val nowPlayingShowProgressBar = Transformations.map(
    comingSoonNetworkState
  ) { it?.status == Status.RUNNING }
  val nowPlayingErrorMessage =
      Transformations.map(comingSoonNetworkState) { it.throwableMessage }
  val comingSoonMovies = comingSoonObservable.movies
  val comingSoonShowProgressBar = Transformations.map(
    comingSoonNetworkState
  ) { it?.status == Status.RUNNING }
  val comingSoonErrorMessage =
      Transformations.map(comingSoonNetworkState) { it.throwableMessage }
  val configObservable = configRepository.configuration()
}
