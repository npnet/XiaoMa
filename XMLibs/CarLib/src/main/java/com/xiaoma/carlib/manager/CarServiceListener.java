package com.xiaoma.carlib.manager;

import android.os.IBinder;

/**
 * @author: iSun
 * @date: 2019/2/28 0028
 */
public interface CarServiceListener {
    public void onCarServiceConnected(IBinder binder);
    public void onCarServiceDisconnected();
}
