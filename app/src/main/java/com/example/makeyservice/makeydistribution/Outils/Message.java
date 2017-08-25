package com.example.makeyservice.makeydistribution.Outils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.makeyservice.makeydistribution.R;

/**
 * Created by makeyservice on 16/05/2017.
 */

public class Message {
    Context context;
    AlertDialog.Builder builder = null;
    AlertDialog dialog;
    public Message(Context mcontext) {
        this.context = mcontext;
    }

    public void  Dialogue(String title,String message){
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }
    public void DialogueDismiss(){
        dialog.dismiss();
    }
}
