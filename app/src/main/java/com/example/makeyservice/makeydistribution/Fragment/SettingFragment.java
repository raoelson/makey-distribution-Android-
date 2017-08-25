package com.example.makeyservice.makeydistribution.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.makeyservice.makeydistribution.Activity.LoginActivity;
import com.example.makeyservice.makeydistribution.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makeyservice on 20/06/2017.
 */

public class SettingFragment extends Fragment {
    SharedPreferences sharedPreferences;
    @BindView(R.id.txtUrlConfig)
    EditText txtUrlConfig;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        ButterKnife.bind(this, view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(sharedPreferences.getString("url",null) !=null){
            txtUrlConfig.setText(sharedPreferences.getString("url",null));
        }
        return view;
    }

    @OnClick(R.id.btnValiderConfig)
    public void OnSave(){
        if (!validate(txtUrlConfig.getText().toString(),
                txtUrlConfig)) {
            return;
        }else{
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            if(!sharedPreferences.getString("url",null).equalsIgnoreCase(txtUrlConfig.getText().toString())){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Info");
                builder.setMessage("Accepteriez-vous redigirer vers la page  d'authentification \n " +
                        "après la modification ?");
                //Button One : Yes
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(sharedPreferences.getString("url",null) !=null){
                            editor.remove("url");
                            editor.apply();
                        }
                        editor.putString("url",txtUrlConfig.getText().toString());
                        editor.commit();
                        txtUrlConfig.setText(sharedPreferences.getString("url",null) );
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });


                //Button Two : No
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog diag = builder.create();
                diag.show();
            }
            /*if(sharedPreferences.getString("url",null) !=null){
                editor.remove("url");
                editor.apply();
            }
            editor.putString("url",txtUrlConfig.getText().toString());
            editor.commit();
            txtUrlConfig.setText(sharedPreferences.getString("url",null) );*/
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
