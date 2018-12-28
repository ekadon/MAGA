package oleg.osipenko.domain.repository

import android.arch.lifecycle.LiveData
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.states.ConfigSyncState

/**
 * Entry point to the config data layer.
 */
interface ConfigRepository {

  /**
   * Updates ands saves into DB API configuration object.
   */
  fun updateConfiguration(): ConfigSyncState

  /**
   * Returns the API configuration object.
   */
  fun configuration(): LiveData<Configuration>
}
