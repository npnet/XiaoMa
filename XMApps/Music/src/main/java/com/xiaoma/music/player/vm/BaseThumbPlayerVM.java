package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;

/**
 * Created by LKF on 2018-12-21 0021.
 */
public class BaseThumbPlayerVM extends BaseViewModel {
    private final MutableLiveData<Integer> mPlayingStatus = new MutableLiveData<>();
    private final MutableLiveData<String> mPlayingTitle = new MutableLiveData<>();
    private final MutableLiveData<Object> mPlayingPicModel = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mPlayingPicBitmap = new MutableLiveData<>();

    public BaseThumbPlayerVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getPlayingTitle() {
        return mPlayingTitle;
    }

    public MutableLiveData<Integer> getPlayingStatus() {
        return mPlayingStatus;
    }

    public MutableLiveData<Object> getPlayingPicModel() {
        return mPlayingPicModel;
    }

    public MutableLiveData<Bitmap> getPlayingPicBitmap() {
        return mPlayingPicBitmap;
    }


}
