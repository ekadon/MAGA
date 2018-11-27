package oleg.osipenko.maga.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import oleg.osipenko.maga.data.entities.GenreRecord

/**
 * Genres data access object interface.
 */
@Dao
interface GenresDao {
  /**
   * Saves Genre records into the database.
   */
  @Insert(onConflict = REPLACE)
  fun insertGenres(genres: List<GenreRecord>)
}
