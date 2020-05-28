package com.example.chilltime;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardUser extends Fragment {

    private TextView name, monthsTT, daysTT, hoursTT, monthsMovie, daysMovie, hoursMovie, monthsSeries, daysSeries, hoursSeries;
    private ImageView photo;
    private PieChart movieChart, seriesChart;
    View view;


    public DashboardUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard_user, container, false);
        name = view.findViewById(R.id.name);
        photo = view.findViewById(R.id.photo);
        monthsTT = view.findViewById(R.id.monthsTT);
        daysTT = view.findViewById(R.id.daysTT);
        hoursTT = view.findViewById(R.id.hoursTT);
        monthsMovie = view.findViewById(R.id.monthsMovies);
        daysMovie = view.findViewById(R.id.daysMovies);
        hoursMovie = view.findViewById(R.id.hoursMovies);
        monthsSeries = view.findViewById(R.id.monthsSeries);
        daysSeries = view.findViewById(R.id.daysSeries);
        hoursSeries = view.findViewById(R.id.hoursSeries);



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



        //Example with fake numbers
        name.setText("Joao Pedro");
        String[] totalTime = transformTime(100000);
        monthsTT.setText(totalTime[0]);
        daysTT.setText(totalTime[1]);
        hoursTT.setText(totalTime[2]);

        String[] moviesTime = transformTime(100000);
        monthsMovie.setText(moviesTime[0]);
        daysMovie.setText(moviesTime[1]);
        hoursMovie.setText(moviesTime[2]);

        String[] seriesTime = transformTime(1000);
        monthsSeries.setText(seriesTime[0]);
        daysSeries.setText(seriesTime[1]);
        hoursSeries.setText(seriesTime[2]);


        //Example with fake numbers for MOVIE CHART
        //IMPORTANT : 40f means 40%//
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(40f, "Horror"));
        value.add(new PieEntry(60f, "Drama"));


        PieDataSet pieDataSet = new PieDataSet(value, "");
        PieData pieData = new PieData(pieDataSet);
        movieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        movieChart.setDescription(desc);



        //Example with fake numbers for SERIES CHART
        List<PieEntry> valueSeries = new ArrayList<>();
        valueSeries.add(new PieEntry(40f, "Thriller"));
        valueSeries.add(new PieEntry(60f, "Drama"));


        PieDataSet pieDataSetSeries = new PieDataSet(valueSeries, "");
        PieData pieDataSeries = new PieData(pieDataSetSeries);
        seriesChart.setData(pieDataSeries);
        pieDataSetSeries.setColors(ColorTemplate.JOYFUL_COLORS);
        seriesChart.setDescription(desc);


        return view;
    }

    private String[] transformTime(int minutes){
        int minutesInAMonth = 43829;
        int minutesInADay = 1440;
        int months = minutes / minutesInAMonth;
        int days = (minutes - months * minutesInAMonth) / minutesInADay;
        int hours = (minutes - days * minutesInADay - months * minutesInAMonth) / 60;
        String[] res = {String.valueOf(months), String.valueOf(days), String.valueOf(hours)};
        System.out.println(res);
        return res;
    }
}
