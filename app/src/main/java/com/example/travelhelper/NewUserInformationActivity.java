package com.example.travelhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewUserInformationActivity extends AppCompatActivity {

    //region VARIABLES
    //LAYOUT
    private TextInputLayout mUserName, mCity, mPhoneNumber;
    private Button continueButton;

    //FIREBASE
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentReference documentReference;
    private String userId, email, password, name, phoneNumber, city;

    //DIALOGUES
    private LoadingDialog loadingDialog;
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

        //region TextChange LISTENERS
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

        continueButton.setOnClickListener(v -> {
            Registration(email, password);
        });
    }

    private void Registration(String email, String password) {
        name = mUserName.getEditText().getText().toString().trim();
        phoneNumber = mPhoneNumber.getEditText().getText().toString().trim();
        city = mCity.getEditText().getText().toString().trim();

        if (!ValidateUserName() | !ValidatePhoneNumber() | !ValidateCity()) return;

        loadingDialog = new LoadingDialog(this);
        loadingDialog.StartLoadingDialog();

        //Udało się
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NewUserInformationActivity.this, "User created", Toast.LENGTH_SHORT).show();
                SaveUserData(name, email, phoneNumber, city);
            } else {
                /*Toast.makeText(NewUserInformationActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();*/
                Toast.makeText(NewUserInformationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                loadingDialog.DismissDialog();
            }
            //Nie udało się
        }).addOnFailureListener(e -> {
            Toast.makeText(NewUserInformationActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            loadingDialog.DismissDialog();
        });
    }

    private void SaveUserData(String name, String email, String phoneNumber, String city) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentTime = sdf.format(new Date());
        //String currentTime = Calendar.getInstance().getTime().toString();

        userId = firebaseAuth.getCurrentUser().getUid();
        documentReference = firebaseFirestore.collection("users").document(userId);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Username", name);
        userMap.put("E-mail", email);
        userMap.put("City", city);
        userMap.put("Phone number", phoneNumber);
        userMap.put("Date of account creation", currentTime);

        documentReference.set(userMap).addOnSuccessListener(aVoid -> {
            //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            loadingDialog.DismissDialog();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
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