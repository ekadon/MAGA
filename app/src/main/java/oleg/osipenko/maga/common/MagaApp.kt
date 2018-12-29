package oleg.osipenko.maga.common

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.maga.data.di.dataModule
import oleg.osipenko.maga.mainactivity.MainFragment
import org.koin.android.ext.android.startKoin
import timber.log.Timber

/**
 * Subclass of [Application].
 */
class MagaApp : Application() {
  override fun onCreate() {
    super.onCreate()
    AndroidThreeTen.init(this)
    GlobalScope.launch {
      AndroidThreeTen.init(applicationContext)
    }
    Timber.plant(Timber.DebugTree())

    startKoin(this, listOf(
      dataModule,
      appModule,
      MainFragment.mainFragmentModule))
  }
}
