package org.autoride.driver.history.trip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.maps.android.ui.IconGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.activity.DriverWelcomeActivity;
import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.history.helpers.OnItemClickListener;
import org.autoride.driver.history.helpers.TripHistoryInfo;
import org.autoride.driver.history.helpers.TripHistoryRecyclerViewAdapter;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.notifications.helper.GoogleAPI;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryActivity extends AppCompatActivity implements AppsConstants, DriverApiUrl {

    private static final String TAG = "TripHistory";
    private RecyclerView rvTripHistory;
    private TripHistoryRecyclerViewAdapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;
    private List<TripHistoryInfo> tripHistoryInfoList;
    private ViewFlipper vfTripHistory;
    private SupportMapFragment tripDetailsMapsFragment;
    private GoogleMap tripDetailsMaps;
    private static final float MAP_ZOOM = 16.0f;
    private TextView tvNoTripMsg, tvDTripDate, tvDTripAmount, tvDTripVehicle, tvDTripPaymentType, tvDTripStatus, tvDTripPickup, tvDTripDrop,
            tvDTripRiderName, tvTripBaseFare, tvTripDistanceFare, tvTripTimeFare, tvTripsFareDiscount, tvTripSubTotal, tvTripFareRounding,
            tvTripsFareTotalToPay, tvTripFareTotal, tvTripReceiptPaymentType, tvTripReceiptPaymentTotal, tvTripReceiptTitle;
    private CircleImageView civTripRiderPhoto;
    private ProgressDialog pDialog;
    private CoordinatorLayout clTripHistoryRoot;
    private View.OnClickListener snackBarDismissListener;
    private String accessToken, rememberToken, driverId, tripStatus;
    private OkHttpClient okHttpClient;
    private GoogleAPI mService;
    private List<LatLng> polyLineList;
    private Polyline blackPolyline;
    private Marker pickupMarker, destinationMarker;
    private LatLng tripPickup, tripDrop;
    private Button btnTripDetailsHelp, btnTripDetailsReceipt;
    private LinearLayout llTripDetailsHelp, llTripDetailsReceipt;
    private View viewTripDetailsHelp, viewTripDetailsReceipt;
    protected Animation slideRightIn, slideRightOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        setUiComponent();
    }

    private void setUiComponent() {

        pDialog = new ProgressDialog(this);
        Common.startWaitingDialog(this, pDialog);
        mService = Common.getGoogleAPI();

        okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        okHttpClient = builder.build();

        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        btnTripDetailsHelp = (Button) findViewById(R.id.btn_trip_details_help);
        btnTripDetailsReceipt = (Button) findViewById(R.id.btn_trip_details_receipt);
        llTripDetailsHelp = (LinearLayout) findViewById(R.id.ll_trip_details_help);
        llTripDetailsReceipt = (LinearLayout) findViewById(R.id.ll_trip_details_receipt);
        viewTripDetailsHelp = findViewById(R.id.view_trip_details_help);
        viewTripDetailsReceipt = findViewById(R.id.view_trip_details_receipt);

        clTripHistoryRoot = (CoordinatorLayout) findViewById(R.id.cl_trip_history_root);
        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        };

        tvNoTripMsg = (TextView) findViewById(R.id.tv_no_trip_msg);
        tvDTripDate = (TextView) findViewById(R.id.tv_details_trip_date);
        tvDTripAmount = (TextView) findViewById(R.id.tv_details_trip_amount);
        tvDTripVehicle = (TextView) findViewById(R.id.tv_details_trip_vehicle_desc);
        tvDTripPaymentType = (TextView) findViewById(R.id.tv_details_trip_payment_type);
        tvDTripStatus = (TextView) findViewById(R.id.tv_details_trip_status);
        tvDTripRiderName = (TextView) findViewById(R.id.tv_trip_rider_name);
        //receipt
        tvTripReceiptTitle = (TextView) findViewById(R.id.tv_trip_receipt_title);
        tvTripBaseFare = (TextView) findViewById(R.id.tv_trips_base_fare);
        tvTripDistanceFare = (TextView) findViewById(R.id.tv_trips_distance_fare);
        tvTripTimeFare = (TextView) findViewById(R.id.tv_trips_time_fare);
        tvTripSubTotal = (TextView) findViewById(R.id.tv_trips_sub_total);
        tvTripsFareTotalToPay = (TextView) findViewById(R.id.tv_trips_fare_total_to_pay);
        tvTripsFareDiscount = (TextView) findViewById(R.id.tv_trips_fare_discount);
        tvTripFareRounding = (TextView) findViewById(R.id.tv_trips_fare_rounding);
        tvTripFareTotal = (TextView) findViewById(R.id.tv_trips_fare_total);
        tvTripReceiptPaymentType = (TextView) findViewById(R.id.tv_trips_receipt_payment_type);
        tvTripReceiptPaymentTotal = (TextView) findViewById(R.id.tv_trips_receipt_payment_total);

        tvDTripPickup = (TextView) findViewById(R.id.tv_details_trip_pickup);
        tvDTripDrop = (TextView) findViewById(R.id.tv_details_trip_dest);

        civTripRiderPhoto = (CircleImageView) findViewById(R.id.civ_trip_rider_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_trip_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripHistoryActivity.this, DriverMainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                TripHistoryActivity.this.finish();
            }
        });
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_trip_history);
        collapsingToolbar.setTitle(getString(R.string.txt_trips1));

        vfTripHistory = (ViewFlipper) findViewById(R.id.view_flipper_trip_history);
        vfTripHistory.setDisplayedChild(0);

        tripHistoryInfoList = new ArrayList<>();
        rvTripHistory = (RecyclerView) findViewById(R.id.recycler_view_trip_history);

        SharedPreferences driverInf = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (driverInf != null) {
            Log.i(TAG, "checkForToken: SESSION : " + driverInf.getAll());
            accessToken = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = driverInf.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performGetTripHistory();
                setTripHistoryDetailsUi();
            }
        }, 1000);
    }

    private void setTripHistoryDetailsUi() {
        rvAdapter = new TripHistoryRecyclerViewAdapter(this, tripHistoryInfoList, new OnItemClickListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onClick(View view, int position) {

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_trip_history_details);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vfTripHistory.setInAnimation(slideRightIn);
                        vfTripHistory.setOutAnimation(slideRightOut);
                        vfTripHistory.showPrevious();
                    }
                });
                CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctl_trip_history_details);
                collapsingToolbar.setTitle(getString(R.string.txt_trips2));

                tripStatus = tripHistoryInfoList.get(position).getTripStatus();  // "riding"; //
                tripPickup = tripHistoryInfoList.get(position).getTripPickup();
                tripDrop = tripHistoryInfoList.get(position).getTripDrop();

                tripDetailsMapsFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trip_details_maps_fragment);
                tripDetailsMapsFragment.getMapAsync(new OnMapReadyCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        tripDetailsMaps = googleMap;
                        tripDetailsMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        tripDetailsMaps.setMyLocationEnabled(false);
                        tripDetailsMaps.getUiSettings().setZoomControlsEnabled(false);
                        tripDetailsMaps.getUiSettings().setAllGesturesEnabled(false);

                        tripDetailsMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(tripPickup, MAP_ZOOM));
                        if (tripPickup != null && tripDrop != null) {
                            detailsRoute(tripPickup, tripDrop, tripStatus);
                        }
                    }
                });

                tvDTripDate.setText(tripHistoryInfoList.get(position).getTripDate());
                tvDTripAmount.setText(tripHistoryInfoList.get(position).getTripAmount());
                tvDTripVehicle.setText(tripHistoryInfoList.get(position).getTripVehicleDesc());
                tvDTripPaymentType.setText(tripHistoryInfoList.get(position).getTripPaymentType());

                if (tripStatus.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {
                    tvDTripStatus.setVisibility(View.GONE);
                } else {
                    tvDTripStatus.setText(tripStatus);
                }
                tvDTripRiderName.setText(getString(R.string.txt_trips3) + " " + tripHistoryInfoList.get(position).getTripRiderName());
                Glide.with(getBaseContext())
                        .load(tripHistoryInfoList.get(position).getTripRiderPhoto())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(civTripRiderPhoto);
                //receipt
                tvTripReceiptTitle.setText(getString(R.string.txt_receipt_title1) + " " + tripHistoryInfoList.get(position).getTripVehicleType() + " " + getString(R.string.txt_receipt_title2));

                Double bFare = tripHistoryInfoList.get(position).getTripBaseFare();
                Double dFare = tripHistoryInfoList.get(position).getTripDistanceFare();
                Double tFare = tripHistoryInfoList.get(position).getTripTimeFare();
                Double subTotal = (bFare + dFare + tFare);
                Double fDiscount = tripHistoryInfoList.get(position).getTripFareDiscount();
                Double fTotal = (subTotal - fDiscount);
                Double payTotal = roundDown(fTotal);
                Double rounding = (payTotal - fTotal);

                String TAKA = getString(R.string.txt_taka) + " ";
                String fBFare = TAKA + String.format("%.2f", bFare);
                String fDFare = TAKA + String.format("%.2f", dFare);
                String fTFare = TAKA + String.format("%.2f", tFare);
                String fSTotal = TAKA + String.format("%.2f", subTotal);
                String fDis = TAKA + String.format("%.2f", fDiscount);
                String fTotals = TAKA + String.format("%.2f", fTotal);
                String pTotal = TAKA + String.format("%.2f", payTotal);
                String fRounding = TAKA + String.format("%.2f", rounding);

                tvTripBaseFare.setText(fBFare);
                tvTripDistanceFare.setText(fDFare);
                tvTripTimeFare.setText(fTFare);
                tvTripSubTotal.setText(fSTotal);
                tvTripsFareDiscount.setText(fDis);
                tvTripFareTotal.setText(fTotals);
                tvTripFareRounding.setText(fRounding);
                tvTripReceiptPaymentTotal.setText(pTotal);
                tvTripsFareTotalToPay.setText(pTotal);
                String pType = tripHistoryInfoList.get(position).getTripPaymentType();
                tvTripReceiptPaymentType.setText(pType);

                JSONObject qrCodeContents = new JSONObject();
                try {
                    qrCodeContents.put(getString(R.string.txt_fare1), fBFare);
                    qrCodeContents.put("Distance Fare", fDFare);
                    qrCodeContents.put("Time Fare", fTFare);
                    qrCodeContents.put("Fare Subtotal", fSTotal);
                    qrCodeContents.put("Fare Discount", fDis);
                    qrCodeContents.put("Fare Total", fTotals);
                    qrCodeContents.put("Fare Rounding", fRounding);
                    qrCodeContents.put("Fare Total to pay", pTotal);
                    qrCodeContents.put("Pay to " + pType, pTotal);
                    ((ImageView) findViewById(R.id.iv_trip_receipt_qr_code)).setImageBitmap(qrCodeMaker(qrCodeContents.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                vfTripHistory = (ViewFlipper) findViewById(R.id.view_flipper_trip_history);
                vfTripHistory.setDisplayedChild(1);

                btnTripDetailsHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (llTripDetailsHelp.getVisibility() != View.VISIBLE) {
                            btnTripDetailsHelp.setTextColor(Color.parseColor("#545961"));
                            llTripDetailsHelp.setVisibility(View.VISIBLE);
                        }
                        if (viewTripDetailsHelp.getVisibility() != View.VISIBLE) {
                            viewTripDetailsHelp.setVisibility(View.VISIBLE);
                        }
                        if (llTripDetailsReceipt.getVisibility() == View.VISIBLE) {
                            btnTripDetailsReceipt.setTextColor(Color.parseColor("#B0B0B7"));
                            llTripDetailsReceipt.setVisibility(View.GONE);
                        }
                        if (viewTripDetailsReceipt.getVisibility() == View.VISIBLE) {
                            viewTripDetailsReceipt.setVisibility(View.GONE);
                        }
                    }
                });

                btnTripDetailsReceipt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (llTripDetailsReceipt.getVisibility() != View.VISIBLE) {
                            btnTripDetailsReceipt.setTextColor(Color.parseColor("#545961"));
                            llTripDetailsReceipt.setVisibility(View.VISIBLE);
                        }
                        if (viewTripDetailsReceipt.getVisibility() != View.VISIBLE) {
                            viewTripDetailsReceipt.setVisibility(View.VISIBLE);
                        }
                        if (llTripDetailsHelp.getVisibility() == View.VISIBLE) {
                            btnTripDetailsHelp.setTextColor(Color.parseColor("#B0B0B7"));
                            llTripDetailsHelp.setVisibility(View.GONE);
                        }
                        if (viewTripDetailsHelp.getVisibility() == View.VISIBLE) {
                            viewTripDetailsHelp.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        rvLayoutManager = new LinearLayoutManager(this);
        rvTripHistory.setLayoutManager(rvLayoutManager);
        rvTripHistory.setHasFixedSize(true);
        rvTripHistory.setNestedScrollingEnabled(false);
        rvTripHistory.setAdapter(rvAdapter);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    private Double roundDown(double number) {
        double result = number / 10;
        result = Math.floor(result);
        result *= 10;
        return result;
    }

    private Bitmap qrCodeMaker(String contents) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(contents, BarcodeFormat.QR_CODE, 400, 400);
        return new BarcodeEncoder().createBitmap(matrix);
    }

    private void performGetTripHistory() {
        try {
            new GetTripHistory().execute(TRIP_HISTORY_URL).get();
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            Common.snackBarAlert(getString(R.string.slow_internet_connection), clTripHistoryRoot, snackBarDismissListener);
            e.printStackTrace();
        }
    }

    private class GetTripHistory extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url[0], getBodyJSON()),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
            } catch (Exception e) {
                Log.i(TAG, ERROR_RESPONSE + e.toString());
                e.printStackTrace();
            }
            Log.i(TAG, HTTP_RESPONSE + response);
            return setTripHistoryInfo(response);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private String setTripHistoryInfo(String response) {
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {
                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS) && responseObj.optBoolean(IS_DATA)) {
                            if (responseObj.has(TRIP_HISTORY)) {
                                JSONArray historyArray = responseObj.optJSONArray(TRIP_HISTORY);
                                if (historyArray != null) {
                                    for (int i = 0; i < historyArray.length(); i++) {

                                        TripHistoryInfo historyInfo = new TripHistoryInfo();
                                        JSONObject arrayObj = historyArray.getJSONObject(i);

                                        historyInfo.setTripDate(arrayObj.getString(TRIP_DATE_TIME));
                                        historyInfo.setTripAmount(String.format("%.2f", arrayObj.getDouble(TRIP_AMOUNT)));
                                        historyInfo.setTripPaymentType(getString(R.string.txt_pay_type));
                                        historyInfo.setTripStatus(arrayObj.getString(TRIP_STATUS));
                                        historyInfo.setTripRiderPhoto(arrayObj.getString(PROFILE_PHOTO));
                                        historyInfo.setTripRiderName(arrayObj.getString(FIRST_NAME));

                                        if (arrayObj.has(VEHICLE)) {
                                            JSONObject vObj = arrayObj.optJSONObject(VEHICLE);
                                            if (vObj != null) {
                                                historyInfo.setTripVehicleType(vObj.getString(VEHICLE_TYPE));
                                                historyInfo.setTripVehicleDesc(vObj.getString(VEHICLE_BRAND) + " " + vObj.getString(VEHICLE_MODEL));
                                            }
                                        }

                                        if (arrayObj.has(PICKUP_LOCATION)) {
                                            JSONObject pickObj = arrayObj.optJSONObject(PICKUP_LOCATION);
                                            if (pickObj != null) {
                                                historyInfo.setTripPickup(new LatLng(pickObj.getDouble(LAT), pickObj.getDouble(LNG)));
                                            }
                                        }

                                        if (arrayObj.has(DESTINATION)) {
                                            JSONObject destObj = arrayObj.optJSONObject(DESTINATION);
                                            if (destObj != null) {
                                                historyInfo.setTripDrop(new LatLng(destObj.getDouble(LAT), destObj.getDouble(LNG)));
                                                // historyInfo.setTripDrop(new LatLng(23.780546, 90.426659));
                                            }
                                        }

                                        if (arrayObj.has(TRIP_RECEIPT)) {
                                            JSONObject receiptObj = arrayObj.optJSONObject(TRIP_RECEIPT);
                                            if (receiptObj != null) {
                                                historyInfo.setTripBaseFare(receiptObj.getDouble(BASE_FARE));
                                                historyInfo.setTripDistanceFare(receiptObj.getDouble(DISTANCE_FARE));
                                                historyInfo.setTripTimeFare(receiptObj.getDouble(TIME_FARE));
                                                historyInfo.setTripFareDiscount(receiptObj.getDouble(FARE_DISCOUNT));
                                            }
                                        }
                                        tripHistoryInfoList.add(historyInfo);
                                    }
                                }
                                Common.stopWaitingDialog(pDialog);
                            }
                        } else if (!responseObj.optBoolean(IS_DATA)) {
                            Common.stopWaitingDialog(pDialog);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    tvNoTripMsg.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            Common.stopWaitingDialog(pDialog);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    tvNoTripMsg.setText(R.string.txt_no_ride2);
                                    tvNoTripMsg.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        logOutHere();
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        logOutHere();
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        logOutHere();
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        logOutHere();
                    }
                } else {
                    logOutHere();
                }
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Common.stopWaitingDialog(pDialog);
                        Toast.makeText(TripHistoryActivity.this, R.string.slow_internet_connection, Toast.LENGTH_SHORT).show();
                        Common.snackBarAlert(getString(R.string.slow_internet_connection), clTripHistoryRoot, snackBarDismissListener);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Common.stopWaitingDialog(pDialog);
                    Toast.makeText(TripHistoryActivity.this, R.string.slow_internet_connection, Toast.LENGTH_SHORT).show();
                    Common.snackBarAlert(getString(R.string.slow_internet_connection), clTripHistoryRoot, snackBarDismissListener);
                }
            });
        }
        return response;
    }

    // body and header json
    private JSONObject getBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_ID, driverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getHeaderJSON() {
        JSONObject postHeader = new JSONObject();
        try {
            postHeader.put(ACCESS_TOKENS, accessToken);
            postHeader.put(REMEMBER_TOKEN, rememberToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postHeader;
    }

    private void logOutHere() {
        getToast();
        AutoRideDriverApps.logout();
        Intent intent = new Intent(TripHistoryActivity.this, DriverWelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        TripHistoryActivity.this.finish();
    }

    private void getToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TripHistoryActivity.this, R.string.web_error_msg2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void detailsRoute(final LatLng pickup, LatLng destination, final String rStatus) {
        try {

            if (pickupMarker != null) {
                pickupMarker.remove();
            }
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            if (blackPolyline != null) {
                blackPolyline.remove();
            }

            mService.getPath(Common.directionsApi(pickup, destination, this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyLine = poly.getString("points");
                            polyLineList = Common.decodePoly(polyLine);
                        }

                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        String startPoint = legsObject.getString("start_address");
                        String endPoint = legsObject.getString("end_address");

                        if (tvDTripPickup != null) {
                            tvDTripPickup.setText(startPoint);
                        }

                        if (tvDTripDrop != null) {
                            tvDTripDrop.setText(endPoint);
                        }

                        if (rStatus.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng latLng : polyLineList) {
                                builder.include(latLng);
                            }

                            LatLngBounds bounds = builder.build();
                            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 130);
                            tripDetailsMaps.moveCamera(mCameraUpdate);

                            PolylineOptions blackPolylineOptions = new PolylineOptions();
                            blackPolylineOptions.color(Color.BLACK);
                            blackPolylineOptions.width(8);
                            blackPolylineOptions.startCap(new SquareCap());
                            blackPolylineOptions.endCap(new SquareCap());
                            blackPolylineOptions.jointType(JointType.ROUND);
                            blackPolylineOptions.addAll(polyLineList);
                            blackPolyline = tripDetailsMaps.addPolyline(blackPolylineOptions);

                            IconGenerator destIconGen = new IconGenerator(TripHistoryActivity.this);
                            int destShapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                            Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.trip_destination_marker, null);
                            destIconGen.setBackground(shapeDrawable);
                            View destView = new View(TripHistoryActivity.this);
                            destView.setLayoutParams(new ViewGroup.LayoutParams(destShapeSize, destShapeSize));
                            destIconGen.setContentView(destView);
                            Bitmap destBitmap = destIconGen.makeIcon();
                            Bitmap destBitmapResized = Bitmap.createScaledBitmap(destBitmap, 25, 25, false);
                            destinationMarker = tripDetailsMaps.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(destBitmapResized)));
                        }

                        IconGenerator pickupIconGen = new IconGenerator(TripHistoryActivity.this);
                        int pickupShapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                        Drawable shapeDrawablePickup = ResourcesCompat.getDrawable(getResources(), R.drawable.trip_pickup_marker, null);
                        pickupIconGen.setBackground(shapeDrawablePickup);
                        View pickupView = new View(TripHistoryActivity.this);
                        pickupView.setLayoutParams(new ViewGroup.LayoutParams(pickupShapeSize, pickupShapeSize));
                        pickupIconGen.setContentView(pickupView);
                        Bitmap pickupBitmap = pickupIconGen.makeIcon();
                        Bitmap pickupBitmapResized = Bitmap.createScaledBitmap(pickupBitmap, 28, 28, false);
                        pickupMarker = tripDetailsMaps.addMarker(new MarkerOptions().position(pickup).icon(BitmapDescriptorFactory.fromBitmap(pickupBitmapResized)));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "Throwable " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        int index = vfTripHistory.getDisplayedChild();
        if (index == 1) {
            vfTripHistory.setInAnimation(slideRightIn);
            vfTripHistory.setOutAnimation(slideRightOut);
            vfTripHistory.showPrevious();
        } else {
            Intent intent = new Intent(TripHistoryActivity.this, DriverMainActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            TripHistoryActivity.this.finish();
        }
    }
}