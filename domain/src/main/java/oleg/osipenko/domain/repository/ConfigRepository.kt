package oleg.osipenko.domain.repository

import android.arch.lifecycle.LiveData
import oleg.osipenko.domain.entities.Configuration

interface ConfigRepository {
  /**
   * Returns the API configuration object
   */
  fun configuration(): LiveData<Configuration>
}