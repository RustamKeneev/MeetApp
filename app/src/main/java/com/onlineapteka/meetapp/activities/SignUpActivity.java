package com.onlineapteka.meetapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.onlineapteka.meetapp.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.image_back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.text_sign_in).setOnClickListener(v -> {onBackPressed();});
    }
}