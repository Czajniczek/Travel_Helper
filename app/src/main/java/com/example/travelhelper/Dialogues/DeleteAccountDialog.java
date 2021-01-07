package com.example.travelhelper.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;

import com.example.travelhelper.LoginAndRegistration.LoginActivity;
import com.example.travelhelper.R;
import com.example.travelhelper.User.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountDialog {

    //region VARIABLES
    //LAYOUT
    private Button yesButton, noButton;

    //FIREBASE
    private UserDatabase userDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    //OTHERS
    private final Activity myActivity;
    private Context myContext;
    private AlertDialog alertDialog;
    //endregion

    public DeleteAccountDialog(Activity myActivity, UserDatabase userDatabase) {
        this.myActivity = myActivity;
        this.userDatabase = userDatabase;
    }

    public void StartDeleteAccountDialog() {
        myContext = myActivity.getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
        LayoutInflater inflater = myActivity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_delete_account, null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        //HOOKS
        yesButton = alertDialog.findViewById(R.id.delete_account_YES);
        noButton = alertDialog.findViewById(R.id.delete_account_NO);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        yesButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            user.delete();
            firebaseAuth.signOut();
            UserDatabase.ClearInstance();
            myActivity.startActivity(new Intent(myContext, LoginActivity.class));
            myActivity.finish();
        });

        noButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    public void DismissDialog() {
        alertDialog.dismiss();
    }
}
