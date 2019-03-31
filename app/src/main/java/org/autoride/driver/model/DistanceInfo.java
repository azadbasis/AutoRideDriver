package org.autoride.driver.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "distance_info")
public class DistanceInfo {

    @ColumnInfo(name = "driver_id")
    @NonNull
    @PrimaryKey
    private String driverId;

    @ColumnInfo(name = "driver_name")
    private String driverName;

    @ColumnInfo(name = "rider_id")
    private String riderId;

    @ColumnInfo(name = "rider_name")
    private String riderName;

    @ColumnInfo(name = "start_lat")
    private double startLat;

    @ColumnInfo(name = "start_lng")
    private double startLng;

    @ColumnInfo(name = "current_lat")
    private double currentLat;

    @ColumnInfo(name = "current_lng")
    private double currentLng;

    @ColumnInfo(name = "end_lat")
    private double endLat;

    @ColumnInfo(name = "end_lng")
    private double endLng;

    @ColumnInfo(name = "total_distance")
    private float totalDistance;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }
}