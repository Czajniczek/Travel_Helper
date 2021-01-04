package com.example.travelhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNeedRideActivity extends AppCompatActivity {

    private TextInputLayout fromCity, fromStreet, toCity, toStreet, day, month, year, hour, minute;
    private String sFromCity, sFromStreet, sToCity, sToStreet, sDay, sMonth, sYear, sHour, sMinute, userId;
    private Button addButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_need_ride);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userId = firebaseAuth.getUid();

        fromCity = findViewById(R.id.new_ride_city_from);
        fromStreet = findViewById(R.id.new_ride_street_from);
        toCity = findViewById(R.id.new_ride_city_to);
        toStreet = findViewById(R.id.new_ride_street_to);
        day = findViewById(R.id.new_ride_day);
        month = findViewById(R.id.new_ride_month);
        year = findViewById(R.id.new_ride_year);
        hour = findViewById(R.id.new_ride_hour);
        minute = findViewById(R.id.new_ride_minutes);
        addButton = findViewById(R.id.need_ride_button_add);

        addButton.setOnClickListener(v -> {
            AddNewNeedRide();
        });
    }

    private void AddNewNeedRide() {
        sFromCity = fromCity.getEditText().getText().toString().trim();
        sFromStreet = fromStreet.getEditText().getText().toString().trim();
        sToCity = toCity.getEditText().getText().toString().trim();
        sToStreet = toStreet.getEditText().getText().toString().trim();
        sDay = day.getEditText().getText().toString().trim();
        sMonth = month.getEditText().getText().toString().trim();
        sYear = year.getEditText().getText().toString().trim();
        sHour = hour.getEditText().getText().toString().trim();
        sMinute = minute.getEditText().getText().toString().trim();

        Map<String, Object> noticeMap = new HashMap<>();
        noticeMap.put("From city", sFromCity);
        noticeMap.put("From street", sFromStreet);
        noticeMap.put("To city", sToCity);
        noticeMap.put("To street", sToStreet);
        noticeMap.put("Day", sDay);
        noticeMap.put("Month", sMonth);
        noticeMap.put("Year", sYear);
        noticeMap.put("Hour", sHour);
        noticeMap.put("Minute", sMinute);
        noticeMap.put("User ID", userId);

        String id = firebaseFirestore.collection("Need ride").document().getId();
        firebaseFirestore.collection("Need ride").document(id).set(noticeMap).addOnSuccessListener(v -> {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(v -> Toast.makeText(getApplicationContext(), "Error: " + v.getLocalizedMessage(), Toast.LENGTH_LONG).show());
    }
}