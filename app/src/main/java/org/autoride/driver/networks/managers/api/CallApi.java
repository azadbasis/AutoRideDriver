package org.autoride.driver.networks.managers.api;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CallApi {

    // get with out header
    public static String GET(OkHttpClient client, HttpUrl url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request).execute().body().string();
    }

    // get with header
    public static String GET(OkHttpClient client, HttpUrl url, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        return client.newCall(request).execute().body().string();
    }

    // post with out header
    public static String POST(OkHttpClient client, HttpUrl url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }

    // post with header
    public static String POST(OkHttpClient client, HttpUrl url, RequestBody body, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }

    // post with multipart body
    public static String POST(OkHttpClient client, HttpUrl url, MultipartBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }

    // post with multipart body and header
    public static String POST(OkHttpClient client, HttpUrl url, MultipartBody body, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }
}