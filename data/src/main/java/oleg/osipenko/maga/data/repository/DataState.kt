package oleg.osipenko.maga.data.repository

/**
 * Represents state of the data source
 */
data class DataState<T>(
        private val data: T,
        private val throwable: Throwable?,
        private val networkError: Boolean
)