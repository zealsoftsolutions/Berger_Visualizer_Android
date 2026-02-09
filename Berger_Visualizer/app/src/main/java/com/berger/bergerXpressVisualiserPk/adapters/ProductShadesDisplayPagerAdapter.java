package com.berger.bergerXpressVisualiserPk.adapters;

import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.berger.bergerXpressVisualiserPk.fragments.ColorShadesDisplayFragment;
import com.berger.bergerXpressVisualiserPk.fragments.ProductShadesDisplayFragment;
import com.berger.bergerXpressVisualiserPk.models.Colors;
import com.berger.bergerXpressVisualiserPk.models.Product;

import java.util.ArrayList;

public class ProductShadesDisplayPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Product> products;
    private ArrayList<Colors> colors;
    private Boolean performSelect;

    public ProductShadesDisplayPagerAdapter(FragmentManager fm, ArrayList<Product> products, ArrayList<Colors> colors, Boolean performSelect) {
        super(fm);
        this.products = products;
        this.colors = colors;
        this.performSelect = performSelect;
    }

    @Override
    public Fragment getItem(int position) {

        if(products != null && colors != null) {
            if (position < products.size()) {
                return ProductShadesDisplayFragment.newInstance(String.valueOf(position + 1), products.get(position), performSelect);
            } else {
                return ColorShadesDisplayFragment.newInstance(String.valueOf(position - (products.size()) + 1), colors.get(position - products.size()), performSelect);
            }
        } else {
            if(products != null && products.size() > 0){
                return ProductShadesDisplayFragment.newInstance(String.valueOf(position + 1), products.get(position), performSelect);
            } else if(colors != null && colors.size() > 0){
                return ColorShadesDisplayFragment.newInstance(String.valueOf(position + 1), colors.get(position), performSelect);
            }  else {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        if (products != null) {
             count = products.size();
        }
        if (colors != null){
            count += colors.size();
        }

        return count;
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
