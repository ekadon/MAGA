package oleg.osipenko.domain.repository

import android.arch.lifecycle.LiveData
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.states.MoviesDataState

/**
 * Entry point to the data layer
 */
interface MoviesRepository {
    /**
     * Returns Now playing movies
     */
    fun nowPlaying(): MoviesDataState<List<Movie>>

    /**
     * Returns Coming soon movies
     */
    fun comingSoon(): MoviesDataState<List<Movie>>

    /**
     * Returns the API configuration object
     */
    fun configuration(): LiveData<Configuration>
}