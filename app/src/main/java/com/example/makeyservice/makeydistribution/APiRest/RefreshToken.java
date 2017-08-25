package com.example.makeyservice.makeydistribution.APiRest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by makeyservice on 24/05/2017.
 */

public class RefreshToken {
    private SharedPreferences sharedPreferences;
    private ApiClient apiClient;
    private Context context;
    public RefreshToken(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public Boolean TempsToken(){
        if(sharedPreferences.getString("acces_token",null) !=null){
            long limit_token = Long.parseLong(sharedPreferences.getString("limit_token",null));
            System.out.print(limit_token);
            long date_now = Calendar.getInstance().getTimeInMillis();
            if(date_now < limit_token){
                return true;
            }
        }

        return false;
    }
    public String getRefreshToken(){
        apiClient = new ApiClient();
        if(sharedPreferences.getString("acces_token",null) !=null){
            Retrofit retrofit_ = apiClient.getClient(sharedPreferences.getString("url",null));
            ApiInterface api = retrofit_.create(ApiInterface.class);
            Call<JSONObject> call = api.getRefreshToken(sharedPreferences.getString("refresh_token"
                    ,null));
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    Log.d("retour token"," "+response.body());
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Log.d("retour token"," fail");
                }
            });
            return null;
        }
        return null;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
