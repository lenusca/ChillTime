package com.example.chilltime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<Movie> movieList;

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView movieImage;
        public TextView movieTitle, movieDescription, movieDuration;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            movieImage = itemView.findViewById(R.id.movieImage);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieDescription = itemView.findViewById(R.id.movieDesciption);
            movieDuration = itemView.findViewById(R.id.movieDuration);
        }
    }


    public MovieAdapter(ArrayList<Movie> movieList){
        this.movieList  = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_movie_card, parent, false);
        MovieViewHolder mVH = new MovieViewHolder(v);
        return mVH;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currMovie = movieList.get(position);
        holder.movieImage.setImageResource(currMovie.getMovieImage());
        holder.movieTitle.setText(currMovie.getMovieName());
        holder.movieDuration.setText(currMovie.getDuration());
        holder.movieDescription.setText(currMovie.getDescription());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }




}
