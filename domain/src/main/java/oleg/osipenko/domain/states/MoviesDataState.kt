package oleg.osipenko.domain.states

import android.arch.lifecycle.LiveData

/**
 * Holder for data associated with particular request
 */
data class MoviesDataState<T>(
        val movies: LiveData<T>,
        val networkState: LiveData<NetworkState>
)