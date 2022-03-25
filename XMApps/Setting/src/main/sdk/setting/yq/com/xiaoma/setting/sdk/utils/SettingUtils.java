package com.xiaoma.setting.sdk.utils;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.LocaleList;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Locale;

/**
 * @author: iSun
 * @date: 2019/1/10 0010
 */
public class SettingUtils {
    public static final int VOLUME_MEDIA= 0;
    public static final int VOLUME_BT_CALL= 1;
    public static final int VOLUME_TTS= 2;
    public static final int VOLUME_BT_MEDIA= 3;


    public static void setLocale(Context context, Locale locale) {
        try {
            Configuration cfg = ActivityManagerNative.getDefault().getConfiguration();
            LocaleList localeList = context.getResources().getConfiguration().getLocales();
            cfg.setLocales(new LocaleList(locale));
            ActivityManagerNative.getDefault().updatePersistentConfiguration(cfg);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static BluetoothSocket setDevice(BluetoothDevice device, int i) {
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocket(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }

}
