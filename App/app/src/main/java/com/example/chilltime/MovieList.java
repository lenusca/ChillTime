package com.example.chilltime;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovieList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        ArrayList<Movie> moviesList = new ArrayList<>();



        //Exemplos de Filmes -- TODO
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie1", "123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.com_facebook_close, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));
        moviesList.add(new Movie(R.drawable.gps, "Movie2","123min", "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate."));



        mRecyclerView = findViewById(R.id.moviesRecyclerView);
        mRecyclerView.setHasFixedSize(true); //?? maybe not TODO
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MovieAdapter(moviesList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



    }
}
