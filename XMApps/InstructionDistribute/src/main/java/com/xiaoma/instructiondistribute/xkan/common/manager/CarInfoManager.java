package com.xiaoma.instructiondistribute.xkan.common.manager;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Thomas on 2019/2/27 0027
 * about car interface
 */

public class CarInfoManager {


    private static final String TAG = "CarInfoManager";

    private CarInfoManager() {
    }

    private static class InstanceHolder {
        static final CarInfoManager sInstance = new CarInfoManager();
    }

    public static CarInfoManager getInstance() {
        return CarInfoManager.InstanceHolder.sInstance;
    }


    /**
     * true 禁止行车观看视频
     * false 不禁止行车观看视频
     *
     * @return
     */
    public boolean getIsWatchVideoInDriving() {
        boolean isWatchVideo = false;
        try {
            isWatchVideo = XmCarFactory.getCarVendorExtensionManager().getBanVideoStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        KLog.d(TAG,"getIsWatchVideoInDriving" + isWatchVideo + "(false can watch else not)");
        return isWatchVideo;
    }

    public int getCurrentSpeedData() {
        int currentSpeedData = 0;
        try {
            currentSpeedData = XmCarFactory.getCarVendorExtensionManager().getCarSpeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        KLog.d(TAG,"getCurrentSpeedData"  + currentSpeedData);
        return currentSpeedData;
    }

}
