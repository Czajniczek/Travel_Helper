package com.example.travelhelper.LoginAndRegistration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelhelper.OtherClasses.MainActivity;
import com.example.travelhelper.R;
import com.example.travelhelper.Dialogues.LoadingDialog;
import com.example.travelhelper.Dialogues.ResetPasswordDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    //region VARIABLES
    //LAYOUT
    private TextInputLayout mEmail, mPassword;
    private Button forgotPassword, signInButton, signUpButton;
    private TextView signInError;
    private Intent intent;

    //DIALOGUES
    private LoadingDialog loadingDialog;
    private ResetPasswordDialog resetPasswordDialog;

    //FIREBASE
    private FirebaseAuth firebaseAuth;

    //VALIDATION
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

    //ANIMATIONS
    //private ImageView image;
    //private TextView textLogo, slogan;
    //private Pair[] pairs;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TURN OFF THE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //HOOKS
        //image = findViewById(R.id.sign_in_logo_image);
        //textLogo = findViewById(R.id.activity_login_inscription_under_logo);
        //slogan = findViewById(R.id.sign_in_slogan_name);
        mEmail = findViewById(R.id.sign_in_e_mail);
        mPassword = findViewById(R.id.sign_in_password);
        forgotPassword = findViewById(R.id.forgot_password_button);
        signInButton = findViewById(R.id.sign_in_button_in_sign_in);
        signInError = findViewById(R.id.sign_in_error);
        signUpButton = findViewById(R.id.sign_up_button_in_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();

        //region TEXT CHANGE LISTENERS
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

        //region ON CLICK LISTENERS
        forgotPassword.setOnClickListener(v -> {
            resetPasswordDialog = new ResetPasswordDialog(LoginActivity.this);
            resetPasswordDialog.StartResetPasswordDialog();
        });

        signInButton.setOnClickListener(v -> {
            String email = mEmail.getEditText().getText().toString().trim();
            String password = mPassword.getEditText().getText().toString().trim();

            if (!ValidateEmail() | !ValidatePassword()) {
                if (!ValidateEmail()) mEmail.requestFocus();
                else mPassword.requestFocus();
                return;
            }

            loadingDialog = new LoadingDialog(LoginActivity.this, false);
            loadingDialog.StartLoadingDialog();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loadingDialog.DismissDialog();
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_successfully_logged_in), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    loadingDialog.DismissDialog();
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_login_error), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                signInError.setVisibility(View.VISIBLE);
                loadingDialog.DismissDialog();
            });
        });

        signUpButton.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, RegistrationActivity.class);

            /*pairs = new Pair[7];

            pairs[0] = new Pair<View, String>(image, "logo_image");
            pairs[1] = new Pair<View, String>(textLogo, "logo_text");
            pairs[2] = new Pair<View, String>(slogan, "logo_desc");
            pairs[3] = new Pair<View, String>(mEmail, "email_tran");
            pairs[4] = new Pair<View, String>(mPassword, "password_tran");
            pairs[5] = new Pair<View, String>(signInButton, "button_tran");
            pairs[6] = new Pair<View, String>(signUpButton, "sign_in_sign_up_tran");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());*/
            startActivity(intent);

            //SLIDE IN
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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