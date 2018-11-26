package oleg.osipenko.domain.repository

import android.arch.lifecycle.LiveData
import oleg.osipenko.domain.entities.Configuration

/**
 * Entry point to the config data layer.
 */
interface ConfigRepository {
  /**
   * Returns the API configuration object.
   */
  fun configuration(): LiveData<Configuration>
}
