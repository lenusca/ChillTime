package com.example.chilltime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class DetailsSerie extends AppCompatActivity {
    // Views
    ImageView backgroundImage;
    ImageView serieImage;
    TextView title;
    TextView releaseDate;
    JustifyTextView overview;
    TextView genres;
    ImageView companie1;
    ImageView companie2;
    ImageView companie3;
    ImageView companie4;
    ImageButton favoriteButton;
    ImageButton watchButton;
    TextView runtime;
    TextView seasons;
    TextView episodes;
    // API
    String idSerie;
    RequestQueue mQueue;
    // Firebase
    String image;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    DocumentReference documentReference;
    String userID;
    List<Long> idsFavorites;
    List<String> imageFavorites;
    List<Long> idsWatches;
    List<String> imageWatches;
    int time = 0;
    long timeWatches;
    // video
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";
    String idYoutube;
    // dar feedback quando carrega num botão
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_serie);
        //Views
        backgroundImage = findViewById(R.id.secondImage);
        serieImage = findViewById(R.id.firstImage);
        title = findViewById(R.id.title);
        releaseDate = findViewById(R.id.releaseDate);
        overview = findViewById(R.id.justifyTextView);
        genres = findViewById(R.id.genres);
        companie1 = findViewById(R.id.companie1);
        companie2 = findViewById(R.id.companie2);
        companie3 = findViewById(R.id.companie3);
        companie4 = findViewById(R.id.companie4);
        favoriteButton = findViewById(R.id.addFavorites);
        watchButton = findViewById(R.id.addViews);
        runtime = findViewById(R.id.runtimeValue);
        seasons = findViewById(R.id.seasons);
        episodes = findViewById(R.id.episodes);

        // receber o id da serie
        Intent intent = getIntent();
        idSerie = intent.getStringExtra(AdapterSeries.EXTRA_MESSAGE);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.get("FavoritesSeries") == null || documentSnapshot.get("FavoritesImagesSeries") == null ){
                    idsFavorites = new ArrayList<>();
                    imageFavorites = new ArrayList<>();
                }
                else{
                    idsFavorites = (List<Long>) documentSnapshot.get("FavoritesSeries");
                    imageFavorites = (List<String>) documentSnapshot.get("FavoritesImagesSeries");
                    // Aparece o emoji preenchido ou o outro
                    if(idsFavorites.contains(Long.parseLong(idSerie))){
                        favoriteButton.setImageResource(R.drawable.removefavorite);
                    }
                }
                if(documentSnapshot.get("WatchesSeries") == null || documentSnapshot.get("WatchesImagesSeries") == null || documentSnapshot.get("WatchesMoviesTime") == null){
                    idsWatches = new ArrayList<>();
                    imageWatches = new ArrayList<>();
                    timeWatches = 0;
                }
                else {
                    idsWatches = (List<Long>) documentSnapshot.get("WatchesSeries");
                    imageWatches = (List<String>) documentSnapshot.get("WatchesImagesSeries");
                    timeWatches = (long) documentSnapshot.get("WatchesSeriesTime");
                    if(idsWatches.contains(Long.parseLong(idSerie))){
                        watchButton.setImageResource(R.drawable.removewatch);
                    }
                }
            }
        });

        // show details
        mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/tv/"+idSerie+"?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getString("backdrop_path")).into(backgroundImage);
                            image = response.getString("poster_path");
                            Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getString("poster_path")).into(serieImage);
                            if(response.getString("original_name").contains(":")){
                                title.setText(response.getString("original_name").split(":")[0]);
                            }
                            else if(response.getString("original_name").contains("(")){
                                title.setText(response.getString("original_name").split(Pattern.quote("("))[0]);
                            }
                            else{
                                title.setText(response.getString("original_name"));
                            }
                            releaseDate.setText(response.getString("first_air_date").split("-")[0]);
                            overview.setText(response.getString("overview"));
                            time = response.getInt("number_of_episodes")*response.getJSONArray("episode_run_time").getInt(0);
                            runtime.setText(Integer.toString(response.getJSONArray("episode_run_time").getInt(0))+"min");
                            String genresString="";
                            genresString = response.getJSONArray("genres").getJSONObject(0).getString("name");
                            // se tiver mais do que 2 generos
                            if(response.getJSONArray("genres").length() > 1){
                                for(int i=1; i<response.getJSONArray("genres").length(); i++){
                                    genresString = genresString + ", " + response.getJSONArray("genres").getJSONObject(i).get("name");
                                }
                            }
                            genres.setText(genresString);
                            int companies =1;
                            for(int i=0; i<response.getJSONArray("production_companies").length(); i++){
                                if(response.getJSONArray("production_companies").getJSONObject(i).getString("logo_path") != "null"){
                                    if(companies == 1){
                                        Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getJSONArray("production_companies").getJSONObject(i).get("logo_path")).into(companie1);
                                    }
                                    else if(companies == 2){
                                        Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getJSONArray("production_companies").getJSONObject(i).get("logo_path")).into(companie2);
                                    }
                                    else if(companies == 3){
                                        Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getJSONArray("production_companies").getJSONObject(i).get("logo_path")).into(companie3);
                                    }
                                    else if(companies == 4){
                                        Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getJSONArray("production_companies").getJSONObject(i).get("logo_path")).into(companie4);
                                    }
                                    companies++;
                                }
                            }
                            seasons.setText(Integer.toString(response.getInt("number_of_seasons")));
                            episodes.setText(Integer.toString(response.getInt("number_of_episodes")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    // adicionar imagem, id ao firebase
    public void favorite(View view) {
        Map<String, Object> userFavorites = new HashMap<>();
        // Se não estiver já na bd, adiciona
        if(favoriteButton.getDrawable().getConstantState().equals((DetailsSerie.this).getResources().getDrawable(R.drawable.addfavorite).getConstantState())){
            imageFavorites.add(image);
            idsFavorites.add(Long.parseLong(idSerie));
            userFavorites.put("FavoritesSeries", idsFavorites);
            userFavorites.put("FavoritesImagesSeries", imageFavorites);
            documentReference.set(userFavorites,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("ADICIONADOOOO");
                }
            });
            favoriteButton.setImageResource(R.drawable.removefavorite);
        }
        // Verificar se está adicionado, se estiver vai remover a bd se carregar de novo
        else if(favoriteButton.getDrawable().getConstantState().equals(this.getResources().getDrawable(R.drawable.removefavorite).getConstantState())){
            idsFavorites.removeAll(Collections.singleton(Long.parseLong(idSerie)));
            imageFavorites.removeAll(Collections.singleton(image));
            userFavorites.put("FavoritesSeries", idsFavorites);
            userFavorites.put("FavoritesImagesSeries", imageFavorites);
            documentReference.set(userFavorites,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("REMOVIDOOOO");
                }
            });
            favoriteButton.setImageResource(R.drawable.addfavorite);
        }
    }

    // adicionar imagem, id e o tempo ao firebase
    public void watch(View view) {
        Map<String, Object> userWatches = new HashMap<>();
        // Se não estiver já na bd, adiciona
        if(watchButton.getDrawable().getConstantState().equals(DetailsSerie.this.getResources().getDrawable(R.drawable.addwatch).getConstantState())) {
            idsWatches.add(Long.parseLong(idSerie));
            imageWatches.add(image);
            userWatches.put("WatchesSeries", idsWatches);
            userWatches.put("WatchesImagesSeries", imageWatches);
            timeWatches = timeWatches + time;
            userWatches.put("WatchesSeriesTime", timeWatches);
            documentReference.set(userWatches, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("ADICIONADOOOO");
                }
            });
            watchButton.setImageResource(R.drawable.removewatch);
        }
        // Verificar se está adicionado, se estiver vai remover a bd se carregar de novo
        else if(watchButton.getDrawable().getConstantState().equals(DetailsSerie.this.getResources().getDrawable(R.drawable.removewatch).getConstantState())){
            idsWatches.removeAll(Collections.singleton(Long.parseLong(idSerie)));
            imageWatches.removeAll(Collections.singleton(image));
            userWatches.put("WatchesSeries", idsWatches);
            userWatches.put("WatchesImagesSeries", imageWatches);
            timeWatches = timeWatches - time;
            userWatches.put("WatchesSeriesTime", timeWatches);
            documentReference.set(userWatches,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("REMOVIDOOOO");
                }
            });
            watchButton.setImageResource(R.drawable.addwatch);
        }
    }

    // ver o trailer
    public void watchVideo(View view) {
        // Feedback visual quando carrega no botão
        view.startAnimation(buttonClick);
        // 1) ir buscar o id para o youtube, apartir da API
        mQueue = Volley.newRequestQueue(this);
        // buscar os series todos apartir do genero, do mais famoso para o menos
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/tv/"+idSerie+"/videos?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            idYoutube = response.getJSONArray("results").getJSONObject(0).getString("key");
                            // 2) Mandar para a atividade que tem o videoview
                            Intent intent = new Intent(DetailsSerie.this, PlayVideo.class);
                            intent.putExtra(EXTRA_MESSAGE, idYoutube);
                            DetailsSerie.this.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
}
