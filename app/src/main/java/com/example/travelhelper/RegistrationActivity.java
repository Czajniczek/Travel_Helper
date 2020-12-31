package com.example.travelhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

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

    private TextInputLayout mEmail, mPassword, mRepeatPassword;
    private Button signUpButton, signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmail = findViewById(R.id.sign_up_e_mail);
        mPassword = findViewById(R.id.sign_up_password);
        mRepeatPassword = findViewById(R.id.sign_up_repeat_password);
        signUpButton = findViewById(R.id.sign_up_button_in_sign_up);
        signInButton = findViewById(R.id.sign_in_button_in_sign_up);

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

        mRepeatPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateRepeatPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signUpButton.setOnClickListener(v -> {
            if (!ValidateEmail() | !ValidatePassword() | !ValidateRepeatPassword()) return;

            final LoadingDialog loadingDialog = new LoadingDialog(RegistrationActivity.this);
            loadingDialog.StartLoadingDialog();

            //TODO FIREBASE
        });

        signInButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    private boolean ValidateRepeatPassword() {
        String password = mPassword.getEditText().getText().toString().trim();
        String repeatPassword = mRepeatPassword.getEditText().getText().toString().trim();

        if (repeatPassword.isEmpty()) {
            mRepeatPassword.setError(getString(R.string.empty_email_error));
            return false;
        } else if (!repeatPassword.equals(password)) {
            mRepeatPassword.setError(getString(R.string.password_are_not_the_same));
            return false;
        } else {
            mRepeatPassword.setError(null);
            return true;
        }
    }
}