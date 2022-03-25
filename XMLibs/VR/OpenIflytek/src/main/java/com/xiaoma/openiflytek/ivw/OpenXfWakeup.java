package com.xiaoma.openiflytek.ivw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.openiflytek.OpenVoiceManager;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.ivw.BaseWakeup;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.vr.recorder.RecordConstants;

import org.json.JSONObject;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * 开放版讯飞唤醒
 */
class OpenXfWakeup extends BaseWakeup {
    // 语音唤醒对象
    private VoiceWakeuper mIvw;

    @Override
    public void init(Context context) {
        if (mIvw != null) {
            return;
        }
        this.context = context;
        OpenVoiceManager.getInstance().initXf(context);
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, new InitListener() {
            @Override
            public void onInit(int i) {
                KLog.d("Init result " + i);
                //startWakeup();
            }
        });
        KLog.d("Ivw init XF wakeup " + mIvw);
        initParameter();
    }

    private void initParameter() {
        if (mIvw == null) {
            return;
        }
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        if (ConfigManager.ApkConfig.isDebug()) {
            int curThresh = (int) TPUtils.get(context, VrConfig.SP_IVW_THRESHOLD, VrConfig.DEFAULT_THRESH);
            //需要输入id,若不输入id,门限值会失效
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD,
                    "0:" + curThresh + ";1:"+ curThresh + ";2:" + curThresh);
        } else {
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "" + VrConfig.DEFAULT_THRESH + "");
        }

        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, "0");
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource(context));

        mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        mIvw.setParameter(SpeechConstant.SAMPLE_RATE, String.valueOf(RecordConstants.DEFAULT_SAMPLE_RATE));
    }

    @Override
    public void setOnWakeUpListener(OnWakeUpListener onWakeUpListener) {
        this.onWakeUpListener = onWakeUpListener;
    }

    @Override
    public void startWakeup() {
        if (mIvw == null || mIvw.isListening()) {
            return;
        }
        int ret = mIvw.startListening(mWakeuperListener);
        if (ret != 0) {
            KLog.d("wakeup Start listening FAILED! " + ret);
        } else {
            KLog.d("wakeup Start listening SUCCESS!");
        }
    }

    @Override
    public void stopWakeup() {
        KLog.d("before stop wakeup");
        if (mIvw == null) {
            return;
        }
        mIvw.stopListening();
        KLog.d("wakeup Stop listening");
    }

    private String getResource(Context context) {
        return ResourceUtil.generateResourcePath(context,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + VrConfig.APP_ID + ".jet");
    }

    @Override
    public void setIvwThreshold(int curThresh) {
        TPUtils.put(context, VrConfig.SP_IVW_THRESHOLD, curThresh);
        stopWakeup();
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw == null) {
            return;
        }
        initParameter();
        startWakeup();
    }

    @Override
    public boolean setWakeupWord(String word) {
        return false;
    }

    @Override
    public boolean resetWakeupWord() {
        return false;
    }

    @Override
    public List<String> getWakeupWord() {
        return null;
    }

    @Override
    public boolean registerOneShotWakeupWord(List<String> wakeupWord) {
        return false;
    }

    @Override
    public boolean unregisterOneShotWakeupWord(List<String> wakeupWord) {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean destroyIvw() {
        return false;
    }

    @Override
    public void appendAudioData(byte[] buffer, int start, int byteCount) {
        if (mIvw == null) {
            return;
        }
        mIvw.writeAudio(buffer, start, byteCount);
    }

    @Override
    public void appendAudioData(byte[] buffer, byte[] bufferLeft, byte[] bufferRight) {

    }

    private void onWakeup(int nMvwId) {
        boolean wakeupOn = TPUtils.get(context,VrConfig.SP_ENABLE_IVW, true);
        KLog.d(StringUtil.format("onWakeup, wakeupOn : %b", wakeupOn));
        if (!wakeupOn) {
            return;
        }
        if (onWakeUpListener != null) {
            onWakeUpListener.onWakeUp(nMvwId,"",true);
        }
        Intent intent = new Intent(VrConstants.Actions.ON_IVW_WAKEUP);
        intent.putExtra(VrConstants.ActionExtras.IVW_WAKEUP, nMvwId);
        context.sendBroadcast(intent);
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onResult(WakeuperResult result) {
            KLog.d("onResult: " + result);
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("[RAW]" + text);
                buffer.append("\n");
                buffer.append("[sst]" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("[id]" + object.optString("id"));
                buffer.append("\n");
                String score = object.optString("score");
                buffer.append("[score]" + score);
                buffer.append("\n");
                buffer.append("[bos]" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("[eos]" + object.optString("eos"));
                String resultString = buffer.toString();
                KLog.d(resultString);
                onWakeup(-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
           /* KLog.d("eventType:" + eventType + "arg1:" + isLast + "arg2:" + arg2);
            // 识别结果
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                RecognizerResult reslut = ((RecognizerResult) obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                KLog.d(reslut.getResultString());
            }*/
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
}
