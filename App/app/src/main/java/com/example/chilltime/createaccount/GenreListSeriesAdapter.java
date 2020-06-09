package com.example.chilltime.createaccount;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chilltime.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GenreListSeriesAdapter extends RecyclerView.Adapter<GenreListSeriesAdapter.GenreSeriesViewHold> {
    private LinkedList<Genre> listGenre;
    private List<String> userGenre = new LinkedList<>();
    private int flagCheck = 0;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    DocumentReference documentReference;

    public GenreListSeriesAdapter(LinkedList<Genre> list){
        this.listGenre = list;
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        documentReference = mStore.collection("Users").document(userID);
    }

    @NonNull
    @Override
    public GenreSeriesViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //System.out.println("LISTAAAAAAAAAAa "+listGenre);
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_button_radio, parent, false);
        return new GenreSeriesViewHold(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final GenreSeriesViewHold holder, final int position) {
        holder.genre.setText(listGenre.get(position).name);
        holder.genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userGenreSeries = new HashMap<>();
                if(holder.genre.isChecked()){
                    if(userGenre.contains(listGenre.get(position).id)){
                        holder.genre.setChecked(false);
                        userGenre.remove(listGenre.get(position).id);
                        //System.out.println("AQUIIIIIIII Removido"+userGenre);
                        userGenreSeries.put("GenreSeries", userGenre);
                        documentReference.set(userGenreSeries, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("REMOVIDOOOO");
                            }
                        });
                    }else {
                        userGenre.add(listGenre.get(position).id);
                        //System.out.println("AQUIIIIIIII adicionado" + userGenre);
                        userGenreSeries.put("GenreSeries", userGenre);
                        documentReference.update(userGenreSeries).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("ADICIONADOOOO");
                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listGenre.size();
    }

    //########## CLASS INTERNA
    public class GenreSeriesViewHold extends RecyclerView.ViewHolder {
        public RadioButton genre;
        GenreListSeriesAdapter mAdapter;

        public GenreSeriesViewHold(@NonNull View itemView, GenreListSeriesAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            genre = itemView.findViewById(R.id.genre);
        }

    }
}


