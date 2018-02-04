package oleg.osipenko.maga.data.db.dbo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Class representing movie object
 */
@Entity(tableName = "movies")
data class Movie(
        @PrimaryKey var id: Int,
        var posterPath: String?,
        var adult: Boolean?,
        var overview: String?,
        var releaseDate: String?,
        var genreIds: List<Int>?,
        var originalTitle: String?,
        var originalLanguage: String?,
        var title: String?,
        var backdropPath: String?,
        var popularity: Float?,
        var voteCount: Int?,
        var video: Boolean?,
        var voteAverage: Float?
)