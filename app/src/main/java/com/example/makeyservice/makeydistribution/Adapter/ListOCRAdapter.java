package com.example.makeyservice.makeydistribution.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.makeyservice.makeydistribution.APISqlite.DatabaseHandler;
import com.example.makeyservice.makeydistribution.Model.GeoAdresse;
import com.example.makeyservice.makeydistribution.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by RAYA on 21/09/2016.
 */
public class ListOCRAdapter extends RecyclerView.Adapter<ListOCRAdapter.ViewHolder> {

    // region Member Variables
    private Context mContext;
    private List<GeoAdresse> geoAdresses;
    private DatabaseHandler db;
    // endregion

    // region Constructors
    public ListOCRAdapter(Context context, List<GeoAdresse> geoAdresseList) {
        mContext = context;
        geoAdresses = geoAdresseList;
        // endregion
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_ocr, parent, false);
        ViewHolder vh = new ViewHolder(v);
        //progressBar = new ProgressBar(mContext,"Makey Distribution","Chargement...");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(position % 2 ==0 )
            holder.itemView.setBackgroundResource(R.drawable.listviews);
        else
            holder.itemView.setBackgroundResource(R.drawable.listview);
        final GeoAdresse geoAdresse = geoAdresses.get(position);
        if (geoAdresse != null) {
            holder.txtID.setText(""+geoAdresse.getId());
            setUpDisplayPertinence(holder.txtPertinence, geoAdresse);
            setUpStreet(holder.txtStreet, geoAdresse);
            setUpDisplayCp(holder.txtCp, geoAdresse);
            setUpDisplayImage(holder.picture, geoAdresse);

        }
        holder.checkBoxDelete.setChecked(false);
        holder.checkBoxDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                geoAdresses.get(position).setSelected(cb.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return geoAdresses.size();
    }

    public List<GeoAdresse> getList(){
        return geoAdresses;
    }

    public void Actualise(){
        db = new DatabaseHandler(mContext);
        geoAdresses.clear();
        geoAdresses  = db.getAllGeoAdresse();
    }



    private void setUpDisplayPertinence(TextView tv, GeoAdresse geoAdresse) {
        String display = "<u>"+String.valueOf(geoAdresse.getScore())+" %"+"</u>";
        if (!TextUtils.isEmpty(display)) {
            tv.setText("Pertinence : "+Html.fromHtml(display));
        }
    }


    private void setUpStreet(TextView tv, GeoAdresse geoAdresse) {
        String display = geoAdresse.getHousenumber()+" "+geoAdresse.getStreet();
        if (!TextUtils.isEmpty(display)) {
            tv.setText(display);
        }
    }

    private void setUpDisplayCp(TextView tv, GeoAdresse geoAdresse) {
        String display= String.valueOf(geoAdresse.getPostcode()) ;
        if (!TextUtils.isEmpty(display)) {
            tv.setText(display);
        }
    }

    private void setUpDisplayImage(ImageView tv, GeoAdresse geoAdresse) {
        String displayName = geoAdresse.getChemin();
        if (!TextUtils.isEmpty(displayName)) {
            Bitmap bitmap = BitmapFactory.decodeFile(displayName);
            tv.setImageBitmap(bitmap);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtID)
        TextView txtID;
        @BindView(R.id.txtPertinence)
        TextView txtPertinence;
        @BindView(R.id.txtStreet)
        TextView txtStreet;
        @BindView(R.id.txtCp)
        TextView txtCp;
        @BindView(R.id.picture)
        ImageView picture;
        @BindView(R.id.checkbox)
        CheckBox checkBoxDelete;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
