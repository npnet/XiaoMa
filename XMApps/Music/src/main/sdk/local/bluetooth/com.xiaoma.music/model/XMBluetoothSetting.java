package com.xiaoma.music.model;

import android.bluetooth.BluetoothAvrcpPlayerSettings;

import com.xiaoma.adapter.base.XMBean;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public class XMBluetoothSetting extends XMBean<BluetoothAvrcpPlayerSettings> {

    /**
     * Equalizer setting.
     */
    public static final int SETTING_EQUALIZER = 0x01;

    /**
     * Repeat setting.
     */
    public static final int SETTING_REPEAT = 0x02;

    /**
     * Shuffle setting.
     */
    public static final int SETTING_SHUFFLE = 0x04;

    /**
     * Scan mode setting.
     */
    public static final int SETTING_SCAN = 0x08;

    /**
     * Invalid state.
     * <p>
     * Used for returning error codes.
     */
    public static final int STATE_INVALID = -1;

    /**
     * OFF state.
     * <p>
     * Denotes a general OFF state. Applies to all settings.
     */
    public static final int STATE_OFF = 0x00;

    /**
     * ON state.
     */
    public static final int STATE_ON = 0x01;

    /**
     * Single track repeat.
     */
    public static final int STATE_SINGLE_TRACK = 0x02;

    /**
     * All track repeat/shuffle.
     */
    public static final int STATE_ALL_TRACK = 0x03;

    /**
     * Group repeat/shuffle.
     */
    public static final int STATE_GROUP = 0x04;

    public XMBluetoothSetting(BluetoothAvrcpPlayerSettings bluetoothAvrcpPlayerSettings) {
        super(bluetoothAvrcpPlayerSettings);
    }

    public int getSettings() {
        return getSDKBean().getSettings();
    }


    public void addSettingValue(int setting, int value) {
        getSDKBean().addSettingValue(setting, value);
    }


    public int getSettingValue(int setting) {
        return getSDKBean().getSettingValue(setting);
    }
}
