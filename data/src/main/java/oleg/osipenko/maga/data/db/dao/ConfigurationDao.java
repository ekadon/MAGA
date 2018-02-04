package oleg.osipenko.maga.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import oleg.osipenko.maga.data.db.dbo.Configuration;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Configuration data access object interface
 */
@Dao
public interface ConfigurationDao {

    @Insert(onConflict = REPLACE) void insertConfiguration(Configuration configuration);

    @Query("SELECT * FROM configuration") Flowable<List<Configuration>> getConfiguration();
}
