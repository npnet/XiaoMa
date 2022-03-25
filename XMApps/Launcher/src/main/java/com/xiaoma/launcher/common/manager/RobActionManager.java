package com.xiaoma.launcher.common.manager;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/08/05
 *     desc   :
 * </pre>
 */
public class RobActionManager {
    private static final String TAG = "[RobActionManager]";
    private static RobActionManager mRobActionManager;

    private RobActionManager() {
    }

    public static RobActionManager getInstance() {
        if (mRobActionManager == null) {
            synchronized (WeatherManager.class) {
                if (mRobActionManager == null) {
                    mRobActionManager = new RobActionManager();
                }
            }
        }
        return mRobActionManager;
    }

    public void setRobAction(int value) {
        KLog.e(TAG,"setRobAction() value="+value);
        XmCarVendorExtensionManager.getInstance().setRobAction(value);
    }
}
