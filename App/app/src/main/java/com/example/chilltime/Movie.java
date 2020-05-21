package com.example.chilltime;

public class Movie {

    private int movieImageResource;
    private String movieName, duration, description;
    public Movie(int movieImageResource, String movieName, String duration, String description)
    {
        this.movieImageResource = movieImageResource;
        this.movieName = movieName;
        this.description = description;
        this.duration = duration;
    }

    public int getMovieImage(){return movieImageResource;}
    public String getMovieName(){return movieName;}
    public String getDuration(){return duration;}
    public String getDescription(){return description;}

}
