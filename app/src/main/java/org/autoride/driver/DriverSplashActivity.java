package org.autoride.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.notifications.commons.Common;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DriverSplashActivity extends BaseAppCompatNoDrawerActivity implements DriverApiUrl {

    private static final String TAG = "DriverSplash";
    private OkHttpClient client;
    private static boolean permissionGranted = false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String[] permissionsRequired = new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity_splash);
        getSupportActionBar().hide();
        logoAnimator();
        setUiComponent();
    }

    private void setUiComponent() {
        swipeView.setPadding(0, 0, 0, 0);
        swipeView.setRefreshing(true);
        client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    private void logoAnimator() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.reset();
        FrameLayout layout = findViewById(R.id.activity_splash);
        layout.clearAnimation();
        layout.startAnimation(animation);
        animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation.reset();
        ImageView ivSplashIcon = (ImageView) findViewById(R.id.iv_driver_splash_icon);
        ivSplashIcon.clearAnimation();
        ivSplashIcon.startAnimation(animation);
    }

    private Thread driverSplashThread = new Thread() {
        @Override
        public void run() {
            try {
                int waited = 0;
                while (waited < 2000) {
                    sleep(100);
                    waited += 100;
                }
                navigate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void navigate() {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (AutoRideDriverApps.isLocationEnabled()) {
                if (AutoRideDriverApps.checkForToken()) {
                    Intent intent = new Intent(DriverSplashActivity.this, DriverMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    DriverSplashActivity.this.startActivity(intent);
                    DriverSplashActivity.this.overridePendingTransition(0, 0);
                    DriverSplashActivity.this.finish();
                } else {
                    AutoRideDriverApps.logout();
                    Intent intent = new Intent(DriverSplashActivity.this, DriverWelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    DriverSplashActivity.this.startActivity(intent);
                    DriverSplashActivity.this.overridePendingTransition(0, 0);
                    DriverSplashActivity.this.finish();
                }
            } else {
                Snackbar.make(coordinatorLayout, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            }
        } else {
            Snackbar.make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
        }
    }

    private boolean checkPermission() {

        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && readPhoneAccepted && writeExternalAccepted) {
                        permissionGranted = true;
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DriverSplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AutoRideDriverApps.isNetworkAvailable()) {
            try {
                String appsV = getAppsVersion();
                String newV = getUpdateVersion();
                if (appsV != null && newV != null) {
                    swipeView.setRefreshing(false);
                    if (!appsV.equalsIgnoreCase(newV)) {
                        showUpdateDialog(newV);
                    } else {
                        if (checkPermission()) {
                            permissionGranted = true;
                        } else if (!checkPermission()) {
                            requestPermission();
                        }
                        if (permissionGranted) {
                            driverSplashThread.start();
                            getUserLastLocation();
                        }
                    }
                } else {
                    Log.i(TAG, "vCheck " + appsV + " newV " + newV);
                    swipeView.setRefreshing(false);
                    Snackbar.make(coordinatorLayout, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
                }
            } catch (Exception e) {
                swipeView.setRefreshing(false);
                e.printStackTrace();
                Snackbar.make(coordinatorLayout, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            }
        } else {
            swipeView.setRefreshing(false);
            Snackbar.make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
        }
    }

    private void showUpdateDialog(String nVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverSplashActivity.this);
        builder.setTitle(getString(R.string.update_available));
        builder.setMessage(getString(R.string.update_message) + " " + nVersion);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_update_available, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.autoride.driver")));
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String getAppsVersion() {
        String appsVersion;
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            appsVersion = packageInfo.versionName;
        } catch (Exception e) {
            appsVersion = null;
            Log.i(TAG, "apps_version " + e.toString());
            e.printStackTrace();
        }
        return appsVersion;
    }

    private String getUpdateVersion() throws ExecutionException, InterruptedException {
        return new GetNewVersion().execute(VERSION_URL).get();
    }

    private class GetNewVersion extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url[0])
                );
            } catch (Exception e) {
                Log.i(TAG, "error_response " + e.toString());
                e.printStackTrace();
            }
            Log.i(TAG, "ok_http_response " + response);
            return versionParser(response);
        }
    }

    private String versionParser(String response) {
        String newVersion = null;
        JSONObject jsonObj = null;
        try {
            if (response != null) {
                jsonObj = new JSONObject(response);
                if (jsonObj.has("statusCode")) {
                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {

                        if (jsonObj.optString("status").equalsIgnoreCase("Success")) {
                            if (jsonObj.has("data")) {
                                JSONObject dataObj = jsonObj.optJSONObject("data");
                                if (dataObj != null) {
                                    if (dataObj.has("versionName")) {
                                        newVersion = dataObj.getString("versionName");
                                    }
                                }
                            }
                        }

                        if (jsonObj.optString("status").equalsIgnoreCase("Error")) {
                            newVersion = null;
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("401")) {
                        newVersion = null;
                    }

                    if (jsonObj.optString("statusCode").equals("404")) {
                        newVersion = null;
                    }

                    if (jsonObj.optString("statusCode").equals("406")) {
                        newVersion = null;
                    }

                    if (jsonObj.optString("statusCode").equals("500")) {
                        newVersion = null;
                    }
                }
            } else {
                newVersion = null;
            }
        } catch (Exception e) {
            newVersion = null;
            e.printStackTrace();
        }
        return newVersion;
    }

    @Override
    public void onBackPressed() {
        //
    }
}