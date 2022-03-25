package com.xiaoma.carlib.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * @author: iSun
 * @date: 2018/12/26 0026
 */
public class XmSystemManager implements ISystem {

    private static XmSystemManager instance;
    private Context context;

    public static XmSystemManager getInstance() {
        if (instance == null) {
            synchronized (XmSystemManager.class) {
                if (instance == null) {
                    instance = new XmSystemManager();
                }
            }
        }
        return instance;
    }

    private XmSystemManager() {
    }

    public void init(Context context) {
        this.context = context;
    }

    @Override
    public boolean getBlueToothStatus() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.isEnabled();
    }

    @Nullable
    private BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter adapter = null;
        if (context != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                adapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                adapter = bluetoothManager.getAdapter();
            }
        }
        return adapter;
    }

    @Override
    public boolean setBlueToothStatus(boolean status) {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter == null) {
            return false;
        } else if (status) {
            return adapter.enable();
        } else {
            return adapter.disable();
        }
    }

    @Override
    public boolean getWIfiStatus() {
        return false;
    }

    @Override
    public void setWIfiStatus(boolean status) {

    }

    @Override
    public int setDataSwitch(boolean status) {
        return 0;
    }

    @Override
    public int setWorkPattern(int status) {
        return 0;
    }

    @Override
    public void getWorkPattern() {

    }

    @Override
    public int operateTelePhoneICall(int operation) {
        return 0;
    }

    @Override
    public int operateTelePhoneBCall(int operation) {
        return 0;
    }

    @Override
    public int hangUpICall() {
        return 0;
    }

    @Override
    public int hangUpBCall() {
        return 0;
    }

    @Override
    public void scanWifiList() {

    }

    @Override
    public void getWifiList() {

    }

    @Override
    public void operateConnectedWifi(int op, String ssid) {

    }

    @Override
    public int connectWifi(int op, int auto, Object tBoxHotSpot) {
        return 0;
    }

    @Override
    public int setHotSpot(Object tBoxHotSpot) {
        return 0;
    }

    @Override
    public int setDataTrafficThreshold(String thresold) {
        return 0;
    }

    @Override
    public void getWifiConnectStatus() {

    }

    @Override
    public void getCellulatData() {

    }

    @Override
    public void getHotSpot() {

    }

    public boolean noUpdate() {
        return true;
    }

    public void setUpdateListener(IupDateListener updateListener) {

    }


    public interface IupDateListener {
        public void onUpdateChange(boolean noUpdate);
    }


}
