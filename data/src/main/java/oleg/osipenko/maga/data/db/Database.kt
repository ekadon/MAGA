package oleg.osipenko.maga.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import oleg.osipenko.maga.data.db.dao.ConfigurationDao
import oleg.osipenko.maga.data.db.dao.MoviesDao
import oleg.osipenko.maga.data.db.dao.NowPlayingDao
import oleg.osipenko.maga.data.db.dao.UpcomingDao
import oleg.osipenko.maga.data.db.dbo.Configuration
import oleg.osipenko.maga.data.db.dbo.Movie
import oleg.osipenko.maga.data.db.dbo.NowPlaying
import oleg.osipenko.maga.data.db.dbo.Upcoming

/**
 * Database version
 */
const val DB_VERSION = 1

/**
 * Database name
 */
const val DB_NAME = "maga"

/**
 * Persistence database
 */
@Database(
        entities = [Configuration::class,
            Movie::class,
            NowPlaying::class,
            Upcoming::class],
        version = DB_VERSION
)
@TypeConverters(oleg.osipenko.maga.data.db.TypeConverters::class)
abstract class Database : RoomDatabase() {
    abstract fun configurationDao(): ConfigurationDao
    abstract fun moviesDao(): MoviesDao
    abstract fun nowPlayingDao(): NowPlayingDao
    abstract fun upcomingDao(): UpcomingDao
}