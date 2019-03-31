package org.autoride.driver.networks.managers.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.Headers;

public class RequestedHeaderBuilder {

    private static String TAG = "tag";

    public static Headers buildRequestedHeader(JSONObject postHeaderData) {
        Log.i(TAG, "postData header " + postHeaderData);
        Headers.Builder headerBuilder = new Headers.Builder();
        Iterator<String> headerKey = postHeaderData.keys();
        while (headerKey.hasNext()) {
            String key = headerKey.next();
            try {
                Object headerValue = postHeaderData.get(key);
                headerBuilder.add(key, headerValue.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return headerBuilder.build();
    }

//    public static Request requestHeader(JSONObject postData) {
//        Log.i(TAG, "postData header " + postData);
//        return new Request.Builder()
//                .addHeader("access_token", "5e4afd16991008e9bb52cc326f75068cb7f78d2ef8411d2e8fc78fd1a0d2891e")
//                .addHeader("rememberToken", "775843b5a382dfb5a081b91d222d3dcfc87b898b75113df47d87c1709ede6be9")
//                .url(ApiUrl.UPDATE_DRIVER_STATUS_URL)
//                .build();
//    }
}