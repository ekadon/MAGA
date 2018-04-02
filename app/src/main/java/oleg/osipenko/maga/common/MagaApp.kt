package oleg.osipenko.maga.common

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MagaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}