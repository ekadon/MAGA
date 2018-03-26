package oleg.osipenko.maga.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import oleg.osipenko.maga.data.entities.MovieRecord

/**
 * Movies data access object interface
 */
@Dao
interface MoviesDao {

    @Insert
    fun insertMovies(movies: List<MovieRecord>)
}
