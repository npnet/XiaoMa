package com.xiaoma.carwxsdkimpl.service;

import android.content.Context;
import android.os.RemoteException;

import com.xiaoma.carwxsdk.AsrListener;
import com.xiaoma.carwxsdk.ClientListener;
import com.xiaoma.carwxsdk.SpeedChangeListener;
import com.xiaoma.carwxsdk.TtsListener;
import com.xiaoma.carwxsdk.XMCarInterface;

import java.util.List;

public class CarWXBinder extends XMCarInterface.Stub {
    private Context context;

    public CarWXBinder(Context context) {
        this.context = context;
    }

    @Override
    public void startNaviByPoi(double lat, double lon, String name) throws RemoteException {
        XMCarManager.getInstance().startNaviByPoi(context, name, lat, lon);
    }

    @Override
    public void startNaviByKey(String keyWords) throws RemoteException {
        XMCarManager.getInstance().startNaviByKey(context, keyWords);
    }

    @Override
    public boolean hasConnectedBluetoothDevice() throws RemoteException {
        return XMCarManager.getInstance().hasConnectedBluetoothDevice(context);
    }

    @Override
    public void callPhone(String phoneNumber) throws RemoteException {
        XMCarManager.getInstance().makeCall(context, phoneNumber);
    }

    @Override
    public void startTTS(String id, String speakContent) throws RemoteException {
        XMCarManager.getInstance().startTts(context, id, speakContent);
    }

    @Override
    public void stopTTS() throws RemoteException {
        XMCarManager.getInstance().stopTts();
    }

    @Override
    public void setTTSListener(TtsListener listener) throws RemoteException {
        XMCarManager.getInstance().setTtsListener(listener);
    }

    @Override
    public void startRecord(boolean needPunctuation) throws RemoteException {
        XMCarManager.getInstance().startRecordNow(context, needPunctuation);
    }

    @Override
    public void finishRecord() throws RemoteException {
        XMCarManager.getInstance().finishRecordNow();
    }

    @Override
    public void cancelRecord() throws RemoteException {
        XMCarManager.getInstance().cancelRecordNow();
    }

    @Override
    public void setASRListener(AsrListener listener) throws RemoteException {
        XMCarManager.getInstance().setASRListener(listener);
    }

    @Override
    public void uploadContact(List<String> contacts, ClientListener listener) throws RemoteException {
        XMCarManager.getInstance().uploadContact(contacts, listener);
    }

    @Override
    public void setSpeedChangeListener(SpeedChangeListener listener) throws RemoteException {
        XMCarManager.getInstance().setSpeedChangeListener(context, listener);
    }

    @Override
    public String getCarVin() throws RemoteException {
        return XMCarManager.getInstance().getVIN(context);
    }

    @Override
    public String getSerialNumber() throws RemoteException {
        return XMCarManager.getInstance().getSerialNumber(context);
    }

    @Override
    public int getCurrentTheme() {
        return XMCarManager.getInstance().getCurrentTheme();
    }

    public void onServiceUnbind(Context context) {
        XMCarManager.getInstance().onServiceUnbind(context);
    }

    public void onServiceBind(Context context) {
        XMCarManager.getInstance().onServiceBind(context);
    }
}
