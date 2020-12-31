package com.example.travelhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final Pattern PAASSWORD_PATTERN =
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

        mPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidatePassword();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signInButton.setOnClickListener(v -> {
            if (!ValidateEmail() | !ValidatePassword()) return;

            final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
            loadingDialog.StartLoadingDialog();

            //TODO FIREBASE
        });

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            finish();
        });
    }

    private boolean ValidateEmail() {
        /*trim() usuwa zbędne odstępy (spacje)*/
        String email = mEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError(getString(R.string.empty_email_error));
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
        /*trim() usuwa zbędne odstępy (spacje)*/
        String password = mPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            mPassword.setError(getString(R.string.empty_email_error));
            return false;
        } else if (!PAASSWORD_PATTERN.matcher(password).matches()) {
            mPassword.setError(getString(R.string.password_is_too_weak));
            return false;
        } else {
            mPassword.setError(null);
            return true;
        }
    }
}