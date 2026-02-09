package com.berger.bergerXpressVisualiserPk.adapters;

import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.berger.bergerXpressVisualiserPk.fragments.ColorShadesDisplayFragment;
import com.berger.bergerXpressVisualiserPk.models.Colors;

import java.util.ArrayList;

public class ColorShadesDisplayPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Colors> colors;
    private Boolean performSelect;

    public ColorShadesDisplayPagerAdapter(FragmentManager fm, ArrayList<Colors> colors, Boolean performSelect) {
        super(fm);
        this.colors = colors;
        this.performSelect = performSelect;
    }

    @Override
    public Fragment getItem(int position) {

        return ColorShadesDisplayFragment.newInstance(String.valueOf(position + 1), colors.get(position), performSelect);
    }

    @Override
    public int getCount() {
        if (colors != null) {
            return colors.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return String.valueOf(position + 1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
