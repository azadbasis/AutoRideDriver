package org.autoride.driver.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.BasicBean;
import org.autoride.driver.model.CountryBean;
import org.autoride.driver.model.CountryListBean;
import org.autoride.driver.model.RegistrationBean;
import org.autoride.driver.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.driver.driver.net.DataManager;
import org.autoride.driver.listeners.BasicListener;
import org.autoride.driver.listeners.LoginListener;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverLoginActivity extends BaseAppCompatNoDrawerActivity {

    private static final String TAG = "DriverLogin";
    private RegistrationBean registrationBean;
    private ViewFlipper driverLoginViewFlipper;
    private Spinner driverSpinnerCountryCodes;
    private CountryListBean driverCountryListBean;

    private ImageView driveLoginIVFlag;
    private EditText driverPhone;
    private EditText driverPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        setUiComponent();
        getUserLastLocation();
    }

    private void setUiComponent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setTitle(R.string.title_activity_driver_login);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeView.setPadding(0, 0, 0, 0);
        registrationBean = new RegistrationBean();

        driverPhone = (EditText) findViewById(R.id.et_driver_login_phone);
        driverPassword = (EditText) findViewById(R.id.et_driver_login_password);
        driverPassword.setTypeface(typeface);
        driveLoginIVFlag = (ImageView) findViewById(R.id.iv_driver_login_mobile_country_flag);
        driverSpinnerCountryCodes = (Spinner) findViewById(R.id.spinner_driver_login_mobile_country_code);
        driverLoginViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper_driver_login);
        driverLoginViewFlipper.setDisplayedChild(0);

        driverCountryListBean = AppConstants.getCountryBean();
        Collections.sort(driverCountryListBean.getCountries());
        List<String> countryDialCodes = new ArrayList<>();
        for (CountryBean bean : driverCountryListBean.getCountries()) {
            countryDialCodes.add(bean.getDialCode());
        }
        ArrayAdapter<String> driverAdapterCountryCodes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryDialCodes);
        driverAdapterCountryCodes.setDropDownViewResource(R.layout.item_spinner);
        driverSpinnerCountryCodes.setAdapter(driverAdapterCountryCodes);
        driverSpinnerCountryCodes.setSelection(210);

        Glide.with(getApplicationContext())
                .load("file:///android_asset/" + "flags/"
                        + driverCountryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(driveLoginIVFlag);

        driverSpinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + driverCountryListBean.getCountries().get(position).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(driveLoginIVFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + driverCountryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(driveLoginIVFlag);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    public void onDriverLoginPhone(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectMobileNumber()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                performMobileAvailabilityCheck(registrationBean.getPhone());
            } else {
                snackBarNoInternet();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                onHomeClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean collectMobileNumber() {
        Log.i(TAG, "collectMobileNumber: Spinner Value : " + driverSpinnerCountryCodes.getSelectedItem().toString());
        if (driverSpinnerCountryCodes.getSelectedItem().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_please_select_a_country_dial_code), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        } else if (driverPhone.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        registrationBean.setPhone(driverSpinnerCountryCodes.getSelectedItem().toString() + driverPhone.getText().toString());
        return true;
    }

    public void performMobileAvailabilityCheck(final String phone) {
        swipeView.setRefreshing(true);
        JSONObject postData = getPhoneNumberAvailabilityJSObj(phone);
        DataManager.performMobileAvailabilityCheck(postData, new BasicListener() {
            @Override
            public void onLoadCompleted(BasicBean basicBean) {
                swipeView.setRefreshing(false);
                if (basicBean != null) {
                    if (basicBean.isPhoneAvailable()) {
                        Snackbar.make(coordinatorLayout, getString(R.string.message_valid_login_phone),
                                Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    } else {
                        driverLoginViewFlipper.setInAnimation(slideLeftIn);
                        driverLoginViewFlipper.setOutAnimation(slideLeftOut);
                        driverLoginViewFlipper.showNext();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(BasicBean basicBean) {
                swipeView.setRefreshing(false);
                if (basicBean != null) {
                    Snackbar.make(coordinatorLayout, basicBean.getErrorMsg(), Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    private JSONObject getPhoneNumberAvailabilityJSObj(String phone) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void onDriverLoginPassword(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectPassword()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                if (AutoRideDriverApps.isLocationEnabled()) {
                    performLogin(registrationBean.getPhone(), registrationBean.getPassword());
                } else {
                    Snackbar.make(coordinatorLayout, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
                }
            } else {
                snackBarNoInternet();
            }
        }
    }

    private boolean collectPassword() {
        registrationBean.setPassword(driverPassword.getText().toString());
        if (registrationBean.getPassword() == null || registrationBean.getPassword().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.message_password_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getPassword().length() < 6) {
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getPassword().length() > 20) {
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performLogin(String dPhone, String dPassword) {
        swipeView.setRefreshing(true);
        JSONObject postData = getLoginJSObj(dPhone, dPassword);
        DataManager.performUserLogin(postData, new LoginListener() {
            @Override
            public void onLoadCompleted(AuthBean authBean) {
                swipeView.setRefreshing(false);
                if (authBean != null) {

                    authBean.setPhoneVerified(true);
                    AutoRideDriverApps.saveToken(authBean);

                    Intent intent = new Intent(DriverLoginActivity.this, DriverMainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    DriverLoginActivity.this.finish();
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(AuthBean authBean) {
                swipeView.setRefreshing(false);
                if (authBean != null) {
                    Snackbar.make(coordinatorLayout, "" + authBean.getErrorMsg(), Snackbar.LENGTH_LONG).setAction("Dismiss", snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    private JSONObject getLoginJSObj(String phone, String password) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", phone);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void onDriverForgotPasswordClicked(View view) {
        Intent intent = new Intent(DriverLoginActivity.this, DriverForgetActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        DriverLoginActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onHomeClick();
            return true;
        }
        return false;
    }

    private void onHomeClick() {
        int index = driverLoginViewFlipper.getDisplayedChild();
        if (index > 0) {
            driverLoginViewFlipper.setInAnimation(slideRightIn);
            driverLoginViewFlipper.setOutAnimation(slideRightOut);
            driverLoginViewFlipper.showPrevious();
        } else {
            Intent intent = new Intent(DriverLoginActivity.this, DriverWelcomeActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverLoginActivity.this.finish();
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
}