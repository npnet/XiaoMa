package com.xiaoma.launcher.common.manager;

import android.content.Intent;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.model.GearData;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.favorites.CarInfoManager;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.screentool.ScreenControlUtil;

import org.simple.eventbus.EventBus;

/**
 * @author taojin
 * @date 2019/4/17
 */
public class LauncherCarEvent implements ICarEvent {

    private static final String TAG = LauncherCarEvent.class.getSimpleName();
    private CallBack mCallBack;

    public static LauncherCarEvent getInstance() {
        return LauncherCarEventHolder.instance;
    }

    private static class LauncherCarEventHolder {
        private static final LauncherCarEvent instance = new LauncherCarEvent();
    }

    private LauncherCarEvent() {

    }

    public CallBack getmCallBack() {
        return mCallBack;
    }

    public void setmCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onCarEvent(CarEvent event) {
//        KLog.d(TAG, "CarEvent" + event.value);
        if (event.id == SDKConstants.CarSenSor.GEAR_ID) {

            if (event.value instanceof GearData) {
                GearData gearData = (GearData) event.value;
                KLog.e(TAG, "SPEED_INFO: " + gearData.toString());
                CarInfoManager.getInstance().setGearData(gearData.gear);
                if (mCallBack != null) {
                    //TODO 等待后续可以获取到车速信息数据优化上报策略，避免频繁上报
                    mCallBack.onGearDataUpdate(gearData);
                }
            }

        } else if (SDKConstants.ID_SPEED_INFO == event.id) {
            int value = (int) event.value;
            KLog.e(TAG, "SPEED_INFO: " + value / 100);
            CarInfoManager.getInstance().setCarSpeed(value / 100);
            if (mCallBack != null) {
                //TODO 等待后续可以获取到车速信息数据优化上报策略，避免频繁上报
                mCallBack.onSpeedData(value / 100);
            }
            // 发送实时车速至车载微信服务端
            CarInfoClient.getInstance().notifyCarSpeed(value / 100);
        } else if (SDKConstants.CarSenSor.WHEEL_ANGEL_ID == event.id) {
            //转角
            int[] value = (int[]) event.value;
            KLog.e(TAG, "WHEEL_ANGEL: " + value);
            CarInfoManager.getInstance().setWheelAngle(value);

        } else if (SDKConstants.ENGINE_STATE == event.id) {
            int value = (int) event.value;
            KLog.e(TAG, "ENGINE_STATE: " + value);
            CarInfoManager.getInstance().setAccState(value);
            if (value == SDKConstants.EngineState.ENGINE_OFF) {
                //熄火
                sendEngineBroadCast(LauncherConstants.EngineEvent.DRIVE_STOP_ACTION);
                // 2019-8-26 狗尾草那边确认:关机时会主动播放退场动画,不需要我们发退场动作的指令
                //RobActionManager.getInstance().setRobAction(LauncherConstants.TRUN_OFF_ACTION);
            } else if (value == SDKConstants.EngineState.ENGINE_RUNNING) {
                //开火
                sendEngineBroadCast(LauncherConstants.EngineEvent.DRIVE_START_ACTION);
            }
        } else if (SDKConstants.ID_SCREEN_STATUS == event.id) {
            boolean screenStatus = (boolean) event.value;
            if (screenStatus) {
                ScreenControlUtil.sendTurnOnScreenBroadCast(AppHolder.getInstance().getAppContext());
            } else {
                ScreenControlUtil.sendTurnOffScreenBroadCast(AppHolder.getInstance().getAppContext());
            }
        } else if (SDKConstants.WifiMode.WIFI_MODE_ID == event.id) {
            int wifiMode = (int) event.value;
            CarInfoManager.getInstance().setWorkPattern(wifiMode);
            EventBus.getDefault().post(wifiMode, LauncherConstants.WIFI_WORK_PATTERN);
        } else if (SDKConstants.SIMMode.SIM_SWITCH_ID == event.id) {
            boolean isDataSwitch = (boolean) event.value;
            EventBus.getDefault().post(isDataSwitch, LauncherConstants.IS_DATA_SWITCH);
            CarInfoManager.getInstance().setDataSwitch(isDataSwitch);
        } else if (SDKConstants.ID_FUEL_WARNING == event.id) {
            //0 油量充足，1 低油量
            int active = (int) event.value;
//            TPUtils.put(AppHolder.getInstance().getAppContext(), LauncherConstants.IS_FUEL_WARNING, active);
            EventBus.getDefault().post(active, LauncherConstants.IS_FUEL_WARNING);
        }

    }

    interface CallBack {
        void onGearDataUpdate(GearData gearData);

        void onSpeedData(int speedData);
    }

    //发送广播通知启明
    private void sendEngineBroadCast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        AppHolder.getInstance().getAppContext().sendBroadcast(intent);
    }

}
