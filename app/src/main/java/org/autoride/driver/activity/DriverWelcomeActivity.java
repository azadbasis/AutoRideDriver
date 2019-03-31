package org.autoride.driver.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.facebookRegistration.FacebookAuthActivity;
import org.autoride.driver.R;
import org.autoride.driver.app.AutoRideDriverApps;

import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.webRegistration.WebRegistrationActivity;

public class DriverWelcomeActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private String phoneNumberString;
    private View.OnClickListener snackBarDismissListener;
    private LinearLayout llWelcomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity_welcome);

        mCallbackManager = CallbackManager.Factory.create();
        llWelcomeActivity = (LinearLayout) findViewById(R.id.ll_welcome_activity);

        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.setVisibility(View.GONE);
            }
        };
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AutoRideDriverApps.logout();
    }

    public void onWelcomeDriverLogin(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (AutoRideDriverApps.isLocationEnabled()) {
                Intent intent = new Intent(DriverWelcomeActivity.this, DriverLoginActivity.class);
                this.startActivity(intent);
                this.overridePendingTransition(0, 0);
                DriverWelcomeActivity.this.finish();
            } else {
                snackBarNoGps();
            }
        } else {
            snackBarNoInternet();
        }
    }

    public void onWelcomeDriverRegistration(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (AutoRideDriverApps.isLocationEnabled()) {
                Intent intent = new Intent(DriverWelcomeActivity.this, DriverRegistrationActivity.class);
                this.startActivity(intent);
                this.overridePendingTransition(0, 0);
                DriverWelcomeActivity.this.finish();
            } else {
                snackBarNoGps();
            }
        } else {
            snackBarNoInternet();
        }
    }

    public void facebookAuthentication(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            if (AutoRideDriverApps.isLocationEnabled()) {
                // Handle Error
                final Intent intent = new Intent(DriverWelcomeActivity.this, AccountKitActivity.class);

                AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                        new AccountKitConfiguration.AccountKitConfigurationBuilder(
                                LoginType.PHONE,
                                AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
                // ... perform additional configuration ...
                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
                startActivityForResult(intent, 101);
            } else {
                snackBarNoGps();
            }
        } else {
            snackBarNoInternet();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();

                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //goToMyLoggedInActivity();

                // Surface the result to your user in an appropriate way.
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        // Get Account Kit ID
                        String accountKitId = account.getId();
                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        if (phoneNumber != null) {
                            phoneNumberString = phoneNumber.toString();
                            Intent intent1 = new Intent(DriverWelcomeActivity.this, FacebookAuthActivity.class);
                            intent1.putExtra("DRIVER-PHONE-NUMBER", phoneNumberString);
                            startActivity(intent1);
                            finish();
                            // Operation.saveString("RIDER-PHONE-NUMBER", phoneNumberString);
                            //  Toast.makeText(RiderWelcomeActivity.this, "phoneNumberString" + phoneNumberString, Toast.LENGTH_SHORT).show();
                        }

                        // Get email
                        String email = account.getEmail();
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        // Handle Error
                    }
                });
            }
        }
    }

    public void webAuthentication(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            Intent intent = new Intent(DriverWelcomeActivity.this, WebRegistrationActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverWelcomeActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    private void snackBarNoInternet() {
        Snackbar snackbar = Snackbar.make(llWelcomeActivity, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void snackBarNoGps() {
        Snackbar snackbar = Snackbar.make(llWelcomeActivity, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}