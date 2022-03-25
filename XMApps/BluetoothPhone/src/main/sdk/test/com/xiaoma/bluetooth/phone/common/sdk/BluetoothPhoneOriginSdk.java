package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothDevice;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;

import java.util.List;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/5/21
 * @Desc:
 */
public class BluetoothPhoneOriginSdk implements IBlueToothPhoneManager {
    private static IBlueToothPhoneManager instance;

    private BluetoothPhoneOriginSdk() {
    }

    public static IBlueToothPhoneManager getInstance() {
        if (instance == null) {
            instance = new BluetoothPhoneOriginSdk();
        }
        return instance;
    }

    @Override
    public boolean dial(String phoneNum) {
        return false;
    }

    @Override
    public boolean sendDTMF(Character code) {
        return false;
    }

    @Override
    public boolean acceptCall() {
        return false;
    }

    @Override
    public boolean rejectCall() {
        return false;
    }

    @Override
    public boolean terminateCall() {
        return false;
    }

    @Override
    public boolean holdCall() {
        return false;
    }

    @Override
    public boolean connectAudio() {
        return false;
    }

    @Override
    public boolean disconnectAudio() {
        return false;
    }

    @Override
    public List<ContactBean> getAllContact() {
        return null;
    }

    @Override
    public boolean dialBack() {
        return false;
    }

    @Override
    public boolean redial() {
        return false;
    }

    @Override
    public String getDialBackNumber() {
        return null;
    }

    @Override
    public String getRedialNumber() {
        return null;
    }

    @Override
    public int getAudioState() {
        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean isContactBookSynchronized() {
        return false;
    }

    @Override
    public void synchronizeContactBook(PullPhoneBookResultCallback callback) {

    }

    @Override
    public List<ContactBean> getCallHistory() {
        return null;
    }

    @Override
    public boolean isBluetoothConnected() {
        return false;
    }

    @Override
    public boolean mutePhone() {
        return false;
    }

    @Override
    public int missCallNum() {
        return 0;
    }

    @Override
    public void downloadByType(BlueToothPhoneManagerFactory.PhoneType phoneType) {

    }

    /**
     * 下载所有通讯信息
     */
    @Override
    public void downloadAll(){

    }

    @Override
    public void setPullResultCallback(PullContactbookCallback callback) {

    }

    @Override
    public void pauseHfpRender() {

    }

    @Override
    public void startHfpRender() {

    }

    @Override
    public void stopDownload(String macAddress) {

    }

    @Override
    public boolean isDeviceDisconnected(BluetoothDevice device) {
        return false;
    }

    @Override
    public boolean isDeviceConnected(BluetoothDevice device) {
        return false;
    }
}
