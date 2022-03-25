package com.xiaoma.openiflytek;

import android.content.Context;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;

/**
 * Created by Administrator
 * 2017/6/19 0019.
 */

public class OpenVoiceManager {
    private static OpenVoiceManager instance;
    private SpeechUtility speechUtility;

    public static OpenVoiceManager getInstance() {
        if (instance == null) {
            synchronized (OpenVoiceManager.class) {
                if (instance == null) {
                    instance = new OpenVoiceManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        KLog.d("XmVoiceManager init");
        initXf(context);
    }

    public void initXf(Context context) {
        if (speechUtility != null) {
            return;
        }
        Setting.setShowLog(false);
        StringBuffer param = new StringBuffer();
        param.append("appid=" + VrConfig.APP_ID);
        param.append(",");
        param.append(VrConfig.XF_DEFAULT_SERVER_URL);
        param.append(",");
        param.append(SpeechConstant.LIB_NAME + "=" + "mscopen");
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        speechUtility = SpeechUtility.createUtility(context, param.toString());
    }
}
