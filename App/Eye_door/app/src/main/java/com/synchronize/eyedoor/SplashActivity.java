package com.synchronize.eyedoor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static final int SPLASH_TIME_OUT=1500;
    boolean isAlreadyLoggedIn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        autoLoginCheck();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent ;
                if(isAlreadyLoggedIn)
                {
                    intent = new Intent(SplashActivity.this,HomeActivity.class);
                }
                else
                    intent = new Intent(SplashActivity.this,SignInHomeActivity.class);
                startActivity(intent);
                finish();

            }
        },SPLASH_TIME_OUT);
    }

    private void autoLoginCheck() {
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
            isAlreadyLoggedIn = true;
        else
            isAlreadyLoggedIn= false;



    }
}