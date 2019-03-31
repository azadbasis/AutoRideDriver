package org.autoride.driver.networks.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.autoride.driver.configs.Config;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedBodyBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.autoride.driver.networks.parsers.DriverTaskParser;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class PerformDriverTask extends AsyncTask<String, Integer, DriverInfo> implements AppsConstants {

    private String TAG = "PerformDriverTask";
    private PerformRiderTaskListener taskListener;
    private OkHttpClient okHttpClient = Config.getInstance().getOkHttpClient();
    private String methods;
    private String url;
    private JSONObject postBody;

    public PerformDriverTask(String methods, String url, JSONObject postBody) {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            okHttpClient = builder.build();
            Config.getInstance().setOkHttpClient(okHttpClient);
        }
        this.methods = methods;
        this.url = url;
        this.postBody = postBody;
    }

    @Override
    protected DriverInfo doInBackground(String... strings) {
        String response = null;
        try {
            if (methods.equalsIgnoreCase(POST)) {
                response = CallApi.POST(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url),
                        RequestedBodyBuilder.buildRequestedBody(postBody)
                );
            } else if (methods.equalsIgnoreCase(GET)) {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url, postBody)
                );
            }
            Log.i(TAG, "ok_http_response " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DriverTaskParser.taskParse(response);
    }

    @Override
    protected void onPostExecute(DriverInfo result) {
        super.onPostExecute(result);
        Log.i(TAG, "result " + result);
        if (result != null) {
            taskListener.dataDownloadedSuccessfully(result);
        } else {
            taskListener.dataDownloadFailed(result);
        }
    }

    public PerformRiderTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(PerformRiderTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public interface PerformRiderTaskListener {

        void dataDownloadedSuccessfully(DriverInfo driverInfo);

        void dataDownloadFailed(DriverInfo driverInfo);
    }
}