package com.example.makeyservice.makeydistribution.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.example.makeyservice.makeydistribution.APiRest.ApiClient;
import com.example.makeyservice.makeydistribution.APiRest.ApiInterface;
import com.example.makeyservice.makeydistribution.APiRest.RefreshToken;
import com.example.makeyservice.makeydistribution.Model.Token;
import com.example.makeyservice.makeydistribution.Model.UrlModel;
import com.example.makeyservice.makeydistribution.Model.User;
import com.example.makeyservice.makeydistribution.Outils.Message;
import com.example.makeyservice.makeydistribution.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by makeyservice on 23/06/2017.
 */

public class WebViewActivity extends AppCompatActivity {
    private String MAKEY_CALLBACK_url = "http://www.example.com";
    private String MAKEY_CALLBACK_user = "oauth/v2/fos_user_security_login";
    private Message message;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.progress)
    android.widget.ProgressBar pB;
    @BindView(R.id.framelayout)
    FrameLayout frameLayout;
    SharedPreferences sharedpreferences;
    Handler handler;
    ApiClient apiClient;
    RefreshToken refreshToken;
    Boolean fosUser = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        ButterKnife.bind(this);
        apiClient = new ApiClient();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        message = new Message(this);
        handler = new Handler();
        refreshToken = new RefreshToken(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(refreshToken.isConnectingToInternet()) {
            pB.setMax(100);
            Bundle extras = getIntent().getExtras();

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDefaultFontSize((int)getResources()
                    .getDimension(R.dimen.activity_horizontal_margin));
            webView.setWebViewClient(new WebClient());
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    frameLayout.setVisibility(View.VISIBLE);
                    pB.setProgress(newProgress);
                    setTitle("Loading ...");
                    if (newProgress == 100) {
                        frameLayout.setVisibility(View.GONE);
                        setTitle(view.getTitle());
                    }
                }
            });
            String url = extras.getString("url");
            CookieSyncManager.createInstance(getApplicationContext());
            CookieSyncManager.getInstance().startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            webView.loadUrl(url);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("urlRedirect", extras.getString("url"));
            editor.commit();
            pB.setProgress(0);
        }else{
            message.Dialogue("Erreur","Veuillez vérifier votre connection svp!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class WebClient extends WebViewClient {
        String authCode;
        String State;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            frameLayout.setVisibility(View.VISIBLE);
            if(url.contains(MAKEY_CALLBACK_user) && fosUser ==false){
                Call<JsonObject> call = getUserFix(sharedpreferences.getString("url",null));
                call.clone().enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.code() == 404){
                            message.Dialogue("Erreur",""+response.message());

                        }else{
                            String jsonString = response.body().toString();
                            Gson gson = new Gson();
                            UrlModel status = gson.fromJson(jsonString, UrlModel.class);
                            if(status.getMessage() != null){
                                message.Dialogue(status.getError(), status.getMessage());
                            }else{
                                Intent intent = new Intent(WebViewActivity.this,LoginActivity.class);
                                //intent.putExtra("url",String.valueOf(status.getUrl()));
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        try {
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
            }
            if(url.startsWith(MAKEY_CALLBACK_url)){
                Uri uri = Uri.parse(url);
                authCode = uri.getQueryParameter("code");
                State = uri.getQueryParameter("state");
                if(authCode != "undefined"){
                    final Retrofit retrofit = apiClient.getClient(sharedpreferences.getString("url",null));
                    ApiInterface api = retrofit.create(ApiInterface.class);
                    Call<JsonObject> call = api.getToken(MAKEY_CALLBACK_url,authCode,State);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if(response.body() == null){
                                startActivity(new Intent(WebViewActivity.this, LoginActivity.class));
                            }else{
                                String jsonString = response.body().toString();
                                Gson gson = new Gson();
                                Token token = gson.fromJson(jsonString, Token.class);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                long limit_token = Calendar.getInstance().getTimeInMillis() + 3300000;
                                if(sharedpreferences.getString("acces_token",null) != null){
                                    editor.remove("refresh_token");
                                    editor.remove("acces_token");
                                    editor.remove("limit_token");
                                    editor.apply();
                                }
                                editor.putString("refresh_token", token.getRefresh_token());
                                editor.putString("acces_token", token.getAccess_token());
                                editor.putString("limit_token", ""+limit_token);
                                editor.commit();
                                getRecupereUser(token.getAccess_token());
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            message.Dialogue("Erreur","Acces denied");
                        }
                    });

                }else{
                    message.Dialogue("Erreur","Acces denied");
                }
            }else{
                view.loadUrl(url);
            }
            return true;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            webView.setVisibility(View.VISIBLE);
            if(webView.canGoBack()){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getRecupereUser(final String token){
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String authorization = new StringBuilder("Bearer ").append(token).toString();
                Request.Builder newRequest = request.newBuilder()
                        .addHeader("Content-Type","Application/json")
                        .addHeader("Authorization",authorization);
                return chain.proceed(newRequest.build());
            }
        });
        Retrofit retrofit_ = apiClient.getClientWithOkHttp(sharedpreferences.getString("url",null),
                okHttpClient);
        ApiInterface api_ = retrofit_.create(ApiInterface.class);
        Call<User> call_ = api_.getWho();
        call_.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(sharedpreferences.getString("profile_user",null) !=null){
                    editor.remove("profile_user");
                    editor.remove("profile_userEmail");
                    editor.apply();
                }
                editor.putString("profile_user",response.body().getUsername());
                editor.putString("profile_userEmail",response.body().getEmail());
                editor.commit();
                startActivity(new Intent(WebViewActivity.this,AccueilActivity.class));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("refresh_token");
                editor.remove("acces_token");
                editor.remove("limit_token");
                editor.apply();
                message.Dialogue("Erreur","Erreur de récupérer votre profil");
            }
        });
    }

    private Call<JsonObject> getUserFix(final String url){

        if(refreshToken.isConnectingToInternet()){

            Retrofit retrofit = apiClient.getClient(url);
            if(retrofit == null){
                message.Dialogue("Erreur","Votre url doit se terminer par / ");
            }
            ApiInterface api = retrofit.create(ApiInterface.class);
            return api.getUrl("http://www.example.com","test");
        }else{
            message.Dialogue("Erreur","Veuillez vérifier votre connection svp!");
        }
        return null;
    }
}