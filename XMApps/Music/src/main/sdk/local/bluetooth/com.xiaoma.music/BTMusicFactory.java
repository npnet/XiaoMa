package com.xiaoma.music;


import com.xiaoma.music.manager.IBTMusic;
import com.xiaoma.music.proxy.XMBTControlProxy;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public class BTMusicFactory {

    public static IBTMusic getBTMusicControl() {
        return XMBTControlProxy.getInstance();
    }
}
