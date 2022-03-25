package com.xiaoma.carwxsdkimpl.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.carwxsdk.AsrListener;
import com.xiaoma.carwxsdk.ClientListener;
import com.xiaoma.carwxsdk.SpeedChangeListener;
import com.xiaoma.carwxsdk.TtsListener;
import com.xiaoma.carwxsdkimpl.utils.CarWXSDKConstants;
import com.xiaoma.carwxsdkimpl.utils.HandleSpecialWordsUtils;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.process.manager.XMApi;
import com.xiaoma.process.manager.XMBluetoothPhoneApiManager;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.utils.SkinInfo;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;

import java.io.File;
import java.util.List;

import static com.xiaoma.carwxbase.utils.CarWXConstants.DEFAULT_THEME_ID;
import static com.xiaoma.carwxbase.utils.CarWXConstants.THEME_ID_DAOMENG;
import static com.xiaoma.carwxbase.utils.CarWXConstants.THEME_ID_QINGSHE;
import static com.xiaoma.carwxbase.utils.CarWXConstants.THEME_ID_ZHIHUI;

public class XMCarManager {
    private static XMCarManager INSTANCE;
    private final String TAG = "XMCarManager";
    private TtsListener ttsListener;
    private AsrListener asrListener;
    private static final String PATH_VOICE_RECORD = "CarWXIat/";
    private SpeedChangeListener speedChangeListener;
    private RemoteIatManager remoteIatManager;
    // 是否需要录音回调状态 当取消录音时 不需要解析 也不需要回调录音状态给车载微信
    private boolean needRecordCallback = true;
    // 录音时 结尾是否需要标点符号 查找联系人时结尾不需要标点
    private boolean needPunctuation;

    private XMCarManager() {
    }

    public static XMCarManager getInstance() {
        if (INSTANCE == null) {
            synchronized (XMCarManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new XMCarManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 开始录音
     *
     *  vadTime   取消此参数      录音超时时间设置 最长录制时间
     * @param needPunctuation 标点符号控制设置，是否需要标点（当识别联系人时不需要标点）
     */
    public void startRecordNow(Context context, boolean needPunctuation) {
        Log.d(TAG, "startRecordNow: " + "asrPit =" + needPunctuation);
        this.needPunctuation = needPunctuation;
        remoteIatManager = RemoteIatManager.getInstance();
        remoteIatManager.init(context);
        if (asrListener != null) {
            needRecordCallback = true;
            remoteIatManager.setOnIatListener(new RecordIatListener());
        }
        //vadTime 此参数底层没有使用，无效，直接调用startListeningRecord永久录音即可
        remoteIatManager.startListeningRecord();
    }

    /**
     * 结束录音
     */
    public void finishRecordNow() {
        Log.d(TAG, "finishRecordNow: ");
        remoteIatManager.stopListening();
    }

    /**
     * 取消录音识别
     */
    public void cancelRecordNow() {
        Log.d(TAG, "cancelRecordNow: ");
        needRecordCallback = false;
        remoteIatManager.cancelListening();
        remoteIatManager.setOnIatListener(null);
    }

    public void setASRListener(AsrListener listener) {
        Log.d(TAG, "setASRListener: listener = " + listener);
        asrListener = listener;
    }

    /**
     * 开始播报文字
     *
     * @param id
     * @param speakContent
     */
    public void startTts(Context context, String id, String speakContent) {
        Log.d(TAG, "startTts: id=" + id + ",speakContent=" + speakContent);
        if (TextUtils.isEmpty(speakContent)) return;
        EventTtsManager.getInstance().init(context);
        if (ttsListener == null)
            EventTtsManager.getInstance().startSpeakingByThird(speakContent);
        else
            EventTtsManager.getInstance().startSpeakingByThird(speakContent, new TTSListener(id));
    }

    /**
     * 停止播报文字
     */
    public void stopTts() {
        Log.d(TAG, "stopTts: ");
        EventTtsManager.getInstance().stopSpeaking();
        EventTtsManager.getInstance().destroy();
    }

    /**
     * 播放状态回调
     *
     * @param listener
     */
    public void setTtsListener(TtsListener listener) {
        Log.d(TAG, "setTtsListener: ");
        ttsListener = listener;
    }

    /**
     * 通过蓝牙打电话
     *
     * @param phoneNumber
     */
    public void makeCall(Context context, String phoneNumber) {
        Log.d(TAG, "makeCall: phoneNumber = " + phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) return;
        boolean bindService = XMApi.getInstance().getXMBluetoothPhoneApiManager().bindService();
        if (!bindService) return;
        XMApi.getInstance().getXMBluetoothPhoneApiManager().dial(phoneNumber);
    }

    public boolean hasConnectedBluetoothDevice(Context context) {
        Log.d(TAG, "hasConnectedBluetoothDevice: ");
        boolean bindService = XMApi.getInstance().getXMBluetoothPhoneApiManager().bindService();
        if (!bindService) return false;
        return XMApi.getInstance().getXMBluetoothPhoneApiManager().isBluetoothConnected();
    }

    /**
     * 关键词导航
     *
     * @param keyWords
     */
    public void startNaviByKey(Context context, String keyWords) {
        Log.d(TAG, "startNaviByKey: keyWords = " + keyWords);
        if (TextUtils.isEmpty(keyWords)) return;
        context.sendBroadcast(new Intent(CarWXSDKConstants.START_NAVI_BY_KEY).putExtra("key_words", keyWords));
    }

    /**
     * 通过经纬度导航
     *
     * @param name 地址信息
     * @param lat
     * @param lon
     */
    public void startNaviByPoi(Context context, String name, double lat, double lon) {
        Log.d(TAG, "startNaviByPoi: name = " + name + ",lat = " + lat + ",lon = " + lon);
        if (TextUtils.isEmpty(name)) return;
        context.sendBroadcast(new Intent(CarWXSDKConstants.START_NAVI_BY_POI).putExtra("name", name).putExtra("lat", lat).putExtra("lon", lon));
    }

    public String getVIN(Context context) {
        Log.d(TAG, "getVIN: context = " + context);
        return ConfigManager.DeviceConfig.getVIN(context);
    }

    public String getSerialNumber(Context context) {
        Log.d(TAG, "getSerialNumber: context = " + context);
        return ConfigManager.DeviceConfig.getICCID(context);
    }

    public void uploadContact(List<String> contacts, ClientListener listener) {
        Log.d(TAG, "uploadContact: contacts = " + contacts + ",listener = " + listener);
    }

    public void setSpeedChangeListener(final Context context, final SpeedChangeListener listener) {
        Log.d(TAG, "setSpeedChangeListener: listener = " + listener);
        if (listener == null) return;
        if (speedChangeListener == null) {
            Center.getInstance().init(context);
            Center.getInstance().runAfterConnected(new Runnable() {
                @Override
                public void run() {
                    listenCarInfo(context);
                }
            });
        }
        speedChangeListener = listener;
    }

    public int getCurrentTheme() {
        int curTheme = DEFAULT_THEME_ID;
        SkinInfo skinInfo = SkinUtils.getSkinMsg();
        if (skinInfo != null) {
            String type = skinInfo.skinType;
            if (SkinUtils.TYPE_NAME.equalsIgnoreCase(type)) {
                String name = skinInfo.skinName;
                if (SkinConstants.SKIN_NAME_QINGSHE.equalsIgnoreCase(name)) {
                    curTheme = THEME_ID_QINGSHE;
                } else if (SkinConstants.SKIN_NAME_DAOMENG.equalsIgnoreCase(name)) {
                    curTheme = THEME_ID_DAOMENG;
                }
            } else if (SkinUtils.TYPE_PATH.equalsIgnoreCase(type)) {
                int skinStyle = skinInfo.skinStyle;
                switch (skinStyle) {
                    case THEME_ID_ZHIHUI:
                    case THEME_ID_QINGSHE:
                    case THEME_ID_DAOMENG:
                        curTheme = skinStyle;
                        break;
                }
            }
        }
        Log.e(TAG, String.format("getCurrentTheme -> curTheme: %s, skinInfo: %s",
                curTheme, GsonHelper.toJson(skinInfo)));
        return curTheme;
    }

    public void onServiceUnbind(Context context) {
        if (remoteIatManager != null)
            remoteIatManager.release();
        XMApi.getInstance().init(context);
        XMBluetoothPhoneApiManager xmBluetoothPhoneApiManager = XMApi.getInstance().getXMBluetoothPhoneApiManager();
        if (xmBluetoothPhoneApiManager == null) return;
        xmBluetoothPhoneApiManager.unBindService();
    }

    public void onServiceBind(Context context) {
        initBtService(context);
    }

    private void initBtService(Context context) {
        XMApi.getInstance().init(context);
        boolean bindService = XMApi.getInstance().getXMBluetoothPhoneApiManager().bindService();
        Log.d(TAG, "initBtService: btservice bind success ? " + bindService);
    }

    private class RecordIatListener implements OnIatListener {
        private File mVoiceFile; // 转换为aac格式的音频文件的保存路径
        private long mVoiceDuration; // 音频时长 发送微信语音时显示

        @Override
        public void onComplete(String voiceText, String parseText) {
            Log.d(TAG, "onComplete voiceContent = " + voiceText + ", parseText = " + parseText);
            if (!needRecordCallback) return;
            voiceText = needPunctuation ? voiceText : HandleSpecialWordsUtils.replaceFilter(voiceText);
            onAsrEnd(voiceText, 0);
        }

        @Override
        public void onError(int errorCode) {
            Log.d(TAG, "onError errorCode = " + errorCode);
            if (!needRecordCallback) return;
            // 如果用户没有说话,语音助手那边会回调onError,此时要去取录音文件
            onAsrEnd("", errorCode);
        }

        private void onAsrEnd(final String voiceContent, final int errorCode) {
            Log.d(TAG, "onAsrEnd  voiceContent = " + voiceContent + ", errorCode = " + errorCode);
            if (!needRecordCallback) return;
            if (asrListener != null) {
                ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                    @Override
                    public void run() {
                        final File voiceFile = mVoiceFile;
                        try {
                            if (errorCode == 0) {
                                if (voiceFile != null && voiceFile.exists()) {
                                    Log.d(TAG, "onAsrEnd  voiceContent = " + voiceContent + ", errorCode = " + errorCode);
                                    asrListener.showSrText(voiceFile.getPath(), voiceContent, true);
                                } else {
                                    Log.d(TAG, "onAsrEnd errorCode = " + errorCode);
                                    asrListener.onError(-1);
                                }
                            } else {
                                asrListener.onError(errorCode);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, Priority.HIGH);
            }
        }

        @Override
        public void onWavFileComplete() {
            Log.d(TAG, "onWavFileComplete()");
            if (!needRecordCallback) return;
            ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                @Override
                public void run() {
                    File sourceFile = new File(VrConstants.PCM_RECORD_FILE_PATH);// 语音助手保存的音频路径
                    File cacheDir = new File(Environment.getExternalStorageDirectory(), PATH_VOICE_RECORD);
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    final String dstFileName = "voice_" + System.currentTimeMillis();
                    File dstFile = new File(cacheDir, dstFileName);// 重新拷贝的pcm

                    Log.d(TAG, "onWavFileComplete run Translate format begin ...");
                    // 不转换格式,直接给pcm微信那边自行转换
                    FileUtils.copy(sourceFile, dstFile);
                    /*try {
                        PcmUtil.pcm2Aac(sourceFile, dstFile, RecordConstants.DEFAULT_SAMPLE_RATE, 1);// 转换格式
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    Log.d(TAG, "onWavFileComplete run Translate format end !!!");
                    mVoiceFile = dstFile;
                    Log.d(TAG, "onWavFileComplete run pcmLen = " + sourceFile.length() + ", dstLen = " + dstFile.length());
                    sourceFile.delete();// 转换完成后删除原来的音频
                }
            }, Priority.HIGH);
        }

        @Override
        public void onVolumeChanged(int volume) {
            Log.d(TAG, "onVolumeChanged: volume = " + volume);
            if (!needRecordCallback) return;
            if (asrListener != null) {
                try {
                    asrListener.onVolumeChanged(volume);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onNoSpeaking() {
            Log.d(TAG, "onNoSpeaking()");
            if (!needRecordCallback) return;
        }

        @Override
        public void onResult(String recognizerText, boolean isLast, String currentText) {
            Log.d(TAG, "onResult: recognizerText = " + recognizerText + ", currentText = " + currentText + ", isLast = " + isLast);
            if (!needRecordCallback) return;
            if (asrListener == null) return;
            try {
                asrListener.showSrText("", recognizerText, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRecordComplete() {
            Log.d(TAG, "onRecordComplete()");
            if (!needRecordCallback) return;
        }
    }

    private class TTSListener implements OnTtsListener {
        private String id;

        public TTSListener(String id) {
            this.id = id;
        }

        @Override
        public void onCompleted() {
            if (ttsListener != null) {
                try {
                    ttsListener.onFinish(id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBegin() {
            if (ttsListener != null) {
                try {
                    ttsListener.onStart(id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(int code) {
            if (ttsListener != null) {
                try {
                    ttsListener.onError(id, code);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 开启车速监听
    private void listenCarInfo(final Context context) {
        // 这个代表远程客户端
        final SourceInfo remote = new SourceInfo("com.xiaoma.launcher", 1038);
        // 这个代表本地客户端
        final SourceInfo local = new SourceInfo(context.getPackageName(), 1038);
        // 这个是连接工作
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                // 请求头(指定远程客户端 + 请求action(action=业务,比如这里是监听测速变化的业务))
                RequestHead head = new RequestHead(remote, 1);
                // 请求体(包含本地客户端 + 请求头 + 额外参数)
                Request request = new Request(local, head, null);
                // 通过Linker发起连接,这里使用connect, 另外还有send, request,可以查看Center的readme文档
                int code = Linker.getInstance().connect(request, new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) {
                        Bundle extra = response.getExtra();
                        float carSpeed = extra.getFloat("carSpeed");
                        if (speedChangeListener == null) return;
                        try {
                            speedChangeListener.onSpeedChanged(carSpeed);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                                + "\n * carSpeed: " + carSpeed);
                    }
                });
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * Linker errorCode: " + code);

                // 监听远程客户端活跃状态
                StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                    @Override
                    public void onClientOut(SourceInfo source) {
                        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                                + "\n * 远程客户端退出:" + source);
                        if (source.equals(remote)) {
                            // 当监听到远程客户端断开,重新等待客户端活跃
                            listenCarInfo(context);
                        }
                    }
                });

            }
        };

        // 判断远程客户端是否活跃
        boolean clientAlive = Center.getInstance().isClientAlive(remote);
        if (clientAlive) {
            Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * 远程客户端活跃, 直接发起连接请求");
            // 远程客户端活跃, 直接发起连接请求
            task.run();
        } else {
            Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * 远程客户端不活跃时等待活跃再开始发起连接");
            // 远程客户端不活跃时等待活跃再开始发起连接
            StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                @Override
                public void onClientIn(SourceInfo source) {
                    if (source.equals(remote)) {
                        task.run();
                        StateManager.getInstance().removeCallback(this);
                    }
                }
            });
        }
    }
}
