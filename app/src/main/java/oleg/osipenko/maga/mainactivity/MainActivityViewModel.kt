package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import oleg.osipenko.domain.repository.MoviesRepository

/**
 * View model for [MainActivity]
 */
class MainActivityViewModel(private val repository: MoviesRepository) : ViewModel() {

  private val _nowPlayingProgress = MutableLiveData<Boolean>()
  private val _upcomingProgress = MutableLiveData<Boolean>()
  private val _error = MutableLiveData<String?>()
  private val viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  val nowPlayingMovies = repository.nowPlaying()
  val nowPlayingShowProgressBar: LiveData<Boolean>
    get() = _nowPlayingProgress
  val error: LiveData<String?>
    get() = _error
  val comingSoonMovies = repository.comingSoon()
  val comingSoonShowProgressBar: LiveData<Boolean>
    get() = _upcomingProgress
  val configObservable = repository.configuration()

  fun refreshNowPlaying() {
    launchDataLoad(_nowPlayingProgress) {
      repository.refreshNowPlaying()
    }
  }

  fun refreshUpcoming() {
    launchDataLoad(_upcomingProgress) {
      repository.refreshComingSoon()
    }
  }

  private fun launchDataLoad(progress: MutableLiveData<Boolean>, block: suspend () -> Unit): Job {
    return uiScope.launch {
      try {
        progress.value = true
        block()
      } catch (error: Exception) {
        _error.value = error.message
      } finally {
        progress.value = false
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }
}