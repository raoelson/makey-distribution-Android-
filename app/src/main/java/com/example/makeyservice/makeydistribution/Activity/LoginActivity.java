package com.example.makeyservice.makeydistribution.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.example.makeyservice.makeydistribution.APiRest.ApiClient;
import com.example.makeyservice.makeydistribution.APiRest.ApiInterface;
import com.example.makeyservice.makeydistribution.APiRest.RefreshToken;
import com.example.makeyservice.makeydistribution.Model.UrlModel;
import com.example.makeyservice.makeydistribution.Outils.Message;
import com.example.makeyservice.makeydistribution.Outils.ProgressBar;
import com.example.makeyservice.makeydistribution.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by makeyservice on 23/06/2017.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.txtUrl)
    EditText txturl;
    ApiClient apiClient;
    String url = "";
    Message message = null;
    SharedPreferences sharedpreferences;
    ProgressBar progressBar;
    RefreshToken refreshToken;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        message = new Message(this);
        apiClient = new ApiClient();
        refreshToken = new RefreshToken(getApplicationContext());
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String urlPref = sharedpreferences.getString("url",null);
        if(urlPref != null){
            txturl.setText(urlPref);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loadData(txturl.getText().toString());
                }
            };
            new Handler().postDelayed(runnable,100);
        }
    }



    @OnClick(R.id.btnValider)
    public void Onclick(){
        url = txturl.getText().toString();
        if(!url.equalsIgnoreCase("")){
            String uri = Uri.parse(url)
                    .buildUpon()
                    .build().toString();
            loadData(uri);
        }else{
            progressBar.Dismiss();
            message.Dialogue("Erreur", " Veuillez configurer d'abord votre url dans le paramètre !");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void loadData(final String url) {
        if(refreshToken.isConnectingToInternet()){
            progressBar = new ProgressBar(LoginActivity.this,"Makey Distribution","Chargement...");
            progressBar.Show();
            Retrofit retrofit = apiClient.getClient(url);
            if(retrofit == null){
                progressBar.Dismiss();
                message.Dialogue("Erreur","Votre url doit se terminer par / ");
                return;
            }
            if (!validate(url,txturl)) {
                return;
            }
            ApiInterface api = retrofit.create(ApiInterface.class);
            Call<JsonObject> call = api.getUrl("http://www.example.com","test");
            call.clone().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.code() == 500){
                        progressBar.Dismiss();
                        message.Dialogue("Erreur", " "+response.message());
                    }
                    if(response.code() == 404){
                        progressBar.Dismiss();
                        message.Dialogue("Erreur",""+response.message());
                    }else if(response.code() == 200){
                        Log.d("test"," - "+response.body());
                        String jsonString = response.body().toString();
                        Gson gson = new Gson();
                        UrlModel status = gson.fromJson(jsonString, UrlModel.class);
                        if(status.getMessage() != null){
                            progressBar.Dismiss();
                            message.Dialogue(status.getError(), status.getMessage());
                        }else{
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove("url");
                            editor.apply();
                            editor.putString("url",url);
                            editor.commit();
                            progressBar.Dismiss();
                            Intent intent = new Intent(LoginActivity.this,WebViewActivity.class);
                            intent.putExtra("url",String.valueOf(status.getUrl()));
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    try {
                        progressBar.Dismiss();
                        if(t instanceof TimeoutException){
                            message.Dialogue("Erreur", " Délai de chargement dépassé. Veuillez " +
                                    "réessayer svp !");
                            return;
                        }
                        message.Dialogue("Erreur", " Veuillez vérifer votre url svp!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            message.Dialogue("Erreur","Veuillez vérifier votre connection svp!");
        }
    }
    public boolean validate(String config,EditText edit) {
        boolean valid = true;
        if (config.isEmpty() || !Patterns.WEB_URL.matcher(config).matches()) {
            edit.setError("Veuillez vérifier le format de votre url svp!");
            valid = false;
        } else {
            edit.setError(null);
        }


        return valid;
    }

}
