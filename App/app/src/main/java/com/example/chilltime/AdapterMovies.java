package com.example.chilltime;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Tentar colocar geral, dar para movie e serie
public class AdapterMovies extends RecyclerView.Adapter<AdapterMovies.ViewHolder> {
    List<String> names;
    List<String> images;
    List<Integer> ids;
    Context context;
    LayoutInflater inflater;
    // User
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    List<Integer> favoritesUser;
    List<String> imagesFavoritesUser;
    List<Integer> watchesUser;
    List<String> imagesWatchesUser;
    DocumentReference documentReference;
    long timeWatches;
    int time = 0;
    //api
    RequestQueue mQueue;
    // Para mandar o id para outra activity(details)
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";

    public AdapterMovies(Context context, List<String> names, List<String> images, List<Integer> ids, List<Integer> idsFavorites, List<String> imagesFavoritesUser, List<Integer> idsWatches, List<String> imagesWatchesUser, long timeWatches){
        this.names = names;
        this.context = context;
        this.images = images;
        this.ids = ids;
        this.inflater = LayoutInflater.from(context);
        this.favoritesUser = idsFavorites;
        this.imagesFavoritesUser = imagesFavoritesUser;
        this.watchesUser = idsWatches;
        this.imagesWatchesUser = imagesWatchesUser;
        this.timeWatches = timeWatches;

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();
        documentReference = mStore.collection("Users").document(userID);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ligação com o gridlayou
        View view = inflater.inflate(R.layout.gridlayout, parent, false);

        return new ViewHolder(view);
    }

    // Colocar a info na view
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // verifica se o filme ja esta na bd do user e muda a imagem por ja adicionado e visto
        if(favoritesUser.contains(ids.get(position).longValue())){
            holder.addFavorite.setImageResource(R.drawable.removefavorite);
        }
        if(watchesUser.contains(ids.get(position).longValue())){
            holder.addWatch.setImageResource(R.drawable.removewatch);
        }

        // para ir buscar a imagem a partir do url
        Picasso.get().load("https://image.tmdb.org/t/p/original/"+images.get(position)).into(holder.movieImage);

        // guardar no firebase quando carrega na imagem e mudar o icon
        // Favoritos
        holder.addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> userFavorites = new HashMap<>();
                // Se não estiver já na bd, adiciona
                if(holder.addFavorite.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.addfavorite).getConstantState())){
                    favoritesUser.add(ids.get(position));
                    imagesFavoritesUser.add(images.get(position));
                    userFavorites.put("FavoritesMovie", favoritesUser);
                    userFavorites.put("FavoritesImagesMovie", imagesFavoritesUser);
                    documentReference.set(userFavorites,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("ADICIONADOOOO");
                        }
                    });
                    holder.addFavorite.setImageResource(R.drawable.removefavorite);
                }
                // Verificar se está adicionado, se estiver vai remover a bd se carregar de novo
                else if(holder.addFavorite.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.removefavorite).getConstantState())){
                    favoritesUser.removeAll(Collections.singleton(ids.get(position)));
                    imagesFavoritesUser.removeAll(Collections.singleton(images.get(position)));
                    userFavorites.put("FavoritesMovie", favoritesUser);
                    userFavorites.put("FavoritesImagesMovie", imagesFavoritesUser);
                    documentReference.set(userFavorites,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("REMOVIDOOOO");
                        }
                    });
                    holder.addFavorite.setImageResource(R.drawable.addfavorite);

                }
            }
        });

        // Vistos
        holder.addWatch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // para saber quanto tempo dura o filme, tenho de aceder a api novamente
                mQueue = Volley.newRequestQueue(context);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+ids.get(position)+"?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    time = response.getInt("runtime");

                                    // adicionar aos vistos o filme selecionado
                                    Map<String, Object> userWatches = new HashMap<>();
                                    // Se não estiver já na bd, adiciona
                                    if(holder.addWatch.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.addwatch).getConstantState())) {
                                        watchesUser.add(ids.get(position));
                                        imagesWatchesUser.add(images.get(position));
                                        userWatches.put("WatchesMovies", watchesUser);
                                        userWatches.put("WatchesImagesMovies", imagesWatchesUser);
                                        timeWatches = timeWatches + time;
                                        userWatches.put("WatchesMoviesTime", timeWatches);
                                        documentReference.set(userWatches, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("ADICIONADOOOO");
                                            }
                                        });
                                        holder.addWatch.setImageResource(R.drawable.removewatch);
                                    }
                                    // Verificar se está adicionado, se estiver vai remover a bd se carregar de novo
                                    else if(holder.addWatch.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.removewatch).getConstantState())){
                                        watchesUser.removeAll(Collections.singleton(ids.get(position)));
                                        imagesWatchesUser.removeAll(Collections.singleton(images.get(position)));
                                        userWatches.put("WatchesMovies", watchesUser);
                                        userWatches.put("WatchesImagesMovies", imagesWatchesUser);
                                        timeWatches = timeWatches - time;
                                        userWatches.put("WatchesMoviesTime", timeWatches);
                                        documentReference.set(userWatches,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("REMOVIDOOOO");
                                            }
                                        });
                                        holder.addWatch.setImageResource(R.drawable.addwatch);
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
        });
        // Ver detalhes do filme
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // enviar para a actividade dos detalhes o id do movie
                Intent intent = new Intent(context, Details.class);
                intent.putExtra(EXTRA_MESSAGE, ids.get(position).toString());
                context.startActivity(intent);
            }
        });
    }

    // nº de elementos que vai haver
    @Override
    public int getItemCount() {
        return ids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton addFavorite;
        ImageButton addWatch;
        ImageView movieImage;

        public ViewHolder(View view) {
            super(view);
            movieImage = view.findViewById(R.id.image);
            addFavorite = view.findViewById(R.id.addFavorite);
            addWatch = view.findViewById(R.id.addWatch);

        }
    }
}
