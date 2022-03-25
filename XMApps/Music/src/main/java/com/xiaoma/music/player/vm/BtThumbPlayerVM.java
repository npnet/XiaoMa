package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.music.model.BTMusic;

/**
 * Created by LKF on 2018-12-21 0021.
 */
public class BtThumbPlayerVM extends BaseThumbPlayerVM {
    private MutableLiveData<BTMusic> mBtMusic = new MutableLiveData<>();

    public BtThumbPlayerVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BTMusic> getBtMusic() {
        return mBtMusic;
    }
}
