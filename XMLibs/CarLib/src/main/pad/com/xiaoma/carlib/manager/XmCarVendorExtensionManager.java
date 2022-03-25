package com.xiaoma.carlib.manager;


import android.graphics.Point;
import android.os.IBinder;

import com.xiaoma.carlib.callback.onGetIntArrayResultListener;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: iSun
 * @date: 2018/10/22 0022
 */
public class XmCarVendorExtensionManager extends BaseCarManager implements IVendorExtension {
    private static final String TAG = XmCarVendorExtensionManager.class.getSimpleName();
    private static final String SERVICE_NAME = "";
    private static XmCarVendorExtensionManager instance;
    private CarServiceListener carServiceListener;

    private DualScreenTabChangeListener mDualScreenTabChangeListener;

    public void setRobClothMode(int roleId, int clothId) {

    }

    public interface DualScreenTabChangeListener {

        void onTabChangeFromLib(int value);

        void onLeftTabChangeFromLib(int state);

        void onThemeChangeFromLib(int value);

    }

    public void setDualScreenTabChangeListener(DualScreenTabChangeListener dualScreenTabChangeListener) {
        this.mDualScreenTabChangeListener = dualScreenTabChangeListener;
    }

    private Set<onGetIntArrayResultListener> effectsListener = new HashSet<>();
    private List<ValueChangeListener> valueChangeListeners = new ArrayList<>();

    public void removeEffectsListener(onGetIntArrayResultListener listener) {
        if (listener != null) {
            effectsListener.remove(listener);
        }
    }

    public void addEffectsListener(onGetIntArrayResultListener listener) {
        if (listener != null) {
            effectsListener.add(listener);
        }
    }

    public static XmCarVendorExtensionManager getInstance() {
        if (instance == null) {
            synchronized (XmCarVendorExtensionManager.class) {
                if (instance == null) {
                    instance = new XmCarVendorExtensionManager();
                }
            }
        }
        return instance;
    }

    private XmCarVendorExtensionManager() {
        super(SERVICE_NAME);
    }

/*
    @Override
    public boolean getRearviewMirror() {
        return false;
    }

    @Override
    public void setRearviewMirror(boolean value) {

    }*/

    @Override
    public int getModeGoHome() {
        return 0;
    }

    @Override
    public void setModeGoHome(int value) {

    }

    @Override
    public int getAmbientLightSwitch() {
        return 0;
    }

    @Override
    public void setAmbientLightSwitch(int value) {

    }

    @Override
    public void setMarkMirrorLeft(int id) {

    }

    @Override
    public void setMarkMirrorRight(int id) {

    }


    @Override
    public int getAmbientLightBrightness() {
        return 0;
    }

    @Override
    public void setAmbientLightBrightness(int value) {

    }

    @Override
    public int getAmbientLightColor() {
        return 0;
    }

    @Override
    public void setAmbientLightColor(int value) {

    }

    @Override
    public void setLaneChangeFlicker(int value) {

    }

    @Override
    public int getLaneChangeFlicker() {
        return 0;
    }

    @Override
    public int getWelcomeLampSwitch() {
        return 0;
    }

    @Override
    public void setWelcomeLampSwitch(int value) {

    }

    @Override
    public int getWelcomeLampTime() {
        return 0;
    }

    @Override
    public void setWelcomeLampTime(int value) {

    }

    @Override
    public boolean getWelcomeSeat() {
        return false;
    }

    @Override
    public void setWelcomeSeat(boolean value) {

    }

    /**
     * 判断电子驻车是否上锁
     */
    public boolean isEPBLocked() {
        return true;
    }

    @Override
    public int getFcwAebSwitch() {
        return 0;
    }

    @Override
    public void setFcwAebSwitch(int value) {

    }

    @Override
    public int getFcwSensitivity() {
        return 0;
    }

    @Override
    public void setFcwSensitivity(int value) {

    }

    /* @Override
     public int getRearBeltWorningSwitch() {
         return 0;
     }

     @Override
     public void setRearBeltWorningSwitch(int value) {

     }
 */
    @Override
    public int getLdwSensitivity() {
        return 0;
    }

    @Override
    public void setLdwSensitivity(int value) {

    }

    @Override
    public int getLKA() {
        return 0;
    }

    @Override
    public void setLKA(int value) {

    }

    @Override
    public int getISA() {
        return 0;
    }

    @Override
    public void setISA(int value) {

    }

    @Override
    public int getIHC() {
        return 0;
    }

    @Override
    public void setIHC(int value) {

    }

    @Override
    public int getDAW() {
        return 0;
    }

    @Override
    public void setDAW(int value) {

    }

    @Override
    public int getSTT() {
        return 0;
    }

    @Override
    public void setSTT(int value) {

    }

    @Override
    public boolean getWelcomeLightByRhythm() {
        return false;
    }

    @Override
    public void setWelcomeLightByRhythm(boolean value) {

    }

    @Override
    public int getInteriorLightDelay() {
        return 0;
    }

    @Override
    public void setInteriorLightDelay(int value) {

    }

    @Override
    public boolean getAmbientLightByRhythm() {
        return false;
    }

    @Override
    public void setAmbientLightByRhythm(boolean value) {

    }

    @Override
    public boolean getAutomaticTrunk() {
        return false;
    }

    @Override
    public void setAutomaticTrunk(boolean value) {

    }

    @Override
    public void setResetTiretPressure(int value) {

    }

    @Override
    public int getEPB() {
        return 0;
    }

    @Override
    public void setEPB(int value) {

    }

    @Override
    public void setSelfClosingWindow(boolean value) {

    }

    @Override
    public boolean getSelfClosingWindow() {
        return false;
    }

    @Override
    public void setSoundEffectsMode(int mode) {

    }

    @Override
    public int getSoundEffectsMode() {
        return 0;
    }

    @Override
    public void setCustomSoundEffects(Integer[] arr) {

    }

    @Override
    public List<Integer> getCurrentSoundEffects(int mode) {
        return null;
    }

    @Override
    public void setArkamys3D(int value) {

    }

    @Override
    public int getArkamys3D() {
        return 0;
    }

    @Override
    public void setSoundFieldMode(int soundFieldMode) {

    }

    @Override
    public void setSoundEffectPositionAtAnyPoint(int x, int y) {

    }

    @Override
    public int getSoundFieldMode() {
        return 0;
    }

    @Override
    public Point getSoundEffectPositionAtAnyPoint() {
        return new Point();
    }

    @Override
    public void setOnOffMusic(boolean opened) {

    }

    @Override
    public boolean getOnOffMusic() {
        return false;
    }

    @Override
    public void setCarInfoSound(int level) {

    }

    @Override
    public int getCarInfoSound() {
        return 0;
    }

    @Override
    public void setCarSpeedVolumeCompensate(int volume) {

    }

    @Override
    public int getCarSpeedVolumeCompensate() {
        return 0;
    }

    @Override
    public void setParkMediaVolume(int volume) {

    }

    @Override
    public int getParkMediaVolume() {
        return 0;
    }

    @Override
    public int getDisplayMode() {
        return 0;
    }

    @Override
    public void setDisplayMode(int mode) {

    }

    @Override
    public int getDayDisplayLevel() {
        return 0;
    }

    @Override
    public int getNightDisplayLevel() {
        return 0;
    }

    @Override
    public void setDayDisplayLevel(int value) {

    }

    @Override
    public int getAutoDisplayLevel() {
        return 0;
    }

    @Override
    public void setAutoDisplayLevel(int value) {

    }

    @Override
    public void setNightDisplayLevel(int value) {

    }

    @Override
    public int getDisplayLevel() {
        return 0;
    }

    @Override
    public void setDisplayLevel(int value) {

    }

    @Override
    public int getKeyBoardLevel() {
        return 0;
    }

    @Override
    public void setKeyBoardLevel(int value) {

    }

    @Override
    public boolean getBanVideoStatus() {
        return false;
    }

    @Override
    public void setBanVideoStatus(boolean value) {

    }

    @Override
    public int getTheme() {
        return 0;
    }

    @Override
    public boolean setTheme(int value) {
        return false;
    }

    public boolean getScreenStatus() {

        return false;
    }

    @Override
    public void closeScreen() {

    }

    @Override
    public void turnOnScreen() {

    }

    @Override
    public void setSpeedAutoLock(boolean value) {

    }

    @Override
    public boolean getSpeedAutoLock() {
        return false;
    }

    @Override
    public void setLeaveAutoLock(boolean value) {

    }

    @Override
    public boolean getLeaveAutoLock() {
        return false;
    }

    /*@Override
    public void setRemoteControlUnlockMode(int value) {

    }*/

   /* @Override
    public int getRemoteControlUnlockMode() {
        return 0;
    }*/

    @Override
    public void setMusicSceneFollow(boolean value) {

    }

    @Override
    public boolean getMusicSceneFollow() {
        return false;
    }

    @Override
    public int getEngineState() {
        return 0;
    }

    @Override
    public String getVinInfo() {
        return "";
    }

    @Override
    public int getIllStatus() {
        return 0;
    }

    @Override
    public int getLanguage() {
        return 0;
    }

    @Override
    public void setLanguage(int languageType) {

    }

    @Override
    public void setTime(long time) {

    }

    @Override
    public void setInteriorLightSwitch(boolean state) {

    }

    @Override
    public int getInteriorLightSwitch() {
        return 0;
    }

    //获取降噪模式
    public int getSrMode() {
        return 0;
    }

    //设置降噪模式
    public void setSrMode(int value) {

    }

    @Override
    public int getFuelConsumption() {
        return 0;
    }

    public void setFarTest(boolean value) {

    }

    /*@Override
    public int getFarTest() {
        return 0;
    }*/

    @Override
    public int getOdometerResidual() {
        return 0;
    }

    @Override
    public void setFarAutoLock(boolean value) {

    }

    @Override
    public int getFarAutoLock() {
        return 0;
    }

    @Override
    public void setApproachAutoUnlock(boolean value) {

    }

    @Override
    public int getApproachAutoUnlock() {
        return 0;
    }

    public void setCloseTest(boolean value) {

    }

    /*@Override
    public int getCloseTest() {
        return 0;
    }*/

    @Override
    public int getTiredState() {
        return 0;
    }

    @Override
    public void setTiredState(int value) {

    }

    @Override
    public int getDistractionState() {
        return 0;
    }

    @Override
    public void setDistractionState(int value) {

    }

    @Override
    public int getBadDriverState() {
        return 0;
    }

    @Override
    public void setBadDriverState(int value) {

    }

    @Override
    public int getRecognizeAvailable() {
        return 0;
    }

    @Override
    public void setRecognizeAvailable(int value) {

    }

    public int getRecognizeState() {
        return 0;
    }

    @Override
    public void setRecognize(int value) {

    }

    @Override
    public void startFaceRecord(int userid) {

    }

    @Override
    public void cancelFaceRecord() {

    }

    @Override
    public void delFaceRecord(int userid) {

    }

    @Override
    public int getCarKey() {
        return 0;
    }

    @Override
    public int getCarSpeed() {
        return 0;
    }

    @Override
    public int getTestValue(int canstant) {
        return 0;
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {

    }

    public void removeValueChangeListener(ValueChangeListener valueChangeListener) {
        valueChangeListeners.remove(valueChangeListener);
    }

    public void addValueChangeListener(ValueChangeListener valueChangeListener) {
        valueChangeListeners.add(valueChangeListener);
    }

    @Override
    public List<Integer> getRobVersion() {
        return null;
    }

    @Override
    public int getRobAction() {
        return 30;
    }

    @Override
    public void setRobAction(int value) {

    }

    @Override
    public void setRobBrightnessIncrease() {
        KLog.d("增加全息设备亮度 ");
    }

    @Override
    public void setRobBrightnessDecrease() {
        KLog.d("减少全息设备亮度 ");
    }

    @Override
    public int getRobBrightness() {
        return 0;
    }


    @Override
    public void setRobClothMode(int value) {

    }

    @Override
    public void setRobCharacterMode(int value) {

    }

    @Override
    public void setHoloId(int holoId) {

    }

    @Override
    public void setRobSwitcher(int value) {

    }

    @Override
    public void setRobFiceDir(int value) {

    }

    @Override
    public void setSynchronizeTimeSwitch(boolean value) {

    }

    @Override
    public void setWindowFortRearHeat(boolean state) {

    }

    @Override
    public Integer[] getConfigInfo() {
        return new Integer[0];
    }

    public interface ValueChangeListener {
        void onChange(int id, Object value);
    }

    @Override
    public void setSubwoofer(boolean open) {

    }

    @Override
    public boolean getSubwoofer() {
        return false;
    }

    @Override
    public void onAvsSwitch() {

    }

    @Override
    public void closeAvs() {

    }

    @Override
    public boolean getCameraStatus() {
        return false;
    }


    @Override
    public int getSensitivityLevel() {
        return 0;
    }

    @Override
    public void setSensivityLevel(int level) {

    }

    @Override
    public int getOdometer() {
        return 0;
    }

    /*双屏互动*/
    //请求切换到指定的tab(主动请求)
    public void setInteractModeReq(int mode) {
    }

    //请求切换到指定的tab(被动请求，用于接收了信号再发送)
    public void setInteractMode(int mode) {
    }

    //获取仪表当前tab的位置和获取tab变化的通知,用于开始投屏后通知仪表
    public int getInteractMode() {
        return 0;
    }

    public void setMediaMenuLevel(int level) {
    }

    public void setNaviDisplay(int value) {
    }

    public void setSimpleMenuDisplay(int value) {
    }

    @Override
    public Integer[] getShowTime() {
        return new Integer[0];
    }

    //仪表选择及方控按键通知（上，下，左，右，返回，确定，接听，挂断等）
    public void setMultifuncSwitch(int value) {
    }

    //左侧仪表投屏,CanCommon_ON,CanCommon_OFF
    public void setIcMenuDisplayReq(int value) {
    }

    public Boolean getSynchronizeTimeSwitch() {
        return true;
    }

    public void setRestoreCmd(int value) {
    }

    public void setRearviewMirror(int value) {
    }

    @Override
    public int getRearViewMirrorEnable() {
        return 0;
    }


    //获取当前油量状态
    public int getFuelWarning() {
        return -1;
    }


    public void addCarServiceListener(CarServiceListener carServiceListener) {
    }

    public void removeCarServiceListener(CarServiceListener carServiceListener) {
    }
}
