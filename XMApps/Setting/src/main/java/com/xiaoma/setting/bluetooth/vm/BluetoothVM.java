package com.xiaoma.setting.bluetooth.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.setting.bluetooth.adapter.BoundDevicesAdapter;
import com.xiaoma.setting.bluetooth.model.BluetoothDeviceStatusModule;
import com.xiaoma.setting.bluetooth.ui.BluetoothControllerAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/10
 */
public class BluetoothVM extends BaseViewModel {

    private BluetoothAdapter bltAdapter = BluetoothAdapter.getDefaultAdapter();
    private MutableLiveData<List<BluetoothDeviceStatusModule>> boundItems;
    private List<BluetoothDevice> temp = new ArrayList<>();
    private BluetoothControllerAgent bluetoothController;

    public BluetoothVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<BluetoothDeviceStatusModule>> getMatchedDevices() {
        if (boundItems == null) {
            boundItems = new MutableLiveData<>();
        }
        boundItems.setValue(getBoundDevices());
        return boundItems;
    }

    private List<BluetoothDeviceStatusModule> getBoundDevices() {

        List<BluetoothDeviceStatusModule> items = new ArrayList<>();
        Set<BluetoothDevice> set = null;
        if (bltAdapter != null) {
            set = bltAdapter.getBondedDevices();
        }
        /*Iterator<BluetoothDevice> iterator = set.iterator();
        while (iterator.hasNext()) {
            BluetoothDeviceStatusModule module = new BluetoothDeviceStatusModule();
            BluetoothDevice next = iterator.next();
            module.setDevice(next);
            items.add(module);
            if (isConnectedDevice(next)) {
                module.setDeviceStatus(BoundDevicesAdapter.CONNECTED);
            } else {
                module.setDeviceStatus(BoundDevicesAdapter.NOT_CONNECTED);
            }
        }*/
        if (set != null && set.size() != 0) {
            for (BluetoothDevice device : set) {
                BluetoothDeviceStatusModule module = new BluetoothDeviceStatusModule();
                module.setDevice(device);
                if (isConnectedDevice(device)) {
                    module.setDeviceStatus(BoundDevicesAdapter.CONNECTED);
                } else {
                    module.setDeviceStatus(BoundDevicesAdapter.NOT_CONNECTED);
                }
                items.add(module);
            }
        }
        sortItems(items);
        return items;
    }

    private void sortItems(List<BluetoothDeviceStatusModule> items) {
        Collections.sort(items, new Comparator<BluetoothDeviceStatusModule>() {
            @Override
            public int compare(BluetoothDeviceStatusModule o1, BluetoothDeviceStatusModule o2) {
                return o2.getDeviceStatus() - o1.getDeviceStatus();
            }
        });
    }


    private boolean isConnectedDevice(BluetoothDevice device) {
        if (bluetoothController == null) return false;
        return bluetoothController.isBluetoothConnected(device);
    }

    public void refreshDevicesStatus() {
        boundItems.setValue(getBoundDevices());
    }

    public void refreshDevicesStatus(BluetoothControllerAgent bluetoothController){
        this.bluetoothController = bluetoothController;
        refreshDevicesStatus();
    }
}
