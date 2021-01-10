package com.example.travelhelper.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.travelhelper.LoginAndRegistration.LoginActivity;
import com.example.travelhelper.R;
import com.example.travelhelper.User.UserDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountDialog {

    //region VARIABLES
    //LAYOUT
    private Button yesButton, noButton;

    //OTHERS
    private final Activity myActivity;
    private Context myContext;
    private AlertDialog alertDialog;
    //endregion

    public DeleteAccountDialog(Activity myActivity) {
        this.myActivity = myActivity;
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

        //FIREBASE
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DocumentReference usersReference = FirebaseFirestore.getInstance().collection("Users").document(userId);
        CollectionReference needRideReference = FirebaseFirestore.getInstance().collection("Need ride");
        StorageReference profilePhoto = FirebaseStorage.getInstance().getReference().child("Users/" + userId + "/User photo");

        yesButton.setOnClickListener(v -> {
            usersReference.delete().addOnCompleteListener(aVoid -> Toast.makeText(myContext, "User deleted", Toast.LENGTH_LONG).show());
            needRideReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String documentId = documentSnapshot.getId();
                        if (documentSnapshot.getString("User ID").equals(userId)) {
                            DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("Need ride").document(documentId);
                            documentReference2.delete();
                        }
                    }
                }
            });
            profilePhoto.delete();
            user.delete();
            FirebaseFirestore.getInstance().clearPersistence();

            /*FirebaseAuth.getInstance().signOut();
            UserDatabase.ClearInstance();*/
            alertDialog.dismiss();
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
