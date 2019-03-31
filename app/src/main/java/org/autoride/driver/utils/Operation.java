package org.autoride.driver.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.autoride.driver.SpeedMeter.GPSData;
import org.autoride.driver.app.AutoRideDriverApps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goldenreign on 9/9/2018.
 */

public class Operation {
    public static void saveString(String keyValue, String getValue) {

        SharedPreferences sharedPreferences = AutoRideDriverApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyValue, getValue);
        editor.apply();

    }

    public static void saveSwitchStatus(String keyValue, boolean getValue){
        SharedPreferences sharedPreferences = AutoRideDriverApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(keyValue, getValue);
        editor.apply();
    }
    public static boolean getSwitchStatus(String keyValue, boolean defaultValue){
        SharedPreferences sharedPreferences = AutoRideDriverApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(keyValue, defaultValue);
    }

    public static String getString(String keyValue, String defaultValue) {

        SharedPreferences sharedPreferences = AutoRideDriverApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyValue, defaultValue);
    }

    public static void saveSMS(Context ctx, String key, String value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static SharedPreferences sharedPreferences;

    public static String getSMS(Context ctx, String key) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void IntSaveToSharedPreference(Context ctx, String key, int value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int getIntegerSharedPreference(Context ctx, String key, int defaultValue) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }



    //region CODE for saving location locally while offline
    private static List<GPSData> listLocal = new ArrayList<>();

    public static void saveGPSDataList(Context ctx, GPSData loc) {
        listLocal.add(loc);
        Gson gson = new Gson();
        String json = gson.toJson(listLocal);//convert into json string-it is a data saving structure
        saveString( "locationList", json);
    }

    public static List<GPSData> GetLocallySavedLocations(Context ctx) {
        String str = getString( "locationList","");
        if (str.length() > 0) {
            Gson gson = new Gson();
            listLocal = gson.fromJson(str, new TypeToken<List<GPSData>>() {
            }.getType());
            return listLocal;
        } else return new ArrayList<GPSData>();
    }

    public static void ClearLocallySavedLocations(Context ctx) {
        listLocal = new ArrayList<>();

        Gson gson = new Gson();
        String json = gson.toJson(listLocal);
        saveString( "locationList", json);
    }


    public static boolean IsOnline(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}

