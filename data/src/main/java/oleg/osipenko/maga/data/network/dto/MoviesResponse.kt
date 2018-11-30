package oleg.osipenko.maga.data.network.dto

import com.google.gson.annotations.SerializedName
import oleg.osipenko.maga.data.entities.MovieRecord

/**
 * Data transfer objects represents array of movies e.g.:
 * Discover movies https://developers.themoviedb.org/3/discover/movie-discover.
 */
data class MoviesResponse(
  var page: Int?, var results: List<MovieRecord>?,
  @SerializedName("total_pages") var totalPages: Int?,
  @SerializedName("total_results") var totalResults: Int?
) {
  constructor() : this(
    0, null, 0, 0
  )
}
