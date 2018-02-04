package oleg.osipenko.maga.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import oleg.osipenko.maga.data.db.dbo.Movie;
import oleg.osipenko.maga.data.db.dbo.NowPlaying;

/**
 * Now playing data access object interface
 */
@Dao
public interface NowPlayingDao {

    @Insert void insertNowPlaying(List<NowPlaying> nowPlaying);

    @Query("SELECT * FROM nowplaying INNER JOIN movies WHERE nowplaying.movieId = movies.id") Flowable<List<Movie>> getNowPlaying();
}
