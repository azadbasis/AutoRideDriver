package org.autoride.driver.services.fcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.autoride.driver.app.AutoRideDriverApps;
import org.autoride.driver.configs.Config;
import org.autoride.driver.model.AuthBean;
import org.autoride.driver.message.PopupMessage;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.networks.parsers.ResponseParser;
import org.autoride.driver.model.FCMToken;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

import static org.autoride.driver.networks.DriverApiUrl.SET_FIRE_BASE_TOKEN_URL;

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    private OkHttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
    }

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        updateTokenToServer(refreshToken);
        super.onTokenRefresh();
    }

    private void updateTokenToServer(String refreshToken) {
        FCMToken FCMToken = new FCMToken(refreshToken);
        if (AutoRideDriverApps.isNetworkAvailable()) {
            new SaveFCMToken().execute(FCMToken.getToken());
        } else {
            new PopupMessage((Activity) getBaseContext()).show(AppConstants.NO_NETWORK_AVAILABLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveFCMToken extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(SET_FIRE_BASE_TOKEN_URL, getBodyJSON("fireBaseToken", url[0])),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
                Log.i("TAG", "ok_http_response " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserFCMToken(result);
        }
    }

    private void parserFCMToken(String response) {
        AuthBean authBean = ResponseParser.responseParser(response);
        assert authBean != null;
        if (authBean.getStatus().equalsIgnoreCase("Success")) {
            // Toast.makeText(getBaseContext(), authBean.getErrorMsg() + "erewre", Toast.LENGTH_SHORT).show();
        } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
            // Toast.makeText(getBaseContext(), authBean.getErrorMsg(), Toast.LENGTH_SHORT).show();
        } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
            // Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
        }
    }

    // body and header json
    private JSONObject getBodyJSON(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", Config.getInstance().getUserID());
            if (key != null & value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getHeaderJSON() {
        JSONObject postHeader = new JSONObject();
        try {
            postHeader.put("access_token", Config.getInstance().getAccessToken());
            postHeader.put("rememberToken", Config.getInstance().getRememberToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postHeader;
    }
}