package com.xiaoma.carlib.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;
import android.util.Log;

/**
 * 用于获取升级状态和监听升级状态变化
 */
public class UpgradeState {
    private static final String TAG = UpgradeState.class.getSimpleName();
    private static final String UPDATE_STATE = "update_state";
    /**
     * 非升级状态
     */
    public static final int NO_UPDATE = 0;
    public static final int USB_UPDATE = 1;
    public static final int OTA_UPDATE = 2;

    private Context mContext;

    public UpgradeState(Context context) {
        mContext = context;
    }

    public void register(ContentObserver observer) {
        mContext.getContentResolver().registerContentObserver(
                Settings.Global.getUriFor(UPDATE_STATE), false, observer);
    }

    public void unregister(ContentObserver observer) {
        mContext.getContentResolver().unregisterContentObserver(observer);
    }

    /**
     * 获取升级标志位
     *
     * @return state {@link #NO_UPDATE} 非升级状态，可正常启动
     */
    public int getUpdateState() {
        int state = NO_UPDATE;
        try {
            state = Settings.Global.getInt(mContext.getContentResolver(), UPDATE_STATE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getUpdateState: state = " + state);
        return state;
    }

}
