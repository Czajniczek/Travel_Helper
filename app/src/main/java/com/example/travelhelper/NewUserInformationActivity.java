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

import java.util.HashMap;
import java.util.Map;

public class NewUserInformationActivity extends AppCompatActivity {

    private TextInputLayout mUserName, mCity, mPhoneNumber;
    private Button continueButton;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_information);

        Bundle extras = getIntent().getExtras();
        final String email = extras.getString("USER_EMAIL");
        final String password = extras.getString("USER_PASSWORD");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mUserName = findViewById(R.id.necessary_data_user_name);
        mCity = findViewById(R.id.necessary_data_city);
        mPhoneNumber = findViewById(R.id.necessary_data_phone_number);
        continueButton = findViewById(R.id.necessary_data_continue_button);

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

        continueButton.setOnClickListener(v -> {
            Registration(email, password);
        });
    }

    private void Registration(String email, String password) {
        final String name = mUserName.getEditText().getText().toString().trim();
        final String phoneNumber = mPhoneNumber.getEditText().getText().toString().trim();
        final String city = mCity.getEditText().getText().toString().trim();

        if (!ValidateUserName() | !ValidateCity()) return;

        loadingDialog = new LoadingDialog(this);
        loadingDialog.StartLoadingDialog();

        //Udało się
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NewUserInformationActivity.this, "User created", Toast.LENGTH_SHORT).show();
                SaveUserData(name, phoneNumber, city);
            } else {
                Toast.makeText(NewUserInformationActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                loadingDialog.DismissDialog();
            }
            //Nie udało się
        }).addOnFailureListener(e -> {
            Toast.makeText(NewUserInformationActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            loadingDialog.DismissDialog();
        });
    }

    private void SaveUserData(String name, String phoneNumber, String city) {
        String userId = firebaseAuth.getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Name", name);
        userMap.put("Phone number", phoneNumber);
        userMap.put("City", city);

        documentReference.set(userMap).addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
    }

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
}