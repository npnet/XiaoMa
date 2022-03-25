package com.xiaoma.music.callback;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public interface OnBTConnectStateChangeListener {
    void onBTConnect();

    void onBTDisconnect();

    void onBTSinkConnected();

    void onBTSinkDisconnected();
}
