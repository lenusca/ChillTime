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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
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

import java.util.ArrayList;
import java.util.List;


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
        Description desc = new Description();
        desc.setText("");






        //Example with fake numbers for MOVIE CHART
        //IMPORTANT : 40f means 40%//
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(40f, "Horror"));
        value.add(new PieEntry(60f, "Drama"));


        PieDataSet pieDataSet = new PieDataSet(value, "");
        PieData pieData = new PieData(pieDataSet);
        movieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        //movieChart.setDescription(desc);



        //Example with fake numbers for SERIES CHART
        List<PieEntry> valueSeries = new ArrayList<>();
        valueSeries.add(new PieEntry(40f, "Thriller"));
        valueSeries.add(new PieEntry(60f, "Drama"));


        PieDataSet pieDataSetSeries = new PieDataSet(valueSeries, "");
        PieData pieDataSeries = new PieData(pieDataSetSeries);
        seriesChart.setData(pieDataSeries);
        pieDataSetSeries.setColors(ColorTemplate.JOYFUL_COLORS);
        //seriesChart.setDescription(desc);

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
}
