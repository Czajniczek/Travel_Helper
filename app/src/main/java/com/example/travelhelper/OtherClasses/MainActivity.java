package com.example.travelhelper.OtherClasses;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
//import android.app.Activity;
//import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.view.WindowManager;
//import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.FirebaseAuth;
import com.example.travelhelper.R;

public class MainActivity extends AppCompatActivity {

    //region VARIABLES
    //OTHERS
    private BottomNavigationView navView;
    private NavController navController;
    private Fragment fragment;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.navigation_view);
        navController = Navigation.findNavController(this, R.id.navigation_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fragment = getSupportFragmentManager().findFragmentById(R.id.navigation_host_fragment);
        fragment.getChildFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
    }
}