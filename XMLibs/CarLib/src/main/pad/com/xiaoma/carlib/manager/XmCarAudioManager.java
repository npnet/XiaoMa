package com.xiaoma.carlib.manager;


import android.os.IBinder;

/**
 * @author: iSun
 * @date: 2018/10/23 0023
 */
public class XmCarAudioManager extends BaseCarManager implements ICarAudio {
    private static final String TAG = XmCarAudioManager.class.getSimpleName();
    private static final String SERVICE_NAME = "";
    private static XmCarAudioManager instance;

    public static XmCarAudioManager getInstance() {
        if (instance == null) {
            synchronized (XmCarAudioManager.class) {
                if (instance == null) {
                    instance = new XmCarAudioManager();
                }
            }
        }
        return instance;
    }

    private XmCarAudioManager() {
        super(SERVICE_NAME);
    }

    @Override
    public void setStreamVolume(int streamType, int volume) {

    }

    @Override
    public int getStreamVolume(int streamType) {
        return 0;
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {

    }

    /**
     * 设置静音
     */
    public boolean setCarMasterMute(boolean isMute) {
        return false;
    }

    @Override
    public boolean isMute() {
        return false;
    }

    public int getCarVolumeGroupId() {
        return 0;
    }
}
