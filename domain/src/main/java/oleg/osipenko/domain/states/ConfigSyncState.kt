package oleg.osipenko.domain.states

/**
 * Set of states for Config sync operation.
 */
sealed class ConfigSyncState

/**
 * Success state.
 */
object Success: ConfigSyncState()

/**
 * Error state.
 *
 * @param t: Throwable error happened while trying to sync config.
 */
data class Error(val t: Throwable): ConfigSyncState()
