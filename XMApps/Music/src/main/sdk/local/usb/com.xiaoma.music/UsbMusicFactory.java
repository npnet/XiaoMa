package com.xiaoma.music;

import android.content.Context;

import com.xiaoma.music.manager.IUsbMusic;
import com.xiaoma.music.manager.IUsbMusicList;
import com.xiaoma.music.manager.UsbPlayerListProxy;
import com.xiaoma.music.manager.UsbPlayerProxy;
import com.xiaoma.utils.receiver.UsbDetector;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public class UsbMusicFactory {

    public static IUsbMusic getUsbPlayerProxy() {
        return UsbPlayerProxy.getInstance();
    }

    public static IUsbMusicList getUsbPlayerListProxy() {
        return UsbPlayerListProxy.getInstance();
    }

    public static void init(Context context) {
        UsbDetector.getInstance().init(context);
        UsbMusicFactory.getUsbPlayerProxy().init(context);
        UsbMusicFactory.getUsbPlayerListProxy().init(context);
    }
}
