package com.example.chilltime;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieList extends AppCompatActivity {
    // Adapter
    RecyclerView recyclerView;
    AdapterCinemas adapter;
    List<String> names;
    List<String> images;
    List<String> runtime;
    List<String> descripton;
    // Firebase
    FirebaseFirestore mStore;

    // Key
    String message = "1"; //trocar para a enviada da outra pagina


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        recyclerView = findViewById(R.id.moviesRecyclerView);
        names = new ArrayList<>();
        images = new ArrayList<>();
        runtime = new ArrayList<>();
        descripton = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        // receber a mensagem da outra atividade
        Intent intent = getIntent();
        message = intent.getStringExtra(GPS.EXTRA_MESSAGE);
        // ir buscar o documento relacionado com o utilizador, usando o uid
        final DocumentReference documentReference = mStore.collection("Cinema").document(message);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // dizer o que quer ir buscar
                names = (List<String>) documentSnapshot.get("filmName");
                images = (List<String>) documentSnapshot.get("filmImage");
                List<Long> runtimes = (List<Long>)documentSnapshot.get("filmRuntime");
                for(Long value : runtimes){
                    runtime.add(String.valueOf(value));
                }
                descripton = (List<String>) documentSnapshot.get("filmDescription");

                adapter = new AdapterCinemas(MovieList.this, names, descripton, runtime, images);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieList.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);

            }
        });

    }
}
