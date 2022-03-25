package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
public class UsbPlayListVM extends BaseViewModel {

    private MutableLiveData<List<UsbMusic>> mUsbPlayList;

    public UsbPlayListVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<UsbMusic>> getUsbPlayList() {
        if (mUsbPlayList == null) {
            mUsbPlayList = new MutableLiveData<>();
        }
        return mUsbPlayList;
    }

    public void fetchUsbPlaylist() {
        List<UsbMusic> usbMusics = UsbMusicFactory.getUsbPlayerListProxy().getUsbMusicList();
        if (!ListUtils.isEmpty(usbMusics)) {
            getUsbPlayList().postValue(usbMusics);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mUsbPlayList = null;
    }
}
