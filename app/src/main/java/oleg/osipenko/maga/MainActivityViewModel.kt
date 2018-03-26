package oleg.osipenko.maga

import android.arch.lifecycle.ViewModel
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.domain.states.MoviesDataState

class MainActivityViewModel(private val repository: MoviesRepository): ViewModel() {
    private val nowPlayingObservable: MoviesDataState<List<Movie>> = repository.nowPlaying()
    val nowPlayingMovies = nowPlayingObservable.movies
    val nowPlayingNetworkState = nowPlayingObservable.networkState
}