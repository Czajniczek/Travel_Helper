package com.example.travelhelper.LoginAndRegistration;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.example.travelhelper.Dialogues.LoadingDialog;
import com.example.travelhelper.OtherClasses.MainActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.travelhelper.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewUserInformationActivity extends AppCompatActivity {

    //region VARIABLES
    //LAYOUT
    private TextInputLayout mUserName, mCity, mPhoneNumber;
    private Button continueButton;

    //DIALOGUES
    private LoadingDialog loadingDialog;

    //FIREBASE
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentReference documentReference;
    private String email, password;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_information);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("USER_EMAIL");
        password = extras.getString("USER_PASSWORD");

        //HOOKS
        mUserName = findViewById(R.id.necessary_data_user_name);
        mCity = findViewById(R.id.necessary_data_city);
        mPhoneNumber = findViewById(R.id.necessary_data_phone_number);
        continueButton = findViewById(R.id.necessary_data_continue_button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //region TEXT CHANGE LISTENERS
        mUserName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateUserName();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPhoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidatePhoneNumber();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateCity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion

        //region ON CLICK LISTENERS
        continueButton.setOnClickListener(v -> Registration(email, password));
        //endregion
    }

    private void Registration(String email, String password) {
        String name = mUserName.getEditText().getText().toString().trim();
        String phoneNumber = mPhoneNumber.getEditText().getText().toString().trim();
        String city = mCity.getEditText().getText().toString().trim();

        if (!ValidateUserName() | !ValidatePhoneNumber() | !ValidateCity()) {
            if (!ValidateUserName()) {
                mUserName.requestFocus();
                return;
            } else if (!ValidatePhoneNumber()) {
                mPhoneNumber.requestFocus();
                return;
            } else {
                mCity.requestFocus();
                return;
            }
        }

        loadingDialog = new LoadingDialog(this, false);
        loadingDialog.StartLoadingDialog();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) SaveUserData(name, email, phoneNumber, city);
            else {
                loadingDialog.DismissDialog();
                Toast.makeText(NewUserInformationActivity.this, getResources().getString(R.string.toast_something_has_gone_wrong), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            loadingDialog.DismissDialog();
            Toast.makeText(NewUserInformationActivity.this, getResources().getString(R.string.toast_something_has_gone_wrong), Toast.LENGTH_LONG).show();
        });
    }

    private void SaveUserData(String name, String email, String phoneNumber, String city) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentTime = simpleDateFormat.format(new Date());

        //https://firebase.google.com/docs/firestore/manage-data/add-data
        String userId = firebaseAuth.getCurrentUser().getUid();
        documentReference = firebaseFirestore.collection("Users").document(userId);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Username", name);
        userMap.put("E-mail", email);
        userMap.put("City", city);
        userMap.put("Phone number", phoneNumber);
        userMap.put("Date of account creation", currentTime);

        documentReference.set(userMap).addOnSuccessListener(aVoid -> {
            loadingDialog.DismissDialog();
            Toast.makeText(NewUserInformationActivity.this, getResources().getString(R.string.toast_user_created), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_user_data_could_not_be_saved), Toast.LENGTH_LONG).show());
    }

    //region VALIDATION
    private boolean ValidateUserName() {
        String user = mUserName.getEditText().getText().toString().trim();

        if (user.isEmpty()) {
            mUserName.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else {
            mUserName.setError(null);
            return true;
        }
    }

    private boolean ValidatePhoneNumber() {
        String phone = mPhoneNumber.getEditText().getText().toString().trim();

        if (phone.isEmpty()) {
            mPhoneNumber.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (phone.length() != 9) {
            mPhoneNumber.setError(getString(R.string.wrong_phone_number));
            return false;
        } else {
            mPhoneNumber.setError(null);
            return true;
        }
    }

    private boolean ValidateCity() {
        String userCity = mCity.getEditText().getText().toString().trim();

        if (userCity.isEmpty()) {
            mCity.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else {
            mCity.setError(null);
            return true;
        }
    }
    //endregion
}