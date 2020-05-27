package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;


public class GPS extends AppCompatActivity implements OnMapReadyCallback {
    //SIDEBAR
    DrawerLayout sidebar;
    ActionBarDrawerToggle choice;
    // Apresentar os dados que estão na bd
    TextView name;
    ImageView image;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    // Para mudar de activities
    Intent intent;
    // Google Map
    GoogleMap map;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MAP
        setContentView(R.layout.activity_g_p_s);
        requestPermissions();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        // quando estiver pronto para carregar
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                        intent = new Intent(GPS.this, Movies.class);
                        startActivity(intent);
                        GPS.this.finish();
                        return true;
                    case R.id.list_series:
                        intent = new Intent(GPS.this, Series.class);
                        startActivity(intent);
                        GPS.this.finish();
                        return true;
                    case R.id.gps:
                        intent = new Intent(GPS.this, GPS.class);
                        startActivity(intent);
                        return true;
                    case R.id.qrcode:
                        Toast.makeText(GPS.this, "QR Code",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.userinfo:
                        intent = new Intent(GPS.this, User.class);
                        startActivity(intent);
                        GPS.this.finish();
                        return true;
                    case R.id.settings:
                        Toast.makeText(GPS.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(GPS.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    private void fetchLastLocation() {

    }

    //sidebar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    System.out.println(location.getLatitude());
                    System.out.println(location.getLongitude());
                    currentLocation = location;
                    LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.addMarker(new MarkerOptions().position(userLocation).title("User"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11));

                }
            }
        });


        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

    }


}
