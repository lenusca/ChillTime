package com.example.chilltime.UserActivity.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chilltime.UserActivity.DashboardUser;
import com.example.chilltime.UserActivity.MoviesUser;
import com.example.chilltime.UserActivity.SeriesUser;

public class TabBarAdapter extends FragmentPagerAdapter {
    int numTabs;

    public TabBarAdapter(@NonNull FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // ir para cada fragment, consoante o que seleciona em cima
        switch (position){
            case 0:
                return new DashboardUser();
            case 1:
                return new MoviesUser();
            case 2:
                return new SeriesUser();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // número de items
        return numTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
