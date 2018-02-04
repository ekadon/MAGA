package oleg.osipenko.maga.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import oleg.osipenko.maga.data.db.dbo.Movie;

/**
 * Movies data access object interface
 */
@Dao
public interface MoviesDao {

    @Insert void insertMovies(List<Movie> movies);

    @Query("SELECT * FROM movies") Flowable<List<Movie>> getMovies();

    @Query("SELECT * FROM movies WHERE id = :movieId") Flowable<List<Movie>> getMovie(int movieId);
}
