package com.example.chilltime.gps.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.chilltime.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.*;

public class AdapterLocation extends RecyclerView.Adapter<AdapterLocation.ViewHolder> {
    Context context;
    List<String> names;
    List<String> description;
    List<String> runtimes;
    List<String> images;
    LayoutInflater inflater;
    RecyclerViewClickListener mListener;
    // Ir buscar as imagens
    RequestQueue mQueue;

    public AdapterLocation(Context context, List<String> names, List<String> description, List<String> runtimes, List<String> images, RecyclerViewClickListener mListener){
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
        this.mListener = mListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ligação com o gridlayou
        View view = inflater.inflate(R.layout.activity_movie_card, parent, false);
        return new AdapterLocation.ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.runtime.setText(runtimes.get(position));
        holder.description.setText(description.get(position));
        holder.name.setText(names.get(position));
        // Verificar se é icon ou reference para ir buscar a imagem a api
        if(images.get(position).contains("https://")){
            Picasso.get().load(images.get(position)).into(holder.image);
        }

        else {

            mQueue = Volley.newRequestQueue(context);
            ImageRequest request = new ImageRequest( "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + images.get(position) + "&key=AIzaSyBnmYS6fjLrp7mtdh-L79054GnpUIml2q4",
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            holder.image.setImageBitmap(Bitmap.createScaledBitmap(response, 140, 150, true));
                        }
                    }, 100,150,null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    out.println("ERROR");
                    error.printStackTrace();
                }
            });
            mQueue.add(request);
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView name, description, runtime;
        RecyclerViewClickListener mListener;
        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.movieImage);
            name = itemView.findViewById(R.id.movieTitle);
            description = itemView.findViewById(R.id.movieDesciption);
            runtime = itemView.findViewById(R.id.movieDuration);
            mListener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}




