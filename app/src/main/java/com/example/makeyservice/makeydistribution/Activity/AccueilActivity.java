package com.example.makeyservice.makeydistribution.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.makeyservice.makeydistribution.APiRest.ApiClient;
import com.example.makeyservice.makeydistribution.APiRest.ApiInterface;
import com.example.makeyservice.makeydistribution.APiRest.RefreshToken;
import com.example.makeyservice.makeydistribution.Fragment.FeuilllesFragment;
import com.example.makeyservice.makeydistribution.Fragment.ListOCRFragment;
import com.example.makeyservice.makeydistribution.Fragment.ProfilFragment;
import com.example.makeyservice.makeydistribution.Fragment.ScannerFragment;
import com.example.makeyservice.makeydistribution.Fragment.SettingFragment;
import com.example.makeyservice.makeydistribution.Outils.ProgressBar;
import com.example.makeyservice.makeydistribution.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccueilActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView profilEmail;
    TextView profilConnecte;

    SharedPreferences sharedPreferences;
    RefreshToken refreshToken;
    ApiClient apiClient;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        apiClient = new ApiClient();
        progressBar = new ProgressBar(AccueilActivity.this, "Makey Distribution", "Déconnexion...");
        refreshToken = new RefreshToken(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        View headerLayout =
                navigationView.getHeaderView(0);
        profilConnecte = (TextView) headerLayout.findViewById(R.id.profile_connecte);
        profilEmail = (TextView) headerLayout.findViewById(R.id.profile_email);
        profilConnecte.setText(sharedPreferences.getString("profile_user", null));
        profilEmail.setText(sharedPreferences.getString("profile_userEmail", null));
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (refreshToken.TempsToken() == false) {
            Intent intent = new Intent(AccueilActivity.this, WebViewActivity.class);
            intent.putExtra("url", sharedPreferences.getString("urlRedirect", null));
            startActivity(intent);
        } else {
            this.CallFragment(new FeuilllesFragment());
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshToken.TempsToken() == false) {
            Intent intent = new Intent(AccueilActivity.this, WebViewActivity.class);
            intent.putExtra("url", sharedPreferences.getString("urlRedirect", null));
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_feuille_route) {
            fragment = new FeuilllesFragment();

        } else if (id == R.id.nav_scanner) {
            fragment = new ScannerFragment();

        } else if (id == R.id.nav_listes) {
            fragment = new ListOCRFragment();

        } else if (id == R.id.nav_profil) {
            fragment = new ProfilFragment();
        } else if (id == R.id.nav_setting) {
            fragment = new SettingFragment();
        } else if (id == R.id.nav_deconnec) {
            drawer.closeDrawer(GravityCompat.START);
            Deconnexion();
            return false;
        }
        this.CallFragment(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CallFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter,
                        R.anim.pop_exit)
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void Deconnexion() {
        progressBar.Show();
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Retrofit retrofit_ = apiClient.getClient(sharedPreferences.getString("url", null));
        ApiInterface api = retrofit_.create(ApiInterface.class);
        Call<Void> call = api.Deconnexion();
        call.clone().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                editor.remove("refresh_token");
                editor.remove("acces_token");
                editor.remove("limit_token");
                editor.remove("profile_user");
                editor.remove("profile_userEmail");
                editor.apply();
                progressBar.Dismiss();
                Intent intent = new Intent(AccueilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("test", " fail ");
            }
        });
    }


}
