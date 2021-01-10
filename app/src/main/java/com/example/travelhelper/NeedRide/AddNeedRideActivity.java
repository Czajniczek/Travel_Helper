package com.example.travelhelper.NeedRide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.travelhelper.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
//import java.util.regex.Pattern;

public class AddNeedRideActivity extends AppCompatActivity {

    //region VARIABLES
    //LAYOUT
    private TextInputLayout fromCity, fromStreet, toCity, toStreet, day, month, year, hour, minute;
    private Button addButton;

    //FIREBASE
    private String sFromCity, sFromStreet, sToCity, sToStreet, sDay, sMonth, sYear, sHour, sMinute, userId;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    //private Context context;
    //endregion


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_need_ride);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.trashcan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userId = firebaseAuth.getUid();

        //region TEXT CHANGE LISTENERS
        fromCity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateCityFrom();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fromStreet.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateStreetFrom();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toCity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateCityTo();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toStreet.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateStreetTo();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        day.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DayValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        month.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MonthValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        year.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                YearValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        hour.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HourValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        minute.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MinuteValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion

        //region ON CLICK LISTENERS
        addButton.setOnClickListener(v -> {

            if (!ValidateCityFrom() | !ValidateStreetFrom() | !ValidateCityTo() | !ValidateStreetTo() |
                    !DayValidation() | !MonthValidation() | !YearValidation() | !HourValidation() | !MinuteValidation()) {
                if (!ValidateCityFrom()) {
                    fromCity.requestFocus();
                    return;
                } else if (!ValidateStreetFrom()) {
                    fromStreet.requestFocus();
                    return;
                } else if (!ValidateCityTo()) {
                    toCity.requestFocus();
                    return;
                } else if (!ValidateStreetTo()) {
                    toStreet.requestFocus();
                    return;
                } else if (!DayValidation()) {
                    day.requestFocus();
                    return;
                } else if (!MonthValidation()) {
                    month.requestFocus();
                    return;
                } else if (!YearValidation()) {
                    year.requestFocus();
                    return;
                } else if (!HourValidation()) {
                    hour.requestFocus();
                    return;
                } else {
                    minute.requestFocus();
                    return;
                }
            }

            AddNewNeedRide();
        });
        //endregion
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

        //String id = firebaseFirestore.collection("Need ride").document().getId();
        firebaseFirestore.collection("Need ride").document().set(noticeMap).addOnSuccessListener(v -> {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_successfully_added_advertisement), Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(v -> Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_something_has_gone_wrong), Toast.LENGTH_LONG).show());
    }

    //region VALIDATION
    private boolean ValidateCityFrom() {
        String city = fromCity.getEditText().getText().toString().trim();

        if (city.isEmpty()) {
            fromCity.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!city.matches(".{3,}")) {
            fromCity.setError(getString(R.string.wrong_city_name));
            return false;
        } else {
            fromCity.setError(null);
            return true;
        }
    }

    private boolean ValidateStreetFrom() {
        String street = fromStreet.getEditText().getText().toString().trim();

        if (street.isEmpty()) {
            fromStreet.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!street.matches(".{3,}")) {
            fromStreet.setError(getString(R.string.wrong_street_name));
            return false;
        } else {
            fromStreet.setError(null);
            return true;
        }
    }

    private boolean ValidateCityTo() {
        String city = toCity.getEditText().getText().toString().trim();

        if (city.isEmpty()) {
            toCity.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!city.matches(".{3,}")) {
            toCity.setError(getString(R.string.wrong_city_name));
            return false;
        } else {
            toCity.setError(null);
            return true;
        }
    }

    private boolean ValidateStreetTo() {
        String street = toStreet.getEditText().getText().toString().trim();

        if (street.isEmpty()) {
            toStreet.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!street.matches(".{3,}")) {
            toStreet.setError(getString(R.string.wrong_street_name));
            return false;
        } else {
            toStreet.setError(null);
            return true;
        }
    }

    private boolean DayValidation() {
        String Day = day.getEditText().getText().toString().trim();

        if (Day.isEmpty()) {
            day.setError(getString(R.string.empty_field));
            return false;
        } else if (Integer.parseInt(Day) > 31 || Integer.parseInt(Day) == 0) {
            day.setError(getString(R.string.wrong_value));
            return false;
        } else {
            day.setError(null);
            return true;
        }
    }

    private boolean MonthValidation() {
        String Month = month.getEditText().getText().toString().trim();

        if (Month.isEmpty()) {
            month.setError(getString(R.string.empty_field));
            return false;
        } else if (Integer.parseInt(Month) > 12 || Integer.parseInt(Month) == 0) {
            month.setError(getString(R.string.wrong_value));
            return false;
        } else {
            month.setError(null);
            return true;
        }
    }

    private boolean YearValidation() {
        String Year = year.getEditText().getText().toString().trim();

        if (Year.isEmpty()) {
            year.setError(getString(R.string.empty_field));
            return false;
        } else if (Integer.parseInt(Year) < Calendar.getInstance().get(Calendar.YEAR)) {
            year.setError(getString(R.string.wrong_value));
            return false;
        } else {
            year.setError(null);
            return true;
        }
    }

    private boolean HourValidation() {
        String Hour = hour.getEditText().getText().toString().trim();

        if (Hour.isEmpty()) {
            hour.setError(getString(R.string.empty_field));
            return false;
        } else if (Integer.parseInt(Hour) < 0 || Integer.parseInt(Hour) > 24) {
            hour.setError(getString(R.string.wrong_value));
            return false;
        } else {
            hour.setError(null);
            return true;
        }
    }

    private boolean MinuteValidation() {
        String Minute = minute.getEditText().getText().toString().trim();

        if (Minute.isEmpty()) {
            minute.setError(getString(R.string.empty_field));
            return false;
        } else if (Integer.parseInt(Minute) < 0 || Integer.parseInt(Minute) > 60) {
            minute.setError(getString(R.string.wrong_value));
            return false;
        } else {
            minute.setError(null);
            return true;
        }
    }
    //endregion
}