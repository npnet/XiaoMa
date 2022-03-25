package com.xiaoma.cariflytek.iat;

import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import com.xiaoma.cariflytek.IVrAidlInterface;
import com.xiaoma.cariflytek.IVrNotifyCallBack;
import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.cariflytek.ivw.LxIvwManager;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/21
 * Desc：Vr 管理类服务端
 */

public class VrAidlServiceManager {

    private static final String TAG = VrAidlServiceManager.class.getSimpleName();
    private static VrAidlServiceManager instance;
    private RemoteCallbackList<IVrNotifyCallBack> mCallBacks = new RemoteCallbackList<>();
    private String mUid;

    private VrAidlServiceManager(){
    }

    public synchronized static VrAidlServiceManager getInstance() {
        if (instance == null) {
            instance = new VrAidlServiceManager();
        }
        return instance;
    }

    public IVrAidlInterface.Stub init(final Context context) {
        return new IVrAidlInterface.Stub() {
            @Override
            public void registerCallBack(IVrNotifyCallBack callBack) throws RemoteException {
                mCallBacks.register(callBack);
            }

            @Override
            public void unregisterCallBack(IVrNotifyCallBack callBack) throws RemoteException {
                mCallBacks.unregister(callBack);
            }

            @Override
            public void initIat() throws RemoteException {
                LxIatManager.getInstance().init(context);
            }

            @Override
            public void reInitIat() throws RemoteException {
                LxIatManager.getInstance().reInit(context);
            }

            @Override
            public boolean startListening() throws RemoteException {
                LxIatManager.getInstance().startListeningNormal();
                return true;
            }

            @Override
            public void startListeningWithTime(int timeOut) throws RemoteException {
                LxIatManager.getInstance().startListeningNormal(timeOut);
            }

            @Override
            public void startListeningRecord() throws RemoteException {
                LxIatManager.getInstance().startListeningRecord();
            }

            @Override
            public void startListeningForChoose(String srSceneStksCmd) throws RemoteException {
                LxIatManager.getInstance().startListeningForChoose(srSceneStksCmd);
            }

            @Override
            public void cancelListening() throws RemoteException {
                LxIatManager.getInstance().cancelListening();
            }

            @Override
            public void stopListening() throws RemoteException {
                LxIatManager.getInstance().stopListening();
            }

            @Override
            public void abandonSrResult() throws RemoteException {
                LxIatManager.getInstance().abandonSrResult();
            }

            @Override
            public void appendIatAudioData(Bundle buffer) throws RemoteException {
                LxIatManager.getInstance().appendAudioData(buffer);
            }

            @Override
            public boolean isInitSuccess() throws RemoteException {
                return LxIatManager.getInstance().isInitSuccess();
            }

            @Override
            public boolean getInitIatState() throws RemoteException {
                return LxIatManager.getInstance().isInitSuccess();
            }

            @Override
            public void uploadContacts(String contactType) throws RemoteException {
                LxIatManager.getInstance().upLoadContact(contactType);
            }

            @Override
            public void uploadAppState(boolean isForeground, String appType) throws RemoteException {
                LxIatManager.getInstance().uploadAppState(isForeground, appType);
            }

            @Override
            public void uploadPlayState(boolean isPlaying, String appType) throws RemoteException {
                LxIatManager.getInstance().uploadPlayState(isPlaying, appType);
            }

            @Override
            public void uploadNaviState(String naviState) throws RemoteException {
                LxIatManager.getInstance().uploadNaviState(naviState);
            }

            @Override
            public void initIvw() throws RemoteException {
                LxIvwManager.getInstance().init(context);
            }

            @Override
            public void startWakeUp() throws RemoteException {
                LxIvwManager.getInstance().startWakeup();
            }

            @Override
            public void stopWakeUp() throws RemoteException {
                LxIvwManager.getInstance().stopWakeup();
            }

            @Override
            public void stopIvwRecorder() throws RemoteException {
                LxIvwManager.getInstance().stopRecorder();
            }

            @Override
            public void appendIvwAudioData(Bundle buffer) throws RemoteException {
                LxIvwManager.getInstance().appendAudioData(buffer);
            }

            @Override
            public boolean setWakeUpWord(String word) throws RemoteException {
                return LxIvwManager.getInstance().setWakeupWord(word);
            }

            @Override
            public List<String> getWakeUpWords() throws RemoteException {
                return LxIvwManager.getInstance().getWakeupWord();
            }

            @Override
            public boolean registerOneShotWakeupWord(List<String> wakeupWord) throws RemoteException {
                return LxIvwManager.getInstance().registerOneShotWakeupWord(wakeupWord);
            }

            @Override
            public boolean unregisterOneShotWakeupWord(List<String> wakeupWord) throws RemoteException {
                return LxIvwManager.getInstance().unregisterOneShotWakeupWord(wakeupWord);
            }

            @Override
            public boolean destroy() throws RemoteException {
                LxIatManager.getInstance().destroySr();
                LxIvwManager.getInstance().destroyIvw();
                Process.killProcess(Process.myPid());
                return true;
            }

            @Override
            public boolean upIvw(boolean isOpenSeopt) throws RemoteException {
                //更新定向识别依赖
                KLog.d(TAG, " upIvw : " + isOpenSeopt);
                VrAidlServiceManager.getInstance().onUpIvw();
                LxIatManager.getInstance().upSeopt(isOpenSeopt);
                LxIvwManager.getInstance().upSeopt(isOpenSeopt);
                LxIvwManager.getInstance().onConfigChange();
                return true;
            }

            @Override
            public void setLocationInfo(double lat, double lon) throws RemoteException {
                LxIatManager.getInstance().setLocationInfo(lat, lon);
            }

            @Override
            public void setUid(String uid) throws RemoteException {
                mUid = uid;
            }

        };
    }


    public void kill() {
        //mCallBacks.kill();
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public synchronized void initIatSuccess() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    mCallBacks.getBroadcastItem(i).initIatSuccess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onVolumeChanged(int volume) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onIatVolumeChanged(volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }

    public synchronized void onUpIvw() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onUpIvw();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onRecordState(boolean isRecordComplete) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onRecordState(isRecordComplete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onWaveFileComplete() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onWavFileComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onIatResult(String content) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onIatResult(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onComplete(String voiceText, String parseText) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onIatComplete(voiceText, parseText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onNoSpeaking() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onNoSpeaking();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onError(int errorCode) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onIatError(errorCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onListeningForChoose() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onListeningForChoose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }

    public synchronized boolean isVrShowing() {
        boolean isShowing = false;
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    isShowing = mCallBacks.getBroadcastItem(i).isVrShowing();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
        return isShowing;
    }


    public synchronized String getAllContacts() {
        String contacts = "";
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    contacts = mCallBacks.getBroadcastItem(i).getAllContacts();
                    if (!TextUtils.isEmpty(contacts)) {
                        break;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
        return contacts;
    }


    public synchronized void onHardwareIatTypeChange(int value) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onHardwareIatTypeChange(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onOneShotStateChange(boolean isOneShot) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    mCallBacks.getBroadcastItem(i).onOneShotStateChange(isOneShot);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onWakeUp(WakeUpInfo info) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onWakeUp(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onWakeUpCmd(String cmdText) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onWakeUpCmd(cmdText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void startWakeup() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    mCallBacks.getBroadcastItem(i).startWakeup();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void startVwRecord() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).startVwRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }

    public synchronized void stopThread() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).stopThread();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }


    public synchronized void onShortTimeSrChange(boolean isInShortSr) {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                mCallBacks.getBroadcastItem(i).onShortTimeSrChange(isInShortSr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallBacks.finishBroadcast();
        }
    }
}
