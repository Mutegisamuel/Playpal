package com.example.humungus.playpal.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BaseThemedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_themed);
    }
}
