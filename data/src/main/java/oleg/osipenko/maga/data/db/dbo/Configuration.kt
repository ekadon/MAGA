package oleg.osipenko.maga.data.db.dbo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Contains values for API configuration
 */
@Entity
data class Configuration(
        @PrimaryKey var id: Int,
        @Embedded var images: ImageConfiguration?
)

/**
 * Contains varues for images configuration
 */
data class ImageConfiguration(
        var baseUrl: String?,
        var secureBaseUrl: String?
)