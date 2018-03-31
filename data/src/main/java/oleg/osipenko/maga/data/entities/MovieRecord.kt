package oleg.osipenko.maga.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Data movie entity
 */
@Entity(tableName = "movies")
data class MovieRecord(
        @PrimaryKey var id: Int?,
        var title: String?,
        var overview: String?,
        @SerializedName("poster_path") var posterPath: String?,
        var adult: Boolean?,
        @SerializedName("release_date") var releaseDate: String?,
        var genres: String?,
        @Ignore @SerializedName("genre_ids") var genreIds: List<Int>?,
        @SerializedName("original_title") var originalTitle: String?,
        @SerializedName("original_language") var originalLanguage: String?,
        @SerializedName("backdrop_path") var backdropPath: String?,
        var popularity: Float?,
        @SerializedName("vote_count") var voteCount: Int?,
        var video: String?,
        @SerializedName("vote_average") var voteAverage: Float?
) {
    constructor(id: Int, title: String?, posterPath: String?, backdropPath: String?, releaseDate: String?, voteAverage: Float?, genres: String?):
            this(id, title, null, posterPath, null, releaseDate, genres, null, null, null,
            backdropPath, null, null, null, voteAverage)
}