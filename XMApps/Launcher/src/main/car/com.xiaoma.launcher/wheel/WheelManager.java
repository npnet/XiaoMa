package com.xiaoma.launcher.wheel;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.media.CarAudioManager;
import android.car.media.ICarVolumeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.launcher.common.views.NaviBarControlLayout;
import com.xiaoma.launcher.common.views.VolumeBarWindow;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/20
 *     desc   :
 * </pre>
 */
public class WheelManager {
    private final static String TAG = "[WheelManager]";
    private static WheelManager mWheelManager;
    private final Object lock = new Object();
    public Context mContext;
    private OnWheelKeyListener.Stub mKeyListener;
    private Car car;
    private CarAudioManager carAudioManager;
    private boolean LONG_PRESS = false;
    private final ServiceConnection carConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KLog.e(TAG, "onServiceConnected()");
            synchronized (lock) {
                try {
                    carAudioManager = (CarAudioManager) car.getCarManager(Car.AUDIO_SERVICE);
                } catch (CarNotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            KLog.e(TAG, "onServiceDisconnected()");
            synchronized (lock) {
                carAudioManager = null;
            }
            connectCarService();
        }
    };
    private CarVolume mCarVolume;

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
        KLog.e(TAG, "init()");
        mContext = context;
        initWheel();
    }

    private void initWheel() {
        KLog.e(TAG, "initWheel()");
        XmWheelManager.getInstance().init(mContext);
        car = Car.createCar(mContext, carConn);
        connectCarService();
        initKeyEvent();
        VolumeBarWindow.getVolumeWindow().init(mContext);
    }

    private void connectCarService() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                car.connect();
            }
        });
    }

    public void initKeyEvent() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                registerCarLibListener();
            }
        }, 3000);
    }

    public CarAudioManager getCarAudioManager() {
        return carAudioManager;
    }

    public void registerCarLibListener() {
        KLog.e(TAG, "registerCarLibListener()");
        XmWheelManager.getInstance().register(mKeyListener = new OnWheelKeyListener.Stub() {
            @Override
            public boolean onKeyEvent(int keyAction, int keyCode) {
                KLog.e(TAG, "onKeyEvent() keyAction=" + keyAction + " keyCode=" + keyCode);
                if (WheelKeyEvent.KEYCODE_WHEEL_MUTE == keyCode) {
                    switch (keyAction) {
                        case WheelKeyEvent.ACTION_CLICK:
                            if (carAudioManager != null) {
                                try {
                                    if (carAudioManager.isMasterMute()) {
                                        carAudioManager.setMasterMute(false, 0);
                                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                                            @Override
                                            public void run() {
                                                VolumeBarWindow.getVolumeWindow().showVolumeWindow();
                                            }
                                        });
                                    } else {
                                        carAudioManager.setMasterMute(true, 0);
                                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                                            @Override
                                            public void run() {
                                                VolumeBarWindow.getVolumeWindow().showVolumeWindow();
                                            }
                                        });
                                    }
                                } catch (CarNotConnectedException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case WheelKeyEvent.ACTION_LONG_PRESS:
                            if (mContext != null) {
                                KLog.d("WheelEventTag", "?????????????????? --- ????????????");
                                KeyEventUtils.isGoHome(mContext, mContext.getPackageName());
                            }
                            break;
                    }
                } else if (keyCode == WheelKeyEvent.KEYCODE_WHEEL_VOL_ADD) {
                    KLog.e(TAG, "onKeyEvent() KEYCODE_WHEEL_VOL_ADD   keyCode=" + keyCode);
                    switch (keyAction) {
                        case WheelKeyEvent.ACTION_CLICK:
                            volumeAdjustUp();
                            break;
                        case WheelKeyEvent.ACTION_LONG_PRESS:
                            LONG_PRESS = true;
                            threadUp();
                            break;
                        case WheelKeyEvent.ACTION_RELEASE:
                            LONG_PRESS = false;
                            break;
                    }

                } else if (keyCode == WheelKeyEvent.KEYCODE_WHEEL_VOL_SUB) {
                    KLog.e(TAG, "onKeyEvent() KEYCODE_WHEEL_VOL_DOWN   keyCode=" + keyCode);
                    switch (keyAction) {
                        case WheelKeyEvent.ACTION_CLICK:
                            volumeAdjustDown();
                            break;
                        case WheelKeyEvent.ACTION_LONG_PRESS:
                            LONG_PRESS = true;
                            threadDown();
                            break;
                        case WheelKeyEvent.ACTION_RELEASE:
                            LONG_PRESS = false;
                            break;
                    }

                }
                return true;
            }

            @Override
            public String getPackageName() throws RemoteException {
                return mContext.getPackageName();
            }
        }, new int[]{WheelKeyEvent.KEYCODE_WHEEL_MUTE, WheelKeyEvent.KEYCODE_WHEEL_VOL_ADD, WheelKeyEvent.KEYCODE_WHEEL_VOL_SUB});
    }

    private void volumeAdjustUp() {
        KLog.e(TAG, "volumeAdjustUp()");
        try {
            if (carAudioManager != null) {
                carAudioManager.adjustCarVolume(CarAudioManager.VOLUME_ADJUST_UP, 0);
            }
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {

                    NaviBarControlLayout.setTouchBar(false);
                    VolumeBarWindow.getVolumeWindow().showVolumeWindow();

                }
            });
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void threadUp() {
        KLog.e(TAG, "threadUp()");
        volumeAdjustUp();
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                KLog.e(TAG, "threadUp() run() LONG_PRESS= " + LONG_PRESS);
                if (LONG_PRESS) {
                    threadUp();
                }
            }
        }, 200);
    }

    private void volumeAdjustDown() {
        KLog.e(TAG, "volumeAdjustDown()");
        try {
            if (carAudioManager != null) {
                carAudioManager.adjustCarVolume(CarAudioManager.VOLUME_ADJUST_DOWN, 0);
            }
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    NaviBarControlLayout.setTouchBar(false);
                    VolumeBarWindow.getVolumeWindow().showVolumeWindow();

                }
            });
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void threadDown() {
        KLog.e(TAG, "threadDown()");
        volumeAdjustDown();
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                KLog.e(TAG, "threadDown() run() LONG_PRESS= " + LONG_PRESS);
                if (LONG_PRESS) {
                    threadDown();

                }
            }
        }, 200);
    }

    /**
     * ?????????????????????????????????????????????
     * ????????????????????????????????????????????????
     *
     * @return
     */
    public int getCarVolumeGroupId() {
        int ret = 0;
        if (null != carAudioManager) {
            try {
                ret = carAudioManager.getCarVolumeGroupId();
                KLog.e(TAG, "getCarVolumeGroupId() ret=" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {

            }
        }
        return ret;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????0-40
     * ???????????????1-40
     * ???????????????0-8
     * ???????????????0-40
     *
     * @param groupId
     * @return
     */
    public int getGroupVolume(int groupId) {
        int ret = 0;
        if (null != carAudioManager) {
            try {
                ret = carAudioManager.getGroupVolume(groupId);
                KLog.e(TAG, "getGroupVolume() ret=" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {

            }
        }
        return ret;
    }

    public void unregisterCarLibListener() {
        if (mKeyListener != null) {
            XmWheelManager.getInstance().unregister(mKeyListener);
        }
    }

    /**
     * ????????????????????????????????????
     * ???????????????????????????
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param volume
     * @param flags  ???????????????????????????????????????0??????
     */
    public void setCarVolume(int volume, int flags) {
        KLog.e(TAG, "setCarVolume() volume=" + volume + " , flags=" + flags);
        if (null != carAudioManager) {
            try {
                carAudioManager.setCarVolume(volume, flags);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {

            }
        }
    }

    /**
     * ??????????????????
     *
     * @param carVolume
     */
    public void registerVolumeCallback(CarVolume carVolume) {
        mCarVolume = carVolume;
        if (null != carAudioManager) {
            try {
                carAudioManager.registerVolumeCallback(new ICarVolumeCallback.Stub() {
                    @Override
                    public void onGroupVolumeChanged(int i, int i1) throws RemoteException {
                        KLog.e(TAG, "onGroupVolumeChanged() i=" + i + " , i1=" + i1);

                    }

                    @Override
                    public void onMasterMuteChanged(int i) throws RemoteException {
                        KLog.e(TAG, "onGroupVolumeChanged() i=" + i);

                    }

                    /**
                     *????????????????????????????????????????????????
                     * ?????????????????????????????????????????????,??????????????????????????????????????????
                     *
                     * ????????????????????????????????????????????????
                     * ???????????????????????????????????????,????????????????????????????????????????????????????????????????????????
                     * @param i     ????????????
                     * @param i1    ????????????
                     * @param i2    ????????????
                     * @throws RemoteException
                     */
                    @Override
                    public void onCarVolumeChanged(int i, int i1, int i2) throws RemoteException {
                        KLog.e(TAG, "onGroupVolumeChanged() i=" + i + " , i1=" + i1 + " , i2=" + i2);
                        if (null != mCarVolume) {
                            mCarVolume.onCarVolumeChanged(i, i1, i2);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {

            }
        }
    }

    public interface CarVolume {
        void onCarVolumeChanged(int i, int i1, int i2);
    }

}
