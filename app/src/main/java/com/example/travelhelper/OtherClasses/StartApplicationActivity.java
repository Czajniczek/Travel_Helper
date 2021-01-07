package com.example.travelhelper.OtherClasses;

import androidx.appcompat.app.AppCompatActivity;
//import android.app.Activity;
//import android.app.ActivityOptions;
import android.content.Intent;
//import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
//import android.util.Pair;
//import android.view.View;
//import android.view.WindowInsets;
//import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelhelper.LoginAndRegistration.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.travelhelper.R;

public class StartApplicationActivity extends AppCompatActivity {

    //region VARIABLES
    //LAYOUT
    private ImageView image;
    private TextView textLogo, slogan;
    //private View startApplication;
    //private Intent intent;

    //FIREBASE
    private FirebaseAuth firebaseAuth;

    //ANIMATIONS
    private static int SPLASH_SCREEN = 3000;
    private Animation topAnimation, bottomAnimation;
    //private ActivityOptions options;
    //private Pair[] pairs;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TURN OFF THE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_application);

        /*TURN OFF THE STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController = getWindow().getInsetsController();
            if (insetsController != null) insetsController.hide(WindowInsets.Type.statusBars());
        } else
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //HOOKS
        image = findViewById(R.id.activity_start_application_logo_image);
        textLogo = findViewById(R.id.activity_start_application_logo_text_view);
        slogan = findViewById(R.id.activity_start_application_slogan_text_view);
        //startApplication = findViewById(R.id.activity_start_application);
        firebaseAuth = FirebaseAuth.getInstance();

        //ANIMATIONS
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image.setAnimation(topAnimation);
        textLogo.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(StartApplicationActivity.this, MainActivity.class));
            finish();
        } else {
            //DELAY METHOD
            new Handler(Looper.myLooper()).postDelayed(() -> {

                //intent = new Intent(StartApplicationActivity.this, LoginActivity.class);

                //pairs = new Pair[2];
                //pairs[0] = new Pair<View, String>(image, "logo_image");
                //pairs[1] = new Pair<View, String>(textLogo, "logo_text");

                //options = ActivityOptions.makeSceneTransitionAnimation(StartApplicationActivity.this, pairs[0]);
                //startActivity(intent, options.toBundle());
                startActivity(new Intent(StartApplicationActivity.this, LoginActivity.class));
                finish();
            }, SPLASH_SCREEN);
        }

        //LAYOUT ONCLICK LISTENER
        /*startApplication.setOnClickListener(v -> {
            Intent intent = new Intent(StartApplicationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });*/
    }
}