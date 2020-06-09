package com.example.chilltime.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chilltime.R;
import com.example.chilltime.createaccount.CreateAccount;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.facebook.FacebookSdk;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.chilltime.menu.DashBoard;

public class Login extends AppCompatActivity {

    // dar feedback quando carrega num botão
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0F);
    EditText email, password;
    ImageView logo;

    // autenticação no firebase
    FirebaseAuth mAuth;

    // autenticação com o google
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 007;

    // guardar a info no Firestore
    FirebaseFirestore mStore;
    String userID;
    DocumentReference documentReference;

    // autenticação com o facebook
    CallbackManager callbackManager;
    ImageButton facebookButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        facebookButton = findViewById(R.id.bt_fb_login);
        logo = findViewById(R.id.logo);

        sharedPreferences = getSharedPreferences(getString(R.string.pwd), Context.MODE_PRIVATE);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode){
            case Configuration.UI_MODE_NIGHT_NO:
                logo.setImageResource(R.drawable.logo);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                logo.setImageResource(R.drawable.logo_dark);
                break;
        }

        // autenticação no firebase
        mAuth = FirebaseAuth.getInstance();

        // verificar se está já login, isto quando tiver a parte do menu colocar a ir para la
        if(mAuth.getCurrentUser() != null){
            // mandar para o menu
            Intent intent = new Intent(Login.this, DashBoard.class);
            startActivity(intent);
            finish();
        }

        // guardar os dados no firestore
        mStore = FirebaseFirestore.getInstance();

        // Configurar o Sign in com o google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                // Feedback visual quando carrega no botão

                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("Deu o login");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Não deu o login");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        System.out.println(error.getMessage());
                    }
                });



        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Feedback visual quando carrega no botão
                view.startAnimation(buttonClick);
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile","email"));
            }
        });
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pwd), password.getText().toString());
        editor.apply();

        // autenticação com o email e pass
        mAuth.signInWithEmailAndPassword(saveemail, savepassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Se conseguiu registar o utilizador
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this,"Authentication successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, DashBoard.class);
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

    public void forgoutPassword(View view) {
        final EditText resetMail = new EditText(view.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter your email to received the link to reset the password");
        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setNeutralButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.sendPasswordResetEmail(resetMail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
        passwordResetDialog.create().show();

    }

    public void loginGoogle(View view) {
        // Feedback visual quando carrega no botão
        view.startAnimation(buttonClick);
        signIn();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                System.out.println("FAILED LOGIN WITH GOOGLE"+e);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println("signInWithCredential:success");
                            // guardar nome, foto, mail na bd
                            final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if(account != null) {
                                userID = mAuth.getCurrentUser().getUid();
                                System.out.println(userID);
                                final DocumentReference documentReference = mStore.collection("Users").document(userID);
                                // verificar se já existe o document0
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            // documento existe, logo não é preciso adicionar
                                            if (documentSnapshot.exists()) {
                                                // Ir para o menu
                                                Intent intent = new Intent(Login.this, DashBoard.class);
                                                startActivity(intent);
                                            } else {
                                                Map<String, Object> userData = new HashMap<>();
                                                // key(o que aparece no firebase antes dos :), value(depois dos :)
                                                userData.put("Name", account.getDisplayName());
                                                userData.put("Email", account.getEmail());
                                                userData.put("Image", account.getPhotoUrl().toString());
                                                documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        System.out.println("ADICIONADOOOO");
                                                    }
                                                });
                                                // Ir para o menu
                                                Intent intent = new Intent(Login.this, DashBoard.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure"+task.getException());
                        }
                    }
                });
    }

    // guardar o login no firebase
    private void handleFacebookAccessToken(AccessToken token) {
        System.out.println("handleFacebookAccessToken"+token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println("signInWithCredential:success");
                            // guardar nome, foto e email na bd
                            userID = mAuth.getCurrentUser().getUid();
                            final DocumentReference documentReference = mStore.collection("Users").document(userID);
                            // verifica se o documento já existe
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                      if(task.isSuccessful()){
                                          DocumentSnapshot documentSnapshot = task.getResult();
                                          // documento existe, logo não é preciso criar novo
                                          if(documentSnapshot.exists()){
                                              // Vai para o menu
                                              Intent intent = new Intent(Login.this, DashBoard.class);
                                              startActivity(intent);
                                          }
                                          // cria um novo documento, com o id do utilizador
                                          else{
                                              Map<String, Object> userData = new HashMap<>();
                                              // key(o que aparece no firebase antes dos :), value(depois dos :)
                                              userData.put("Name", user.getDisplayName());
                                              userData.put("Email", user.getEmail());
                                              userData.put("Image", user.getPhotoUrl().toString());
                                              documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void aVoid) {
                                                      System.out.println("ADICIONADOOOO");
                                                  }
                                              });
                                              // Ir para o menu
                                              Intent intent = new Intent(Login.this, DashBoard.class);
                                              startActivity(intent);
                                          }
                                      }
                                  }
                              }

                            );

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
