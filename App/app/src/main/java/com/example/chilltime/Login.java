package com.example.chilltime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class Login extends AppCompatActivity {
    // dar feedback quando carrega num botão
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createAccount(View view) {
        // Feedback visual quando carrega no botão
        view.startAnimation(buttonClick);

        // Ir para a página do create account
        Intent intent = new Intent(Login.this, CreateAccount.class);
        startActivity(intent);
    }

    public void menu(View view) {
        // Feedback visual quando carrega no botão
        view.startAnimation(buttonClick);

        // Ir para a página menu (por agora que não tem menu mandei para a do gps)
        Intent intent = new Intent(Login.this, GPS.class);
        startActivity(intent);

    }
}
