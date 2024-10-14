package com.example.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DamagedContainerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damaged_container);
    }
    public void moveToNext(View view) {
        Intent intent = new Intent(DamagedContainerActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
