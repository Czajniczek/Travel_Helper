package com.example.travelhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    private TextInputLayout mEmail, mPassword;
    private Button signInButton, signUpButton, forgotPassword;
    private TextView signInError;
    private LoadingDialog loadingDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.sign_in_e_mail);
        mPassword = findViewById(R.id.sign_in_password);
        forgotPassword = findViewById(R.id.forgot_password_button);
        signInButton = findViewById(R.id.sign_in_button_in_sign_in);
        signInError = findViewById(R.id.sign_in_error);
        signUpButton = findViewById(R.id.sign_up_button_in_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();

        //Nie trzeba się logować za każdym razem
        /*if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }*/

        mEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateEmail();
                signInError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidatePassword();
                signInError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signInButton.setOnClickListener(v -> {
            String email = mEmail.getEditText().getText().toString().trim();
            String password = mPassword.getEditText().getText().toString().trim();

            if (!ValidateEmail() | !ValidatePassword()) return;

            loadingDialog = new LoadingDialog(LoginActivity.this);
            loadingDialog.StartLoadingDialog();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    loadingDialog.DismissDialog();
                }
            }).addOnFailureListener(e -> {
                signInError.setVisibility(View.VISIBLE);
                loadingDialog.DismissDialog();
            });
        });

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            finish();
        });
    }

    private boolean ValidateEmail() {
        String email = mEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(getString(R.string.email_validate_error));
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }

    private boolean ValidatePassword() {
        String password = mPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            mPassword.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            mPassword.setError(getString(R.string.password_is_too_weak));
            return false;
        } else {
            mPassword.setError(null);
            return true;
        }
    }
}