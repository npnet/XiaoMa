package com.xiaoma.vrfactory.tts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.vr.tts.BaseTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.tts.TtsPriority;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:Local TTS manager
 */

public class XmTtsManager {

    private static XmTtsManager instance;
    private BaseTtsManager ttsManager;
    private String curTtsRole;

    private BroadcastReceiver ttsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //id,名称，资源路径，试听音频路径
            String ttsID = intent.getStringExtra(VrConstants.ActionExtras.TTS_NAME);
            String ttsName = intent.getStringExtra(VrConstants.ActionExtras.TTS_NAME);
            String ttsPath = intent.getStringExtra(VrConstants.ActionExtras.TTS_AUDIO);
            String ttsAudio = intent.getStringExtra(VrConstants.ActionExtras.TTS_PATH);
            setTtsRole(ttsID, ttsName, ttsPath, ttsAudio);
        }
    };


    public static XmTtsManager getInstance() {
        if (null == instance) {
            instance = new XmTtsManager();
        }

        return instance;
    }

    public XmTtsManager() {
        ttsManager = TtsManagerFactory.getTtsManager();
    }


    public void init(Context context) {
        ttsManager.init(context);
        initTtsConfig(context);
    }

    private void initTtsConfig(Context context) {
        context.registerReceiver(ttsReceiver, new IntentFilter(VrConstants.Actions.TTS_CHANGE));
        curTtsRole = XmProperties.build(VrAidlServiceManager.getInstance().getUid()).get(VrConstants.ActionExtras.TTS_CUR_ROLE, "");
        if (!TextUtils.isEmpty(curTtsRole)) {
            setTtsRole(curTtsRole, null, null, null);
        }
    }

    private void setTtsRole(String ttsId, String ttsName, String ttsPath, String ttsVideo) {
        //检查是否为本地
        if (isLocalTTS(ttsId, ttsName, ttsPath)) {
            setTtsForLocal(ttsId, ttsName);
        } else {
            setTtsForSdcard(ttsName, ttsPath, ttsVideo);
        }
    }

    private void checkTtsParam() {
        // TODO: 2019/4/19 0019 检查下发tts资源路径及合法性
    }

    private void setTtsForSdcard(String ttsName, String ttsPath, String ttsVideo) {
        // TODO: 2019/4/19 0019
        checkTtsParam();
    }

    private void setTtsForLocal(String ttsId, String ttsName) {
        // TODO: 2019/4/19 0019
        setVoiceType(ttsId, ttsName);
    }

    /**
     * 本地自带或已经下载的TTS
     *
     * @param ttsId
     * @param ttsName
     * @param ttsPath
     * @return
     */
    private boolean isLocalTTS(String ttsId, String ttsName, String ttsPath) {
        //套件自带或本地已经下载该资源可直接切换
        return true;
    }

    //蓝牙电话tts播报
    public boolean startSpeakingByPhone(String text) {
        return ttsManager.startSpeaking(text, TtsPriority.HIGH);
    }
    //蓝牙电话tts播报
    public boolean startSpeakingByPhone(String text, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, TtsPriority.HIGH, listener);
    }
    //蓝牙电话tts播报
    public boolean startSpeakingByPhone(String text, int speed, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, speed, TtsPriority.HIGH, listener);
    }
    //语音助手tts播报
    public boolean startSpeakingByAssistant(String text) {
        return ttsManager.startSpeaking(text, TtsPriority.NORMAL);
    }
    //语音助手tts播报
    public boolean startSpeakingByAssistant(String text, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, TtsPriority.NORMAL, listener);
    }
    //语音助手tts播报
    public boolean startSpeakingByAssistant(String text, int speed, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, speed, TtsPriority.NORMAL, listener);
    }
    //第三方应用tts播报
    public boolean startSpeakingByThird(String text) {
        return ttsManager.startSpeaking(text, TtsPriority.LOW);
    }
    //第三方应用tts播报
    public boolean startSpeakingByThird(String text, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, TtsPriority.LOW, listener);
    }
    //第三方应用tts播报
    public boolean startSpeakingByThird(String text, int speed, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, speed, TtsPriority.LOW, listener);
    }
    //导航tts播报
    public boolean startSpeakingByNavi(String text) {
        return ttsManager.startSpeaking(text, TtsPriority.LOWER);
    }
    //导航tts播报
    public boolean startSpeakingByNavi(String text, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, TtsPriority.LOWER, listener);
    }
    //导航tts播报
    public boolean startSpeakingByNavi(String text, int speed, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, speed, TtsPriority.LOWER, listener);
    }
    //小马应用tts播报
    public boolean startSpeakingByXmApp(String text) {
        return ttsManager.startSpeaking(text, TtsPriority.LOWEST);
    }
    //小马应用tts播报
    public boolean startSpeakingByXmApp(String text, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, TtsPriority.LOWEST, listener);
    }
    //小马应用tts播报
    public boolean startSpeakingByXmApp(String text, int speed, OnTtsListener listener) {
        return ttsManager.startSpeaking(text, speed, TtsPriority.LOWEST, listener);
    }

    public boolean isTtsSpeaking() {
        return ttsManager.isTtsSpeaking();
    }

    public void stopSpeaking() {
        ttsManager.stopSpeaking();
    }


    public void removeListeners() {
        ttsManager.removeListeners();
    }


    public void auditionVoiceType(String voiceParam, String speakContent, OnTtsListener listener) {
        ttsManager.auditionVoiceType(voiceParam, speakContent, listener);
    }


    public String setVoiceType(int voiceType) {
        return ttsManager.setVoiceType(voiceType);
    }


    public boolean setVoiceType(String voiceParam, String voiceName) {
        return ttsManager.setVoiceType(voiceParam, voiceName);
    }


    public void destroy() {
        ttsManager.destroy();
    }
}
