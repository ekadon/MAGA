package oleg.osipenko.maga.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import oleg.osipenko.maga.data.entities.ConfigurationRecord;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Configuration data access object interface
 */
@Dao
public interface ConfigurationDao {

    @Insert(onConflict = REPLACE) void insertConfiguration(ConfigurationRecord configuration);

    @Query("SELECT * FROM configuration")
    LiveData<ConfigurationRecord> getConfiguration();
}
