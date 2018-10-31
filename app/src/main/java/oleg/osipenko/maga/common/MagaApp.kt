package oleg.osipenko.maga.common

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import oleg.osipenko.maga.data.di.dataModule
import oleg.osipenko.maga.mainactivity.MainActivity
import org.koin.android.ext.android.startKoin

class MagaApp : Application() {
  override fun onCreate() {
    super.onCreate()
    AndroidThreeTen.init(this)

    startKoin(this, listOf(dataModule, MainActivity.activityModel))
  }
}