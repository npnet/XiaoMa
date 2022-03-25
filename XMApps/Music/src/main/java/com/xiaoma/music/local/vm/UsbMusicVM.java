package com.xiaoma.music.local.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
public class UsbMusicVM extends AndroidViewModel {

    private MutableLiveData<ArrayList<UsbMusic>> mUsbList;

    public UsbMusicVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<UsbMusic>> getUsbList() {
        if (mUsbList == null) {
            mUsbList = new MutableLiveData<>();
        }
        return mUsbList;
    }

    public void playMusic(int position) {
        ArrayList<UsbMusic> value = getUsbList().getValue();
        if (!ListUtils.isEmpty(value)) {
            UsbMusic usbMusic = value.get(position);
            UsbMusicFactory.getUsbPlayerProxy().play(usbMusic);
//            UsbMusicFactory.getUsbPlayerListProxy().addUsbMusicList(value);
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mUsbList = null;
    }
}
