package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Series extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
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
    // Buscar os dados
    RequestQueue mQueue;
    List<String> serieName;
    List<String> serieImage;
    List<Integer> serieId;
    List<Integer> idsFavorites;
    List<String> imageFavorites;
    List<Integer> idsWatches;
    List<String> imageWatches;
    long timeWatches;

    AdapterSeries adapter;
    RecyclerView dataList;
    // Botão dos géneros
    Spinner genreList;
    List<Integer> genresId;
    List<String> genresName;
    // Search
    SearchView searchSerie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        dataList = findViewById(R.id.dataList);
        genreList = findViewById(R.id.choose_genre);
        genreList.setOnItemSelectedListener(this);
        searchSerie = findViewById(R.id.search_serie);

        // SideBar
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
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // dizer o que quer ir buscar
                String printname = "";
                String printimage = "";
                if(documentSnapshot.get("FavoritesSeries") == null || documentSnapshot.get("FavoritesImagesSeries") == null){
                    idsFavorites = new ArrayList<>();
                    imageFavorites = new ArrayList<>();
                }
                else{
                    idsFavorites = (List<Integer>) documentSnapshot.get("FavoritesSeries");
                    imageFavorites = (List<String>) documentSnapshot.get("FavoritesImagesSeries");
                }
                if(documentSnapshot.get("WatchesSeries") == null || documentSnapshot.get("WatchesImagesSeries") == null || documentSnapshot.get("WatchesSeriesTime") == null){
                    idsWatches = new ArrayList<>();
                    imageWatches = new ArrayList<>();
                    timeWatches = 0;
                }
                else {
                    idsWatches = (List<Integer>) documentSnapshot.get("WatchesSeries");
                    imageWatches = new ArrayList<>();
                    timeWatches = (long) documentSnapshot.get("WatchesSeriesTime");

                }
                printname =documentSnapshot.getString("Name");
                printimage = documentSnapshot.getString("Image");
                name.setText(printname);
                Picasso.get().load(printimage).into(image);
            }
        });

        choice = new ActionBarDrawerToggle(this, sidebar, R.string.Open, R.string.Close);
        choice.setDrawerIndicatorEnabled(true);

        sidebar.addDrawerListener(choice);
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
                        intent = new Intent(Series.this, Movies.class);
                        startActivity(intent);
                        Series.this.finish();
                        return true;
                    case R.id.list_series:
                        intent = new Intent(Series.this, Series.class);
                        startActivity(intent);
                        return true;
                    case R.id.gps:
                        intent = new Intent(Series.this, GPS.class);
                        startActivity(intent);
                        Series.this.finish();
                        return true;
                    case R.id.qrcode:
                        Toast.makeText(Series.this, "QR Code",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.userinfo:
                        Toast.makeText(Series.this, "User Activities",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.settings:
                        Toast.makeText(Series.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(Series.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Dados a API
        serieName = new ArrayList<>();
        serieImage = new ArrayList<>();
        serieId = new ArrayList<>();
        genresId = new ArrayList<>();
        genresName = new ArrayList<>();
        // Dropdown
        getGenres();
        getSeries("");
        // Search
        searchSerie.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                serieId.clear();
                serieImage.clear();
                serieName.clear();
                // API das series
                mQueue = Volley.newRequestQueue(Series.this);
                // buscar as series todas apartir de uma palavra
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,"https://api.themoviedb.org/3/search/tv?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US&query="+s, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray results = response.getJSONArray("results");
                                    for(int i=0; i<results.length(); i++){
                                        JSONObject serieData = results.getJSONObject(i);
                                        if(!serieData.getString("poster_path").equals("null")){
                                            //id
                                            serieId.add(serieData.getInt("id"));
                                            //name
                                            serieName.add(serieData.getString("name"));
                                            //image
                                            serieImage.add(serieData.getString("poster_path"));
                                        }

                                    }
                                    System.out.println(serieId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // mandar para o adapter que vai mandar para o reciclerview
                                adapter = new AdapterSeries(Series.this, serieName, serieImage, serieId, idsFavorites, imageFavorites, idsWatches, imageWatches, timeWatches);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(Series.this, 3, GridLayoutManager.VERTICAL, false);
                                dataList.setLayoutManager(gridLayoutManager);
                                // colocar os dados no view
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

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    //sidebar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void getSeries(String genre) {
        serieId.clear();
        serieImage.clear();
        serieName.clear();
        if(genre.equals("")){
            genre="16";
        }
        // API das séries
        mQueue = Volley.newRequestQueue(this);
        // buscar os filmes todos apartir do genero, do mais famoso para o menos
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/discover/tv?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US&sort_by=popularity.desc&page=1&timezone=America%2FNew_York&with_genres="+genre+"&include_null_first_air_dates=false", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for(int i=0; i<results.length(); i++){
                                JSONObject serieData = results.getJSONObject(i);
                                if(!serieData.getString("poster_path").equals("null")){
                                    //id
                                    serieId.add(serieData.getInt("id"));
                                    //name
                                    serieName.add(serieData.getString("name"));
                                    //image
                                    serieImage.add(serieData.getString("poster_path"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // mandar para o adapter que vai mandar para o reciclerview
                        adapter = new AdapterSeries(Series.this, serieName, serieImage, serieId, idsFavorites, imageFavorites, idsWatches, imageWatches, timeWatches);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(Series.this, 3, GridLayoutManager.VERTICAL, false);
                        dataList.setLayoutManager(gridLayoutManager);
                        // colocar os dados no view
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

    private void getGenres(){
        // API dos filmes
        mQueue = Volley.newRequestQueue(this);
        // buscar os generos de filmes
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/genre/tv/list?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("genres");
                            for(int i=0; i<results.length(); i++){
                                JSONObject genresData = results.getJSONObject(i);
                                //id
                                genresId.add(genresData.getInt("id"));
                                //name
                                genresName.add(genresData.getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // mandar para o dropdown
                        ArrayAdapter adapter = new ArrayAdapter(Series.this, android.R.layout.simple_spinner_item, genresName);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        genreList.setAdapter(adapter);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.choose_genre){
            String value = adapterView.getItemAtPosition(i).toString();
            getSeries(genresId.get(i).toString());

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
