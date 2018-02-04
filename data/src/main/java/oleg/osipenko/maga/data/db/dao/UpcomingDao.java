package oleg.osipenko.maga.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import oleg.osipenko.maga.data.db.dbo.Movie;
import oleg.osipenko.maga.data.db.dbo.Upcoming;

/**
 * Upcoming movies data access object interface
 */
@Dao
public interface UpcomingDao {

    @Insert void insertNowPlaying(List<Upcoming> upcoming);
    @Query("SELECT *  FROM upcoming INNER JOIN movies ON upcoming.movieId = movies.id") Flowable<List<Movie>> getUpcoming();
}
