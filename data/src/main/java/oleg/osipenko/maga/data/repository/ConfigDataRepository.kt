package oleg.osipenko.maga.data.repository

import android.annotation.TargetApi
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.SharedPreferences
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.repository.ConfigRepository
import oleg.osipenko.domain.states.ConfigSyncState
import oleg.osipenko.domain.states.Error
import oleg.osipenko.domain.states.Success
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.entities.ConfigurationRecord
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.network.dto.ApiConfigurationResponse
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException
import java.time.Duration

/**
 * Implementation of [ConfigRepository].
 */
class ConfigDataRepository(
  private val db: MoviesDb,
  private val api: TMDBApi,
  private val sp: SharedPreferences
) : ConfigRepository {

  companion object {
    private const val CONFIG_LOADED = "is config loaded"
    private const val CONFIG_WORK = "config syncing"
    private const val FLEX = 3L
    private const val INTERVAL = 5L
  }

  init {
    if (!sp.contains(CONFIG_LOADED)) {
      GlobalScope.launch(Dispatchers.IO) {
        loadConfiguration()
        scheduleConfigSyncWork()
        sp.edit().putBoolean(CONFIG_LOADED, true).apply()
      }
    }
  }

  override fun updateConfiguration(): ConfigSyncState {
    Timber.d(Thread.currentThread().name)

    val configResponse: Response<ApiConfigurationResponse>?
    return try {
      configResponse = api.getConfigCall().execute()
      saveConfig(configResponse.body())
      Success
    } catch (e: IOException) {
      Timber.e(e)
      Error(e)
    }
  }

  private suspend fun loadConfiguration() {
    try {
      val configResponse = api.getConfig().await()
      saveConfig(configResponse)
    } catch (e: HttpException) {
      Timber.e(e)
    } catch (e: UnknownHostException) {
      Timber.e(e)
    }
  }

  private fun saveConfig(configResponse: ApiConfigurationResponse?) {
    configResponse?.images?.let { imagesResponse ->
      db.runInTransaction {
        db.configDao().deleteAll()
        db.configDao().insertConfiguration(
          ConfigurationRecord(
            1,
            imagesResponse.secureBaseUrl,
            imagesResponse.posterSizes,
            imagesResponse.backdropSizes
          )
        )
      }
    }
  }

  override fun configuration(): LiveData<Configuration> =
      savedConfig()

  private fun savedConfig(): LiveData<Configuration> =
    Transformations.map(db.configDao().configuration) {
      Configuration(
        it?.baseUrl ?: "",
        it?.posterSizes ?: emptyList(),
        it?.backdropSizes ?: emptyList()
      )
    }

  @TargetApi(Build.VERSION_CODES.O)
  private fun scheduleConfigSyncWork() {
    val syncConstraints = Constraints.Builder()
      .setRequiresCharging(true)
      .setRequiresDeviceIdle(true)
      .setRequiredNetworkType(NetworkType.UNMETERED)
      .build()

    val configSyncWork = PeriodicWorkRequest.Builder(
      ConfigSyncWorker::class.java,
      Duration.ofHours(FLEX),
      Duration.ofDays(INTERVAL)
    )
      .setConstraints(syncConstraints)
      .build()

    WorkManager.getInstance().enqueueUniquePeriodicWork(
      CONFIG_WORK, ExistingPeriodicWorkPolicy.KEEP, configSyncWork
    )
  }
}
