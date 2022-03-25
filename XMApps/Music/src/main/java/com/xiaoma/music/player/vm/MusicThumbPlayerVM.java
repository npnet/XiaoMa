package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import cn.kuwo.base.bean.Music;

/**
 * Created by LKF on 2018-12-21 0021.
 */
public class MusicThumbPlayerVM extends BaseThumbPlayerVM {
    private MutableLiveData<Music> mMusic = new MutableLiveData<>();

    public MusicThumbPlayerVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Music> getMusic() {
        return mMusic;
    }
}