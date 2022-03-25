package com.xiaoma.setting.sound.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.RadioButtonLayout;
import com.xiaoma.setting.common.views.SettingSwitch;
import com.xiaoma.setting.common.views.TextTackSeekBar;
import com.xiaoma.setting.sound.vm.VolumeSettingsVM;

/**
 * Created by qiuboxiang on 2018/10/11 17:53
 * description:
 */
@PageDescComponent(EventConstants.PageDescribe.volumeSettingFragmentPagePathDesc)
public class VolumeSettingsFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener, RadioButtonLayout.CustomOnCheckedChangeListener, VolumeSettingsVM.OnChangeEvent, SettingSwitch.StateListener {

    public static final String TAG = "VolumeSettingsFragment";
    private VolumeSettingsVM mVolumeSettingsVM;
    public static final int CALL_MIN_VOLUME = 1;
    private TextTackSeekBar mMediaVolumeSeekBar;
    private int lastVolume;
    private TextTackSeekBar mBluetoothusicSeekBar;
    private TextTackSeekBar mCallVolumeSeekBar;
    private TextTackSeekBar mSpeechVolumeSeekBar;
    private SettingSwitch mOnOffMusicSwitch;
    private RadioButtonLayout mCarInfoSoundRadioGorup;
    private RadioButtonLayout mParkMediaVolumeRadioGorup;
    private RadioButtonLayout mCarSpeedVolumeRadioGorup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_volume_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mVolumeSettingsVM = ViewModelProviders.of(this).get(VolumeSettingsVM.class);
        bindView(view);
        mVolumeSettingsVM.setOnChangeEvent(this);
    }

    private void bindView(View view) {
        mOnOffMusicSwitch = view.findViewById(R.id.switch_on_off_music);
        mCarInfoSoundRadioGorup = view.findViewById(R.id.car_info_sound_radioGroup);
        mParkMediaVolumeRadioGorup = view.findViewById(R.id.park_media_volume_radioGroup);
        mCarSpeedVolumeRadioGorup = view.findViewById(R.id.car_speed_volume_compensate_radioGroup);
        mMediaVolumeSeekBar = view.findViewById(R.id.media_volume_seekbar);
        mBluetoothusicSeekBar = view.findViewById(R.id.bluetooth_music_seekbar);
        mCallVolumeSeekBar = view.findViewById(R.id.call_volume_seekbar);
        mSpeechVolumeSeekBar = view.findViewById(R.id.speech_volume_seekbar);

        mOnOffMusicSwitch.setChecked(mVolumeSettingsVM.getOnOffMusic().getValue());
        mCarInfoSoundRadioGorup.checkIndex(mVolumeSettingsVM.getCarInfoSound().getValue());
        mParkMediaVolumeRadioGorup.checkIndex(mVolumeSettingsVM.getParkMediaVolume().getValue());
        mCarSpeedVolumeRadioGorup.checkIndex(mVolumeSettingsVM.getCarSpeedVolumeCompensate().getValue());

        mOnOffMusicSwitch.setListener(this);
        mCarInfoSoundRadioGorup.setCustomOnCheckedChangeListener(this);
        mParkMediaVolumeRadioGorup.setCustomOnCheckedChangeListener(this);
        mCarSpeedVolumeRadioGorup.setCustomOnCheckedChangeListener(this);

        int mediaVolume = mVolumeSettingsVM.getMediaVolume().getValue();
        int blueToothMusicVolume = mVolumeSettingsVM.getBlueToothMusicVolume().getValue();
        int callVolume = mVolumeSettingsVM.getCallVolume().getValue();
        int speechVolume = mVolumeSettingsVM.getSpeechVolume().getValue();

//        mCallVolumeSeekBar.setMinValue(CALL_MIN_VOLUME);
        mMediaVolumeSeekBar.setProgress(mediaVolume);
        mBluetoothusicSeekBar.setProgress(blueToothMusicVolume);
        mCallVolumeSeekBar.setProgress(callVolume);
        mSpeechVolumeSeekBar.setProgress(speechVolume);

        mMediaVolumeSeekBar.setOnSeekBarChangeListener(this);
        mBluetoothusicSeekBar.setOnSeekBarChangeListener(this);
        mCallVolumeSeekBar.setOnSeekBarChangeListener(this);
        mSpeechVolumeSeekBar.setOnSeekBarChangeListener(this);
        // 设置最小值在切换主题的时候会有问题
//        mCallVolumeSeekBar.setMinValue(CALL_MIN_VOLUME);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        String event = "";
        switch (seekBar.getId()) {
            case R.id.media_volume_seekbar:
                if (lastVolume != i) {
                    lastVolume = i;
                    mVolumeSettingsVM.setMediaVolume(i);
                }
                event = EventConstants.slideEvent.mediaVolumeSetting;
                break;
            case R.id.bluetooth_music_seekbar:
                mVolumeSettingsVM.setBlueToothMusicVolume(i);
                event = EventConstants.slideEvent.bluetoothMusicSetting;
                break;
            case R.id.call_volume_seekbar:
                mVolumeSettingsVM.setCallVolume(i < CALL_MIN_VOLUME ? CALL_MIN_VOLUME : i);
                event = EventConstants.slideEvent.callVolumeSetting;
                break;
            case R.id.speech_volume_seekbar:
                mVolumeSettingsVM.setSpeechVolume(i);
                event = EventConstants.slideEvent.speechVolumeSetting;
                break;
        }
        XmAutoTracker.getInstance().onEvent(event, String.valueOf(i), TAG,
                EventConstants.PageDescribe.volumeSettingFragmentPagePathDesc);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.car_info_sound_radioGroup:
                mVolumeSettingsVM.setCarInfoSound(checkedId);
                break;
            case R.id.park_media_volume_radioGroup:
                mVolumeSettingsVM.setParkMediaVolume(checkedId);
                break;
            case R.id.car_speed_volume_compensate_radioGroup:
                mVolumeSettingsVM.setCarSpeedVolumeCompensate(checkedId);
                break;
        }
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.VALUE.ID_MEDIA_VOLUME) {    //媒体音量
            mMediaVolumeSeekBar.setOnSeekBarChangeListener(null);
            mMediaVolumeSeekBar.setProgress((int) value);
            mMediaVolumeSeekBar.setOnSeekBarChangeListener(this);
        } else if (id == SDKConstants.VALUE.ID_BT_MEDIA_VOLUME) { //蓝牙音乐音量
            mBluetoothusicSeekBar.setOnSeekBarChangeListener(null);
            mBluetoothusicSeekBar.setProgress((int) value);
            mBluetoothusicSeekBar.setOnSeekBarChangeListener(this);
        } else if (id == SDKConstants.VALUE.ID_BT_VOLUME) {  //电话音音量
            mCallVolumeSeekBar.setOnSeekBarChangeListener(null);
            mCallVolumeSeekBar.setProgress((int) value);
            mCallVolumeSeekBar.setOnSeekBarChangeListener(this);
        } else if (id == SDKConstants.VALUE.ID_TTS_VOLUME) {   //语音音量
            mSpeechVolumeSeekBar.setOnSeekBarChangeListener(null);
            mSpeechVolumeSeekBar.setProgress((int) value);
            mSpeechVolumeSeekBar.setOnSeekBarChangeListener(this);
        } else if (id == SDKConstants.BOOT_MUSIC) { //开关机音效
            mOnOffMusicSwitch.setListener(null);
            mOnOffMusicSwitch.setChecked((Boolean) value);
            mOnOffMusicSwitch.setListener(this);
        } else if (id == SDKConstants.VEHICLE_INFOMATION_LEVEL) { //车辆信息音
            mCarSpeedVolumeRadioGorup.setCustomOnCheckedChangeListener(null);
            mCarInfoSoundRadioGorup.checkIndex((int) value);
            mCarSpeedVolumeRadioGorup.setCustomOnCheckedChangeListener(this);
        } else if (id == SDKConstants.PARKING_MEDIA_LEVEL) { //泊车媒体音量
            mParkMediaVolumeRadioGorup.setCustomOnCheckedChangeListener(null);
            mParkMediaVolumeRadioGorup.checkIndex(mVolumeSettingsVM.caseParkingMediaLevelToIndex((int) value));
            mParkMediaVolumeRadioGorup.setCustomOnCheckedChangeListener(this);
        } else if (id == SDKConstants.ALC_LEVEL) { //车速音量补偿
            mCarSpeedVolumeRadioGorup.setCustomOnCheckedChangeListener(null);
            mCarSpeedVolumeRadioGorup.checkIndex(mVolumeSettingsVM.caseCarSpeedVolumeToIndex((int) value));
            mCarSpeedVolumeRadioGorup.setCustomOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheck(int viewId, boolean isChecked) {
        mVolumeSettingsVM.setOnOffMusic(isChecked);
        String status = isChecked ? "打开" : "关闭";
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.onOffMusicSetting, status,
                TAG, EventConstants.PageDescribe.volumeSettingFragmentPagePathDesc);
    }
}
