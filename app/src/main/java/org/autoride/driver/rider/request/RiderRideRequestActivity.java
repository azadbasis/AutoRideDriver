package org.autoride.driver.rider.request;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.listeners.updates.listeners.ParserListener;
import org.autoride.driver.model.DataMessage;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.model.FCMResponse;
import org.autoride.driver.model.FCMToken;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.data.ManagerData;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.notifications.helper.FCMService;
import org.autoride.driver.notifications.helper.GoogleAPI;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiderRideRequestActivity extends AppCompatActivity
        implements AppsConstants, DriverApiUrl {

    private String TAG = "RiderRideRequest";
    private TextView tvRequestedTimeCount, tvTime, tvRiderLocation;
    private GoogleAPI googleService;
    private FCMService fcmService;
    private String accessToken, rememberToken, driverId, riderToken, vehicleType, riderId, riderDestPlace, address;
    private String dPhoto, dvPhoto, dName, vBrand, dRating, dPhone, vDesc, arrivingTime, confirmFare, destKm, destMin, riderRating;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private double riderLat, riderLng, destLat, destLng;
    private RippleBackground rippleBackground;
    private ImageView foundDevice;
    private LinearLayout riderInfoContainer;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private ProgressDialog pDialog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_ride_request);

        pDialog = new ProgressDialog(RiderRideRequestActivity.this);
        handler = new Handler();

        setUiComponent();
    }

    private void setUiComponent() {

        tvRequestedTimeCount = (TextView) findViewById(R.id.tv_requested_time_count);
        tvTime = (TextView) findViewById(R.id.tv_duration);
        tvRiderLocation = (TextView) findViewById(R.id.tv_rider_location);
        TextView tvDriverVehicle = (TextView) findViewById(R.id.tv_driver_vehicle);
        Button btnRiderRequestAccept = (Button) findViewById(R.id.btn_rider_request_accept);
        Button btnRiderRequestCancel = (Button) findViewById(R.id.btn_rider_request_cancel);
        TextView tvRiderRating = (TextView) findViewById(R.id.tv_rider_rating);
        tvRiderRating.setText("4.5");

        rippleBackground = (RippleBackground) findViewById(R.id.request_container);
        foundDevice = (ImageView) findViewById(R.id.found_device);
        riderInfoContainer = (LinearLayout) findViewById(R.id.rider_info_container);

        long startTime = 15 * 1000;
        long interval = 1 * 1000;
        countDownTimer = new MyCountDownTimer(startTime, interval);
        tvRequestedTimeCount.setText(String.format("00:00:%s%s", tvRequestedTimeCount.getText(), String.valueOf(startTime / 1000)));

        final ImageView button = (ImageView) findViewById(R.id.center_image);
        button.performClick();
        button.setPressed(true);
        button.invalidate();
        rippleBackground.startRippleAnimation();
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setPressed(false);
                button.invalidate();
                foundDevice();
                countDownTimer();
            }
        }, 3000);

        fcmService = Common.getFCMService();
        googleService = Common.getGoogleAPI();

        mediaPlayer = MediaPlayer.create(this, R.raw.notification_tone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15000);

        if (getIntent() != null) {
            String notificationStatus = getIntent().getStringExtra("notification_status");
            if (notificationStatus != null) {
                if (notificationStatus.equalsIgnoreCase("rider_ride_request")) {

                    String nTitle = getIntent().getStringExtra("title");
                    riderToken = getIntent().getStringExtra("rider_fcm_token");
                    vehicleType = getIntent().getStringExtra("vehicle_type");
                    tvDriverVehicle.setText(String.format("AUTO RIDE %s", vehicleType.toUpperCase()));
                    riderId = getIntent().getStringExtra("rider_id");
                    riderDestPlace = getIntent().getStringExtra("rider_destination_place");
                    confirmFare = getIntent().getStringExtra("confirm_fare");
                    destKm = getIntent().getStringExtra("dest_km");
                    destMin = getIntent().getStringExtra("dest_min");
                    riderLat = getIntent().getDoubleExtra("pickup_lat", -1.0);
                    riderLng = getIntent().getDoubleExtra("pickup_lng", -1.0);
                    destLat = getIntent().getDoubleExtra("dest_lat", -1.0);
                    destLng = getIntent().getDoubleExtra("dest_lng", -1.0);
                    riderRating = getIntent().getStringExtra("rider_rating");

                    getDirection(riderLat, riderLng);
                }
            }
        }

        btnRiderRequestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestAccept();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestAccept();
            }
        });

        foundDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestAccept();
            }
        });

        btnRiderRequestCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(riderToken)) {
                    requestCanceledDialog();
                } else {
                    ifProblemInRequest();
                }
            }
        });

        SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForToken: " + sp.getAll());
            accessToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    private void getRequestAccept() {
        if (!TextUtils.isEmpty(riderToken)) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                riderRideRequestAccept();
            } else {
                sendNotificationToRider("driver_request_problem", "Driver request problem occurred");
                msgNoInternet();
                finish();
            }
        } else {
            ifProblemInRequest();
        }
    }

    private void countDownTimer() {
        if (!timerHasStarted) {
            countDownTimer.start();
            timerHasStarted = true;
        } else {
            countDownTimer.cancel();
            timerHasStarted = false;
        }
    }

    private void foundDevice() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        riderInfoContainer.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    @SuppressLint("SetTextI18n")
    public class MyCountDownTimer extends CountDownTimer {

        MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if (!isFinishing()) {
                riderRequestNotReceived();
                tvRequestedTimeCount.setText(getString(R.string.txt_req1));
                rippleBackground.stopRippleAnimation();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvRequestedTimeCount.setText("00:00:" + millisUntilFinished / 1000);
        }
    }

    private void riderRideRequestAccept() {
        Common.startWaitingDialog(this, pDialog);
        performRequestAccept();
    }

    private void performRequestAccept() {
        ManagerData.taskManager(GET, ACCEPT_REQUEST_URL, getRequestAcceptBodyJSON(), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(DriverInfo driverInfo) {
                if (driverInfo != null) {
                    if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                        Common common = new Common(handler, accessToken, rememberToken, driverId);
                        common.startSetDriverLocationTask();

                        driverId = driverInfo.getDriverId();
                        dPhoto = driverInfo.getDriverAsUser().getProfilePhoto();
                        dvPhoto = driverInfo.getDriverAsUser().getVehiclePhoto();
                        dName = driverInfo.getDriverAsUser().getFirstName();
                        vBrand = driverInfo.getDriverAsUser().getVehicleBrand() + " " + driverInfo.getDriverAsUser().getDriverVehicleModel();
                        dRating = driverInfo.getDriverAsUser().getDriverRating();
                        dPhone = driverInfo.getDriverAsUser().getDriverPhone();
                        vDesc = driverInfo.getDriverAsUser().getVehicleNo();

                        sendNotificationToRider("driver_request_accept", "The driver has accepted your request");

                        Intent intent = new Intent(RiderRideRequestActivity.this, DriverTracking.class);
                        intent.putExtra("intent_status", "driver_request_accept");
                        intent.putExtra("rider_fcm_token", riderToken);
                        intent.putExtra("vehicle_type", vehicleType);
                        intent.putExtra("rider_id", riderId);
                        intent.putExtra("pickup_lat", riderLat);
                        intent.putExtra("pickup_lng", riderLng);
                        intent.putExtra("dest_lat", destLat);
                        intent.putExtra("dest_lng", destLng);
                        intent.putExtra("location", address);
                        intent.putExtra("nav_type", "riderLocation");
                        intent.putExtra("confirm_fare", confirmFare);
                        intent.putExtra("rider_phone", driverInfo.getRiderInfo().getPhone());
                        intent.putExtra("rider_name", driverInfo.getRiderInfo().getFirstName());
                        intent.putExtra("photo_url", driverInfo.getRiderInfo().getProfilePhoto());
                        intent.putExtra("rider_destination_place", riderDestPlace);
                        intent.putExtra("driver_photo", dPhoto);
                        intent.putExtra("vehicle_photo", dvPhoto);
                        intent.putExtra("driver_name", dName);
                        intent.putExtra("v_brand", vBrand);
                        intent.putExtra("driver_rating", dRating);
                        intent.putExtra("driver_phone", dPhone);
                        intent.putExtra("v_desc", vDesc);
                        intent.putExtra("dest_km", destKm);
                        intent.putExtra("dest_min", destMin);
                        intent.putExtra("rider_rating", riderRating);
                        Common.stopWaitingDialog(pDialog);

                        startActivity(intent);
                        finish();
                    }
                } else {
                    Common.stopWaitingDialog(pDialog);
                    ifProblemInRequest();
                }
            }

            @Override
            public void onLoadFailed(DriverInfo driverInfo) {
                Common.stopWaitingDialog(pDialog);
                ifProblemInRequest();
            }
        });
    }

    private JSONObject getRequestAcceptBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(RIDER_ID, riderId);
            postBody.put(DRIVER_IDS, driverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private void ifProblemInRequest() {
        sendNotificationToRider("driver_request_problem", "Driver request problem occurred ");
        toastSlowInternet();
        finish();
    }

    private void riderRequestNotReceived() {
        Common.startWaitingDialog(this, pDialog);
        performRequestNotReceived();
    }

    private void performRequestNotReceived() {
        sendNotificationToRider("driver_request_cancel", "The driver has canceled your request");
        ManagerData.taskManager(GET, REQUEST_NOT_RECEIVED_URL, getNotReceivedBodyJSON(), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(DriverInfo driverInfo) {
                if (driverInfo != null) {
                    Common.stopWaitingDialog(pDialog);
                    Intent intent = new Intent(RiderRideRequestActivity.this, DriverMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                } else {
                    Common.stopWaitingDialog(pDialog);
                    finishRequestActivity();
                }
            }

            @Override
            public void onLoadFailed(DriverInfo driverInfo) {
                Common.stopWaitingDialog(pDialog);
                finishRequestActivity();
            }
        });
    }

    private JSONObject getNotReceivedBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_IDS, driverId);
            postBody.put(RIDER_ID, riderId);
            if (Common.mLastKnownLocation != null) {
                postBody.put(LAT, Common.mLastKnownLocation.getLatitude());
                postBody.put(LNG, Common.mLastKnownLocation.getLongitude());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private void finishRequestActivity() {
        Intent intent = new Intent(RiderRideRequestActivity.this, DriverMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        RiderRideRequestActivity.this.finish();
    }

    private void sendNotificationToRider(String title, String msg) {

        if (Common.mLastKnownLocation != null) {

            String riderPickupLocation = new Gson().toJson(new LatLng(riderLat, riderLng));
            String riderDestLocation = new Gson().toJson(new LatLng(destLat, destLng));
            String driverLocation = new Gson().toJson(new LatLng(Common.mLastKnownLocation.getLatitude(), Common.mLastKnownLocation.getLongitude()));

            FCMToken fcmFCMToken = new FCMToken(riderToken);
            Map<String, String> content = new HashMap<>();
            content.put("title", title);
            content.put("message", msg);
            content.put("rider_fcm_token", riderToken);
            content.put("vehicle_type", vehicleType);
            content.put("rider_id", riderId);
            content.put("rider_destination_place", riderDestPlace);
            content.put("pickup_location", riderPickupLocation);
            content.put("dest_location", riderDestLocation);
            content.put("driver_id", driverId);
            content.put("driver_photo", dPhoto);
            content.put("vehicle_photo", dvPhoto);
            content.put("driver_name", dName);
            content.put("v_brand", vBrand);
            content.put("driver_rating", dRating);
            content.put("driver_phone", dPhone);
            content.put("v_desc", vDesc);
            content.put("arriving_time", arrivingTime);
            content.put("driver_location", driverLocation);
            content.put("confirm_fare", confirmFare);
            content.put("dest_km", destKm);
            content.put("dest_min", destMin);
            content.put("rider_rating", riderRating);

            DataMessage dataMessage = new DataMessage(fcmFCMToken.getToken(), content);
            fcmService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                    Log.i(TAG, "success " + response.body().getResults().get(0).getMessage_id());
                    if (response.body().getSuccess() == 1) {
                        Log.i(TAG, "InDriver success " + response.message());
                        //   Toast.makeText(RiderRideRequestActivity.this, "Cancelled Success", Toast.LENGTH_SHORT).show();
                        // finish();
                    } else {
                        Log.i(TAG, "InDriver Failed " + response.message());
                        // Toast.makeText(RiderRideRequestActivity.this, "Cancelled Failed", Toast.LENGTH_SHORT).show();
                        // finish();
                    }
                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {
                    Log.i(TAG, "InDriver Error " + t.getMessage());
                    // Toast.makeText(RiderRideRequestActivity.this, "Cancelled Error", Toast.LENGTH_SHORT).show();
                    // finish();
                }
            });
        }
    }

    private void getDirection(double rLat, double rLng) {

        String requestApi;
        try {

            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + Common.mLastKnownLocation.getLatitude() + "," + Common.mLastKnownLocation.getLongitude() + "&" +
                    "destination=" + rLat + "," + rLng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi request " + requestApi);

            googleService.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i(TAG, "Direction Success " + call.toString());
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject timeObj = legsObject.getJSONObject("duration");
                        String timeText = timeObj.getString("text");
                        int getTime = (int) Double.parseDouble(timeText.replaceAll("[^0-9\\\\.]+", ""));
                        String timeUnit;
                        if (getTime < 2) {
                            timeUnit = "MINUTE";
                        } else {
                            timeUnit = "MINUTES";
                        }
                        arrivingTime = getTime + " " + timeUnit;
                        tvTime.setText(arrivingTime);

                        address = legsObject.getString("end_address");
                        tvRiderLocation.setText(address);

                    } catch (Exception e) {
                        Log.i(TAG, "Direction Error " + e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "Direction Error " + t.getMessage());
                    // Toast.makeText(RiderRideRequestActivity.this, "DirectionError " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "Direction Error " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
        mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        requestCanceledDialog();
    }

    private void requestCanceledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderRideRequestActivity.this);
        builder.setTitle(getString(R.string.txt_req2));
        builder.setMessage(getString(R.string.txt_req3));
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.txt_req4, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                riderRideRequestCanceled();
            }
        });
        builder.setNegativeButton(R.string.txt_req5, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void riderRideRequestCanceled() {
        Common.startWaitingDialog(this, pDialog);
        performRequestCanceled();
    }

    private void performRequestCanceled() {
        sendNotificationToRider("driver_request_cancel", "The driver has canceled your request");
        ManagerData.taskManager(GET, CANCEL_REQUEST_URL, getRequestCanceledBodyJSON(), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(DriverInfo driverInfo) {
                if (driverInfo != null) {
                    Common.stopWaitingDialog(pDialog);
                    Intent intent = new Intent(RiderRideRequestActivity.this, DriverMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                } else {
                    Common.stopWaitingDialog(pDialog);
                    finishRequestActivity();
                }
            }

            @Override
            public void onLoadFailed(DriverInfo driverInfo) {
                Common.stopWaitingDialog(pDialog);
                finishRequestActivity();
            }
        });
    }

    private JSONObject getRequestCanceledBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(RIDER_ID, driverId);
            if (Common.mLastKnownLocation != null) {
                postBody.put(LAT, Common.mLastKnownLocation.getLatitude());
                postBody.put(LNG, Common.mLastKnownLocation.getLongitude());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
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

    private void msgNoInternet() {
        Toast.makeText(RiderRideRequestActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
    }

    private void toastSlowInternet() {
        Toast.makeText(RiderRideRequestActivity.this, R.string.slow_internet_connection, Toast.LENGTH_SHORT).show();
    }
}