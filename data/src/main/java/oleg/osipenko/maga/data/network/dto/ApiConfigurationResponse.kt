package oleg.osipenko.maga.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object represents API configuration varues:
 * https://developers.themoviedb.org/3/configuration/get-api-configuration
 */

data class ApiConfigurationResponse(
    var images: ImagesResponse?,
    @SerializedName("change_keys") var changeKeys: List<String>?
) {
    constructor(): this(null, null)
}

/**
 * Data transfer object represents configuration varues
 * for requesting images
 */
data class ImagesResponse(
    @SerializedName("base_url") var baseUrl: String?,
    @SerializedName("secure_base_url") var secureBaseUrl: String?,
    @SerializedName("backdrop_sizes") var backdropSizes: List<String>?,
    @SerializedName("logo_sizes") var logoSizes: List<String>?,
    @SerializedName("poster_sizes") var posterSizes: List<String>?,
    @SerializedName("profile_sizes") var profileSizes: List<String>?,
    @SerializedName("still_sizes") var stillSizes: List<String>?
) {
    constructor(): this(null, null, null, null, null, null, null)
}
