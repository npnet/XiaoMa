package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.music.model.UsbMusic;

/**
 * Created by LKF on 2018-12-21 0021.
 */
public class UsbThumbPlayerVM extends BaseThumbPlayerVM {
    private MutableLiveData<UsbMusic> mUsbMusic = new MutableLiveData<>();

    public UsbThumbPlayerVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<UsbMusic> getUsbMusic() {
        return mUsbMusic;
    }
}