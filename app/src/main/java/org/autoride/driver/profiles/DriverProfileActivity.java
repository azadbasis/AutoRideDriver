package org.autoride.driver.profiles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.parsers.DriverInfoParser;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class DriverProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, DriverApiUrl {

    private static final String TAG = "DriverProfile";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayout mTitleContainer;
    private TextView tvDTitleName, tvDProfileName, tvDriverVehicleModel, tvDriverVehicleNo, tvDriverVehicleBrand, tvMainBal, tvUsableBal, tvCommission,
            tvDriverTrip, tvDriverRating, tvDriverYears, tvPickUpRate, tvVehicleSpeed, tvDPhone, tvDEmail, tvDAccountNumber, tvDPromoCode,
            tvDAddress, tvDTotalCommission, tvDLostCommission, tvDDailyTarget, tvDWeeklyTarget, tvDriverNearestTouchPoint;
    private CircleImageView civDriverProfileImg;
    private ImageView driverVehicleImg;
    private String accessToken, rememberToken, driverId;
    private static OkHttpClient client;
    private MenuItem itemDriverStatus;
    private CoordinatorLayout coordinatorLayoutProfile;
    private View.OnClickListener snackBarDismissListener;
    private ProgressDialog pDialog;
    private double touchLat, touchLng;
    private String touchPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        client = new OkHttpClient();

        setUiViewComponent();
    }

    private void setUiViewComponent() {

        Toolbar driverProfileToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(driverProfileToolbar);

        coordinatorLayoutProfile = (CoordinatorLayout) findViewById(R.id.coordinator_layout_profile);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linear_layout_title);
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        tvDTitleName = (TextView) findViewById(R.id.tv_profile_tab_driver_name);
        tvDProfileName = (TextView) findViewById(R.id.tv_profile_driver_name);

        tvDriverVehicleModel = (TextView) findViewById(R.id.tv_driver_vehicle_model_name);
        tvDriverVehicleBrand = (TextView) findViewById(R.id.tv_driver_vehicle_brand_name);
        tvDriverVehicleNo = (TextView) findViewById(R.id.tv_driver_vehicle_rg_no);

        tvMainBal = (TextView) findViewById(R.id.tv_bal_main);
        tvUsableBal = (TextView) findViewById(R.id.tv_bal_usable);
        tvCommission = (TextView) findViewById(R.id.tv_commission);

        tvDriverTrip = (TextView) findViewById(R.id.tv_driver_trip);
        tvDriverRating = (TextView) findViewById(R.id.tv_driver_rating);
        tvDriverYears = (TextView) findViewById(R.id.tv_driver_years);
        tvPickUpRate = (TextView) findViewById(R.id.tv_driver_pick_up_rate);
        tvVehicleSpeed = (TextView) findViewById(R.id.tv_driver_vehicle_speed);

        tvDPhone = (TextView) findViewById(R.id.tv_profile_driver_phone);
        tvDEmail = (TextView) findViewById(R.id.tv_profile_driver_email);
        tvDAccountNumber = (TextView) findViewById(R.id.tv_driver_account_number);
        tvDPromoCode = (TextView) findViewById(R.id.tv_driver_promo_code);
        tvDAddress = (TextView) findViewById(R.id.tv_profile_driver_address);

        tvDTotalCommission = (TextView) findViewById(R.id.tv_driver_total_commission);
        tvDLostCommission = (TextView) findViewById(R.id.tv_driver_lost_commission);
        tvDDailyTarget = (TextView) findViewById(R.id.tv_driver_daily_target);
        tvDWeeklyTarget = (TextView) findViewById(R.id.tv_driver_weekly_target);
        tvDriverNearestTouchPoint = (TextView) findViewById(R.id.tv_driver_nearest_touch_point);

        civDriverProfileImg = (CircleImageView) findViewById(R.id.civ_driver_profile_img);
        driverVehicleImg = (ImageView) findViewById(R.id.iv_driver_vehicle_photo);

        pDialog = new ProgressDialog(DriverProfileActivity.this);
        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.setVisibility(View.GONE);
            }
        };

        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(tvDTitleName, 0, View.INVISIBLE);

        SharedPreferences dinf = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (dinf != null) {
            Log.i(TAG, "checkForToken: " + dinf.getAll());

            accessToken = dinf.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = dinf.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = dinf.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");

            getDriverProfile();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(tvDTitleName, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(tvDTitleName, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        itemDriverStatus = menu.findItem(R.id.menu_driver_toolbar);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                // lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                onGoBack();
//                return true;
//        }
        return true;
    }

    @Override
    public void onBackPressed() {
        onGoBack();
    }

    private void onGoBack() {
        Intent intent = new Intent(DriverProfileActivity.this, DriverMainActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        DriverProfileActivity.this.finish();
    }

    // get set profile info
    private void getDriverProfile() {
        Common.startWaitingDialog(this, pDialog);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            new GetSetDriverProfile().execute();
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
            // new PopupMessage(this).show(AppConstants.NO_NETWORK_AVAILABLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSetDriverProfile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(DRIVER_PROFILE_URL, getBodyJSON()),
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
            profileParse(result);
        }
    }

    @SuppressLint("SetTextI18n")
    private void profileParse(String serverResponse) {
        List<DriverInfo> driverInfoList = new DriverInfoParser().parseDriverInfoRequest(serverResponse);
        if (driverInfoList != null) {
            for (DriverInfo driverInfo : driverInfoList) {
                if (driverInfo.getStatus().equalsIgnoreCase("Success")) {

                    tvDTitleName.setText(driverInfo.getDriverAsUser().getFullName().equals("null") ? "" : driverInfo.getDriverAsUser().getFullName());
                    tvDProfileName.setText(driverInfo.getDriverAsUser().getFullName().equals("null") ? "" : driverInfo.getDriverAsUser().getFullName());

                    tvDriverVehicleBrand.setText(driverInfo.getDriverAsUser().getVehicleBrand().equals("null") ? getString(R.string.txt_profile3) : driverInfo.getDriverAsUser().getVehicleBrand());
                    tvDriverVehicleModel.setText(driverInfo.getDriverAsUser().getDriverVehicleModel().equals("null") ? getString(R.string.txt_profile4) : driverInfo.getDriverAsUser().getDriverVehicleModel());
                    tvDriverVehicleNo.setText(driverInfo.getDriverAsUser().getVehicleNo().equals("null") ? getString(R.string.txt_profile5) : driverInfo.getDriverAsUser().getVehicleNo());

                    itemDriverStatus.setTitle((driverInfo.getDriverAsUser().getVerificationStatus().equals("null") ? "" : driverInfo.getDriverAsUser().getVerificationStatus()));

                    tvMainBal.setText(driverInfo.getDriverAsUser().getMainBalance());
                    tvUsableBal.setText(driverInfo.getDriverAsUser().getUsableBalance());
                    tvCommission.setText(driverInfo.getDriverAsUser().getCommission());

                    tvDriverTrip.setText(driverInfo.getDriverAsUser().getTotalTrip());
                    tvDriverRating.setText(driverInfo.getDriverAsUser().getDriverRating());
                    tvDriverYears.setText(driverInfo.getDriverAsUser().getDrivingYears());

                    tvPickUpRate.setText(driverInfo.getDriverAsUser().getPickupRate());
                    tvVehicleSpeed.setText(driverInfo.getDriverAsUser().getSpeed());

                    tvDPhone.setText(driverInfo.getDriverAsUser().getDriverPhone().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverPhone());
                    tvDEmail.setText(driverInfo.getDriverAsUser().getDriverEmail().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverEmail());
                    tvDAccountNumber.setText(getString(R.string.txt_profile1) + " " + (driverInfo.getDriverAsUser().getAccountNo().equals("null") ? "" : driverInfo.getDriverAsUser().getAccountNo()) + " )");
                    tvDPromoCode.setText(getString(R.string.txt_profile2) + " " + (driverInfo.getDriverAsUser().getPromoCode().equals("null") ? "" : driverInfo.getDriverAsUser().getPromoCode()) + " )");

                    tvDAddress.setText((driverInfo.getDriverAsUser().getDriverAddress().getHouse().equals("null") ? "" : getString(R.string.txt_profile6) + " " + driverInfo.getDriverAsUser().getDriverAddress().getHouse())
                            + (driverInfo.getDriverAsUser().getDriverAddress().getRoad().equals("null") ? "" : getString(R.string.txt_profile7) + " " + driverInfo.getDriverAsUser().getDriverAddress().getRoad())
                            + (driverInfo.getDriverAsUser().getDriverAddress().getUnit().equals("null") ? "" : getString(R.string.txt_profile8) + " " + driverInfo.getDriverAsUser().getDriverAddress().getUnit())
                            + (driverInfo.getDriverAsUser().getDriverAddress().getFax().equals("null") ? "" : getString(R.string.txt_profile9) + " " + driverInfo.getDriverAsUser().getDriverAddress().getFax())
                            + (driverInfo.getDriverAsUser().getDriverAddress().getZipCode().equals("null") ? "" : getString(R.string.txt_profile10) + " " + driverInfo.getDriverAsUser().getDriverAddress().getZipCode())
                            + (driverInfo.getDriverAsUser().getDriverAddress().getStateProvince().equals("null") ? "" : getString(R.string.txt_profile11) + " " + driverInfo.getDriverAsUser().getDriverAddress().getStateProvince())
                            + (driverInfo.getDriverAsUser().getDriverAddress().getCountry().equals("null") ? "" : getString(R.string.txt_profile12) + " " + driverInfo.getDriverAsUser().getDriverAddress().getCountry()));

                    tvDTotalCommission.setText(driverInfo.getDriverAsUser().getTotalCommission());
                    tvDLostCommission.setText(driverInfo.getDriverAsUser().getLostCommission());
                    tvDDailyTarget.setText(driverInfo.getDriverAsUser().getDailyTarget());
                    tvDWeeklyTarget.setText(driverInfo.getDriverAsUser().getWeeklyTarget());

                    touchPhone = driverInfo.getDriverAsUser().getDriverAddress().getTouchPointPhone();
                    if (touchPhone == null || touchPhone.equalsIgnoreCase("") || touchPhone.equalsIgnoreCase("null")) {
                        findViewById(R.id.iv_phone_call).setVisibility(View.GONE);
                    }
                    tvDriverNearestTouchPoint.setText(touchPhone);

                    touchLat = driverInfo.getDriverAsUser().getDriverAddress().getTouchPointLat();
                    touchLng = driverInfo.getDriverAsUser().getDriverAddress().getTouchPointLng();
                    if (touchLat == 0 && touchLng == 0) {
                        findViewById(R.id.iv_touch_navigation).setVisibility(View.GONE);
                    }

                    Glide.with(getApplicationContext())
                            .load(driverInfo.getDriverAsUser().getProfilePhoto())
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(civDriverProfileImg);

                    Glide.with(getApplicationContext())
                            .load(driverInfo.getDriverAsUser().getVehiclePhoto())
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter()
                                    .error(R.drawable.driver_vehicle)
                                    .fallback(R.drawable.driver_vehicle))
                            .into(driverVehicleImg);

                    Common.stopWaitingDialog(pDialog);
                } else if (driverInfo.getStatus().equalsIgnoreCase("Error")) {
                    Common.stopWaitingDialog(pDialog);
                    Snackbar.make(coordinatorLayoutProfile, driverInfo.getErrorMsg(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
                } else if (driverInfo.getStatus().equalsIgnoreCase("Errors")) {
                    Common.stopWaitingDialog(pDialog);
                    Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                    logOutHere();
                }
            }
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarSlowInternet();
        }
    }

    // body and header json
    private JSONObject getBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
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

    public void onCallTouchPoint(View view) {
        if (touchPhone != null) {
            callToPhone(touchPhone);
        }
    }

    private void callToPhone(String pNo) {
        Common.callToNumber(DriverProfileActivity.this, pNo);
    }

    public void onNavigationWithTouchPoint(View view) {
        String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination=" + touchLat + "+" + touchLng;
        Uri location = Uri.parse(URL);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void snackBarNoInternet() {
        Snackbar.make(coordinatorLayoutProfile, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
    }

    private void snackBarSlowInternet() {
        Snackbar.make(coordinatorLayoutProfile, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
    }

    private void logOutHere() {
        AutoRideDriverApps.logout();
        Intent intent = new Intent(DriverProfileActivity.this, DriverWelcomeActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        DriverProfileActivity.this.finish();
    }
}