package oleg.osipenko.domain.entities

/**
 * Information required for loading images
 */
data class Configuration(
        val baseUrl: String = "",
        val posterSizes: List<String> = emptyList(),
        val backdropSizes: List<String> = emptyList()
)
