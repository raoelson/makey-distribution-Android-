package com.example.makeyservice.makeydistribution.APiRest;

import com.example.makeyservice.makeydistribution.Model.Adresse;
import com.example.makeyservice.makeydistribution.Model.GeoAdresse;
import com.example.makeyservice.makeydistribution.Model.User;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by makeyservice on 16/05/2017.
 */

public interface ApiInterface {

    @POST("api/login/url/get")
    Call<JsonObject> getUrl(@Query("redirect_uri") String redirect_uri, @Query("state") String state);

    @POST("api/token/get")
    Call<JsonObject> getToken(@Query("redirect_uri") String redirect_uri, @Query("code") String code, @Query("state") String state);

    @GET("api/ocr/whoami")
    Call<User> getWho();

    @POST("api/token/refresh")
    Call<JSONObject> getRefreshToken(@Query("refresh_token") String refresh_token);

    @POST("api/ocr/predecouper_texte")
    Call<Object> getDecoupeTexte(@Query("image") String image, @Query("test") String test);

    //@Multipart
    @POST("api/ocr/predecouper_texte")
    @FormUrlEncoded
    //Call<JsonObject>getUpload(@Part MultipartBody.Part image,@Query("test") String test);
    Call<Adresse>getUpload(@Field("image") String image, @Query("test") String test);

    @GET("api/ocr/lister-planning")
    Call<JsonObject> getPlanning();

    @GET("api/ocr/lister-planning-tournees")
    Call<JsonObject> getTournee(@Query("id") Integer id);

    @GET("api/ocr/feuille-de-route")
    Call<List<JsonObject>> getFeuilleRoute(@Query("android") String android, @Query("idPlanning") Integer id
            , @Query("idTournee") Integer idTournee);

    @POST("api/ocr/saisie_statut")
    Call<JsonObject> getValide(@Query("android") String android, @Query("idStatut") String idStatut
            , @Query("idVisite") Integer idVisite, @Query("commentaire") String commentaire);

    @GET("logout")
    Call<Void> Deconnexion();

    @POST("api/ocr/geocoder_adresse")
    Call<GeoAdresse> getGeoAdresse(@Query("adresse") String adresse, @Query("cp") String cp
            , @Query("ville") String ville);
}
