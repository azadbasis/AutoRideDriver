package org.autoride.driver.networks.managers.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.HttpUrl;

public class RequestedUrlBuilder {

    private static String TAG = "tag";

    public static HttpUrl buildRequestedPOSTUrl(String url) {
        Log.i(TAG, "postData post url " + url);
        return HttpUrl.parse(url);
    }

    public static HttpUrl buildRequestedGETUrl(String url, JSONObject postUrlData) {
        Log.i(TAG, "postData get url " + postUrlData);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        Iterator<String> urlKey = postUrlData.keys();
        while (urlKey.hasNext()) {
            String key = urlKey.next();
            try {
                Object urlValue = postUrlData.get(key);
                urlBuilder.addQueryParameter(key, urlValue.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urlBuilder.build();
    }

//    public static HttpUrl buildUrlWithParameter(String protocol, String host) {
//        Log.i(TAG, "host " + host);
//        return new HttpUrl.Builder()
//                .scheme(protocol)   //https
//                .host(host)   // "www.somehostname.com"
//                .addPathSegment("/")   //adds "/pathSegment" at the end of hostname
//                .addQueryParameter("param", "value")   //add query parameters to the URL
//                .addEncodedQueryParameter("encodedName", "encodedValue")   //add encoded query parameters to the URL
//                .build();
//        /**
//         * The return URL:
//         *  https://www.somehostname.com/pathSegment?param1=value1&encodedName=encodedValue
//         */
//    }
//
//    public static HttpUrl.Builder buildUrl(String url) {
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//        return urlBuilder.addQueryParameter("userId", "");
//    }
}