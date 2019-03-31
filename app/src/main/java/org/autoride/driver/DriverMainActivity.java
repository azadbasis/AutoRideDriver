package org.autoride.driver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import org.autoride.driver.SpeedMeter.SpeedMeterActivity;
import org.autoride.driver.SpeedMeter.service.BackService;
import org.autoride.driver.SpeedMeter.service.LocationMonitoringService;
import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.autorideReference.ReferenceActivity;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.driver.documents.DriverDocumentActivity;
import org.autoride.driver.driver.net.ApiUrl;
import org.autoride.driver.driver.net.parsers.DriverStatusParser;
import org.autoride.driver.history.trip.TripHistoryActivity;
import org.autoride.driver.listeners.updates.listeners.ParserListenerString;
import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.TripDetailsInfo;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedBodyBuilder;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.networks.managers.data.ManagerData;
import org.autoride.driver.networks.parsers.ResponseParser;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.notifications.helper.FCMService;
import org.autoride.driver.profiles.DriverProfileActivity;
import org.autoride.driver.rider.request.DriverTracking;
import org.autoride.driver.settings.DriverSettingsActivity;
import org.autoride.driver.utils.AppConstants;
import org.autoride.driver.utils.reference.ReferenceItem;
import org.autoride.driver.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class DriverMainActivity extends BaseAppCompatNoDrawerActivity
        implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        ApiUrl, DriverApiUrl, AppsConstants,
        NetworkConnectionReciever.ConnectivityRecieverListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "DriverMain";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private boolean doubleBackPressed = false;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LatLng driverCurrentLatLng;
    private final LatLng mDefaultLocation = new LatLng(23.8365, 90.3695);
    private static final float MAP_ZOOM = 14.0f;
    private SupportMapFragment driversMapFragment;
    private String accessToken, rememberToken, driverId;
    private GoogleMap driversMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private OkHttpClient client;
    private Marker driverMarker;
    private FCMService mService;
    private Switch switchOnLineOffLine;
    private Handler handler;
    private ProgressDialog pDialog;
    private static ArrayList<ReferenceItem> myrReferenceItemsList;
    private Boolean isConnected;
    private static final String NA = "NA";
    private ReferenceItem referenceItem;
    private int switchStatus = 0;

    Intent intent1;
    PendingIntent pintent1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        mService = Common.getFCMService();
        handler = new Handler();
        pDialog = new ProgressDialog(DriverMainActivity.this);

        myrReferenceItemsList = new ArrayList<>();
        referenceItem = new ReferenceItem();

        if (savedInstanceState != null) {
            Common.mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        Common.startWaitingDialog(DriverMainActivity.this, pDialog);
        startService();
        startBackService();
        if (AutoRideDriverApps.LOCATION != null) {
            stopService();
        }


        Common.startWaitingDialog(DriverMainActivity.this, pDialog);

        setUiComponent();

        if (getIntent() != null) {
            String notificationStatus = getIntent().getStringExtra("notification_status");
            if (notificationStatus != null) {
                if (notificationStatus.equalsIgnoreCase("rider_trip_canceled")) {
                    Toast.makeText(DriverMainActivity.this, getIntent().getStringExtra("notifications_msg"), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setUiComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        CircleImageView civDriverDrawerImg = (CircleImageView) findViewById(R.id.civ_driver_drawer_image);
        TextView tvDriverDrawerFullName = (TextView) findViewById(R.id.tv_driver_drawer_full_name);

        String btnText = null;
        if (Common.getSelectedLanguage(getApplicationContext()).equalsIgnoreCase("bn")) {
            btnText = getString(R.string.txt_set_lang1);
        } else {
            btnText = getString(R.string.txt_set_lang2);
        }
        final Button btnLangChanger = (Button) findViewById(R.id.btn_lang_changer);
        btnLangChanger.setText(btnText);
        btnLangChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
                if (btnLangChanger.getText().toString().equalsIgnoreCase("english")) {
                    LocaleHelpers.setLocale(getApplicationContext(), "en");
                } else {
                    LocaleHelpers.setLocale(getApplicationContext(), "bn");
                }
                Intent intent = new Intent(DriverMainActivity.this, DriverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                DriverMainActivity.this.finish();
            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        driversMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map_frag);
        driversMapFragment.getMapAsync(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences driverInf = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (driverInf != null) {
            Log.i(TAG, "checkForToken: SESSION : " + driverInf.getAll());
            tvDriverDrawerFullName.setText(driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_FIRSTNAME, "") + " " + driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_LASTNAME, ""));
            String img = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_DIMG, "");

            Glide.with(getApplicationContext().getApplicationContext())
                    .load(img)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .fitCenter()
                            .error(R.drawable.ic_profile_photo_default)
                            .fallback(R.drawable.ic_profile_photo_default))
                    .into(civDriverDrawerImg);

            accessToken = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");
        }

        uploadRefence();
        getUserLastLocation();
        getDriverStatus();
        updateFireBaseToken();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        driversMap = googleMap;
//        driversMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));
        driversMap.setTrafficEnabled(false);
        driversMap.setIndoorEnabled(false);
        driversMap.setBuildingsEnabled(false);
        driversMap.getUiSettings().setZoomControlsEnabled(false);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {

        if (driversMap == null) {
            return;
        }

        try {
            if (mLocationPermissionGranted) {

                View locButton = ((View) driversMapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlPosition = (RelativeLayout.LayoutParams) locButton.getLayoutParams();

                rlPosition.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlPosition.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlPosition.setMargins(0, 0, 35, 150);

                driversMap.setMyLocationEnabled(true);
                driversMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                driversMap.setMyLocationEnabled(false);
                driversMap.getUiSettings().setMyLocationButtonEnabled(false);
                Common.mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Common.mLastKnownLocation = task.getResult();
                            if (Common.mLastKnownLocation != null) {
                                driverCurrentLatLng = new LatLng(Common.mLastKnownLocation.getLatitude(), Common.mLastKnownLocation.getLongitude());
                                driversMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, MAP_ZOOM));
                                // driversMap.addMarker(new MarkerOptions().position(driverCurrentLatLng).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                              /*  try {
                                    Geocoder geocoder = new Geocoder(getBaseContext());
                                    List<Address> addressList = geocoder.getFromLocation(driverCurrentLatLng.latitude, driverCurrentLatLng.longitude, 1);
                                    Address address = addressList.get(0);
                                    address.getFeatureName();

                                    //Toast.makeText(rootView.getContext().getApplicationContext(), "curPlace " + driverCurrentPlace, Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } */
                            } else {
                                Log.e(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                driversMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, MAP_ZOOM));
                                driversMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        } else {
                            Log.e(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            driversMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, MAP_ZOOM));
                            driversMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Snackbar.make(coordinatorLayout, "Location can't detect", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (driversMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, driversMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, Common.mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    Snackbar.make(coordinatorLayout, R.string.location_permission, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        performGetLastTrip();
        // startSetDriverLocationTask();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStack();
            } else {
                if (doubleBackPressed) {
                    moveTaskToBack(true);
                } else {
                    doubleBackPressed = true;
                    Snackbar.make(coordinatorLayout, R.string.back_press, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackPressed = false;
                        }
                    }, 3000);
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_driver_main, menu);
        MenuItem itemSwitch = menu.findItem(R.id.item_switch_on_off);
        itemSwitch.setActionView(R.layout.layout_online_offline_switch);
        switchOnLineOffLine = (Switch) itemSwitch.getActionView().findViewById(R.id.switch_driver_on_off);
        switchOnLineOffLine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switchStatus = 1;
                switchOnLineOffLine.setOnCheckedChangeListener(DriverMainActivity.this);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (switchStatus != 0) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                if (switchOnLineOffLine.isChecked()) {
                    getSupportActionBar().setTitle(R.string.txt_on_off1);
                    driverStatusUpdate("active");
                } else {
                    getSupportActionBar().setTitle(R.string.txt_on_off3);
                    driverStatusUpdate("inactive");
                }
            } else {
                if (switchOnLineOffLine.isChecked()) {
                    switchOnLineOffLine.setChecked(false);
                } else {
                    switchOnLineOffLine.setChecked(true);
                }
                snackBarNoInternet();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (driversMap != null) {
            if (id == R.id.map_none) {
                driversMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            } else if (id == R.id.map_normal) {
                driversMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (id == R.id.map_satellite) {
                driversMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (id == R.id.map_hybrid) {
                driversMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (id == R.id.map_terrain) {
                driversMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDriverProfile(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            Intent intent = new Intent(DriverMainActivity.this, DriverProfileActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    public void onTripHistory(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            Intent intent = new Intent(DriverMainActivity.this, TripHistoryActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    public void onDriverSettings(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            Intent intent = new Intent(DriverMainActivity.this, DriverSettingsActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    public void onDriverDocument(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            Intent intent = new Intent(DriverMainActivity.this, DriverDocumentActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    @Override
    public boolean isFinishing() {
        return super.isFinishing();
    }

    public void onLogout(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverMainActivity.this);
        builder.setTitle(R.string.btn_logout);
        builder.setMessage(R.string.txt_log_out);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.btn_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                new UpdateDriverStatus().execute("inactive");
                logOutHere();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stopSetDriverLocationTask();
    }

    public void onMyPartner(View view) {
        Intent intent = new Intent(DriverMainActivity.this, ReferenceActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        // DriverMainActivity.this.finish();
    }

    @Override
    public void OnNetworkChange(boolean inConnected) {

    }

    private void getDriverStatus() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            new GetDriverStatus().execute();
        } else {
            snackBarNoInternet();
        }
    }

    public void getDriverSpeed(View view) {
        startActivity(new Intent(this, SpeedMeterActivity.class));
    }

    @SuppressLint("StaticFieldLeak")
    private class GetDriverStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(DRIVER_STATUS_URL, getBodyJSON(null, null)),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
                Log.i(TAG, "ok_http_response " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            statusParser(result);
        }
    }
    void startBackService() {
        Intent intent1 = new Intent(DriverMainActivity.this, BackService.class);
        PendingIntent pintent1 = PendingIntent.getService(DriverMainActivity.this, 0, intent1, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 6 * 60 * 1000, pintent1);
    }

    void startService() {
        intent1 = new Intent(AutoRideDriverApps.getInstance(), LocationMonitoringService.class);
        pintent1 = PendingIntent.getService(AutoRideDriverApps.getInstance(), 0, intent1, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4 * 60 * 1000, 5 * 60 * 1000, pintent1);
    }

    public void stopService() {
        Intent intent2 = new Intent(AutoRideDriverApps.getInstance(), LocationMonitoringService.class);
        PendingIntent pintent2 = PendingIntent.getService(AutoRideDriverApps.getInstance(), 0, intent2, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4 * 60 * 1000, 5 * 60 * 1000, pintent2);
        if (intent2.filterEquals(intent1)) {
            stopService(intent2);
            alarm.cancel(pintent1);
        }
    }
    private void statusParser(String result) {
        AuthBean authBean = DriverStatusParser.parseDriverStatus(result);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {
                switchOnLineOffLine.setChecked(true);
                switchOnLineOffLine.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(R.string.txt_on_off5);
                startSetDriverLocationTask();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                switchOnLineOffLine.setChecked(false);
                switchOnLineOffLine.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(R.string.txt_on_off6);
                stopSetDriverLocationTask();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                stopSetDriverLocationTask();
                Log.i(TAG, "error" + authBean.getErrorMsg());
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();

                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void driverStatusUpdate(String onOff) {
        Common.startWaitingDialog(DriverMainActivity.this, pDialog);
        new UpdateDriverStatus().execute(onOff);
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateDriverStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(UPDATE_DRIVER_STATUS_URL),
                        RequestedBodyBuilder.buildRequestedBody(getStatusUpdateBodyJSON(url[0])),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
                Log.i(TAG, "ok_http_response " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            statusUpdateParser(result);
        }
    }

    private void statusUpdateParser(String response) {
        AuthBean authBean = DriverStatusParser.parseDriverStatus(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {
                switchOnLineOffLine.setOnCheckedChangeListener(null);
                if (authBean.getDriverCurrentStatus().equalsIgnoreCase("active")) {
                    switchOnLineOffLine.setChecked(true);
                    getSupportActionBar().setTitle(R.string.txt_on_off5);
                    startSetDriverLocationTask();
                } else {
                    switchOnLineOffLine.setChecked(false);
                    getSupportActionBar().setTitle(R.string.txt_on_off6);
                    stopSetDriverLocationTask();
                }
                Common.stopWaitingDialog(pDialog);
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                switchOnLineOffLine.setOnCheckedChangeListener(null);
                if (switchOnLineOffLine.isChecked()) {
                    switchOnLineOffLine.setChecked(false);
                    getSupportActionBar().setTitle(R.string.txt_on_off6);
                    stopSetDriverLocationTask();
                } else {
                    switchOnLineOffLine.setChecked(true);
                    getSupportActionBar().setTitle(R.string.txt_on_off5);
                }
                Common.stopWaitingDialog(pDialog);
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                stopSetDriverLocationTask();
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_LONG).show();
                logOutHere();
            }
        } else {
            switchOnLineOffLine.setOnCheckedChangeListener(null);
            if (switchOnLineOffLine.isChecked()) {
                switchOnLineOffLine.setChecked(false);
                getSupportActionBar().setTitle(R.string.txt_on_off6);
                stopSetDriverLocationTask();
            } else {
                switchOnLineOffLine.setChecked(true);
                getSupportActionBar().setTitle(R.string.txt_on_off5);
            }
            Common.stopWaitingDialog(pDialog);
            snackBarSlowInternet();
        }
    }

    private void updateFireBaseToken() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            String fcmToken = FirebaseInstanceId.getInstance().getToken();
            Log.i(TAG, "fire_base_tokens " + fcmToken);
            new SaveFCMToken().execute(fcmToken);
        } else {
            snackBarNoInternet();
            // new PopupMessage(this).show(AppConstants.NO_NETWORK_AVAILABLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveFCMToken extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... token) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(SET_FIRE_BASE_TOKEN_URL, getBodyJSON("fireBaseToken", token[0])),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
                Log.i(TAG, "ok_http_response " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserFCMToken(result);
        }
    }

    private void parserFCMToken(String response) {
        AuthBean authBean = ResponseParser.responseParser(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {
                // Toast.makeText(getBaseContext(), authBean.getErrorMsg(), Toast.LENGTH_SHORT).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                // Toast.makeText(getBaseContext(), authBean.getErrorMsg(), Toast.LENGTH_SHORT).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                // Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private JSONObject getBodyJSON(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
            if (key != null & value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getStatusUpdateBodyJSON(String dStatus) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("userId", driverId);
            postData.put("lat", String.valueOf(lastLatitude));
            postData.put("lng", String.valueOf(lastLongitude));
            if (dStatus != null) {
                postData.put("status", dStatus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getHeaderJSON() {
        JSONObject postHeader = new JSONObject();
        try {
            postHeader.put("access_token", accessToken);
            postHeader.put("rememberToken", rememberToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postHeader;
    }

    private void markerMover(final Marker marker, final float degree) {
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                float rot = t * degree + (1 - t) * startRotation;
                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void startSetDriverLocationTask() {
        handler.post(setDriverLocation);
    }

    private void stopSetDriverLocationTask() {
        if (setDriverLocation != null) {
            handler.removeCallbacks(setDriverLocation);
        }
    }

    private Runnable setDriverLocation = new Runnable() {
        @Override
        public void run() {
            driverLocationUpdate();
            int FIVE_MINUTES = 300000;
            handler.postDelayed(setDriverLocation, FIVE_MINUTES);
        }
    };

    private void driverLocationUpdate() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (lastLatitude != null && lastLongitude != null) {
                new UpdateDriverLocation().execute();
            } else {
                Snackbar.make(coordinatorLayout, "Can't detect your current location", Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            }
        } else {
            snackBarNoInternet();
            // new PopupMessage(this).show(AppConstants.NO_NETWORK_AVAILABLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateDriverLocation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(UPDATE_DRIVER_LOCATION_URL, getStatusUpdateBodyJSON(null)),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
                Log.i(TAG, "ok_http_response " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                snackBarSlowInternet();
            }
            // Toast.makeText(DriverMainActivity.this, "1111" + result, Toast.LENGTH_SHORT).show();
        }
    }

    private void markerRemover() {
        handler.postDelayed(new Runnable() {
            public void run() {
               /* if (driverMarker != null) {
                    driverMarker.remove();
                }*/
            }
        }, 5000);
    }

    private void snackBarNoInternet() {
        Snackbar.make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
    }

    private void snackBarSlowInternet() {
        Snackbar.make(coordinatorLayout, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
    }

    private void logOutHere() {
        AutoRideDriverApps.logout();
        Intent intent = new Intent(DriverMainActivity.this, DriverWelcomeActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        DriverMainActivity.this.finish();
    }

    private void performGetLastTrip() {
        ManagerData.stringTaskManager(GET, RIDING_MODE_URL, getBodyJSON(null, null), getHeaderJSON(), new ParserListenerString() {
            @Override
            public void onLoadCompleted(String response) {
                if (response != null) {
                    lastTripParser(response);
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
    }

    private void lastTripParser(String response) {
        TripDetailsInfo tripInfo;
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {
                        if (responseObj.optString(RIDING_MODE_STATUS).equalsIgnoreCase(MODE_STATUS_RIDING)) {

                            tripInfo = new TripDetailsInfo();

                            if (responseObj.has(TRIP_DETAILS)) {
                                JSONObject tripObj = responseObj.optJSONObject(TRIP_DETAILS);
                                if (tripObj != null) {

                                    if (tripObj.has(PREDICTED_AMOUNT)) {
                                        tripInfo.setPredictedAmount(tripObj.getDouble(PREDICTED_AMOUNT));
                                    }

                                    if (tripObj.has(PREDICTED_KILOMETER)) {
                                        tripInfo.setPredictedKilometer(tripObj.getDouble(PREDICTED_KILOMETER));
                                    }

                                    if (tripObj.has(PREDICTED_MINUTE)) {
                                        tripInfo.setPredictedMinute(tripObj.getDouble(PREDICTED_MINUTE));
                                    }

                                    if (tripObj.has(PICKUP_LOCATION)) {
                                        JSONObject pickObj = tripObj.optJSONObject(PICKUP_LOCATION);
                                        if (pickObj != null) {
                                            if (pickObj.has(LAT)) {
                                                tripInfo.setPickupLat(pickObj.getDouble(LAT));
                                            }

                                            if (pickObj.has(LNG)) {
                                                tripInfo.setPickupLng(pickObj.getDouble(LNG));
                                            }
                                        }
                                    }

                                    if (tripObj.has(DESTINATION)) {
                                        JSONObject destObj = tripObj.optJSONObject(DESTINATION);
                                        if (destObj != null) {
                                            if (destObj.has(LAT)) {
                                                tripInfo.setDestLat(destObj.getDouble(LAT));
                                            }

                                            if (destObj.has(LNG)) {
                                                tripInfo.setDestLng(destObj.getDouble(LNG));
                                            }
                                        }
                                    }

                                    if (tripObj.has(DRIVER_LOCATION)) {
                                        JSONObject locObj = tripObj.optJSONObject(DRIVER_LOCATION);
                                        if (locObj != null) {
                                            if (locObj.has(LAT)) {
                                                tripInfo.setDriverLat(locObj.getDouble(LAT));
                                            }

                                            if (locObj.has(LNG)) {
                                                tripInfo.setDriverLng(locObj.getDouble(LNG));
                                            }
                                        }
                                    }
                                }
                            }

                            if (responseObj.has(VEHICLE_DETAILS)) {
                                JSONObject vehicleObj = responseObj.optJSONObject(VEHICLE_DETAILS);
                                if (vehicleObj != null) {
                                    if (vehicleObj.has(VEHICLE_TYPE)) {
                                        tripInfo.setVehicleType(vehicleObj.getString(VEHICLE_TYPE));
                                    }

                                    if (vehicleObj.has(VEHICLE_BRAND)) {
                                        tripInfo.setVehicleBrand(vehicleObj.getString(VEHICLE_BRAND));
                                    }

                                    if (vehicleObj.has(VEHICLE_MODEL)) {
                                        tripInfo.setVehicleModel(vehicleObj.getString(VEHICLE_MODEL));
                                    }

                                    if (vehicleObj.has(VEHICLE_NUMBER)) {
                                        tripInfo.setVehicleNumber(vehicleObj.getString(VEHICLE_NUMBER));
                                    }
                                }
                            }

                            if (responseObj.has(DRIVER_DETAILS)) {
                                JSONObject driverObj = responseObj.optJSONObject(DRIVER_DETAILS);
                                if (driverObj != null) {

                                    if (driverObj.has(DRIVER_IDS)) {
                                        tripInfo.setDriverId(driverObj.getString(DRIVER_IDS));
                                    }

                                    if (driverObj.has(FIRE_BASE_TOKEN)) {
                                        tripInfo.setRiderFcmToken(driverObj.getString(FIRE_BASE_TOKEN));
                                    }

                                    if (driverObj.has(PHONE)) {
                                        tripInfo.setDriverPhone(driverObj.getString(PHONE));
                                    }

                                    if (driverObj.has(FIRST_NAME)) {
                                        tripInfo.setDriverFirstName(driverObj.getString(FIRST_NAME));
                                    }

                                    if (driverObj.has(LAST_NAME)) {
                                        tripInfo.setDriverLastName(driverObj.getString(LAST_NAME));
                                    }

                                    if (driverObj.has(PROFILE_PHOTO)) {
                                        tripInfo.setDriverPhoto(driverObj.getString(PROFILE_PHOTO));
                                    }

                                    if (driverObj.has(RATING)) {
                                        tripInfo.setDriverRating(driverObj.getString(RATING));
                                    }
                                }
                            }

                            if (responseObj.has(RIDER_DETAILS)) {
                                JSONObject userObj = responseObj.optJSONObject(RIDER_DETAILS);
                                if (userObj != null) {

                                    if (userObj.has(RIDER_ID)) {
                                        tripInfo.setRiderId(userObj.getString(RIDER_ID));
                                    }

                                    if (userObj.has(FIRE_BASE_TOKEN)) {
                                        tripInfo.setRiderFcmToken(userObj.getString(FIRE_BASE_TOKEN));
                                    }

                                    if (userObj.has(PHONE)) {
                                        tripInfo.setRiderPhone(userObj.getString(PHONE));
                                    }

                                    if (userObj.has(FIRST_NAME)) {
                                        tripInfo.setRiderFirstName(userObj.getString(FIRST_NAME));
                                    }

                                    if (userObj.has(LAST_NAME)) {
                                        tripInfo.setRiderLastName(userObj.getString(LAST_NAME));
                                    }

                                    if (userObj.has(PROFILE_PHOTO)) {
                                        tripInfo.setRiderPhoto(userObj.getString(PROFILE_PHOTO));
                                    }

                                    if (userObj.has(RATING)) {
                                        tripInfo.setRiderRating(userObj.getString(RATING));
                                    }
                                }
                            }
                            showTripActivity(tripInfo);
                        } else {
                            // handle here
                            Common.stopWaitingDialog(pDialog);
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
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

    private void showTripActivity(TripDetailsInfo tripDetails) {

        Intent intent = new Intent(DriverMainActivity.this, DriverTracking.class);

        intent.putExtra("intent_status", "driver_riding_mode");
        intent.putExtra("rider_fcm_token", tripDetails.getRiderFcmToken());
        intent.putExtra("vehicle_type", tripDetails.getVehicleType());
        intent.putExtra("rider_id", tripDetails.getRiderId());
        intent.putExtra("pickup_lat", tripDetails.getPickupLat());
        intent.putExtra("pickup_lng", tripDetails.getPickupLng());
        intent.putExtra("dest_lat", tripDetails.getDestLat());
        intent.putExtra("dest_lng", tripDetails.getDestLng());
        intent.putExtra("confirm_fare", tripDetails.getPredictedAmount());
        intent.putExtra("rider_phone", tripDetails.getRiderPhone());
        intent.putExtra("rider_name", tripDetails.getRiderFirstName());
        intent.putExtra("photo_url", tripDetails.getRiderPhoto());
        intent.putExtra("driver_photo", tripDetails.getDriverPhoto());
        intent.putExtra("driver_name", tripDetails.getDriverFirstName());
        intent.putExtra("v_brand", tripDetails.getVehicleBrand());
        intent.putExtra("driver_rating", tripDetails.getDriverRating());
        intent.putExtra("driver_phone", tripDetails.getDriverPhone());
        intent.putExtra("v_desc", tripDetails.getVehicleNumber());
        intent.putExtra("dest_km", tripDetails.getPredictedKilometer());
        intent.putExtra("dest_min", tripDetails.getPredictedMinute());
        intent.putExtra("driver_lat", tripDetails.getDriverLat());
        intent.putExtra("driver_lng", tripDetails.getDriverLng());
        intent.putExtra("driver_cur_lat", lastLatitude);
        intent.putExtra("driver_cur_lng", lastLongitude);
        intent.putExtra("rider_rating", (tripDetails.getRiderRating().equalsIgnoreCase("null") ? "0.0" : tripDetails.getRiderRating()));

        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void uploadRefence() {
        if (checkConnectivity()) {
            try {
                getReference();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showSnackBar();
        }
    }

    public void getReference() throws Exception {
        String url = AppConstants.URL + driverId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Result", response);
                parseReference(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AutoRideDriverApps.getInstance().addToRequestQueue(stringRequest);
    }

    public void parseReference(String response) {

        JSONArray jsonArray;
        JSONObject referenceObject;

        try {
            referenceObject = new JSONObject(response);
            //  jsonArray = new JSONArray(new String(response));
            jsonArray = referenceObject.getJSONArray("reference");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject addressObject = jsonObject.getJSONObject("address");
                JSONObject registerLocationObject = jsonObject.getJSONObject("registeredLocation");
                if (isContain(jsonObject, "name")) {
                    if (jsonObject.getString("name").equalsIgnoreCase("null")) {
                        referenceItem.name = "--";
                    } else {
                        referenceItem.name = jsonObject.getString("name");
                    }
                } else {
                    referenceItem.name = NA;
                }
                if (isContain(jsonObject, "status")) {

                    if (jsonObject.getString("status").equalsIgnoreCase("null")) {
                        referenceItem.status = "--";
                    } else {
                        referenceItem.status = jsonObject.getString("status");
                    }
                } else {
                    referenceItem.status = NA;
                }
                if (isContain(jsonObject, "address")) {
                    referenceItem.address = jsonObject.getString("address");

                    for (int addressIndex = 0; addressIndex < addressObject.length(); addressIndex++) {

                        String house = addressObject.getString("house");
                        if (house.equalsIgnoreCase("null")) {
                            house = "--";
                        }
                        String road = addressObject.getString("road");
                        if (road.equalsIgnoreCase("null")) {
                            road = "--";
                        }
                        String stateProvince = addressObject.getString("stateProvince");
                        if (stateProvince.equalsIgnoreCase("null")) {
                            stateProvince = "district--";
                        }
                        String country = addressObject.getString("country");
                        if (country.equalsIgnoreCase("null")) {
                            country = "country--";
                        }
                        String unit = addressObject.getString("unit");
                        if (unit.equalsIgnoreCase("null")) {
                            unit = "--";
                        }

                        String zipCode = addressObject.getString("zipCode");
                        if (zipCode.equalsIgnoreCase("null")) {
                            zipCode = "--";
                        }
                        String fax = addressObject.getString("fax");
                        if (fax.equalsIgnoreCase("null")) {
                            fax = "--";
                        }
                        referenceItem.address = "House# " + house + ", " + "Road# " + road + "\n" + "Unit# " + unit + "," + "Zipcode# " + zipCode + "," + "Fax# " + fax + "\n" + stateProvince + ", " + country;
                    }
                } else {
                    referenceItem.address = NA;
                }
                if (isContain(jsonObject, "registeredLocation")) {

                    referenceItem.registeredLocation = jsonObject.getString("registeredLocation");
                    for (int latIndex = 0; latIndex < registerLocationObject.length(); latIndex++) {
                        String lng = registerLocationObject.getString("lng");
                        String lat = registerLocationObject.getString("lat");
                        double longitude = Double.parseDouble(lng);
                        double latitude = Double.parseDouble(lat);
                        if (longitude == 0 & latitude == 0) {
                            referenceItem.registeredLocation = "Registered Location --";
                        } else {
                            try {
                                Geocoder geocoder = new Geocoder(getBaseContext());
                                if (longitude != 0 & latitude != 0) {
                                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                    Address address = addressList.get(0);
                                    String riderSearchPlace = address.getAddressLine(0);
                                    referenceItem.registeredLocation = riderSearchPlace;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    referenceItem.registeredLocation = NA;
                }
                if (isContain(jsonObject, "accountNumber")) {

                    if (jsonObject.getString("accountNumber").equalsIgnoreCase("null")) {
                        referenceItem.accountNumber = "A/C--";
                    } else {
                        referenceItem.accountNumber = jsonObject.getString("accountNumber");
                    }

                } else {
                    referenceItem.status = NA;
                }
                if (isContain(jsonObject, "createdDate")) {
                    if (jsonObject.getString("createdDate").equalsIgnoreCase("null")) {
                        referenceItem.createdDate = "--";
                    } else {
                        referenceItem.createdDate = jsonObject.getString("createdDate");
                    }
                } else {
                    referenceItem.status = NA;
                }
                if (isContain(jsonObject, "imgeUrl")) {
                    if (jsonObject.getString("imgeUrl").equalsIgnoreCase("http://128.199.80.10/golden/image")) {
                        referenceItem.imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9ol5BfFvTTdFKhwwTrp5XXeTbvmDiIAXJ2fheHMdCC4oxH1nh";
                    } else {
                        referenceItem.imageUrl = jsonObject.getString("imgeUrl");
                    }
                } else {
                    referenceItem.imageUrl = NA;
                }
//                Utils.generateReferenceItems(this).add(referenceItem);
                myrReferenceItemsList.add(new ReferenceItem(referenceItem.name, referenceItem.status, referenceItem.address, referenceItem.accountNumber, referenceItem.registeredLocation, referenceItem.createdDate, referenceItem.imageUrl));
                Log.d("referenceItemListsIZE", myrReferenceItemsList.size() + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ReferenceItem> generateReferenceItems(Context context) {
        ArrayList<ReferenceItem> referenceItems = new ArrayList<>();
        referenceItems = myrReferenceItemsList;
        return referenceItems;
    }

    public boolean isContain(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

    public void showSnackBar() {

        //into threa
        Snackbar.make(drawerLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_setting), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();
    }

    public boolean checkConnectivity() {
        return NetworkConnectionReciever.isConnected();
    }

    public void onCallTo999(View view) {
        Common.callToNumber(DriverMainActivity.this, "999");
    }
}