package com.xiaoma.launcher.wheel;

import android.car.media.CarAudioManager;
import android.content.Context;

import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/20
 *     desc   :
 * </pre>
 */
public class WheelManager {
    private OnWheelKeyListener.Stub mKeyListener;
    public Context mContext;
    private final Object lock = new Object();
    private static WheelManager mWheelManager;

    private WheelManager() {
    }

    public static WheelManager getInstance() {
        if (mWheelManager == null) {
            synchronized (WheelManager.class) {
                if (mWheelManager == null) {
                    mWheelManager = new WheelManager();
                }
            }
        }
        return mWheelManager;
    }

    public void init(Context context) {
    }

    private void initWheel() {
    }

    private void connectCarService() {
    }

    public void registerCarLibListener() {
    }

    public void unregisterCarLibListener() {
    }

    public void setCarVolume(int volume, int flags) {
    }

    public int getCarVolumeGroupId() {
        return 0;
    }

    public int getGroupVolume(int groupId) {
        return 0;
    }

    public void registerVolumeCallback(CarVolume carVolume) {

    }

    public interface CarVolume {
        void onCarVolumeChanged(int i, int i1, int i2);
    }

    public CarAudioManager getCarAudioManager() {
        return new CarAudioManager(null, null, null);
    }

}
