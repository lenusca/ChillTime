package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GPS extends AppCompatActivity implements OnMapReadyCallback, AdapterLocation.RecyclerViewClickListener {
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
    List<MarkerOptions> markers;
    // Ir buscar todos os cinemas
    RequestQueue mQueue;
    // Adapter
    AdapterLocation adapter;
    RecyclerView dataList;
    List<String> names;
    List<String> images;
    List<String> coordinates;
    List<String> closed;
    List<String> photo_reference;
    // Para mandar o id para outra activity(details)
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MAP
        setContentView(R.layout.activity_g_p_s);
        markers = new ArrayList<>();
        //Views
        dataList = findViewById(R.id.cinemas);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
                        Intent intent = new Intent(GPS.this, Settings.class);
                        startActivity(intent);
                        GPS.this.finish();
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



    //sidebar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Dialog a pedir premissão para usar a localização do tele
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        // Todos os cinemas
        cinemaLocalization();

        // Carregar e vai para a atividade que tem os filmes todos daquele cinema
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(GPS.this, MovieList.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(names.indexOf(marker.getTitle())+1));
                GPS.this.startActivity(intent);
                return false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ){
        userLocalization();
    }

    public void userLocalization(){
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    map.addMarker(new MarkerOptions().position(userLocation).title("User").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11));
                }
            }
        });
    }

    public void cinemaLocalization(){
        names = new ArrayList<>();
        images = new ArrayList<>();
        coordinates = new ArrayList<>();
        closed = new ArrayList<>();
        // para ir buscar as imagens
        // API google places
        mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://maps.googleapis.com/maps/api/place/textsearch/json?query=Cinema&location=40.626709%2C-8.644752&radius=100&key=AIzaSyBnmYS6fjLrp7mtdh-L79054GnpUIml2q4&fbclid=IwAR2Va3vheUJS9djO5V0s1c_FuU6l-XJZ7gQdCeFk1nZZdgWFc-m0iAdhBvw", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for(int i=0; i< results.length(); i++){
                                // Todos os cinemas
                                double lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                double lng = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                names.add(results.getJSONObject(i).getString("name"));
                                closed.add(""); // não era preciso este
                                coordinates.add("["+String.valueOf(lat)+", "+String.valueOf(lng)+"]");
                                // se tiver imagem vai buscar imagem
                                if(results.getJSONObject(i).has("photos")){
                                    images.add(results.getJSONObject(i).getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
                                }
                                // se não vai buscar icon
                                else{
                                    images.add(results.getJSONObject(i).getString("icon"));
                                }
                                map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(results.getJSONObject(i).getString("name")));
                                markers.add(new MarkerOptions().position(new LatLng(lat, lng)).title(results.getJSONObject(i).getString("name")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter = new AdapterLocation(GPS.this, names, coordinates, closed, images, GPS.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GPS.this, LinearLayoutManager.HORIZONTAL, false);
                        dataList.setLayoutManager(linearLayoutManager);
                        dataList.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR");
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }


    @Override
    public void onClick(View view, int position) {
         String locationName = names.get(position);
         for(MarkerOptions m: markers){
            if(m.getTitle().equals(locationName)){

                map.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
            }
         }
    }
}
