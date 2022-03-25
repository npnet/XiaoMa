package com.xiaoma.systemui.common.ui;

/**
 * Created by LKF on 2018/11/1 0001.
 */
public interface ISystemUI {
    void show();

    void update();

    void dismiss();

    boolean isShowing();
}
