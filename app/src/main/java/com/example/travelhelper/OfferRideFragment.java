package com.example.travelhelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelhelper.API.City;
import com.example.travelhelper.API.CityProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class OfferRideFragment extends Fragment {

    int PERMISSION_ID = 44;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Activity myActivity;
    private Context myContext;
    private FloatingActionButton addFloatingButton;

    private TextView cityName, cityPopulation, country, division;

    CityProvider.CityLoaded cityLoaded;

    FusedLocationProviderClient fusedLocationProviderClient;

    List<City> cityList;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            CityProvider.fetchCityData(cityLoaded, lastLocation.getLatitude(), lastLocation.getLongitude());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        myActivity = getActivity();
        myContext = myActivity.getApplicationContext();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);

        cityLoaded = this::fetchCityData;

        addFloatingButton = view.findViewById(R.id.offer_ride_floating_button);

        cityName = view.findViewById(R.id.city_name);
        cityPopulation = view.findViewById(R.id.city_population);
        country = view.findViewById(R.id.city_country);
        division = view.findViewById(R.id.city_division);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myActivity);

        getLastLocation();

        return view;
    }

    private void fetchCityData(boolean isLoaded, List<City> cityList) {
        if (isLoaded) {
            this.cityList = cityList;

            if (cityList.get(0).getName() != null) {
                cityName.setText(cityList.get(0).getName());
            }

            if (cityList.get(0).getPopulation() != null) {
                cityPopulation.setText(cityList.get(0).getPopulation());
            }

            if (cityList.get(0).getCountry() != null) {
                country.setText(cityList.get(0).getCountry().getName());
            }

            if (cityList.get(0).getAdminDivision() != null) {
                division.setText(cityList.get(0).getAdminDivision().getName());
            }
        } else {
            Toast.makeText(myContext, "Błędzik", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                requestNewLocationData();
                            }
                        }
                );
            } else {
                Toast.makeText(myContext, "Włącz lokalizacje bo masz wyłączoną draniu", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) myActivity.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(myActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myActivity);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}