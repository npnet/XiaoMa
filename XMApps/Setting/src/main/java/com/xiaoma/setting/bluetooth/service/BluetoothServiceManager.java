package com.xiaoma.setting.bluetooth.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.xiaoma.setting.Setting;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @author: iSun
 * @date: 2019/8/12 0012
 */
public class BluetoothServiceManager {
    private static BluetoothServiceManager instance;
    private boolean isConn = false;
    private List<ServiceConnection> connections = new ArrayList<>();
    private IBinder mService;
    private ComponentName mComponentName;
    private BluetoothAdapter adapter;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConn = true;
            mService = service;
            mComponentName = name;
            onConnected(name, service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConn = false;
            mService = null;
            mComponentName = null;
            onDisconnected(name);

        }
    };

    private void onConnected(ComponentName name, IBinder service) {
        for (ServiceConnection connection : connections) {
            connection.onServiceConnected(name, service);
        }
    }

    private void onDisconnected(ComponentName name) {
        for (ServiceConnection connection : connections) {
            connection.onServiceDisconnected(name);
        }
    }

    public static BluetoothServiceManager getInstance() {
        if (instance == null) {
            synchronized (BluetoothServiceManager.class) {
                if (instance == null) {
                    instance = new BluetoothServiceManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, BluetoothService.class);
            context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }


    public void addServiceConnection(ServiceConnection connection) {
        if (isConn && connection != null) {
            connection.onServiceConnected(mComponentName, mService);
        }
        connections.add(connection);
    }

    public void removeServiceConnection(ServiceConnection connection) {
        connections.remove(connection);
    }

    public void removeServiceConnection(ServiceConnection connection, boolean isDisconnected) {
        if (isDisconnected && connection != null) {
            connection.onServiceDisconnected(null);
        }
        connections.remove(connection);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        if (adapter == null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                adapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                final BluetoothManager bluetoothManager = (BluetoothManager) Setting.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
                adapter = bluetoothManager.getAdapter();
            }
        }
        return adapter;
    }


}
