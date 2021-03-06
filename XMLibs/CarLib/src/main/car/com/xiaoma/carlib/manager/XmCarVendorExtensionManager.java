package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarVendorExtensionManager;
import android.car.hardware.vendor.CanAction;
import android.car.hardware.vendor.CanCommon;
import android.car.hardware.vendor.CanOnOff2;
import android.car.hardware.vendor.CanOnOff3;
import android.car.hardware.vendor.CanOnOff4;
import android.car.hardware.vendor.DisplayScreenMode;
import android.car.hardware.vendor.DoorUnlock;
import android.car.hardware.vendor.EpbWorkingState;
import android.car.hardware.vendor.ILLState;
import android.car.hardware.vendor.LDWSensitivity;
import android.car.hardware.vendor.LampLightDalay;
import android.car.hardware.vendor.LdwLkaLcMode;
import android.car.hardware.vendor.RmbLeKey;
import android.car.hardware.vendor.SpeechOnOff2;
import android.car.hardware.vendor.UserId;
import android.car.hardware.vendor.UserIdRecognize;
import android.graphics.Point;
import android.os.IBinder;
import android.util.MutableInt;

import com.xiaoma.carlib.R;
import com.xiaoma.carlib.callback.onGetIntArrayResultListener;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author: iSun
 * @date: 2018/10/22 0022
 */
public class XmCarVendorExtensionManager extends BaseCarManager<CarVendorExtensionManager> implements IVendorExtension {
    private static final String TAG = XmCarVendorExtensionManager.class.getSimpleName();
    private static final String SERVICE_NAME = Car.VENDOR_EXTENSION_SERVICE;
    private static XmCarVendorExtensionManager instance;
    private CountDownLatch errorLatch = new CountDownLatch(1);
    private MutableInt propertyIdReceived = new MutableInt(0);
    private MutableInt areaIdReceived = new MutableInt(0);
    //    private onGetIntArrayResultListener effectsListener;
    private Set<onGetIntArrayResultListener> effectsListener = new HashSet<>();
    private List<ValueChangeListener> valueChangeListeners = new ArrayList<>();
    private DualScreenTabChangeListener mDualScreenTabChangeListener;
    private Set<CarServiceListener> carServiceListeners = new HashSet<>();

    public interface ValueChangeListener {
        void onChange(int id, Object value);
    }

    public interface DualScreenTabChangeListener {

        void onTabChangeFromLib(int value);

        void onLeftTabChangeFromLib(int state);

        void onThemeChangeFromLib(int value);

        void onIgnChange(int value);

    }

    public void setDualScreenTabChangeListener(DualScreenTabChangeListener dualScreenTabChangeListener) {
        KLog.e("dualScreenTabChangeListener=" + dualScreenTabChangeListener);
        this.mDualScreenTabChangeListener = dualScreenTabChangeListener;
    }

    public void removeValueChangeListener(ValueChangeListener valueChangeListener) {
        valueChangeListeners.remove(valueChangeListener);
    }

    public void addValueChangeListener(ValueChangeListener valueChangeListener) {
        valueChangeListeners.add(valueChangeListener);
    }

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

    private CarVendorExtensionManager.CarVendorExtensionCallback callBack = new CarVendorExtensionManager.CarVendorExtensionCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            //Log.d( "CarVendorExtensionCallback onChangeEvent carPropertyValue=" + carPropertyValue);
            changeEvent(carPropertyValue);
        }

        @Override
        public void onErrorEvent(int propertyId, int area) {
            propertyIdReceived.value = propertyId;
            areaIdReceived.value = area;
            errorLatch.countDown();
        }
    };

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

    private void changeEvent(final CarPropertyValue carPropertyValue) {
        //TODO:????????????????????????id??????????????????????????????????????????????????????
        if (carPropertyValue.getPropertyId() == 291505923
                || carPropertyValue.getPropertyId() == 290521862
                || carPropertyValue.getPropertyId() == 291504900
                || carPropertyValue.getPropertyId() == 291504901) {
            return;
        }
        final int propertyId = carPropertyValue.getPropertyId();
        KLog.e("changeEvent key id:" + propertyId + " value:" + carPropertyValue.getValue());
        switch (propertyId){
            case SDKConstants.ENGINE_STATE:
            case SDKConstants.ID_SCREEN_STATUS:
            case SDKConstants.ID_FUEL_WARNING:
            case SDKConstants.ID_CAMERA_STATUS:
                XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
                break;
            case SDKConstants.SOUND_EFFECTS:
                Integer[] list = (Integer[]) carPropertyValue.getValue();
                for (onGetIntArrayResultListener onGetIntArrayResultListener : effectsListener) {
                    onGetIntArrayResultListener.onSoundEffectsGetResult(list);
                }
                break;
            case SDKConstants.ARKAMYS_3D:
                int value = (int) carPropertyValue.getValue();
                for (onGetIntArrayResultListener onGetIntArrayResultListener : effectsListener) {
                    onGetIntArrayResultListener.onArkamys3dEffectsGetResult(value);
                }
                break;
            case SDKConstants.FADER_BALANCE:
                Integer[] lis = (Integer[]) carPropertyValue.getValue();
                for (onGetIntArrayResultListener onGetIntArrayResultListener : effectsListener) {
                    onGetIntArrayResultListener.onSoundEffectPositionGetResult(lis);
                }
                break;
            case SDKConstants.ID_INTERACT_MODE:
                int modeValue = (int) carPropertyValue.getValue();
                if (mDualScreenTabChangeListener != null) {
                    mDualScreenTabChangeListener.onTabChangeFromLib(modeValue);
                } else {  //??????????????????
                    if (modeValue == SDKConstants.VALUE.INACTIVE) {
                        setInteractMode(SDKConstants.VALUE.InteractMode_INACTIVE_REQ);
                    }
                }
                break;
            case SDKConstants.ID_PWR_MODE:
                int pwdMode = (int) carPropertyValue.getValue();
                if (mDualScreenTabChangeListener != null) {
                    mDualScreenTabChangeListener.onIgnChange(pwdMode);
                }
                break;
            case SDKConstants.ID_MEDIA_MENU_REQ:
                break;
            case SDKConstants.ID_NAVI_DISPLAY_IN_IC:
                if (mDualScreenTabChangeListener != null) {
                    mDualScreenTabChangeListener.onLeftTabChangeFromLib((int) carPropertyValue.getValue());
                } else {  //??????????????????
                    int valueDisplay = (int) carPropertyValue.getValue();
                    if (valueDisplay == 0) {
                        setNaviDisplay(SDKConstants.VALUE.CanCommon_OFF);
                    }
                }
                break;
            case SDKConstants.ID_NAVI_FULL_SCREEN_REQ:
                break;
            case SDKConstants.ID_THEME:
                if (mDualScreenTabChangeListener != null) {
                    mDualScreenTabChangeListener.onThemeChangeFromLib((int) carPropertyValue.getValue());
                }
                break;
            case SDKConstants.ID_SPEED_INFO:
                XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
                break;
            case SDKConstants.ID_SHOW_TIME_INFO:
                XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
                break;
                default:
                    if (!CollectionUtil.isListEmpty(valueChangeListeners)) {
                        for (ValueChangeListener valueChangeListener : valueChangeListeners) {
                            valueChangeListener.onChange(carPropertyValue.getPropertyId(), carPropertyValue.getValue());
                        }
                    }
        }
        XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
    }

    private CarEvent carPropertyToXmEvent(CarPropertyValue carPropertyValue) {

        return new CarEvent(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getValue());
    }

    private <E> E getValue(Class<E> propertyClass, int propId) {
        KLog.e(" getValue:" + propId);
        E globalProperty = getDefaultValue(propertyClass);
        if (getManager() != null) {
            try {
                globalProperty = getManager().getGlobalProperty(propertyClass, propId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
        return globalProperty;
    }

    private Integer getIntegerValue(int propId) {
        int value = Integer.MAX_VALUE;
        if (getManager() != null) {
            try {
                value = getManager().getGlobalProperty(Integer.class, propId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
        return value;
    }

    private Integer[] getIntegerArrayValue(int propId) {
        Integer[] value = null;
        if (getManager() != null) {
            try {
                value = getManager().getGlobalProperty(Integer[].class, propId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
        return value;
    }

    private int getIntegerValue(int propId, int area) {
        KLog.e(" getValue:" + propId);
        int value = Integer.MAX_VALUE;
        if (getManager() != null) {
            try {
                value = getManager().getProperty(Integer.class, propId, area);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
        return value;
    }

    private <T> boolean setValue(Class cls, int propId, T value) {
        if (getManager() != null) {
            try {
                getManager().setGlobalProperty(cls, propId, value);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
        return false;
    }

    private void setValue(Class cls, int propId, boolean value) {
        if (getManager() != null) {
            try {
                getManager().setGlobalProperty(cls, propId, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
    }

    private void setIntegerValue(int propId, int value) {
        setValue(Integer.class, propId, value);
    }

    private void setIntegerValue(int propId, int area, int value) {
        if (getManager() != null) {
            try {
                getManager().setProperty(Integer.class, propId, area, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
    }

    private void setIntegerArrayValue(int propId, int area, Integer[] value) {
        if (getManager() != null) {
            try {
                getManager().setProperty(Integer[].class, propId, area, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KLog.e(" manager is null");
        }
    }


    @Override
    public void onCarServiceConnected(IBinder binder) {
        super.onCarServiceConnected();
        initCarEvent();
        for (CarServiceListener listener : carServiceListeners) {
            listener.onCarServiceConnected(binder);
        }
        //???????????? ??????callBack
    }

    @Override
    public void onCarServiceDisconnected() {
        super.onCarServiceDisconnected();
        //????????????
    }

    private void initCarEvent() {
        if (getManager() != null) {
            try {
                List<CarPropertyConfig> properties = getManager().getProperties();
                KLog.e(" getProperties:" + properties.size());
                for (CarPropertyConfig property : properties) {
                    int propertyId = property.getPropertyId();
                    KLog.e(" getPropertyId:" + Integer.toHexString(propertyId));
                    KLog.e(" getPropertyId:" + (propertyId == 0x21400600));
                }

                getManager().registerCallback(callBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    ?????????????????????(BEEP)?????????
    public boolean getBeepSwitch() {
        boolean result = getValue(Boolean.class, SDKConstants.BEEP_SWITCH);
        KLog.e(" getBeepSwitch:" + result);
        return result;
    }

    public void setBeepSwitch(boolean power) {
        KLog.e(" setBeepSwitch:" + power);
        setValue(Boolean.class, SDKConstants.BEEP_SWITCH, power);
    }


    //???????????????????????????????????????????????????
    //?????????????????????????????????????????????????????????
//    The value must be BcmState.LeaveHome#OFF or BcmState.LeaveHome#M1 or BcmState.LeaveHome#M2 or BcmState.LeaveHome#M3
    public int getModeLeaveHome() {
        int result = getValue(Integer.class, SDKConstants.MODE_LEAVE_HOME);
        KLog.e(" getModeLeaveHome:" + result);
        return result;
    }

    public void setModeLeaveHome(int value) {
        KLog.e(" setModeLeaveHome:" + value);
        setValue(Integer.class, SDKConstants.MODE_LEAVE_HOME, value);
    }


   /* //?????????????????????
    public boolean getRearviewMirror() {
        int result = getValue(Integer.class, SDKConstants.MIRROR_AUTOMATIC_FOLDING);
        KLog.e( mContext.getString(R.string.log_car_vendor_extension_get_mirror) + result);
        return result == CanOnOff2.ON;
    }

    public void setRearviewMirror(boolean value) {
        KLog.e( mContext.getString(R.string.log_car_vendor_extension_set_mirror) + value);
        int realValue = value ? CanOnOff1.ON_REQ : CanOnOff1.OFF_REQ;
        setValue(Integer.class, SDKConstants.MIRROR_AUTOMATIC_FOLDING, realValue);
    }*/

    public int getLanguage() {
        int result = getValue(Integer.class, SDKConstants.LANGUAGE);
        return result;
    }

    public void setLanguage(int languageType) {
        KLog.e("???????????? ???type" + languageType);
        setValue(Integer.class, SDKConstants.LANGUAGE, languageType);
    }

    @Override
    public void setInteriorLightSwitch(boolean state) {
        KLog.e("setInteriorLightSwitch:state" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_INSIDE_LIGHT, state ? CanOnOff2.ON_REQ : CanOnOff2.OFF_REQ);
    }

    @Override
    public int getInteriorLightSwitch() {
        int state = getValue(Integer.class, SDKConstants.ID_INTERIOR_LIGHT_SWITCH);
        KLog.e("getInteriorLightSwitch:state" + state);
        return state;
    }


    //???????????????????????????????????????????????????
    //?????????????????????????????????????????????????????????
    //The value must be BcmState.GoHome#OFF or BcmState.GoHome#M1 or BcmState.GoHome#M2 or BcmState.GoHome#M3
    public int getModeGoHome() {
        int result = getValue(Integer.class, SDKConstants.MODE_GO_HOME);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_go_home) + result);
        return result;
    }

    public void setModeGoHome(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_go_home) + value);
        setValue(Integer.class, SDKConstants.MODE_GO_HOME, value);
    }


    //????????????????????????
    public int getAmbientLightSwitch() {
        int result = getValue(Integer.class, SDKConstants.AMBIENT_LIGHT_SWITCH);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_light_switch) + result);
        return result;
    }

    public void setAmbientLightSwitch(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_light_switch) + value);
        setValue(Integer.class, SDKConstants.AMBIENT_LIGHT_SWITCH, value);
    }

    //??????????????????????????????
    public int getAmbientLightBrightness() {
        int result = getValue(Integer.class, SDKConstants.AMBIENT_LIGHT_BRIGHTNESS);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_light_brightness) + result);
        return result;
    }

    public void setAmbientLightBrightness(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_light_brightness) + value);
        // TODO ????????????0-100
        setValue(Integer.class, SDKConstants.AMBIENT_LIGHT_BRIGHTNESS, value);
    }

    //???????????????????????????
    public int getAmbientLightColor() {
        int result = getValue(Integer.class, SDKConstants.MUSIC_SCENE_FOLLOW_COLOR);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_light_color) + result);
        return result;
    }

    public void setAmbientLightColor(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_light_color) + value);
        // TODO ???????????????
        setValue(Integer.class, SDKConstants.MUSIC_SCENE_FOLLOW_COLOR, value);
    }

    //????????????
    public void setLaneChangeFlicker(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_lane_change) + value);
        setValue(Integer.class, SDKConstants.LANE_CHANGE_FLICKER, value);
    }

    public int getLaneChangeFlicker() {
        int result = getValue(Integer.class, SDKConstants.LANE_CHANGE_FLICKER);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_lane_change) + result);
        return result;
    }


    //????????????????????????
    public int getWelcomeLampSwitch() {
        int result = getValue(Integer.class, SDKConstants.WELCOME_LAMP_SWITCH);
        KLog.e(" getWelcomeLampSwitch:" + result);
        return result;
    }

    public void setWelcomeLampSwitch(int value) {
        KLog.e(" setWelcomeLampSwitch:" + value);
        setValue(Integer.class, SDKConstants.WELCOME_LAMP_SWITCH, value);
    }

    //???????????????
    public int getWelcomeLampTime() {
        int result = getValue(Integer.class, SDKConstants.WELCOME_LAMP_TIME);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_welcome_lamp_time) + result);
        return result;
    }

    public void setWelcomeLampTime(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_welcome_lamp_time) + value);
        setValue(Integer.class, SDKConstants.WELCOME_LAMP_TIME, value);
    }


    //?????????????????????????????????
    public boolean getWelcomeSeat() {
        int result = getValue(Integer.class, SDKConstants.WELCOME_SEAT);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_welcome_seat) + result);
        return result == CanOnOff4.ON;
    }

    /**
     * ??????????????????????????????
     */
    public boolean isEPBLocked() {
        Integer state = getIntegerValue(SDKConstants.EPB_WORKING_STATE);
        KLog.e(TAG, " isEPBLocked:" + state);
        return EpbWorkingState.LOCKED == state;
    }

    public void setWelcomeSeat(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_welcome_seat) + value);
        int realValue = value ? CanOnOff4.ON_REQ : CanOnOff4.OFF_REQ;
        setValue(Integer.class, SDKConstants.WELCOME_SEAT, realValue);
    }

    //????????????????????????/????????????????????????
    //????????????????????????????????????/??????????????????
    //The value must be IfcState.Fcw#OFF or IfcState.Fcw#ACTIVE or IfcState.Fcw#STANDBY or IfcState.Fcw#ACTIVE_FCW or IfcState.Fcw#ACTIVE_AEB
    public int getFcwAebSwitch() {
        int result = getValue(Integer.class, SDKConstants.FCW_AEB_SWITCH);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_fcw) + result);
        return result;
    }

    public void setFcwAebSwitch(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_fcw) + value);
        setValue(Integer.class, SDKConstants.FCW_AEB_SWITCH, value);
    }

    //????????????????????????/???????????????????????????????????????????????????
    //The value must be IfcState.FcwSensitivity#LOW or IfcState.FcwSensitivity#NORMAL or IfcState.FcwSensitivity#HIGH
    public int getFcwSensitivity() {
        int result = getValue(Integer.class, SDKConstants.FCW_SENSITIVITY);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_fcw_sensitivity) + result);
        return result;
    }

    public void setFcwSensitivity(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_fcw_sensitivity) + value);
        setValue(Integer.class, SDKConstants.FCW_SENSITIVITY, value);
    }

    @Override
    public void setMarkMirrorLeft(int value) {
        setValue(Integer.class, SDKConstants.VALUE.MIRROR_LEFT_CONFIRM, value);
    }

    @Override
    public void setMarkMirrorRight(int value) {
        setValue(Integer.class, SDKConstants.VALUE.MIRROR_RIGHT_CONFIRM, value);
    }


  /*  //?????????????????????????????????????????????
    //?????????????????????????????????????????????
    public int getRearBeltWorningSwitch() {
        int result = getValue(Integer.class, SDKConstants.REAR_BELT_WORNING_SWITCH);
        KLog.e( mContext.getString(R.string.log_car_vendor_extension_get_worning_switch) + result);
        return result;
    }

    public void setRearBeltWorningSwitch(int value) {
        KLog.e( mContext.getString(R.string.log_car_vendor_extension_set_worning_switch) + value);
        setValue(Integer.class, SDKConstants.REAR_BELT_WORNING_SWITCH, value);
    }*/


    //???????????????LDW???????????????????????????
    //The value must be IfcState.LdwSensitivity#NORMAL or IfcState.LdwSensitivity#HIGH or IfcState.LdwSensitivity#LOW
    public int getLdwSensitivity() {
        int result = getValue(Integer.class, SDKConstants.LDW_SENSITIVITY);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_ldw_sensitivity) + result);
        int realValue = -1;
        switch (result) {
            case LDWSensitivity.LOW:
                realValue = 0;
                break;
            case LDWSensitivity.NORMAL:
                realValue = 1;
                break;
            case LDWSensitivity.HIGH:
                realValue = 2;
                break;

        }
        return realValue;
    }


    public void setLdwSensitivity(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_ldw_sensitivity) + value);
        setValue(Integer.class, SDKConstants.LDW_SENSITIVITY, value);
    }

    private int getLdwValue(int value) {
        int tempValue = -1;
        switch (value) {
            case 0:
                tempValue = LDWSensitivity.LOW_REQ;
                break;
            case 1:
                tempValue = LDWSensitivity.NORMAL_REQ;
                break;
            case 2:
                tempValue = LDWSensitivity.HIGH_REQ;
                break;
        }
        return tempValue;
    }


    //???????????????LKA ??????????????????
    //The value must be IfcState.Lka#OFF or IfcState.Lka#STANDBY or IfcState.Lka#ACTIVE The value TBD.
    public int getLKA() {
        int result = getValue(Integer.class, SDKConstants.LKA_MODE);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_lka) + result);
        int realValue = -1;
        switch (result) {
            case LdwLkaLcMode.LDW:
                realValue = 0;
                break;
            case LdwLkaLcMode.LKA:
                realValue = 1;
                break;
            case LdwLkaLcMode.LC:
                realValue = 2;
                break;
        }
        return realValue;
    }

    public void setLKA(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_lka) + value);
        setValue(Integer.class, SDKConstants.LKA_MODE, getLKAValue(value));
    }

    private int getLKAValue(int value) {
        int realValue = 0;
        switch (value) {
            case 0:
                realValue = LdwLkaLcMode.LDW_REQ;
                break;
            case 1:
                realValue = LdwLkaLcMode.LKA_REQ;
                break;
            case 2:
                realValue = LdwLkaLcMode.LC_REQ;
                break;
        }

        return realValue;
    }

    //???????????????ISA ????????????????????????
    //The value must be IfcState.Isa#OFF or IfcState.Isa#STANDBY or IfcState.Isa#ACTIVE
    public int getISA() {
        int result = getValue(Integer.class, SDKConstants.ISA);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_isa) + result);
        return result;
    }

    public void setISA(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_isa) + value);
        setValue(Integer.class, SDKConstants.ISA, value);
    }

    //???????????????IHC ??????????????????
    //The value must be IfcState.Ihc#OFF or IfcState.Ihc#STANDBY or IfcState.Ihc#ACTIVE
    public int getIHC() {
        int result = getValue(Integer.class, SDKConstants.IHC);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_ihc) + result);
        return result;
    }

    public void setIHC(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_ihc) + value);
        setValue(Integer.class, SDKConstants.IHC, value);
    }

    //???????????????DAW????????????????????????
    // The value must be IfcState.Daw#OFF or IfcState.Daw#STANDBY or IfcState.Daw#ACTIVE The value TBD.
    public int getDAW() {
        int result = getValue(Integer.class, SDKConstants.DAW);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_daw) + result);
        return result;
    }


    public void setDAW(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_daw) + value);
        setValue(Integer.class, SDKConstants.DAW, value);
    }


    //????????????????????????????????????
    //The value must be IfcState.Stt#OFF or IfcState.Stt#STANDBY or IfcState.Stt#ACTIVE
    public int getSTT() {
        int result = getValue(Integer.class, SDKConstants.STT);
        KLog.e(" getSTT:" + result);
        return result;
    }

    public void setSTT(int value) {
        KLog.e(" setSTT:" + value);
        setValue(Integer.class, SDKConstants.STT, value);
    }


    //????????????????????????????????????
    //????????????????????????????????????
    public boolean getWelcomeLightByRhythm() {
        int result = getValue(Integer.class, SDKConstants.WELCOME_LIGHT_BY_RHYTHM);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_selcomelight_byrhythm) + result);
        return SDKConstants.VALUE.EELCOME_LIGHT_MOTION_ON;
    }

    public void setWelcomeLightByRhythm(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_selcomelight_byrhythm) + value);
        setValue(Boolean.class, SDKConstants.WELCOME_LIGHT_BY_RHYTHM, value);
    }

    //???????????????????????????
    //???????????????????????????
    public int getInteriorLightDelay() {
        int result = getValue(Integer.class, SDKConstants.INTERIOR_LIGHT_DELAY);
        int realValue = -1;
        switch (result) {
            case LampLightDalay.DELAY1:
                realValue = 0;
                break;
            case LampLightDalay.DELAY2:
                realValue = 1;
                break;
            case LampLightDalay.DELAY3:
                realValue = 2;
                break;
        }
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_interiorlight_delay) + result);
        return realValue;
    }

    public void setInteriorLightDelay(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_interiorlight_delay) + value);
        int realValue = 0;
        switch (value) {
            case 0:
                realValue = LampLightDalay.DELAY1_REQ;
                break;
            case 1:
                realValue = LampLightDalay.DELAY2_REQ;
                break;
            case 2:
                realValue = LampLightDalay.DELAY3_REQ;
                break;

        }
        setValue(Integer.class, SDKConstants.INTERIOR_LIGHT_DELAY, realValue);
    }

    //?????????????????????????????????
    public boolean getAmbientLightByRhythm() {
        boolean result = getValue(Boolean.class, SDKConstants.AMBIENT_LIGHT_BY_RHYTHM);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_light_by_rhythm) + result);
        return result;
    }

    //?????????????????????????????????
    public void setAmbientLightByRhythm(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_light_by_rhythm) + value);
        setValue(Boolean.class, SDKConstants.AMBIENT_LIGHT_BY_RHYTHM, value);
    }

    //?????????????????????
    public boolean getAutomaticTrunk() {
        int result = getValue(Integer.class, SDKConstants.AUTOMATIC_TRUNK);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_auto_trunk) + result);
        boolean isOpen = false;
        switch (result) {
            case CanOnOff3.ON:
                isOpen = true;
                break;
            case CanOnOff3.OFF:
                isOpen = false;
                break;
        }
        return isOpen;
    }

    public void setAutomaticTrunk(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_auto_trunk) + value);
        int realValue = value ? CanOnOff3.ON_REQ : CanOnOff3.OFF_REQ;
        setValue(Integer.class, SDKConstants.AUTOMATIC_TRUNK, realValue);
    }

    //????????????
    public void setResetTiretPressure(int value) {
        KLog.e(" ????????????:" + value);
        setValue(Integer.class, SDKConstants.RESET_TIRE_PRESSURE, value);
    }

    //????????????
    public int getEPB() {
        int result = getValue(Integer.class, SDKConstants.EPB);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_epb) + result);
        return result;
    }

    public void setEPB(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_epb) + value);
        setValue(Integer.class, SDKConstants.EPB, value);
    }

    //??????????????????
    public void setSelfClosingWindow(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_closing_window) + value);
        int realValue = value ? CanOnOff3.ON_REQ : CanOnOff3.OFF_REQ;
        setValue(Integer.class, SDKConstants.SELF_CLOSING_WINDOW, realValue);
    }

    public boolean getSelfClosingWindow() {
        int result = getValue(Integer.class, SDKConstants.SELF_CLOSING_WINDOW);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_closing_window) + result);
        return result == CanOnOff3.ON;
    }

    /**
     * ??????Arkamys3D?????????????????????????????????????????????
     */
    public void setArkamys3D(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_arkamys3d) + value);
        setIntegerValue(SDKConstants.ARKAMYS_3D, value);
    }

    /**
     * ??????Arkamys3D?????????????????????????????????????????????
     */
    public int getArkamys3D() {
        int result = getIntegerValue(SDKConstants.ARKAMYS_3D);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_arkamys3d) + result);
        return result;
    }

    /**
     * ??????????????????
     *
     * @param mode
     */
    public void setSoundEffectsMode(int mode) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_sound_effects_mode) + mode);
        Integer[] arr = new Integer[6];
        arr[0] = mode;
        arr[1] = 0xff;
        arr[2] = 0xff;
        arr[3] = 0xff;
        arr[4] = 0xff;
        arr[5] = 0xff;
        setValue(arr.getClass(), SDKConstants.SOUND_EFFECTS, arr);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public int getSoundEffectsMode() {
        Integer[] arr = new Integer[6];
        arr = getValue(arr.getClass(), SDKConstants.SOUND_EFFECTS);
        if (arr != null && arr.length >= 1) {
            KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_sound_effects_mode) + arr[0]);
            return arr[0];
        }
        return 0;
    }

    /**
     * ?????????????????????
     *
     * @param arr
     */
    @Override
    public void setCustomSoundEffects(Integer[] arr) {
        setValue(arr.getClass(), SDKConstants.SOUND_EFFECTS, arr);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public List<Integer> getCurrentSoundEffects(int mode) {
        Integer[] arr = new Integer[6];
        arr = getValue(arr.getClass(), SDKConstants.SOUND_EFFECTS);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(arr));
        if (!ListUtils.isEmpty(list)) {
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * ??????????????????
     *
     * @param soundFieldMode
     */
    public void setSoundFieldMode(int soundFieldMode) {
        Integer[] value = new Integer[3];
        Point point = getSoundEffectPositionAtAnyPoint();
        value[0] = point.y;
        value[1] = point.x;
        value[2] = soundFieldMode;
        setValue(value.getClass(), SDKConstants.FADER_BALANCE, value);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_sound_field_mode) + soundFieldMode);
    }

    public void setSoundEffectPositionAtAnyPoint(int x, int y) {
        Integer[] value = new Integer[3];
        value[0] = y;
        value[1] = x;
        value[2] = getSoundFieldMode();
        setValue(value.getClass(), SDKConstants.FADER_BALANCE, value);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_sound_field_position_at_any_point) + "(" + x + "," + y + ")");
    }

    public Point getSoundEffectPositionAtAnyPoint() {
        Integer[] value = new Integer[3];
        value = getValue(value.getClass(), SDKConstants.FADER_BALANCE);
        if (value != null && value.length >= 2) {
            int x = value[1];
            int y = value[0];
            KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_sound_field_position_at_any_point) + "(" + x + "," + y + ")");
            return new Point(x, y);
        } else {
            return new Point(0, 0);
        }
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public int getSoundFieldMode() {
        Integer[] value = new Integer[3];
        value = getValue(value.getClass(), SDKConstants.FADER_BALANCE);
        if (value != null && value.length >= 3) {
            KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_sound_field_mode) + value[2]);
            return value[2];
        } else {
            return 0;
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param opened
     */
    public void setOnOffMusic(boolean opened) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_on_off_music) + opened);
        setValue(Boolean.class, SDKConstants.BOOT_MUSIC, opened);
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    public boolean getOnOffMusic() {
        boolean result = getValue(Boolean.class, SDKConstants.BOOT_MUSIC);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_on_off_music) + result);
        return result;
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param level
     */
    public void setCarInfoSound(int level) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_car_info_sound) + level);
        setIntegerValue(SDKConstants.VEHICLE_INFOMATION_LEVEL, level);
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @return
     */
    public int getCarInfoSound() {
        int result = getIntegerValue(SDKConstants.VEHICLE_INFOMATION_LEVEL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_car_info_sound) + result);
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param volume
     */
    public void setCarSpeedVolumeCompensate(int volume) {
        setIntegerValue(SDKConstants.ALC_LEVEL, volume);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_car_speed_volume_compensate) + volume);
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public int getCarSpeedVolumeCompensate() {
        int result = getIntegerValue(SDKConstants.ALC_LEVEL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_car_speed_volume_compensate) + result);
        return result;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param volume
     */
    public void setParkMediaVolume(int volume) {
        setIntegerValue(SDKConstants.PARKING_MEDIA_LEVEL, volume);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_park_media_volume) + volume);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @return
     */
    public int getParkMediaVolume() {
        int result = getIntegerValue(SDKConstants.PARKING_MEDIA_LEVEL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_park_media_volume) + result);
        return result;
    }

    /*??????????????????*/
    //????????????????????????
    public int getDisplayMode() {
        int result = getValue(Integer.class, SDKConstants.ID_DISPLAY_MODE);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_display_mode) + result);
        return result;
    }

    //????????????????????????
    public void setDisplayMode(int mode) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_display_mode) + mode);
        setValue(Integer.class, SDKConstants.ID_DISPLAY_MODE, mode);
    }

    //??????????????????????????????-??????
    public int getDayDisplayLevel() {
        int result = getValue(Integer.class, SDKConstants.ID_DAY_DISPLAY_LEVEL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_day_display_level) + result);
        return result;
    }

    //??????????????????????????????-??????
    public int getNightDisplayLevel() {
        int result = getValue(Integer.class, SDKConstants.ID_NIGHT_DISPLAY_LEVEL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_night_display_level) + result);
        return result;
    }

    //????????????????????????-??????
    public void setDayDisplayLevel(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_day_display_level) + value);
        setValue(Integer.class, SDKConstants.ID_DAY_DISPLAY_LEVEL, value);
    }

    //??????????????????????????????-??????
    public int getAutoDisplayLevel() {
        int level;
        int status = getIllStatus();
        if (status == ILLState.ON) {  //??????
            level = getNightDisplayLevel();
        } else {   //??????
            level = getDayDisplayLevel();
        }
        return level;
    }

    //????????????????????????-??????
    public void setAutoDisplayLevel(int value) {
        int status = getIllStatus();
        if (status == ILLState.OFF) {//??????
            setDayDisplayLevel(value);
        } else {   //??????
            setNightDisplayLevel(value);
        }
    }


    //????????????????????????-??????
    public void setNightDisplayLevel(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_night_display_level) + value);
        setValue(Integer.class, SDKConstants.ID_NIGHT_DISPLAY_LEVEL, value);
    }

    public int getDisplayLevel() {
        int level = 0;
        int mode = getDisplayMode();
        if (mode == DisplayScreenMode.AUTO) {
            level = getAutoDisplayLevel();
        } else if (mode == DisplayScreenMode.DAY) {
            level = getDayDisplayLevel();
        } else if (mode == DisplayScreenMode.NIGHT) {
            level = getNightDisplayLevel();
        }
        return level;
    }

    public void setDisplayLevel(int value) {
        int mode = getDisplayMode();
        if (mode == DisplayScreenMode.AUTO) {
            setAutoDisplayLevel(value);
        } else if (mode == DisplayScreenMode.DAY) {
            setDayDisplayLevel(value);
        } else if (mode == DisplayScreenMode.NIGHT) {
            setNightDisplayLevel(value);
        }
    }


    //??????????????????
    public int getKeyBoardLevel() {
        int result = getValue(Integer.class, SDKConstants.ID_KEYBOARD_LEVEL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_key_board_level) + result);
        return result;
    }

    //??????????????????
    public void setKeyBoardLevel(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_key_board_level) + value);
        setValue(Integer.class, SDKConstants.ID_KEYBOARD_LEVEL, value);
    }

    //??????????????????
    public boolean getBanVideoStatus() {
        boolean result = getValue(Boolean.class, SDKConstants.ID_BAN_VIDEO);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_ban_video_status) + result);
        return result;
    }

    //??????????????????
    public void setBanVideoStatus(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_ban_video_status) + value);
        setValue(Boolean.class, SDKConstants.ID_BAN_VIDEO, value);
    }

    //??????????????????
    public int getTheme() {
        int result = getValue(Integer.class, SDKConstants.ID_THEME);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_theme) + result);
        return result;
    }

    //??????????????????
    public boolean setTheme(int value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_theme) + value);
        return setValue(Integer.class, SDKConstants.ID_THEME, value);
    }

    //??????????????????
    @Override
    public boolean getScreenStatus() {
        boolean result = getValue(Boolean.class, SDKConstants.ID_SCREEN_STATUS);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_screen_status) + result);
        return result;
    }

    //????????????
    @Override
    public void closeScreen() {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_close_screen));
        setValue(Boolean.class, SDKConstants.ID_SCREEN_STATUS, SDKConstants.VALUE.SCREEN_OFF);
    }

    @Override
    public void turnOnScreen() {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_open_screen));
        setValue(Boolean.class, SDKConstants.ID_SCREEN_STATUS, SDKConstants.VALUE.SCREEN_ON);
    }

    public boolean getSpeedAutoLock() {
        int result = getValue(Integer.class, SDKConstants.SPEED_AUTOMATIC_LOCK);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_speed_auto_lock) + result);
        return result == CanOnOff2.ON;
    }

    // ????????????
    public void setSpeedAutoLock(boolean value) {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_speed_auto_lock) + value);
        setValue(Integer.class, SDKConstants.SPEED_AUTOMATIC_LOCK, value ? CanOnOff2.ON_REQ : CanOnOff2.OFF_REQ);
    }

    //??????????????????
    public void setLeaveAutoLock(boolean value) {
        int realValue = value ? CanOnOff4.ON_REQ : CanOnOff4.OFF_REQ;
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_leave_auto_lock) + realValue);
        setValue(Integer.class, SDKConstants.LEAVE_CAR_AUTOMATIC_LOCK, realValue);
    }

    public boolean getLeaveAutoLock() {
        int result = getValue(Integer.class, SDKConstants.LEAVE_CAR_AUTOMATIC_LOCK);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_leave_auto_lock) + result);
        return result == CanOnOff4.ON;
    }

    /*//?????????????????? REMOTE_CONTROL_UNLOCK_MODE
    public void setRemoteControlUnlockMode(int value) {
        KLog.e( mContext.getString(R.string.log_car_vendor_extension_set_remote_control_mode) + value);
        setValue(Integer.class, SDKConstants.REMOTE_CONTROL_UNLOCK_MODE, getRemoteControlUnlockValue(value));
    }*/

    private int getRemoteControlUnlockValue(int value) {
        int realValue = 0;
        switch (value) {
            case 0:
                realValue = DoorUnlock.ALL_DOOR_REQ;
                break;
            case 1:
                realValue = DoorUnlock.DRIVER_DOOR_REQ;
                break;
        }
        return realValue;
    }

    /*public int getRemoteControlUnlockMode() {
        int result = getValue(Integer.class, SDKConstants.REMOTE_CONTROL_UNLOCK_MODE);
        int realValue = -1;
        switch (result) {
            case DoorUnlock.ALL_DOOR:
                realValue = 0;
                break;
            case DoorUnlock.DRIVER_DOOR:
                realValue = 1;
                break;
        }
        KLog.e( mContext.getString(R.string.log_car_vendor_extension_get_remote_control_mode) + result);
        return realValue;
    }*/

    //?????????????????? MUSIC_SCENE_FOLLOW
    public void setMusicSceneFollow(boolean value) {
        int realValue = value ? CanCommon.ON : CanCommon.OFF;
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_set_music_follow) + realValue);
        setValue(Integer.class, SDKConstants.MUSIC_SCENE_FOLLOW, realValue);
    }

    public boolean getMusicSceneFollow() {
        int result = getValue(Integer.class, SDKConstants.MUSIC_SCENE_FOLLOW);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_music_follow) + result);
        boolean realResult = false;
        switch (result) {
            case CanCommon.ON:
                realResult = true;
                break;
            case CanCommon.OFF:
                realResult = false;
                break;
        }
        return realResult;
    }

    //?????????????????????
    public int getEngineState() {
        int result = getValue(Integer.class, SDKConstants.ENGINE_STATE);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_engine_state) + result);
        return result;
    }

    //??????vin?????????
    public String getVinInfo() {
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_vin_info));
        return getValue(String.class, SDKConstants.ID_VIN_INFO);
    }

    //??????????????????
    public int getIllStatus() {
        int status = getValue(Integer.class, SDKConstants.ID_ILL_STATUS);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_ill_status) + status);
        if (status != ILLState.OFF && status != ILLState.ON) {
            status = ILLState.OFF;
        }
        return status;
    }

    //??????????????????
    public int getSrMode() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_SPEECH_RECOGNITION_MODE);
        return result;
    }

    //??????????????????
    public void setSrMode(int value) {
        setValue(Integer.class, CarVendorExtensionManager.ID_SPEECH_RECOGNITION_MODE, value);
    }

    /*????????????*/
    //????????????????????????tab(????????????)
    public void setInteractModeReq(int mode) {
        KLog.e("setInteractModeReq mode=" + mode);
        setValue(Integer.class, CarVendorExtensionManager.ID_HU_INTERFACE_REQ, mode);
    }

    //????????????????????????tab(?????????????????????????????????????????????)
    public void setInteractMode(int mode) {
        KLog.e("setInteractMode mode = " + mode);
        setValue(Integer.class, CarVendorExtensionManager.ID_INTERACT_MODE, mode);
    }

    //??????????????????tab??????????????????tab???????????????,?????????????????????????????????
    public int getInteractMode() {
        KLog.e("getInteractMode");
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_INTERACT_MODE);
        return result;
    }

    //????????????????????????????????????
    public int getNaviDisplayInICMode() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_NAVI_DISPLAY_IN_IC);
        if(result < 0 ){
            //????????????????????????default???-1????????????-1????????????????????????????????????
            return 0;
        }
        KLog.e("getNaviDisplayInICMode  result " +result);
        return result;
    }

    public void setMediaMenuLevel(int level) {
        KLog.e("setMediaMenuLevel,level=" + level);
        setValue(Integer.class, CarVendorExtensionManager.ID_MEDIA_MENU_REQ, level);
    }

    public void setNaviDisplay(int value) {
        KLog.e("???????????????????????? setNaviDisplay;value=" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_NAVI_DISPLAY_REQ, value);
    }

    public void setSimpleMenuDisplay(int value) {
        KLog.e("setSimpleMenuDisplay;value=" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_IC_MENU_DISPLAY_REQ, value);
    }

    @Override
    public Integer[] getShowTime() {
        return getIntegerArrayValue(SDKConstants.ID_SHOW_TIME_INFO);
    }

    //???????????????????????????????????????????????????????????????????????????????????????????????????
    public void setMultifuncSwitch(int value) {
        setValue(Integer.class, CarVendorExtensionManager.ID_MULTIFUNC_SWITCH, value);
    }

    //??????????????????,CanCommon_ON,CanCommon_OFF
    public void setIcMenuDisplayReq(int value) {
        KLog.d("setIcMenuDisplayReq");
        setValue(Integer.class, CarVendorExtensionManager.ID_IC_MENU_DISPLAY_REQ, value);
    }

    //??????????????????????????????????????????
    public void getCarModel() {
        getValue(Integer.class, CarVendorExtensionManager.ID_CAR_MODEL);
    }

    //?????????????????? (int L/100Km,????????????*0.01)
    public int getFuelConsumption() {
        int result = getValue(Integer.class, SDKConstants.ID_LONG_TIME_FUEL_CONSUMPTION);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_fuel_consumption) + result);
        return result;
    }

    //??????????????????
    public int getOdometerResidual() {
        int result = getValue(Integer.class, SDKConstants.ID_ODOMETER_RESIDUAL);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_odometer_residual) + result);
        return result;
    }

    @Override
    public void setFarAutoLock(boolean value) {
        KLog.e("????????????????????????: " + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_FAR_DOOR_AUTO_LOCK_MODE, value ? CanOnOff4.ON_REQ : CanOnOff4.OFF_REQ);
    }

    @Override
    public int getFarAutoLock() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_FAR_DOOR_AUTO_LOCK_MODE);
        KLog.e("????????????????????????: " + result);
        return result;
    }

    @Override
    public void setApproachAutoUnlock(boolean value) {
        KLog.e("????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_NEAR_DOOR_AUTO_UNLOCK_MODE, value ? CanOnOff4.ON_REQ : CanOnOff4.OFF_REQ);
    }

    @Override
    public int getApproachAutoUnlock() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_NEAR_DOOR_AUTO_UNLOCK_MODE);
        KLog.e("????????????????????????:" + result);
        return result;
    }

    /**
     * ????????????????????????
     *
     * @return value ????????????{@link CanOnOff2#OFF,CanOnOff2#ON}
     */
    @Override
    public int getTiredState() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_TIRED_REMIND_STATE);
        KLog.e("????????????????????????:" + result);
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param value {@link CanOnOff2#OFF_REQ,CanOnOff2#ON_REQ}
     */
    @Override
    public void setTiredState(int value) {
        KLog.e("????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_TIRED_REMIND_STATE, value);
    }

    /**
     * ????????????????????????
     *
     * @return ?????????????????? {@link CanOnOff2#ON,CanOnOff2#OFF}
     */
    @Override
    public int getDistractionState() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_DISTRACTION_REMIND_STATE);
        KLog.e("????????????????????????" + result);
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param value ?????????????????? {@link CanOnOff2#ON_REQ,CanOnOff2#OFF_REQ}
     */
    @Override
    public void setDistractionState(int value) {
        KLog.e("????????????????????????" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_DISTRACTION_REMIND_STATE, value);
    }

    /**
     * ????????????????????????????????????
     *
     * @return value ????????????{@link CanOnOff2#OFF,CanOnOff2#ON}
     */
    @Override
    public int getBadDriverState() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_BAD_DRIVING_STATE);
        KLog.e("????????????????????????????????????:" + result);
        return result;
    }

    //

    /**
     * ????????????????????????????????????
     *
     * @param value {@link CanOnOff2#OFF_REQ,CanOnOff2#ON_REQ}
     */
    @Override
    public void setBadDriverState(int value) {
        KLog.e("????????????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_BAD_DRIVING_STATE, value);
    }

    /**
     * ???????????????????????????
     *
     * @return value ????????????{@link CanOnOff2}
     */
    @Override
    public int getRecognizeAvailable() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_DMS_STATE);
        KLog.e("???????????????????????????:" + result);
        return result;
    }

    /**
     * ??????????????????
     *
     * @param value ??????{@link CanOnOff2}
     */
    @Override
    public void setRecognizeAvailable(int value) {
        KLog.e("??????????????????????????????: " + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_DMS_STATE, value);
    }

    /**
     * ????????????????????????,????????????????????????
     *
     * @return value ????????????{@link UserIdRecognize}
     */
    public int getRecognizeState() {
        int result = getValue(Integer.class, CarVendorExtensionManager.ID_RECOGNIZE_STATE);
        KLog.e("????????????????????????:" + result);
        return result;
    }

    /**
     * ??????????????????/??????????????????
     * <p>
     * ?????????????????????{@link CarVendorExtensionManager#ID_DMS_KEY_NUM}?????????????????????UserId
     *
     * @param value ????????????{@link UserIdRecognize}
     */
    @Override
    public void setRecognize(int value) {
        KLog.e("??????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_USER_ID_RECOG, value);
    }

    //=============================?????????????????????????????????????????????????????????????????????????????????dms????????????????????????????????????dms?????????????????????????????????????????????????????????===============================

    public void openDms(int value) {
        KLog.e("??????dms ????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_DMS_STATE, value);
    }

    public void openDistraction(int value) {
        KLog.e("?????????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_DISTRACTION_REMIND_STATE, value);
    }

    public void openTired(int value) {
        KLog.e("??????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_TIRED_REMIND_STATE, value);
    }

    public void openBadDrive(int value) {
        KLog.e("????????????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_BAD_DRIVING_STATE, value);
    }

    public void openUserId(int value) {
        KLog.e("??????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_USER_ID_STATE, value);
    }

    public void openUserFeeling(int value) {
        KLog.e("????????????????????????????????????:" + value);
        setValue(Integer.class, CarVendorExtensionManager.ID_USER_FEELING_STATE, value);
    }

    //==================================================================================

    /**
     * ??????????????????
     * <p>
     * ?????????????????????{@link CarVendorExtensionManager#ID_DMS_KEY_NUM}?????????????????????UserId
     *
     * @return value ????????????{@link UserId}
     */
    @Override
    public void startFaceRecord(int userid) {
        KLog.e("??????????????????:" + userid);
        setValue(Integer.class, CarVendorExtensionManager.ID_USER_ID_ADD_REQ, userid);
    }

    /**
     * ??????????????????
     * ?????????{@link CanAction#ACTION_REQ}
     */
    @Override
    public void cancelFaceRecord() {
        KLog.e("??????????????????:" + CanAction.ACTION_REQ);
        setValue(Integer.class, CarVendorExtensionManager.ID_CANCEL_REGISTER_USER_ID, CanAction.ACTION_REQ);
    }

    /**
     * ?????????????????????
     * <p>
     * <p>
     * ???????????????
     *
     * @param faceId ?????????carlib????????????????????????id {@link android.car.hardware.vendor.UserIdAction}
     * @return value ????????????{@link UserId}
     */
    @Override
    public void delFaceRecord(int faceId) {
        KLog.e("?????????????????????:" + faceId);
        setValue(Integer.class, CarVendorExtensionManager.ID_USER_ID_DEL_REQ, faceId);
    }

    /**
     * TODO:???????????????
     * ??????????????????id
     * @return value ????????????{@link UserId}
     */
   /* public int Compare(int userid){
        KLog.e( "?????????????????????" + userid);
        setValue(Integer.class, CarVendorExtensionManager.ID_DMS_KEY_NUM,userid);
    }*/

    /**
     * ??????????????????
     *
     * @return ??????ID {@link RmbLeKey}
     */
    @Override
    public int getCarKey() {
        Integer key = getIntegerValue(CarVendorExtensionManager.ID_RMB_LE_KEY);
        KLog.e("??????????????????:" + key);
        return key;
    }

    @Override
    public int getCarSpeed() {
        int value = getValue(Integer.class, CarVendorExtensionManager.ID_SPEED_INFO);
        return value / 100;
    }

    @Override
    public int getTestValue(int canstant) {
        int value = getValue(Integer.class, canstant);
        return value;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public List<Integer> getRobVersion() {
        Integer[] arr = new Integer[4];
        arr = getValue(arr.getClass(), SDKConstants.ID_ROB_VERSION);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(arr));
        if (!ListUtils.isEmpty(list)) {
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    public int getRobAction(){
        Integer[] data=getIntegerArrayValue(SDKConstants.ID_ROB_ACTION_MODE);
        if(data!=null && data.length>2){
            return data[1];
        }
        return 30;
    }

    public void setRobAction(int actionId) {
        setRobAction(actionId, 0);
    }

    /**
     * ??????????????????????????????
     *
     * @param actionId ??????id
     * @param duration ????????????(??????: ?????????????????????,?????????????????????,??????0-126,??????:???; ???127???????????????????????????)
     */
    public void setRobAction(int actionId, int duration) {
        KLog.d(String.format("??????????????????????????????: actionId: %s, duration: %s", actionId, duration));
        int roleId = HologramRepo.getUsingRoleId(mContext);
        // ??????D077?????????:
        // ???????????????????????????????????????????????????,??????1???????????????????????????.
        // ?????????????????????????????????: ???????????????ID,???????????????,??????????????????????????????ID,???????????????.
        // ??????????????????????????????0??????,???????????????????????????ID,??????????????????.
        setValue(Integer[].class, SDKConstants.ID_ROB_ACTION_MODE, new Integer[]{roleId, 0, 0});
        // ????????????,????????????: [0,127]
        duration = Math.min(Math.max(duration, 0), 127);
        // ????????????
        setValue(Integer[].class, SDKConstants.ID_ROB_ACTION_MODE, new Integer[]{roleId, actionId, duration});
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public void setRobBrightnessIncrease() {
        KLog.d("???????????????????????? ");
        setValue(Integer.class, SDKConstants.ID_ROB_BRIGHTNESS, SDKConstants.VALUE.INCREASE_REQ);
    }


    /**
     * ????????????????????????
     */
    public void setRobBrightnessDecrease() {
        KLog.d("???????????????????????? ");
        setValue(Integer.class, SDKConstants.ID_ROB_BRIGHTNESS, SDKConstants.VALUE.DECREASE_REQ);
    }


    /**
     * ??????????????????
     *
     * @return
     */
    public int getRobBrightness() {
        int result = getValue(Integer.class, SDKConstants.ID_ROB_BRIGHTNESS);
        return result;
    }

    public void setRobClothMode(int clothId) {
        setRobClothMode(HologramRepo.getUsingRoleId(mContext), clothId);
    }

    /**
     * ??????????????????????????????
     */
    public void setRobClothMode(int roleId, int clothId) {
        setValue(Integer[].class,
                CarVendorExtensionManager.ID_ROB_FIGURE_CLOTH_MODE,
                new Integer[]{roleId, clothId});
        // ?????????????????????,??????ID????????????,???????????????????????????
        HologramRepo.putUsingRoleId(mContext, roleId);
        HologramRepo.putUsingClothId(mContext, roleId, clothId);
    }

    /**
     * ??????????????????????????????
     *
     * @param roleId ????????????ID
     */
    public void setRobCharacterMode(int roleId) {
        int clothId = HologramRepo.getUsingClothId(mContext, roleId);
        setValue(Integer[].class,
                CarVendorExtensionManager.ID_ROB_FIGURE_CLOTH_MODE,
                new Integer[]{roleId, clothId});
        HologramRepo.putUsingRoleId(mContext, roleId);

    }

    @Override
    public void setHoloId(int holoId) {
        HologramRepo.putUsingHoloId(mContext, holoId);
    }

    /**
     * ??????????????????????????????
     * ??????????????? ??????
     * RobotMode.OFF,
     * RobotMode.ON,
     * RobotMode.SLEEP,
     * RobotMode.INACTIVE_REQ,
     * RobotMode.ON_REQ,
     * RobotMode.OFF_REQ,
     * RobotMode.SLEEP_REQ,
     *
     * @return
     */
    public void setRobSwitcher(int value) {
        setValue(Integer.class, SDKConstants.ID_ROB_SWITCH, value);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public void setRobFiceDir(int value) {
        setValue(Integer.class, SDKConstants.ID_ROB_FACE_DIR, value);
    }

    /**
     * ??????/??????????????????????????????
     *
     * @param value
     */
    @Override
    public void setSynchronizeTimeSwitch(boolean value) {
        KLog.e(mContext.getString(R.string.set_sychronize_time_switch) + value);
        setValue(Boolean.class, SDKConstants.ID_SYNC_TIME_ON, value);
    }

    public Boolean getSynchronizeTimeSwitch() {
        return getValue(Boolean.class, SDKConstants.ID_SYNC_TIME_ON);
    }

    //??????/????????????????????????
    public void setWindowFortRearHeat(boolean state) {
        setValue(Integer.class, SDKConstants.ID_SPEECH_DEF, state ? SpeechOnOff2.ON_REQ : SpeechOnOff2.OFF_REQ);
    }

    @Override
    public Integer[] getConfigInfo() {
        Integer[] arrayValue = getIntegerArrayValue(SDKConstants.ID_CONFIG);
        String msg = "";
        if (arrayValue != null && arrayValue.length != 0) {
            for (int i = 0; i < arrayValue.length; i++) {
                msg += "[ " + i + ", " + arrayValue[i] + "], ";
            }
            KLog.e("???????????????????????? " + msg);
        } else {
            KLog.e("????????????????????????:????????? ");
        }
        return arrayValue;
    }

    /**
     * ?????????????????????????????????
     */
    @Override
    public void setSubwoofer(boolean open) {
        KLog.e(mContext.getString(R.string.set_subwoofer) + open);
        setValue(Boolean.class, SDKConstants.ID_ARKAMYS_BASS_BOOST_SWITCH, open);
    }

    /**
     * ?????????????????????????????????
     */
    @Override
    public boolean getSubwoofer() {
        boolean open = getValue(Boolean.class, SDKConstants.ID_ARKAMYS_BASS_BOOST_SWITCH);
        KLog.e(mContext.getString(R.string.get_subwoofer) + open);
        return open;
    }

    @Override
    public void onAvsSwitch() {
        setValue(Integer.class, SDKConstants.ID_AVS_SWITCH, SDKConstants.VALUE.WELCOM_LIGHT_ON_REQ);
    }

    @Override
    public void closeAvs() {
        setValue(Integer.class, SDKConstants.ID_AVS_SWITCH, SDKConstants.VALUE.WELCOM_LIGHT_OFF_REQ);
    }

    @Override
    public boolean getCameraStatus() {
        boolean result = getValue(Boolean.class, SDKConstants.ID_CAMERA_STATUS);
        KLog.e(" getCameraStatus:" + result);
        return result;
    }

    @Override
    public int getSensitivityLevel() {
        int result = getValue(Integer.class, SDKConstants.ID_TIRED_SENSITIVE);
        KLog.e(" ???????????????????????????:" + result);
        return result;
    }

    @Override
    public void setSensivityLevel(int level) {
        setValue(Integer.class, SDKConstants.ID_TIRED_SENSITIVE, level);
        KLog.e(" ???????????????????????????:" + level);
    }

    @Override
    public int getOdometer() {
        int result = getValue(Integer.class, SDKConstants.ID_ODOMETER);
        KLog.e(" ???????????????:" + result);
        return result;
    }

    public void setRestoreCmd(int value) {
        KLog.e(" setRestoreCmd:" + value);
        setValue(Integer.class, SDKConstants.ID_RESTORE_CMD, value);
    }

    @Override
    public void setTime(long time) {
        Integer[] params = getTimeParams(time);
        setValue(Integer[].class, SDKConstants.ID_TIME_INFO, params);
    }

    private Integer[] getTimeParams(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int yearH = year >> 8;
        int yearL = year & 0xff;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        Integer[] params = new Integer[7];
        params[0] = yearH;
        params[1] = yearL;
        params[2] = month;
        params[3] = day - 1;
        params[4] = hour;
        params[5] = minutes;
        params[6] = seconds;
        String paramsStr = "";
        for (int a : params) {
            paramsStr += a;
            paramsStr += ", ";
        }
        KLog.d("car_control_req", "????????????????????????: " + paramsStr);
        return params;
    }

    @Override
    public void setRearviewMirror(int value) {
        setValue(Integer.class, SDKConstants.ID_INTELLIGENT_MIRROR_SWITCH, value);
    }

    @Override
    public int getRearViewMirrorEnable() {
        return getValue(Integer.class, SDKConstants.ID_INTELLIGENT_MIRROR_SWITCH);
    }

    //????????????????????????
    public int getFuelWarning() {
        int result = getValue(Integer.class, SDKConstants.ID_FUEL_WARNING);
        KLog.e(mContext.getString(R.string.log_car_vendor_extension_get_fuel_consumption) + result);
        return result;
    }

    public void addCarServiceListener(CarServiceListener carServiceListener) {
        if(carServiceListener != null && carServiceListeners.contains(carServiceListener))
            carServiceListeners.add(carServiceListener);
    }

    public void removeCarServiceListener(CarServiceListener carServiceListener) {
        if(carServiceListener != null && carServiceListeners.contains(carServiceListener))
            carServiceListeners.remove(carServiceListener);
    }
}
