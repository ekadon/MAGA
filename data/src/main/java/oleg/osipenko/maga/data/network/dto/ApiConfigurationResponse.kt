package oleg.osipenko.maga.data.network.dto

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

/**
 * Data transfer object represents API configuration varues:
 * https://developers.themoviedb.org/3/configuration/get-api-configuration
 */

@JsonObject
data class ApiConfigurationResponse(
        @JsonField var images: ImagesResponse?,
        @JsonField(name = ["change_keys"]) var changeKeys: List<String>?
) {
    constructor(): this(null, null)
}

/**
 * Data transfer object represents configuration varues
 * for requesting images
 */
@JsonObject
data class ImagesResponse(
        @JsonField(name = ["base_url"]) var baseUrl: String?,
        @JsonField(name = ["secure_base_url"]) var secureBaseUrl: String?,
        @JsonField(name = ["backdrop_sizes"]) var backdropSizes: List<String>?,
        @JsonField(name = ["logo_sizes"]) var logoSizes: List<String>?,
        @JsonField(name = ["poster_sizes"]) var posterSizes: List<String>?,
        @JsonField(name = ["profile_sizes"]) var profileSizes: List<String>?,
        @JsonField(name = ["still_sizes"]) var stillSizes: List<String>?
) {
    constructor(): this(null, null, null, null, null, null, null)
}