package com.xiaoma.carlib.wheelcontrol;

/**
 * Created by LKF on 2019-5-6 0006.
 */
public class WheelConstant {
    /**
     * 方控服务挂在那个APP上,包名是哪个APP的包名.
     * 如果方控服务换了APP,一定要同步修改此包名.
     */
    public static final String WHEEL_SERVICE_PACKAGE_NAME = "com.xiaoma.systemui";

    public static final String ACTION_WHEEL_KEY_EVENT = "com.xiaoma.carlib.wheelcontrol.KEY_EVENT";
    static final String ACTION_WHEEL_SERVICE = "com.xiaoma.carlib.WheelControl.SERVICE";

    public static final String EXTRA_KEY_ACTION = "keyAction";
    public static final String EXTRA_KEY_CODE = "keyCode";
}
