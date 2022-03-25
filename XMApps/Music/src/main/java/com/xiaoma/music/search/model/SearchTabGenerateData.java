package com.xiaoma.music.search.model;

import android.support.v4.app.Fragment;

import com.xiaoma.music.search.ui.SearchMusicFragment;
import com.xiaoma.music.search.ui.SearchSingerFragment;
import com.xiaoma.music.search.ui.SearchSongListFragment;

import java.util.ArrayList;
import java.util.List;


public class SearchTabGenerateData {


    public static List<Fragment> getFragments(String from) {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SearchMusicFragment.newInstance(from));
        fragments.add(SearchSingerFragment.newInstance(from));
        fragments.add(SearchSongListFragment.newInstance(from));
        return fragments;
    }

    public static List<Fragment> getSingerFragments(String from) {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SearchMusicFragment.newInstance(from));
        fragments.add(SearchSongListFragment.newInstance(from));
        return fragments;
    }
}
