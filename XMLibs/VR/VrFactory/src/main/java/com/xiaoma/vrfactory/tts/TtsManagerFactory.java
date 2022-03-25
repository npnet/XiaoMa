package com.xiaoma.vrfactory.tts;

import com.xiaoma.cariflytek.tts.LxTtsManager;
import com.xiaoma.openiflytek.tts.OpenTtsManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.tts.BaseTtsManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:TTS factory
 */

public class TtsManagerFactory {

    private static BaseTtsManager ttsManager;

    public static BaseTtsManager getTtsManager(){
        if (ttsManager != null) {
            return ttsManager;
        }

        switch (VrConfig.mainVrSource) {

            case OpenIFlyTek:
                ttsManager =  new OpenTtsManager();
                break;
            case LxIFlyTek:
                ttsManager = new LxTtsManager();
              break;
            case Speech:
                break;

            default:
                ttsManager =  new OpenTtsManager();
                break;
        }

        return ttsManager;
    }

}
