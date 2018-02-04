package oleg.osipenko.maga.data.db.dbo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Represents the collection of upcoming movies
 */
@Entity(foreignKeys =
[ForeignKey(entity = Movie::class,
        parentColumns = ["id"],
        childColumns = ["movieId"],
        onUpdate = ForeignKey.CASCADE)])
data class Upcoming(
        @PrimaryKey var movieId: Int
)