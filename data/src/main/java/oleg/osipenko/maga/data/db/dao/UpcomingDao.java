package oleg.osipenko.maga.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import oleg.osipenko.maga.data.entities.MovieRecord;
import oleg.osipenko.maga.data.entities.Upcoming;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UpcomingDao {

    @Insert(onConflict = REPLACE)
    void saveUpcoming(List<Upcoming> upcomings);

    @Query("SELECT movies.id, movies.title, movies.posterPath, movies.backdropPath, movies.releaseDate, movies.voteAverage, " +
        "GROUP_CONCAT(genres.name) AS genres " +
        "FROM upcoming INNER JOIN movie_genres " +
        "JOIN movies ON movies.id = movie_genres.movieId " +
        "JOIN genres ON genres.id = movie_genres.genreId " +
        "WHERE upcoming.movieId = movies.id " +
        "GROUP BY movies.id")
    LiveData<List<MovieRecord>> getUpcoming();

    @Query("DELETE FROM upcoming")
    void deleteAll();
}
