package org.autoride.driver.networks.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.autoride.driver.configs.Config;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.networks.managers.api.CallApi;
import org.autoride.driver.networks.managers.api.RequestedBodyBuilder;
import org.autoride.driver.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.driver.networks.managers.api.RequestedUrlBuilder;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class PerformStringResponseTask extends AsyncTask<String, Integer, String> implements AppsConstants {

    private String TAG = "PerformRiderTask";
    private PerformStringResponseTaskListener taskListener;
    private OkHttpClient okHttpClient = Config.getInstance().getOkHttpClient();
    private String methods;
    private String url;
    private JSONObject postBody;
    private JSONObject postHeader;

    public PerformStringResponseTask(String methods, String url, JSONObject postBody, JSONObject postHeader) {
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
        this.postHeader = postHeader;
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = null;
        try {
            if (methods.equalsIgnoreCase(POST) && postHeader == null) {
                response = CallApi.POST(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url),
                        RequestedBodyBuilder.buildRequestedBody(postBody)
                );
            } else if (methods.equalsIgnoreCase(GET) && postHeader == null) {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url, postBody)
                );
            } else if (methods.equalsIgnoreCase(POST) && postHeader != null) {
                response = CallApi.POST(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url),
                        RequestedBodyBuilder.buildRequestedBody(postBody),
                        RequestedHeaderBuilder.buildRequestedHeader(postHeader)
                );
            } else if (methods.equalsIgnoreCase(GET) && postHeader != null) {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url, postBody),
                        RequestedHeaderBuilder.buildRequestedHeader(postHeader)
                );
            }
            Log.i(TAG, "ok_http_response " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i(TAG, "result " + result);
        if (result != null) {
            taskListener.dataDownloadedSuccessfully(result);
        } else {
            taskListener.dataDownloadFailed(result);
        }
    }

    public PerformStringResponseTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(PerformStringResponseTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public interface PerformStringResponseTaskListener {

        void dataDownloadedSuccessfully(String driverInfo);

        void dataDownloadFailed(String driverInfo);
    }
}