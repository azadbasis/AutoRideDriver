package org.autoride.driver.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.driver.driver.net.parsers.DriverInfoParser;
import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.DriverAddress;
import org.autoride.driver.model.DriverAsUser;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedBodyBuilder;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.networks.parsers.ResponseParser;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.utils.AppConstants;
import org.autoride.driver.utils.ImageEncodeReducer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class DriverSettingsActivity extends BaseAppCompatNoDrawerActivity implements DriverApiUrl, AppsConstants {

    private static final String TAG = "DriverSettings";
    private ViewFlipper viewFlipperDriverSettings;
    private TextView tvDFirstName, tvDLastName, tvDEmail, tvDPhone, tvDHouse, tvDRoad,
            tvDUnit, tvDFax, tvDZipCode, tvDCity, tvDCountry;
    private EditText etDFirstName, etDLastName, etDEmail, etDHouse, etDRoad, etDUnit,
            etDFax, etDZipCode, etDCity, etDCountry, etDCurPassword, etDPassword;
    private DriverAsUser driverInfo;
    private DriverAddress driverAddress;
    private ImageView ivDriverProfileImg;
    private CircleImageView civDSettingsProfileImg;
    private static String accessToken, rememberToken, driverId;
    private Button btnSaveFirstName, btnSaveLastName, btnSaveEmail,
            btnSaveAddress, btnUpdatePassword, btnSaveProfileImage;
    private static OkHttpClient client;
    private LinearLayout llPhotoSave, llPhotoTake;
    private int CAMERA_RUNTIME_PERMISSION = 1, WRITE_EXTERNAL_PERMISSION_RUNTIME = 2;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 3, CAMERA_IMAGE_REQUEST_CODE = 4;
    private PhotoCaptureModal photoCaptureModal;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);

        client = new OkHttpClient();
        setUiComponent();
    }

    private void setUiComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        driverInfo = new DriverAsUser();
        driverAddress = new DriverAddress();
        viewFlipperDriverSettings = (ViewFlipper) findViewById(R.id.view_flipper_driver_settings);
        viewFlipperDriverSettings.setDisplayedChild(0);
        civDSettingsProfileImg = (CircleImageView) findViewById(R.id.civ_driver_settings__profile_img);
        tvDFirstName = (TextView) findViewById(R.id.tv_driver_settings_first_name);
        tvDLastName = (TextView) findViewById(R.id.tv_driver_settings_last_name);
        tvDEmail = (TextView) findViewById(R.id.tv_driver_settings_email);
        tvDPhone = (TextView) findViewById(R.id.tv_driver_settings_phone);
        tvDHouse = (TextView) findViewById(R.id.tv_driver_settings_house);
        tvDRoad = (TextView) findViewById(R.id.tv_driver_settings_road);
        tvDUnit = (TextView) findViewById(R.id.tv_driver_settings_unit);
        tvDFax = (TextView) findViewById(R.id.tv_driver_settings_fax);
        tvDZipCode = (TextView) findViewById(R.id.tv_driver_settings_zip_code);
        tvDCity = (TextView) findViewById(R.id.tv_driver_settings_city);
        tvDCountry = (TextView) findViewById(R.id.tv_driver_settings_country);
        ivDriverProfileImg = (ImageView) findViewById(R.id.iv_driver_settings_profile_img);
        CardView cvImage = (CardView) findViewById(R.id.card_view_image);
        etDFirstName = (EditText) findViewById(R.id.et_driver_settings_first_name);
        etDLastName = (EditText) findViewById(R.id.et_driver_settings_last_name);
        etDEmail = (EditText) findViewById(R.id.et_driver_settings_email);
        etDHouse = (EditText) findViewById(R.id.et_driver_settings_house);
        etDRoad = (EditText) findViewById(R.id.et_driver_settings_road);
        etDUnit = (EditText) findViewById(R.id.et_driver_settings_unit);
        etDFax = (EditText) findViewById(R.id.et_driver_settings_fax);
        etDZipCode = (EditText) findViewById(R.id.et_driver_settings_zip_code);
        etDCity = (EditText) findViewById(R.id.et_driver_settings_city);
        etDCountry = (EditText) findViewById(R.id.et_driver_settings_country);
        etDCurPassword = (EditText) findViewById(R.id.et_driver_settings_current_password);
        etDPassword = (EditText) findViewById(R.id.et_driver_settings_new_password);

        llPhotoSave = (LinearLayout) findViewById(R.id.ll_photo_save);
        llPhotoTake = (LinearLayout) findViewById(R.id.ll_photo_take);

        btnSaveFirstName = (Button) findViewById(R.id.btn_save_first_name);
        btnSaveLastName = (Button) findViewById(R.id.btn_save_last_name);
        btnSaveEmail = (Button) findViewById(R.id.btn_save_email);
        btnSaveAddress = (Button) findViewById(R.id.btn_save_address);
        btnUpdatePassword = (Button) findViewById(R.id.btn_update_password);
        btnSaveProfileImage = (Button) findViewById(R.id.btn_save_profile_image);

        btnSaveFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveDriverFirstName();
            }
        });

        btnSaveLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveDriverLastName();
            }
        });

        btnSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveDriverEmail();
            }
        });

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveDriverAddress();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdateDriverPassword();
            }
        });

        btnSaveProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadDriverProfileImage();
            }
        });

        cvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(1);
                getSupportActionBar().setTitle(getString(R.string.title_settings1));
            }
        });

        LinearLayout llFirstName = (LinearLayout) findViewById(R.id.linear_layout_first_name);
        llFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(2);
                etDFirstName.setText(tvDFirstName.getText());
                getSupportActionBar().setTitle(getString(R.string.title_settings7));
            }
        });

        LinearLayout llLastName = (LinearLayout) findViewById(R.id.linear_layout_last_name);
        llLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(3);
                etDLastName.setText(tvDLastName.getText());
                getSupportActionBar().setTitle(getString(R.string.title_settings2));
            }
        });

        LinearLayout llEmail = (LinearLayout) findViewById(R.id.linear_layout_email);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(4);
                etDEmail.setText(tvDEmail.getText());
                getSupportActionBar().setTitle(getString(R.string.title_settings3));
            }
        });

        LinearLayout llAddress = (LinearLayout) findViewById(R.id.linear_layout_address);
        llAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(5);
                etDHouse.setText(tvDHouse.getText());
                etDRoad.setText(tvDRoad.getText());
                etDUnit.setText(tvDUnit.getText());
                etDFax.setText(tvDFax.getText());
                etDZipCode.setText(tvDZipCode.getText());
                etDCity.setText(tvDCity.getText());
                etDCountry.setText(tvDCountry.getText());
                getSupportActionBar().setTitle(getString(R.string.title_settings4));
            }
        });

        LinearLayout llPassword = (LinearLayout) findViewById(R.id.linear_layout_password);
        llPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(6);
                getSupportActionBar().setTitle(getString(R.string.title_settings5));
            }
        });

        LinearLayout llPhone = (LinearLayout) findViewById(R.id.linear_layout_phone);
        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(coordinatorLayout, getString(R.string.title_settings6), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            }
        });

        SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForToken: " + sp.getAll());
            accessToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");

            if (AutoRideDriverApps.isNetworkAvailable()) {
                new GetSetDriverProfile().execute();
            } else {
                snackBarNoInternet();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    // set profile info
    @SuppressLint("StaticFieldLeak")
    private class GetSetDriverProfile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            swipeView.setRefreshing(true);
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(DRIVER_PROFILE_URL, getBodyJSON(null, null)),
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

    private void profileParse(String sResponse) {
        swipeView.setRefreshing(false);
        List<DriverInfo> driverInfoList = new DriverInfoParser().parseDriverInfoRequest(sResponse);
        if (driverInfoList != null) {
            for (DriverInfo driverInfo : driverInfoList) {
                if (driverInfo.getStatus().equalsIgnoreCase("Success")) {

                    tvDFirstName.setText(driverInfo.getDriverAsUser().getFirstName().equals("null") ? "" : driverInfo.getDriverAsUser().getFirstName());
                    tvDLastName.setText(driverInfo.getDriverAsUser().getLastName().equals("null") ? "" : driverInfo.getDriverAsUser().getLastName());
                    tvDEmail.setText(driverInfo.getDriverAsUser().getDriverEmail().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverEmail());
                    tvDPhone.setText(driverInfo.getDriverAsUser().getDriverPhone().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverPhone());

                    tvDHouse.setText(driverInfo.getDriverAsUser().getDriverAddress().getHouse().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getHouse());
                    tvDRoad.setText(driverInfo.getDriverAsUser().getDriverAddress().getRoad().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getRoad());
                    tvDUnit.setText(driverInfo.getDriverAsUser().getDriverAddress().getUnit().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getUnit());
                    tvDFax.setText(driverInfo.getDriverAsUser().getDriverAddress().getFax().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getFax());
                    tvDZipCode.setText(driverInfo.getDriverAsUser().getDriverAddress().getZipCode().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getZipCode());
                    tvDCity.setText(driverInfo.getDriverAsUser().getDriverAddress().getStateProvince().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getStateProvince());
                    tvDCountry.setText(driverInfo.getDriverAsUser().getDriverAddress().getCountry().equals("null") ? "" : driverInfo.getDriverAsUser().getDriverAddress().getCountry());

                    Glide.with(getBaseContext())
                            .load(driverInfo.getDriverAsUser().getProfilePhoto())
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default)
                                    .fitCenter())
                            .into(civDSettingsProfileImg);

                    Glide.with(getBaseContext())
                            .load(driverInfo.getDriverAsUser().getProfilePhoto())
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter())
                            .into(ivDriverProfileImg);

                } else if (driverInfo.getStatus().equalsIgnoreCase("Error")) {
                    Snackbar.make(coordinatorLayout, driverInfo.getErrorMsg(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else if (driverInfo.getStatus().equalsIgnoreCase("Errors")) {
                    Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                    logOutHere();
                } else {
                    Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                    logOutHere();
                }
            }
        } else {
            snackBarSlowInternet();
        }
    }

    public void onProfileTakePhoto(View view) {
        photoCaptureModal = new PhotoCaptureModal();
        photoCaptureModal.show(getSupportFragmentManager(), TAG);
    }

    public void onImageFromGallery(View view) {
        if (ActivityCompat.checkSelfPermission(DriverSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) DriverSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_RUNTIME);
            if (photoCaptureModal != null) {
                photoCaptureModal.dismiss();
            }
        } else {
            if (photoCaptureModal != null) {
                photoCaptureModal.dismiss();
            }
            photoSelectFromGallery();
        }
    }

    public void onImageFromCamera(View view) {
        if (ActivityCompat.checkSelfPermission(DriverSettingsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) DriverSettingsActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_RUNTIME_PERMISSION);
            if (photoCaptureModal != null) {
                photoCaptureModal.dismiss();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(DriverSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) DriverSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_RUNTIME);
                if (photoCaptureModal != null) {
                    photoCaptureModal.dismiss();
                }
            } else {
                if (photoCaptureModal != null) {
                    photoCaptureModal.dismiss();
                }
                photoCaptureFromCamera();
            }
        }
    }

    public void onImageCancel(View view) {
        if (photoCaptureModal != null) {
            photoCaptureModal.dismiss();
        }
    }

    // first name save/update
    private void onSaveDriverFirstName() {
        swipeView.setRefreshing(true);
        btnSaveFirstName.setText(R.string.text_btn_waiting);
        btnSaveFirstName.setEnabled(false);
        if (collectFirstName()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                new SaveFirstName().execute();
            } else {
                swipeView.setRefreshing(false);
                btnSaveFirstName.setText(R.string.btn_save);
                btnSaveFirstName.setEnabled(true);

                snackBarNoInternet();
            }
        }
    }

    private boolean collectFirstName() {
        driverInfo.setFirstName(etDFirstName.getText().toString());
        if (driverInfo.getFirstName() == null || driverInfo.getFirstName().equals("")) {
            swipeView.setRefreshing(false);
            btnSaveFirstName.setText(R.string.btn_save);
            btnSaveFirstName.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.first_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getFirstName().length() < 3) {
            swipeView.setRefreshing(false);
            btnSaveFirstName.setText(R.string.btn_save);
            btnSaveFirstName.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.first_name2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (driverInfo.getFirstName().length() > 14) {
            swipeView.setRefreshing(false);
            btnSaveFirstName.setText(R.string.btn_save);
            btnSaveFirstName.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.first_name3, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveFirstName extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(UPDATE_FIRST_NAME_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("firstName", driverInfo.getFirstName())),
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
            firstNameParser(result);
        }
    }

    private void firstNameParser(String response) {

        swipeView.setRefreshing(false);
        btnSaveFirstName.setText(R.string.btn_save);
        btnSaveFirstName.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {

                tvDFirstName.setText(authBean.getFirstName());

                SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(AppConstants.PREFERENCE_KEY_SESSION_FIRSTNAME, authBean.getFirstName());
                editor.commit();

                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_driver_settings));
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(0);
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    // last name save/update
    private void onSaveDriverLastName() {

        swipeView.setRefreshing(true);
        btnSaveLastName.setText(R.string.text_btn_waiting);
        btnSaveLastName.setEnabled(false);

        if (collectLastName()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                new SaveLastName().execute();
            } else {
                swipeView.setRefreshing(false);
                btnSaveLastName.setText(R.string.btn_save);
                btnSaveLastName.setEnabled(true);

                snackBarNoInternet();
            }
        }
    }

    private boolean collectLastName() {
        driverInfo.setLastName(etDLastName.getText().toString());
        if (driverInfo.getLastName() == null || driverInfo.getLastName().equals("")) {
            swipeView.setRefreshing(false);
            btnSaveLastName.setText(R.string.btn_save);
            btnSaveLastName.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.last_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getLastName().length() < 3) {
            swipeView.setRefreshing(false);
            btnSaveLastName.setText(R.string.btn_save);
            btnSaveLastName.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.last_name2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getLastName().length() > 14) {
            swipeView.setRefreshing(false);
            btnSaveLastName.setText(R.string.btn_save);
            btnSaveLastName.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.last_name3, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveLastName extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(UPDATE_LAST_NAME_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("lastName", driverInfo.getLastName())),
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
            lastNameParser(result);
        }
    }

    private void lastNameParser(String response) {

        swipeView.setRefreshing(false);
        btnSaveLastName.setText(R.string.btn_save);
        btnSaveLastName.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {

                tvDLastName.setText(authBean.getLastName());

                SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(AppConstants.PREFERENCE_KEY_SESSION_LASTNAME, authBean.getLastName());
                editor.commit();

                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_driver_settings));
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(0);
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    // email save/update
    private void onSaveDriverEmail() {

        swipeView.setRefreshing(true);
        btnSaveEmail.setText(R.string.text_btn_waiting);
        btnSaveEmail.setEnabled(false);

        if (collectEmail()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                new SaveEmail().execute();
            } else {
                swipeView.setRefreshing(false);
                btnSaveEmail.setText(R.string.btn_save);
                btnSaveEmail.setEnabled(true);

                snackBarNoInternet();
            }
        }
    }

    private boolean collectEmail() {
        driverInfo.setDriverEmail(etDEmail.getText().toString());
        if (driverInfo.getDriverEmail() == null || driverInfo.getDriverEmail().equals("")) {
            swipeView.setRefreshing(false);
            btnSaveEmail.setText(R.string.btn_save);
            btnSaveEmail.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.message_email_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getDriverEmail().length() < 12) {
            swipeView.setRefreshing(false);
            btnSaveEmail.setText(R.string.btn_save);
            btnSaveEmail.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.email2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getDriverEmail().length() > 30) {
            swipeView.setRefreshing(false);
            btnSaveEmail.setText(R.string.btn_save);
            btnSaveEmail.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.email2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(driverInfo.getDriverEmail()).matches()) {
            swipeView.setRefreshing(false);
            btnSaveEmail.setText(R.string.btn_save);
            btnSaveEmail.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.message_enter_a_valid_email_address, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveEmail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(UPDATE_EMAIL_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("email", driverInfo.getDriverEmail())),
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
            emailParser(result);
        }
    }

    private void emailParser(String response) {

        swipeView.setRefreshing(false);
        btnSaveEmail.setText(R.string.btn_save);
        btnSaveEmail.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {
                tvDEmail.setText(authBean.getEmail());
                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_driver_settings));
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(0);
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    // address save/update
    private void onSaveDriverAddress() {

        swipeView.setRefreshing(true);
        btnSaveAddress.setText(R.string.text_btn_waiting);
        btnSaveAddress.setEnabled(false);

        if (collectAddress()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                new SaveAddress().execute();
            } else {
                swipeView.setRefreshing(false);
                btnSaveAddress.setText(R.string.btn_save);
                btnSaveAddress.setEnabled(true);

                snackBarNoInternet();
            }
        }
    }

    private boolean collectAddress() {

        driverAddress.setHouse(etDHouse.getText().toString());
        driverAddress.setRoad(etDRoad.getText().toString());
        driverAddress.setUnit(etDUnit.getText().toString());
        driverAddress.setFax(etDFax.getText().toString());
        driverAddress.setZipCode(etDZipCode.getText().toString());
        driverAddress.setStateProvince(etDCity.getText().toString());
        driverAddress.setCountry(etDCountry.getText().toString());

        if (driverAddress.getHouse().length() == 0 ^
                driverAddress.getRoad().length() == 0 ^
                driverAddress.getUnit().length() == 0 ^
                driverAddress.getFax().length() == 0 ^
                driverAddress.getZipCode().length() == 0 ^
                driverAddress.getStateProvince().length() == 0 ^
                driverAddress.getCountry().length() == 0) {

            swipeView.setRefreshing(false);
            btnSaveAddress.setText(R.string.btn_save);
            btnSaveAddress.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.address_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveAddress extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(UPDATE_ADDRESS_URL),
                        RequestedBodyBuilder.buildRequestedBody(getAddressBodyJSON()),
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
            addressParser(result);
        }
    }

    private void addressParser(String response) {

        swipeView.setRefreshing(false);
        btnSaveAddress.setText(R.string.btn_save);
        btnSaveAddress.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {

                tvDHouse.setText(driverAddress.getHouse().equals("null") ? "" : driverAddress.getHouse());
                tvDRoad.setText(driverAddress.getRoad().equals("null") ? "" : driverAddress.getRoad());
                tvDUnit.setText(driverAddress.getUnit().equals("null") ? "" : driverAddress.getUnit());
                tvDFax.setText(driverAddress.getFax().equals("null") ? "" : driverAddress.getFax());
                tvDZipCode.setText(driverAddress.getZipCode().equals("null") ? "" : driverAddress.getZipCode());
                tvDCity.setText(driverAddress.getStateProvince().equals("null") ? "" : driverAddress.getStateProvince());
                tvDCountry.setText(driverAddress.getCountry().equals("null") ? "" : driverAddress.getCountry());

                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_driver_settings));
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(0);
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    // password update
    private void onUpdateDriverPassword() {

        swipeView.setRefreshing(true);
        btnUpdatePassword.setText(R.string.text_btn_waiting);
        btnUpdatePassword.setEnabled(false);

        if (collectPassword()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                new UpdatePassword().execute();
            } else {
                swipeView.setRefreshing(false);
                btnUpdatePassword.setText(R.string.change_pass);
                btnUpdatePassword.setEnabled(true);

                snackBarNoInternet();
            }
        }
    }

    private boolean collectPassword() {
        driverInfo.setPassword(etDPassword.getText().toString());
        driverInfo.setCurrentPassword(etDCurPassword.getText().toString());
        if (driverInfo.getCurrentPassword() == null || driverInfo.getCurrentPassword().equals("")) {
            swipeView.setRefreshing(false);
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.current_pass, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getPassword() == null || driverInfo.getPassword().equals("")) {
            swipeView.setRefreshing(false);
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.new_pass, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getPassword().length() < 6) {
            swipeView.setRefreshing(false);
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.new_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getPassword().length() > 20) {
            swipeView.setRefreshing(false);
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Snackbar.make(coordinatorLayout, R.string.new_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdatePassword extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVER_PASS_URL),
                        RequestedBodyBuilder.buildRequestedBody(getPasswordBodyJSON()),
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
            passwordParser(result);
        }
    }

    private void passwordParser(String response) {

        swipeView.setRefreshing(false);
        btnUpdatePassword.setText(R.string.change_pass);
        btnUpdatePassword.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(response);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {
                Toast.makeText(getBaseContext(), authBean.getErrorMsg(), Toast.LENGTH_SHORT).show();
                logOutHere();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    // body and header json
    private JSONObject getBodyJSON(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
            if (key != null && value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getPasswordBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
            postBody.put("currentPassword", driverInfo.getCurrentPassword());
            postBody.put("newPassword", driverInfo.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getAddressBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
            postBody.put("house", driverAddress.getHouse());
            postBody.put("road", driverAddress.getRoad());
            postBody.put("unit", driverAddress.getUnit());
            postBody.put("fax", driverAddress.getFax());
            postBody.put("zipCode", driverAddress.getZipCode());
            postBody.put("stateProvince", driverAddress.getStateProvince());
            postBody.put("country", driverAddress.getCountry());
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

    // image get set
    public void onImageRetake(View view) {
        photoCaptureModal = new PhotoCaptureModal();
        photoCaptureModal.show(getSupportFragmentManager(), TAG);
    }

    private void photoSelectFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_IMAGE_REQUEST_CODE);
    }

    private void photoCaptureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            } catch (IOException ex) {
                Toast.makeText(this, R.string.web_error_msg2, Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "org.autoride.driver.fileprovider",
                        photoFile);

                List<ResolveInfo> resolvedIntentActivities = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST_CODE);
            } else {
                Toast.makeText(this, R.string.web_error_msg2, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mCurrentPhotoPath = ImageEncodeReducer.getRealPathFromURI(data.getData(), DriverSettingsActivity.this);
            showSelectedImage();
        } else if (requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {  // never check data null here
            showSelectedImage();
        }
    }

    private void showSelectedImage() {
        ivDriverProfileImg.setImageBitmap(ImageEncodeReducer.getBitmapImage(mCurrentPhotoPath));
        llPhotoTake.setVisibility(View.GONE);
        llPhotoSave.setVisibility(View.VISIBLE);
        viewFlipperDriverSettings.setInAnimation(slideRightIn);
        viewFlipperDriverSettings.setOutAnimation(slideRightOut);
        viewFlipperDriverSettings.setDisplayedChild(1);
    }

    // profile image upload
    private void onUploadDriverProfileImage() {

        swipeView.setRefreshing(true);
        btnSaveProfileImage.setText(R.string.text_btn_waiting);
        btnSaveProfileImage.setEnabled(false);

        if (AutoRideDriverApps.isNetworkAvailable()) {
            new DriverImageUploader().execute();
        } else {
            swipeView.setRefreshing(false);
            btnSaveProfileImage.setText(R.string.btn_save);
            btnSaveProfileImage.setEnabled(true);

            snackBarNoInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DriverImageUploader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVER_PROFILE_IMG_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("profilePic", ImageEncodeReducer.compressImage(mCurrentPhotoPath))),
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
            profileImageParser(result);
        }
    }

    private void profileImageParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnSaveProfileImage.setText(R.string.btn_save);
        btnSaveProfileImage.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase("Success")) {
                String url = authBean.getProfilePhoto();
                SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(AppConstants.PREFERENCE_KEY_SESSION_DIMG, url);
                editor.commit();

                Glide.with(getBaseContext())
                        .load(url)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .fitCenter())
                        .into(civDSettingsProfileImg);

                Glide.with(getBaseContext())
                        .load(url)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .fitCenter())
                        .into(ivDriverProfileImg);

                llPhotoTake.setVisibility(View.VISIBLE);
                llPhotoSave.setVisibility(View.GONE);
                mCurrentPhotoPath = null;

                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_driver_settings));
                viewFlipperDriverSettings.setInAnimation(slideRightIn);
                viewFlipperDriverSettings.setOutAnimation(slideRightOut);
                viewFlipperDriverSettings.setDisplayedChild(0);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
                Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                onGoBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onGoBack();
    }

    private void onGoBack() {
        int index = viewFlipperDriverSettings.getDisplayedChild();
        if (index > 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_driver_settings));
            viewFlipperDriverSettings.setInAnimation(slideRightIn);
            viewFlipperDriverSettings.setOutAnimation(slideRightOut);
            viewFlipperDriverSettings.setDisplayedChild(0);
        } else {
            Intent intent = new Intent(DriverSettingsActivity.this, DriverMainActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverSettingsActivity.this.finish();
        }
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
        Intent intent = new Intent(DriverSettingsActivity.this, DriverWelcomeActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        DriverSettingsActivity.this.finish();
    }

    public static class PhotoCaptureModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_photo_dialogue, null);
            dialog.setContentView(view);
        }
    }
}