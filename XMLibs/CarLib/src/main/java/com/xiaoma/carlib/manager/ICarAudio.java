package com.xiaoma.carlib.manager;

/**
 * @author: iSun
 * @date: 2018/12/21 0021
 */
public interface ICarAudio {

    public void setStreamVolume(int streamType, int volume);

    public int getStreamVolume(int streamType);

    public boolean setCarMasterMute(boolean isMute);

    public boolean isMute();
}
