package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import static com.google.android.material.navigation.NavigationView.*;

public class GPS extends AppCompatActivity {
    //SIDEBAR
    DrawerLayout sidebar;
    ActionBarDrawerToggle choice;
    // Apresentar os dados que estão na bd
    TextView name;
    ImageView image;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_s);
        sidebar = (DrawerLayout)findViewById(R.id.sidebar);
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        name = headerView.findViewById(R.id.user_name);
        image = headerView.findViewById(R.id.user_photo);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();

        // ir buscar o documento relacionado com o utilizador, usando o uid
        final DocumentReference documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener< DocumentSnapshot >(){

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // dizer o que quer ir buscar
                String printname = "";
                String printimage = "";
                printname =documentSnapshot.getString("Name");
                printimage = documentSnapshot.getString("Image");
                System.out.println(printname);
                name.setText(printname);
                Picasso.get().load(printimage).into(image);
            }
        });
        choice = new ActionBarDrawerToggle(this, sidebar, R.string.Open, R.string.Close);
        choice.setDrawerIndicatorEnabled(true);

        sidebar.addDrawerListener(choice);
        choice.syncState();



        nav_view.setItemIconTintList(null);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch(id)
                {
                    case R.id.list_movies:
                        Toast.makeText(GPS.this, "MOVIES",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.list_series:
                        Toast.makeText(GPS.this, "SERIES",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.gps:
                        Toast.makeText(GPS.this, "GPS",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.qrcode:
                        Toast.makeText(GPS.this, "QR Code",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.userinfo:
                        Toast.makeText(GPS.this, "User Activities",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.settings:
                        Toast.makeText(GPS.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(GPS.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


}
