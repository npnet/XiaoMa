package com.xiaoma.setting.client;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarCabin;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.setting.common.constant.ClientConstants;
import com.xiaoma.utils.log.KLog;


public class SettingClient extends Client {

    private IVendorExtension mCarVendorExtensionSdkManager;
    private final ICarCabin carCabinManager;

    public SettingClient(Context context, int port) {
        super(context, port);
        mCarVendorExtensionSdkManager = XmCarFactory.getCarVendorExtensionManager();
        carCabinManager = XmCarFactory.getCarCabinManager();
    }

    @Override
    protected void onReceive(int action, Bundle data) {
        KLog.d("ljb", "onReceive:action=" + action);
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        KLog.d("ljb", "onRequest:action=" + action);
        switch (action){
            //显示
            //获取当前显示模式
            case ClientConstants.ApiAction.GET_DISPLAY_MODE:
                int mode = mCarVendorExtensionSdkManager.getDisplayMode();
                handleCallBack(callback, ClientConstants.DataKey.KEY_DISPLAY_MODE, mode);
                break;
            //设置当前显示模式
            case ClientConstants.ApiAction.SET_DISPLAY_MODE:
                mCarVendorExtensionSdkManager.setDisplayMode(data.getInt(ClientConstants.DataKey.KEY_DISPLAY_MODE));
                break;
            //获取屏幕亮度
            case ClientConstants.ApiAction.GET_DISPLAY_LEVEL:
                //int displayLevel = mCarVendorExtensionSdkManager.getDisplayLevel();
                handleCallBack(callback, ClientConstants.DataKey.KEY_DISPLAY_LEVEL, 2);
                break;
            //设置屏幕亮度
            case ClientConstants.ApiAction.SET_DISPLAY_LEVEL:
                mCarVendorExtensionSdkManager.setDisplayLevel(data.getInt(ClientConstants.DataKey.KEY_DISPLAY_LEVEL));
                break;
            //获取按键亮度
            case ClientConstants.ApiAction.GET_KEYBOARD_LEVEL:
                int keyBoardLevel = mCarVendorExtensionSdkManager.getKeyBoardLevel();
                handleCallBack(callback, ClientConstants.DataKey.KEY_KEYBOARD_LEVEL, keyBoardLevel);
                break;
            //设置按键亮度
            case ClientConstants.ApiAction.SET_KEYBOARD_LEVEL:
                mCarVendorExtensionSdkManager.setKeyBoardLevel(data.getInt(ClientConstants.DataKey.KEY_KEYBOARD_LEVEL));
                break;
            //获取获取屏保状态
            case ClientConstants.ApiAction.GET_BAN_VIDEO_STATUS:
                boolean banVideoStatus = mCarVendorExtensionSdkManager.getBanVideoStatus();
                handleCallBack(callback, ClientConstants.DataKey.KEY_BAN_VIDEO_STATUS, banVideoStatus);
                break;
            //设置屏保状态
            case ClientConstants.ApiAction.SET_BAN_VIDEO_STATUS:
                mCarVendorExtensionSdkManager.setBanVideoStatus(data.getBoolean(ClientConstants.DataKey.KEY_BAN_VIDEO_STATUS));
                break;
            //关闭屏幕
            case ClientConstants.ApiAction.CLOSE_SCREEN:
                mCarVendorExtensionSdkManager.closeScreen();
                break;

                //声音
            //获取音量(快捷方式)
            case ClientConstants.ApiAction.GET_SOUND_VALUE:
                int soundValue = 0;
                //todo
                handleCallBack(callback, ClientConstants.DataKey.KEY_SOUND_VALUE, soundValue);
                break;
            //设置音量(快捷方式)
            case ClientConstants.ApiAction.SET_SOUND_VALUE:
                //todo
                break;
            //设置音效模式
            case ClientConstants.ApiAction.SET_SOUND_EFFETCS_MODE:
                mCarVendorExtensionSdkManager.setSoundEffectsMode(data.getInt(ClientConstants.DataKey.KEY_SOUND_EFFETCS_MODE));
                break;
            //获取音效模式
            case ClientConstants.ApiAction.GET_SOUND_EFFETCS_MODE:
                int effectsMode = mCarVendorExtensionSdkManager.getSoundEffectsMode();
                handleCallBack(callback, ClientConstants.DataKey.KEY_SOUND_EFFETCS_MODE, effectsMode);
                break;
            //设置Arkamys3D音效（开、关）
            case ClientConstants.ApiAction.SET_ARKAMYS3D_STATUS:
                mCarVendorExtensionSdkManager.setArkamys3D(data.getInt(ClientConstants.DataKey.KEY_ARKAMYS3D_STATUS));
                break;
            //获取音效模式
            case ClientConstants.ApiAction.GET_ARKAMYS3D_STATUS:
                int status = mCarVendorExtensionSdkManager.getArkamys3D();
                handleCallBack(callback, ClientConstants.DataKey.KEY_ARKAMYS3D_STATUS, status);
                break;
            //设置声场模式
            case ClientConstants.ApiAction.SET_SOUNDFIELD_MODE_OR_POSITION:
                mCarVendorExtensionSdkManager.setSoundFieldMode(data.getInt(ClientConstants.DataKey.KEY_SOUNDFIELD_MODE));
                break;
            //获取声场模式
            case ClientConstants.ApiAction.GET_SOUNDFIELD_MODE:
                handleCallBack(callback, ClientConstants.DataKey.KEY_SOUNDFIELD_MODE, mCarVendorExtensionSdkManager.getSoundFieldMode());
                break;
            //获取最佳听音位
            case ClientConstants.ApiAction.GET_SOUNDFIELD_POSITION:
//                handleCallBack(callback, ClientConstants.DataKey.KEY_SOUNDFIELD_POSITION, mCarVendorExtensionSdkManager.getSoundEffectPosition());
                break;
            //设置开关机音效（开、关）
            case ClientConstants.ApiAction.SET_ON_OFF_MUSIC_STATUS:
                mCarVendorExtensionSdkManager.setOnOffMusic(data.getBoolean(ClientConstants.DataKey.KEY_ON_OFF_MUSIC_STATUS));
                break;
            //获取开关机音效（开、关）
            case ClientConstants.ApiAction.GET_ON_OFF_MUSIC_STATUS:
                handleCallBack(callback, ClientConstants.DataKey.KEY_ON_OFF_MUSIC_STATUS, mCarVendorExtensionSdkManager.getOnOffMusic());
                break;
            //设置车辆提示音的级别（一级或是二级）
            case ClientConstants.ApiAction.SET_CAR_INFO_SOUND:
                mCarVendorExtensionSdkManager.setCarInfoSound(data.getInt(ClientConstants.DataKey.KEY_CAR_INFO_SOUND));
                break;
            //获取车辆提示音的级别（一级或是二级）
            case ClientConstants.ApiAction.GET_CAR_INFO_SOUND:
                handleCallBack(callback, ClientConstants.DataKey.KEY_CAR_INFO_SOUND, mCarVendorExtensionSdkManager.getCarInfoSound());
                break;
            //设置车速音量补偿的级别
            case ClientConstants.ApiAction.SET_CAR_SPEED_VOLUME:
                mCarVendorExtensionSdkManager.setCarSpeedVolumeCompensate(data.getInt(ClientConstants.DataKey.KEY_CAR_SPEED_VOLUME));
                break;
            //获取车速音量补偿的级别
            case ClientConstants.ApiAction.GET_CAR_SPEED_VOLUME:
                handleCallBack(callback, ClientConstants.DataKey.KEY_CAR_SPEED_VOLUME, mCarVendorExtensionSdkManager.getCarSpeedVolumeCompensate());
                break;
            //设置泊车媒体音量的级别（静音、弱化、正常）
            case ClientConstants.ApiAction.SET_PARK_MEDIA_VOLUME:
                mCarVendorExtensionSdkManager.setParkMediaVolume(data.getInt(ClientConstants.DataKey.KEY_PARK_MEDIA_VOLUME));
                break;
            //获取泊车媒体音量的级别（静音、弱化、正常）
            case ClientConstants.ApiAction.GET_PARK_MEDIA_VOLUME:
                handleCallBack(callback, ClientConstants.DataKey.KEY_PARK_MEDIA_VOLUME, mCarVendorExtensionSdkManager.getParkMediaVolume());
                break;

                //连接
            case ClientConstants.ApiAction.GET_WIFY_CONNECTION:
                //todo
                break;
             case ClientConstants.ApiAction.CONNECT_WIFY:
                 //todo
                 break;
            case ClientConstants.ApiAction.OPEN_WIFY:
                //todo
                break;
            case ClientConstants.ApiAction.CLOSE_WIFY:
                //todo
                break;
            case ClientConstants.ApiAction.GET_BLUETOOTH_CONNECTION:
                //todo
                break;
            case ClientConstants.ApiAction.CONNECT_BLUETOOTH:
                //todo
                break;
            case ClientConstants.ApiAction.OPEN_BLUETOOTH:
                //todo
                break;
            case ClientConstants.ApiAction.GET_HOTSPOT:
                //todo
                break;
            case ClientConstants.ApiAction.CONNECT_HOTSPOT:
                //todo
                break;
            case ClientConstants.ApiAction.OPEN_HOTSPOT:
                //todo
                break;
            case ClientConstants.ApiAction.CLOSE_HOTSPOT:
                //todo
                break;
            case ClientConstants.ApiAction.SET_INTERNET_TYPE:
                //todo
                break;

                //车身
            //FCW前防撞预警/AEB主动制动 开启/关闭
            case ClientConstants.ApiAction.SET_FCW_AEB_STATE:
                boolean fcwAebState = data.getBoolean(ClientConstants.DataKey.KEY_FCW_AEB);
                mCarVendorExtensionSdkManager.setFcwAebSwitch(fcwAebState ? SDKConstants.VALUE.FCW_ON_REQ : SDKConstants.VALUE.FCW_OFF_REQ);
                break;
            //ISA交通标志识别 开启/关闭
            case ClientConstants.ApiAction.SET_ISA_STATE:
                boolean isaState = data.getBoolean(ClientConstants.DataKey.KEY_ISA);
                mCarVendorExtensionSdkManager.setISA(isaState ? SDKConstants.VALUE.IFC_ACTIVE : SDKConstants.VALUE.IFC_OFF);
                break;
            //驾驶员注意力提醒
            case ClientConstants.ApiAction.SET_DAW_STATE:
                boolean dawState = data.getBoolean(ClientConstants.DataKey.KEY_DAW);
                mCarVendorExtensionSdkManager.setDAW(dawState ? SDKConstants.VALUE.DAW_ON_REQ : SDKConstants.VALUE.DAW_OFF_REQ);
                break;
            //电子刹车自动夹紧EPB
            case ClientConstants.ApiAction.SET_EPB_STATE:
                boolean epbState = data.getBoolean(ClientConstants.DataKey.KEY_EPB);
                mCarVendorExtensionSdkManager.setEPB(epbState ? SDKConstants.VALUE.EPB_ON : SDKConstants.VALUE.EPB_OFF_REQ);
                break;
            //后排安全带提醒
            case ClientConstants.ApiAction.SET_SEAT_BELT_STATE:
                boolean seatBeltState = data.getBoolean(ClientConstants.DataKey.KEY_SEAT_BELT);
                carCabinManager.setRearBeltWorningSwitch(seatBeltState ? SDKConstants.VALUE.REAR_BELT_ON_REQ : SDKConstants.VALUE.REAR_BELT_OFF);
                break;
            //随速闭锁
            case ClientConstants.ApiAction.SET_SPEED_LOCK_CONTROL:
                boolean speedLockControlState = data.getBoolean(ClientConstants.DataKey.KEY_SPEED_LOCK_CONTROL);
                mCarVendorExtensionSdkManager.setSpeedAutoLock(speedLockControlState);
                break;
            //离车自动落锁
            case ClientConstants.ApiAction.SET_LEAVE_LOCK_STATE:
                boolean leaveLockState = data.getBoolean(ClientConstants.DataKey.KEY_LEAVE_LOCK);
                mCarVendorExtensionSdkManager.setLeaveAutoLock(leaveLockState);
                break;
            //锁车自动关窗
            case ClientConstants.ApiAction.SET_SELF_CLOSING_WINDOW_STATE:
                boolean closingWindowState = data.getBoolean(ClientConstants.DataKey.KEY_SELF_CLOSING_WINDOW);
                mCarVendorExtensionSdkManager.setSelfClosingWindow(closingWindowState);
                break;
            //智能行李箱
            case ClientConstants.ApiAction.SET_TRUNK_STATE:
                boolean trunkState = data.getBoolean(ClientConstants.DataKey.KEY_TRUNK);
                mCarVendorExtensionSdkManager.setAutomaticTrunk(trunkState);
                break;
            //锁车后视镜自动折叠
            case ClientConstants.ApiAction.SET_REARVIEW_MIRROR_STATE:
                boolean mirrorState = data.getBoolean(ClientConstants.DataKey.KEY_REARVIEW_MIRROR);
                carCabinManager.setRearviewMirror(mirrorState);
                break;
            //座椅迎宾退让
            case ClientConstants.ApiAction.SET_WELCOME_SEAT_STATE:
                boolean welcomeState = data.getBoolean(ClientConstants.DataKey.KEY_WELCOME_SEAT);
                mCarVendorExtensionSdkManager.setWelcomeSeat(welcomeState);
                break;
            //离车灯光
            case ClientConstants.ApiAction.SET_LEAVE_LIGHT_STATE:
                boolean leaveLightState = data.getBoolean(ClientConstants.DataKey.KEY_LEAVE_LIGHT);
                mCarVendorExtensionSdkManager.setWelcomeLightByRhythm(leaveLightState);
                break;
            //迎宾灯
            case ClientConstants.ApiAction.SET_WELCOME_LIGHT_STATE:
                boolean welcomeLightState = data.getBoolean(ClientConstants.DataKey.KEY_WELCOME_LIGHT);
                int value = SDKConstants.VALUE.WELCOM_LIGHT_OFF_REQ;
                if (welcomeLightState == true) {
                    value = SDKConstants.VALUE.WELCOM_LIGHT_ON;
                }
                mCarVendorExtensionSdkManager.setWelcomeLampTime(value);
                break;
            //IHC智能远光
            case ClientConstants.ApiAction.SET_IHC_STATE:
                boolean ihcState = data.getBoolean(ClientConstants.DataKey.KEY_IHC);
                mCarVendorExtensionSdkManager.setIHC(ihcState ? SDKConstants.VALUE.IHC_ON : SDKConstants.VALUE.IHC_OFF);
                break;
            //氛围灯开关
            case ClientConstants.ApiAction.SET_AMBIENT_LIGHT_STATE:
                boolean ambientLightState = data.getBoolean(ClientConstants.DataKey.KEY_AMBIENT_LIGHT);
                mCarVendorExtensionSdkManager.setAmbientLightSwitch(ambientLightState ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON : SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF);
                break;
            //音乐情景随动开关
            case ClientConstants.ApiAction.SET_SCENE_LIGHT_STATE:
                boolean sceneLightState = data.getBoolean(ClientConstants.DataKey.KEY_SCENE_LIGHT);
                mCarVendorExtensionSdkManager.setMusicSceneFollow(sceneLightState ? SDKConstants.VALUE.MUSIC_FOLLOW_ON : SDKConstants.VALUE.MUSIC_FOLLOW_OFF);
                break;
            //获取车机的vin码
            case ClientConstants.ApiAction.GET_ID_VIN_INFO:
                String vinInfo = mCarVendorExtensionSdkManager.getVinInfo();
                handleCallBack(callback, ClientConstants.DataKey.KEY_GET_ID_VIN_INFO, vinInfo);
                break;
            case ClientConstants.ApiAction.GET_DRIVE_DISTANCE:
                //todo
                int distance = 100;
                handleCallBack(callback, ClientConstants.DataKey.KEY_GET_DRIVE_DISTANCE, distance);
                break;
        }
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        KLog.d("ljb", "SettingClient-onConnect");
    }

    private void handleCallBack(ClientCallback callback, String key, int value){
        Bundle bundle = new Bundle();
        bundle.putInt(key, value);
        KLog.d("ljb", "handleCallBack:callback=" + callback);
        callback.setData(bundle);
        callback.callback();
    }

    private void handleCallBack(ClientCallback callback, String key, boolean value){
        Bundle bundle = new Bundle();
        bundle.putBoolean(key, value);
        callback.setData(bundle);
        callback.callback();
    }

    private void handleCallBack(ClientCallback callback, String key, String value){
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        callback.setData(bundle);
        callback.callback();
    }

}
