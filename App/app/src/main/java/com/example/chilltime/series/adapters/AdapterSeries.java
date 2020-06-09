package com.example.chilltime.series.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chilltime.R;
import com.example.chilltime.series.DetailsSerie;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterSeries extends RecyclerView.Adapter<AdapterSeries.ViewHolder> {
    //
    List<String> names;
    List<String> images;
    List<Long> ids;
    Context context;
    LayoutInflater inflater;
    // User
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    List<Long> favoritesUser;
    List<String> imagesFavoritesUser;
    List<Long> watchesUser;
    List<String> imagesWatchesUser;
    List<String> dateWatches;
    List<String> idGenderSeriesWatch;
    List<String> idGenresSeries;
    DocumentReference documentReference;
    long timeWatches;
    int time = 0;
    String date = "";
    //api
    RequestQueue mQueue;
    // Para mandar o id para outra activity(details)
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";


    public AdapterSeries(Context context, List<String> names, List<String> images, List<Long> ids, List<Long> idsFavorites, List<String> imagesFavoritesUser, List<Long> idsWatches, List<String> imagesWatchesUser, long timeWatches, List<String> dateWatches,
                            List<String> idGenderSeriesWatch, List<String> idGenresSeries){
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
        this.dateWatches = dateWatches;
        this.idGenderSeriesWatch = idGenderSeriesWatch;
        this.idGenresSeries = idGenresSeries;

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

        // verificar se a serie já se encontra na bd do user e se sim mudar o imagebutton
        if(favoritesUser.contains(ids.get(position))){
            holder.addFavorite.setImageResource(R.drawable.removefavorite);
        }
        if(watchesUser.contains(ids.get(position))){
            holder.addWatch.setImageResource(R.drawable.removewatch);
        }

        // para ir buscar a imagem a partir do url
        Picasso.get().load("https://image.tmdb.org/t/p/original/"+images.get(position)).into(holder.serieImage);
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
                    userFavorites.put("FavoritesSeries", favoritesUser);
                    userFavorites.put("FavoritesImagesSeries", imagesFavoritesUser);
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
                    userFavorites.put("FavoritesSeries", favoritesUser);
                    userFavorites.put("FavoritesImagesSeries", imagesFavoritesUser);
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
                // para saber quanto tempo dura a série, tenho de aceder a api novamente
                mQueue = Volley.newRequestQueue(context);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/tv/"+ids.get(position)+"?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // notificações
                                    Calendar myAlarmDate = Calendar.getInstance();
                                    myAlarmDate.setTimeInMillis(System.currentTimeMillis());

                                    if(response.getString("last_air_date") != null){
                                        date = response.getString("last_air_date");
                                        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                    }

                                    time = response.getInt("number_of_episodes")*response.getJSONArray("episode_run_time").getInt(0);
                                    // adicionar aos vistos series selecionado
                                    Map<String, Object> userWatches = new HashMap<>();
                                    // Se não estiver já na bd, adiciona
                                    if(holder.addWatch.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.addwatch).getConstantState())) {
                                        watchesUser.add(ids.get(position));
                                        imagesWatchesUser.add(images.get(position));
                                        idGenderSeriesWatch.add(idGenresSeries.get(position));
                                        dateWatches.add(date);
                                        userWatches.put("WatchesSeries", watchesUser);
                                        userWatches.put("WatchesImagesSeries", imagesWatchesUser);
                                        userWatches.put("IdGenderSeriesWatch", idGenderSeriesWatch);
                                        userWatches.put("WatchesSeriesDate", dateWatches);
                                        timeWatches = timeWatches + time;
                                        userWatches.put("WatchesSeriesTime", timeWatches);
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
                                        idGenderSeriesWatch.removeAll(Collections.singleton(idGenresSeries.get(position)));
                                        dateWatches.remove(date);
                                        userWatches.put("WatchesSeries", watchesUser);
                                        userWatches.put("WatchesImagesSeries", imagesWatchesUser);
                                        userWatches.put("WatchesSeriesDate", dateWatches);
                                        userWatches.put("IdGenderSeriesWatch", idGenderSeriesWatch);
                                        timeWatches = timeWatches - time;
                                        userWatches.put("WatchesSeriesTime", timeWatches);
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

        // ver detalhes da séries
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // enviar para a atividade dos detalhes o id da série
                Intent intent = new Intent(context, DetailsSerie.class);
                intent.putExtra(EXTRA_MESSAGE, ids.get(position).toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton addFavorite;
        ImageButton addWatch;
        ImageView serieImage;
        public ViewHolder(View view) {
            super(view);
            addFavorite = view.findViewById(R.id.addFavorite);
            serieImage = view.findViewById(R.id.image);
            addWatch = view.findViewById(R.id.addWatch);
        }
    }
}
