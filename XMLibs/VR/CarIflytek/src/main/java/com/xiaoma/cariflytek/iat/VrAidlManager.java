package com.xiaoma.cariflytek.iat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.speech.seopt.SeoptHelper;
import com.xiaoma.cariflytek.IVrAidlInterface;
import com.xiaoma.cariflytek.IVrNotifyCallBack;
import com.xiaoma.cariflytek.R;
import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.OnHardWareChange;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IAssistantView;
import com.xiaoma.vr.iat.IIatManager;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.OnShortSrListener;
import com.xiaoma.vr.iat.OnVrStateChangeListener;
import com.xiaoma.vr.ivw.IHandleWakeupWord;
import com.xiaoma.vr.ivw.IIvwManager;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.vr.model.ConfigType;
import com.xiaoma.vr.recorder.BaseRecorder;
import com.xiaoma.vr.recorder.IBufferListener;
import com.xiaoma.vr.recorder.OnRecordListener;
import com.xiaoma.vr.recorder.RecorderManager;
import com.xiaoma.vr.recorder.RecorderType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.xiaoma.vr.VoiceConfigManager.IS_VOICE_WAKEUP_ON;
import static com.xiaoma.vr.VoiceConfigManager.VOICE_WAKEUP_WORD;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/21
 * Desc:Vr 管理类客户端
 */

public class VrAidlManager implements IIvwManager, IIatManager, IHandleWakeupWord, OnRecordListener, SeoptHelper.ISeoptListener, IatAudioProcess.IatProcessListener, VoiceConfigManager.IVoiceConfigChange {
    boolean isSavePcm = false;
    private FileOutputStream mFosIat;
    private int iatCount = 0;
    private String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iflytek/xm/";
    private static final String TAG = VrAidlManager.class.getSimpleName();
    private Context context;
    private IVrAidlInterface mService;
    private static volatile VrAidlManager instance;
    private boolean isDestroyXF = false;
    private boolean isTalkIatRecord = false;
    private boolean isServiceConnected = false;
    private OnIatListener onIatListener;
    private OnHardWareChange onHardWareChange;
    private OnWakeUpListener onOtherWakeUpListener;
    private OnShortSrListener onShortSrListener;
    private RecorderType recorderType = RecorderType.IVW;
    private IBufferListener iBufferListener;
    private boolean oneShotState = false;
    private File fileIat;
    private ArrayList<OnVrStateChangeListener> onVrStateChangeListeners = new ArrayList<>();
    private String currentContactType;
    private String phoneContact;
    private String weChatContact;
    private IAssistantView mAssistantView;
    private String mUid;

    public interface ContactType {
        String Phone = "Phone";
        String WeChat = "WeChat";
    }

    private IVrNotifyCallBack mIIatNotifyCallBack = new IVrNotifyCallBack.Stub() {
        @Override
        public void initIatSuccess() throws RemoteException {

        }

        @Override
        public void onIatVolumeChanged(final int volume) throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null) {
                        onIatListener.onVolumeChanged(volume);
                    }
                }
            });
        }

        @Override
        public void onRecordState(final boolean isComplete) throws RemoteException {
            if (isTalkIatRecord || !VrConfig.useShortTimeSr) {
                stopIatRecordInternal();
                VrAidlManager.getInstance().startWakeup();
            }
            //通知语音助手停止开始识别
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null && isComplete) {
                        onIatListener.onRecordComplete();
                    }
                }
            });
        }

        @Override
        public void onWavFileComplete() throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null) {
                        onIatListener.onWavFileComplete();
                    }
                }
            });

        }

        @Override
        public void onIatResult(final String content) throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null) {
                        onIatListener.onResult(content, true, content);
                    }
                }
            });

        }

        @Override
        public void onIatComplete(final String voiceText, final String parseText) throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null) {
                        onIatListener.onComplete(voiceText, parseText);
                    }
                }
            });

        }

        @Override
        public void onNoSpeaking() throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null) {
                        onIatListener.onNoSpeaking();
                    }
                }
            });

        }

        @Override
        public void onIatError(final int errorCode) throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onIatListener != null) {
                        onIatListener.onError(errorCode);
                    }
                }
            });

        }

        @Override
        public void onListeningForChoose() throws RemoteException {
            startIatRecordInternal();
        }

        @Override
        public String getAllContacts() throws RemoteException {
            switch (currentContactType) {
                case ContactType.Phone:
                default:
                    return phoneContact;
                case ContactType.WeChat:
                    return weChatContact;
            }
        }

        @Override
        public void onHardwareIatTypeChange(int value) throws RemoteException {
            //todo 硬件录音模式切换
        }

        @Override
        public void onOneShotStateChange(boolean isOneShot) throws RemoteException {
            oneShotState = isOneShot;
            for (final OnVrStateChangeListener listener : onVrStateChangeListeners) {
                if (listener != null) {
                    listener.changeToIvwState();
                }
            }
        }

        @Override
        public void onShortTimeSrChange(final boolean isShortSR) throws RemoteException {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (onShortSrListener != null) {
                        onShortSrListener.onShortSrChange(isShortSR);
                    }
                }
            });
        }

        @Override
        public void onWakeUp(final WakeUpInfo info) throws RemoteException {
            KLog.e(TAG, " onWakeUp : " + GsonHelper.toJson(info));
            SeoptHelper.getInstance().setOrientation(info.isLeft());
            IatAudioProcess.getInstance().onWakeUp(info);
            if (onOtherWakeUpListener != null) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        onOtherWakeUpListener.onWakeUp(info.getnMvwScene(), info.getnKeyword(), info.isLeft());
                    }
                });
            }
        }

        @Override
        public void onWakeUpCmd(final String cmdText) throws RemoteException {
            if (onOtherWakeUpListener != null) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        onOtherWakeUpListener.onWakeUpCmd(cmdText);
                    }
                });
            }
        }

        @Override
        public void startWakeup() throws RemoteException {
            VrAidlManager.getInstance().startWakeup();
        }

        @Override
        public void startVwRecord() throws RemoteException {
            RecorderManager.getInstance().startRecord(RecorderType.IVW);
        }

        @Override
        public void stopThread() throws RemoteException {
            RecorderManager.getInstance().stopAudioRecorder();
        }

        @Override
        public void onUpIvw() throws RemoteException {
            stopWakeup();
            stopListening();
            RecorderManager.getInstance().stopAudioRecorder();
        }

        @Override
        public boolean isVrShowing() throws RemoteException {
            if (mAssistantView != null) {
                return mAssistantView.isVrShowing();
            }
            return false;
        }

    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IVrAidlInterface.Stub.asInterface(service);
            try {
                VrAidlManager.getInstance().setUid(mUid);
                mService.registerCallBack(mIIatNotifyCallBack);
                initIat();
                initIvw(onOtherWakeUpListener);
                startWakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                KLog.e("onServiceDisconnected");
                mService.unregisterCallBack(mIIatNotifyCallBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isServiceConnected = false;
        }
    };


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            KLog.e("binderDied");
            if (mService == null) {
                return;
            }
            isServiceConnected = false;
            mService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mService = null;
            if (!isDestroyXF) {
                //重新绑定远程服务
                Intent intent = new Intent(context, VrAidlService.class);
                context.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
            }
        }
    };

    public static VrAidlManager getInstance() {
        if (instance == null) {
            synchronized (VrAidlManager.class) {
                if (instance == null) {
                    instance = new VrAidlManager();
                }
            }
        }
        return instance;
    }

    private VrAidlManager() {
        if (isSavePcm) {
            if (fileIat == null) {
                fileIat = new File(VrConfig.IFLY_TEK_RES + "/iat.pcm");
            }
            try {
                mFosIat = new FileOutputStream(fileIat);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(Context mContext) {
        this.context = mContext.getApplicationContext();
        if (!isServiceConnected) {
            Intent intent = new Intent(context, VrAidlService.class);
            context.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
            IatAudioProcess.getInstance().init(context);
            IatAudioProcess.getInstance().setProcessAudioDataListener(this);
            VoiceConfigManager.getInstance().init(context);
            VoiceConfigManager.getInstance().registerListener(this);
        }
        IntentFilter intentFilter = new IntentFilter("com.xiaoma.cariflytek.destroyXF");
        context.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public void upWakeupOrientation() {
//        SeoptHelper.getInstance().upWakeupOrientation();
    }

    @Override
    public void addAssistantView(IAssistantView view) {
        if (view != null) {
            this.mAssistantView = view;
        }
    }


    public void initIat() {
        if (mService == null) return;
        try {
            SeoptHelper.getInstance().setSeoptListener(this);
            mService.initIat();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void reInitIat() {
        if (mService == null) return;
        try {
            mService.reInitIat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnIatListener(OnIatListener onIatListener) {
        this.onIatListener = onIatListener;
    }


    public boolean getInitIatState() {
        if (mService == null) return false;
        boolean initState = false;
        try {
            initState = mService.getInitIatState();
            KLog.d("initState: " + initState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initState;
    }


    @Override
    public void startListeningNormal() {
        if (isDestroyXF) {
            return;
        }
        try {
            startIatRecordInternal();
            if (mService == null) return;
            //增加返回值 防止oneway异步导致oneshot音频送入失败
            boolean result = mService.startListening();
            handleIatAudioHead();
            //todo 降噪
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stopListening() {
        if (isDestroyXF) {
            return;
        }
        closeIatPcm();
        IatAudioProcess.getInstance().stopIat();
        try {
            stopIatRecordInternal();
            if (mService == null) return;
            mService.stopListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeIatPcm() {
        KLog.d(TAG, " closeIatPcm : ");
        try {
            if (mFosIat != null) {
                mFosIat.close();
                mFosIat = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void appendIatAudioData(byte[] buffer, byte[] leftBuffer, byte[] rightBuffer) {
        try {
            if (isTalkIatRecord) {
                if (iBufferListener != null) {
                    iBufferListener.onBuffer(leftBuffer);
                }
            } else {
                if (mService == null) return;
                Bundle bundle = new Bundle();
                bundle.putByteArray("buffer", buffer);
                mService.appendIatAudioData(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveIatPcm(byte[] leftBuffer, byte[] rightBuffer) {
        try {
            if (isSavePcm) {
                if (mFosIat == null) {
                    mFosIat = new FileOutputStream(new File(logPath + "/iat_vraidl_" + iatCount++ + ".pcm"));
                }
                mFosIat.write(leftBuffer);
                mFosIat.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendIvwAudioData(byte[] buffer) {
        if (mService == null) return;
        try {
            Bundle bundle = new Bundle();
            bundle.putByteArray("buffer", buffer);
            mService.appendIvwAudioData(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendIvwAudioDataV2(byte[] buffer, byte[] leftBuffer, byte[] rightBuffer) {
        if (mService == null) return;
        try {
            Bundle bundle = new Bundle();
            bundle.putByteArray("buffer", buffer);
            bundle.putByteArray("left_buffer", leftBuffer);
            bundle.putByteArray("right_buffer", rightBuffer);
            mService.appendIvwAudioData(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startListeningRecord() {
        if (isDestroyXF) {
            return;
        }
        try {
            startIatRecordInternal();
//            if (mService == null) return;
//            mService.startListeningRecord();
            //调用降噪
            if (onHardWareChange != null) {
                onHardWareChange.onNoiseClean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startListeningRecord(int startTimeOut, int endTimeOut) {
        if (isDestroyXF) {
            return;
        }
        startListeningRecord();
    }

    @Override
    public void startListeningForChoose() {
        if (isDestroyXF) {
            return;
        }
        startListeningForChoose("");
    }

    @Override
    public void startListeningForChoose(String srSceneStkCmd) {
        if (isDestroyXF) {
            return;
        }
        try {
            startIatRecordInternal();
            if (mService == null) return;
            mService.startListeningForChoose(srSceneStkCmd);
            handleIatAudioHead();
            //调用降噪
            if (onHardWareChange != null) {
                onHardWareChange.onNoiseClean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelListening() {
        if (mService == null) return;
        try {
            mService.abandonSrResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUid(String uid) {
        this.mUid = uid;
        if (mService == null) return;
        try {
            mService.setUid(mUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upLoadContact(boolean isPhoneContact, String contacts) {
        if (isPhoneContact) {
            currentContactType = ContactType.Phone;
            phoneContact = contacts;
        } else {
            currentContactType = ContactType.WeChat;
            weChatContact = contacts;
        }
        uploadContacts(currentContactType);
    }

    @Override
    public void uploadAppState(boolean isForeground, String appType) {
        if (mService == null) return;
        try {
            mService.uploadAppState(isForeground, appType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadPlayState(boolean isPlaying, String appType) {
        if (mService == null) return;
        try {
            mService.uploadPlayState(isPlaying, appType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadNaviState(String naviState) {
        if (mService == null) return;
        try {
            mService.uploadNaviState(naviState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void setVrChangeListener(OnVrStateChangeListener onVrStateChangeListener) {
        if (onVrStateChangeListener != null && !onVrStateChangeListeners.contains(onVrStateChangeListener)) {
            onVrStateChangeListeners.add(onVrStateChangeListener);
        }
    }


    public synchronized void removeVrChangeListener(OnVrStateChangeListener onVrStateChangeListener) {
        if (onVrStateChangeListener != null && !onVrStateChangeListeners.contains(onVrStateChangeListener)) {
            onVrStateChangeListeners.remove(onVrStateChangeListener);
        }
    }

    public boolean isInitSuccess() {
        if (mService == null) return false;
        try {
            return mService.isInitSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void uploadContacts(String contactType) {
        if (mService == null) return;
        try {
            mService.uploadContacts(contactType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBufferListener(IBufferListener listener) {
        this.iBufferListener = listener;
    }


    public void startIatRecordInternal() {
        recorderType = RecorderType.IAT;
        IatAudioProcess.getInstance().startIat();
        BaseRecorder r = RecorderManager.getInstance().startRecord(RecorderType.IAT);
        if (r != null) {
            r.addOnRecordListener(this);
        }
    }

    public void handleIatAudioHead() {
        // mService.startListening();一定要在此之后再调用
        IatAudioProcess.getInstance().handleIatAudioData();
    }


    private void stopIatRecordInternal() {
        isTalkIatRecord = false;
        RecorderManager.getInstance().stopRecord();
    }


    public void isTalkShow(boolean isTalkIatRecord) {
        this.isTalkIatRecord = isTalkIatRecord;
    }


    public void setOnWakeUpListener(OnWakeUpListener otherListener) {
        if (otherListener != null) {
            this.onOtherWakeUpListener = otherListener;
        }
    }


    public void initIvw(OnWakeUpListener otherListener) {
        setOnWakeUpListener(otherListener);
        if (mService == null) return;
        try {
            mService.initIvw();
        } catch (Exception e) {
            e.printStackTrace();
        }
        upIvw(VoiceConfigManager.getInstance().isOpenSeopt());
    }

    public void startWakeup() {
        if (isDestroyXF) {
            return;
        }
        if (mService == null) return;
        try {
            startRecorder();
            mService.startWakeUp();
            IatAudioProcess.getInstance().startWakeUp();
            //调用唤醒
            if (onHardWareChange != null) {
                onHardWareChange.onEchoCancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopWakeup() {
        if (mService == null) return;
        try {
            stopRecorder();
            mService.stopWakeUp();
            IatAudioProcess.getInstance().stopWakeUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startRecorder() {
        isTalkIatRecord = false;
        recorderType = RecorderType.IVW;
        BaseRecorder r = RecorderManager.getInstance().startRecord(RecorderType.IVW);
        if (r != null) {
            r.addOnRecordListener(this);
        }
    }

    @Override
    public void stopRecorder() {
        RecorderManager.getInstance().stopRecord(RecorderType.IVW);
    }

    @Override
    public void setIvwThreshold(int curThresh) {

    }

    @Override
    public void setWakeupInterception(boolean interception) {

    }

    public void stopIvwRecorder() {
        if (mService == null) return;
        try {
            mService.stopIvwRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean setWakeupWord(String word) {
        if (mService == null) return false;
        try {
            return mService.setWakeUpWord(word);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean resetWakeupWord() {
        return false;
    }

    @Override
    public List<String> getWakeupWord() {
        if (mService == null) return new ArrayList<>();
        try {
            return mService.getWakeUpWords();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public void setOnHardWareChange(OnHardWareChange onHardWareChange) {
        this.onHardWareChange = onHardWareChange;
    }


    public void setOnShortSrListener(OnShortSrListener listener) {
        this.onShortSrListener = listener;
    }

    @Override
    public void onStart(BaseRecorder recorder) {
        KLog.d(context.getString(R.string.start_recording));
    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, int start, int byteCount) {
        IatAudioProcess.getInstance().addBufferToProcess(buffer, buffer, buffer);
    }

    /**
     * 录制的原始音频
     *
     * @param recorder
     * @param buffer      双声道音频
     * @param leftBuffer  左声道音频
     * @param rightBuffer 右声道音频
     * @param start
     * @param byteCount
     */
    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, byte[] leftBuffer, byte[] rightBuffer, int start, int byteCount) {
        IatAudioProcess.getInstance().addBufferToProcess(buffer, leftBuffer, rightBuffer);
//        if (recorderType == RecorderType.IVW) {
//            appendIvwAudioDataV2(buffer, leftBuffer, rightBuffer);
////            if (VrConfig.useOneShot && oneShotState) {
////                appendIatAudioForSeoptStatus(buffer, leftBuffer, rightBuffer, true);
////            }
//        } else {
//            appendIatAudioData(leftBuffer);
////            appendIatAudioForSeoptStatus(buffer, leftBuffer, rightBuffer, false);
//        }
    }


    @Override
    public void onStop(BaseRecorder recorder) {
        KLog.d(context.getString(R.string.stop_recording));
    }

    @Override
    public void onRecordFailed(BaseRecorder recorder, Exception e) {
        KLog.d(context.getString(R.string.record_exception));
        if (onIatListener != null) {
            onIatListener.onError(-1);
        }
    }


    @Override
    public void release() {
        //startWakeup
    }


    @Override
    public void destroy() {
        context.unbindService(mServiceConnection);
        VoiceConfigManager.getInstance().unregisterListener(this);
    }

    @Override
    public boolean getInitState() {
        if (mService == null) return false;
        try {
            return mService.getInitIatState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean upIvw(boolean isOpenSeopt) {
        if (mService == null) return false;
        try {
            return mService.upIvw(isOpenSeopt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean destroyXF() {
        Log.e("destroy log", " VrAidlManager ");
        if (mService == null) {
            return false;
        }
        if (isDestroyXF) {
            return true;
        }
        isDestroyXF = true;
        destroyAudio();
        try {
            mService.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.unbindService(mServiceConnection);
        return true;
    }

    public boolean destroyAudio() {
        stopWakeup();
        stopListening();
        RecorderManager.getInstance().stopAudioRecorder();
        Log.e(TAG, "xm destroy Audio");
        return true;
    }


    @Override
    public void onSeopt(byte[] buffer, byte[] leftBuffer, byte[] buffer2Mvw) {
        if (recorderType == RecorderType.IVW) {
            appendIvwAudioDataV2(buffer, leftBuffer, buffer2Mvw);
        } else {
            appendIatAudioData(buffer, leftBuffer, buffer2Mvw);
        }
    }

    public RecorderType getRecorderType() {
        return recorderType;
    }

    @Override
    public void onIatHeadProcess(byte[] buffer, byte[] leftBuffer, byte[] rightBuffer) {
        SeoptHelper.getInstance().seoptToSrByHardEnc(leftBuffer, rightBuffer);
    }

    @Override
    public void onConfigChange(ConfigType type) {
        KLog.d("ljb", "VrAidlManager onConfigChange ConfigType:" + type);
        if (type == ConfigType.KEYWORD) { //唤醒词
            String keyWord = XmProperties.build(VoiceConfigManager.getInstance().getUid()).get(VOICE_WAKEUP_WORD, "");
            setWakeupWord(keyWord);
            KLog.d("ljb", "VrAidlManager getWakeupWord" + getWakeupWord());
        } else if (type == ConfigType.WAKEUP_SWITCH) {  //唤醒开关
            boolean switchData = XmProperties.build(VoiceConfigManager.getInstance().getUid()).get(IS_VOICE_WAKEUP_ON, true);
            if (switchData) {
                startWakeup();
            } else {
                stopWakeup();
            }
        } else if (type == ConfigType.VOICE_SWITCH) {
            if (VoiceConfigManager.getInstance().isVoiceFocusSwitch()) {
                startWakeup();
            } else {
                destroyAudio();
            }
        } else if (type == ConfigType.SEOPT) {
            if (mService != null) {
                try {
                    RecorderManager.getInstance().stopAudioRecorder();
                    boolean result = mService.upIvw(VoiceConfigManager.getInstance().isOpenSeopt());
                    if (result) {
                        startWakeup();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLocationInfo(double lat, double lon) {
        if (mService != null) {
            try {
                mService.setLocationInfo(lat, lon);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean ac = intent.getAction().equals("com.xiaoma.cariflytek.destroyXF");
            boolean pack = "com.xiaoma.assistant".equals(intent.getPackage());
            if (ac && pack) {
                boolean b = destroyXF();
                if (b) {
                    Intent destroyXFResultIntent = new Intent();
                    destroyXFResultIntent.setAction("com.xiaoma.carlib.manager.destroyXFResult");
                    destroyXFResultIntent.setPackage(context.getPackageName());
                    context.sendBroadcast(destroyXFResultIntent);
                }
            }
        }
    };

}
