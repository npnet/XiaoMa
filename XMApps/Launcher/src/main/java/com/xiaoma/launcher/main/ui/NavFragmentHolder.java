package com.xiaoma.launcher.main.ui;

import android.annotation.SuppressLint;

import com.mapbar.android.launchersupport.NaviSupportFragment;

/**
 * Created by LKF on 2019-7-15 0015.
 */
class NavFragmentHolder {
    @SuppressLint("StaticFieldLeak")
    private static NaviSupportFragment sInstance;

    static NaviSupportFragment getInstance() {
        if (sInstance == null) {
            synchronized (NavFragmentHolder.class) {
                if (sInstance == null) {
                    sInstance = NaviSupportFragment.newInstance();
                }
            }
        }
        return sInstance;
    }
}
