package org.autoride.driver.services.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.autoride.driver.model.DistanceInfo;

import java.util.List;

@Dao
public interface DistanceDao {

    @Insert
    public void putDistance(DistanceInfo distanceInfo);

    @Query("SELECT * FROM distance_info")
    public List<DistanceInfo> getDistance();

    @Update
    public void updateDistance(DistanceInfo distanceInfo);

    @Query("DELETE FROM distance_info")
    public int deleteDistanceTable();

    @Delete
    public void deleteDistance(DistanceInfo distanceInfo);
}