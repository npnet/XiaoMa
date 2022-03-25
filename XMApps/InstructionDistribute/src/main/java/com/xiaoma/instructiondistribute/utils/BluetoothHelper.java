package com.xiaoma.instructiondistribute.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.xiaoma.instructiondistribute.utils.bluetooth.BluetoothControllerAgent;
import com.xiaoma.instructiondistribute.utils.bluetooth.BluetoothControllerFactory;

import java.util.Set;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/7/24
 * @Desc:
 */
public class BluetoothHelper {

    private static BluetoothHelper sHelper;
    private BluetoothAdapter bltAdapter;
    private Context mAppContext;
    private BluetoothControllerAgent bluetoothControllerAgent;

    private BluetoothHelper() {

    }

    public static BluetoothHelper newSingleton() {
        if (sHelper == null) {
            synchronized (BluetoothHelper.class) {
                if (sHelper == null) {
                    sHelper = new BluetoothHelper();
                }
            }
        }
        return sHelper;
    }

    public void init(Context context) {
        mAppContext = context.getApplicationContext();
        bltAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothControllerAgent = BluetoothControllerFactory.getInstance().getBluetoothControllerAgent(mAppContext);

    }

    public boolean connectWithMac(String mac) {
        Set<BluetoothDevice> set = bltAdapter.getBondedDevices();
        if (set != null && set.size() != 0) {
            for (BluetoothDevice device : set) {
                if (device == null) {
                    continue;
                }

                if (mac.equalsIgnoreCase(device.getAddress())) {
                    if (!device.isConnected()) {
                        bluetoothControllerAgent.connect(device);
                    }
                    return true;
                }
            }
        }
        return false;
    }
//
//    private List<BluetoothDeviceStatusModule> getBoundDevices() {
//
//        List<BluetoothDeviceStatusModule> items = new ArrayList<>();
//        Set<BluetoothDevice> set = null;
//        if (bltAdapter != null) {
//            set = bltAdapter.getBondedDevices();
//        }
//        /*Iterator<BluetoothDevice> iterator = set.iterator();
//        while (iterator.hasNext()) {
//            BluetoothDeviceStatusModule module = new BluetoothDeviceStatusModule();
//            BluetoothDevice next = iterator.next();
//            module.setDevice(next);
//            items.add(module);
//            if (isConnectedDevice(next)) {
//                module.setDeviceStatus(BoundDevicesAdapter.CONNECTED);
//            } else {
//                module.setDeviceStatus(BoundDevicesAdapter.NOT_CONNECTED);
//            }
//        }*/
//        if (set != null && set.size() != 0) {
//            for (BluetoothDevice device : set) {
//                BluetoothDeviceStatusModule module = new BluetoothDeviceStatusModule();
//                module.setDevice(device);
//                if (isConnectedDevice(device)) {
//                    module.setDeviceStatus(BoundDevicesAdapter.CONNECTED);
//                } else {
//                    module.setDeviceStatus(BoundDevicesAdapter.NOT_CONNECTED);
//                }
//                items.add(module);
//            }
//        }
//        sortItems(items);
//        return items;
//    }
}
