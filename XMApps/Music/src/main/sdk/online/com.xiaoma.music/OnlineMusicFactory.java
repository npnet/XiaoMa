package com.xiaoma.music;


import com.xiaoma.music.kuwo.proxy.XMKWMusicListControlProxy;
import com.xiaoma.music.kuwo.proxy.XMKWPlayStateObserverProxy;
import com.xiaoma.music.kuwo.proxy.XMKWAudioFetchProxy;
import com.xiaoma.music.kuwo.proxy.XMKWLoginProxy;
import com.xiaoma.music.kuwo.proxy.XMKWPlayerOperateProxy;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public class OnlineMusicFactory {

    public static XMKWAudioFetchProxy getKWAudioFetch() {
        return XMKWAudioFetchProxy.getInstance();
    }

    public static XMKWPlayerOperateProxy getKWPlayer() {
        return XMKWPlayerOperateProxy.getInstance();
    }

    public static XMKWLoginProxy getKWLogin() {
        return XMKWLoginProxy.getInstance();
    }

    public static XMKWPlayStateObserverProxy getKWMessage() {
        return XMKWPlayStateObserverProxy.getInstance();
    }

    public static XMKWMusicListControlProxy getMusicListControl() {
        return XMKWMusicListControlProxy.getInstance();
    }
}
