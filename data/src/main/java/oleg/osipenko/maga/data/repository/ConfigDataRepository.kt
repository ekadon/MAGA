package oleg.osipenko.maga.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.domain.entities.Configuration
import oleg.osipenko.domain.repository.ConfigRepository
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.entities.ConfigurationRecord
import oleg.osipenko.maga.data.network.TMDBApi
import retrofit2.HttpException
import java.net.UnknownHostException

class ConfigDataRepository(
    private val db: MoviesDb, private val api: TMDBApi
) : ConfigRepository {

  init {
    GlobalScope.launch(Dispatchers.IO) {
      loadConfiguration()
    }
  }

  private suspend fun loadConfiguration() {
    try {
      val configResponse = api.getConfig().await()
      configResponse.images?.let { imagesResponse ->
        db.runInTransaction {
          db.configDao().deleteAll()
          db.configDao().insertConfiguration(
            ConfigurationRecord(
              1, imagesResponse.secureBaseUrl, imagesResponse.posterSizes,
              imagesResponse.backdropSizes
            )
          )
        }
      }
    } catch (e: HttpException) {

    } catch (e: UnknownHostException) {

    }
  }

  override fun configuration(): LiveData<Configuration> {
    return Transformations.map(db.configDao().configuration) {
      Configuration(
        it?.baseUrl ?: "", it?.posterSizes ?: emptyList(),
        it?.backdropSizes ?: emptyList()
      )
    }
  }
}