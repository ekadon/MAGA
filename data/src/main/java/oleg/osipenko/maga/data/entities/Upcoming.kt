package oleg.osipenko.maga.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey

/**
 * Represents the collection of upcoming movies
 */
@Entity(foreignKeys =
[ForeignKey(entity = MovieRecord::class,
        parentColumns = ["id"],
        childColumns = ["movieId"],
        onUpdate = CASCADE)])
data class Upcoming(
        @PrimaryKey var movieId: Int
)