package org.autoride.driver.networks.managers.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestedBodyBuilder {

    private static String TAG = "tag";

    public static RequestBody buildRequestedBody(JSONObject postBodyData) {
        Log.i(TAG, "postData body " + postBodyData);
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        Iterator<String> bodyKey = postBodyData.keys();
        while (bodyKey.hasNext()) {
            String key = bodyKey.next();
            try {
                Object bodyValue = postBodyData.get(key);
                bodyBuilder.add(key, bodyValue.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bodyBuilder.build();
    }

    public static MultipartBody buildRequestedMultipartBody(String title, String imageFormat, File file) {
        Log.i(TAG, "postData file body " + file);
        MediaType MEDIA_TYPE = MediaType.parse("image/" + imageFormat); // e.g. "image/png"
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filename", title) //e.g. title.png --> imageFormat = png
                .addFormDataPart("file", "...", RequestBody.create(MEDIA_TYPE, file))
                .build();
    }

//    public static RequestBody requestedBody(JSONObject postBodyData) {
//        Log.i(TAG, "postData " + postBodyData);
//        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBodyData.toString());
//    }

//    public static RequestBody requestBody(String username, String password, String token) {
//        Log.i(TAG, "postData " + username);
//        return new FormBody.Builder()
//                .add("action", "login")
//                .add("format", "json")
//                .add("username", username)
//                .add("password", password)
//                .add("login_token", token)
//                .build();
//    }
}