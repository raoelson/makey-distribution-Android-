package com.example.makeyservice.makeydistribution.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.makeyservice.makeydistribution.APiRest.ApiClient;
import com.example.makeyservice.makeydistribution.APiRest.ApiInterface;
import com.example.makeyservice.makeydistribution.APiRest.RefreshToken;
import com.example.makeyservice.makeydistribution.Adapter.ListPlanningAdapter;
import com.example.makeyservice.makeydistribution.Model.Tournee;
import com.example.makeyservice.makeydistribution.Outils.Message;
import com.example.makeyservice.makeydistribution.Outils.ProgressBar;
import com.example.makeyservice.makeydistribution.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class FeuilllesFragment extends Fragment {
    SharedPreferences sharedPreferences = null;
    RefreshToken refreshToken;
    ApiClient apiClient;
    String token = null;
    Retrofit retrofit_ = null;
    OkHttpClient.Builder okHttpClient;
    @BindView(R.id.spinnerPalnning)
    Spinner spPlanning;
    @BindView(R.id.spinnerIDPalnning)
    Spinner spIDPlanning;
    @BindView(R.id.spinnerTournee)
    Spinner spTournee;
    @BindView(R.id.spinnerIDTournee)
    Spinner spinnerIDTournee;
    @BindView(R.id.btnAffichage)
    Button btnAffichage;
    @BindView(R.id.btnTest)
    Button btnAdresse;
    ProgressBar progressBar;
    @BindView(R.id.myrecyclerview)
    RecyclerView mRecyclerView;
    private Message message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feuilles_fragment, container, false);
        ButterKnife.bind(this, v);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        apiClient = new ApiClient();
        message = new Message(getContext());
        progressBar = new ProgressBar(getContext(), "Makey Distribution", "Chargement...");
        refreshToken = new RefreshToken(getContext());
        token = sharedPreferences.getString("acces_token", null);
        okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String authorization = new StringBuilder("Bearer ").append(token).toString();
                Request.Builder newRequest = request.newBuilder()
                        .addHeader("Content-Type", "Application/json")
                        .addHeader("Authorization", authorization);
                return chain.proceed(newRequest.build());
            }
        });

        retrofit_ = apiClient.getClientWithOkHttp(sharedPreferences.getString("url", null),
                okHttpClient);
        LoadPlanning();
        spPlanning.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().
                        equalsIgnoreCase("Aucun planning n'a été trouvé")){
                    spPlanning.getSelectedView().setEnabled(true);
                }else{
                    progressBar.Show();
                    spIDPlanning.setSelection(position);
                    getTournee(spIDPlanning.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spTournee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerIDTournee.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAffichage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerIDTournee.getSelectedItem() != null &&
                        spIDPlanning.getSelectedItem() != null){
                    AffichageList();
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(refreshToken.TempsToken() == false){
            Intent intent = new Intent(getActivity().getApplicationContext(),WebViewActivity.class);
            intent.putExtra("url",sharedPreferences.getString("urlRedirect",null));
            startActivity(intent);
        }else{

        }*/
    }

    private void LoadPlanning() {
        progressBar.Show();
        final List<String> keys = new ArrayList<String>();
        ApiInterface api_ = retrofit_.create(ApiInterface.class);
        Call<JsonObject> call = api_.getPlanning();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Log.d("test"," "+response);
                JsonParser parser = new JsonParser();
                JsonObject jObj = (JsonObject) parser.parse(String.valueOf(response.body()));
                List<String> keys_id = new ArrayList<String>();
                for (Map.Entry<String, JsonElement> e : jObj.entrySet()) {
                    if (!e.getValue().getAsString().isEmpty()) {
                        keys.add(e.getValue().getAsString());
                        keys_id.add(e.getKey().toString());
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, keys_id);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spIDPlanning.setAdapter(dataAdapter);
                if(keys.isEmpty()){
                    keys.add("Aucun planning n'a été trouvé");
                    spTournee.setVisibility(View.INVISIBLE);
                }
                ArrayAdapter<String> dataAdapterID = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, keys);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPlanning.setAdapter(dataAdapterID);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(keys.isEmpty()){
                    keys.add("Aucun planning n'a été trouvé");
                    spPlanning.setEnabled(true);
                    spTournee.setVisibility(View.INVISIBLE);
                }
                ArrayAdapter<String> dataAdapterID = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, keys);
                spPlanning.setAdapter(dataAdapterID);
                progressBar.Dismiss();
            }
        });
    }

    private void getTournee(String id) {
        ApiInterface api_ = retrofit_.create(ApiInterface.class);
        Call<JsonObject> call = api_.getTournee(Integer.parseInt(id));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonParser parser = new JsonParser();
                JsonObject jObj = (JsonObject) parser.parse(String.valueOf(response.body()));
                List<String> keys = new ArrayList<String>();
                List<String> keys_id = new ArrayList<String>();
                for (Map.Entry<String, JsonElement> e : jObj.entrySet()) {
                    if (!e.getKey().equalsIgnoreCase("0")) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(e.getValue().toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        Iterator<String> it = jsonObject.keys();
                        String nom_concat = "";
                        String id_concat = "";
                        while (it.hasNext()) {
                            String key = it.next();
                            Object value = null;
                            try {
                                value = jsonObject.get(key);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            if (key.toString().equalsIgnoreCase("tournee_lib")) {
                                if (!value.toString().equalsIgnoreCase("null")) {
                                    nom_concat += "" + value.toString();
                                }
                            } else if (key.toString().equalsIgnoreCase("nom")) {
                                nom_concat += " ( " + value.toString();
                            } else if (key.toString().equalsIgnoreCase("prenom")) {
                                nom_concat += " " + value.toString() + ")";
                            } else if (key.toString().equalsIgnoreCase("tournee_id")) {
                                id_concat += value.toString();
                            }
                        }
                        keys.add(nom_concat);
                        keys_id.add(id_concat);

                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, keys);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTournee.setAdapter(dataAdapter);

                ArrayAdapter<String> dataAdapterId = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, keys_id);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerIDTournee.setAdapter(dataAdapterId);
                progressBar.Dismiss();
                if(spinnerIDTournee.getSelectedItem() != null &&
                        spIDPlanning.getSelectedItem() != null){
                    AffichageList();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.Dismiss();
            }
        });
    }

    private void AffichageList() {
        progressBar.Show();
        ApiInterface api_ = retrofit_.create(ApiInterface.class);
        Call<List<JsonObject>> call = api_.getFeuilleRoute("android", Integer.parseInt(spIDPlanning.getSelectedItem()
                .toString()), Integer.parseInt(spinnerIDTournee.getSelectedItem().toString()));
        call.clone().enqueue(new Callback<List<JsonObject>>() {
            List<Tournee> tournees = new ArrayList<Tournee>();

            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                JsonObject getReponse0 = response.body().get(0);
                JsonObject getReponse1 = response.body().get(1);
                JsonObject getReponse3 = response.body().get(3);
                for (Map.Entry<String, JsonElement> e : getReponse0.entrySet()) {
                    try {
                        JSONObject jsonObject = new JSONObject(e.getValue().toString());
                        Iterator<String> it = jsonObject.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            Object value = jsonObject.get(key);

                            JSONObject jsonObject0 = new JSONObject(value.toString());
                            Iterator<String> it0 = jsonObject0.keys();
                            while (it0.hasNext()) {
                                String key0 = it0.next();
                                Object value0 = jsonObject0.get(key0);

                                JSONObject jsonObject1 = new JSONObject(value0.toString());
                                Iterator<String> iterator = jsonObject1.keys();
                                Tournee tournee = new Tournee();
                                while (iterator.hasNext()) {
                                    String key01 = iterator.next();
                                    Object value01 = jsonObject1.get(key01);
                                    if (key01.toString().equalsIgnoreCase("tourn_id")) {
                                        tournee.setTourn_id(Integer.parseInt(value01.toString()));
                                    } else if (key01.toString().equalsIgnoreCase("tourn_lib")) {
                                        tournee.setTourn_lib(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("prod_id")) {
                                        tournee.setProd_id(Integer.parseInt(value01.toString()));
                                    } else if (key01.toString().equalsIgnoreCase("prod_lib")) {
                                        tournee.setProd_lib(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("vis_id")) {
                                        tournee.setVis_id(Integer.parseInt(value01.toString()));
                                    } else if (key01.toString().equalsIgnoreCase("vis_quant")) {
                                        tournee.setTourn_lib(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("pl_id")) {

                                    } else if (key01.toString().equalsIgnoreCase("p_libel")) {
                                        tournee.setP_libel(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("stop_id")) {

                                    } else if (key01.toString().equalsIgnoreCase("nom")) {
                                        tournee.setNom(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("street")) {
                                        tournee.setStreet(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("city")) {
                                        tournee.setCity(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("cp")) {
                                        tournee.setCp(Integer.parseInt(value01.toString()));
                                    } else if (key01.toString().equalsIgnoreCase("type_visite")) {
                                        tournee.setType_visite((value01.toString()));
                                    } else if (key01.toString().equalsIgnoreCase("type_code")) {
                                        tournee.setType_code(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("vcptrs_libelle")) {
                                        tournee.setVcptrs_libelle(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("vcptrs_id")) {
                                        tournee.setVcptrs_id(value01.toString());
                                    } else if (key01.toString().equalsIgnoreCase("vis_id")) {
                                        tournee.setVis_id(Integer.parseInt(value01.toString()));
                                    }
                                    else if(key01.toString().equalsIgnoreCase("v_comment_val")){
                                        tournee.setV_comment_val(value01.toString());
                                    }else if(key01.toString().equalsIgnoreCase("ordre_stop")){
                                        tournee.setOrdre_stop(Integer.parseInt(value01.toString()));
                                    }                      }
                                tournees.add(tournee);
                                Collections.sort(tournees);
                            }

                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                mRecyclerView.setHasFixedSize(false);
                ListPlanningAdapter adapter = new ListPlanningAdapter(getContext(), tournees, getReponse3,
                        Integer.parseInt(spIDPlanning.getSelectedItem()
                                .toString()), Integer.parseInt(spinnerIDTournee.getSelectedItem().toString()));
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                adapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(adapter);

                //colis adresse + nom personne
                String adresse = "";
                for (Map.Entry<String, JsonElement> e1 : getReponse1.entrySet()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(e1.getValue().toString());
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    Iterator<String> it1 = jsonObject1.keys();
                    while (it1.hasNext()) {
                        String key = it1.next();
                        Object value = null;
                        try {
                            value = jsonObject1.get(key);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        if (key.toString().equalsIgnoreCase("depot_libelle")) {
                            adresse += value.toString();
                        } else if (key.toString().equalsIgnoreCase("depot_city")) {
                            adresse += "\n" + value.toString();
                        }
                    }

                }
                btnAdresse.setText(adresse);
                btnAdresse.setVisibility(View.VISIBLE);

                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                progressBar.Dismiss();
                Toast.makeText(getContext(), "Tournée vide", Toast.LENGTH_SHORT).show();
                btnAdresse.setVisibility(View.INVISIBLE);
                mRecyclerView.setHasFixedSize(false);
                JsonObject ob = null;
                ListPlanningAdapter adapter = new ListPlanningAdapter(getContext(), tournees, ob, 0, 0);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }
}
