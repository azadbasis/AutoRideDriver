package org.autoride.driver.services.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.autoride.driver.model.DistanceInfo;

@Database(entities = {DistanceInfo.class}, version = 1)
public abstract class DBDistance extends RoomDatabase {
    public abstract DistanceDao distanceDao();
}