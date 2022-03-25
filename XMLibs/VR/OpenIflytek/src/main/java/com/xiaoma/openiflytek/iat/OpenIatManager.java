package com.xiaoma.openiflytek.iat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.xiaoma.openiflytek.OpenVoiceManager;
import com.xiaoma.openiflytek.ivw.OpenIvwManager;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IIatManager;
import com.xiaoma.vr.iat.IatType;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.recorder.BaseRecorder;
import com.xiaoma.vr.recorder.OnRecordListener;
import com.xiaoma.vr.recorder.RecordConstants;
import com.xiaoma.vr.recorder.RecorderManager;
import com.xiaoma.vr.recorder.RecorderType;
import com.xiaoma.vr.utils.ContactsUtils;
import com.xiaoma.vr.utils.VrUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * Desc：Iat manager by Open IFlyTek Speech
 */
public class OpenIatManager implements IIatManager, OnRecordListener {
    private Context context;
    private OnIatListener onIatListener;
    private static OpenIatManager instance = null;
    private SpeechRecognizer speechRecognizer;
    private Map<String, String> mIatResults = new LinkedHashMap<>();
    private IatType iatType = IatType.Normal;
    private IatParamBuilder iatParamBuilder = new IatParamBuilder();
    private FileOutputStream mPcmOutput;
    private boolean iatStateCancel = false;
    private boolean iatStateStop = false;
    private String TAG = OpenIatManager.class.getSimpleName();


    public synchronized static OpenIatManager getInstance() {
        if (instance == null) {
            instance = new OpenIatManager();
        }
        return instance;
    }


    @Override
    public void init(Context context) {
        if (speechRecognizer != null) {
            return;
        }
        this.context = context.getApplicationContext();
        OpenVoiceManager.getInstance().initXf(context);
        createRecognizer(context);
    }


    private void createRecognizer(Context context) {
        speechRecognizer = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int i) {
                KLog.d("onInit state is " + i);
                if (i == ErrorCode.SUCCESS) {
                    upLoadContact(true, "");
                }
            }
        });
    }


    @Override
    public void setOnIatListener(OnIatListener onIatListener) {
        this.onIatListener = onIatListener;
    }


    private boolean shouldWriteToFile() {
        //保存录音文件（聊天、评论等长语音场景）
        return IatType.Record == iatType;
    }


    @Override
    public void onStart(BaseRecorder recorder) {
        boolean shouldWrite = shouldWriteToFile();
        KLog.d(TAG, StringUtil.format("XmLog onStart [ iatType:%s, shouldWrite : %b ]", iatType, shouldWrite));
        if (shouldWrite) {
            File file = new File(VrConfig.PCM_FILE_PATH);
            if (!file.exists()) {
                //这里不存在时候去创建一下，防止第一次启动的时候因为文件目录不存在，而导致第一次的录音文件异常
                file.mkdirs();
            }
            if (file.exists()) {
                KLog.d(StringUtil.format("delete pcm file rlt : %b", file.delete()));
            }
            try {
                mPcmOutput = new FileOutputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, int start, int byteCount) {
        appendAudioData(buffer, start, byteCount);
        if (shouldWriteToFile()) {
            try {
                mPcmOutput.write(buffer, start, byteCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, byte[] leftBuffer, byte[] rightBuffer, int start, int byteCount) {

    }

    @Override
    public void onStop(BaseRecorder recorder) {
        KLog.d(TAG, "XmLog onStop ");
        boolean shouldWrite = shouldWriteToFile();
        KLog.i(StringUtil.format("recorder stop [ iatType : %s, shouldWrite : %b ]", iatType, shouldWrite));
        if (shouldWrite) {
            try {
                mPcmOutput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //WaveFileHeaderUtils.copyWaveFile(VrConstants.PCM_FILE_PATH, VrConstants.WAV_FILE_PATH);
                File dstFile = new File(VrConstants.PCM_RECORD_FILE_PATH);
                if (dstFile.exists())
                    dstFile.delete();
                boolean copy = FileUtils.copy(new File(VrConstants.PCM_FILE_PATH), dstFile);
                KLog.e(TAG, " onStop : copy file :" + copy);
            } catch (Exception e) {
                e.printStackTrace();
            }
            KLog.d(TAG, "XmLog onWavFileComplete : ");
            if (onIatListener != null) {
                onIatListener.onWavFileComplete();
            }
            context.sendBroadcast(new Intent(VrConstants.Actions.ON_IAT_WAV_FILE_COMPLETE));
        }
    }

    @Override
    public void onRecordFailed(BaseRecorder recorder, Exception e) {
        KLog.i(StringUtil.format("recorder failed, err msg : %s", String.valueOf(e)));
    }


    private void appendAudioData(byte[] buffer, int start, int byteCount) {
        getSpeechRecognizer().writeAudio(buffer, start, byteCount);
    }

    private void startRecorderInternal() {
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            return;
        }
        BaseRecorder r = RecorderManager.getInstance().startRecord(RecorderType.IAT);
        if (r != null) {
            r.addOnRecordListener(this);
        } else {
            KLog.e("iat recorder start failed !");
        }
    }

    private void stopRecorderInternal() {
        if (VrConfig.mainVrSource == VrConfig.VrSource.LxIFlyTek) {
            return;
        }
        RecorderManager.getInstance().stopRecord(RecorderType.IAT);
    }


    /**
     * 科大讯飞识别回调
     */
    private RecognizerListener recognizerListener = new RecognizerListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            int volume = i;
            if (onIatListener != null) {
                onIatListener.onVolumeChanged(volume);
            }
            Intent intent = new Intent(VrConstants.Actions.ON_IAT_VOLUME_CHANGE);
            intent.putExtra(VrConstants.ActionExtras.IAT_VOLUME, volume);
            intent.addFlags(0x01000000);
            context.sendBroadcast(intent);
        }

        @Override
        public void onBeginOfSpeech() {
            KLog.d("speech begin...");
        }

        @Override
        public void onEndOfSpeech() {
            KLog.d("speech end !");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            KLog.d(recognizerResult.getResultString());
            String text = VrUtils.IFly.parseIatResult(recognizerResult.getResultString());
            KLog.d(TAG, "XmLog onResult : " + text);
            String sn = getResultKey(recognizerResult, text);
            if (!TextUtils.isEmpty(text)) {
                mIatResults.put(sn, text);
            }
            handleEachText(isLast, text);
            if (isLast) {
                handleLastText();
            }
        }

        @Nullable
        private String getResultKey(RecognizerResult recognizerResult, String text) {
            String sn = null;
            try {
                JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
                sn = resultJson.optString("sn");
                KLog.d(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return sn;
        }

        @SuppressLint("WrongConstant")
        private void handleEachText(boolean isLast, String text) {
            KLog.d(TAG, "XmLog onResult : " + text);
            if (iatStateCancel) {
                return;
            }
            String totalText = getVoiceContent();
            if (onIatListener != null) {
                onIatListener.onResult(totalText, isLast, text);
            }
            context.sendBroadcast(new Intent(VrConstants.Actions.ON_IAT_RESULT)
                    .addFlags(0x01000000)
                    .putExtra(VrConstants.ActionExtras.IAT_VOICE_TEXT, totalText)
                    .putExtra(VrConstants.ActionExtras.IAT_VOICE_ISLAST, isLast));
        }

        @SuppressLint("WrongConstant")
        private void handleLastText() {
            KLog.d(TAG, "XmLog onComplete : ");
            if (needRestartListeningAutomatic()) {
                //如果识别到语音结束，在选择场景下会自动重新开始识别
                startListeningForChoose();
                return;
            }
            if (iatType == IatType.Normal) {
                OpenIvwManager.getInstance().startWakeup();
            }

            if (iatType == IatType.ForChoose) {
                return;
            }

            if (iatStateCancel) {
                return;
            }

            String voiceContent = getVoiceContent();
            if (onIatListener != null) {
                onIatListener.onComplete(voiceContent, "");
            }
            Intent intent = new Intent(VrConstants.Actions.ON_IAT_COMPLETE);
            intent.putExtra(VrConstants.ActionExtras.IAT_VOICE_CONTENT, voiceContent);
            intent.addFlags(0x01000000);
            context.sendBroadcast(intent);
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onError(SpeechError speechError) {
            String msg = speechError.getErrorCode() + " " + speechError.getErrorDescription();
            KLog.d(msg);
            KLog.d(TAG, "XmLog onError : " + msg);
            if (isNoVoiceError(speechError)) {
                //没有说话的错误，在选择场景下会自动重新开始识别
                if (needRestartListeningAutomatic()) {
                    startListeningForChoose();
                    KLog.d(TAG, "XmLog onError :startListeningForChoose");
                    return;
                } else if (needRestartRecord()) {
                    KLog.d(TAG, "XmLog onError,startListeningRecord ");
                    startListeningRecord();
                    return;
                } else {
                    if (onIatListener != null) {
                        KLog.d(TAG, "XmLog onNoSpeaking : " + msg);
                        onIatListener.onNoSpeaking();
                        return;
                    }
                }
            }

            if (iatStateCancel) {
                return;
            }

            int code = speechError.getErrorCode() == IatError.ERROR_NO_SPEAK ? speechError.getErrorCode() : IatError.ERROR;
            if (onIatListener != null) {
                Log.e(TAG, "XmLog open iat error code:" + speechError.getErrorCode() + "  msg:" + speechError.getErrorDescription());
                onIatListener.onError(code);
            }

            sendOnError(code);
            OpenIvwManager.getInstance().startWakeup();
        }

        private boolean needRestartListeningAutomatic() {
            return (iatType == IatType.ForChoose) && !iatStateStop;
        }

        private boolean needRestartRecord() {
            return (iatType == IatType.Record) && !iatStateStop;
        }

        private boolean isNoVoiceError(SpeechError speechError) {
            return speechError.getErrorCode() == 10118;
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    private String getVoiceContent() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = mIatResults.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            stringBuilder.append(entry.getValue());
        }
        KLog.d("Recognize voice: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void clearLastIatResults() {
        mIatResults.clear();
    }


    private void startXfIat() {
        getSpeechRecognizer().startListening(recognizerListener);
    }

    private SpeechRecognizer getSpeechRecognizer() {
        if (speechRecognizer == null) {
            createRecognizer(this.context);
        }
        return speechRecognizer;
    }

    private void startIat(int startTimeOut, int endTimeOut) {
        startIat(startTimeOut, endTimeOut, false);
    }

    /**
     * @param startTimeOut 前端点
     * @param endTimeOut   尾端点
     * @param isDefine     永久录制
     */
    private void startIat(int startTimeOut, int endTimeOut, boolean isDefine) {
        if (isDefine) {
            //永久录制 此标记为如果NOSpeak，再次启动录音
            iatStateCancel = false;
            iatStateStop = false;
        } else {
            //超时
            iatStateStop = true;
        }
        iatParamBuilder.build(startTimeOut, endTimeOut);
        clearLastIatResults();
        startXfIat();
        startRecorderInternal();
    }


    @Override
    public void release() {

    }

    @Override
    public void stopListening() {
        KLog.d("Iat stopListening in OpenIatManager");
        this.iatStateStop = true;
        this.iatStateCancel = false;
        getSpeechRecognizer().stopListening();
        stopRecorderInternal();
    }

    public void onMicError() {
        if (onIatListener != null) {
            onIatListener.onError(IatError.ERROR_MIC_FOCUS_LOSS);
        }
        sendOnError(IatError.ERROR_MIC_FOCUS_LOSS);
    }

    private void sendOnError(int errorCode) {
        Intent intent = new Intent(VrConstants.Actions.ON_IAT_ERROR);
        intent.putExtra(VrConstants.ActionExtras.IAT_ERROR_CODE, errorCode);
        intent.addFlags(0x01000000);
        context.sendBroadcast(intent);
    }

    @Override
    public void startListeningNormal() {
        iatType = IatType.Normal;
        startIat(0, 0, true);
    }


    @Override
    public void startListeningRecord() {
        iatType = IatType.Record;
        startIat(0, 0, true);
    }


    @Override
    public void startListeningRecord(int startTimeOut, int endTimeOut) {
        iatType = IatType.Record;
        startIat(startTimeOut, endTimeOut, false);
    }


    @Override
    public void startListeningForChoose() {
        iatType = IatType.ForChoose;
        startIat(0, 0, true);
    }

    @Override
    public void startListeningForChoose(String srSceneStkCmd) {

    }


    @Override
    public void cancelListening() {
        KLog.d("Iat cancelListening in OpenIatManager");
        this.iatStateCancel = true;
        this.iatStateStop = true;
        getSpeechRecognizer().stopListening();
    }


    @Override
    public void upLoadContact(final boolean isPhoneContact, final String contacts) {
        KLog.i("OpenIatManager : uploadContacts isPhoneContact : " + isPhoneContact + ", contacts = " + contacts);
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                if (speechRecognizer == null) {
                    ThreadDispatcher.getDispatcher().postDelayed(this, 2000, Priority.LOW);
                    return;
                }
                try {
                    String data;
                    if (isPhoneContact) data = ContactsUtils.getAllContactUserName();
                    else data = contacts;
                    if (!TextUtils.isEmpty(data)) {
                        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
                        int ret = speechRecognizer.updateLexicon("contact", data, lexiconListener);
                        KLog.i("OpenIatManager : upload contactInfos : " +
                                ((ret == ErrorCode.SUCCESS) ? "success" : "failed") + ", ret = " + ret);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void uploadAppState(boolean isForeground, String appType) {

    }

    @Override
    public void uploadPlayState(boolean isPlaying, String appType) {

    }

    @Override
    public void uploadNaviState(String naviState) {

    }

    //上传联系人监听器。
    private LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                KLog.i("OpenIatManager : " + error.toString());
            } else {
                KLog.i("OpenIatManager : upload contactInfo success");
            }
        }
    };


    @Override
    public void destroy() {
        getSpeechRecognizer().stopListening();
        getSpeechRecognizer().cancel();
        getSpeechRecognizer().destroy();
        speechRecognizer = null;
    }

    @Override
    public boolean getInitState() {
        return false;
    }


    private class IatParamBuilder {

        void useRecord() {
            speechRecognizer.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "60000");
            speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "30000");
            speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "30000");
        }


        void useRecord(int bosTimeOut, int eosTimeOut) {
            KLog.d("IatParamBuilder : useRecord timeout = ");
            speechRecognizer.setParameter(SpeechConstant.NET_TIMEOUT, "5000");
            speechRecognizer.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "60000");
            speechRecognizer.setParameter(SpeechConstant.VAD_BOS, getStartTimeOut(bosTimeOut) + "");
            speechRecognizer.setParameter(SpeechConstant.VAD_EOS, getEndTimeOut(eosTimeOut) + "");
        }

        void noPit() {
            speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
        }

        void usePit() {
            speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");
        }

        void build(int startTimeOut, int endTimeOut) {
            if (speechRecognizer == null) createRecognizer(OpenIatManager.this.context);
            useNormal();
            if (iatType == IatType.Record) {
                useRecord(startTimeOut, endTimeOut);
                usePit();
            } else if (iatType == IatType.ForChoose) {
                useRecord();
                noPit();
            }
        }

        private void useNormal() {
            speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
            speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
            speechRecognizer.setParameter(SpeechConstant.NET_TIMEOUT, "30000");
            speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
            speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1200");
            speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
            //speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE,
                    String.valueOf(RecordConstants.DEFAULT_SAMPLE_RATE));
            //speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, wavFilePath);
            speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            //启动录音后，有短暂的音频跳变，忽略前面xxxms的音频
            speechRecognizer.setParameter(SpeechConstant.FILTER_AUDIO_TIME, "400");
        }

        public int getStartTimeOut(int timeOut) {
            return timeOut > 0 ? timeOut : 30000;
        }

        public int getEndTimeOut(int timeOut) {
            return timeOut > 0 ? timeOut : 30000;
        }
    }

}


