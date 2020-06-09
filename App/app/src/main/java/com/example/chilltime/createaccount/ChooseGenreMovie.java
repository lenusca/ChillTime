package com.example.chilltime.createaccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chilltime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


public class ChooseGenreMovie extends AppCompatActivity {
    private LinkedList<Genre> listGenre = new LinkedList<>();
    private RequestQueue mQueue;
    private RecyclerView genreRecyclerView;
    private GenreListMovieAdapter mAdapter;
    String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US";

    String userID;
    // autenticação no firebase
    FirebaseAuth mAuth;
    // guardar os dados
    FirebaseFirestore mStore;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_genre_movie);
        genreRecyclerView = findViewById(R.id.recyclerview_genre);
        mAdapter = new GenreListMovieAdapter(listGenre);
        genreRecyclerView.setAdapter(mAdapter);
        genreRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        getGenre();
    }

    public void getGenre(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("genres");
                            //System.out.println("AQUIIIIIIIII "+jsonArray);
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject genre = jsonArray.getJSONObject(i);
                                listGenre.add(new Genre(genre.getString("id"), genre.getString("name")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue = Volley.newRequestQueue(this);
        mQueue.add(request);
    }

    public void onNext(View view) {
        Intent intent =  new Intent(ChooseGenreMovie.this, ChooseGenreSeries.class);
        startActivity(intent);
        ChooseGenreMovie.this.finish();

    }
}
