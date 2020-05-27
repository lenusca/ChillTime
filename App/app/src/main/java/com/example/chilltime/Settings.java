package com.example.chilltime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    //XML
    Switch switchDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchDark=findViewById(R.id.switchDark);
    }

    public void addImage(View view) {
    }

    public void addPhoto(View view) {
    }

    public void addDate(View view) {
    }

    public void userSex(View view) {
    }

    public void darkOn(View view) {
        if(switchDark.isChecked()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
