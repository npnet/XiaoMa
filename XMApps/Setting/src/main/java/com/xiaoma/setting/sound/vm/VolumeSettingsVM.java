package com.xiaoma.setting.sound.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarAudio;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.DebugUtils;
import com.xiaoma.setting.sound.ui.VolumeSettingsFragment;

/**
 * Created by qiuboxiang on 2018/10/13 11:10
 * description:
 */
public class VolumeSettingsVM extends AndroidViewModel implements XmCarVendorExtensionManager.ValueChangeListener {

    private static final String TAG = "VolumeSettingsVM";
    private MutableLiveData<Boolean> mOnOffMusic;
    private MutableLiveData<Integer> mCarInfoSound;
    private MutableLiveData<Integer> mCarSpeedVolumeCompensate;
    private MutableLiveData<Integer> mParkMediaVolume;
    private MutableLiveData<Integer> mMediaVolume;
    private MutableLiveData<Integer> mBlueToothMusicVolume;
    private MutableLiveData<Integer> mCallVolume;
    private MutableLiveData<Integer> mSpeechVolume;
    private final int DEFAULT_VALUE = Integer.MAX_VALUE;
    private final int MIN_MEDIA_VOLUME = 0;
    private final int MIN_BLUETOOTH_MUSIC_VOLUME = 0;
    private final int MIN_CALL_VOLUME = VolumeSettingsFragment.CALL_MIN_VOLUME;
    private final int MIN_SPEECH_VOLUME = 0;
    private Context mContext;
    private OnChangeEvent event;

    public VolumeSettingsVM(@NonNull Application application) {
        super(application);
        mContext = application;
        getSDKManager().addValueChangeListener(this);
    }

    public boolean setOnOffMusic(boolean opened) {
        getSDKManager().setOnOffMusic(opened);
        getOnOffMusic().setValue(true);
        DebugUtils.e(TAG, mContext.getString(R.string.on_off_music) + ": " + opened);
        return true;
    }

    public boolean setCarInfoSound(int index) {
        int level = caseIndexToVehicleHintsLevel(index);
        getSDKManager().setCarInfoSound(level);
        getCarInfoSound().setValue(index);
        DebugUtils.e(TAG, mContext.getString(R.string.car_info_sound) + ": " + index);
        return true;
    }

    public boolean setCarSpeedVolumeCompensate(int index) {
        int volume = caseIndexToCarSpeedVolume(index);
        getSDKManager().setCarSpeedVolumeCompensate(volume);
        getCarSpeedVolumeCompensate().setValue(index);
        DebugUtils.e(TAG, mContext.getString(R.string.car_speed_volume_compensate) + ": " + index);
        return true;
    }

    public boolean setParkMediaVolume(int index) {
        int volume = caseIndexToParkMediaVolume(index);
        getSDKManager().setParkMediaVolume(volume);
        getParkMediaVolume().setValue(index);
        DebugUtils.e(TAG, mContext.getString(R.string.park_media_volume) + ": " + index);
        return true;
    }

    public boolean setMediaVolume(int volume) {
        getAudioManager().setStreamVolume(SDKConstants.MEDIA_VOLUME, volume);
        getMediaVolume().setValue(volume);
        DebugUtils.e(TAG, mContext.getString(R.string.media_volume) + ": " + volume);
        return true;
    }

    public boolean setBlueToothMusicVolume(int volume) {
        getAudioManager().setStreamVolume(SDKConstants.BT_MEDIA_VOLUME, volume);
        getBlueToothMusicVolume().setValue(volume);
        DebugUtils.e(TAG, mContext.getString(R.string.bluetooth_music) + ": " + volume);
        return true;
    }

    public boolean setCallVolume(int volume) {
        getAudioManager().setStreamVolume(SDKConstants.PHONE_VOLUME, volume);
        getCallVolume().setValue(volume);
        DebugUtils.e(TAG, mContext.getString(R.string.call_volume) + ": " + volume);
        return true;
    }

    public boolean setSpeechVolume(int volume) {
        getAudioManager().setStreamVolume(SDKConstants.TTS_VOLUME, volume);
        getSpeechVolume().setValue(volume);
        DebugUtils.e(TAG, mContext.getString(R.string.speech_volume) + ": " + volume);
        return true;
    }

    public MutableLiveData<Boolean> getOnOffMusic() {
        if (mOnOffMusic == null) {
            mOnOffMusic = new MutableLiveData<>();
            mOnOffMusic.setValue(getSDKManager().getOnOffMusic());
        }
        return mOnOffMusic;
    }

    public MutableLiveData<Integer> getCarInfoSound() {
        if (mCarInfoSound == null) {
            mCarInfoSound = new MutableLiveData<>();
            mCarInfoSound.setValue(caseVehicleHintsLevelToIndex(getSDKManager().getCarInfoSound()));
        }
        return mCarInfoSound;
    }

    public MutableLiveData<Integer> getCarSpeedVolumeCompensate() {
        if (mCarSpeedVolumeCompensate == null) {
            mCarSpeedVolumeCompensate = new MutableLiveData<>();
            mCarSpeedVolumeCompensate.setValue(caseCarSpeedVolumeToIndex(getSDKManager().getCarSpeedVolumeCompensate()));
        }
        return mCarSpeedVolumeCompensate;
    }

    public MutableLiveData<Integer> getParkMediaVolume() {
        if (mParkMediaVolume == null) {
            mParkMediaVolume = new MutableLiveData<>();
            mParkMediaVolume.setValue(caseParkMediaVolumeToIndex(getSDKManager().getParkMediaVolume()));
        }
        return mParkMediaVolume;
    }

    /**
     * 转换泊车媒体音量等级 静音/弱化/正常
     * @param value
     * @return
     */
    public int caseParkingMediaLevelToIndex(int value) {
        switch (value) {
            case SDKConstants.VALUE.PARKING_MEDIA_MUTE:
                value = 0;
                break;
            case SDKConstants.VALUE.PARKING_MEDIA_REDUCE:
                value = 1;
                break;
            case SDKConstants.VALUE.PARKING_MEDIA_NORMAL:
                value = 2;
                break;
        }
        return value;
    }

    public int getStreamVolume(int streamType) {
        int volume = getAudioManager().getStreamVolume(streamType);
        if (volume == DEFAULT_VALUE) {
            switch (streamType) {
                case SDKConstants.MEDIA_VOLUME:
                    volume = MIN_MEDIA_VOLUME;
                    break;
                case SDKConstants.BT_MEDIA_VOLUME:
                    volume = MIN_BLUETOOTH_MUSIC_VOLUME;
                    break;
                case SDKConstants.PHONE_VOLUME:
                    volume = MIN_CALL_VOLUME;
                    break;
                case SDKConstants.TTS_VOLUME:
                    volume = MIN_SPEECH_VOLUME;
                    break;
            }
        }
        return volume;
    }

    public MutableLiveData<Integer> getMediaVolume() {
        if (mMediaVolume == null) {
            mMediaVolume = new MutableLiveData<>();
            mMediaVolume.setValue(getStreamVolume(SDKConstants.MEDIA_VOLUME));
        }
        return mMediaVolume;
    }

    public MutableLiveData<Integer> getBlueToothMusicVolume() {
        if (mBlueToothMusicVolume == null) {
            mBlueToothMusicVolume = new MutableLiveData<>();
            mBlueToothMusicVolume.setValue(getStreamVolume(SDKConstants.BT_MEDIA_VOLUME));
        }
        return mBlueToothMusicVolume;
    }

    public MutableLiveData<Integer> getCallVolume() {
        if (mCallVolume == null) {
            mCallVolume = new MutableLiveData<>();
            mCallVolume.setValue(getStreamVolume(SDKConstants.PHONE_VOLUME));
        }
        return mCallVolume;
    }

    public MutableLiveData<Integer> getSpeechVolume() {
        if (mSpeechVolume == null) {
            mSpeechVolume = new MutableLiveData<>();
            mSpeechVolume.setValue(getStreamVolume(SDKConstants.TTS_VOLUME));
        }
        return mSpeechVolume;
    }

    private IVendorExtension getSDKManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    private ICarAudio getAudioManager() {
        return XmCarFactory.getCarAudioManager();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mOnOffMusic = null;
        mCarInfoSound = null;
        mCarSpeedVolumeCompensate = null;
        mParkMediaVolume = null;
        mMediaVolume = null;
        mBlueToothMusicVolume = null;
        mCallVolume = null;
        mSpeechVolume = null;
    }

    private int caseIndexToVehicleHintsLevel(int index) {
        int level;
        switch (index) {
            case 0:
            default:
                level = SDKConstants.VALUE.VehicleHints_LV1;
                break;
            case 1:
                level = SDKConstants.VALUE.VehicleHints_LV2;
                break;
        }
        return level;
    }

    private int caseVehicleHintsLevelToIndex(int level) {
        int index;
        switch (level) {
            case SDKConstants.VALUE.VehicleHints_LV1:
            default:
                index = 0;
                break;
            case SDKConstants.VALUE.VehicleHints_LV2:
                index = 1;
                break;
        }
        return index;
    }

    private int caseIndexToCarSpeedVolume(int index) {
        int level;
        switch (index) {
            case 0:
            default:
                level = SDKConstants.VALUE.SPEEDGAIN_OFF;
                break;
            case 1:
                level = SDKConstants.VALUE.SPEEDGAIN_LOW;
                break;
            case 2:
                level = SDKConstants.VALUE.SPEEDGAIN_MID;
                break;
            case 3:
                level = SDKConstants.VALUE.SPEEDGAIN_HIGH;
                break;
        }
        return level;
    }

    public int caseCarSpeedVolumeToIndex(int level) {
        int index;
        switch (level) {
            case SDKConstants.VALUE.SPEEDGAIN_OFF:
            default:
                index = 0;
                break;
            case SDKConstants.VALUE.SPEEDGAIN_LOW:
                index = 1;
                break;
            case SDKConstants.VALUE.SPEEDGAIN_MID:
                index = 2;
                break;
            case SDKConstants.VALUE.SPEEDGAIN_HIGH:
                index = 3;
                break;
        }
        return index;
    }

    private int caseIndexToParkMediaVolume(int index) {
        int level;
        switch (index) {
            case 0:
            default:
                level = SDKConstants.VALUE.VEHICLEHINTS_MUTE;
                break;
            case 1:
                level = SDKConstants.VALUE.VEHICLEHINTS_REDUCE;
                break;
            case 2:
                level = SDKConstants.VALUE.VEHICLEHINTS_NORMAL;
                break;
        }
        return level;
    }

    private int caseParkMediaVolumeToIndex(int level) {
        int index;
        switch (level) {
            case SDKConstants.VALUE.VEHICLEHINTS_MUTE:
                index = 0;
                break;
            case SDKConstants.VALUE.VEHICLEHINTS_REDUCE:
            default:
                index = 1;
                break;
            case SDKConstants.VALUE.VEHICLEHINTS_NORMAL:
                index = 2;
                break;
        }
        return index;
    }

    @Override
    public void onChange(int id, Object value) {
        if (event != null) {
            event.onChange(id, value);
        }
    }

    public void setOnChangeEvent(OnChangeEvent event) {
        this.event = event;
    }

    public interface OnChangeEvent {
        void onChange(int id, Object value);
    }

    /*
    private boolean caseOnOffMusicToIndex(int musicSwitch) {
        boolean open;
        switch (musicSwitch) {
            case SDKConstants.VALUE.BOOT_MUSIC_ON:
            default:
                open = true;
                break;
            case SDKConstants.VALUE.BOOT_MUSIC_OFF:
                open = false;
                break;
        }
        return open;
    }*/

}
