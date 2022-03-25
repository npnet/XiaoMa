package com.xiaoma.setting.bluetooth.Factory;

import android.content.Context;

import com.xiaoma.setting.bluetooth.ui.BluetoothControllerAgent;
import com.xiaoma.setting.sdk.controller.BluetoothController;
import com.xiaoma.setting.sdk.controller.BluetoothPairController;


/**
 * @Author ZiXu Huang
 * @Data 2019/1/7
 */
public class BluetoothControllerFactory {
    private static BluetoothControllerFactory instance;
    private BluetoothControllerAgent bluetoothControllerAgent;
    private BluetoothPairController bluetoothPairController;

    private BluetoothControllerFactory() {
    }

    public static BluetoothControllerFactory getInstance() {
        if (instance == null) {
            synchronized (BluetoothControllerFactory.class) {
                if (instance == null) {
                    instance = new BluetoothControllerFactory();
                }
            }
        }
        return instance;
    }

    public BluetoothControllerAgent getBluetoothControllerAgent(Context context) {
        if (bluetoothControllerAgent == null) {
            bluetoothControllerAgent = new BluetoothControllerAgent(context);
        }
        setInterfaceAndInit(context);
        return bluetoothControllerAgent;
    }

    private void setInterfaceAndInit(Context context) {
        BluetoothController bluetoothController = new BluetoothController(context);
        bluetoothControllerAgent.setInterface(bluetoothController);
        bluetoothControllerAgent.initProfile();
    }

    public BluetoothPairController getBluetoothPairController() {
        if (bluetoothPairController == null) {
            bluetoothPairController = new BluetoothPairController();
        }
        return bluetoothPairController;
    }
}
