package com.xiaoma.carwxsdk.manager;

import android.content.Context;
import android.util.Log;

import com.xiaoma.carwxsdk.callback.AsrCallBack;
import com.xiaoma.carwxsdk.callback.CarSpeedChangeCallBack;
import com.xiaoma.carwxsdk.callback.TtsCallBack;
import com.xiaoma.carwxsdk.callback.UploadContactCallBack;

import java.util.List;

public class CarWXManager {
    private static CarWXManager instance;
    private final String TAG = "CarWXManager";

    private CarWXManager() {
    }

    public static CarWXManager getInstance() {
        if (instance == null) {
            synchronized (CarWXManager.class) {
                if (instance == null)
                    instance = new CarWXManager();
            }
        }
        return instance;
    }

    public boolean init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        if (isServiceConnected()) {
            Log.d(TAG, "init: CarWXManager has inited !!!");
            return true;
        }
        return LinkManager.getInstance().init(context.getApplicationContext());
    }

    /**
     * 远程服务是否连接
     */
    public boolean isServiceConnected() {
        boolean linked = LinkManager.getInstance().isLinked();
        Log.d(TAG, "isServiceConnected: " + linked);
        return linked;
    }

    public String getVin() {
        Log.d(TAG, "getVin: ");
        return LinkManager.getInstance().getVin();
    }

    public String getSerialNumber() {
        return LinkManager.getInstance().getSerialNumber();
    }

    public boolean hasConnectedBluetoothDevice() {
        return LinkManager.getInstance().hasConnectedBluetoothDevice();
    }

    public void callPhone(String phoneNumber) {
        LinkManager.getInstance().callPhone(phoneNumber);
    }

    public void startNaviByPoi(String name, double lat, double lon) {
        LinkManager.getInstance().startNaviByPoi(name, lat, lon);
    }

    public void startNaviByKey(String keyWords) {
        LinkManager.getInstance().startNaviByKey(keyWords);
    }

    public void stopTTS() {
        LinkManager.getInstance().stopTTS();
    }

    public void startTTS(String id, String speakCount) {
        LinkManager.getInstance().startTTS(id, speakCount);
    }

    public void setTtsListener(TtsCallBack ttsCallBack) {
        LinkManager.getInstance().setTtsListener(ttsCallBack);
    }

    public void startRecord(boolean needPunctuation) {
        LinkManager.getInstance().startRecord(needPunctuation);
    }

    public void finishRecord() {
        LinkManager.getInstance().finishRecord();
    }

    public void cancelRecord() {
        LinkManager.getInstance().cancelRecord();
    }

    public void setASRListener(AsrCallBack asrCallBack) {
        LinkManager.getInstance().setASRListener(asrCallBack);
    }

    private void uploadContact(List<String> contacts, UploadContactCallBack callback) {
        LinkManager.getInstance().uploadContact(contacts, callback);
    }

    public void setSpeedChangeListener(CarSpeedChangeCallBack callBack) {
        LinkManager.getInstance().setSpeedChangeListener(callBack);
    }

    public int getCurrentTheme() {
        return LinkManager.getInstance().getCurrentTheme();
    }
}
