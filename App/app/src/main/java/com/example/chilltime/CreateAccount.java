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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {
    // dar feedback quando carrega num botão
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0F);
    EditText email, password, confirmPassword;
    // Dados para serem guardados
    EditText name;
    Button confirm;
    // autenticação no firebase
    FirebaseAuth mAuth;
    // guardar os dados
    FirebaseFirestore mStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        confirmPassword = findViewById(R.id.confirm_password);
        confirm = findViewById(R.id.confirm);
        name = findViewById(R.id.Name);

        // autenticação no firebase
        mAuth = FirebaseAuth.getInstance();

        // guardar os dados no firestore
        mStore = FirebaseFirestore.getInstance();

        // verificar se está já login, isto quando tiver a parte do menu colocar a ir para la
        if(mAuth.getCurrentUser() != null){
            // mandar para o menu
            Intent intent = new Intent(CreateAccount.this, GPS.class);
            startActivity(intent);
            finish();
        }


    }


    public void backToLogin(View view) {
        // Feedback visual quando carrega no botão
        view.startAnimation(buttonClick);

        // Ir para a página do login
        Intent intent = new Intent(CreateAccount.this, Login.class);
        startActivity(intent);
    }




    public void confirm(View view) {
        String saveemail = email.getText().toString();
        String savepassword = password.getText().toString();
        String savename = name.getText().toString();

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
        if(!confirmPassword.getText().toString().equals(savepassword)){
            password.setError("Password and Confirm Password must be the same");
            confirmPassword.setError("Password and Confirm Password must be the same");
            return;
        }

        // colocar aqui o progress bar

        // guardar os dados no firebase
        mAuth.createUserWithEmailAndPassword(saveemail, savepassword).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Se conseguiu registar o utilizador
                            Toast.makeText(CreateAccount.this,"User created successfully!", Toast.LENGTH_SHORT).show();
                            // Guardar os dados no documentation
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = mStore.collection("Users").document(userID);
                            Map<String, Object> userData = new HashMap<>();
                            // key(o que aparece no firebase antes dos :), value(depois dos :)
                            userData.put("Name", name.getText().toString());
                            userData.put("Email", email.getText().toString());
                            documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("ADICIONADOOOO");
                                }
                            });
                            // Ir para o login
                            Intent intent = new Intent(CreateAccount.this, Login.class);
                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // Se não conseguir registar o utilizador
                            Toast.makeText(CreateAccount.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                }
        );
    }
}
