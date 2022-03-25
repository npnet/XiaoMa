package com.xiaoma.music.kuwo.observer;

import com.xiaoma.music.kuwo.listener.OnPlayControlListener;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public interface IMessageObserver {
    void addPlayStateListener(OnPlayControlListener kwStateListener);

    void removePlayStateListener(OnPlayControlListener kwStateListener);

    void registerKuwoStateListener();

    void unregisterKuwoStateListener();
}
