package oleg.osipenko.maga.common

import android.app.Application
import android.os.StrictMode
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.maga.data.di.dataModule
import oleg.osipenko.maga.mainactivity.MainActivity
import org.koin.android.ext.android.startKoin

class MagaApp : Application() {
  override fun onCreate() {
    val vmPolicy =
        StrictMode.VmPolicy.Builder()
          .detectActivityLeaks()
          .detectCleartextNetwork()
          .detectContentUriWithoutPermission()
          .detectFileUriExposure()
          .detectLeakedClosableObjects()
          .detectLeakedRegistrationObjects()
          .detectLeakedSqlLiteObjects()
          .penaltyLog()
          .penaltyDeath()
          .build()
    StrictMode.setVmPolicy(vmPolicy)
    val tPolicy =
        StrictMode.ThreadPolicy.Builder()
          .detectDiskWrites()
          .detectCustomSlowCalls()
          .detectNetwork()
          .detectResourceMismatches()
          .detectUnbufferedIo()
          .penaltyLog()
          .penaltyDeath()
          .build()
    StrictMode.setThreadPolicy(tPolicy)

    super.onCreate()
    GlobalScope.launch {
      AndroidThreeTen.init(applicationContext)
    }

    startKoin(this, listOf(dataModule, MainActivity.activityModel))
  }
}