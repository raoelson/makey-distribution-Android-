package com.example.makeyservice.makeydistribution.APiRest;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by makeyservice on 16/05/2017.
 */

public class ApiClient {
    public ApiClient() {

    }

    public Retrofit getClient(String url) {

        Retrofit retrofit = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception e) {
            Log.d("res","test"+e.getMessage());
            return null;
        }
        return retrofit;
    }

    public Retrofit getClientWithOkHttp(String url, OkHttpClient.Builder okHttpClient) {
        Retrofit retrofit = null;
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception e) {

            return null;
        }
        return retrofit;
    }
}
