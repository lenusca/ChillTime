package com.example.chilltime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterCinemas extends RecyclerView.Adapter<AdapterCinemas.ViewHolder> {
    Context context;
    List<String> names;
    List<String> description;
    List<String> runtimes;
    List<String> images;
    LayoutInflater inflater;

    public AdapterCinemas(Context context, List<String> names, List<String> description, List<String> runtimes, List<String> images){
        this.names = new ArrayList<>();
        this.description = new ArrayList<>();
        this.runtimes = new ArrayList<>();
        this.images = new ArrayList<>();

        this.context = context;
        this.names = names;
        this.description = description;
        this.runtimes = runtimes;
        this.images = images;
        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ligação com o gridlayou
        View view = inflater.inflate(R.layout.activity_movie_card, parent, false);
        return new AdapterCinemas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.runtime.setText(runtimes.get(position));
        holder.description.setText(description.get(position));
        holder.name.setText(names.get(position));
        // falta a imagem
        Picasso.get().load(images.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, description, runtime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.movieImage);
            name = itemView.findViewById(R.id.movieTitle);
            description = itemView.findViewById(R.id.movieDesciption);
            runtime = itemView.findViewById(R.id.movieDuration);
        }
    }
}