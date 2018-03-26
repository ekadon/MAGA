package oleg.osipenko.maga.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import oleg.osipenko.maga.data.db.MoviesDb.Companion.DB_VERSION
import oleg.osipenko.maga.data.db.dao.*
import oleg.osipenko.maga.data.entities.*

/**
 * Persistence database
 */
@Database(
        entities = [ConfigurationRecord::class,
            MovieRecord::class,
            GenreRecord::class,
            MovieGenreRecord::class,
            NowPlaying::class,
            Upcoming::class],
        version = DB_VERSION
)
@TypeConverters(oleg.osipenko.maga.data.db.TypeConverters::class)
abstract class MoviesDb : RoomDatabase() {
    abstract fun nowPlayingDao(): NowPlayingDao
    abstract fun moviesDao(): MoviesDao
    abstract fun genresDao(): GenresDao
    abstract fun movieGenresDao(): MovieGenresDao
    abstract fun configDao(): ConfigurationDao

    companion object {
        /**
         * MoviesDb version
         */
        const val DB_VERSION = 1

        /**
         * MoviesDb name
         */
        private const val DB_NAME = "maga"

        fun create(context: Context): MoviesDb {
            return Room.databaseBuilder(context, MoviesDb::class.java, DB_NAME).build()
        }
    }
}