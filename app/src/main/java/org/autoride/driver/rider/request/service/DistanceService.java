package org.autoride.driver.rider.request.service;

import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.autoride.driver.model.DistanceInfo;
import org.autoride.driver.services.local.DBDistance;

public class DistanceService extends Service {

    private static final String TAG = "DistanceService";
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private double oldLatitude, oldLongitude, newLatitude, newLongitude;
    private float totalDistance;
    private DBDistance dbDistance;
    private DistanceInfo distanceInfo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        localCalculation();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "local Distance Service Destroy");
        super.onDestroy();
    }

    private void localCalculation() {

        distanceInfo = new DistanceInfo();
        dbDistance = Room.databaseBuilder(getApplicationContext(), DBDistance.class, "db_distance").allowMainThreadQueries().build();
        distanceInfo.setDriverId(dbDistance.distanceDao().getDistance().get(0).getDriverId());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                newLatitude = location.getLatitude();
                newLongitude = location.getLongitude();

                if (oldLatitude != newLatitude) {

                    totalDistance = meterDistanceBetweenPoints(oldLatitude, oldLongitude, newLatitude, newLongitude);
                    oldLatitude = newLatitude;
                    oldLongitude = newLongitude;

                    Toast.makeText(DistanceService.this, "local calculate_d " + totalDistance, Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "local calculate_d " + totalDistance);

                    float preTotal = dbDistance.distanceDao().getDistance().get(0).getTotalDistance();
                    Toast.makeText(DistanceService.this, "local pre_d " + preTotal, Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "local pre_d " + preTotal);

                    float currentTotal = preTotal + totalDistance;
                    Toast.makeText(DistanceService.this, "local current_d " + currentTotal, Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "local current_d " + currentTotal);

                    // distance update
                    distanceInfo.setTotalDistance(currentTotal);
                    Toast.makeText(DistanceService.this, "local get_current_d " + distanceInfo.getTotalDistance(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "local get_current_d " + distanceInfo.getTotalDistance());

                    Log.w("TAG", "local did " + distanceInfo.getDriverId());
                    dbDistance.distanceDao().updateDistance(distanceInfo);

                    distanceInfo.setTotalDistance(0.0f);
                    Toast.makeText(DistanceService.this, "local after_d " + distanceInfo.getTotalDistance(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "local after_d " + distanceInfo.getTotalDistance() + "\n" + "\n");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        long minTime = 5 * 1000; // Minimum time interval for update in seconds 30*1000
        long minDistance = 0; // Minimum distance change for update in meters

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
    }

    private float meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 6371000; // 6371 in km, 6371000 in meter
        double dLat = Math.toRadians(lat_b - lat_a);
        double dLng = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        // Toast.makeText(DriverTracking.this, "calculated distance" + dist + "," + Math.abs((float) oldLongitude - (float) newLongitude), Toast.LENGTH_LONG).show();
        // System.out.println("**********this is distance calculation**********" + dist);
        return dist;
    }
}