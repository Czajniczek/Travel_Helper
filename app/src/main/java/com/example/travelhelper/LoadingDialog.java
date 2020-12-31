package com.example.travelhelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

    private final Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    void StartLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void DismissDialog() {
        alertDialog.dismiss();
    }
}
