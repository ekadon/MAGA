package oleg.osipenko.maga.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * MoviesDb genre entity
 */
@Entity(tableName = "genres")
data class GenreRecord(
    @PrimaryKey val id: Int, val name: String?
)
