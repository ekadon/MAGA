package oleg.osipenko.domain.repository

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
}