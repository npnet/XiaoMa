package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.media.CarAudioManager;
import android.car.media.ICarVolumeCallback;
import android.database.ContentObserver;
import android.database.IContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.utils.LogUtils;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2018/10/23 0023
 */
public class XmCarAudioManager extends BaseCarManager<CarAudioManager> implements ICarAudio {
    private static final String TAG = XmCarAudioManager.class.getSimpleName();
    private static final String SERVICE_NAME = Car.AUDIO_SERVICE;
    private static XmCarAudioManager instance;
    private ContentObserver contentObserver;

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
        contentObserver = new ContentObserver(null) {
            @Override
            public IContentObserver getContentObserver() {
                return super.getContentObserver();
            }

            @Override
            public IContentObserver releaseContentObserver() {
                return super.releaseContentObserver();
            }

            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri, int userId) {
                super.onChange(selfChange, uri, userId);
            }
        };
    }

    private void changeEvent(CarPropertyValue carPropertyValue) {
        XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
        LogUtils.e(TAG, "changeEvent key id:" + carPropertyValue.getPropertyId() + ":" + carPropertyValue.getValue());
    }

    private CarEvent carPropertyToXmEvent(CarPropertyValue carPropertyValue) {
        return new CarEvent(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getValue());
    }


    public void setStreamVolume(int streamType, int volume) {
        LogUtils.e(TAG, " ????????????: ??????=" + streamType + " ?????????=" + volume);
        if (getManager() != null) {
            try {
                getManager().setGroupVolume(streamType, volume, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.e(TAG, " manager is null");
        }
    }

    public int getStreamVolume(int streamType) {
        LogUtils.e(TAG, " ????????????: ??????=:" + streamType);
        int volume = Integer.MAX_VALUE;
        if (getManager() != null) {
            try {
                volume = getManager().getGroupVolume(streamType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.e(TAG, " manager is null");
        }
        return volume;
    }

    /**
     * ????????????
     * ?????????????????????
     */
    public boolean setCarMasterMute(boolean isMute) {
        LogUtils.e(TAG, " ????????????:" + isMute);
        // TODO: 2019/5/14 0014 3.6jar??????????????????
        int flags_media = SDKConstants.MEDIA_VOLUME;
        int flags_bt_media = SDKConstants.BT_MEDIA_VOLUME;
        if (getManager() != null) {
            try {
                getManager().setMasterMute(isMute, flags_media);
                getManager().setMasterMute(isMute, flags_bt_media);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * ?????????????????????????????????????????????
     */
    public int getCarVolumeGroupId() {
        int result = -1;
        if (getManager() != null) {
            try {
                result = getManager().getCarVolumeGroupId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean isMute() {
        // TODO: 2019/5/14 0014 3.6jar??????????????????
        boolean result = false;
        if (getManager() != null) {
            try {
                result = getManager().isMasterMute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Override
    public void onCarServiceConnected(IBinder binder) {
        super.onCarServiceConnected();
        //???????????? ??????callBack
        KLog.d("hzx","onCarServiceConnected");
        if (getManager() != null) {
            try {
                getManager().registerVolumeChangeObserver(contentObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCarServiceDisconnected() {
        super.onCarServiceDisconnected();
        //????????????
        KLog.d("hzx","onCarServiceDisconnected");
    }
}
