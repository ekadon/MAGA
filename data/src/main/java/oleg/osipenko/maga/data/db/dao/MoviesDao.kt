package oleg.osipenko.maga.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import oleg.osipenko.maga.data.entities.MovieRecord

/**
 * Movies data access object interface
 */
@Dao
interface MoviesDao {

    @Insert(onConflict = REPLACE)
    fun insertMovies(movies: List<MovieRecord>)
}
