package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.DialogRedirect;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
    //XML
    Switch switchDark;
    //SideBar
    ActionBarDrawerToggle choice;
    DrawerLayout sidebar;
    TextView name;
    ImageView image;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    FirebaseUser firebaseUser;
    DocumentReference documentReference;
    Intent intent;

    SharedPreferences sharedPreferences;

    String dark="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchDark=findViewById(R.id.switchDark);

        sharedPreferences = getSharedPreferences(getString(R.string.pwd), Context.MODE_PRIVATE);

        //SideBar
        sidebar = (DrawerLayout)findViewById(R.id.sidebar);
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        name = headerView.findViewById(R.id.user_name);
        image = headerView.findViewById(R.id.user_photo);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();
        firebaseUser = mAuth.getCurrentUser();
        // ir buscar o documento relacionado com o utilizador, usando o uid
        documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // dizer o que quer ir buscar
                String printname = "";
                String printimage = "";
                dark = documentSnapshot.getString("Dark");
                printname =documentSnapshot.getString("Name");
                printimage = documentSnapshot.getString("Image");
                System.out.println(printname);
                name.setText(printname);
                Picasso.get().load(printimage).into(image);

                if(dark!=null){
                    if(dark.equals("1")){
                        switchDark.setChecked(true);
                    }else{
                        switchDark.setChecked(false);
                    }
                }

            }
        });

        choice = new ActionBarDrawerToggle(this, sidebar, R.string.Open, R.string.Close);
        sidebar.addDrawerListener(choice);
        choice.setDrawerIndicatorEnabled(true);
        choice.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_view.setItemIconTintList(null);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch(id)
                {
                    case R.id.list_movies:
                        intent = new Intent(Settings.this, Movies.class);
                        startActivity(intent);
                        Settings.this.finish();
                        return true;
                    case R.id.list_series:
                        intent = new Intent(Settings.this, Series.class);
                        startActivity(intent);
                        Settings.this.finish();
                        return true;
                    case R.id.gps:
                        intent = new Intent(Settings.this, GPS.class);
                        startActivity(intent);
                        return true;
                    case R.id.qrcode:
                        intent = new Intent(Settings.this, QRCode.class);
                        startActivity(intent);
                        return true;
                    case R.id.userinfo:
                        intent = new Intent(Settings.this, User.class);
                        startActivity(intent);
                        Settings.this.finish();
                        return true;
                    case R.id.settings:
                        intent = new Intent(Settings.this, Settings.class);
                        startActivity(intent);
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(Settings.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    //sidebar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceType")
    public void darkOn(View view) {
        Map<String, Object> userData = new HashMap<>();
        if(switchDark.isChecked()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dark="1";
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            dark="0";
        }
        userData.put("Dark", dark);
        documentReference.update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("ADICIONADOOOO");
            }
        });
    }

    public void onEditAccount(View view) {
        Intent intent = new Intent(this, EditAccount.class);
        startActivity(intent);
    }

    public void onRemove(View view) {
        final String resultPwd = sharedPreferences.getString(getString(R.string.pwd), "");
        AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("This will completely delete your account from our system");
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Settings.this, "Remove Users!", Toast.LENGTH_SHORT).show();
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Settings.this, "Account deleted!", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(Settings.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(Settings.this, task.getException().getMessage()+"\nTry again", Toast.LENGTH_LONG).show();
                                    System.out.println("MAILLLL: "+firebaseUser.getEmail());
                                    System.out.println("PASSWORDDD: "+resultPwd);
                                    AuthCredential credential = EmailAuthProvider
                                            .getCredential(firebaseUser.getEmail(), resultPwd);
                                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            System.out.println("re-authenticate");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void onLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        intent = new Intent(Settings.this, Login.class);
        startActivity(intent);
        finish();
    }

}
