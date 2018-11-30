package oleg.osipenko.maga.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import oleg.osipenko.maga.data.entities.MovieGenreRecord

/**
 * Data access object interface for movie-genres relations.
 */
@Dao
interface MovieGenresDao {
  /**
   *Saves movie-genre records into the database.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertMovieGenres(moviesGenres: List<MovieGenreRecord>)
}
