package com.xiaoma.bluetooth.phone.common.manager;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.bluetooth.phone.common.listener.BluetoothStateListener;
import com.xiaoma.utils.log.KLog;

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
    private BlueToothReceiver receiver;

    public static BluetoothStateManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BluetoothStateManager.class) {
                if (instance == null) {
                    instance = new BluetoothStateManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private BluetoothStateManager(Context context) {
        this.mContext = context;

        registerReceiver();
    }

    class BlueToothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_OFF:
                        PhoneStateManager.getInstance(mContext).clearState();
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
                PhoneStateManager.getInstance(mContext).clearState();
                for (BluetoothStateListener listener : listeners) {
                    listener.onBlueToothDisConnected(null);
                }

            } else if (action.equals(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED)) {
                int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (extra_state == BluetoothProfile.STATE_CONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onHfpConnected(device);
                    }
                } else if (extra_state == BluetoothProfile.STATE_DISCONNECTED) {
                    // hfp断开连接,反注册方控
                    PhoneStateManager.getInstance(mContext).clearState();
                    WheelOperatePhoneManager.getInstance().unregisterAllCarLibListener();
                    for (BluetoothStateListener listener : listeners) {
                        listener.onHfpDisConnected(device);
                    }
                }
            } else if (action.equals(BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED)) {
                int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                if (extra_state == BluetoothProfile.STATE_CONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onPbapConnected();
                    }
                } else if (extra_state == BluetoothProfile.STATE_DISCONNECTED) {
                    for (BluetoothStateListener listener : listeners) {
                        listener.onPbapDisconnected();
                    }
                }
            } else if (action.equals(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED)) {
                int extra_state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (extra_state == BluetoothProfile.STATE_CONNECTED) {
                    KLog.d("hzx", "a2dp连接上,设备进行名: " + device.getName());
                    for (BluetoothStateListener listener : listeners) {
                        listener.onA2dpConnected(device);
                    }
                } else if (extra_state == BluetoothProfile.STATE_DISCONNECTED) {
                    KLog.d("hzx", "a2dp断开, 设备名: " + device.getName());
                    for (BluetoothStateListener listener : listeners) {
                        listener.onA2dpDisconnected(device);
                    }
                }
            }
        }
    }

    private void registerReceiver() {
        receiver = new BlueToothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        mContext.getApplicationContext().registerReceiver(receiver, filter);
    }

    public void unregisterReceiver() {
        if (receiver != null) {
            mContext.getApplicationContext().unregisterReceiver(receiver);
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
