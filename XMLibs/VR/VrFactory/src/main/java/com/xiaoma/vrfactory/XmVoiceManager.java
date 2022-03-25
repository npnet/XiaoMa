package com.xiaoma.vrfactory;

import android.content.Context;

import com.xiaoma.openiflytek.OpenVoiceManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vrfactory.tts.XmTtsManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/17
 * Desc：Engine config setting and other things init
 */

public class XmVoiceManager {
    private static XmVoiceManager instance;

    public static XmVoiceManager getInstance() {
        if (null == instance) {
            instance = new XmVoiceManager();
        }

        return instance;
    }


    public void init(Context context){
        switch (VrConfig.mainVrSource) {

            case OpenIFlyTek:
//                OpenVoiceManager.getInstance().init(context);
                break;
            case LxIFlyTek:
                break;

            case Speech:
                break;

            default:
//                OpenVoiceManager.getInstance().init(context);
                break;
        }
        //初始化TTS播报
        XmTtsManager.getInstance().init(context);

    }

}
