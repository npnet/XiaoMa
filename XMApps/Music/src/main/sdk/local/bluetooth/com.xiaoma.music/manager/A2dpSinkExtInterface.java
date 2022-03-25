package com.xiaoma.music.manager;

/**
 * Created by hamm on 19-5-10.
 */

public interface A2dpSinkExtInterface {

    boolean isConnected();

    String getConnectedAddress();

    void pauseRender();

    void startRender();

    boolean setLocalVolume(float vol);

    boolean setStreamType(int type);

    int getStreamType();

    void setPlaybackStateCallback(A2dpPlaybackStateCallback callback);
}
