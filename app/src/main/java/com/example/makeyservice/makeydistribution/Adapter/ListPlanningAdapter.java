package com.example.makeyservice.makeydistribution.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.makeyservice.makeydistribution.APiRest.ApiClient;
import com.example.makeyservice.makeydistribution.APiRest.ApiInterface;
import com.example.makeyservice.makeydistribution.Model.Tournee;
import com.example.makeyservice.makeydistribution.Outils.ProgressBar;
import com.example.makeyservice.makeydistribution.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

public class ListPlanningAdapter extends RecyclerView.Adapter<ListPlanningAdapter.ViewHolder> {

    // region Member Variables
    private Context mContext;
    private List<Tournee> mTournee;
    private int lastPosition = 0;
    private JsonObject getResponse;
    private Integer idPlanning, idTounee;
    private ProgressBar progressBar;
    // endregion

    // region Constructors
    public ListPlanningAdapter(Context context, List<Tournee> tournees, JsonObject getResponse3,
                               Integer idPlanning, Integer idTournee) {
        mContext = context;
        mTournee = tournees;
        getResponse = getResponse3;
        this.idPlanning = idPlanning;
        this.idTounee = idTournee;
        // endregion
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feuilles, parent, false);
        ViewHolder vh = new ViewHolder(v);
        progressBar = new ProgressBar(mContext,"Makey Distribution","Chargement...");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position % 2 ==0 )
            holder.itemView.setBackgroundResource(R.drawable.listviews);
        else
            holder.itemView.setBackgroundResource(R.drawable.listview);
        final Tournee tournee = mTournee.get(position);
        if (tournee != null) {
            setUpDisplayName(holder.tvName, tournee);
            setUpStreet(holder.tvStreet, tournee);
            setUpDisplayCp(holder.tvCp, tournee);
            setUpDisplayType(holder.tvType, tournee);
            setUpDisplayQte(holder.tvQte, tournee);
            setUpDisplayButton(holder.btnSelecte,tournee);
        }

        holder.btnSelecte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                UpdateStatut(tournee, getResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTournee.size();
    }

    private void setUpDisplayName(TextView tv, Tournee tournee) {
        String displayName = String.valueOf(tournee.getNom());
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(Html.fromHtml("<b>" + displayName + "</b> "));
        }
    }


    private void setUpStreet(TextView tv, Tournee tournee) {
        String displayName = tournee.getStreet();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayCp(TextView tv, Tournee tournee) {
        String displayName = (tournee.getCp()) + " " +
                tournee.getCity();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayType(TextView tv, Tournee tournee) {
        String displayName = tournee.getType_visite();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText("Type : "+displayName);
        }
    }

    private void setUpDisplayQte(TextView tv, Tournee tournee) {
        String displayName = tournee.getProd_lib();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText("Qté    : "+displayName);
        }
    }

    private void setUpDisplayButton(Button btn, Tournee tournee) {
        if (tournee.getVcptrs_libelle().equalsIgnoreCase("null")) {
            btn.setBackgroundResource(R.drawable.btn_desactive);
        }else{
            btn.setBackgroundResource(R.drawable.btn_active);

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNam)
        TextView tvName;
        @BindView(R.id.tvStreet)
        TextView tvStreet;
        @BindView(R.id.tvCp)
        TextView tvCp;
        @BindView(R.id.tvType)
        TextView tvType;
        @BindView(R.id.tvQte)
        TextView tvQte;
        @BindView(R.id.btnSelected)
        Button btnSelecte;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    String token = null;
    Boolean testShow = false;
    String selectedtext;
    Retrofit retrofit_ = null;
    RadioButton radioButton, radioBtn;

    private void UpdateStatut(final Tournee tournee, JsonObject getReponse) {
        final RadioGroup radioGroup;
        Button btnButton;
        final EditText editCommentaire;
        final TextView ajoutCommentaire;
        SharedPreferences sharedPreferences = null;
        ApiClient apiClient;
        OkHttpClient.Builder okHttpClient;
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_dialog);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        apiClient = new ApiClient();
        radioGroup = (RadioGroup) dialog.findViewById(R.id.radioAction);
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
        String str = "<i><u> Ajouter un commentaire </u></i>";
        ajoutCommentaire = (TextView) dialog.findViewById(R.id.ajoutCommentaire);
        editCommentaire = (EditText) dialog.findViewById(R.id.editCommentaire);
        if(!tournee.getV_comment_val().equalsIgnoreCase("null")){
            editCommentaire.setText(tournee.getV_comment_val());
        }
        ajoutCommentaire.setText(Html.fromHtml(str));
        Affichage(tournee, getReponse, radioGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
        selectedtext = (String) radioButton.getText();
        btnButton = (Button) dialog.findViewById(R.id.btnDialog);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                radioBtn = (RadioButton) dialog.findViewById(checkedRadioButtonId);
                selectedtext = (String) radioBtn.getText();

                if(selectedtext.equalsIgnoreCase("Ne pas enregistrer")){
                    editCommentaire.setVisibility(View.GONE);
                    testShow = false;
                }
            }
        });
        ajoutCommentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedtext.equalsIgnoreCase("Ne pas enregistrer")) {
                    if (testShow) {
                        editCommentaire.setVisibility(View.GONE);
                        testShow = false;
                    } else {
                        editCommentaire.setVisibility(View.VISIBLE);
                        testShow = true;
                    }
                } else {
                    editCommentaire.setVisibility(View.GONE);
                    testShow = false;
                }
            }
        });

        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                Call<JsonObject> call;
                ApiInterface api_ = retrofit_.create(ApiInterface.class);
                if(selectedtext.equalsIgnoreCase("Ne pas enregistrer")){
                    call = api_.getValide("android",
                            "-1", tournee.getVis_id(),
                            editCommentaire.getText().toString());
                }else{
                    call = api_.getValide("android",
                            selectedtext, tournee.getVis_id(),
                            editCommentaire.getText().toString());
                }

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        AffichageList();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("test", " fail");
                        dialog.dismiss();
                    }
                });
            }
        });
        progressBar.Dismiss();
        dialog.show();

    }

    private void Affichage(Tournee tournee, JsonObject getReponse, RadioGroup radioGroup) {
        int i = 0;
        List<String> data = new ArrayList<>();
        for (Map.Entry<String, JsonElement> e3 : getReponse.entrySet()) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(e3.getValue().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = null;
                try {
                    value = jsonObject.get(key);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (e3.getKey().equalsIgnoreCase(tournee.getType_code())) {
                    data.add(value.toString());
                }

            }
        }
        createRadioButton(data, radioGroup, tournee);

    }

    private void createRadioButton(List<String> data, RadioGroup radioGroup, Tournee tournee) {
        final RadioButton[] rb = new RadioButton[data.size() + 1];
        radioGroup.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for (int i = 0; i < data.size() + 1; i++) {
            if (i < data.size()) {
                rb[i] = new RadioButton(mContext);
                rb[i].setText(data.get(i));
                rb[i].setId(i + 100);
                if (tournee.getVcptrs_libelle().equalsIgnoreCase(data.get(i))) {
                    rb[i].setChecked(true);
                } else {
                    if (i == 0) {
                        rb[i].setChecked(true);
                    }
                }
            }
            if (i == data.size()) {
                rb[i] = new RadioButton(mContext);
                rb[i].setText("Ne pas enregistrer");
                rb[i].setId(i + 100);
            }
            radioGroup.addView(rb[i]);
        }
    }


    private void AffichageList() {
        ApiInterface api_ = retrofit_.create(ApiInterface.class);
        Call<List<JsonObject>> call = api_.getFeuilleRoute("android", idPlanning, idTounee);
        call.clone().enqueue(new Callback<List<JsonObject>>() {
            List<Tournee> tournees = new ArrayList<Tournee>();

            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                JsonObject getReponse0 = response.body().get(0);
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
                                    }else if(key01.toString().equalsIgnoreCase("v_comment_val")){
                                        tournee.setV_comment_val(value01.toString());
                                    }else if(key01.toString().equalsIgnoreCase("ordre_stop")){
                                        tournee.setOrdre_stop(Integer.parseInt(value01.toString()));
                                    }
                                }
                                tournees.add(tournee);
                                Collections.sort(tournees);

                            }

                        }
                        mTournee.clear();
                        mTournee.addAll(tournees);
                        Collections.sort(mTournee);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                notifyItemChanged(lastPosition);
                notifyDataSetChanged();
                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                progressBar.Dismiss();
            }
        });
    }

}
