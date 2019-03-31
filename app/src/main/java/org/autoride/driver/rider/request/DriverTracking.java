package org.autoride.driver.rider.request;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.ui.IconGenerator;
import com.skyfishjy.library.RippleBackground;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.SpeedMeter.Data;
import org.autoride.driver.SpeedMeter.service.GpsServices;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.driver.net.parsers.DirectionsJSONParser;
import org.autoride.driver.listeners.updates.listeners.ParserListener;
import org.autoride.driver.listeners.updates.listeners.ParserListenerString;
import org.autoride.driver.model.DataMessage;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.model.FCMResponse;
import org.autoride.driver.model.FCMToken;
import org.autoride.driver.model.FareInfo;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.networks.managers.data.ManagerData;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.notifications.helper.FCMService;
import org.autoride.driver.notifications.helper.GoogleAPI;
import org.autoride.driver.rider.request.service.DistanceService;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverTracking extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        NavigationView.OnNavigationItemSelectedListener,
        AppsConstants, DriverApiUrl, android.location.LocationListener, GpsStatus.Listener {

    private String TAG = "DriverTracking";
    private GoogleMap mMap;
    private double riderLat, riderLng, destLat, destLng;
    private String accessToken, rememberToken, driverId, riderId, riderToken, confirmFare,
            navigationType, vehicleType, riderPhone, riderName, photoUrl,
            riderDestPlace, dPhoto, dvPhoto, dName, vBrand, dRating, dPhone, vDesc, destKm, destMin;
    private int predictedMinute;
    private double predictedKilometer;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private static final float MAP_ZOOM = 14.0f;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Marker squareMarker;
    private Polyline polyDirection;
    private GoogleAPI gAPIService;
    private ActionBarDrawerToggle riderToggle;
    private DrawerLayout riderDrawer;
    private NavigationView riderNavView;
    private TextView tvRiderLocation, tvRiderLocation2, tvRiderName, tvRiderName2, tvRiderName3, tvRiderName4, tvRiderName5,
            tvRiderName6, tvRiderName7, tvRiderName8, tvRiderPhone, tvDriverVehicle2, tvRiderRatings, tvTotalFareCollect,
            tvTripPayFare, tvTripTotalFare, tvTripBaseFare, tvTripMinimumFare, tvTripTimeRate, tvTripDistanceRate, tvTripUsableDiscount,
            tvTripDistance, tvTripTime, tvCancelCompleteTrip, tvStartTrip, tvPickUp;
    private ImageView ivFareQRCode, ivDriverVehicleType, ivRiderPhoto, ivRiderPhoto2, ivRiderPhoto3, ivRiderPhoto4;
    private FrameLayout flRiderInfo, flRiderTrip;
    private LinearLayout llConfirmArrived, llStartTrip, llDropOff, llCalculateFare;
    private Button btnCompleteTrip;
    private FCMService fcmService;
    private ViewFlipper vfRiderInfo;
    private Animation slideLeftOut, slideRightIn;
    private ProgressDialog pDialog;
    private Location pickupLocation;
    private View.OnClickListener snackBarDismissListener;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private static boolean pickupRunning = false;
    private float rideDistance;
    private double oldLatitude, oldLongitude, newLatitude, newLongitude;
    private float totalDistance = 0.0f;
    private int completeMin, distanceCalculateStatus;
    private Handler handler;
    private String completeFare, completeKM;
    private OkHttpClient client;
    com.google.android.gms.location.LocationListener myLocationListener;

    //SPEEDMETER ACTIVIT
    private SharedPreferences sharedPreferences;
    private LocationManager mLocationManager;
    private static Data data;

    private Toolbar speedToolbar;
    private com.melnykov.fab.FloatingActionButton fab;
    private com.melnykov.fab.FloatingActionButton refresh;
    private ProgressBarCircularIndeterminate progressBarCircularIndeterminate;
    private TextView satellite;
    private TextView status;
    private TextView accuracy;
    private TextView currentSpeed;
    private TextView maxSpeed;
    private TextView averageSpeed;
    private TextView distance;
    private Chronometer time, chronometer;
    private Data.onGpsServiceUpdate onGpsServiceUpdate;
    private boolean firstfix;
    private boolean running;
    private long pauseOffset;
    android.support.design.widget.FloatingActionButton myLocationButton;
    View mapView;
    ru.dimorinny.floatingtextbutton.FloatingTextButton btnBackTrip, btnCompleteTrips;
    RippleBackground contentDropOffRippleBackground;
    private SlidingUpPanelLayout mLayout;
    TextView toolbarTitle, toolbarAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_tracking_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tracking_driver);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarAddress = (TextView) findViewById(R.id.toolbarAddress);
        myLocationButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.myLocationButton);
        if (myLocationButton.getVisibility() == View.VISIBLE) {
            myLocationButton.setVisibility(View.GONE);
        }
        btnCompleteTrips = (ru.dimorinny.floatingtextbutton.FloatingTextButton) findViewById(R.id.btnCompleteTrip);
        btnBackTrip = (ru.dimorinny.floatingtextbutton.FloatingTextButton) findViewById(R.id.btnBackTrip);

        btnCompleteTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideDriverApps.isNetworkAvailable()) {
                    completeTripDialog();
                } else {
                    snackBarNoInternet();
                }
            }
        });
        btnBackTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DriverTracking.this,DriverMainActivity.class));

            }
        });
        setUiComponent();
        setSpeedMeterUI();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    Common.mLastKnownLocation = location;
                }
            }
        };
    }

    private void setUiComponent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setTitle(R.string.title_activity_driver_tracking);
        }
        client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        handler = new Handler();
        gAPIService = Common.getGoogleAPI();
        pDialog = new ProgressDialog(DriverTracking.this);
        fcmService = Common.getFCMService();

        riderDrawer = (DrawerLayout) findViewById(R.id.rider_drawer_layout);
        riderToggle = new ActionBarDrawerToggle(this, riderDrawer, R.string.open, R.string.close);
        riderDrawer.addDrawerListener(riderToggle);
        riderToggle.syncState();

        vfRiderInfo = (ViewFlipper) findViewById(R.id.vf_rider_info);
        riderNavView = (NavigationView) findViewById(R.id.nv_rider_request_info);
        riderNavView.setNavigationItemSelectedListener(this);

        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);

        ivFareQRCode = (ImageView) findViewById(R.id.iv_fare_qr_code);
        ivDriverVehicleType = (ImageView) findViewById(R.id.iv_driver_vehicle_type);
        ivRiderPhoto = (ImageView) findViewById(R.id.iv_requested_rider_photo);
        ivRiderPhoto2 = (ImageView) findViewById(R.id.iv_requested_rider_photo2);
        ivRiderPhoto3 = (ImageView) findViewById(R.id.iv_requested_rider_photo3);
        ivRiderPhoto4 = (ImageView) findViewById(R.id.iv_requested_rider_photo4);

        tvCancelCompleteTrip = (TextView) findViewById(R.id.tv_cancel_complete_trip);
        tvStartTrip = (TextView) findViewById(R.id.tv_start_trip);
        tvPickUp = (TextView) findViewById(R.id.tv_pick_up);

        tvRiderName = (TextView) findViewById(R.id.tv_requested_rider_name);
        tvRiderName2 = (TextView) findViewById(R.id.tv_requested_rider_name2);
        tvRiderName3 = (TextView) findViewById(R.id.tv_requested_rider_name3);
        tvRiderName4 = (TextView) findViewById(R.id.tv_requested_rider_name4);
        tvRiderName5 = (TextView) findViewById(R.id.tv_requested_rider_name5);
        tvRiderName6 = (TextView) findViewById(R.id.tv_requested_rider_name6);
        tvRiderName7 = (TextView) findViewById(R.id.tv_requested_rider_name7);
        tvRiderName8 = (TextView) findViewById(R.id.tv_requested_rider_name8);

        tvTripPayFare = (TextView) findViewById(R.id.tv_trip_rider_total_fare);
        tvTripTotalFare = (TextView) findViewById(R.id.tv_trip_total_fare);
        tvTripBaseFare = (TextView) findViewById(R.id.tv_trip_base_fare);
        tvTripMinimumFare = (TextView) findViewById(R.id.tv_trip_minimum_fare);
        tvTripTimeRate = (TextView) findViewById(R.id.tv_trip_time_rate);
        tvTripDistanceRate = (TextView) findViewById(R.id.tv_trip_distance_rate);
        tvTripUsableDiscount = (TextView) findViewById(R.id.tv_trip_usable_discount);
        tvTripDistance = (TextView) findViewById(R.id.tv_trip_distance);
        tvTripTime = (TextView) findViewById(R.id.tv_trip_time);

        tvRiderLocation = (TextView) findViewById(R.id.tv_requested_rider_location);
        tvRiderLocation2 = (TextView) findViewById(R.id.tv_requested_rider_location2);
        tvDriverVehicle2 = (TextView) findViewById(R.id.tv_driver_vehicle2);
        tvTotalFareCollect = (TextView) findViewById(R.id.tv_total_fare_collect);

        flRiderInfo = (FrameLayout) findViewById(R.id.frame_layout_rider_info);
        flRiderTrip = (FrameLayout) findViewById(R.id.frame_layout_rider_trip);
        llConfirmArrived = (LinearLayout) findViewById(R.id.ll_confirm_arrived);
        llStartTrip = (LinearLayout) findViewById(R.id.ll_start_trip);
        llDropOff = (LinearLayout) findViewById(R.id.ll_drop_off);
        llCalculateFare = (LinearLayout) findViewById(R.id.ll_calculate_fare);

        tvRiderPhone = (TextView) findViewById(R.id.tv_rider_phone);
        tvRiderRatings = (TextView) findViewById(R.id.tv_rider_ratings);

        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.setVisibility(View.GONE);
            }
        };

        btnCompleteTrip = (Button) findViewById(R.id.btn_complete_trip);
        btnCompleteTrip.setEnabled(false);
        btnCompleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideDriverApps.isNetworkAvailable()) {
                    completeTripDialog();
                } else {
                    snackBarNoInternet();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps_driver_tracking);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForToken: " + sp.getAll());
            accessToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");
        }
        setUpLocation();

        if (getIntent() != null) {
            String iStatus = getIntent().getStringExtra("intent_status");
            if (iStatus.equalsIgnoreCase("driver_request_accept")) {

                riderToken = getIntent().getStringExtra("rider_fcm_token");
                vehicleType = getIntent().getStringExtra("vehicle_type");
                tvDriverVehicle2.setText(String.format("AUTO RIDE %s", vehicleType.toUpperCase()));
                riderId = getIntent().getStringExtra("rider_id");

                riderLat = getIntent().getDoubleExtra("pickup_lat", -1.0);
                riderLng = getIntent().getDoubleExtra("pickup_lng", -1.0);
                destLat = getIntent().getDoubleExtra("dest_lat", -1.0);
                destLng = getIntent().getDoubleExtra("dest_lng", -1.0);

                tvRiderLocation.setText(getIntent().getStringExtra("location"));
                toolbarAddress.setText(getIntent().getStringExtra("location"));

                tvRiderLocation2.setText(getIntent().getStringExtra("location"));
                navigationType = getIntent().getStringExtra("nav_type");
                confirmFare = getIntent().getStringExtra("confirm_fare");

                riderPhone = getIntent().getStringExtra("rider_phone");
                riderName = getIntent().getStringExtra("rider_name");
                photoUrl = getIntent().getStringExtra("photo_url");

                riderDestPlace = getIntent().getStringExtra("rider_destination_place");
                dPhoto = getIntent().getStringExtra("driver_photo");
                dvPhoto = getIntent().getStringExtra("vehicle_photo");
                dName = getIntent().getStringExtra("driver_name");
                vBrand = getIntent().getStringExtra("v_brand");
                dRating = getIntent().getStringExtra("driver_rating");
                dPhone = getIntent().getStringExtra("driver_phone");
                vDesc = getIntent().getStringExtra("v_desc");

                destKm = getIntent().getStringExtra("dest_km");
                destMin = getIntent().getStringExtra("dest_min");

                // rider rating need
                tvRiderRatings.setText(getIntent().getStringExtra("rider_rating"));

                tvRiderPhone.setText(riderPhone);
                tvRiderName.setText(riderName);
                tvRiderName2.setText(riderName);
                tvRiderName3.setText(riderName + " " + "Notified"+","+"Cash Payment Trip");
                tvRiderName4.setText(riderName);
                tvRiderName5.setText(riderName);
                tvRiderName6.setText(riderName);
                tvRiderName7.setText(riderName);
                tvRiderName8.setText(riderName);

                getDirection();

                if (vehicleType.equalsIgnoreCase("car")) {
                    Glide.with(getApplicationContext().getApplicationContext())
                            .load(R.drawable.car)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.car)
                                    .fallback(R.drawable.car))
                            .into(ivDriverVehicleType);
                } else if (vehicleType.equalsIgnoreCase("bike")) {
                    Glide.with(getApplicationContext().getApplicationContext())
                            .load(R.drawable.icon_bike)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.icon_bike)
                                    .fallback(R.drawable.icon_bike))
                            .into(ivDriverVehicleType);
                }

              /*  Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto);*/
                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions().optionalCircleCrop()

                        )
                        .into(new SimpleTarget<Drawable>() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 150, 110, false);

                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), bitmapResized);
                                roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                                roundedBitmapDrawable.setCircular(true);
                                ivRiderPhoto.setImageDrawable(roundedBitmapDrawable);

                            }
                        });

/*                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto2);*/

                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto3);

                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto4);
                if (mLayout != null) {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            } else if (iStatus.equalsIgnoreCase("driver_riding_mode")) {

//                distanceCalculateStatus2 = 1;
                distanceCalculateStatus = 1;
//                pickupRunning2 = true;
                pickupRunning = true;

                double driverCurLat = getIntent().getDoubleExtra("driver_cur_lat", -1.0);
                double driverCurLng = getIntent().getDoubleExtra("driver_cur_lng", -1.0);

                double driverEndLat = getIntent().getDoubleExtra("driver_lat", -1.0);
                double driverEndLng = getIntent().getDoubleExtra("driver_lng", -1.0);

                totalDistance = meterDistanceBetweenPoints(driverEndLat, driverEndLng, driverCurLat, driverCurLng);
                // totalDistance2 = meterDistanceBetweenPoints2(driverEndLat, driverEndLng, driverCurLat, driverCurLng);

                //oldLatitude2 = driverCurLat;
                // oldLongitude2 = driverCurLng;
                //localCalculation();

                oldLatitude = driverCurLat;
                oldLongitude = driverCurLng;
                //calculateDistance();
                navigationType = "destination";

                toolbarTitle.setText("Trip Running");

                getSupportActionBar().setTitle(R.string.txt_req6);

                riderToken = getIntent().getStringExtra("rider_fcm_token");
                vehicleType = getIntent().getStringExtra("vehicle_type");
                tvDriverVehicle2.setText(String.format("AUTO RIDE %s", vehicleType.toUpperCase()));
                riderId = getIntent().getStringExtra("rider_id");

                riderLat = getIntent().getDoubleExtra("pickup_lat", -1.0);
                riderLng = getIntent().getDoubleExtra("pickup_lng", -1.0);

                destLat = getIntent().getDoubleExtra("dest_lat", -1.0);
                destLng = getIntent().getDoubleExtra("dest_lng", -1.0);

                riderPhone = getIntent().getStringExtra("rider_phone");
                riderName = getIntent().getStringExtra("rider_name");
                photoUrl = getIntent().getStringExtra("photo_url");

                dPhoto = getIntent().getStringExtra("driver_photo");
                dName = getIntent().getStringExtra("driver_name");
                vBrand = getIntent().getStringExtra("v_brand");
                dRating = getIntent().getStringExtra("driver_rating");
                dPhone = getIntent().getStringExtra("driver_phone");
                vDesc = getIntent().getStringExtra("v_desc");

                tvRiderRatings.setText(getIntent().getStringExtra("rider_rating"));

                tvRiderPhone.setText(riderPhone);
                tvRiderName4.setText(riderName);
                tvRiderName5.setText(riderName);
                tvRiderName6.setText(riderName);
                tvRiderName7.setText(riderName);
                tvRiderName8.setText(riderName);

                flRiderInfo.setVisibility(View.GONE);
                flRiderTrip.setVisibility(View.VISIBLE);

                tvCancelCompleteTrip.setText(getString(R.string.txt_req31));
                tvStartTrip.setText(getString(R.string.txt_req31));
                tvPickUp.setText(getString(R.string.txt_req31));

                getDestDirection();

                if (vehicleType.equalsIgnoreCase("car")) {
                    Glide.with(getApplicationContext().getApplicationContext())
                            .load(R.drawable.car)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.car)
                                    .fallback(R.drawable.car))
                            .into(ivDriverVehicleType);
                } else if (vehicleType.equalsIgnoreCase("bike")) {
                    Glide.with(getApplicationContext().getApplicationContext())
                            .load(R.drawable.icon_bike)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.icon_bike)
                                    .fallback(R.drawable.icon_bike))
                            .into(ivDriverVehicleType);
                }

               /* Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto2);*/
                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions().optionalCircleCrop()

                        )
                        .into(new SimpleTarget<Drawable>() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 150, 110, false);

                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), bitmapResized);
                                roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                                roundedBitmapDrawable.setCircular(true);
                                ivRiderPhoto2.setImageDrawable(roundedBitmapDrawable);

                            }
                        });

                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto3);

                Glide.with(getApplicationContext().getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(ivRiderPhoto4);


                if (mLayout != null) {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }

                if (myLocationButton.getVisibility() == View.GONE) {
                    myLocationButton.setVisibility(View.VISIBLE);
                }

            }
        }
        contentDropOffRippleBackground = (RippleBackground) findViewById(R.id.contentDropOffRippleBackground);
        contentDropOffRippleBackground.startRippleAnimation();
    }

    private void setSpeedMeterUI() {
        data = new Data(onGpsServiceUpdate);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        speedToolbar = (Toolbar) findViewById(R.id.toolbar);
        speedToolbar.setTitle("");

        setSupportActionBar(speedToolbar);
        //setTitle("");
        fab = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        refresh = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.refresh);

        refresh.setVisibility(View.INVISIBLE);

        onGpsServiceUpdate = new Data.onGpsServiceUpdate() {
            @Override
            public void update() {
                double maxSpeedTemp = data.getMaxSpeed();
                double distanceTemp = data.getDistance();
                double averageTemp;
                if (sharedPreferences.getBoolean("auto_average", false)) {
                    averageTemp = data.getAverageSpeedMotion();
                } else {
                    averageTemp = data.getAverageSpeed();
                }

                String speedUnits;
                String distanceUnits;
                if (sharedPreferences.getBoolean("miles_per_hour", false)) {
                    maxSpeedTemp *= 0.62137119;
                    distanceTemp = distanceTemp / 1000.0 * 0.62137119;
                    averageTemp *= 0.62137119;
                    speedUnits = "mi/h";
                    distanceUnits = "mi";
                } else {
                    speedUnits = "km/h";
                    if (distanceTemp <= 1000.0) {
                        distanceUnits = "m";
                    } else {
                        distanceTemp /= 1000.0;
                        distanceUnits = "km";
                    }
                }

                SpannableString s = new SpannableString(String.format("%.0f", maxSpeedTemp) + speedUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);
                maxSpeed.setText(s);
                String tripMaxSpeed = maxSpeed.getText().toString();
                if (tripMaxSpeed != null) {
                    AutoRideDriverApps.MAX_SPEED = tripMaxSpeed;
                }

                s = new SpannableString(String.format("%.0f", averageTemp) + speedUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);
                averageSpeed.setText(s);
                String tripAverageSpeed = averageSpeed.getText().toString();
                if (tripAverageSpeed != null) {
                    AutoRideDriverApps.AVERAGE_SPEED = tripAverageSpeed;
                }

                s = new SpannableString(String.format("%.3f", distanceTemp) + distanceUnits);
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 2, s.length(), 0);
                distance.setText(s);
                String tripDistance = distance.getText().toString();
                if (tripDistance != null) {
                    AutoRideDriverApps.DISTANCE = tripDistance;
                    //Toast.makeText(DriverTracking.this, "Distance " + tripDistance, Toast.LENGTH_SHORT).show();
                }
            }
        };

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        satellite = (TextView) findViewById(R.id.satellite);
        status = (TextView) findViewById(R.id.status);
        accuracy = (TextView) findViewById(R.id.accuracy);
        maxSpeed = (TextView) findViewById(R.id.maxSpeed);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        distance = (TextView) findViewById(R.id.distance);
        time = (Chronometer) findViewById(R.id.time);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        progressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);

        time.setText("00:00:00");

        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chrono) {
                long time;
                if (data.isRunning()) {
                    time = SystemClock.elapsedRealtime() - chrono.getBase();
                    data.setTime(time);
                } else {
                    time = data.getTime();
                }

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                chrono.setText(hh + ":" + mm + ":" + ss);

                if (data.isRunning()) {
                    chrono.setText(hh + ":" + mm + ":" + ss);

                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });

        Timer t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {

                                      String cromometerTime = chronometer.getText().toString();

                                      if (cromometerTime != null) {
                                          final int waitingTime = getSecondsFromDurationString(cromometerTime);
                                          DriverTracking.this.runOnUiThread(new Runnable() {
                                              public void run() {
                                                  double farePerSecond = 300 / 60;
                                                  double waitingFareTotal = (farePerSecond * waitingTime) / 100;
                                                  TextView textWaitingFare = (TextView) findViewById(R.id.textWaitingFare);
                                                  textWaitingFare.setText("BDT " + (new DecimalFormat("##.##").format(waitingFareTotal)));
                                                  String waitingBill = textWaitingFare.getText().toString();
                                                  //   Toast.makeText(DriverTracking.this, "WAITING BILL" + waitingBill, Toast.LENGTH_SHORT).show();
                                                  if (waitingTime != 0) {
                                                      AutoRideDriverApps.WAITING_TIME = waitingTime;
                                                  }
                                                  if (waitingBill != null) {
                                                      AutoRideDriverApps.WAITING_BILL = waitingBill;
                                                  }
                                              }
                                          });
                                      }
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                10000);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }



    private void setUpLocation() {
        if (checkPlayService()) {
            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private boolean checkPlayService() {
        int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resCode)) {
                GooglePlayServicesUtil.getErrorDialog(resCode, this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 200);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Common.mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (Common.mLastKnownLocation != null) {
            final double lat = Common.mLastKnownLocation.getLatitude();
            final double lng = Common.mLastKnownLocation.getLongitude();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), MAP_ZOOM));
        } else {
            Log.i(TAG, "Errors " + "Cannot get your location");
        }
    }

    private void getDirection() {

        if (polyDirection != null) {
            polyDirection.remove();
        }

        try {
            String requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + Common.mLastKnownLocation.getLatitude() + "," + Common.mLastKnownLocation.getLongitude() + "&" +
                    "destination=" + riderLat + "," + riderLng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi location " + requestApi);

            gAPIService.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    new DirectionParserTask().execute(response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "direction errors " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "direction errors " + e.toString());
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, myLocationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSION_REQUEST_DODE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (checkPlayService()) {
//                        buildGoogleApiClient();
//                        createLocationRequest();
//                        displayLocation();
//                    }
//                }
//            }
//        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        riderDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (riderToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int satsUsed;

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                @SuppressLint("MissingPermission") GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                int satsInView = 0;
                satsUsed = 0;
                Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                for (GpsSatellite sat : sats) {
                    satsInView++;
                    if (sat.usedInFix()) {
                        satsUsed++;
                    }
                }
                satellite.setText(String.valueOf(satsUsed) + "/" + String.valueOf(satsInView));
                String satelliteCondition = satellite.getText().toString();
                if (satelliteCondition != null) {
                    AutoRideDriverApps.SATELLITE = satelliteCondition;
                }
                if (satsUsed == 0) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                    data.setRunning(false);
                    status.setText("");
                    stopService(new Intent(getBaseContext(), GpsServices.class));
                    fab.setVisibility(View.INVISIBLE);
                    refresh.setVisibility(View.INVISIBLE);
                    accuracy.setText("");
                    status.setText(getResources().getString(R.string.waiting_for_fix));
                    firstfix = true;
                    //   btnStartTrip.setEnabled(false);
                    // llStartTrip.setVisibility(View.GONE);


                }
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    public void showGpsDisabledDialog() {
        Dialog dialog = new Dialog(this, getResources().getString(R.string.gps_disabled), getResources().getString(R.string.please_enable_gps));

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            SpannableString s = new SpannableString(String.format("%.0f", location.getAccuracy()) + "m");
            s.setSpan(new RelativeSizeSpan(0.75f), s.length() - 1, s.length(), 0);
            accuracy.setText(s);
            String myAccuracy = accuracy.getText().toString();
            if (myAccuracy != null) {
                AutoRideDriverApps.ACCURACY = myAccuracy;
            }

            if (firstfix) {
                status.setText("");
                fab.setVisibility(View.VISIBLE);
//                 busyDialog.dismis();
                autoStartSpeedMeter();

                if (!data.isRunning() && !maxSpeed.getText().equals("")) {
                    refresh.setVisibility(View.VISIBLE);
                }
                firstfix = false;
            } else {
            }
        } else {
            firstfix = true;
        }

        if (location.hasSpeed()) {
            progressBarCircularIndeterminate.setVisibility(View.GONE);
            String speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + "km/h";
            if (sharedPreferences.getBoolean("miles_per_hour", false)) { // Convert to MPH
                speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6 * 0.62137119) + "mi/h";
            }
            SpannableString s = new SpannableString(speed);
            if (s.equals(new SpannableString("0km/h"))) {
                if (!running) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    running = true;
                }

            } else {
                if (running) {
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    running = false;

                }
            }
            s.setSpan(new RelativeSizeSpan(0.25f), s.length() - 4, s.length(), 0);
            currentSpeed.setText(s);
            String mySpeed = currentSpeed.getText().toString();
            if (mySpeed != null) {
                AutoRideDriverApps.SPEED = mySpeed;
            }

        }

    }

    public void onFabClick(View v) {
        autoStartSpeedMeter();
    }

    public void onRefreshClick(View v) {
        resetData();
        stopService(new Intent(getBaseContext(), GpsServices.class));
    }

    private void autoStartSpeedMeter() {
        if (!data.isRunning()) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            data.setRunning(true);
            time.setBase(SystemClock.elapsedRealtime() - data.getTime());
            time.start();

            String movingTime = time.getText().toString();
            if (movingTime != null) {
                int myMovingTime = getSecondsFromDurationString(movingTime);
                AutoRideDriverApps.TIME = myMovingTime;
            }

            data.setFirstTime(true);
            startService(new Intent(getBaseContext(), GpsServices.class));
            refresh.setVisibility(View.INVISIBLE);
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
            data.setRunning(false);
            status.setText("");
            stopService(new Intent(getBaseContext(), GpsServices.class));
            refresh.setVisibility(View.VISIBLE);
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

    public void resetData() {
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        refresh.setVisibility(View.INVISIBLE);
        time.stop();
        maxSpeed.setText("");
        averageSpeed.setText("");
        distance.setText("");
        time.setText("00:00:00");
        data = new Data(onGpsServiceUpdate);
    }

    private class DirectionParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.startWaitingDialog(DriverTracking.this, pDialog);
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            Common.stopWaitingDialog(pDialog);
            List<LatLng> points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get(LAT));
                    double lng = Double.parseDouble(point.get(LNG));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLACK);
                polylineOptions.geodesic(true);
            }

            IconGenerator iconGen = new IconGenerator(DriverTracking.this);
            int shapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
            Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.maps_marker, null);
            iconGen.setBackground(shapeDrawable);
            View view = new View(DriverTracking.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
            iconGen.setContentView(view);
            Bitmap bitmap = iconGen.makeIcon();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 30, 30, false);
            squareMarker = mMap.addMarker(new MarkerOptions().position(points.get(points.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(bitmapResized)));
            polyDirection = mMap.addPolyline(polylineOptions);
        }
    }

    public void onDriverToRiderLocationNavigation(View view) {
        locationNavigation(navigationType);
    }

    private void locationNavigation(String type) {

        if (type.equalsIgnoreCase("destination")) {
            String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination=" + destLat + "+" + destLng;
            Uri location = Uri.parse(URL);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else if (type.equalsIgnoreCase("riderLocation")) {
            String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination=" + riderLat + "+" + riderLng;
            Uri location = Uri.parse(URL);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        //        if (Common.mLastKnownLocation != null) {
//            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + riderLat + ",+" + riderLng + "");
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);
//        } else {
////            Snackbar.make(coordinatorLayout, R.string.message_something_went_wrong, Snackbar.LENGTH_LONG)
////                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
//        }

        //  String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + riderLat + "," + riderLng + "&daddr=" + riderLat + "," + riderLng;

//        String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination=" + riderLat + "+" + riderLng;
//        Uri location = Uri.parse(URL);
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);

//        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("google.navigation:/?free=1&mode=d&entry=fnls"));
//        startActivity(intent);
    }

    public void onRiderPickup(View view) {
        tvPickUp.setText(getString(R.string.txt_req8));
        flRiderInfo.setVisibility(View.GONE);
        llConfirmArrived.setVisibility(View.VISIBLE);
    }

    public void onRiderPickup2(View view) {
        if (tvPickUp.getText().toString().equalsIgnoreCase("PICK UP") ||
                tvPickUp.getText().toString().equalsIgnoreCase("পিকআপ")) {

            tvPickUp.setText(getString(R.string.txt_req8));
            riderDrawer.closeDrawer(GravityCompat.START);
            flRiderInfo.setVisibility(View.GONE);
            llConfirmArrived.setVisibility(View.VISIBLE);
        } else if (tvPickUp.getText().toString().equalsIgnoreCase("Confirm You've Arrived") ||
                tvPickUp.getText().toString().equalsIgnoreCase("নিশ্চিত করুন যে আপনি এসেছেন")) {

            if (AutoRideDriverApps.isNetworkAvailable()) {
                tvPickUp.setText(getString(R.string.btn_start_trip));
                riderDrawer.closeDrawer(GravityCompat.START);
                notificationSendToRider(riderToken, "driver_arrived", "The driver has arrived at your location");
                flRiderInfo.setVisibility(View.GONE);
                llConfirmArrived.setVisibility(View.GONE);
                llStartTrip.setVisibility(View.VISIBLE);
            } else {
                snackBarNoInternet();
            }
        } else if (tvPickUp.getText().toString().equalsIgnoreCase("Start Trip") ||
                tvPickUp.getText().toString().equalsIgnoreCase("ট্রিপ শুরু করুন")) {

            riderDrawer.closeDrawer(GravityCompat.START);
            performStartTrip();
        } else if (tvPickUp.getText().toString().equalsIgnoreCase("DROP OFF") ||
                tvPickUp.getText().toString().equalsIgnoreCase("ট্রিপ শেষ")) {

            tvPickUp.setText(getString(R.string.txt_req11));
            riderDrawer.closeDrawer(GravityCompat.START);
            getDropOff();
        } else if (tvPickUp.getText().toString().equalsIgnoreCase("Collect Cash") ||
                tvPickUp.getText().toString().equalsIgnoreCase("নগদ সংগ্রহ করুন")) {

            riderDrawer.closeDrawer(GravityCompat.START);
            getCashCollect();
        }
    }

    public void onConfirmArrived(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            tvPickUp.setText(getString(R.string.btn_start_trip));
            notificationSendToRider(riderToken, "driver_arrived", "The driver has arrived at your location");
            flRiderInfo.setVisibility(View.GONE);
            llConfirmArrived.setVisibility(View.GONE);
            llStartTrip.setVisibility(View.VISIBLE);
        } else {
            snackBarNoInternet();
        }
    }

    private void notificationSendToRider(String riderToken, String title, String msg) {

        FCMToken fcmFCMToken = new FCMToken(riderToken);
        String driverLatLng = new Gson().toJson(new LatLng(Common.mLastKnownLocation.getLatitude(), Common.mLastKnownLocation.getLongitude()));
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("message", msg);
        content.put("vehicle_type", vehicleType);
        content.put("driver_lat_lng", driverLatLng);

        DataMessage dataMessage = new DataMessage(fcmFCMToken.getToken(), content);
        fcmService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                Log.i(TAG, "success " + response.body().getResults().get(0).getMessage_id());
                if (response.body().getSuccess() == 1) {
                    Log.i(TAG, "Notify success " + response.message());
                    // Toast.makeText(DriverTracking.this, "Notify Success", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Notify Failed " + response.message());
                    // Toast.makeText(DriverTracking.this, "Notify Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.i(TAG, "Notify Error " + t.getMessage());
                // Toast.makeText(DriverTracking.this, "Notify Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStartTrip(View view) {
        nowTripstart();
        llDropOff.setVisibility(View.GONE);
        if (myLocationButton.getVisibility() == View.GONE) {
            myLocationButton.setVisibility(View.VISIBLE);
        }
    }

    private void nowTripstart() {
        performStartTrip();

        riderDrawer.closeDrawer(GravityCompat.START);
        if (mLayout != null) {
            if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

            } else {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        }

    }

    public void onStartTrip2(View view) {
        if (tvStartTrip.getText().toString().equalsIgnoreCase("Start Trip") ||
                tvStartTrip.getText().toString().equalsIgnoreCase("ট্রিপ শুরু করুন")) {

            riderDrawer.closeDrawer(GravityCompat.START);
            performStartTrip();
        } else if (tvStartTrip.getText().toString().equalsIgnoreCase("DROP OFF") ||
                tvStartTrip.getText().toString().equalsIgnoreCase("ট্রিপ শেষ")) {

            riderDrawer.closeDrawer(GravityCompat.START);
            getDropOff();
        } else if (tvStartTrip.getText().toString().equalsIgnoreCase("Collect Cash") ||
                tvStartTrip.getText().toString().equalsIgnoreCase("নগদ সংগ্রহ করুন")) {

            riderDrawer.closeDrawer(GravityCompat.START);
            getCashCollect();
        }
    }

    public void onDropOff(View view) {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        if (myLocationButton.getVisibility() == View.VISIBLE) {
            myLocationButton.setVisibility(View.GONE);
        }
        contentDropOffRippleBackground.stopRippleAnimation();
        getDropOff();
        getCashCollect();
    }

    private void getDropOff() {
        if (AutoRideDriverApps.isNetworkAvailable()) {

            tvCancelCompleteTrip.setText(getString(R.string.txt_req11));
            tvStartTrip.setText(getString(R.string.txt_req11));
            tvPickUp.setText(getString(R.string.txt_req11));

            flRiderInfo.setVisibility(View.GONE);
            llConfirmArrived.setVisibility(View.GONE);
            llStartTrip.setVisibility(View.GONE);
            flRiderTrip.setVisibility(View.GONE);
            llDropOff.setVisibility(View.GONE);
        } else {
            snackBarNoInternet();
        }
    }

    public void onCashCollect(View view) {
        getCashCollect();
    }

    private void getCashCollect() {
        Common.startWaitingDialog(DriverTracking.this, pDialog);
        getCalculatedFare();
    }

    public void onCashCollected(View view) {
        flRiderInfo.setVisibility(View.GONE);
        llConfirmArrived.setVisibility(View.GONE);
        llStartTrip.setVisibility(View.GONE);
        flRiderTrip.setVisibility(View.GONE);
        llDropOff.setVisibility(View.VISIBLE);
        llCalculateFare.setVisibility(View.GONE);

        tvTotalFareCollect.setText(tvTripPayFare.getText());

        btnCompleteTrip.setEnabled(true);
        btnCompleteTrip.setBackgroundColor(Color.RED);
        btnCompleteTrip.setAlpha(1);
    }

    public void onShowDriverContactToRider(View view) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        riderDrawer.closeDrawer(GravityCompat.START);
        vfRiderInfo.setInAnimation(slideRightIn);
        vfRiderInfo.setOutAnimation(slideLeftOut);
        vfRiderInfo.setDisplayedChild(1);
    }

    public void onCancelCompleteTrip(View view) {

     /*   if (tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("CANCEL") ||
                    tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("বাতিল")) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                riderDrawer.closeDrawer(GravityCompat.START);
                vfRiderInfo.setInAnimation(slideRightIn);
                vfRiderInfo.setOutAnimation(slideLeftOut);
                vfRiderInfo.setDisplayedChild(2);
                tripCancelByCause();
            } else if (tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("DROP OFF") ||
                    tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("ট্রিপ শেষ")) {

                riderDrawer.closeDrawer(GravityCompat.START);
                getDropOff();
            } else if (tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("Collect Cash") ||
                    tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("নগদ সংগ্রহ করুন")) {

                riderDrawer.closeDrawer(GravityCompat.START);*/

        getCashCollect();
        if (mLayout != null) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        if (myLocationButton.getVisibility() == View.VISIBLE) {
            myLocationButton.setVisibility(View.GONE);

            if (tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("CANCEL") ||
                    tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("বাতিল")) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                riderDrawer.closeDrawer(GravityCompat.START);
                vfRiderInfo.setInAnimation(slideRightIn);
                vfRiderInfo.setOutAnimation(slideLeftOut);
                vfRiderInfo.setDisplayedChild(2);
                tripCancelByCause();
            } else if (tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("DROP OFF") ||
                    tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("ট্রিপ শেষ")) {

                riderDrawer.closeDrawer(GravityCompat.START);
                getDropOff();
            } else if (tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("Collect Cash") ||
                    tvCancelCompleteTrip.getText().toString().equalsIgnoreCase("নগদ সংগ্রহ করুন")) {

                riderDrawer.closeDrawer(GravityCompat.START);
                getCashCollect();

            }
        }
    }

    public void tripCancelByCause() {

        final TextView tv1 = findViewById(R.id.tv_cause1);
        final TextView tv2 = findViewById(R.id.tv_cause2);
        final TextView tv3 = findViewById(R.id.tv_cause3);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTripDialog("The ride was not charged");
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTripDialog("Rider could not be found");
            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTripDialog("Ride requested cancel");
            }
        });
    }

    private void performTripCancel(String cause) {
        ManagerData.taskManager(POST, REQUESTED_TRIP_CANCEL_URL, getTripCancelBodyJSON("reason", cause), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(DriverInfo driverInfo) {
                Common.stopWaitingDialog(pDialog);
                if (driverInfo != null) {
                    if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        notificationSendToRider(riderToken, "driver_trip_canceled", "The driver has canceled your ride");
                        finishTrackingActivity();
                    } else {
                        snackBarSlowInternet();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(DriverInfo driverInfo) {
                Common.stopWaitingDialog(pDialog);
                snackBarSlowInternet();
            }
        });
    }

    private void performStartTrip() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            Common.startWaitingDialog(DriverTracking.this, pDialog);
            ManagerData.taskManager(GET, START_RIDING_URL, getTripBodyJSON(), getHeaderJSON(), new ParserListener() {
                @Override
                public void onLoadCompleted(DriverInfo driverInfo) {
                    if (driverInfo != null) {
                        if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                            setStartTrip();
                        } else {
                            Common.stopWaitingDialog(pDialog);
                            snackBarSlowInternet();
                        }
                    } else {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                }

                @Override
                public void onLoadFailed(DriverInfo driverInfo) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            });
        } else {
            snackBarNoInternet();
        }
    }

    private void setStartTrip() {
        // need to be rider notify to here
        // pickupLocation = Common.mLastKnownLocation;

        tvCancelCompleteTrip.setText(getString(R.string.txt_req31));
        tvStartTrip.setText(getString(R.string.txt_req31));
        tvPickUp.setText(getString(R.string.txt_req31));

        navigationType = "destination";
        getSupportActionBar().setTitle(R.string.txt_req6);

        toolbarTitle.setText("Trip Running");


        getDestDirection();
        startTripNotification("driver_start_trip", "Driver start trip");

        flRiderInfo.setVisibility(View.GONE);
        llConfirmArrived.setVisibility(View.GONE);
        llStartTrip.setVisibility(View.GONE);
        flRiderTrip.setVisibility(View.VISIBLE);

//        pickupRunning2 = true;
        pickupRunning = true;
        //localCalculation();

        //   calculateDistance();


        Common.stopWaitingDialog(pDialog);
    }

    private void performCompleteTrip() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            ManagerData.taskManager(GET, COMPLETE_TRIP_URL, getTipCompleteBodyJSON(), getHeaderJSON(), new ParserListener() {
                @Override
                public void onLoadCompleted(DriverInfo driverInfo) {
                    Common.stopWaitingDialog(pDialog);
                    if (driverInfo != null) {
                        if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                            // testing
                            stopService(new Intent(getBaseContext(), DistanceService.class));
                            //pickupRunning2 = false;
                            //   locationManager2.removeUpdates(locationListener2);

                            pickupRunning = false;
                            notificationSendToRider(riderToken, "driver_trip_completed", "The driver has completed your ride");
                            finishTrackingActivity();
                        } else {
                            snackBarSlowInternet();
                        }
                    } else {
                        snackBarSlowInternet();
                    }
                }

                @Override
                public void onLoadFailed(DriverInfo driverInfo) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            });
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private void finishTrackingActivity() {
        startActivity(new Intent(DriverTracking.this, DriverMainActivity.class));
        finish();
    }

    private JSONObject getTripCancelBodyJSON(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_IDS, driverId);
            if (key != null && value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getTripBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(RIDER_ID, riderId);
            postBody.put(DRIVER_IDS, driverId);
            postBody.put(PREDICTED_MINUTE, destMin);
            postBody.put(TRIP_AMOUNT, confirmFare);
            postBody.put(PREDICTED_KILOMETER, destKm);
            postBody.put(LAT, Common.mLastKnownLocation.getLatitude());
            postBody.put(LNG, Common.mLastKnownLocation.getLongitude());
            postBody.put(DEST_LAT, destLat);
            postBody.put(DEST_LNG, destLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    public void onDriverToRiderPhoneCall(View view) {
        Common.callToNumber(DriverTracking.this, riderPhone);
    }

    public void onDriverToRiderSms(View view) {
        smsToPhone(riderPhone);
    }

    private void smsToPhone(String pNo) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + pNo)));
    }

    @Override
    public void onBackPressed() {
        riderDrawer.closeDrawer(GravityCompat.START);
        int index = vfRiderInfo.getDisplayedChild();
        if (index > 0) {
            vfRiderInfo.setInAnimation(slideRightIn);
            vfRiderInfo.setOutAnimation(slideLeftOut);
            vfRiderInfo.setDisplayedChild(0);
        } else {
            moveTaskToBack(true);
        }
    }

    private void getDestDirection() {

        if (squareMarker != null) {
            squareMarker.remove();
        }

        if (polyDirection != null) {
            polyDirection.remove();
        }

        String requestApi;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + riderLat + "," + riderLng + "&" +
                    "destination=" + destLat + "," + destLng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi destination " + requestApi);

            gAPIService.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    new DirectionParserTask2().execute(response.body());

                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distanceObj = legsObject.getJSONObject("distance");
                        String distanceText = distanceObj.getString("text");
                        predictedKilometer = Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]+", ""));

                        JSONObject durationObj = legsObject.getJSONObject("duration");
                        String durationText = durationObj.getString("text");
                        predictedMinute = (int) Double.parseDouble(durationText.replaceAll("[^0-9\\\\.]+", ""));

                        String address = legsObject.getString("end_address");
                        tvRiderLocation.setText(address);
                        toolbarAddress.setText(address);
                        tvRiderLocation2.setText(address);
                        riderDestPlace = address;
                    } catch (Exception e) {
                        Log.i(TAG, "direction errors " + e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "direction errors " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "direction errors " + e.toString());
            e.printStackTrace();
        }
    }

    private class DirectionParserTask2 extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.startWaitingDialog(DriverTracking.this, pDialog);
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            Common.stopWaitingDialog(pDialog);
            LatLng position = null;
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get(LAT));
                    double lng = Double.parseDouble(point.get(LNG));
                    position = new LatLng(lat, lng);
                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLACK);
                polylineOptions.geodesic(true);
            }

            IconGenerator iconGen = new IconGenerator(DriverTracking.this);
            int shapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
            Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.maps_marker, null);
            iconGen.setBackground(shapeDrawable);
            View view = new View(DriverTracking.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
            iconGen.setContentView(view);
            Bitmap bitmap = iconGen.makeIcon();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 30, 30, false);
            squareMarker = mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(bitmapResized)));
            mMap.setPadding(300, 350, 0, 0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f));
            polyDirection = mMap.addPolyline(polylineOptions);
        }
    }

    private void completeTripDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverTracking.this);
        builder.setTitle(R.string.txt_req13);
        builder.setMessage(R.string.txt_req30);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.txt_req4, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Common.startWaitingDialog(DriverTracking.this, pDialog);
                dialogInterface.dismiss();

                performCompleteTrip();
                resetData();
                stopService(new Intent(getBaseContext(), GpsServices.class));
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

    private void cancelTripDialog(final String cause) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverTracking.this);
            builder.setTitle(R.string.txt_req32);
            builder.setMessage(R.string.txt_req33);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.txt_req4, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Common.startWaitingDialog(DriverTracking.this, pDialog);
                    dialogInterface.dismiss();
                    performTripCancel(cause);
                }
            });
            builder.setNegativeButton(R.string.txt_req5, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else {
            snackBarNoInternet();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        firstfix = true;
        if (!data.isRunning()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("data", "");
            data = gson.fromJson(json, Data.class);
        }
        if (data == null) {
            data = new Data(onGpsServiceUpdate);
        } else {
            data.setOnGpsServiceUpdate(onGpsServiceUpdate);
        }

        if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        } else {
            Log.w("MainActivity", "No GPS location provider found. GPS data display will not be available.");
        }

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog();
        }

        mLocationManager.addGpsStatusListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefsEditor.putString("data", json);
        prefsEditor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), GpsServices.class));
    }

    public static Data getData() {
        return data;
    }

    private void startTripNotification(String title, String msg) {

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
        content.put("pickup_location", riderPickupLocation);
        content.put("dest_location", riderDestLocation);
        content.put("driver_location", driverLocation);
        content.put("confirm_fare", confirmFare);
        content.put("driver_id", driverId);
        content.put("rider_destination_place", riderDestPlace);
        content.put("driver_photo", dPhoto);
        content.put("vehicle_photo", dvPhoto);
        content.put("driver_name", dName);
        content.put("v_brand", vBrand);
        content.put("driver_rating", dRating);
        content.put("driver_phone", dPhone);
        content.put("v_desc", vDesc);
        content.put("arriving_time", destMin);


        DataMessage dataMessage = new DataMessage(fcmFCMToken.getToken(), content);
        fcmService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body().getSuccess() == 1) {
                    Log.i(TAG, "InDriverTracking success " + response.message());
                    //   Toast.makeText(RiderRideRequestActivity.this, "Cancelled Success", Toast.LENGTH_SHORT).show();
                    // finish();
                } else {
                    Log.i(TAG, "InDriverTracking Failed " + response.message());
                    // Toast.makeText(RiderRideRequestActivity.this, "Cancelled Failed", Toast.LENGTH_SHORT).show();
                    // finish();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.i(TAG, "InDriverTracking Error " + t.getMessage());
                // Toast.makeText(RiderRideRequestActivity.this, "Cancelled Error", Toast.LENGTH_SHORT).show();
                // finish();
            }
        });
    }

    private void snackBarNoInternet() {
        Snackbar snackbar = Snackbar.make(riderDrawer, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void snackBarSlowInternet() {
        Snackbar snackbar = Snackbar.make(riderDrawer, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void calculateDistance() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (distanceCalculateStatus != 1) {
            oldLatitude = Common.mLastKnownLocation.getLatitude();
            oldLongitude = Common.mLastKnownLocation.getLongitude();
        }

        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (getApplicationContext() != null && pickupRunning) {

                    newLatitude = location.getLatitude();
                    newLongitude = location.getLongitude();
                    // totalDistanceBefore.setText("before " + String.valueOf(totalDistance));

                    if (oldLatitude != newLatitude) {

                        totalDistance += meterDistanceBetweenPoints(oldLatitude, oldLongitude, newLatitude, newLongitude); //+ totalDistance;

                        //  increaseDistance.setText("increase " + String.valueOf(meterDistanceBetweenPoints(oldLatitude, oldLongitude, newLatitude, newLongitude)));

                        updateDistanceToServer(String.valueOf(totalDistance));

                        oldLatitude = newLatitude;
                        oldLongitude = newLongitude;
                    }

                    // totalDistanceAfter.setText("after " + String.valueOf(totalDistance));
                } else {
                    //  locationManager.removeUpdates(locationListener);
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        long minTime = 30 * 1000; // Minimum time interval for update in seconds 30 * 1000
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

    private void updateDistanceToServer(String distance) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (AutoRideDriverApps.isLocationEnabled()) {
                try {
                    new GetDistanceUpdate().execute(distance).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar snackbar = Snackbar.make(riderDrawer, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } else {
            snackBarNoInternet();
        }
    }

    private class GetDistanceUpdate extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... distance) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(UPDATE_RIDING_DISTANCE_URL, getDistanceUpdateBodyJSON(distance[0]))
                );
            } catch (Exception e) {
                Log.i(TAG, "error_response " + e.toString());
                e.printStackTrace();
            }
            Log.i(TAG, "ok_http_response " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res != null) {
                distanceParser(res);
            } else {
                snackBarSlowInternet();
            }
        }
    }

    private JSONObject getDistanceUpdateBodyJSON(String distance) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_IDS, driverId);
            postBody.put(RUNNING_KILOMETER, distance);
            postBody.put(LAT, Common.mLastKnownLocation.getLatitude());
            postBody.put(LNG, Common.mLastKnownLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private void distanceParser(String response) {
        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(response);
            if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                        totalDistance = 0.0f;

                        if (responseObj.has(WEB_RESPONSE_DATA)) {

                            JSONObject dataObj = responseObj.optJSONObject(WEB_RESPONSE_DATA);
                            if (dataObj != null) {

                                if (dataObj.has(RUNNING_KILOMETER)) {
                                    String db = dataObj.getString(RUNNING_KILOMETER);
                                    // tvRiderName4.setText(" db_km " + db);
                                    //Toast.makeText(this, "local not_is_db_kilometer " + db, Toast.LENGTH_SHORT).show();
                                    Log.w("TAG", "local not_is_db_kilometer " + db);
                                }

                               /* if (dataObj.has(PREDICTED_KILOMETER)) {
                                    dataObj.getInt(PREDICTED_KILOMETER);
                                }

                                if (dataObj.has(RUNNING_KILOMETER)) {
                                    dataObj.getString(RUNNING_KILOMETER);
                                }*/
                            }
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        snackBarSlowInternet();
                    }
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                    snackBarSlowInternet();
                }
            } else {
                snackBarSlowInternet();
            }
        } catch (Exception e) {
            snackBarSlowInternet();
            e.printStackTrace();
        }
    }

    private void getCalculatedFare() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            ManagerData.stringTaskManager(GET, CALCULATE_FARE_URL, getCalculatedFareBodyJSON(), getHeaderJSON(), new ParserListenerString() {
                @Override
                public void onLoadCompleted(String response) {
                    if (response != null) {
                        calculatedFareParser(response);
                    } else {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                }

                @Override
                public void onLoadFailed(String response) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            });
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private JSONObject getCalculatedFareBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_IDS, driverId);
            postBody.put(LAT, Common.mLastKnownLocation.getLatitude());
            postBody.put(LNG, Common.mLastKnownLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private void calculatedFareParser(String response) {
        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(response);
            if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        if (responseObj.has(WEB_RESPONSE_FARE)) {
                            JSONObject fareObj = responseObj.optJSONObject(WEB_RESPONSE_FARE);
                            if (fareObj != null) {

                                if (fareObj.has(TOTAL_FARE)) {
                                    completeFare = fareObj.getString(TOTAL_FARE);
                                    tvTripPayFare.setText("BDT"+" "+completeFare);
                                    tvTripTotalFare.setText(completeFare);
                                }

                                if (fareObj.has(BASE_FARE)) {
                                    tvTripBaseFare.setText(String.format("BDT %s", String.valueOf(fareObj.getDouble(BASE_FARE))));
                                }

                                if (fareObj.has(MINIMUM_FARE)) {
                                    tvTripMinimumFare.setText(String.format("BDT %s", String.valueOf(fareObj.getDouble(MINIMUM_FARE))));
                                }

                                if (fareObj.has(FARE_PER_MINUTE)) {
                                    tvTripTimeRate.setText(String.format("BDT %s", String.valueOf(fareObj.getDouble(FARE_PER_MINUTE))));
                                }

                                if (fareObj.has(FARE_PER_KILOMETER)) {
                                    tvTripDistanceRate.setText(String.format("BDT %s", String.valueOf(fareObj.getDouble(FARE_PER_KILOMETER))));
                                }

                                if (fareObj.has(USABLE_DISCOUNT)) {
                                    tvTripUsableDiscount.setText(String.format("BDT %s", String.valueOf(fareObj.getDouble(USABLE_DISCOUNT))));
                                }

                                if (fareObj.has(TOTAL_KILOMETERS)) {
                                    completeKM = fareObj.getString(TOTAL_KILOMETERS);
                                    tvTripDistance.setText(completeKM);
                                }

                                if (fareObj.has(TOTAL_MINUTES)) {
                                    completeMin = fareObj.getInt(TOTAL_MINUTES);
                                    tvTripTime.setText(String.format("MIN %s", String.valueOf(completeMin)));
                                }

                                if (fareObj.has(FARE_QR_CODE)) {
                                    String fareQRCode = fareObj.getString(FARE_QR_CODE);
                                    Glide.with(getApplicationContext().getApplicationContext())
                                            .load(fareQRCode)
                                            .apply(new RequestOptions()
                                                    .centerCrop()
                                                    .circleCrop()
                                                    .fitCenter())
                                            .into(ivFareQRCode);
                                }

                                flRiderInfo.setVisibility(View.GONE);
                                llConfirmArrived.setVisibility(View.GONE);
                                llStartTrip.setVisibility(View.GONE);
                                flRiderTrip.setVisibility(View.GONE);
                                llDropOff.setVisibility(View.GONE);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                getSupportActionBar().setTitle(R.string.txt_req7);
                                riderDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                llCalculateFare.setVisibility(View.VISIBLE);
                                Common.stopWaitingDialog(pDialog);
                            }
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            } else {
                Common.stopWaitingDialog(pDialog);
                snackBarSlowInternet();
            }
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            snackBarSlowInternet();
            e.printStackTrace();
        }
    }

    private JSONObject getTipCompleteBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_IDS, driverId);
            postBody.put(TRIP_AMOUNT, completeFare);
            postBody.put(RUNNING_KILOMETER, completeKM);
            postBody.put(LAT, Common.mLastKnownLocation.getLatitude());
            postBody.put(LNG, Common.mLastKnownLocation.getLongitude());
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

    private void startDistanceService() {

       /* distanceInfo = new DistanceInfo();
        dbDistance = Room.databaseBuilder(getApplicationContext(), DBDistance.class, "db_distance").allowMainThreadQueries().build();
     //   if (distanceCalculateStatus2 != 1) {
            File dbFile = getApplicationContext().getDatabasePath("db_distance");
            if (dbFile.exists()) {
                int del = dbDistance.distanceDao().deleteDistanceTable();
                if (del == 1) {
                    Log.w("TAG", "local del = " + del);
                    distanceInfo.setDriverId(driverId);
                    dbDistance.distanceDao().putDistance(distanceInfo);
                } else {
                    Log.w("TAG", "local del = " + del);
                    distanceInfo.setDriverId(driverId);
                    distanceInfo.setTotalDistance(0.0f);
                    dbDistance.distanceDao().updateDistance(distanceInfo);
                }
            } else {
                distanceInfo.setDriverId(driverId);
                dbDistance.distanceDao().putDistance(distanceInfo);
            }
            //oldLatitude2 = Common.mLastKnownLocation.getLatitude();
           // oldLongitude2 = Common.mLastKnownLocation.getLongitude();
       // } else {
        //    distanceInfo.setDriverId(driverId);
      //      dbDistance.distanceDao().updateDistance(distanceInfo);
      //  }*/

        Intent intent = new Intent(this, DistanceService.class);
        startService(intent);
    }


    // distance calculation in local
    // private LocationManager locationManager2;
    //  private android.location.LocationListener locationListener2;
    // private int distanceCalculateStatus2;
    //  private double oldLatitude2, oldLongitude2, newLatitude2, newLongitude2;
    //  private float totalDistance2;
    //  private static boolean pickupRunning2 = false;
    //  private DBDistance dbDistance;
    //  private DistanceInfo distanceInfo;

    private void localCalculation() {
        //startDistanceService();

        // distanceInfo = new DistanceInfo();
        //  dbDistance = Room.databaseBuilder(getApplicationContext(), DBDistance.class, "db_distance").allowMainThreadQueries().build();
        //locationManager2 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //if (distanceCalculateStatus2 != 1) {
           /* File dbFile = getApplicationContext().getDatabasePath("db_distance");
            if (dbFile.exists()) {
                int del = dbDistance.distanceDao().deleteDistanceTable();
                if (del == 1) {
                    Log.w("TAG", "local del = " + del);
                    distanceInfo.setDriverId(driverId);
                    dbDistance.distanceDao().putDistance(distanceInfo);
                } else {
                    Log.w("TAG", "local del = " + del);
                    distanceInfo.setDriverId(driverId);
                    distanceInfo.setTotalDistance(0.0f);
                    dbDistance.distanceDao().updateDistance(distanceInfo);
                }
            } else {
                distanceInfo.setDriverId(driverId);
                dbDistance.distanceDao().putDistance(distanceInfo);
            }*/
        //oldLatitude2 = Common.mLastKnownLocation.getLatitude();
        // oldLongitude2 = Common.mLastKnownLocation.getLongitude();
        //  } else {
        //     distanceInfo.setDriverId(driverId);
        // }

       /* //locationListener2 = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (getApplicationContext() != null ) {

                   // newLatitude2 = location.getLatitude();
                  //  newLongitude2 = location.getLongitude();

                 //   if (oldLatitude2 != newLatitude2) {

                      //  totalDistance2 = meterDistanceBetweenPoints2(oldLatitude2, oldLongitude2, newLatitude2, newLongitude2);
                  //      oldLatitude2 = newLatitude2;
                  //      oldLongitude2 = newLongitude2;
//
                       // Toast.makeText(DriverTracking.this, "local calculate_d " + totalDistance2, Toast.LENGTH_SHORT).show();
                       // Log.w("TAG", "local calculate_d " + totalDistance2);

                        float preTotal = dbDistance.distanceDao().getDistance().get(0).getTotalDistance();
                        Toast.makeText(DriverTracking.this, "local pre_d " + preTotal, Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "local pre_d " + preTotal);

                      //  float currentTotal = preTotal + totalDistance2;
                      //  Toast.makeText(DriverTracking.this, "local current_d " + currentTotal, Toast.LENGTH_SHORT).show();
                      //  Log.w("TAG", "local current_d " + currentTotal);

                        // distance update
                       // distanceInfo.setTotalDistance(currentTotal);
                        Toast.makeText(DriverTracking.this, "local get_current_d " + distanceInfo.getTotalDistance(), Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "local get_current_d " + distanceInfo.getTotalDistance());

                        dbDistance.distanceDao().updateDistance(distanceInfo);
                        tvRiderLocation.setText("local " + String.valueOf(dbDistance.distanceDao().getDistance().get(0).getTotalDistance()));

                        distanceInfo.setTotalDistance(0.0f);
                        Toast.makeText(DriverTracking.this, "local after_d " + distanceInfo.getTotalDistance(), Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "local after_d " + distanceInfo.getTotalDistance() + "\n" + "\n");
                    }
              //  } else {
                    // locationManager2.removeUpdates(locationListener2);
               // }
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        long minTime2 = 30 * 1000; // Minimum time interval for update in seconds 30*1000
        long minDistance2 = 0; // Minimum distance change for update in meters

        locationManager2.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime2, minDistance2, locationListener2);
        locationManager2.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime2, minDistance2, locationListener2);*/
    }

    private float meterDistanceBetweenPoints2(double lat_a, double lng_a, double lat_b, double lng_b) {
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

    private void distanceMeasure(LatLng start, LatLng end) {
      /*  try {
            gAPIService.getPath(Common.directionsApi(start, end, DriverTracking.this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

=======

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        // Toast.makeText(DriverTracking.this, "calculated distance" + dist + "," + Math.abs((float) oldLongitude - (float) newLongitude), Toast.LENGTH_LONG).show();
        // System.out.println("**********this is distance calculation**********" + dist);
        return dist;
    }

    private void distanceMeasure(LatLng start, LatLng end) {
      /*  try {
            gAPIService.getPath(Common.directionsApi(start, end, DriverTracking.this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

>>>>>>> 7087fbe73a0de76950f432b60e86cb45412e51eb
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distanceObj = legsObject.getJSONObject("distance");
                        String distanceText = distanceObj.getString("text");
                        double distanceValue = Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]+", ""));

                      //  Toast.makeText(DriverTracking.this, "local calculate_d " + totalDistance2, Toast.LENGTH_SHORT).show();
                      //  Log.w("TAG", "local calculate_d " + totalDistance2);

                        float preTotal = dbDistance.distanceDao().getDistance().get(0).getTotalDistance();
                        Toast.makeText(DriverTracking.this, "local pre_d " + preTotal, Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "local pre_d " + preTotal);

                        //float currentTotal = preTotal + totalDistance2;
                       // Toast.makeText(DriverTracking.this, "local current_d " + currentTotal, Toast.LENGTH_SHORT).show();
                      //  Log.w("TAG", "local current_d " + currentTotal);

                        // distance update
                       // distanceInfo.setTotalDistance(currentTotal);
                        Toast.makeText(DriverTracking.this, "local get_current_d " + distanceInfo.getTotalDistance(), Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "local get_current_d " + distanceInfo.getTotalDistance());

                        dbDistance.distanceDao().updateDistance(distanceInfo);
                        distanceInfo.setTotalDistance(0.0f);
                        Toast.makeText(DriverTracking.this, "local after_d " + distanceInfo.getTotalDistance(), Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "local after_d " + distanceInfo.getTotalDistance() + "\n" + "\n");

                    } catch (Exception e) {
                        Log.i(TAG, "calculation errors " + e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "calculation errors " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "calculation errors " + e.toString());
            e.printStackTrace();
        }*/
    }


    /************************** Extras
     **********************/
    private void getCompleteTripFare() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            ManagerData.stringTaskManager(GET, CALCULATE_FARE_URL, getCalculatedFareBodyJSON(), null, new ParserListenerString() {
                @Override
                public void onLoadCompleted(String response) {
                    if (response != null) {
                        completeTripFareParser(response);
                    } else {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                }

                @Override
                public void onLoadFailed(String response) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            });
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private void completeTripFareParser(String response) {
        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(response);
            if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        if (responseObj.has(WEB_RESPONSE_FARE)) {
                            JSONObject fareObj = responseObj.optJSONObject(WEB_RESPONSE_FARE);
                            if (fareObj != null) {

                                String kilometers = null;

                                if (fareObj.has(TOTAL_FARE)) {
                                    completeFare = fareObj.getString(TOTAL_FARE);
                                }

                                if (fareObj.has(TOTAL_KILOMETERS)) {
                                    int rideDis = fareObj.getInt(TOTAL_KILOMETERS);
                                    if (rideDis < 1) {
                                        rideDis = 1;
                                    }
                                    kilometers = String.valueOf(rideDis);
                                }

                                // performCompleteTrip(String.valueOf(completeFare), kilometers);

                                /*if (collectFare == completeFare) {
                                    performCompleteTrip(String.valueOf(completeFare), kilometers);
                                } else {
                                    Common.stopWaitingDialog(pDialog);
                                    Snackbar snackbar = Snackbar.make(riderDrawer, "Please collect cash again", Snackbar.LENGTH_LONG)
                                            .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

                                    View sbView = snackbar.getView();
                                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    snackbar.show();
                                }*/
                            }
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            } else {
                Common.stopWaitingDialog(pDialog);
                snackBarSlowInternet();
            }
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            snackBarSlowInternet();
            e.printStackTrace();
        }
    }

    private void calculateFare(Location pickupLocation, Location dropOffLocation) {
        String requestApi;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + pickupLocation.getLatitude() + "," + pickupLocation.getLongitude() + "&" +
                    "destination=" + dropOffLocation.getLatitude() + "," + dropOffLocation.getLongitude() + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi calculate " + requestApi);

            gAPIService.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try { //01834623824

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distanceObj = legsObject.getJSONObject("distance");
                        String distanceText = distanceObj.getString("text");
                        //distanceValue = Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]+", ""));

                        JSONObject timeObj = legsObject.getJSONObject("duration");
                        String timeText = timeObj.getString("text");
                        Double timeValue = Double.parseDouble(timeText.replaceAll("[^0-9\\\\.]+", ""));

                        //  tvTripPayFare.setText(String.format("BDT %s", String.valueOf(Common.priceFormula(distanceValue, timeValue))));
                        // tvTripTotalFare.setText(String.format("BDT %s", String.valueOf(Common.priceFormula(distanceValue, timeValue))));
                        tvTripBaseFare.setText(String.format("BDT %s", String.valueOf(FareInfo.getBaseFare())));
                        tvTripTimeRate.setText(String.format("BDT %s", String.valueOf(FareInfo.getTimeRate())));
                        tvTripDistanceRate.setText(String.format("BDT %s", String.valueOf(FareInfo.getDistanceRate())));
                        //  tvTripRiderDiscount.setText(String.format("BDT %s", String.valueOf(FareInfo.getDiscount())));
                        // tvTripRiderCoupon.setText(String.format("BDT %s", String.valueOf(FareInfo.getCoupon())));
                        //  tvTripDistance.setText(String.format("KM %s", String.valueOf(distanceValue)));
                        tvTripTime.setText(String.format("MIN %s", String.valueOf(timeValue)));

                        // String address = legsObject.getString("end_address");

                    } catch (Exception e) {
                        Log.i(TAG, "calculation errors " + e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "calculation errors " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "calculation errors " + e.toString());
            e.printStackTrace();
        }
    }

    private JSONObject getBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", "userId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }


    public static int getSecondsFromDurationString(String value) {

        String[] parts = value.split(":");

        // Wrong format, no value for you.
        if (parts.length < 2 || parts.length > 3)
            return 0;

        int seconds = 0, minutes = 0, hours = 0;

        if (parts.length == 2) {
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        } else if (parts.length == 3) {
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[0]);
        }

        return seconds + (minutes * 60) + (hours * 3600);
    }

}