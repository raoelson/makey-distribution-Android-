package com.example.makeyservice.makeydistribution.Outils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by makeyservice on 23/05/2017.
 */

public class ProgressBar {

    ProgressDialog dialog = null;
    Context context = null;
    String title;
    String message;

    public ProgressBar(Context mContext,String title,String message) {
        this.context = mContext;
        this.title = title;
        this.message= message;
        //dialog = new ProgressDialog(context,R.style.AppTheme_Dark_Dialog);
        dialog = new ProgressDialog(context);
    }

    public void Show(){
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.show();
    }
    public void Dismiss(){
        dialog.dismiss();
    }
}
