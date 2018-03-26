package oleg.osipenko.maga.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * MoviesDb entity linking together movie and genre
 */
@Entity(
        tableName = "movie_genres",
        indices = arrayOf(Index("movieId"), Index("genreId")),
        foreignKeys = [
            (ForeignKey(
                    entity = MovieRecord::class,
                    parentColumns = ["id"],
                    childColumns = ["movieId"],
                    onUpdate = CASCADE,
                    onDelete = CASCADE)),
            (ForeignKey(
                    entity = GenreRecord::class,
                    parentColumns = ["id"],
                    childColumns = ["genreId"],
                    onUpdate = CASCADE,
                    onDelete = CASCADE))]
)
data class MovieGenreRecord(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val movieId: Int,
        val genreId: Int
)