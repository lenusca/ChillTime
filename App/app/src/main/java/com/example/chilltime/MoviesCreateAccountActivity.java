package com.example.chilltime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MoviesCreateAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    //dados
    private List<String> idsGenreMovie = new ArrayList<>();
    List<String> names = new LinkedList<>();  //nome do movie
    List<String> images = new LinkedList<>(); //imagem do movie
    List<Long> idsMovie = new LinkedList<>(); //id do movie
    List<Long> idsFavorites = new LinkedList<>(); //ids dos movies favoritos
    List<String> imagesFavoritesUser = new LinkedList<>(); //imagens dos filmes favoritos do user
    List<Long> idsWatches = new LinkedList<>(); //ids dos movies vistos pelo user
    List<String> imagesWatchesUser= new LinkedList<>(); //imagens dos filmes vistos pelo user
    long timeWatches = 0;
    //XML
    RecyclerView dataList;
    AdapterMovies mAdapter;
    //Volley
    RequestQueue mQueue;
    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_create_account);
        dataList = findViewById(R.id.dataList);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();
        documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            //ir buscar se não tiver null
                if(documentSnapshot.get("GenreMovies")!=null){
                    idsGenreMovie = (List<String>) documentSnapshot.get("GenreMovies");
                    getMovies(idsGenreMovie);
                }
            }
        });
        if(idsGenreMovie!=null){
            System.out.println("IDSSSSSSSS GENRES MOVIES--------- "+idsGenreMovie);
            getMovies(idsGenreMovie);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

  /*  private void getIds(){
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //ir buscar se não tiver null
                if(documentSnapshot.get("GenreMovies")!=null){
                    idsGenreMovie = (List<String>) documentSnapshot.get("GenreMovies");
                }else{
                    idsGenreMovie = null;
                }
            }
        });
    }*/

    private void getMovies(List<String> ids){
        StringBuilder allIds = new StringBuilder();
        if(ids != null){
            for(int i=0; i<ids.size(); i++){
                allIds.append(ids.get(i)).append("%7C");
            }
            System.out.println("################################### allIds: "+allIds);
            mQueue = Volley.newRequestQueue(this);
            //ir buscar os filmes a partir do genero os populares
            String url = "https://api.themoviedb.org/3/discover/movie?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=true&page=1%7C2&with_genres="+allIds;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int size = 0;
                        JSONArray results = response.getJSONArray("results");
                        if(results.length()>15){ size = 15;}
                        else {size = results.length();}
                        for(int i=0; i<size; i++){
                            JSONObject movieData = results.getJSONObject(i);
                            if(!movieData.getString("poster_path").equals("null")){
                                //id
                                idsMovie.add(movieData.getLong("id"));
                                //image
                                images.add(movieData.getString("poster_path"));
                                //nome
                                names.add(movieData.getString("title"));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    // mandar para o adapter que vai mandar para o reciclerview
                    mAdapter = new AdapterMovies(MoviesCreateAccountActivity.this, names,  images, idsMovie, idsFavorites, imagesFavoritesUser,  idsWatches, imagesWatchesUser, timeWatches);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MoviesCreateAccountActivity.this, 3, GridLayoutManager.VERTICAL, false);
                    dataList.setLayoutManager(gridLayoutManager);
                    // colocar os dados no view
                    dataList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            mQueue.add(request);
        }
    }

    public void onNext(View view) {
        Intent intent = new Intent(this, SeriesCreateAccountActivity.class);
        startActivity(intent);
        MoviesCreateAccountActivity.this.finish();
    }
}
