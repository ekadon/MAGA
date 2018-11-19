package oleg.osipenko.maga.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Holds the values required for API configuration
 */
@Entity(tableName = "config")
data class ConfigurationRecord(
    @PrimaryKey val id: Int,
    val baseUrl: String?,
    val posterSizes: List<String>?,
    val backdropSizes: List<String>?
)
