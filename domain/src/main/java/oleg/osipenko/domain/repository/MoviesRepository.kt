package oleg.osipenko.domain.repository

import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.states.DataState

/**
 * Entry point to the movies data layer.
 */
interface MoviesRepository {
  /**
   * Returns Now playing movies.
   */
  fun nowPlaying(): DataState<List<Movie>>

  /**
   * Returns Coming soon movies.
   */
  fun comingSoon(): DataState<List<Movie>>
}
