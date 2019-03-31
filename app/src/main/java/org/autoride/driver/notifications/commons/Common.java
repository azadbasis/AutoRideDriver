package org.autoride.driver.notifications.commons;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.autoride.driver.R;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.listeners.updates.listeners.ParserListenerString;
import org.autoride.driver.model.FareInfo;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.data.ManagerData;
import org.autoride.driver.notifications.helper.FCMClient;
import org.autoride.driver.notifications.helper.FCMService;
import org.autoride.driver.notifications.helper.GoogleAPI;
import org.autoride.driver.notifications.helper.RetrofitClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Common implements AppsConstants, DriverApiUrl {

    private Handler handler;
    private String accessToken, rememberToken, driverId;
    public static Location mLastKnownLocation = null;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String googleUrl = "https://maps.googleapis.com";
    private static final String fcmUrl = "https://fcm.googleapis.com/";

    public Common() {
    }

    public Common(Handler handler, String accessToken, String rememberToken, String driverId) {
        this.handler = handler;
        this.accessToken = accessToken;
        this.rememberToken = rememberToken;
        this.driverId = driverId;
    }

    public static double priceFormula(double km, double min) {
        return ((FareInfo.getBaseFare() + (km * FareInfo.getDistanceRate()) + (min * FareInfo.getTimeRate())) - (FareInfo.getDiscount() + FareInfo.getCoupon()));
    }

    public static GoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(googleUrl).create(GoogleAPI.class);
    }

    public static FCMService getFCMService() {
        return FCMClient.getClient(fcmUrl).create(FCMService.class);
    }

    public void startSetDriverLocationTask() {
        handler.post(setDriverLocation);

        /*double d = distance(previousLat, currentLat, previousLng, currentLng);
        Toast.makeText(AutoRideDriverApps.getInstance(), "Distancec " + d, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "startSetDriverLocationTask: " + d);*/
    }

    public void stopSetDriverLocationTask() {
        if (setDriverLocation != null) {
            handler.removeCallbacks(setDriverLocation);
        }
    }

    private Runnable setDriverLocation = new Runnable() {
        @Override
        public void run() {
            if (mLastKnownLocation != null) {
                driverLocationUpdate();
            } else {
                Log.i("common ", "location not detected");
            }
            handler.postDelayed(setDriverLocation, 4000);
        }
    };


    private void driverLocationUpdate() {
        ManagerData.stringTaskManager(GET, UPDATE_DRIVER_LOCATION_URL, driverLocationUpdateBodyJSON(), getHeaderJSON(), new ParserListenerString() {
            @Override
            public void onLoadCompleted(String response) {
                if (response != null) {
                    Log.i("LocationResponse ", response);
                    // Toast.makeText(DriverTracking.this, "1111" + response, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("common ", UNABLE_FOUND_DRIVER);
                }
            }

            @Override
            public void onLoadFailed(String response) {
                Log.i("common ", UNABLE_FOUND_DRIVER);
            }
        });
    }

    double previousLat, previousLng;
    double currentLat = 0.0;
    double currentLng = 0.0;

    private JSONObject driverLocationUpdateBodyJSON() {
        JSONObject postData = new JSONObject();
        try {
            postData.put(DRIVER_ID, driverId);
            postData.put(LAT, mLastKnownLocation.getLatitude());
            postData.put(LNG, mLastKnownLocation.getLongitude());
            currentLat = mLastKnownLocation.getLatitude();
            currentLng = mLastKnownLocation.getLongitude();
            previousLat = currentLat;
            previousLng = currentLng;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postData;
    }

    public static Double distance(double latitudeA, double latitudeB, double longitudeA, double longitudeB) {
        double radius = 3958.75;
        double dlat = ToRadians(Double.parseDouble(String.valueOf(latitudeB))
                - Double.parseDouble(String.valueOf(latitudeA)));
        double dlon = ToRadians(Double.parseDouble(String.valueOf(longitudeB))
                - Double.parseDouble(String.valueOf(longitudeA)));
        double a = Math.sin(dlat / 2)
                * Math.sin(dlat / 2)
                + Math.cos(ToRadians(Double.parseDouble(String.valueOf(latitudeA))))
                * Math.cos(ToRadians(Double.parseDouble(String.valueOf(latitudeB)))) * Math.sin(dlon / 2)
                * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = radius * c;
        double meterConversion = 1609.00;
        return d * meterConversion;
    }

    private static double ToRadians(double degrees) {
        double radians = degrees * 3.1415926535897932385 / 180;
        return radians;
    }

    private JSONObject getHeaderJSON() {
        JSONObject postHeader = new JSONObject();
        try {
            postHeader.put(ACCESS_TOKENS, accessToken);
            postHeader.put(REMEMBER_TOKEN, rememberToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postHeader;
    }

    public static void startWaitingDialog(AppCompatActivity activity, ProgressDialog pDialog) {
        pDialog.setMessage(activity.getString(R.string.txt_progress));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void stopWaitingDialog(ProgressDialog pDialog) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public static void snackBarAlert(String message, View view, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, listener).setActionTextColor(Color.RED);
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public static String directionsApi(LatLng startPoint, LatLng endPoint, Context context) {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=driving&" +
                "transit_routing_preference=less_driving&" +
                "origin=" + startPoint.latitude + "," + startPoint.longitude + "&" +
                "destination=" + endPoint.latitude + "," + endPoint.longitude + "&" +
                "key=" + context.getResources().getString(R.string.google_direction_api_key);
    }

    public static List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static void callToNumber(final AppCompatActivity context, String number) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            showMessageOKCancel(context,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
                            }
                        }
                    });
            return;
        }
        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
    }

    private static void showMessageOKCancel(AppCompatActivity context, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.txt_allow_phone)
                .setPositiveButton(R.string.txt_allow, okListener)
                .setNegativeButton(R.string.txt_deny, null)
                .create()
                .show();
    }

    public static String getSelectedLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, "");
    }

    public static void setSelectedLanguage(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }
}