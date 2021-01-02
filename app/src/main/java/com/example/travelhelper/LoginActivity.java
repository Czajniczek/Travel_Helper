package com.example.travelhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    //VARIABLES
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +        //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +             //no white spaces
                    ".{4,}" +                 //at least 4 characters
                    "$");

    private TextInputLayout mEmail, mPassword;
    private Button signInButton, signUpButton, forgotPassword;
    private ImageView image;
    private TextView signInError, textLogo, slogan;
    private LoadingDialog loadingDialog;
    private ResetPasswordDialog resetPasswordDialog;
    private FirebaseAuth firebaseAuth;
    private WindowInsetsController insetsController;
    private Intent intent;
    private Pair[] pairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TURN OFF THE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        /*//TURN OFF THE STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController = getWindow().getInsetsController();
            if (insetsController != null) insetsController.hide(WindowInsets.Type.statusBars());
        } else getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //HOOKS
        image = findViewById(R.id.sign_in_logo_image);
        textLogo = findViewById(R.id.activity_login_inscription_under_logo);
        slogan = findViewById(R.id.sign_in_slogan_name);
        mEmail = findViewById(R.id.sign_in_e_mail);
        mPassword = findViewById(R.id.sign_in_password);
        forgotPassword = findViewById(R.id.forgot_password_button);
        signInButton = findViewById(R.id.sign_in_button_in_sign_in);
        signInError = findViewById(R.id.sign_in_error);
        signUpButton = findViewById(R.id.sign_up_button_in_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();

        //It is not necessary to log in every time
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //region TextChange LISTENERS
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
        //endregion

        //region OnClick LISTENERS
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
                    /*Toast.makeText(LoginActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();*/
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                    loadingDialog.DismissDialog();
                }
            }).addOnFailureListener(e -> {
                signInError.setVisibility(View.VISIBLE);
                loadingDialog.DismissDialog();
            });
        });

        forgotPassword.setOnClickListener(v -> {
            resetPasswordDialog = new ResetPasswordDialog(LoginActivity.this);
            resetPasswordDialog.StartResetPasswordDialog();
        });

        signUpButton.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, RegistrationActivity.class);

            pairs = new Pair[7];

            pairs[0] = new Pair<View, String>(image, "logo_image");
            pairs[1] = new Pair<View, String>(textLogo, "logo_text");
            pairs[2] = new Pair<View, String>(slogan, "logo_desc");
            pairs[3] = new Pair<View, String>(mEmail, "email_tran");
            pairs[4] = new Pair<View, String>(mPassword, "password_tran");
            pairs[5] = new Pair<View, String>(signInButton, "button_tran");
            pairs[6] = new Pair<View, String>(signUpButton, "sign_in_sign_up_tran");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());
        });
        //endregion
    }

    //region VALIDATION
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
    //endregion
}