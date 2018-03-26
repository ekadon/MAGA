package oleg.osipenko.maga.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import oleg.osipenko.maga.data.entities.MovieRecord;
import oleg.osipenko.maga.data.entities.NowPlaying;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface NowPlayingDao {

    @Insert(onConflict = REPLACE)
    void saveNowPlaying(List<NowPlaying> nowPlayings);

    @Query("SELECT movies.id, movies.title, movies.posterPath, movies.releaseDate, movies.voteAverage, " +
        "GROUP_CONCAT(genres.name) AS genres " +
        "FROM nowplaying INNER JOIN movie_genres " +
        "JOIN movies ON movies.id = movie_genres.movieId " +
        "JOIN genres ON genres.id = movie_genres.genreId " +
        "WHERE nowplaying.movieId = movies.id " +
        "GROUP BY movies.id")
    LiveData<List<MovieRecord>> getNowPlaying();
}
