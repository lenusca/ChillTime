package com.example.chilltime.createaccount;

public class Genre{
    String id;
    String name;

    public Genre(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }
}