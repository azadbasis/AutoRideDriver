package org.autoride.driver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.configs.Config;
import org.autoride.driver.driver.net.DataManager;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.listeners.DriverListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppsKilledStatusService extends Service implements DriverApiUrl {

    private static final String TAG = "TAG";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "apps_status " + "onDestroy_Service");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // super.onTaskRemoved(rootIntent);
        updateDriverStatus();
        Log.i(TAG, "apps_status " + "onTaskRemoved");
    }

    public void performUpdateDriverStatus() {
        JSONObject postData = getForgetPasJSObj();
        DataManager.driverStatusUpdate(postData, new DriverListener() {
            @Override
            public void onLoadCompleted(AuthBean authBean) {
                Log.i(TAG, "apps_status " + authBean.getErrorMsg());
            }

            @Override
            public void onLoadFailed(AuthBean authBean) {
                Log.i(TAG, "apps_status " + authBean.getErrorMsg());
            }
        });
    }

    public JSONObject getForgetPasJSObj() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("userId", Config.getInstance().getUserID());
            postData.put("status", "Inactive");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void updateDriverStatus() {

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("userId", Config.getInstance().getUserID())
                .add("status", "Inactive")
                .build();

        Request request = new Request.Builder()
                .url(UPDATE_DRIVER_STATUS_URL)
                .post(formBody)
                .addHeader("access_token", Config.getInstance().getAccessToken())
                .addHeader("rememberToken", Config.getInstance().getRememberToken())
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, "response " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "response " + response.toString());
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "response " + e.toString());
            e.printStackTrace();
        }
    }
}