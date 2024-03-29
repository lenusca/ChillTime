package com.example.chilltime.movies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chilltime.gps.GPS;
import com.example.chilltime.login.Login;
import com.example.chilltime.movies.adapters.AdapterMovies;
import com.example.chilltime.qrcode.QRCode;
import com.example.chilltime.R;
import com.example.chilltime.series.Series;
import com.example.chilltime.settings.Settings;
import com.example.chilltime.UserActivity.User;
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

public class Movies extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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
    List<String> movieName;
    List<String> movieImage;
    List<Long> movieId;
    List<Long> idsFavorites;
    List<String> imageFavorites;
    List<Long> idsWatches;
    List<String> imageWatches;
    List<String> idGenderMoviesWatch;
    List<String> idGenresMovies;
    long timeWatches;

    AdapterMovies adapter;
    RecyclerView dataList;
    // Botão dos géneros
    Spinner genreList;
    List<Integer> genresId;
    List<String> genresName;
    // search
    SearchView searchMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        dataList = findViewById(R.id.dataList);
        genreList = findViewById(R.id.choose_genre);
        genreList.setOnItemSelectedListener(this);
        searchMovie = findViewById(R.id.search_movie);


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
                if(documentSnapshot.get("FavoritesMovie") == null || documentSnapshot.get("FavoritesImagesMovie") == null ){
                    idsFavorites = new ArrayList<>();
                    imageFavorites = new ArrayList<>();
                }
                else{
                    idsFavorites = (List<Long>) documentSnapshot.get("FavoritesMovie");
                    imageFavorites = (List<String>) documentSnapshot.get("FavoritesImagesMovie");
                }
                if(documentSnapshot.get("IdGenderMoviesWatch")==null || documentSnapshot.get("WatchesMovies") == null || documentSnapshot.get("WatchesImagesMovies") == null || documentSnapshot.get("WatchesMoviesTime") == null){
                    idsWatches = new ArrayList<>();
                    imageWatches = new ArrayList<>();
                    idGenderMoviesWatch = new ArrayList<>();
                    timeWatches = 0;
                }
                else {
                    idsWatches = (List<Long>) documentSnapshot.get("WatchesMovies");
                    imageWatches = (List<String>) documentSnapshot.get("WatchesImagesMovies");
                    idGenderMoviesWatch = (List<String>) documentSnapshot.get("IdGenderMoviesWatch");
                    timeWatches = (long) documentSnapshot.get("WatchesMoviesTime");
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
                        intent = new Intent(Movies.this, Movies.class);
                        startActivity(intent);
                        return true;
                    case R.id.list_series:
                        intent = new Intent(Movies.this, Series.class);
                        startActivity(intent);
                        Movies.this.finish();
                        return true;
                    case R.id.gps:
                        intent = new Intent(Movies.this, GPS.class);
                        startActivity(intent);
                        Movies.this.finish();
                        return true;
                    case R.id.qrcode:
                        intent = new Intent(Movies.this, QRCode.class);
                        startActivity(intent);
                        //Movies.this.finish();
                        return true;
                    case R.id.userinfo:
                        intent = new Intent(Movies.this, User.class);
                        startActivity(intent);
                        Movies.this.finish();
                        return true;
                    case R.id.settings:
                        Intent intent = new Intent(Movies.this, Settings.class);
                        startActivity(intent);
                        Movies.this.finish();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(Movies.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Dados a API
        movieName = new ArrayList<>();
        movieImage = new ArrayList<>();
        movieId = new ArrayList<>();
        genresId = new ArrayList<>();
        genresName = new ArrayList<>();
        idGenresMovies = new ArrayList<>();
        // Dropdown
        getGenres();
        getMovies("");
        // SEARCH
        searchMovie.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                movieId.clear();
                movieImage.clear();
                movieName.clear();
                // API dos filmes
                mQueue = Volley.newRequestQueue(Movies.this);
                // buscar os filmes todos apartir de uma palavra
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,"https://api.themoviedb.org/3/search/movie?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US&query="+s, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray results = response.getJSONArray("results");
                                    for(int i=0; i<results.length(); i++){
                                        JSONObject movieData = results.getJSONObject(i);
                                        if(!movieData.getString("poster_path").equals("null")){
                                            //id
                                            movieId.add(movieData.getLong("id"));
                                            //name
                                            movieName.add(movieData.getString("title"));
                                            //image
                                            movieImage.add(movieData.getString("poster_path"));
                                            //genre
                                            idGenresMovies.add(movieData.getString("genre_ids"));
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // mandar para o adapter que vai mandar para o reciclerview
                                adapter = new AdapterMovies(Movies.this, movieName, movieImage, movieId, idsFavorites, imageFavorites, idsWatches, imageWatches, timeWatches, idGenderMoviesWatch, idGenresMovies);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(Movies.this, 3, GridLayoutManager.VERTICAL, false);
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

    private void getMovies(String genre){
        movieId.clear();
        movieImage.clear();
        movieName.clear();

        if(genre.equals("")){
            genre = "28";
        }

        // API dos filmes
        mQueue = Volley.newRequestQueue(this);
        // buscar os filmes todos apartir do genero, do mais famoso para o menos
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/discover/movie?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=true&with_genres="+genre, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for(int i=0; i<results.length(); i++){
                                JSONObject movieData = results.getJSONObject(i);
                                if(!movieData.getString("poster_path").equals("null")){
                                    //id
                                    movieId.add(movieData.getLong("id"));
                                    //name
                                    movieName.add(movieData.getString("title"));
                                    //image
                                    movieImage.add(movieData.getString("poster_path"));
                                    //genre
                                    System.out.println("IDGENRESMOVIES "+movieData.getString("genre_ids"));
                                    idGenresMovies.add(movieData.getString("genre_ids"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // mandar para o adapter que vai mandar para o reciclerview
                        adapter = new AdapterMovies(Movies.this, movieName, movieImage, movieId, idsFavorites, imageFavorites, idsWatches, imageWatches, timeWatches, idGenderMoviesWatch, idGenresMovies);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(Movies.this, 3, GridLayoutManager.VERTICAL, false);
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/genre/movie/list?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
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
                        ArrayAdapter adapter = new ArrayAdapter(Movies.this, android.R.layout.simple_spinner_item, genresName);
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

    // para a dropdown, quando seleciona o genero
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.choose_genre){
            String value = adapterView.getItemAtPosition(i).toString();
            getMovies(genresId.get(i).toString());

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
