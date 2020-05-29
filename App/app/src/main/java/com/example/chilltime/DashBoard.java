package com.example.chilltime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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


    public void GPS(View view) {
        //isServiceOK();
        Intent intent = new Intent(this, GPS.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void Movies(View view) {
        Intent intent = new Intent(this, Movies.class);
        startActivity(intent);
        DashBoard.this.finish();
    }


    public void Series(View view) {
        Intent intent = new Intent(this, Series.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void QRCode(View view) {
        Intent intent = new Intent(this, MovieList.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void Settings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void UserActivity(View view) {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    // GOOGLE MAPS
    public boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DashBoard.this);
        if(available == ConnectionResult.SUCCESS){
            System.out.println("Google play service is working");
            return true;
        }
        return false;
    }
}
