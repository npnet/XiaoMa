package com.xiaoma.music.manager;

import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.utils.ListUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZYao.
 * Date ：2018/12/21 0021
 */
public class SortUsbMusicManager {

    private List<UsbMusic> mGenreMusicList = new ArrayList<>();
    private List<UsbMusic> mArtistMusicList = new ArrayList<>();
    private List<UsbMusic> mDefaultMusicList = new ArrayList<>();

    public static SortUsbMusicManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final SortUsbMusicManager instance = new SortUsbMusicManager();
    }

    public void setDefaultMusicList(List<UsbMusic> mDefaultMusicList) {
        this.mDefaultMusicList = mDefaultMusicList;
    }

    public List<UsbMusic> getSortByGenre() {
        if (ListUtils.isEmpty(mDefaultMusicList)) {
            return new ArrayList<>();
        }
        if (!ListUtils.isEmpty(mGenreMusicList)) {
            return mGenreMusicList;
        }

        mGenreMusicList = new ArrayList<>(mDefaultMusicList);
        try {
            Collections.sort(mGenreMusicList, new Comparator<UsbMusic>() {
                @Override
                public int compare(UsbMusic o1, UsbMusic o2) {
                    if (o1 == null || o1.getGenre() == null) {
                        return -1;
                    }
                    if (o2 == null || o2.getGenre() == null) {
                        return 1;
                    }
                    return Collator.getInstance(Locale.CHINA).compare(o1.getGenre(), o2.getGenre());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mGenreMusicList;
    }

    public List<UsbMusic> getSortByArtist() {
        if (ListUtils.isEmpty(mDefaultMusicList)) {
            return new ArrayList<>();
        }
        if (!ListUtils.isEmpty(mArtistMusicList)) {
            return mArtistMusicList;
        }
        mArtistMusicList = new ArrayList<>(mDefaultMusicList);
        try {
            Collections.sort(mArtistMusicList, new Comparator<UsbMusic>() {
                @Override
                public int compare(UsbMusic o1, UsbMusic o2) {
                    if (o1 == null || o1.getArtist() == null) {
                        return -1;
                    }
                    if (o2 == null || o2.getArtist() == null) {
                        return 1;
                    }
                    return Collator.getInstance(Locale.CHINA).compare(o1.getArtist(), o2.getArtist());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArtistMusicList;
    }

    public void clear() {
        mDefaultMusicList = null;
    }

    public void clearAll() {
        mArtistMusicList = null;
        mDefaultMusicList = null;
        mGenreMusicList = null;
    }
}
