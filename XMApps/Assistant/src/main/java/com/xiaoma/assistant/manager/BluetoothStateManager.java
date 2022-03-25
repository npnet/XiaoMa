package com.xiaoma.assistant.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaoma.assistant.callback.BluetoothStateListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: iSun
 * @date: 2018/11/28 0028
 */
public class BluetoothStateManager {
    private static BluetoothStateManager instance;
    private final Context mContext;
    private List<BluetoothStateListener> listeners = new CopyOnWriteArrayList<>();

    public static BluetoothStateManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BluetoothStateManager.class) {
                if (instance == null) {
                    instance = new BluetoothStateManager(context);
                }
            }
        }
        return instance;
    }

    private BluetoothStateManager(Context context) {
        this.mContext = context;

        registerReceiver();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_OFF:
                        for (BluetoothStateListener listener : listeners) {
                            listener.onBlueToothDisabled();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                }
            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                for (BluetoothStateListener listener : listeners) {
                    listener.onBlueToothConnected();
                }

            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                for (BluetoothStateListener listener : listeners) {
                    listener.onBlueToothDisConnected();
                }

            } else if (action.equals(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED)) {
                int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                if (extra_state == BluetoothProfile.STATE_CONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onHfpConnected();
                    }
                } else if (extra_state == BluetoothProfile.STATE_DISCONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onHfpDisConnected();
                    }
                }
            } else if (action.equals(BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED)) {
                int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE,-1);
                if (extra_state == BluetoothProfile.STATE_CONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onPbapConnected();
                    }
                } else if (extra_state == BluetoothProfile.STATE_DISCONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onPbapDisconnected();
                    }
                }
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public void addListener(BluetoothStateListener listener) {
        if (receiver == null) {
            registerReceiver();
        }
        listeners.add(listener);
    }

    public void removeListener(BluetoothStateListener listener) {
        listeners.remove(listener);
    }

}
