package oleg.osipenko.maga.data.network.dto

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

/**
 * Data transfer objects represents array of movies e.g.:
 * Now playing response https://developers.themoviedb.org/3/movies/get-now-playing
 * Upcoming response https://developers.themoviedb.org/3/movies/get-upcoming
 */
@JsonObject
data class MoviesResponse(
        @JsonField var page: Int?,
        @JsonField var results: List<MovieResponse>?,
        @JsonField(name = ["total_pages"]) var totalPages: Int?,
        @JsonField(name = ["total_results"]) var totalResults: Int?
) {
    constructor(): this(
            0, null, 0, 0
    )
}

/**
 * API response object containing information about movie
 */
@JsonObject
data class MovieResponse(
        @JsonField(name = ["poster_path"]) var posterPath: String?,
        @JsonField var adult: Boolean?,
        @JsonField var overview: String?,
        @JsonField(name = ["release_date"]) var releaseDate: String?,
        @JsonField(name = ["genre_ids"]) var genreIds: List<Int>?,
        @JsonField var id: Int?,
        @JsonField(name = ["original_title"]) var originalTitle: String?,
        @JsonField(name = ["original_language"]) var originalLanguage: String?,
        @JsonField var title: String?,
        @JsonField(name = ["backdrop_path"]) var backdropPath: String?,
        @JsonField var popularity: Float?,
        @JsonField(name = ["vote_count"]) var voteCount: Int?,
        @JsonField var video: Boolean?,
        @JsonField(name = ["vote_average"]) var voteAverage: Float?
) {
    constructor(): this(null, null, null, null, null, 0, null, null, null, null, 0f, 0, null, 0f)
}
