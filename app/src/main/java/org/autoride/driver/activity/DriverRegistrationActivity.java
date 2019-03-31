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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

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
import org.autoride.driver.listeners.RegistrationListener;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.utils.AppConstants;
import org.autoride.driver.widgets.OTPEditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DriverRegistrationActivity extends BaseAppCompatNoDrawerActivity {

    private static final String TAG = "DriverRegistration";
    private RegistrationBean registrationBean;
    private ViewFlipper driverRegistrationViewFlipper;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etxtPhone;
    private EditText etxtEmail;
    private EditText etxtPassword;
    private EditText etPromoCode;
    private LinearLayout llVerification;
    private TextView txtVerificationLabel;
    private Spinner spinnerCountryCodes;
    private CountryListBean countryListBean;
    private ImageView ivFlag;
    private OTPEditText etxtOne;
    private OTPEditText etxtTwo;
    private OTPEditText etxtThree;
    private OTPEditText etxtFour;
    private OTPEditText etxtFive;
    private OTPEditText etxtSix;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean isVerificationEnabled;
    private String uRole = "partner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        setUiComponent();
        getUserLastLocation();
    }

    private void setUiComponent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setTitle(R.string.title_activity_driver_registration);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeView.setPadding(0, 0, 0, 0);
        registrationBean = new RegistrationBean();

        driverRegistrationViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper_registration);
        driverRegistrationViewFlipper.setDisplayedChild(0);

        llVerification = (LinearLayout) findViewById(R.id.ll_driver_registration_mobile_otp);
        txtVerificationLabel = (TextView) findViewById(R.id.ctv_driver_registration_mobile_otp_label);

        spinnerCountryCodes = (Spinner) findViewById(R.id.spinner_driver_registration_mobile_country_code);
        countryListBean = AppConstants.getCountryBean();
        Collections.sort(countryListBean.getCountries());
        List<String> countryDialCodes = new ArrayList<>();
        for (CountryBean bean : countryListBean.getCountries()) {
            countryDialCodes.add(bean.getDialCode());
        }

        ArrayAdapter<String> adapterCountryCodes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryDialCodes);
        adapterCountryCodes.setDropDownViewResource(R.layout.item_spinner);
        spinnerCountryCodes.setAdapter(adapterCountryCodes);
        spinnerCountryCodes.setSelection(210);

        ivFlag = (ImageView) findViewById(R.id.iv_driver_registration_mobile_country_flag);

        etFirstName = (EditText) findViewById(R.id.et_driver_registration_first_name);
        etLastName = (EditText) findViewById(R.id.et_driver_registration_last_name);
        etxtPhone = (EditText) findViewById(R.id.et_driver_registration_phone);
        etxtEmail = (EditText) findViewById(R.id.et_driver_registration_email);
        etxtPassword = (EditText) findViewById(R.id.et_driver_registration_password);
        etPromoCode = (EditText) findViewById(R.id.et_driver_registration_promo_code);

        etxtOne = (OTPEditText) findViewById(R.id.oet_driver_registration_mobile_one);
        etxtTwo = (OTPEditText) findViewById(R.id.oet_driver_registration_mobile_two);
        etxtThree = (OTPEditText) findViewById(R.id.oet_driver_registration_mobile_three);
        etxtFour = (OTPEditText) findViewById(R.id.oet_driver_registration_mobile_four);
        etxtFive = (OTPEditText) findViewById(R.id.oet_driver_registration_mobile_five);
        etxtSix = (OTPEditText) findViewById(R.id.oet_driver_registration_mobile_six);

        etxtOne.setTypeface(typeface);
        etxtTwo.setTypeface(typeface);
        etxtThree.setTypeface(typeface);
        etxtFour.setTypeface(typeface);
        etxtFive.setTypeface(typeface);
        etxtSix.setTypeface(typeface);

        etFirstName.setTypeface(typeface);
        etLastName.setTypeface(typeface);
        etxtPhone.setTypeface(typeface);
        etxtEmail.setTypeface(typeface);
        etxtPassword.setTypeface(typeface);
        etxtPassword.setTransformationMethod(new PasswordTransformationMethod());

        mAuth = FirebaseAuth.getInstance();
        setVerificationLayoutVisibility(false);

        Glide.with(getApplicationContext())
                .load("file:///android_asset/" + "flags/"
                        + countryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(ivFlag);

        spinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + countryListBean.getCountries().get(position).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(ivFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + countryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(ivFlag);
            }
        });

        etxtOne.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength1 = etxtOne.getText().length();
                if (textlength1 >= 1) {
                    etxtOne.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtTwo.requestFocus();
                } else {
                    etxtOne.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
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
                    etxtOne.setText("");
                    etxtOne.requestFocus();
                }
            }
        });

        etxtSix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
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
                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
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
                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
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
                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
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
                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
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
                /*Snackbar.make(coordinatorLayout, R.string.message_phone_verification_failed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();*/
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
            txtVerificationLabel.setVisibility(View.VISIBLE);
            etxtOne.requestFocus();
            isVerificationEnabled = true;
        } else {
            llVerification.setVisibility(View.GONE);
            txtVerificationLabel.setVisibility(View.GONE);
            etxtOne.setText("");
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
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (isVerificationEnabled) {
                String otpCode = "" + etxtOne.getText().toString() + etxtTwo.getText().toString()
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
                    performRegistrationMobileAvailabilityCheck(registrationBean.getPhone());
                }
            }
        } else {
            snackBarNoInternet();
        }
    }

    private boolean collectMobileNumber() {
        Log.i(TAG, "collectMobileNumber: " + spinnerCountryCodes.getSelectedItem().toString());
        if (spinnerCountryCodes.getSelectedItem().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_please_select_a_country_dial_code), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        } else if (etxtPhone.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }

        registrationBean.setPhone(spinnerCountryCodes.getSelectedItem().toString() + etxtPhone.getText().toString());
        return true;
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

                    driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
                    driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
                    driverRegistrationViewFlipper.showNext();
                } else {
                    swipeView.setRefreshing(false);
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout, R.string.message_invalid_verification_code, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                }
            }
        });
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
                        initiatePhoneVerification();
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
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectEmailAddress()) {
            driverRegistrationViewFlipper.setInAnimation(slideLeftIn);
            driverRegistrationViewFlipper.setOutAnimation(slideLeftOut);
            driverRegistrationViewFlipper.showNext();
        }
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
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
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
                    Snackbar.make(coordinatorLayout, basicBean.getErrorMsg(), Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
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

                    Intent intent = new Intent(DriverRegistrationActivity.this, DriverMainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    DriverRegistrationActivity.this.finish();
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
            Intent intent = new Intent(DriverRegistrationActivity.this, DriverWelcomeActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverRegistrationActivity.this.finish();
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