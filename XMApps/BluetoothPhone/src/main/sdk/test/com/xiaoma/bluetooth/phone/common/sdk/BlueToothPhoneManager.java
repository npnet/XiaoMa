package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019-01-07 16:26
 */
public class BlueToothPhoneManager implements IBlueToothPhoneManager {
    public enum PhoneType{
        CONTACT,
        RECEIVER,
        MISS,
        DIAL,
        Logs
    }

    public static final String TAG = "BlueToothPhoneManager";

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
    public int getAudioState() {
        return BluetoothHeadsetClient.STATE_AUDIO_DISCONNECTED;
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
        return new ArrayList<>();
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
        return "";
    }

    @Override
    public String getRedialNumber() {
        return "";
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

    public void downloadByType(BlueToothPhoneManagerFactory.PhoneType phoneType){

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
