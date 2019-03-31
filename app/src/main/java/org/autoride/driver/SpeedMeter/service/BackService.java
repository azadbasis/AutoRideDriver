package org.autoride.driver.SpeedMeter.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.autoride.driver.SpeedMeter.GPSData;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.utils.AppConstants;
import org.autoride.driver.utils.Operation;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.autoride.driver.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.driver.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.driver.constants.AppsConstants.DRIVER_ID;
import static org.autoride.driver.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.driver.constants.AppsConstants.SESSION_SHARED_PREFERENCES;
import static org.autoride.driver.networks.DriverApiUrl.UPDATE_RIDING_DISTANCE_URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackService extends IntentService {
    RequestQueue requestQueue;
    ConnectivityManager connectivity;
    private static final String TAG = BackService.class.getSimpleName();
    public static final String ACTION_SUCCESS_BROADCAST = BackService.class.getName() + "SuccessBroadcast";
    public static final String EXTRA_SUCCESS = "extra_success";
    public static final String EXTRA_MESSAGE = "extra_message";


    public BackService() {
        super("BackService");
        // requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        doWork();
    }

    void doWork() {

        if (AutoRideDriverApps.LOCATION != null) {
            double lat = AutoRideDriverApps.LOCATION.getLatitude();
            double lon = AutoRideDriverApps.LOCATION.getLongitude();
            double distance = AutoRideDriverApps.DRIVER_DISTANCE;
            String co = "Lat:" + lat + " Lon:" + lon;

            String address = "";

            Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));
            try {
                // get address from location
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() != 0) {
                    StringBuilder builder = new StringBuilder();
                    Address returnAddress = addresses.get(0);
                    for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                        builder.append(returnAddress.getAddressLine(i));
                        builder.append(", ");
                    }
                    address = builder.toString();

                    // Toast.makeText(getApplicationContext(), messageLocation, Toast.LENGTH_LONG).show();
                } else {
                }
            } catch (IOException e) {
            }

            //   Operations.SaveToSharedPreference(this, "Address", address);
            //  Operations.SaveToSharedPreference(this, "Coordinate", co);


            if (Operation.IsOnline(getApplicationContext())) {

                setRiderGPS(lat, lon);

                List<GPSData> list = Operation.GetLocallySavedLocations(getApplicationContext());
                if (list.size() > 0) {
                   /* String joined = TextUtils.join(", ", list);
                    setRiderGPS(lat, lon,joined);*/
                    String jsonCoordinate = null;
                    List<String> mylist = new ArrayList<String>();
                    for (GPSData location : list) {
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        String dateTime = location.getDateTime();
                        //   GPSData gpsData = new GPSData(latitude, longitude);
                        GPSData gpsData = new GPSData(latitude, longitude, dateTime);
                        Gson gson = new Gson();
                        jsonCoordinate = gson.toJson(gpsData);
                        mylist.add(jsonCoordinate);
                    }
                    String str = Arrays.toString(mylist.toArray());
                    String joined = TextUtils.join(", ", mylist);
                    //  setRiderGPS(lat, lon, joined);
                    // setRiderGPS(lat, lon, str);
                    setRiderGPSHistory(str);
                    Operation.ClearLocallySavedLocations(getApplicationContext());
                }

            } else {
                GPSData gpsData = new GPSData();
                gpsData.setLatitude(lat);
                gpsData.setLongitude(lon);
                //   DateFormat datum = new SimpleDateFormat("MMM dd yyyy, h:mm:ss");
                DateFormat datum = new SimpleDateFormat("dd-MM-yyyy h:mm:ss");
                String date = datum.format(Calendar.getInstance().getTime());
                gpsData.setDateTime(date);
                Operation.saveGPSDataList(getApplicationContext(), gpsData);
            }
        }
    }

    private void setRiderGPS(Double latitude, Double longitude) {
        SharedPreferences driverInf = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        String driverId = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");

        //"http://128.199.80.10/golden/app/user/set/user/tracking/location?userId=5adc77eac31240181c1a919c&lat=23.7897857&lng=90.4254355"
        String url = UPDATE_RIDING_DISTANCE_URL + "driverId=" + driverId + "&kilometer=" + ".50m" + "&lat=" + latitude + "&lng=" + longitude;
        //  String url = SET_LOCATION_URL;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if (statusCode.equalsIgnoreCase("200")) {
                                String status = jsonObject.getString("status");
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), status + "\n" + success + "\n" + message, Toast.LENGTH_SHORT).show();
                                sendMessageToUI(success, message);
                            } else {
                                Toast.makeText(BackService.this, "Slow Internet Connection!", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences driverInf = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);

                String accessToken = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
                String rememberToken = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");


                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                params.put("rememberToken", rememberToken);

                return params;
            }
        };
        AutoRideDriverApps.getInstance().addToRequestQueue(postRequest);
        /*NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
        //    requestQueue.add(postRequest);

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }*/

    }


    private void sendMessageToUI(String success, String message) {

        Log.d(TAG, "Sending info...");
        Intent intent = new Intent(ACTION_SUCCESS_BROADCAST);
        intent.putExtra(EXTRA_SUCCESS, success);
        intent.putExtra(EXTRA_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    public void setRiderGPSHistory(final String previousCord) {
        SharedPreferences sp = BackService.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final String driverId = sp.getString(DRIVER_ID, DOUBLE_QUOTES);
        String url = "";// SET_LOCATION_HISTORY_URL;
        //  String url = SET_LOCATION_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if (statusCode.equalsIgnoreCase("200")) {
                                String status = jsonObject.getString("status");
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), status + "\n" + success + "\n" + message, Toast.LENGTH_SHORT).show();
                                sendMessageToUI(success, message);
                            } else {
                                Toast.makeText(BackService.this, "Slow Internet Connection!", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences sp = BackService.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
                String rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);

                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                params.put("rememberToken", rememberToken);

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", driverId);
                params.put("previousCord", previousCord);

                return super.getParams();
            }
        };


        AutoRideDriverApps.getInstance().addToRequestQueue(postRequest);
    }

}
