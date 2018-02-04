package oleg.osipenko.maga.data.db.dbo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey

/**
 * Represents the collection of Now playing movies
 */
@Entity(foreignKeys =
[ForeignKey(entity = Movie::class,
        parentColumns = ["id"],
        childColumns = ["movieId"],
        onUpdate = CASCADE)])
data class NowPlaying(
        @PrimaryKey var movieId: Int
)