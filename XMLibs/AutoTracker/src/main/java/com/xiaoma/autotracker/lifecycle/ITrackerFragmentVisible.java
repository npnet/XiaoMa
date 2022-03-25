package com.xiaoma.autotracker.lifecycle;

import android.support.v4.app.Fragment;

/**
 * 用于监听[Fragment]的可见性
 *
 * @author taojin
 * @date 2018/12/6
 */
public interface ITrackerFragmentVisible {

    /**
     * 在Fragment中的setUserVisibleHint()和onHidden()方法被调用时，同步调用该方法
     * 以便于能够正确的观察到Fragment状态的变化
     */
    void onFragmentVisibilityChanged(boolean visible, Fragment f);
}
