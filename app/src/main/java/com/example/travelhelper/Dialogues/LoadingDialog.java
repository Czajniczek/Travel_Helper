package com.example.travelhelper.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.travelhelper.R;

public class LoadingDialog {

    //region VARIABLES
    //OTHERS
    private final Activity activity;
    private AlertDialog alertDialog;
    private Boolean cancelable;
    //endregion

    public LoadingDialog(Activity myActivity, Boolean cancelable) {
        activity = myActivity;
        this.cancelable = cancelable;
    }

    public void StartLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(cancelable);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    public void DismissDialog() {
        alertDialog.dismiss();
    }
}
