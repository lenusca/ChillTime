package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

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
