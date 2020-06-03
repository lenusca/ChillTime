package com.example.chilltime;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardUser extends Fragment {

    private TextView monthsTT, daysTT, hoursTT, monthsMovie, daysMovie, hoursMovie, monthsSeries, daysSeries, hoursSeries;

    private PieChart movieChart, seriesChart;
    View view;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    int mTime, sTime, tvTime;
    List<String> idGenreMovies;
    List<String> idsDivided;
    Map<String, Integer> countGenreMovies = new HashMap<>();
    List<String> idGenreSeries;
    List<String> idsDividedSeries;
    Map<String, Integer> countGenreSeries = new HashMap<>();
    private RequestQueue mQueue;


    public DashboardUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard_user, container, false);
        monthsTT = view.findViewById(R.id.monthsTT);
        daysTT = view.findViewById(R.id.daysTT);
        hoursTT = view.findViewById(R.id.hoursTT);
        monthsMovie = view.findViewById(R.id.monthsMovies);
        daysMovie = view.findViewById(R.id.daysMovies);
        hoursMovie = view.findViewById(R.id.hoursMovies);
        monthsSeries = view.findViewById(R.id.monthsSeries);
        daysSeries = view.findViewById(R.id.daysSeries);
        hoursSeries = view.findViewById(R.id.hoursSeries);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        // ir buscar o documento relacionado com o utilizador, usando o uid
        final DocumentReference documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener((Activity) getContext(), new EventListener<DocumentSnapshot>(){
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.get("IdGenderMoviesWatch") ==null ){
                    idGenreMovies=new ArrayList<>();
                }else{
                    idGenreMovies = (List<String>) documentSnapshot.get("IdGenderMoviesWatch");
                }
                if(documentSnapshot.get("IdGenderSeriesWatch")==null  ){
                    idGenreSeries=new ArrayList<>();
                }else {
                    idGenreSeries = (List<String>) documentSnapshot.get("IdGenderSeriesWatch");
                }

                idsDivided = getIDGenre(idGenreMovies);
                idsDividedSeries = getIDGenre(idGenreSeries);
                getGenre(); //get genres from volley
                getGenreSeries(); //get genres from volley
                mTime = (documentSnapshot.getLong("WatchesMoviesTime")).intValue();
                sTime = (documentSnapshot.getLong("WatchesSeriesTime")).intValue();
                tvTime = mTime + sTime;

                System.out.println(mTime);
                System.out.println(tvTime);
                // Tempo total
                String[] totalTime = transformTime(tvTime);
                if(totalTime[0].length() == 1){
                    monthsTT.setText("0"+totalTime[0]);
                }
                else{
                    monthsTT.setText(totalTime[0]);
                }

                if(totalTime[1].length() == 1){
                    daysTT.setText("0"+totalTime[1]);
                }
                else{
                    daysTT.setText(totalTime[1]);
                }

                if(totalTime[2].length() == 1){
                    hoursTT.setText("0"+totalTime[2]);
                }
                else{
                    hoursTT.setText(totalTime[2]);
                }

                // Tempo de filmes
                String[] moviesTime = transformTime(mTime);
                if(moviesTime[0].length() == 1){
                    monthsMovie.setText("0"+moviesTime[0]);
                }
                else{
                    monthsMovie.setText(moviesTime[0]);
                }

                if(moviesTime[1].length() == 1){
                    daysMovie.setText("0"+moviesTime[1]);
                }
                else{
                    daysMovie.setText(moviesTime[1]);
                }

                if(moviesTime[2].length() == 1){
                    hoursMovie.setText("0"+moviesTime[2]);
                }
                else{
                    hoursMovie.setText(moviesTime[2]);
                }

                // Tempo de séries
                String[] seriesTime = transformTime(sTime);
                if(seriesTime[0].length() == 1){
                    monthsSeries.setText("0"+seriesTime[0]);
                }
                else{
                    monthsSeries.setText(seriesTime[0]);
                }

                if(seriesTime[1].length() == 1){
                    daysSeries.setText("0"+seriesTime[1]);
                }
                else{
                    daysSeries.setText(seriesTime[1]);
                }

                if(seriesTime[2].length() == 1){
                    hoursSeries.setText("0"+seriesTime[2]);
                }
                else{
                    hoursSeries.setText(seriesTime[2]);
                }
            }
        });


        //MOVIES CHART
        movieChart = view.findViewById(R.id.moviesChart);
        movieChart.setUsePercentValues(true);
        movieChart.setHoleRadius(0f);
        movieChart.setTransparentCircleRadius(0f);

        //SERIES CHART
        seriesChart = view.findViewById(R.id.seriesChart);
        seriesChart.setUsePercentValues(true);
        seriesChart.setHoleRadius(0f);
        seriesChart.setTransparentCircleRadius(0f);

        return view;
    }

    private String[] transformTime(int minutes){
        int minutesInAMonth = 43829;
        int minutesInADay = 1440;
        int months = minutes / minutesInAMonth;
        int days = (minutes - months * minutesInAMonth) / minutesInADay;
        int hours = (minutes - days * minutesInADay - months * minutesInAMonth) / 60;
        String[] res = {String.valueOf(months), String.valueOf(days), String.valueOf(hours)};
        return res;
    }

    private List<String> getIDGenre(List<String> idGenreMovies){
        List<String> tmp = new LinkedList<>();
        String ids;
        String[] id;
        for(int i=0; i<idGenreMovies.size(); i++) {
            ids = idGenreMovies.get(i);
            String replace = ids.replace("[", "");
            String replace1 = replace.replace("]", "");
            id = replace1.split(",");
            for (int j = 0; j < id.length; j++) {
                tmp.add(id[j]);
            }
        }
        return tmp;
    }

    public void getGenre(){
        final HashMap<String, String> tmp = new HashMap<>();
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("genres");
                            //System.out.println("AQUIIIIIIIII "+jsonArray);
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject genre = jsonArray.getJSONObject(i);
                                tmp.put(genre.getString("id"), genre.getString("name"));
                            }
                            tabela(tmp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue = Volley.newRequestQueue(this.getContext());
        mQueue.add(request);
    }

    public void tabela(HashMap<String, String> listGenre){
        //associar nome com o numero de vezes que ve filme do mesmo genero
        //hashMap countGenreMovies: key-nome do genero; value-count de casa genero
        //idsDivided lista de string com idGenre
        //hashMap listGenre da api: key-id genero; value-nome associado ao genero
        int count=0;
        int getCount=0;
        if(listGenre!=null && idsDivided!=null){
            //count diferent genres
            for(String idGenre: idsDivided){
                if(!countGenreMovies.containsKey(idGenre)){
                    count=count+1;
                    countGenreMovies.put(listGenre.get(idGenre), count);
                    count=0;
                }else{
                    getCount = countGenreMovies.get(listGenre.get(idGenre))+1;
                    countGenreMovies.remove(listGenre.get(idGenre));
                    countGenreMovies.put(listGenre.get(idGenre), getCount);
                }
            }
        }
        List<PieEntry> value = new ArrayList<>();
        PieDataSet pieDataSet;
        PieData pieData;
        //System.out.println("COUNT GENRE MOVIES "+countGenreMovies);
        if(countGenreMovies!=null){
            //get all keys
            Set<String> keys = countGenreMovies.keySet();
            //Example with fake numbers for MOVIE CHART
            //IMPORTANT : 40f means 40%//
            for(String key: keys) {
                float percentagem = ((float) countGenreMovies.get(key) / (float) countGenreMovies.size()) * 100;
                value.add(new PieEntry(percentagem, key));
            }
            pieDataSet = new PieDataSet(value, "");
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieData = new PieData(pieDataSet);
            movieChart.setData(pieData);
            Description descMovies = new Description();
            //System.out.println("ACtion movies "+descMovies);
            descMovies.setText("Movie Genres");
            movieChart.setDescription(descMovies);
            movieChart.invalidate(); // refreshes chart

        }
    }

    public void getGenreSeries(){
        final HashMap<String, String> tmp = new HashMap<>();
        String url = "https://api.themoviedb.org/3/genre/tv/list?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("genres");
                            //System.out.println("AQUIIIIIIIII "+jsonArray);
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject genre = jsonArray.getJSONObject(i);
                                tmp.put(genre.getString("id"), genre.getString("name"));
                            }
                            tabelaSeries(tmp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue = Volley.newRequestQueue(this.getContext());
        mQueue.add(request);
    }

    public void tabelaSeries(HashMap<String, String> listGenre){
        //associar nome com o numero de vezes que ve filme do mesmo genero
        //hashMap countGenreMovies: key-nome do genero; value-count de casa genero
        //idsDivided lista de string com idGenre
        //hashMap listGenre da api: key-id genero; value-nome associado ao genero
        int count=0;
        int getCount=0;
        if(listGenre!=null && idsDividedSeries!=null){
            //count diferent genres
            for(String idGenre: idsDividedSeries){
                if(!countGenreSeries.containsKey(idGenre)){
                    count=count+1;
                    countGenreSeries.put(listGenre.get(idGenre), count);
                    count=0;
                }else{
                    getCount = countGenreSeries.get(listGenre.get(idGenre))+1;
                    countGenreSeries.remove(listGenre.get(idGenre));
                    countGenreSeries.put(listGenre.get(idGenre), getCount);
                }
            }
        }
        List<PieEntry> valueSeries = new ArrayList<>();
        //System.out.println("COUNT GENRE MOVIES "+countGenreMovies);
        if(countGenreSeries!=null){
            //get all keys
            Set<String> keys = countGenreSeries.keySet();
            //Example with fake numbers for MOVIE CHART
            //IMPORTANT : 40f means 40%//
            for(String key: keys){
                float percentagem =((float)countGenreSeries.get(key)/(float) countGenreSeries.size())*100;
                valueSeries.add(new PieEntry(percentagem, key));
            }

            //System.out.println("GRAFICOOO DE SERIES "+valueSeries);

            PieDataSet pieDataSetSeries = new PieDataSet(valueSeries, "");
            pieDataSetSeries.setColors(ColorTemplate.JOYFUL_COLORS);
            PieData pieDataSeries = new PieData(pieDataSetSeries);
            seriesChart.setData(pieDataSeries);
            Description descSeries = new Description();
            descSeries.setText("Series Genres");
            seriesChart.setDescription(descSeries);
            seriesChart.invalidate(); // refreshes chart
        }
    }

}
