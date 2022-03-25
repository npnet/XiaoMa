package com.xiaoma.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2018/10/19
 */
public class AppDetailPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public AppDetailPageAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void clear() {
        fragments.clear();
    }

    public void add(Fragment f) {
        fragments.add(f);
    }
}
