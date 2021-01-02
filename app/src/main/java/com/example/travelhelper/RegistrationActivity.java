package com.example.travelhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    //VARIABLES
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

    private TextInputLayout mEmail, mPassword, mRepeatPassword;
    private ImageView image;
    private TextView textLogo, slogan;
    private Button signUpButton, signInButton;
    private FirebaseAuth firebaseAuth;
    private LoadingDialog loadingDialog;
    private Intent intent;
    private Pair[] pairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        //HOOKS
        image = findViewById(R.id.sign_up_logo_image);
        textLogo = findViewById(R.id.inscription_under_sign_up_logo);
        slogan = findViewById(R.id.sign_up_slogan_name);
        mEmail = findViewById(R.id.sign_up_e_mail);
        mPassword = findViewById(R.id.sign_up_password);
        mRepeatPassword = findViewById(R.id.sign_up_repeat_password);
        signUpButton = findViewById(R.id.sign_up_button_in_sign_up);
        signInButton = findViewById(R.id.sign_in_button_in_sign_up);
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
        //endregion

        //region OnClick LISTENERS
        signUpButton.setOnClickListener(v -> {
                    /*trim() usuwa zbędne odstępy (spacje)*/
                    String email = mEmail.getEditText().getText().toString().trim();
                    String password = mPassword.getEditText().getText().toString().trim();
                    String repeatPassword = mRepeatPassword.getEditText().getText().toString().trim();

                    if (!ValidateEmail() | !ValidatePassword() | !ValidateRepeatPassword())
                        return;

                    loadingDialog = new LoadingDialog(RegistrationActivity.this);
                    loadingDialog.StartLoadingDialog();

                    //Sprawdzamy, czy podany E-mail nie jest już zarejestrowany
                    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> methods = task.getResult().getSignInMethods();

                            if (!methods.isEmpty()) {
                                mEmail.setError(getString(R.string.email_exist_error));
                                mEmail.requestFocus();
                            } else {
                                intent = new Intent(getApplicationContext(), NewUserInformationActivity.class);
                                intent.putExtra("USER_EMAIL", email);
                                intent.putExtra("USER_PASSWORD", password);
                                startActivity(intent);
                                finish();
                            }
                        }
                        loadingDialog.DismissDialog();
                    });
                }
        );

        signInButton.setOnClickListener(v -> {
            intent = new Intent(RegistrationActivity.this, LoginActivity.class);

            pairs = new Pair[7];

            pairs[0] = new Pair<View, String>(image, "logo_image");
            pairs[1] = new Pair<View, String>(textLogo, "logo_text");
            pairs[2] = new Pair<View, String>(slogan, "logo_desc");
            pairs[3] = new Pair<View, String>(mEmail, "email_tran");
            pairs[4] = new Pair<View, String>(mPassword, "password_tran");
            pairs[5] = new Pair<View, String>(signUpButton, "button_tran");
            pairs[6] = new Pair<View, String>(signInButton, "sign_in_sign_up_tran");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this, pairs);
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

    private boolean ValidateRepeatPassword() {
        String password = mPassword.getEditText().getText().toString().trim();
        String repeatPassword = mRepeatPassword.getEditText().getText().toString().trim();

        if (repeatPassword.isEmpty()) {
            mRepeatPassword.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!repeatPassword.equals(password)) {
            mRepeatPassword.setError(getString(R.string.password_are_not_the_same));
            return false;
        } else {
            mRepeatPassword.setError(null);
            return true;
        }
    }
    //endregion
}