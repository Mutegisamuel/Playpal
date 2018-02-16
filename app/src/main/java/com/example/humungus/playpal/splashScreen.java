package com.example.humungus.playpal;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class splashScreen extends AppCompatActivity {

    //    splashscreen timmer
    private static int SPLASH_TIME_OUT = 2000;

    private LottieAnimationView animationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.playAnimation();


        //        splashscreen code
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeintent = new Intent(splashScreen.this, MainActivity.class);
                startActivity(homeintent);
                animationView.cancelAnimation();
                finish();
            }

        },SPLASH_TIME_OUT);


    }
}
