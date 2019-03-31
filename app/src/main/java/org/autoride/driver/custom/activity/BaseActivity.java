package org.autoride.driver.custom.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.autoride.driver.R;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.listeners.PermissionListener;
import org.autoride.driver.message.PopupMessage;
import org.autoride.driver.utils.FileOp;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final int REQUEST_ENABLE_BT = 0;
    protected static final int REQUEST_PERMISSIONS = 1;
    protected static final int REQUEST_PERMISSIONS_LOCATION = 2;
    protected static final int REQUEST_PERMISSIONS_READ_CONTACTS = 3;
    protected static final int REQUEST_PERMISSIONS_READ_WRITE = 4;
    protected static final int REQUEST_PERMISSIONS_GET_ACCOUNTS = 5;
    protected static final int REQUEST_PERMISSIONS_READ_PHONE_STATE = 6;
    protected static final int REQUEST_PERMISSIONS_CALL = 7;
    private static final String TAG = "BaseA";
    protected FileOp fop;

    protected boolean isBluetoothEnableRequestShown;
    protected boolean isLocationServiceEnableRequestShown;

    protected View.OnClickListener snackBarDismissOnClickListener;
    protected Resources r;
    protected static float px;
    protected int width;
    protected int height;
    protected Animation disappear;
    protected Animation slideLeftIn;
    protected Animation slideLeftOut;
    protected Animation slideUpIn;
    protected Animation slideDownOut;
    protected Animation slideDownIn;
    protected Animation slideUpOut;
    protected Animation fadeIn;
    protected Animation fadeOut;
    protected Animation fadeFastIn;
    protected Animation fadeFastOut;
    protected Animation growBottom;
    protected Animation shrinkBottom;
    protected Animation growFromBottomRightToTopLeft;
    protected Animation shrinkFromTopLeftToBottomRight;
    protected Animation pushRightOut;
    protected Typeface typeface;
    protected Typeface typefaceBold;
    protected Typeface typefaceItalic;
    protected Typeface typefaceBoldItalic;
    protected Vibrator mVibrator;
    protected float mActionBarHeight;

    protected boolean hasAllPermissions;
    protected boolean hasLocationPermissions;
    protected boolean hasReadWritePermissions;
    protected boolean hasReadContactsPermissions;
    protected boolean hasGetAccountsPermissions;
    protected boolean hasReadPhoneStatePermissions;
    protected boolean hasCallPermissions;

    private PermissionListener permissionListener;
    protected Animation slideRightIn;
    protected Animation slideRightOut;

    protected void initBase() {

        AutoRideDriverApps.checkForToken();

//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this,new Crashlytics());
        // Fabric.with(this, new Crashlytics()/*, new TwitterCore(authConfig), new Digits.Builder().build()*/);

//        Digits.enableSandbox();

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //	getActionBar().setHomeButtonEnabled(true);

        fop = new FileOp(this.getApplicationContext());

        final TypedArray mstyled = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        mActionBarHeight = mstyled.getDimension(0, 0);
        mstyled.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
/*        if (android.os.Build.VERSION.SDK_INT >= 21) {
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }*/

        //	llBottomBarActionPopup=(LinearLayout)findViewById(R.id.ll_bottombar_popmenu);

/*        typeface = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        typefaceBold = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        typefaceItalic = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Italic.ttf");
        typefaceBoldItalic = Typeface.createFromAsset(getAssets(), "RobotoCondensed-BoldItalic.ttf");*/

        pushRightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);
        disappear = AnimationUtils.loadAnimation(this, R.anim.disappear);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        slideUpIn = AnimationUtils.loadAnimation(this, R.anim.slide_up_in);
        slideDownOut = AnimationUtils.loadAnimation(this, R.anim.slide_down_out);
        slideDownIn = AnimationUtils.loadAnimation(this, R.anim.slide_down_in);
        slideUpOut = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeFastIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in);
        fadeFastOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out);
        growBottom = AnimationUtils.loadAnimation(this, R.anim.grow_from_bottom);
        shrinkBottom = AnimationUtils.loadAnimation(this, R.anim.shrink_from_top);
        growFromBottomRightToTopLeft = AnimationUtils.loadAnimation(this, R.anim.grow_from_bottomright_to_topleft);
        shrinkFromTopLeftToBottomRight = AnimationUtils.loadAnimation(this, R.anim.shrink_from_topleft_to_bottomright);

        r = getResources();
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
        width = r.getDisplayMetrics().widthPixels;
        height = r.getDisplayMetrics().heightPixels;

        snackBarDismissOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                //mVibrator.vibrate(25);
                v.setVisibility(View.GONE);
            }
        };
    }

    protected static void restart(Context context, int delay) {

        if (delay == 0) {
            delay = 1;
        }

        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

   /* public void performCall(String phone) {
        String url = "tel:" + phone;
        Log.i(TAG, "performCall:  PHONE : " + phone);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }*/
/*
    public static String getUserDateFromUnix(String time) {
        if (time.equalsIgnoreCase("-62169984000") || time.equalsIgnoreCase("false") || time.equalsIgnoreCase("true"))
            return "";
        try {
            Calendar calTemp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calTemp.setTimeInMillis(Long.valueOf(time) * 1000);
            calTemp.setTimeZone(Calendar.getInstance().getTimeZone());
            time = new SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    .format(new Date(calTemp.getTimeInMillis()));
            return time;
        } catch (Exception e) {
            //	e.printStackTrace();
            return time;
        }
    }

    public static String getUserTimeFromUnix(String date) {

        if (date.equalsIgnoreCase("-62169984000") || date.equalsIgnoreCase("false") || date.equalsIgnoreCase("true"))
            return "";
        try {
            Calendar calTemp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calTemp.setTimeInMillis(Long.valueOf(date) * 1000);
            calTemp.setTimeZone(Calendar.getInstance().getTimeZone());
            date = new SimpleDateFormat("hh:mma", Locale.US)
                    .format(new Date(calTemp.getTimeInMillis()));
            return date;
        } catch (Exception e) {
            //	e.printStackTrace();
            return date;
        }
    }
*/

  /*  protected String getDeviceID() {
        String DEVICEID = "";
        String IMEI = "";
        try {
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = mngr.getDeviceId();

            System.out.println("IMEI : " + IMEI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DEVICEID = Build.SERIAL + IMEI;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DEVICE ID : " + DEVICEID);
        return DEVICEID;
    }*/

    void setPermissionListener(PermissionListener permissionListener) {
        this.permissionListener = permissionListener;
    }

    protected boolean checkForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CLEAR_APP_CACHE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                /*String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.CLEAR_APP_CACHE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);*/
                return hasAllPermissions = false;
            } else {
                return hasAllPermissions = true;
            }
        } else {
            return hasAllPermissions = true;
        }
    }

    protected void getAllPermssions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CLEAR_APP_CACHE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.CLEAR_APP_CACHE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE};

                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            }
        }
    }

    protected boolean checkForContactsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

               /* String[] permissions = new String[]{
                        Manifest.permission.READ_CONTACTS};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_CONTACTS);*/
                return hasReadContactsPermissions = false;
            } else {
                return hasReadContactsPermissions = true;
            }
        } else {
            return hasReadContactsPermissions = true;
        }
    }

    protected void getContactsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = new String[]{
                        Manifest.permission.READ_CONTACTS};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_CONTACTS);
            }
        }
    }

    protected boolean checkForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                /*String[] permissions = new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_LOCATION);*/
                return hasLocationPermissions = false;
            } else {
                return hasLocationPermissions = true;
            }
        } else {
            return hasLocationPermissions = true;
        }
    }

    protected void getLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_LOCATION);
            }
        }
    }

    protected boolean checkForReadWritePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                /*String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_WRITE);*/
                return hasReadWritePermissions = false;
            } else {
                return hasReadWritePermissions = true;
            }
        } else {
            return hasReadWritePermissions = true;
        }
    }

    protected void getReadWritePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_WRITE);
            }
        }
    }

    protected boolean checkForGetAccountsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
              /*  String[] permissions = new String[]{Manifest.permission.GET_ACCOUNTS};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_GET_ACCOUNTS);*/
                return hasGetAccountsPermissions = false;
            } else {
                return hasGetAccountsPermissions = true;
            }
        } else {
            return hasGetAccountsPermissions = true;
        }
    }

    protected void getGetAccountsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.GET_ACCOUNTS};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_GET_ACCOUNTS);
            }
        }
    }

    protected boolean checkForReadPhoneStatePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
//                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_PHONE_STATE);
                return hasReadPhoneStatePermissions = false;
            } else {
                return hasReadPhoneStatePermissions = true;
            }
        } else {
            return hasReadPhoneStatePermissions = true;
        }
    }

    protected void getReadPhoneStatePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_PHONE_STATE);
            }
        }
    }

    protected boolean checkForCallPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
//                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_READ_PHONE_STATE);
                return hasCallPermissions = false;
            } else {
                return hasCallPermissions = true;
            }
        } else {
            return hasCallPermissions = true;
        }
    }

    protected void getCallPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.CALL_PHONE};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CALL);
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_PERMISSIONS) {
//            hasAllPermissions = grantResults[1] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[7] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[8] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[9] == PackageManager.PERMISSION_GRANTED;
//            try {
//                permissionListener.onPermissionCheckCompleted(requestCode, hasAllPermissions);
//            } catch (Exception ignored) {
//            }
//        }
//        if (requestCode == REQUEST_PERMISSIONS_LOCATION) {
//            if (grantResults.length == 2) {
//                hasLocationPermissions = grantResults[1] == PackageManager.PERMISSION_GRANTED
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                try {
//                    permissionListener.onPermissionCheckCompleted(requestCode, hasLocationPermissions);
//                } catch (Exception ignored) {
//                }
//            }
//        }
//        if (requestCode == REQUEST_PERMISSIONS_READ_WRITE) {
//            if (grantResults.length == 2) {
//                hasReadWritePermissions = grantResults[1] == PackageManager.PERMISSION_GRANTED
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                try {
//                    permissionListener.onPermissionCheckCompleted(requestCode, hasReadWritePermissions);
//                } catch (Exception ignored) {
//                }
//            }
//        }
//        if (requestCode == REQUEST_PERMISSIONS_READ_CONTACTS) {
//            if (grantResults.length == 1) {
//                hasReadContactsPermissions = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                try {
//                    permissionListener.onPermissionCheckCompleted(requestCode, hasReadContactsPermissions);
//                } catch (Exception ignored) {
//                }
//            }
//        }
//        if (requestCode == REQUEST_PERMISSIONS_GET_ACCOUNTS) {
//            if (grantResults.length == 1) {
//                hasGetAccountsPermissions = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                try {
//                    permissionListener.onPermissionCheckCompleted(requestCode, hasGetAccountsPermissions);
//                } catch (Exception ignored) {
//                }
//            }
//        }
//
//        if (requestCode == REQUEST_PERMISSIONS_READ_PHONE_STATE) {
//            if (grantResults.length == 1) {
//                hasReadPhoneStatePermissions = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                try {
//                    permissionListener.onPermissionCheckCompleted(requestCode, hasReadPhoneStatePermissions);
//                } catch (Exception ignored) {
//                }
//            }
//        }
//
//        if (requestCode == REQUEST_PERMISSIONS_CALL) {
//            if (grantResults.length == 1) {
//                hasCallPermissions = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                try {
//                    permissionListener.onPermissionCheckCompleted(requestCode, hasCallPermissions);
//                } catch (Exception ignored) {
//                }
//            }
//        }
//    }

    protected boolean checkLocationSettingsStatus() {

        int locationMode = 0;
        String locationProviders;
        boolean isLocationServiceEnabled = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            isLocationServiceEnabled = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isLocationServiceEnabled = !TextUtils.isEmpty(locationProviders);
        }

        if (!isLocationServiceEnabled) {
            // notify user
            if (!isFinishing()) {
                if (!isLocationServiceEnableRequestShown) {
                    isLocationServiceEnableRequestShown = true;
                    PopupMessage popupMessage = new PopupMessage(this);
                    popupMessage.show("Please enable location service from the Settings.", 0, "Open Settings");
                    popupMessage.setPopupActionListener(new PopupMessage.PopupActionListener() {
                        @Override
                        public void actionCompletedSuccessfully(boolean result) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            isLocationServiceEnableRequestShown = false;
                        }

                        @Override
                        public void actionFailed() {
                            isLocationServiceEnableRequestShown = false;
                        }
                    });
                }
            }
        }
        return isLocationServiceEnabled;
    }

    public abstract void onConnectionSuspended(int arg0);
}