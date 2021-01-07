package com.example.travelhelper.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import com.example.travelhelper.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class ResetPasswordDialog {

    //region VARIABLES
    //LAYOUT
    private TextInputLayout mEmail;
    private Button resetButton;

    //FIREBASE
    private FirebaseAuth firebaseAuth;

    //OTHERS
    private final Activity activity;
    private AlertDialog alertDialog;
    //endregion

    public ResetPasswordDialog(Activity myActivity) {
        this.activity = myActivity;
    }

    public void StartResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_reset_password, null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        //HOOKS
        mEmail = alertDialog.findViewById(R.id.forgot_password_e_mail);
        resetButton = alertDialog.findViewById(R.id.forgot_password_reset_button);
        firebaseAuth = FirebaseAuth.getInstance();

        //region TextChange LISTENERS
        mEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion

        resetButton.setOnClickListener(v -> {
            String email = mEmail.getEditText().getText().toString().trim();

            if (!ValidateEmail()) return;

            //Checking whether the e-mail address provided exists
            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> methods = Objects.requireNonNull(task.getResult()).getSignInMethods();
                    if (methods.isEmpty()) {
                        mEmail.setError(activity.getResources().getString(R.string.reset_password_email_not_exist));
                        mEmail.requestFocus();
                    } else {
                        alertDialog.dismiss();
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                            Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.toast_password_change), Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.toast_something_has_gone_wrong), Toast.LENGTH_LONG).show();
                            /*.addOnFailureListener(e -> Toast.makeText(activity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());*/
                        });
                    }
                }
            });
        });
    }

    //region VALIDATION
    private boolean ValidateEmail() {
        String email = mEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError(activity.getResources().getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(activity.getResources().getString(R.string.email_validate_error));
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }
    //endregion
}
