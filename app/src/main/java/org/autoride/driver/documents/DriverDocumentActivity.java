package org.autoride.driver.documents;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import org.autoride.driver.driver.net.parsers.DriverInfoParser;
import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.DriverAsUser;
import org.autoride.driver.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.parsers.DriverStatusParser;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedBodyBuilder;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.networks.parsers.ResponseParser;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.utils.AppConstants;
import org.autoride.driver.utils.ImageEncodeReducer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DriverDocumentActivity extends BaseAppCompatNoDrawerActivity implements DriverApiUrl, AppsConstants {

    private static final String TAG = "DriverDocument";
    private ViewFlipper viewFlipperDriverDocuments;
    private LinearLayout llVehicleButton, llDrivingLicenceButton, llInsuranceButton, llBlueBookButton, llTaxTokenButton,
            llVehicleTakePhoto, llLicenceTakePhoto, llInsuranceTakePhoto, llBlueBookTakePhoto, llTaxTokenTakePhoto;
    private ImageView ivVehiclePhoto, ivDrivingLicencePhoto, ivInsurancePhoto, ivBlueBookPhoto, ivTaxTokenPhoto;
    private TextView tvVehicleMsg, tvLicenceMsg, tvInsuranceMsg, tvBlueBookMsg, tvTaxTokenMsg, tvVehicleInfoMsg,
            tvVehicleMsg1, tvLicenceMsg1, tvInsuranceMsg1, tvBlueBookMsg1, tvTaxTokenMsg1, tvVehicleInfoMsg1, tvVehicleSureMsg;
    private String accessToken, rememberToken, driverId, uploadPhotoType, viewPhotoType, mCurrentPhotoPath;
    private EditText etVehicleNo, etVehicleModel, etVehicleBrand;
    private ArrayAdapter<String> adapterVehicleType;
    private Spinner spinnerVehicleType;
    private List<String> vehicleType;
    private DriverAsUser driverInfo;
    private static OkHttpClient client;
    private Button btnUploadVehiclePhoto, btnUploadLicencePhoto, btnUploadInsurancePhoto,
            btnUploadBlueBookPhoto, btnUploadTaxTokenPhoto, btnSetVehicleInfo;
    private int CAMERA_RUNTIME_PERMISSION = 1, WRITE_EXTERNAL_PERMISSION_RUNTIME = 2;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 3, CAMERA_IMAGE_REQUEST_CODE = 4;
    private PhotoCaptureModal photoCaptureModal;
    private PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_document);

        client = new OkHttpClient();
        setUiViewComponent();
    }

    private void setUiViewComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewFlipperDriverDocuments = (ViewFlipper) findViewById(R.id.view_flipper_driver_documents);
        viewFlipperDriverDocuments.setDisplayedChild(0);

        spinnerVehicleType = (Spinner) findViewById(R.id.spinner_vehicle_type);
        etVehicleNo = (EditText) findViewById(R.id.et_vehicle_no);
        etVehicleModel = (EditText) findViewById(R.id.et_vehicle_model);
        etVehicleBrand = (EditText) findViewById(R.id.et_vehicle_brand);

        llVehicleButton = (LinearLayout) findViewById(R.id.ll_driver_vehicle_button);
        llDrivingLicenceButton = (LinearLayout) findViewById(R.id.ll_driver_licence_button);
        llInsuranceButton = (LinearLayout) findViewById(R.id.ll_insurance_button);
        llBlueBookButton = (LinearLayout) findViewById(R.id.ll_blue_book_button);
        llTaxTokenButton = (LinearLayout) findViewById(R.id.ll_tax_token_button);

        llVehicleTakePhoto = (LinearLayout) findViewById(R.id.ll_vehicle_take_photo);
        llLicenceTakePhoto = (LinearLayout) findViewById(R.id.ll_licence_take_photo);
        llInsuranceTakePhoto = (LinearLayout) findViewById(R.id.ll_insurance_take_photo);
        llBlueBookTakePhoto = (LinearLayout) findViewById(R.id.ll_blue_book_take_photo);
        llTaxTokenTakePhoto = (LinearLayout) findViewById(R.id.ll_tax_token_take_photo);

        ivVehiclePhoto = (ImageView) findViewById(R.id.iv_driver_vehicle_photo1);
        ivDrivingLicencePhoto = (ImageView) findViewById(R.id.iv_driver_licence_photo);
        ivInsurancePhoto = (ImageView) findViewById(R.id.iv_insurance_photo);
        ivBlueBookPhoto = (ImageView) findViewById(R.id.iv_blue_book_photo);
        ivTaxTokenPhoto = (ImageView) findViewById(R.id.iv_tax_token_photo);

        tvVehicleMsg = (TextView) findViewById(R.id.tv_vehicle_msg);
        tvVehicleMsg1 = (TextView) findViewById(R.id.tv_vehicle_msg1);
        tvLicenceMsg = (TextView) findViewById(R.id.tv_licence_msg);
        tvLicenceMsg1 = (TextView) findViewById(R.id.tv_licence_msg1);
        tvInsuranceMsg = (TextView) findViewById(R.id.tv_insurance_msg);
        tvInsuranceMsg1 = (TextView) findViewById(R.id.tv_insurance_msg1);
        tvBlueBookMsg = (TextView) findViewById(R.id.tv_blue_book_msg);
        tvBlueBookMsg1 = (TextView) findViewById(R.id.tv_blue_book_msg1);
        tvTaxTokenMsg = (TextView) findViewById(R.id.tv_tax_token_msg);
        tvTaxTokenMsg1 = (TextView) findViewById(R.id.tv_tax_token_msg1);
        tvVehicleInfoMsg = (TextView) findViewById(R.id.tv_vehicle_info_msg);
        tvVehicleInfoMsg1 = (TextView) findViewById(R.id.tv_vehicle_info_msg1);
        tvVehicleSureMsg = (TextView) findViewById(R.id.tv_vehicle_sure_msg);

        btnUploadVehiclePhoto = (Button) findViewById(R.id.btn_upload_vehicle_photo);
        btnUploadVehiclePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadVehiclePhoto();
            }
        });

        btnUploadLicencePhoto = (Button) findViewById(R.id.btn_upload_licence_photo);
        btnUploadLicencePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadLicencePhoto();
            }
        });

        btnUploadInsurancePhoto = (Button) findViewById(R.id.btn_upload_insurance_photo);
        btnUploadInsurancePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadInsurancePhoto();
            }
        });

        btnUploadBlueBookPhoto = (Button) findViewById(R.id.btn_upload_blue_book_photo);
        btnUploadBlueBookPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadBlueBookPhoto();
            }
        });

        btnUploadTaxTokenPhoto = (Button) findViewById(R.id.btn_upload_tax_token_photo);
        btnUploadTaxTokenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadTaxTokenPhoto();
            }
        });

        btnSetVehicleInfo = (Button) findViewById(R.id.btn_set_vehicle_info);
        btnSetVehicleInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetVehicleInfo();
            }
        });

        SharedPreferences sp = getBaseContext().getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForToken: " + sp.getAll());

            accessToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
            rememberToken = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
            driverId = sp.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");
        }

        driverInfo = new DriverAsUser();
        vehicleType = new ArrayList<>();
        vehicleType.add(getString(R.string.txt_doc8));

        adapterVehicleType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleType);
        adapterVehicleType.setDropDownViewResource(R.layout.item_spinner);
        spinnerVehicleType.setAdapter(adapterVehicleType);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }

    public void onDriverVehicleImage(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            viewPhotoType = "driverVehiclePhoto";
            getAllDriverDocumentPhotos();
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(1);
            getSupportActionBar().setTitle(R.string.txt_doc2);
        } else {
            snackBarNoInternet();
        }
    }

    public void onDrivingLicenceImage(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            viewPhotoType = "drivingLicence";
            getAllDriverDocumentPhotos();
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(2);
            getSupportActionBar().setTitle(R.string.txt_doc3);
        } else {
            snackBarNoInternet();
        }
    }

    public void onVehicleInsuranceImage(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            viewPhotoType = "vehicleInsurance";
            getAllDriverDocumentPhotos();
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(3);
            getSupportActionBar().setTitle(R.string.txt_doc4);
        } else {
            snackBarNoInternet();
        }
    }

    public void onDriverBlueBookImage(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            viewPhotoType = "blueBook";
            getAllDriverDocumentPhotos();
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(4);
            getSupportActionBar().setTitle(R.string.txt_doc5);
        } else {
            snackBarNoInternet();
        }
    }

    public void onDriverTaxTokenImage(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            viewPhotoType = "taxToken";
            getAllDriverDocumentPhotos();
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(5);
            getSupportActionBar().setTitle(R.string.txt_doc6);
        } else {
            snackBarNoInternet();
        }
    }

    public void onVehicleInformation(View view) {
        if (AutoRideDriverApps.isNetworkAvailable()) {
            getVehicleInfo();
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(6);
            getSupportActionBar().setTitle(R.string.txt_doc7);
        } else {
            snackBarNoInternet();
        }
    }

    public void onDriverVehicleTakePhoto(View view) {
        uploadPhotoType = "driverVehiclePhoto";
        showPhotoCaptureModal();
    }

    public void onDrivingLicenceTakePhoto(View view) {
        uploadPhotoType = "drivingLicence";
        showPhotoCaptureModal();
    }

    public void onVehicleInsuranceTakePhoto(View view) {
        uploadPhotoType = "vehicleInsurance";
        showPhotoCaptureModal();
    }

    public void onBlueBookTakePhoto(View view) {
        uploadPhotoType = "blueBook";
        showPhotoCaptureModal();
    }

    public void onTaxTokenTakePhoto(View view) {
        uploadPhotoType = "taxToken";
        showPhotoCaptureModal();
    }

    public void onVehiclePhotoRetake(View view) {
        uploadPhotoType = "driverVehiclePhoto";
        showPhotoCaptureModal();
    }

    public void onLicenceRetake(View view) {
        uploadPhotoType = "drivingLicence";
        showPhotoCaptureModal();
    }

    public void onVehicleInsuranceRetake(View view) {
        uploadPhotoType = "vehicleInsurance";
        showPhotoCaptureModal();
    }

    public void onBlueBookRetake(View view) {
        uploadPhotoType = "blueBook";
        showPhotoCaptureModal();
    }

    public void onTaxTokenRetake(View view) {
        uploadPhotoType = "taxToken";
        showPhotoCaptureModal();
    }

    private void showPhotoCaptureModal() {
        photoCaptureModal = new PhotoCaptureModal();
        photoCaptureModal.show(getSupportFragmentManager(), TAG);
    }

    public void onImageFromGallery(View view) {
        if (ActivityCompat.checkSelfPermission(DriverDocumentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) DriverDocumentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_RUNTIME);
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
        if (ActivityCompat.checkSelfPermission(DriverDocumentActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) DriverDocumentActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_RUNTIME_PERMISSION);
            if (photoCaptureModal != null) {
                photoCaptureModal.dismiss();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(DriverDocumentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) DriverDocumentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_RUNTIME);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mCurrentPhotoPath = ImageEncodeReducer.getRealPathFromURI(data.getData(), DriverDocumentActivity.this);
            showSelectedImage();
        } else if (requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {  // never check data null here
            showSelectedImage();
        }
    }

    private void showSelectedImage() {
        Bitmap bitmap = ImageEncodeReducer.getBitmapImage(mCurrentPhotoPath);
        if (uploadPhotoType.equals("driverVehiclePhoto")) {
            ivVehiclePhoto.setImageBitmap(bitmap);
            llVehicleButton.setVisibility(View.VISIBLE);
            llVehicleTakePhoto.setVisibility(View.GONE);
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(1);
        } else if (uploadPhotoType.equals("drivingLicence")) {
            ivDrivingLicencePhoto.setImageBitmap(bitmap);
            llDrivingLicenceButton.setVisibility(View.VISIBLE);
            llLicenceTakePhoto.setVisibility(View.GONE);
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(2);
        } else if (uploadPhotoType.equals("vehicleInsurance")) {
            ivInsurancePhoto.setImageBitmap(bitmap);
            llInsuranceButton.setVisibility(View.VISIBLE);
            llInsuranceTakePhoto.setVisibility(View.GONE);
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(3);
        } else if (uploadPhotoType.equals("blueBook")) {
            ivBlueBookPhoto.setImageBitmap(bitmap);
            llBlueBookButton.setVisibility(View.VISIBLE);
            llBlueBookTakePhoto.setVisibility(View.GONE);
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(4);
        } else if (uploadPhotoType.equals("taxToken")) {
            ivTaxTokenPhoto.setImageBitmap(bitmap);
            llTaxTokenButton.setVisibility(View.VISIBLE);
            llTaxTokenTakePhoto.setVisibility(View.GONE);
            viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            viewFlipperDriverDocuments.setDisplayedChild(5);
        }
    }

    private void getVehicleInfo() {
        swipeView.setRefreshing(true);
        new GetSetVehicleType().execute();
        swipeView.setRefreshing(true);
        new GetSetVehicleInfo().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSetVehicleType extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            swipeView.setRefreshing(true);
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(VEHICLE_TYPE_URL)
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
            vehicleTypeParser(result);
        }
    }

    private void vehicleTypeParser(String sResponse) {
        swipeView.setRefreshing(false);
        JSONObject jsonObj = null;
        try {
            if (sResponse != null) {
                jsonObj = new JSONObject(sResponse);
                if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {
                    if (jsonObj.has("vehicle")) {
                        JSONArray jsonArray = jsonObj.getJSONArray("vehicle");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String vName = (String) jsonArray.get(i);
                            vehicleType.add(vName);
                        }
                    }
                }

                if (jsonObj.optString("statusCode").equals(String.valueOf(401))) {
                    if (jsonObj.optString("status").equalsIgnoreCase("Error")) {
                        if (jsonObj.has("message")) {
                            Snackbar.make(coordinatorLayout, jsonObj.optString("message"), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                        } else {
                            Snackbar.make(coordinatorLayout, jsonObj.optString("message"), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSetVehicleInfo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            swipeView.setRefreshing(true);
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(VEHICLE_INFO_URL, getBodyJSON(null, null)),
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
            vehicleInfParse(result);
        }
    }

    private void vehicleInfParse(String sResponse) {

        tvVehicleInfoMsg.setVisibility(View.GONE);
        tvVehicleInfoMsg1.setVisibility(View.GONE);
        tvVehicleSureMsg.setVisibility(View.GONE);
        swipeView.setRefreshing(false);

        AuthBean authBean = DriverStatusParser.parseDriverStatus(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                for (DriverAsUser vInfo : authBean.getDriverInfoList()) {
                    spinnerVehicleType.setSelection(adapterVehicleType.getPosition(vInfo.getVehicleType()));
                    etVehicleBrand.setText(vInfo.getVehicleBrand());
                    etVehicleModel.setText(vInfo.getDriverVehicleModel());
                    etVehicleNo.setText(vInfo.getVehicleNo());
                }
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void getAllDriverDocumentPhotos() {
        swipeView.setRefreshing(true);
        new GetSetDocumentPhotos().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSetDocumentPhotos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
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
            documentParser(result);
        }
    }

    private void documentParser(String serverResponse) {
        swipeView.setRefreshing(false);
        List<DriverInfo> driverInfoList = new DriverInfoParser().parseDriverInfoRequest(serverResponse);
        if (driverInfoList != null) {
            for (DriverInfo driverInfo : driverInfoList) {
                if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                    if (viewPhotoType.equalsIgnoreCase("driverVehiclePhoto")) {

                        photoViewAttacher = new PhotoViewAttacher(ivVehiclePhoto);
                        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        photoViewAttacher.update();

                        // "http://128.199.80.10/golden/image"

                        String url = driverInfo.getDriverAsUser().getVehiclePhoto();
                        Glide.with(getApplicationContext())
                                .load(url)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .fitCenter())
                                .into(ivVehiclePhoto);

                        if (url == null || url.equals("")) {
                            tvVehicleMsg.setVisibility(View.VISIBLE);
                            tvVehicleMsg1.setVisibility(View.VISIBLE);
                        }
                    } else if (viewPhotoType.equalsIgnoreCase("drivingLicence")) {

                        photoViewAttacher = new PhotoViewAttacher(ivDrivingLicencePhoto);
                        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        photoViewAttacher.update();

                        String url2 = driverInfo.getDriverAsUser().getLicencePhoto();
                        Glide.with(getApplicationContext())
                                .load(url2)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .fitCenter())
                                .into(ivDrivingLicencePhoto);

                        if (url2 == null || url2.equals("")) {
                            tvLicenceMsg.setVisibility(View.VISIBLE);
                            tvLicenceMsg1.setVisibility(View.VISIBLE);
                        }
                    } else if (viewPhotoType.equalsIgnoreCase("vehicleInsurance")) {

                        photoViewAttacher = new PhotoViewAttacher(ivInsurancePhoto);
                        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        photoViewAttacher.update();

                        String url3 = driverInfo.getDriverAsUser().getInsurancePhoto();
                        Glide.with(getApplicationContext())
                                .load(url3)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .fitCenter())
                                .into(ivInsurancePhoto);

                        if (url3 == null || url3.equals("")) {
                            tvInsuranceMsg.setVisibility(View.VISIBLE);
                            tvInsuranceMsg1.setVisibility(View.VISIBLE);
                        }
                    } else if (viewPhotoType.equalsIgnoreCase("blueBook")) {

                        photoViewAttacher = new PhotoViewAttacher(ivBlueBookPhoto);
                        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        photoViewAttacher.update();

                        String url4 = driverInfo.getDriverAsUser().getBlueBookPhoto();
                        Glide.with(getApplicationContext())
                                .load(url4)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .fitCenter())
                                .into(ivBlueBookPhoto);

                        if (url4 == null || url4.equals("")) {
                            tvBlueBookMsg.setVisibility(View.VISIBLE);
                            tvBlueBookMsg1.setVisibility(View.VISIBLE);
                        }
                    } else if (viewPhotoType.equalsIgnoreCase("taxToken")) {

                        photoViewAttacher = new PhotoViewAttacher(ivTaxTokenPhoto);
                        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        photoViewAttacher.update();

                        String url5 = driverInfo.getDriverAsUser().getTexTokenPhoto();
                        Glide.with(getApplicationContext())
                                .load(url5)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .fitCenter())
                                .into(ivTaxTokenPhoto);

                        if (url5 == null || url5.equals("")) {
                            tvTaxTokenMsg.setVisibility(View.VISIBLE);
                            tvTaxTokenMsg1.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                    Snackbar.make(coordinatorLayout, driverInfo.getErrorMsg(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                    logOutHere();
                }
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void onUploadVehiclePhoto() {

        swipeView.setRefreshing(true);
        btnUploadVehiclePhoto.setText(R.string.text_btn_waiting);
        btnUploadVehiclePhoto.setEnabled(false);

        if (AutoRideDriverApps.isNetworkAvailable()) {
            new VehiclePhotoUploader().execute();
        } else {
            swipeView.setRefreshing(false);
            btnUploadVehiclePhoto.setText(R.string.btn_save);
            btnUploadVehiclePhoto.setEnabled(true);

            snackBarNoInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class VehiclePhotoUploader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVER_VEHICLE_IMG_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("cover", ImageEncodeReducer.compressImage(mCurrentPhotoPath))),
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
            vehiclePhotoParser(result);
        }
    }

    private void vehiclePhotoParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnUploadVehiclePhoto.setText(R.string.btn_save);
        btnUploadVehiclePhoto.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                llVehicleTakePhoto.setVisibility(View.VISIBLE);
                llVehicleButton.setVisibility(View.GONE);
                mCurrentPhotoPath = null;

                viewFlipperDriverDocuments.setInAnimation(slideRightIn);
                viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
                viewFlipperDriverDocuments.setDisplayedChild(0);
                getSupportActionBar().setTitle(R.string.title_activity_driver_document);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void onUploadLicencePhoto() {

        swipeView.setRefreshing(true);
        btnUploadLicencePhoto.setText(R.string.text_btn_waiting);
        btnUploadLicencePhoto.setEnabled(false);

        if (AutoRideDriverApps.isNetworkAvailable()) {
            new LicencePhotoUploader().execute();
        } else {
            swipeView.setRefreshing(false);
            btnUploadLicencePhoto.setText(R.string.btn_save);
            btnUploadLicencePhoto.setEnabled(true);

            snackBarNoInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LicencePhotoUploader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVING_LICENCE_IMG_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("licence", ImageEncodeReducer.compressImage(mCurrentPhotoPath))),
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
            licencePhotoParser(result);
        }
    }

    private void licencePhotoParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnUploadLicencePhoto.setText(R.string.btn_save);
        btnUploadLicencePhoto.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                llLicenceTakePhoto.setVisibility(View.VISIBLE);
                llDrivingLicenceButton.setVisibility(View.GONE);
                mCurrentPhotoPath = null;

                viewFlipperDriverDocuments.setInAnimation(slideRightIn);
                viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
                viewFlipperDriverDocuments.setDisplayedChild(0);
                getSupportActionBar().setTitle(R.string.title_activity_driver_document);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void onUploadInsurancePhoto() {

        swipeView.setRefreshing(true);
        btnUploadInsurancePhoto.setText(R.string.text_btn_waiting);
        btnUploadInsurancePhoto.setEnabled(false);

        if (AutoRideDriverApps.isNetworkAvailable()) {
            new InsurancePhotoUploader().execute();
        } else {
            swipeView.setRefreshing(false);
            btnUploadInsurancePhoto.setText(R.string.btn_save);
            btnUploadInsurancePhoto.setEnabled(true);

            snackBarNoInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class InsurancePhotoUploader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVER_VEHICLE_INSURANCE_IMG_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("insurance", ImageEncodeReducer.compressImage(mCurrentPhotoPath))),
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
            insurancePhotoParser(result);
        }
    }

    private void insurancePhotoParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnUploadInsurancePhoto.setText(R.string.btn_save);
        btnUploadInsurancePhoto.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                llInsuranceTakePhoto.setVisibility(View.VISIBLE);
                llInsuranceButton.setVisibility(View.GONE);
                mCurrentPhotoPath = null;

                viewFlipperDriverDocuments.setInAnimation(slideRightIn);
                viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
                viewFlipperDriverDocuments.setDisplayedChild(0);
                getSupportActionBar().setTitle(R.string.title_activity_driver_document);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void onUploadBlueBookPhoto() {

        swipeView.setRefreshing(true);
        btnUploadBlueBookPhoto.setText(R.string.text_btn_waiting);
        btnUploadBlueBookPhoto.setEnabled(false);

        if (AutoRideDriverApps.isNetworkAvailable()) {
            new BlueBookPhotoUploader().execute();
        } else {
            swipeView.setRefreshing(false);
            btnUploadBlueBookPhoto.setText(R.string.btn_save);
            btnUploadBlueBookPhoto.setEnabled(true);

            snackBarNoInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class BlueBookPhotoUploader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVER_VEHICLE_BLUE_BOOK_IMG_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("bluebook", ImageEncodeReducer.compressImage(mCurrentPhotoPath))),
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
            blueBookPhotoParser(result);
        }
    }

    private void blueBookPhotoParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnUploadBlueBookPhoto.setText(R.string.btn_save);
        btnUploadBlueBookPhoto.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                llBlueBookTakePhoto.setVisibility(View.VISIBLE);
                llBlueBookButton.setVisibility(View.GONE);
                mCurrentPhotoPath = null;

                viewFlipperDriverDocuments.setInAnimation(slideRightIn);
                viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
                viewFlipperDriverDocuments.setDisplayedChild(0);
                getSupportActionBar().setTitle(R.string.title_activity_driver_document);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void onUploadTaxTokenPhoto() {

        swipeView.setRefreshing(true);
        btnUploadTaxTokenPhoto.setText(R.string.text_btn_waiting);
        btnUploadTaxTokenPhoto.setEnabled(false);

        if (AutoRideDriverApps.isNetworkAvailable()) {
            new TaxTokenPhotoUploader().execute();
        } else {
            swipeView.setRefreshing(false);
            btnUploadTaxTokenPhoto.setText(R.string.btn_save);
            btnUploadTaxTokenPhoto.setEnabled(true);

            snackBarNoInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TaxTokenPhotoUploader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(EDIT_DRIVER_VEHICLE_TAX_TOKEN_IMG_URL),
                        RequestedBodyBuilder.buildRequestedBody(getBodyJSON("taxtoken", ImageEncodeReducer.compressImage(mCurrentPhotoPath))),
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
            taxTokenPhotoParser(result);
        }
    }

    private void taxTokenPhotoParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnUploadTaxTokenPhoto.setText(R.string.btn_save);
        btnUploadTaxTokenPhoto.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                llTaxTokenTakePhoto.setVisibility(View.VISIBLE);
                llTaxTokenButton.setVisibility(View.GONE);
                mCurrentPhotoPath = null;

                viewFlipperDriverDocuments.setInAnimation(slideRightIn);
                viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
                viewFlipperDriverDocuments.setDisplayedChild(0);
                getSupportActionBar().setTitle(R.string.title_activity_driver_document);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private void onSetVehicleInfo() {

        swipeView.setRefreshing(true);
        btnSetVehicleInfo.setText(R.string.text_btn_waiting);
        btnSetVehicleInfo.setEnabled(false);

        if (collectVehicleType() && collectBrand() && collectModel() && collectVNumber()) {
            if (AutoRideDriverApps.isNetworkAvailable()) {
                new VehicleInfoSetter().execute();
            } else {
                swipeView.setRefreshing(false);
                btnSetVehicleInfo.setText(R.string.btn_save);
                btnSetVehicleInfo.setEnabled(true);

                snackBarNoInternet();
            }
        } else {
            swipeView.setRefreshing(false);
            btnSetVehicleInfo.setText(R.string.btn_save);
            btnSetVehicleInfo.setEnabled(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class VehicleInfoSetter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(VEHICLE_INFO_UPDATE_URL),
                        RequestedBodyBuilder.buildRequestedBody(getVehicleInfoBodyJSON()),
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
            vehicleInfoParser(result);
        }
    }

    private void vehicleInfoParser(String sResponse) {

        swipeView.setRefreshing(false);
        btnSetVehicleInfo.setText(R.string.btn_save);
        btnSetVehicleInfo.setEnabled(true);

        AuthBean authBean = ResponseParser.responseParser(sResponse);
        if (authBean != null) {
            if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                viewFlipperDriverDocuments.setInAnimation(slideRightIn);
                viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
                viewFlipperDriverDocuments.setDisplayedChild(0);
                getSupportActionBar().setTitle(R.string.title_activity_driver_document);

                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                Snackbar.make(coordinatorLayout, authBean.getErrorMsg(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            } else if (authBean.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                logOutHere();
            }
        } else {
            snackBarSlowInternet();
        }
    }

    private boolean collectVehicleType() {
        if (spinnerVehicleType.getSelectedItemPosition() < 1) {
            Snackbar.make(coordinatorLayout, R.string.spinner_vehicle_type, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        driverInfo.setVehicleType(spinnerVehicleType.getSelectedItem().toString());
        return true;
    }

    private boolean collectBrand() {
        driverInfo.setVehicleBrand(etVehicleBrand.getText().toString());
        if (driverInfo.getVehicleBrand() == null || driverInfo.getVehicleBrand().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle_brand, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getVehicleBrand().length() < 3) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle1, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getVehicleBrand().length() > 30) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle1, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private boolean collectModel() {
        driverInfo.setDriverVehicleModel(etVehicleModel.getText().toString());
        if (driverInfo.getDriverVehicleModel() == null || driverInfo.getDriverVehicleModel().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle_model, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getDriverVehicleModel().length() < 3) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getDriverVehicleModel().length() > 30) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle2, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private boolean collectVNumber() {
        driverInfo.setVehicleNo(etVehicleNo.getText().toString());
        if (driverInfo.getVehicleNo() == null || driverInfo.getVehicleNo().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle_no, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getVehicleNo().length() < 3) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle3, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (driverInfo.getVehicleNo().length() > 30) {
            Snackbar.make(coordinatorLayout, R.string.et_vehicle3, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

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

    private JSONObject getVehicleInfoBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
            postBody.put("vehicleType", driverInfo.getVehicleType());
            postBody.put("vehicleBrand", driverInfo.getVehicleBrand());
            postBody.put("vehicleModel", driverInfo.getDriverVehicleModel());
            postBody.put("vehicleNumber", driverInfo.getVehicleNo());
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
        int index = viewFlipperDriverDocuments.getDisplayedChild();
        if (index > 0) {

            Intent intent = new Intent(DriverDocumentActivity.this, DriverDocumentActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverDocumentActivity.this.finish();

            // getSupportActionBar().setTitle(R.string.title_activity_driver_document);
            // viewFlipperDriverDocuments.setInAnimation(slideRightIn);
            // viewFlipperDriverDocuments.setOutAnimation(slideRightOut);
            // viewFlipperDriverDocuments.setDisplayedChild(0);
        } else {
            Intent intent = new Intent(DriverDocumentActivity.this, DriverMainActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            DriverDocumentActivity.this.finish();
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
        Toast.makeText(getBaseContext(), R.string.web_error_msg2, Toast.LENGTH_SHORT).show();
        AutoRideDriverApps.logout();
        Intent intent = new Intent(DriverDocumentActivity.this, DriverWelcomeActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        DriverDocumentActivity.this.finish();
    }

    public static class PhotoCaptureModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_photo_dialogue, null);
            dialog.setContentView(view);
        }
    }
}