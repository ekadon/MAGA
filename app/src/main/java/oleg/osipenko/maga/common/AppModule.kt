package oleg.osipenko.maga.common

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

const val APP_PREFS = "app_preferences"

val appModule = module {
  single {
    androidContext().applicationContext.getSharedPreferences(
      APP_PREFS, Context.MODE_PRIVATE
    )
  }
}
