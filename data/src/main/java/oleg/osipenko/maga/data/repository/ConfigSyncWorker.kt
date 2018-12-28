package oleg.osipenko.maga.data.repository

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import oleg.osipenko.domain.repository.ConfigRepository
import oleg.osipenko.domain.states.Error
import oleg.osipenko.domain.states.Success
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Worker class for periodical synchronization of Config API object.
 */
class ConfigSyncWorker(context: Context, workerParams: WorkerParameters) :
  Worker(context, workerParams), KoinComponent {

  private val configRepository: ConfigRepository by inject()

  override fun doWork(): Result =
    when (configRepository.updateConfiguration()) {
      is Success -> Result.success()
      is Error   -> Result.retry()
    }
}
