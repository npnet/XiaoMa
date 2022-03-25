package com.xiaoma.shop.business.tts;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.cariflytek.tts.XttsWork;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.LoginManager;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.vr.tts.EventTtsManager;

import java.io.File;
import java.util.Objects;

/**
 * Created by LKF on 2019-7-1 0001.
 */
public class TTSUsing {
    private static final String TAG = "TtsUsing";
    private static final String VOICE_SPEAKER_KEY = "speaker_role";

    /**
     * 使用主题
     */
    public static boolean useTTS(BaseFragment fragment, SkusBean tts) {
        if (fragment == null || fragment.isDestroy()) {
            Log.e(TAG, "useTTS: fragment is destroy");
            return false;
        }
        if (tts == null) {
            Log.e(TAG, "useTTS: tts is null");
            return false;
        }
        String voiceParam = tts.getVoiceParam();
        String voiceName = tts.getThemeName();
        String resUrl = tts.getTtsResDownloadUrl();
        Log.i(TAG, String.format("useTTS: { voiceParam: %s, voiceName: %s, resUrl: %s }", voiceParam, voiceName, resUrl));
        // 声音参数是int类型,这里需要做个校验,避免传入无效的声音参数导致TTS失效
        int voiceId = -1;
        try {
            voiceId = Integer.parseInt(voiceParam);
        } catch (Exception ignored) {
        }
        if (voiceId < 0) {
            XMToast.toastException(fragment.getContext(), R.string.invalid_tts_id);
            return false;
        }
        // 无下载链接的判定为内置TTS
        if (TextUtils.isEmpty(resUrl)) {
            useInternalTTS(tts);
            XMToast.toastSuccess(fragment.getContext(), R.string.successful_use);
            return true;
        } else {
            TTSDownload download = TTSDownload.getInstance();
            File ttsResFile = download.getDownloadFile(tts);
            if (ttsResFile != null && ttsResFile.exists()) {
                useExtensionTTS(tts, ttsResFile);
                XMToast.toastSuccess(fragment.getContext(), R.string.successful_use);
                return true;
            } else {
                download.start(tts);
            }
        }
        return false;
    }

    /**
     * 切换内置音色
     */
    public static void useInternalTTS(SkusBean tts) {
        if (tts == null) {
            Log.e(TAG, "useInternalTTS: tts is null !!!");
            return;
        }
        String voiceParam = tts.getVoiceParam();
        String voiceName = tts.getThemeName();
        Log.i(TAG, String.format("useInternalTTS: { voiceParam: %s, voiceName: %s }", voiceParam, voiceName));
        XmProperties.build().put(VOICE_SPEAKER_KEY, voiceParam);
        EventTtsManager.getInstance().setVoiceType(voiceParam, voiceName);
    }

    /**
     * 切换扩展TTS
     */
    public static void useExtensionTTS(SkusBean tts, File zipResPath) {
        if (tts == null) {
            Log.e(TAG, "useExtensionTTS: tts is null !!!");
            return;
        }
        if (zipResPath == null || !zipResPath.exists()) {
            Log.e(TAG, String.format("useExtensionTTS: { tts: %s, path: %s } Invalid res path !!!", tts.getThemeName(), zipResPath));
            return;
        }
        String voiceParam = tts.getVoiceParam();
        String voiceName = tts.getThemeName();
        Log.i(TAG, String.format("useExtensionTTS: { voiceParam: %s, voiceName: %s }", voiceParam, voiceName));
        int voiceParamInt = -1;
        try {
            voiceParamInt = Integer.parseInt(tts.getVoiceParam());
        } catch (Exception ignored) {
        }
        if (voiceParamInt >= 0) {
            XmProperties.build().put(VOICE_SPEAKER_KEY, voiceParam);
            new XttsWork(1, zipResPath.getPath(),
                    voiceParamInt, tts.getThemeName(),
                    null)
                    .run();
        }
    }

    /**
     * 获取使用中的TTS的VoiceParam
     *
     * @return 使用中的TTS的VoiceParam
     */
    public static String getUsingTTS() {
        String usingTTS = XmProperties.build().get(VOICE_SPEAKER_KEY, null);
        Log.i(TAG, "getUsingTTS: %s" + usingTTS);
        return usingTTS;
    }

    /**
     * 判断音色是否正在使用
     */
    public static boolean isTTSUsing(SkusBean tts) {
        return tts != null && Objects.equals(tts.getVoiceParam(), getUsingTTS());
    }
}
