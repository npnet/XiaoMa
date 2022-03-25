package com.xiaoma.music.kuwo.proxy;

import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.observer.IMessageObserver;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class XMKWPlayStateObserverProxy implements IMessageObserver {


    private final IMessageObserver playObserver;

    public static XMKWPlayStateObserverProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final XMKWPlayStateObserverProxy instance = new XMKWPlayStateObserverProxy();
    }

    public XMKWPlayStateObserverProxy() {
        playObserver = KuwoPlayControlObserver.getInstance();
    }

    @Override
    public void addPlayStateListener(OnPlayControlListener kwStateListener) {
        playObserver.addPlayStateListener(kwStateListener);
    }

    @Override
    public void removePlayStateListener(OnPlayControlListener kwStateListener) {
        playObserver.removePlayStateListener(kwStateListener);
    }

    @Override
    public void registerKuwoStateListener() {
        playObserver.registerKuwoStateListener();
    }

    @Override
    public void unregisterKuwoStateListener() {
        playObserver.unregisterKuwoStateListener();
    }
}
