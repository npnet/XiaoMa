package com.xiaoma.setting.service;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.setting.ISettingBinderPool;
import com.xiaoma.process.manager.XMSettingApiManager;

/**
 * Created by LaiLai on 2018/12/5 0005.
 */

public class SettingBinderPool extends ISettingBinderPool.Stub {

    private Context mContext;

    public SettingBinderPool(Context context) {
        mContext = context;
    }

    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException {
        IBinder binder = null;
        switch (binderCode) {
            case XMSettingApiManager.SETTING_DISPLAY_BINDER:
                binder = new SettingDisplayBinder(mContext);
                break;
            case XMSettingApiManager.SETTING_CONNECTION_BINDER:
                binder = new SettingConnectionBinder(mContext);
                break;
            case XMSettingApiManager.SETTING_SOUND_BINDER:
                binder = new SettingSoundBinder(mContext);
                break;
            case XMSettingApiManager.SETTING_CAR_BINDER:
                binder = new SettingCarBinder(mContext);
                break;
        }
        return binder;
    }
}
