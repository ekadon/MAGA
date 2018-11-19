package oleg.osipenko.maga.data.network.dto

import oleg.osipenko.maga.data.entities.GenreRecord

/**
 * Data transfer object represents array of genres
 * https://developers.themoviedb.org/3/genres/get-movie-list
 */
data class GenresResponse(var genres: List<GenreRecord>?) {
  constructor() : this(null)
}
