package org.autoride.driver.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

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
import org.autoride.driver.listeners.ForgotPasswordListener;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.utils.AppConstants;
import org.autoride.driver.widgets.OTPEditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DriverForgetActivity extends BaseAppCompatNoDrawerActivity {

    private static final String TAG = "DriverForget";
    private RegistrationBean registrationBean;
    private ViewFlipper driverForgetViewFlipper;
    private LinearLayout llVerification;
    private TextView verificationLabel;
    private Spinner driverSpinnerCountryCodes;
    private CountryListBean driverCountryListBean;
    private ImageView driverIVFlag;
    private EditText driverForgotPhone;
    private EditText driverNewPassword;
    private OTPEditText oetOne;
    private OTPEditText etxtTwo;
    private OTPEditText etxtThree;
    private OTPEditText etxtFour;
    private OTPEditText etxtFive;
    private OTPEditText etxtSix;
    private FirebaseAuth mAuth;
    private boolean isVerificationEnabled;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_forgot_password);

        setUiComponent();
    }

    private void setUiComponent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setTitle(R.string.title_activity_driver_forgot);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeView.setPadding(0, 0, 0, 0);
        registrationBean = new RegistrationBean();

        driverIVFlag = (ImageView) findViewById(R.id.iv_driver_forgot_pass_mobile_country_flag);
        driverForgotPhone = (EditText) findViewById(R.id.et_driver_forgot_password_phone);
        driverNewPassword = (EditText) findViewById(R.id.et_driver_new_password);

        oetOne = (OTPEditText) findViewById(R.id.oet_driver_forgot_mobile_one);
        etxtTwo = (OTPEditText) findViewById(R.id.oet_driver_forgot_mobile_two);
        etxtThree = (OTPEditText) findViewById(R.id.oet_driver_forgot_mobile_three);
        etxtFour = (OTPEditText) findViewById(R.id.oet_driver_forgot_mobile_four);
        etxtFive = (OTPEditText) findViewById(R.id.oet_driver_forgot_mobile_five);
        etxtSix = (OTPEditText) findViewById(R.id.oet_driver_forgot_mobile_six);

        llVerification = (LinearLayout) findViewById(R.id.ll_driver_forgot_mobile_otp);
        verificationLabel = (TextView) findViewById(R.id.ctv_driver_forgot_mobile_otp_label);
        driverSpinnerCountryCodes = (Spinner) findViewById(R.id.spinner_driver_forgot_password_mobile_country_code);
        driverForgetViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper_driver_forgot_password);
        driverForgetViewFlipper.setDisplayedChild(0);

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

        oetOne.setTypeface(typeface);
        etxtTwo.setTypeface(typeface);
        etxtThree.setTypeface(typeface);
        etxtFour.setTypeface(typeface);
        etxtFive.setTypeface(typeface);
        etxtSix.setTypeface(typeface);

        driverForgotPhone.setTypeface(typeface);
        driverNewPassword.setTypeface(typeface);
        driverNewPassword.setTransformationMethod(new PasswordTransformationMethod());

        mAuth = FirebaseAuth.getInstance();
        setVerificationLayoutVisibility(false);

        Glide.with(getApplicationContext())
                .load("file:///android_asset/" + "flags/"
                        + driverCountryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(driverIVFlag);

        driverSpinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + driverCountryListBean.getCountries().get(position).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(driverIVFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + driverCountryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(driverIVFlag);
            }
        });

        oetOne.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength1 = oetOne.getText().length();
                if (textlength1 >= 1) {
                    oetOne.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtTwo.requestFocus();
                } else {
                    oetOne.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtTwo.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength2 = etxtTwo.getText().length();
                if (textlength2 >= 1) {
                    etxtTwo.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtThree.requestFocus();
                } else {
                    etxtTwo.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtThree.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength3 = etxtThree.getText().length();
                if (textlength3 >= 1) {
                    etxtThree.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtFour.requestFocus();
                } else {
                    etxtThree.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = etxtFour.getText().toString().length();
                if (textlength4 == 1) {
                    etxtFour.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtFive.requestFocus();
                } else {
                    etxtFour.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = etxtFive.getText().toString().length();
                if (textlength4 == 1) {
                    etxtFive.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtSix.requestFocus();
                } else {
                    etxtFive.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = etxtSix.getText().toString().length();
                if (textlength4 == 1) {
                    etxtSix.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                } else {
                    etxtSix.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtSix.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = etxtSix.getText().toString().length();
                if (i == 0) {
                    etxtFive.setText("");
                    etxtFive.requestFocus();
                }
            }
        });

        etxtFive.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = etxtFive.getText().toString().length();
                if (i == 0) {
                    etxtFour.setText("");
                    etxtFour.requestFocus();
                }
            }
        });

        etxtFour.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = etxtFour.getText().toString().length();
                if (i == 0) {
                    etxtThree.setText("");
                    etxtThree.requestFocus();
                }
            }
        });

        etxtThree.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = etxtThree.getText().toString().length();
                if (i == 0) {
                    etxtTwo.setText("");
                    etxtTwo.requestFocus();
                }
            }
        });

        etxtTwo.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = etxtTwo.getText().toString().length();
                if (i == 0) {
                    oetOne.setText("");
                    oetOne.requestFocus();
                }
            }
        });

        etxtSix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    } else if (etxtThree.getText().toString().length() == 0) {
                        etxtThree.requestFocus();
                    } else if (etxtFour.getText().toString().length() == 0) {
                        etxtFour.requestFocus();
                    } else if (etxtFour.getText().toString().length() == 0) {
                        etxtFive.requestFocus();
                    }
                }
            }
        });

        etxtFive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    } else if (etxtThree.getText().toString().length() == 0) {
                        etxtThree.requestFocus();
                    } else if (etxtFour.getText().toString().length() == 0) {
                        etxtFour.requestFocus();
                    }
                }
            }
        });

        etxtFour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    } else if (etxtThree.getText().toString().length() == 0) {
                        etxtThree.requestFocus();
                    }
                }
            }
        });

        etxtThree.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    }
                }
            }
        });

        etxtTwo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.i(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.i(TAG, "onVerificationFailed: " + e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.i(TAG, "onVerificationFailed: " + e);
                }
                /* Snackbar.make(coordinatorLayout, R.string.message_phone_verification_failed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show(); */
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.i(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Snackbar.make(coordinatorLayout, getString(R.string.message_verification_code_sent_to) + " " + registrationBean.getPhone(),
                        Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                setVerificationLayoutVisibility(true);
                swipeView.setRefreshing(false);
            }
        };
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    private void setVerificationLayoutVisibility(boolean isVisible) {
        if (isVisible) {
            llVerification.setVisibility(View.VISIBLE);
            verificationLabel.setVisibility(View.VISIBLE);
            oetOne.requestFocus();
            isVerificationEnabled = true;
        } else {
            llVerification.setVisibility(View.GONE);
            verificationLabel.setVisibility(View.GONE);
            oetOne.setText("");
            etxtTwo.setText("");
            etxtThree.setText("");
            etxtFour.setText("");
            etxtFive.setText("");
            etxtSix.setText("");
            isVerificationEnabled = false;
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

    public void onDriverForgotPasswordPhone(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (isVerificationEnabled) {
                String otpCode = "" + oetOne.getText().toString() + etxtTwo.getText().toString()
                        + etxtThree.getText().toString() + etxtFour.getText().toString()
                        + etxtFive.getText().toString() + etxtSix.getText().toString();
                if (!otpCode.equalsIgnoreCase("")) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Snackbar.make(coordinatorLayout, getString(R.string.message_invalid_verification_code), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
                }
            } else {
                if (collectMobileNumber()) {
                    performMobileAvailabilityCheck(registrationBean.getPhone());
                }
            }
        } else {
            snackBarNoInternet();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        swipeView.setRefreshing(true);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);
                    swipeView.setRefreshing(false);
                    Log.i(TAG, "signInWithCredential:success");
                    Log.i(TAG, "onComplete: " + new Gson().toJson(task));

                    driverForgetViewFlipper.setInAnimation(slideLeftIn);
                    driverForgetViewFlipper.setOutAnimation(slideLeftOut);
                    driverForgetViewFlipper.showNext();
                } else {

                    swipeView.setRefreshing(false);
                    Log.i(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout, R.string.message_invalid_verification_code, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                }
            }
        });
    }

    private boolean collectMobileNumber() {
        Log.i(TAG, "collectMobileNumber: Spinner Value : " + driverSpinnerCountryCodes.getSelectedItem().toString());
        if (driverSpinnerCountryCodes.getSelectedItem().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_please_select_a_country_dial_code), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        } else if (driverForgotPhone.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        registrationBean.setPhone(driverSpinnerCountryCodes.getSelectedItem().toString() + driverForgotPhone.getText().toString());
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
                        initiatePhoneVerification();
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

    private void initiatePhoneVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                registrationBean.getPhone(),        // Phone number to verify
                2,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

        Log.i(TAG, "code_phone " + registrationBean.getPhone());

        Snackbar.make(coordinatorLayout, R.string.message_sending_verification_code, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
        swipeView.setRefreshing(true);
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

    public void onDriverForgotPassword(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectPassword()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                performResetPassword();
            } else {
                snackBarNoInternet();
            }
        }
    }

    private boolean collectPassword() {
        registrationBean.setNewPassword(driverNewPassword.getText().toString());
        if (registrationBean.getNewPassword() == null || registrationBean.getNewPassword().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.message_password_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getNewPassword().length() < 6) {
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (registrationBean.getNewPassword().length() > 20) {
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performResetPassword() {
        swipeView.setRefreshing(true);
        JSONObject postData = getForgetPasJSObj();
        DataManager.performForgotPassword(postData, new ForgotPasswordListener() {
            @Override
            public void onLoadCompleted(AuthBean authBean) {
                swipeView.setRefreshing(false);
                if (authBean != null) {
                    Toast.makeText(getApplicationContext(), authBean.getErrorMsg(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DriverForgetActivity.this, DriverLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    DriverForgetActivity.this.finish();
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

    private JSONObject getForgetPasJSObj() {
        JSONObject postData = new JSONObject();
        try {
            Log.i(TAG, "getForgetPasJSObj: Mobile" + registrationBean.getPhone());

            postData.put("phone", registrationBean.getPhone());
            postData.put("password", registrationBean.getNewPassword());

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        int index = driverForgetViewFlipper.getDisplayedChild();
        if (index == 1) {
            driverForgetViewFlipper.setInAnimation(slideRightIn);
            driverForgetViewFlipper.setOutAnimation(slideRightOut);
            driverForgetViewFlipper.showPrevious();
            setVerificationLayoutVisibility(false);
            swipeView.setPadding(0, 0, 0, 0);
        } else {
            Intent intent = new Intent(DriverForgetActivity.this, DriverLoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverForgetActivity.this.finish();
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