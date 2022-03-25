package com.xiaoma.pet.common.utils;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.model.GearSpeedData;
import com.xiaoma.carlib.model.SpeedData;
import com.xiaoma.pet.common.annotation.UnityAction;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/30 0030 15:46
 *   desc:   处理车信号
 * </pre>
 */
public class ReceiveCarSignal implements ICarEvent {


    private static final String TAG = ReceiveCarSignal.class.getSimpleName();
    private static final long INTERVAL_LIMIT = 5 * 60 * 1000;
    private boolean driving;
    private long currentTime;


    public static ReceiveCarSignal getInstance() {
        return Holder.RECEIVE_CAR_SIGNAL;
    }

    public void register() {
        XmCarEventDispatcher.getInstance().registerEvent(this);
    }

    public void unRegister() {
        XmCarEventDispatcher.getInstance().unregisterEvent(this);
    }

    public int getCarSpeed() {
        return XmCarVendorExtensionManager.getInstance().getCarSpeed();
    }

    private ReceiveCarSignal() {
    }

    @Override
    public void onCarEvent(CarEvent event) {
        //TODO 获取实时车速，改变宠物行为，并记录行驶时间戳
        if (event.id == SDKConstants.ID_SPEED_INFO) {
            if (event.value instanceof GearSpeedData) {
                GearSpeedData gearSpeedData = (GearSpeedData) event.value;
                SpeedData speedData = gearSpeedData.speedData;
                if (speedData == null) {
                    KLog.w(TAG, "SpeedData instance is null.");
                    return;
                }

                KLog.d(TAG, "SpeedData variable ===> speed: " + speedData.carSpeed +
                        "  timestamp: " + speedData.timestamp);

                if (speedData.carSpeed > 0) {
                    ConvertMapTimeCoordinate.startRunning();
                } else {
                    ConvertMapTimeCoordinate.postRunningTimePercentToServer();
                }

                //TODO 下发速度至unity，改变宠物奔跑速度
                AUBridgeDispatcher.getInstance().callUnity(System.currentTimeMillis(), UnityAction.SPEED, speedData.carSpeed);
            }
        }
    }



    private static class Holder {
        private static final ReceiveCarSignal RECEIVE_CAR_SIGNAL = new ReceiveCarSignal();
    }
}
