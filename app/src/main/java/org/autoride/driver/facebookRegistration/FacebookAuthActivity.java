package org.autoride.driver.facebookRegistration;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.driver.driver.net.DataManager;
import org.autoride.driver.listeners.BasicListener;
import org.autoride.driver.listeners.RegistrationListener;
import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.BasicBean;
import org.autoride.driver.model.RegistrationBean;
import org.autoride.driver.notifications.commons.Common;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FacebookAuthActivity extends BaseAppCompatNoDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        if (getIntent() != null) {
            driverPhoneNumber = getIntent().getStringExtra("DRIVER-PHONE-NUMBER");
        }

        registrationBean = new RegistrationBean();
        registrationBean.setPhone(driverPhoneNumber);
        setUiComponent();
        getUserLastLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
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

    public void onDriverRegistrationPhone(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (collectMobileNumber()) {

            }
            swipeView.setRefreshing(true);
            if (driverPhoneNumber != null) {
                swipeView.setRefreshing(false);
                driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
                driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
                driverRegistrationViewFlipper.showNext();
                swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);
            } else {
                swipeView.setRefreshing(false);
            }
        } else {
            snackBarNoInternet();
        }
    }

    private boolean collectMobileNumber() {
        if (driverPhoneNumber.toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_verified_successfully), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        registrationBean.setPhone(driverPhoneNumber);
        return true;
    }

    public void performRegistrationMobileAvailabilityCheck(final String phone) {
        swipeView.setRefreshing(true);
        JSONObject postData = getPhoneNumberAvailabilityJSObj(phone);
        DataManager.performRegistrationMobileAvailabilityCheck(postData, new BasicListener() {

            @Override
            public void onLoadCompleted(BasicBean basicBean) {
                swipeView.setRefreshing(false);
                if (basicBean != null) {
                    if (basicBean.isPhoneAvailable()) {
                        if (collectEmailAddress()) {
                            driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
                            driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
                            driverRegistrationViewFlipper.showNext();
                        }
                    } else {
                        Snackbar.make(coordinatorLayout, phone + " " + getString(R.string.message_is_already_registered), Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(BasicBean basicBean) {
                swipeView.setRefreshing(false);
                if (basicBean != null) {
                    Snackbar.make(coordinatorLayout, basicBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
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

    public void onDriverRegistrationEmail(View view) {
        performRegistrationMobileAvailabilityCheck(registrationBean.getPhone());
     /*
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectEmailAddress()) {
            driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
            driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
            driverRegistrationViewFlipper.showNext();
        }*/
    }

    private boolean collectEmailAddress() {
        registrationBean.setEmail(etxtEmail.getText().toString());
        if (registrationBean.getEmail() == null || registrationBean.getEmail().equals("")) {
            registrationBean.setEmail("");
            return true;
        } else if (registrationBean.getEmail().length() < 12) {
            Snackbar.make(coordinatorLayout, R.string.email2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getEmail().length() > 30) {
            Snackbar.make(coordinatorLayout, R.string.email2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(registrationBean.getEmail()).matches()) {
            Snackbar.make(coordinatorLayout, R.string.message_enter_a_valid_email_address, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    public void onDriverRegistrationName(View view) {
        if (collectName()) {
            driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
            driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
            driverRegistrationViewFlipper.showNext();
        }
    }

    private boolean collectName() {

        registrationBean.setFirstName(etFirstName.getText().toString());
        registrationBean.setLastName(etLastName.getText().toString());

        if (registrationBean.getFirstName() == null || registrationBean.getFirstName().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.first_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getFirstName().length() < 3) {
            Snackbar.make(coordinatorLayout, R.string.first_name2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getFirstName().length() > 14) {
            Snackbar.make(coordinatorLayout, R.string.first_name3, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }

        if (registrationBean.getLastName() == null || registrationBean.getLastName().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.last_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getLastName().length() < 3) {
            Snackbar.make(coordinatorLayout, R.string.last_name2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getLastName().length() > 14) {
            Snackbar.make(coordinatorLayout, R.string.last_name3, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    public void onDriverRegistrationPromoCode(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectPromoCode()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                performPromoCodeAvailabilityCheck(registrationBean.getPromoCode());
            } else {
                snackBarNoInternet();
            }
        }
    }

    private boolean collectPromoCode() {
        registrationBean.setPromoCode(etPromoCode.getText().toString());
        if (registrationBean.getPromoCode() == null || registrationBean.getPromoCode().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.promo_code_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    public void performPromoCodeAvailabilityCheck(String promoCode) {
        swipeView.setRefreshing(true);
        JSONObject postData = getPromoCodeAvailabilityJSObj(promoCode);
        DataManager.performPromoCodeAvailabilityCheck(postData, new BasicListener() {
            @Override
            public void onLoadCompleted(BasicBean basicBean) {
                swipeView.setRefreshing(false);
                if (basicBean != null) {
                    if (basicBean.isPhoneAvailable()) {
                        driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
                        driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
                        driverRegistrationViewFlipper.showNext();
                    } else {
                        Snackbar.make(coordinatorLayout, basicBean.getErrorMsg(),
                                Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(BasicBean basicBean) {
                swipeView.setRefreshing(false);
                if (basicBean != null) {
                    Snackbar.make(coordinatorLayout, basicBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    private JSONObject getPromoCodeAvailabilityJSObj(String pCode) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("role", uRole);
            postData.put("promoCode", pCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void onDriverRegistrationPassword(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectPassword()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                if (AutoRideDriverApps.isLocationEnabled()) {
                    registrationBean.setRegistrationLat(String.valueOf(lastLatitude));
                    registrationBean.setRegistrationLng(String.valueOf(lastLongitude));
                    performRegistration();
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
        registrationBean.setPassword(etxtPassword.getText().toString());
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

    private void performRegistration() {
        swipeView.setRefreshing(true);
        JSONObject postData = getRegistrationJSObj();
        DataManager.performRegistration(postData, new RegistrationListener() {
            @Override
            public void onLoadCompleted(AuthBean authBean) {
                swipeView.setRefreshing(false);
                if (authBean != null) {
                    AutoRideDriverApps.saveToken(authBean);
                    startActivity(new Intent(getApplicationContext(), DriverMainActivity.class));
                    finish();
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(AuthBean authBean) {
                swipeView.setRefreshing(false);
                if (authBean != null) {
                    Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }

            }
        });
    }

    private JSONObject getRegistrationJSObj() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", registrationBean.getPhone());
            postData.put("firstName", registrationBean.getFirstName());
            postData.put("lastName", registrationBean.getLastName());
            postData.put("role", uRole);
            postData.put("promoCode", registrationBean.getPromoCode());
            postData.put("password", registrationBean.getPassword());
            postData.put("email", registrationBean.getEmail());
            postData.put("lat", registrationBean.getRegistrationLat());
            postData.put("lng", registrationBean.getRegistrationLng());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "postData " + postData);
        return postData;
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
        int index = driverRegistrationViewFlipper.getDisplayedChild();
        if (index > 1) {
            driverRegistrationViewFlipper.setInAnimation(slideRightIn);
            driverRegistrationViewFlipper.setOutAnimation(slideRightOut);
            driverRegistrationViewFlipper.showPrevious();
        } else if (index == 1) {
            driverRegistrationViewFlipper.setInAnimation(slideRightIn);
            driverRegistrationViewFlipper.setOutAnimation(slideRightOut);
            driverRegistrationViewFlipper.showPrevious();
            setVerificationLayoutVisibility(false);
            swipeView.setPadding(0, 0, 0, 0);
        } else {
            Intent intent = new Intent(FacebookAuthActivity.this, DriverWelcomeActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            FacebookAuthActivity.this.finish();
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

    private static final String TAG = "DriverRegistration";
    private RegistrationBean registrationBean;
    private ViewFlipper driverRegistrationViewFlipper;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etxtEmail;
    private EditText etxtPassword;
    private EditText etPromoCode;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean isVerificationEnabled;
    private String uRole = "partner";
    private String driverPhoneNumber;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
        CalligraphyContextWrapper.wrap(base);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Xerox Serif Wide.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void setUiComponent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setTitle(R.string.title_activity_driver_registration);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);
        driverRegistrationViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_registration);
        driverRegistrationViewFlipper.setDisplayedChild(0);

        etFirstName = (EditText) findViewById(R.id.et_driver_registration_first_name);
        etLastName = (EditText) findViewById(R.id.et_driver_registration_last_name);
        etxtEmail = (EditText) findViewById(R.id.et_driver_registration_email);
        etxtPassword = (EditText) findViewById(R.id.et_driver_registration_password);
        etPromoCode = (EditText) findViewById(R.id.et_driver_registration_promo_code);

        etFirstName.setTypeface(typeface);
        etLastName.setTypeface(typeface);
        etxtEmail.setTypeface(typeface);
        etxtPassword.setTypeface(typeface);
        etxtPassword.setTransformationMethod(new PasswordTransformationMethod());

        mAuth = FirebaseAuth.getInstance();
        setVerificationLayoutVisibility(false);
    }

    private void setVerificationLayoutVisibility(boolean isVisible) {
        if (isVisible) {
            isVerificationEnabled = true;
        } else {
            isVerificationEnabled = false;
        }
    }
}