package oleg.osipenko.domain.entities

/**
 * Information about specific movie
 */
data class Movie(
        val posterPath: String = "",
        val adult: Boolean = false,
        val overview: String = "",
        val releaseDate: String = "",
        val genres: List<String> = emptyList(),
        val id: Int = Int.MIN_VALUE,
        val originalTitle: String = "",
        val originalLanguage: String = "",
        val title: String = "",
        val backdropPath: String = "",
        val popularity: Float = Float.MIN_VALUE,
        val voteCount: Int = Int.MIN_VALUE,
        val video: String = "",
        val voteAverage: Float = Float.MIN_VALUE
)