package com.xiaoma.instructiondistribute.utils.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/7
 */
public class BluetoothControllerAgent {
    private Context context;
    private BluetoothAdapter bltAdapter;
    private BluetoothControllerAgentInterface btControllerAgentInterface;

    public BluetoothControllerAgent(Context context) {
        this.context = context;
        bltAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
    }

    public void initProfile() {
        btControllerAgentInterface.initProfile(context);
    }

    public void setInterface(BluetoothControllerAgentInterface btControllerInterface) {
        btControllerAgentInterface =  btControllerInterface;
    }

    public void onPair(int type, BluetoothDevice device, String pairingKey) {
        btControllerAgentInterface.onPair(type, device, pairingKey);
    }

    public void canBeDiscovered() {
        btControllerAgentInterface.canBeDiscovered();
    }

    public void unableDiscovered() {
        btControllerAgentInterface.unableDiscovered();
    }

    public void removeBond(BluetoothDevice device) {
        btControllerAgentInterface.removeBond(device);
    }

    public void connect(BluetoothDevice device) {
        btControllerAgentInterface.connect(device);
    }

    public void disconnect(BluetoothDevice device) {
        btControllerAgentInterface.disconnect(device);
    }

    public boolean disconnectOtherDevice(BluetoothDevice device) {
        return btControllerAgentInterface.disconnectOtherDevice(device);
    }

    public void onDestroy() {
        btControllerAgentInterface.onDestroy();
    }

    public boolean filterConnection(BluetoothDevice device) {
        return btControllerAgentInterface.filterConnection(device);
    }

    public void filterConnection(BluetoothDevice device, int profile){
        btControllerAgentInterface.filterConnection(device, profile);
    }

    public boolean isBluetoothConnected(BluetoothDevice device) {
        return btControllerAgentInterface.isBluetoothConnected(device);
    }

    public boolean isDeviceDisconnected(BluetoothDevice device){
        return btControllerAgentInterface.isDeviceDisconnected(device);
    }
}
