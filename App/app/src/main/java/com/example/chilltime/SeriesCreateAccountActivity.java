package com.example.chilltime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SeriesCreateAccountActivity extends AppCompatActivity {
    //dados
    private List<String> idsGenreSerie = new ArrayList<>();
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
    AdapterSeries mAdapter;
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
        setContentView(R.layout.activity_series_create_account);
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
                if(documentSnapshot.get("GenreSeries")!=null){
                    idsGenreSerie = (List<String>) documentSnapshot.get("GenreSeries");
                }else{
                    idsGenreSerie = null;
                }
            }
        });
        if(idsGenreSerie!=null){
            System.out.println("IDSSSSSSSS GENRES MOVIES--------- "+idsGenreSerie);
            getMovies(idsGenreSerie);
        }

    }


    private void getMovies(List<String> ids){
        StringBuilder allIds = new StringBuilder();
        System.out.println("LISTTTTTTTTT -------------> "+ids);
        if(ids != null){
            for(int i=0; i<ids.size(); i++){
                allIds.append(ids.get(i)).append("%7C");
            }
            System.out.println("################################### allIds: "+allIds);
            mQueue = Volley.newRequestQueue(this);
            //ir buscar os filmes a partir do genero os populares
            String url = "https://api.themoviedb.org/3/discover/tv?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US&sort_by=popularity.desc&page=1%7C2&with_genres="+allIds+"&include_null_first_air_dates=false";
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
                    mAdapter = new AdapterSeries(SeriesCreateAccountActivity.this, names,  images, idsMovie, idsFavorites, imagesFavoritesUser,  idsWatches, imagesWatchesUser, timeWatches);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(SeriesCreateAccountActivity.this, 3, GridLayoutManager.VERTICAL, false);
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

    public void onDone(View view) {
        Intent intent = new Intent(this, DashBoard.class);
        startActivity(intent);
        SeriesCreateAccountActivity.this.finish();
    }

}
