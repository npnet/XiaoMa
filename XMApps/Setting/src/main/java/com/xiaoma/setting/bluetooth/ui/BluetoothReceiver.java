package com.xiaoma.setting.bluetooth.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.utils.log.KLog;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/29
 */
public class BluetoothReceiver extends BroadcastReceiver {
    private Context context;
    private BluetoothStateCallback callback;
    private BluetoothTurnOnStateCallBack turnOnCallback;

    public BluetoothReceiver(Context context, BluetoothStateCallback connectStateCallback, BluetoothTurnOnStateCallBack turnOnCallback) {
        this.context = context;
        this.callback = connectStateCallback;
        this.turnOnCallback = turnOnCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KLog.d(action);
        int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
        int preState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, -1);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device != null) {
            KLog.d("hzx", "Bluetooth status change: " + action + ", preState: " + preState + ". curState: " + state + ", device name: " + device.getName());
        }
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            int deviceState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            KLog.d("hzx", "boned device state: " + deviceState);
            switch (deviceState) {
                case BOND_NONE:
                    KLog.d(context.getString(R.string.log_blt_unbond));
                    int intExtra = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                    if (intExtra == BOND_BONDING) {
                        callback.onPairingFailed();
                    } else if (intExtra == BOND_BONDED) {
                        callback.onDeleteBondedDevice();
                    }
                    break;
                case BOND_BONDING:
                    KLog.d(context.getString(R.string.log_blt_bonding));
                    break;
                case BOND_BONDED:
                    KLog.d(context.getString(R.string.log_blt_bonded));
                    callback.onBltBonded(device);
                    break;

            }
        } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
            KLog.d("hzx", "配对请求");
            callback.onPairRequested(intent);
        } else if (action == SettingConstants.BT_A2DP_SINK_ACTION_CONNECTION_STATE_CHANGED) {
//            KLog.d("hzx","action: " + action + ", preState: " + preState + ". curState: " + state + ", device name: " + device.getName());
            if (state == BluetoothProfile.STATE_CONNECTED) {
                callback.onBtA2dpSinkConnected(device, true);
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                callback.onBtDisconnected(device, preState == BluetoothProfile.STATE_CONNECTED);
            }
        } else if (action == SettingConstants.BT_HEADSETCLINET_ACTION_CONNECTION_STATE_CHANGED) {
//            KLog.d("hzx", "action: " + action + ", preState: " + preState + ". curState: " + state + ", device name: " + device.getName());
            if (state == BluetoothProfile.STATE_CONNECTED) {
                callback.onBtHeadSetClientConnected(device);
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                callback.onBtDisconnected(device, preState == BluetoothProfile.STATE_CONNECTED);
            }
        } else if (action == SettingConstants.BT_PBAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED) {
//            KLog.d("hzx", "action: " + action + ", preState: " + preState + ". curState: " + state + ", device name: " + device.getName());
            if (state == BluetoothProfile.STATE_CONNECTED) {
                callback.onBtPbapClientConnected(device);
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                callback.onBtDisconnected(device, false);
            }
        } else if (action == SettingConstants.BT_MAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED) {
//            KLog.d("hzx", "action: " + action + ", preState: " + preState + ". curState: " + state + ", device name: " + device.getName());
            if (state == BluetoothProfile.STATE_CONNECTED) {
                callback.onBtMapClientConnected(device);
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                callback.onBtDisconnected(device, false);
            }
        } else if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            int btAdapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            int btAdapterPreState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
            if (btAdapterPreState == BluetoothAdapter.STATE_TURNING_ON && btAdapterState == BluetoothAdapter.STATE_ON) {
                turnOnCallback.onBltTurnOnSuccess();
            }else if (btAdapterPreState == BluetoothAdapter.STATE_TURNING_OFF && btAdapterState == BluetoothAdapter.STATE_OFF){
                turnOnCallback.onBltClose();
            } else{
                if (btAdapterPreState == BluetoothAdapter.STATE_TURNING_ON && btAdapterState == BluetoothAdapter.STATE_OFF) {
                    turnOnCallback.onBltTurnOnFailed();
                }
            }
        } else {
//            KLog.d("hzx","Bluetooth status change: " + action + ", preState: " + preState + ". curState: " + state + ", device name: " + device.getName());
        }
    }

    public interface BluetoothStateCallback {
        void onBtDisconnected(BluetoothDevice device, boolean isShowDisconnectDialog);

        void onBtA2dpSinkConnected(BluetoothDevice device, boolean isShowConnectDialog);

        void onBtHeadSetClientConnected(BluetoothDevice device);

        void onBtPbapClientConnected(BluetoothDevice device);

        void onBtMapClientConnected(BluetoothDevice device);

        void onBltBonded(BluetoothDevice device);

        void onDeleteBondedDevice();

        void onPairingFailed();

        void onPairRequested(Intent intent);
    }

    public interface BluetoothTurnOnStateCallBack {
        void onBltTurnOnSuccess();

        void onBltClose();

        void onBltTurnOnFailed();
    }
}
