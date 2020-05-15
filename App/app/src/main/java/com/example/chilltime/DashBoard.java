package com.example.chilltime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashBoard extends AppCompatActivity {

    Button btMovies, btSeries, btGPS, btQrCode, btUserActivity, btSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Buttons
        btMovies = findViewById(R.id.btMovies);
        btSeries = findViewById(R.id.btSeries);
        btGPS = findViewById(R.id.btGPS);
        btQrCode = findViewById(R.id.btQrCode);
        btUserActivity = findViewById(R.id.btUserActivity);
        btSettings = findViewById(R.id.btSettings);


    }


    public void onClickMovies(View view) {
        Intent intent = new Intent(this, GPS.class);
        startActivity(intent);
    }
}
