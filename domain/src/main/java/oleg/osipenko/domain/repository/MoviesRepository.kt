package oleg.osipenko.domain.repository

import android.arch.lifecycle.LiveData
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.entities.Movie

/**
 * Entry point to the data layer
 */
interface MoviesRepository {
    /**
     * Returns Now playing movies
     */
    fun nowPlaying(): LiveData<List<Movie>>

    /**
     * Reloads Now playing movies
     */
    suspend fun refreshNowPlaying()

    /**
     * Returns Coming soon movies
     */
    fun comingSoon(): LiveData<List<Movie>>

    /**
     * Reloads Coming soon movies
     */
    suspend fun refreshComingSoon()

    /**
     * Returns the API configuration object
     */
    fun configuration(): LiveData<Configuration>
}