package oleg.osipenko.maga.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import oleg.osipenko.maga.data.db.MoviesDb.Companion.DB_VERSION
import oleg.osipenko.maga.data.db.dao.ConfigurationDao
import oleg.osipenko.maga.data.db.dao.GenresDao
import oleg.osipenko.maga.data.db.dao.MovieGenresDao
import oleg.osipenko.maga.data.db.dao.MoviesDao
import oleg.osipenko.maga.data.db.dao.NowPlayingDao
import oleg.osipenko.maga.data.db.dao.UpcomingDao
import oleg.osipenko.maga.data.entities.ConfigurationRecord
import oleg.osipenko.maga.data.entities.GenreRecord
import oleg.osipenko.maga.data.entities.MovieGenreRecord
import oleg.osipenko.maga.data.entities.MovieRecord
import oleg.osipenko.maga.data.entities.NowPlaying
import oleg.osipenko.maga.data.entities.Upcoming

/**
 * Persistence database
 */
@Database(
  entities = [
    ConfigurationRecord::class,
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
  abstract fun upcomingDao(): UpcomingDao

  companion object {
    /**
     * MoviesDb version
     */
    const val DB_VERSION = 1

    private const val DB_NAME = "maga"

    fun create(context: Context): MoviesDb =
      Room.databaseBuilder(context, MoviesDb::class.java, DB_NAME).build()
  }
}
