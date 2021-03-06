package com.xiaoma.process.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.aidl.setting.ISettingBinderPool;
import com.xiaoma.aidl.setting.ISettingCarAidlInterface;
import com.xiaoma.aidl.setting.ISettingConnectionAidlInterface;
import com.xiaoma.aidl.setting.ISettingDisplayAidlInterface;
import com.xiaoma.aidl.setting.ISettingNotifyAidlInterface;
import com.xiaoma.aidl.setting.ISettingSoundAidlInterface;
import com.xiaoma.process.base.BaseAidlServiceBindManager;
import com.xiaoma.process.base.BaseApiManager;
import com.xiaoma.process.constants.XMApiConstants;
import com.xiaoma.utils.log.KLog;

public class XMSettingApiManager extends BaseApiManager<ISettingBinderPool> {

    public static final int SETTING_DISPLAY_BINDER = 0x001;
    public static final int SETTING_SOUND_BINDER = 0x002;
    public static final int SETTING_CONNECTION_BINDER = 0x003;
    public static final int SETTING_CAR_BINDER = 0x004;

    private ISettingDisplayAidlInterface mSettingDisplayAidlInterface;
    private ISettingConnectionAidlInterface mSettingConnectionAidlInterface;
    private ISettingSoundAidlInterface mSettingSoundAidlInterface;
    private ISettingCarAidlInterface mSettingCarAidlInterface;

    private ISettingNotifyAidlInterface.Stub mNotifyAidl = new ISettingNotifyAidlInterface.Stub(){

        @Override
        public void onConferConnectWifi(String wifiName, boolean status) throws RemoteException {
            KLog.d("ljb", "onConferConnectwifi wifiName:" + wifiName + ";status:" + status);
        }

        @Override
        public void onConferConnectBlueTooch(String blueTouchName, boolean status) throws RemoteException {
            KLog.d("ljb", "onConferConnectBlueTooch blueTouchName:" + blueTouchName + ";status:" + status);
        }

        @Override
        public void onConferConnectHotspot(String hotspotName, boolean status) throws RemoteException {
            KLog.d("ljb", "onConferConnectHotspot hotspotName:" + hotspotName + ";status:" + status);
        }

    };

    XMSettingApiManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean bindService(){
        return this.bindServiceConnected();
    }

    @Override
    public void unBindService() {
    }

    private boolean bindServiceConnected(){
        if(aidlServiceBind == null){
            aidlServiceBind = new BaseAidlServiceBindManager<ISettingBinderPool>(context, XMApiConstants.SETTING_SERVICE_CONNECT_ACTION, XMApiConstants.SETTING, this){
                @Override
                public ISettingBinderPool initServiceByIBinder(IBinder service) {
                    return ISettingBinderPool.Stub.asInterface(service);
                }
            };
        }
        if(!aidlServiceBind.isConnectedRemoteServer()) {
            return aidlServiceBind.connectRemoteService();
        }else {
            return true;
        }
    }

    @Override
    public void onConnected() {
        initAidlInterface();
    }

    private void initAidlInterface(){
        try {
            IBinder displayBinder = aidlServiceBind.getServerInterface().queryBinder(SETTING_DISPLAY_BINDER);
            mSettingDisplayAidlInterface = ISettingDisplayAidlInterface.Stub.asInterface(displayBinder);

            IBinder connectionBinder = aidlServiceBind.getServerInterface().queryBinder(SETTING_CONNECTION_BINDER);
            mSettingConnectionAidlInterface = ISettingConnectionAidlInterface.Stub.asInterface(connectionBinder);
            mSettingConnectionAidlInterface.registerStatusNotify(mNotifyAidl);

            IBinder soundBinder = aidlServiceBind.getServerInterface().queryBinder(SETTING_SOUND_BINDER);
            mSettingSoundAidlInterface = ISettingSoundAidlInterface.Stub.asInterface(soundBinder);

            IBinder carBinder = aidlServiceBind.getServerInterface().queryBinder(SETTING_CAR_BINDER);
            mSettingCarAidlInterface = ISettingCarAidlInterface.Stub.asInterface(carBinder);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //????????????????????????
    public int getDisplayMode(){
        int mode = 0;
        try {
            mode = mSettingDisplayAidlInterface.getDisplayMode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mode;
    }
    //????????????????????????
    public void setDisplayMode(int mode){
        try {
            mSettingDisplayAidlInterface.setDisplayMode(mode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public int getDisplayLevel(){
        int level = 0;
        try {
            level = mSettingDisplayAidlInterface.getDisplayLevel();
        }catch (Exception e){
            e.printStackTrace();
        }
        return level;
    }
    //??????????????????
    public void setDisplayLevel(int level){
        try {
            mSettingDisplayAidlInterface.setDisplayLevel(level);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public int getKeyBoardLevel(){
        int level = 0;
        try {
            level = mSettingDisplayAidlInterface.getKeyBoardLevel();
        }catch (Exception e){
            e.printStackTrace();
        }
        return level;
    }
    //??????????????????
    public void setKeyBoardLevel(int value){
        try {
            mSettingDisplayAidlInterface.setKeyBoardLevel(value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public boolean getBanVideoStatus(){
        boolean status = false;
        try {
            status = mSettingDisplayAidlInterface.getBanVideoStatus();
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //??????????????????
    public void setBanVideoStatus(boolean status){
        try {
            mSettingDisplayAidlInterface.setBanVideoStatus(status);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //????????????
    public void closeScreen(){
        try {
            mSettingDisplayAidlInterface.closeScreen();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //??????wifi????????????
    public boolean getWifiConnection(){
        boolean status = false;
        try {
            status = mSettingConnectionAidlInterface.getWifiConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //??????wifi
    public void connectWifi(){
        try {
            mSettingConnectionAidlInterface.connectWifi();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????wifi
    public void closeWifi(){
        try {
            mSettingConnectionAidlInterface.closeWifi();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public boolean getBlueToothStatus(){
        boolean status = false;
        try {
            status = mSettingConnectionAidlInterface.getBlueToothStatus();
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????
    public void connectBlueTooth() {
        try {
            mSettingConnectionAidlInterface.connectBlueTooth();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //????????????
    public void closeBlueTooch(){
        try {
            mSettingConnectionAidlInterface.closeBlueTooch();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public boolean getHotspotStatus(){
        boolean status = false;
        try {
            status = mSettingConnectionAidlInterface.getHotspotStatus();
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????
    public void openHotspot(){
        try {
            mSettingConnectionAidlInterface.openHotspot();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //????????????
    public void closeHotspot(){
        try {
            mSettingConnectionAidlInterface.closeHotspot();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public void setInternetType(int type){
        try {
            mSettingConnectionAidlInterface.setInternetType(type);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //????????????(????????????)
    public int getSoundValue(){
        int value = 0;
        try {
            mSettingSoundAidlInterface.getSoundValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }
    //????????????(????????????)
    public void setSoundValue(int value){
        try {
            mSettingSoundAidlInterface.setSoundValue(value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public void setSoundEffectsMode(int mode){
        try {
            mSettingSoundAidlInterface.setSoundEffectsMode(mode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public int getSoundEffectsMode(){
        int mode = 0;
        try {
            mode = mSettingSoundAidlInterface.getSoundEffectsMode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mode;
    }
    //?????????????????????
    public void setCustomSoundEffects(int zone, int effectLevel){
        try {
            mSettingSoundAidlInterface.setCustomSoundEffects(zone, effectLevel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //??????Arkamys3D?????????????????????
    public void setArkamys3D(int value){
        try {
            mSettingSoundAidlInterface.setArkamys3D(value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //??????Arkamys3D?????????????????????
    public int getArkamys3D(){
        try {
            return mSettingSoundAidlInterface.getArkamys3D();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //??????????????????
    public void setSoundFieldMode(int soundFieldMode){
        try {
            mSettingSoundAidlInterface.setSoundFieldMode(soundFieldMode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public int getSoundFieldMode(){
        int mode = 0;
        try {
            mode = mSettingSoundAidlInterface.getSoundFieldMode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mode;
    }
    //?????????????????????
    public int getSoundEffectPosition(){
        int position = 0;
        try {
            position = mSettingSoundAidlInterface.getSoundEffectPosition();
        }catch (Exception e){
            e.printStackTrace();
        }
        return position;
    }
    //????????????????????????????????????
    public void setOnOffMusic(boolean opened){
        try {
            mSettingSoundAidlInterface.setOnOffMusic(opened);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //????????????????????????????????????
    public boolean getOnOffMusic(){
        boolean status = false;
        try {
            status = mSettingSoundAidlInterface.getOnOffMusic();
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //??????????????????????????????????????????????????????
    public void setCarInfoSound(int level){
        try {
            mSettingSoundAidlInterface.setCarInfoSound(level);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????????????????????????????????????????
    public int getCarInfoSound(){
        int level = 0;
        try {
            level = mSettingSoundAidlInterface.getCarInfoSound();
        }catch (Exception e){
            e.printStackTrace();
        }
        return level;
    }
    //?????????????????????????????????
    public void setCarSpeedVolumeCompensate(int volume){
        try {
            mSettingSoundAidlInterface.setCarSpeedVolumeCompensate(volume);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //?????????????????????????????????
    public int getCarSpeedVolumeCompensate(){
        int level = 0;
        try {
            level = mSettingSoundAidlInterface.getCarSpeedVolumeCompensate();
        }catch (Exception e){
            e.printStackTrace();
        }
        return level;
    }
    //???????????????????????????????????????????????????????????????
    public void setParkMediaVolume(int volume){
        try {
            mSettingSoundAidlInterface.setParkMediaVolume(volume);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //???????????????????????????????????????????????????????????????
    public int getParkMediaVolume(){
        int volume = 0;
        try {
            volume = mSettingSoundAidlInterface.getParkMediaVolume();
        }catch (Exception e){
            e.printStackTrace();
        }
        return volume;
    }

    public void registerNotifyStatus(ISettingNotifyAidlInterface notifyAidlInterface){
        try {
            mSettingConnectionAidlInterface.registerStatusNotify(notifyAidlInterface);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void unRegisterNotifyStatus(ISettingNotifyAidlInterface notifyAidlInterface){
        try {
            mSettingConnectionAidlInterface.unRegisterStatusNotify(notifyAidlInterface);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //FCW???????????????/AEB???????????? ??????/??????
    public boolean setFCwAebSwitch(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setFCwAebSwitch(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //ISA?????????????????? ??????/??????
    public boolean setISA(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setISA(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????????????????
    public boolean setDAW(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setDAW(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????????????????EPB
    public boolean setElectronicBrake(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setElectronicBrake(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //?????????????????????
    public boolean setSeatBelt(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setSeatBelt(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????
    public boolean setSpeedLockControl(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setSpeedLockControl(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //??????????????????
    public void setLeaveAutomaticLock(boolean state){
        try {
            mSettingCarAidlInterface.setLeaveAutomaticLock(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //??????????????????
    public boolean setSelfClosingWindow(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setSelfClosingWindow(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //???????????????
    public void setTrunk(boolean state){
        try {
            mSettingCarAidlInterface.setTrunk(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //???????????????????????????
    public boolean setRearviewMirror(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setRearviewMirror(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //??????????????????
    public boolean setWelcomeSeat(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setWelcomeSeat(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????
    public boolean setLeaveLight(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setLeaveLight(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //?????????
    public boolean setWelcomeLight(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setWelcomeLight(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //IHC????????????
    public boolean setIHC(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setIHC(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //???????????????
    public boolean setAmbientLightSwitch(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setAmbientLightSwitch(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
    //????????????????????????
    public boolean setSceneLightSwitch(boolean state){
        boolean status = false;
        try {
            status = mSettingCarAidlInterface.setSceneLightSwitch(state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
}
