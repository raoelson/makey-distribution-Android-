package com.example.makeyservice.makeydistribution.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.makeyservice.makeydistribution.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by makeyservice on 14/06/2017.
 */

public class ProfilFragment extends Fragment {

    SharedPreferences sharedPreferences = null;
    @BindView(R.id.user_connecte)
    TextView userConnecte;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil_fragment, container, false);
        ButterKnife.bind(this, view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userConnecte.setText(sharedPreferences.getString("profile_user", null));
        txtEmail.setText(sharedPreferences.getString("profile_userEmail", null));
        return view;
    }
}
