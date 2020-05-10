package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    // dar feedback quando carrega num botão
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0F);
    EditText email, password;
    Button login;
    // autenticação no firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        login = findViewById(R.id.Login);

        // autenticação no firebase
        mAuth = FirebaseAuth.getInstance();

        // verificar se está já login, isto quando tiver a parte do menu colocar a ir para la
        if(mAuth.getCurrentUser() != null){
            // mandar para o menu
            Intent intent = new Intent(Login.this, GPS.class);
            startActivity(intent);
            finish();
        }


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
        String saveemail = email.getText().toString();
        String savepassword = password.getText().toString();

        // verificar se o que foi inserido no editText está correto
        if(TextUtils.isEmpty(saveemail)){
            email.setError("Email is required!");
            return;
        }
        if(TextUtils.isEmpty(savepassword)){
            password.setError("Password is required!");
            return;
        }
        if(savepassword.length() < 6){
            password.setError("Password need to have 6 characters!");
            return;
        }

        // autenticação com o email e pass
        mAuth.signInWithEmailAndPassword(saveemail, savepassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Se conseguiu registar o utilizador
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this,"Authentication successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, GPS.class);
                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // Se não conseguir registar o utilizador
                            Toast.makeText(Login.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });


    }
}
