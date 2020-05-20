package com.example.chilltime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.regex.Pattern;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class Details extends AppCompatActivity {
    // Intent
    String idMovie;
    // Buscar os dados
    RequestQueue mQueue;
    // Views
    ImageView backgroundImage;
    ImageView movieImage;
    TextView title;
    TextView releaseDate;
    JustifyTextView overview;
    TextView genres;
    ImageView companie1;
    ImageView companie2;
    ImageView companie3;
    ImageView companie4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // Receber o id
        Intent intent = getIntent();
        idMovie = intent.getStringExtra(AdapterMovies.EXTRA_MESSAGE).toString();
        System.out.println(idMovie);
        // Views
        backgroundImage = findViewById(R.id.secondImage);
        movieImage = findViewById(R.id.firstImage);
        title = findViewById(R.id.title);
        releaseDate = findViewById(R.id.releaseDate);
        overview = findViewById(R.id.justifyTextView);
        genres = findViewById(R.id.genres);
        companie1 = findViewById(R.id.companie1);
        companie2 = findViewById(R.id.companie2);
        companie3 = findViewById(R.id.companie3);
        companie4 = findViewById(R.id.companie4);


        // API dos filmes, buscar detalhes do filme e atualisar na view respetiva
        mQueue = Volley.newRequestQueue(this);
        // buscar os filmes todos apartir do genero, do mais famoso para o menos
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+idMovie+"?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getString("backdrop_path")).into(backgroundImage);
                            Picasso.get().load("https://image.tmdb.org/t/p/original/"+response.getString("poster_path")).into(movieImage);
                            if(response.getString("original_title").contains(":")){
                                title.setText(response.getString("original_title").split(":")[0]);
                            }
                            else if(response.getString("original_title").contains("(")){
                                title.setText(response.getString("original_title").split(Pattern.quote("("))[0]);
                            }
                            else{
                                title.setText(response.getString("original_title"));
                            }

                            releaseDate.setText(response.getString("release_date").split("-")[0]);
                            overview.setText(response.getString("overview"));
                            String genresString="";
                            genresString = response.getJSONArray("genres").getJSONObject(0).getString("name");
                            // se tiver mais do que 2 generos
                            if(response.getJSONArray("genres").length() > 1){
                                for(int i=1; i<response.getJSONArray("genres").length(); i++){
                                    genresString = genresString + ", " + response.getJSONArray("genres").getJSONObject(i).get("name");
                                }

                            }
                            genres.setText(genresString);
                            //
                            int companies =1;
                            for(int i=0; i<response.getJSONArray("production_companies").length(); i++){
                                if(response.getJSONArray("production_companies").getJSONObject(i).getString("logo_path") != "null"){
                                    System.out.println(response.getJSONArray("production_companies").getJSONObject(i).get("logo_path"));
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
