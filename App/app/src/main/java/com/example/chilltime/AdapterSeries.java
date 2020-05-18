package com.example.chilltime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterSeries extends RecyclerView.Adapter<AdapterSeries.ViewHolder> {
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
    List<Integer> watchesUser;
    DocumentReference documentReference;

    public AdapterSeries(Context context, List<String> names, List<String> images, List<Integer> ids, List<Integer> idsFavorites, List<Integer> idsWatches){
        this.names = names;
        this.context = context;
        this.images = images;
        this.ids = ids;
        this.inflater = LayoutInflater.from(context);
        this.favoritesUser = idsFavorites;
        this.watchesUser = idsWatches;

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
        if(favoritesUser.contains(ids.get(position).longValue())){
            holder.addFavorite.setImageResource(R.drawable.removefavorite);
        }
        if(watchesUser.contains(ids.get(position).longValue())){
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
                    userFavorites.put("FavoritesSeries", favoritesUser);
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
                    favoritesUser.removeAll(Collections.singleton(ids.get(position).longValue()));
                    userFavorites.put("FavoritesSeries", favoritesUser);
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
                // adicionar aos vistos o filme selecionado
                Map<String, Object> userWatches = new HashMap<>();
                // Se não estiver já na bd, adiciona
                if(holder.addWatch.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.addwatch).getConstantState())) {
                    watchesUser.add(ids.get(position));
                    userWatches.put("WatchesSeries", watchesUser);
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
                    watchesUser.removeAll(Collections.singleton(ids.get(position).longValue()));
                    userWatches.put("WatchesSeries", watchesUser);
                    documentReference.set(userWatches,  SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("REMOVIDOOOO");
                        }
                    });
                    holder.addWatch.setImageResource(R.drawable.addwatch);

                }
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
