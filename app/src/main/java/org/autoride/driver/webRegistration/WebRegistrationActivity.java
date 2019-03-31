package org.autoride.driver.webRegistration;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.autoride.driver.R;
import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.notifications.commons.Common;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WebRegistrationActivity extends AppCompatActivity {

    private WebView webViewRegistration;

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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_registration);

        ProgressDialog pDialog = new ProgressDialog(WebRegistrationActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activiy_bar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setTitle(R.string.title_activity_driver_registration);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WebRegistrationActivity.this, DriverWelcomeActivity.class);
                WebRegistrationActivity.this.startActivity(intent);
                WebRegistrationActivity.this.overridePendingTransition(0, 0);
                WebRegistrationActivity.this.finish();
            }
        });

        Common.startWaitingDialog(this, pDialog);
        if (!AutoRideDriverApps.isNetworkAvailable()) {
            showSnackBar();
            Common.stopWaitingDialog(pDialog);
        } else {
            webViewRegistration = findViewById(R.id.webViewRegistration);
            webViewRegistration.setWebViewClient(new WebViewClient());
            webViewRegistration.getSettings().setJavaScriptEnabled(true);
            webViewRegistration.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            //  webViewRegistration.loadUrl("https://www.autoride.org/extra/registration");
            webViewRegistration.loadUrl("https://www.autoride.org/vehicle/registration");
            Common.stopWaitingDialog(pDialog);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webViewRegistration.canGoBack()) {
                        webViewRegistration.goBack();
                        Intent intent = new Intent(WebRegistrationActivity.this, DriverWelcomeActivity.class);
                        this.startActivity(intent);
                        this.overridePendingTransition(0, 0);
                        WebRegistrationActivity.this.finish();
                    } else {
                        Intent intent = new Intent(WebRegistrationActivity.this, DriverWelcomeActivity.class);
                        this.startActivity(intent);
                        this.overridePendingTransition(0, 0);
                        WebRegistrationActivity.this.finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.mybookinglayout), R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}