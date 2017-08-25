package com.example.makeyservice.makeydistribution.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.makeyservice.makeydistribution.APISqlite.DatabaseHandler;
import com.example.makeyservice.makeydistribution.Adapter.ListOCRAdapter;
import com.example.makeyservice.makeydistribution.Model.GeoAdresse;
import com.example.makeyservice.makeydistribution.Outils.ProgressBar;
import com.example.makeyservice.makeydistribution.R;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makeyservice on 28/06/2017.
 */

public class ListOCRFragment extends Fragment {

    @BindView(R.id.myrecyclerview)
    RecyclerView myrecyclerview;
    @BindView(R.id.txtSelction)
    TextView txtSelction;
    ListOCRAdapter listOCRAdapter;
    ProgressBar progressBar;
    Boolean reponse = false;
    DatabaseHandler db ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listocr_activity, container, false);
        ButterKnife.bind(this, view);
        progressBar = new ProgressBar(getActivity(),"Makey Distribution","Chargement...");
        db = new DatabaseHandler(getContext());
        txtSelction.setText(Html.fromHtml("<u>Supprimer la sélection</u>"));
        Affichage();
        return view;
    }
    public void Affichage(){
        List<GeoAdresse> adresseList = db.getAllGeoAdresse();
        progressBar.Show();
        listOCRAdapter = new ListOCRAdapter(getContext(),adresseList);
        myrecyclerview.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myrecyclerview.setLayoutManager(layoutManager);
        myrecyclerview.setAdapter(listOCRAdapter);
        progressBar.Dismiss();
    }

    @OnClick(R.id.LinerLayoutClick)
    public void OnClick(){
        String data = "";
        progressBar.Show();
        final List<GeoAdresse> geoAdresses =  listOCRAdapter.getList();
        for (int i = 0; i < geoAdresses.size(); i++) {
            GeoAdresse singleGeoAdresse = geoAdresses.get(i);
            if (singleGeoAdresse.isSelected() == true) {
                data = data + singleGeoAdresse.getId().toString()+"|";
            }
        }
        final String[] dataSplit = data.split(Pattern.quote("|"));
        if(!data.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Info");
            builder.setMessage("Voulez-vous vraiment supprimer cet enregistrement ?");
            //Button One : Yes
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(int i=0;i<dataSplit.length;i++){
                        GeoAdresse adresse = new GeoAdresse();
                        adresse.setId(Integer.parseInt(dataSplit[i]));
                        GeoAdresse nomFile = db.getGeoAdresse(adresse.getId());
                        if(nomFile!=null){
                            delete(new File(nomFile.getChemin()));
                        }
                        reponse = true;
                        db.deleteGeoAdresse(adresse);

                    }
                    if(reponse){
                        listOCRAdapter.Actualise();
                        listOCRAdapter.notifyDataSetChanged();
                    }
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
        progressBar.Dismiss();
    }

    private void delete(File file){
        if(file.exists()){
            file.delete();
        }
    }
}
