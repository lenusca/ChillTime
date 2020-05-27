package com.example.chilltime;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.polyak.iconswitch.IconSwitch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesUser extends Fragment implements IconSwitch.CheckedChangeListener{
    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;

    // Recycler view
    AdapterMovies adapter;
    RecyclerView dataList;
    List<Long> idsFavorites;
    List<String> imageFavorites;
    List<Long> idsWatches;
    List<String> imageWatches;
    long timeWatches;

    // Switch
    IconSwitch iconSwitch;
    String choice = "LEFT";

    //
    Context context;
    View view;

    public MoviesUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view = inflater.inflate(R.layout.fragment_movies_user, container, false);
        iconSwitch = view.findViewById(R.id.icon_switch);
        iconSwitch.setCheckedChangeListener(this);
        switchChange();


        return view;
    }

    @Override
    public void onCheckChanged(IconSwitch.Checked current) {
        this.choice = current.toString();
        switchChange();
    }

    public void switchChange(){
        timeWatches = 0;
        dataList = view.findViewById(R.id.dataList);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();
        if(choice.equals("LEFT")){
            System.out.println("AQ()");
            // ir buscar o documento relacionado com o utilizador, usando o uid
            final DocumentReference documentReference = mStore.collection("Users").document(userID);
            documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>(){

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.get("FavoritesMovie") == null || documentSnapshot.get("FavoritesImagesMovie") == null ){
                        idsFavorites = new ArrayList<>();
                        imageFavorites = new ArrayList<>();
                    }
                    else{
                        idsFavorites = (List<Long>) documentSnapshot.get("FavoritesMovie");
                        imageFavorites = (List<String>) documentSnapshot.get("FavoritesImagesMovie");
                    }
                    if(documentSnapshot.get("WatchesMovies") == null || documentSnapshot.get("WatchesImagesMovies") == null || documentSnapshot.get("WatchesMoviesTime") == null){
                        idsWatches = new ArrayList<>();
                        imageWatches = new ArrayList<>();
                        timeWatches = 0;
                    }
                    else {
                        idsWatches = (List<Long>) documentSnapshot.get("WatchesMovies");
                        imageWatches = (List<String>) documentSnapshot.get("WatchesImagesMovies");
                        timeWatches = (long) documentSnapshot.get("WatchesMoviesTime");
                    }
                    adapter = new AdapterMovies(getContext(), imageWatches, imageWatches, idsWatches, idsFavorites, imageFavorites, idsWatches, imageWatches, timeWatches);

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
                    dataList.setLayoutManager(gridLayoutManager);
                    // colocar os dados no view
                    dataList.setAdapter(adapter);

                }
            });
        }
        else{

            // ir buscar o documento relacionado com o utilizador, usando o uid
            final DocumentReference documentReference = mStore.collection("Users").document(userID);
            documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>(){

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.get("FavoritesMovie") == null || documentSnapshot.get("FavoritesImagesMovie") == null ){
                        idsFavorites = new ArrayList<>();
                        imageFavorites = new ArrayList<>();
                    }
                    else{
                        idsFavorites = (List<Long>) documentSnapshot.get("FavoritesMovie");
                        imageFavorites = (List<String>) documentSnapshot.get("FavoritesImagesMovie");
                    }
                    if(documentSnapshot.get("WatchesMovies") == null || documentSnapshot.get("WatchesImagesMovies") == null || documentSnapshot.get("WatchesMoviesTime") == null){
                        idsWatches = new ArrayList<>();
                        imageWatches = new ArrayList<>();
                        timeWatches = 0;
                    }
                    else {
                        idsWatches = (List<Long>) documentSnapshot.get("WatchesMovies");
                        imageWatches = (List<String>) documentSnapshot.get("WatchesImagesMovies");
                        timeWatches = (long) documentSnapshot.get("WatchesMoviesTime");
                    }
                    adapter = new AdapterMovies(getContext(), imageFavorites, imageFavorites, idsFavorites, idsFavorites, imageFavorites, idsWatches, imageWatches, timeWatches);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
                    dataList.setLayoutManager(gridLayoutManager);
                    // colocar os dados no view
                    dataList.setAdapter(adapter);

                }
            });
        }
    }
}
