package oleg.osipenko.domain.states

/**
 * Possible states of network request
 */
enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

/**
 * Represents the status of network request
 */
data class NetworkState(
        val status: Status,
        val throwableMessage: String? = null) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}