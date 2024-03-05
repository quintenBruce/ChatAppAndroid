package com.example.blankapplication.data.remote;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 */
public class OkHTTP {
    private OkHttpClient client = new OkHttpClient();

    public void AsyncRequest(Callback callback, Request request) {

        //execute the request synchronously. This block the thread.
        //client.newCall(request).execute();

        // .enqueue schedules the request to be executed at some point in the future.
        // responseCallBack will be called back when finished.
        //new Callback() {} is an anonymous class that implements the Callback interface

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(10, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(10, TimeUnit.SECONDS)   // Write timeout
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("OkHTTP!", Objects.requireNonNull(e.getMessage()));
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }
}
