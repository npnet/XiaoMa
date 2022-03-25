package com.xiaoma.xkan.common.manager;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.utils.log.KLog;

/**
 * @author taojin
 * @date 2019/4/17
 */
public class XkanCarEvent implements ICarEvent {

    private static final String TAG = XkanCarEvent.class.getSimpleName();
    private final int SPEED = 10;
    private CallBack mCallBack;

    public static XkanCarEvent getInstance() {
        return LauncherCarEventHolder.instance;
    }

    private static class LauncherCarEventHolder {
        private static final XkanCarEvent instance = new XkanCarEvent();
    }

    private XkanCarEvent() {

    }

    public CallBack getmCallBack() {
        return mCallBack;
    }

    public void setmCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onCarEvent(CarEvent event) {
        try {
            if (event.id == SDKConstants.ID_SPEED_INFO) {
                int speedData = (int) event.value;
                KLog.e(TAG, "CarEvent Speed" + event.value);
                //TODO 等待后续可以获取到车速信息数据在具体调整
                if (CarInfoManager.getInstance().getIsWatchVideoInDriving() && speedData / 100 >= SPEED) {
                    mCallBack.onBanVideo();
                } else if (speedData / 100 >= SPEED) {
                    mCallBack.onUnBanVideo();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallBack {
        /**
         * 禁止行车观看视频
         */
        void onBanVideo();

        /**
         * 允许行车观看视频
         */
        void onUnBanVideo();
    }

}
