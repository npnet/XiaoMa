package com.xiaoma.bluetooth.phone.common.sdk;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/5/20
 * @Desc:
 */
public class BluetoothAdapterManager {
    private static String TAG = "BluetoothAdapterManager";
    private static BluetoothAdapterManager btAdapterManager;
    private final Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadsetClient mBTHeadSetClient;
    private BluetoothA2dpSink btA2DPSink;
    private BluetoothProfile.ServiceListener mHfpPbaClientProfileService = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            KLog.i(TAG, "onServiceConnected profile:" + profile);
            if (profile == BluetoothProfile.HEADSET_CLIENT) {

                //蓝牙开启且支持该协议，则获取非null协议代理对象
                mBTHeadSetClient = (BluetoothHeadsetClient) proxy;
                if (mBTHeadSetClient != null) {
                    KLog.i(TAG, "BluetoothHeadsetClient agent is not null");
                    /**
                     * 获取已连接HFP远程蓝牙设备列表
                     * 适应场景：蓝牙已开启，HFP已连接成功，之后启动APP
                     */
                    List<BluetoothDevice> deviceList = mBTHeadSetClient.getConnectedDevices();
                    if (!deviceList.isEmpty()) {
                        BluetoothDevice device = deviceList.get(0);
                        String macAddress = device.getAddress();
                        PhoneStateManager.getInstance(context).setMacAddress(macAddress);
                        BluetoothReceiverManager.getInstance().setDeviceData(true, device);
                    }
                } else {
                    KLog.i(TAG, "do not support HFP profile");
                }
            } else if (profile == BluetoothProfile.A2DP_SINK) {
                btA2DPSink = (BluetoothA2dpSink) proxy;
            }
        }

        public void onServiceDisconnected(int profile) {
            //相关协议断开，自动会回调该方法，置null
            KLog.i(TAG, "onServiceDisconnected profile:" + profile);
            if (profile == BluetoothProfile.HEADSET_CLIENT) {
                mBTHeadSetClient = null;
            } else {
                btA2DPSink = null;
            }
        }
    };

    private BluetoothAdapterManager() {
        context = BlueToothPhone.getContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectProfile();
    }

    public static BluetoothAdapterManager getInstance() {
        if (btAdapterManager == null) {
            btAdapterManager = new BluetoothAdapterManager();
        }
        return btAdapterManager;
    }

    public void connectProfile() {
        if (mBluetoothAdapter != null) {
            if (mBTHeadSetClient == null) {
                mBluetoothAdapter.getProfileProxy(context, mHfpPbaClientProfileService, BluetoothProfile.HEADSET_CLIENT);
            }
            if (btA2DPSink == null) {
                mBluetoothAdapter.getProfileProxy(context, mHfpPbaClientProfileService, BluetoothProfile.A2DP_SINK);
            }
        }
    }

    public BluetoothHeadsetClient getBtHeadsetClient(){
        return mBTHeadSetClient;
    }

    public BluetoothA2dpSink getA2DPSink(){
        return btA2DPSink;
    }

    public boolean isBtStateOk() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT) == BluetoothProfile.STATE_CONNECTED) {
                    return true;
                } else {
                    BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
                    if (connectedDevice != null) {
//                        hfpDisConnected();
                        return true;
                    } else {
                        bluetoothDisConnected();
                        return false;
                    }
                }

            } else {
                bluetoothDisabled();
                return false;
            }

        } else {
            bluetoothNonExistent();
            return false;
        }
    }

    boolean getBluetoothStateWithoutTTS() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT) == BluetoothProfile.STATE_CONNECTED;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void bluetoothNonExistent() {
        XMToast.showToast(context, context.getResources().getString(R.string.bluetooth_not_exist));
    }

    private void bluetoothDisabled() {
        XMToast.showToast(context, context.getResources().getString(R.string.bluetooth_closed));
    }

    private void bluetoothDisConnected() {
        XMToast.showToast(context, context.getResources().getString(R.string.bluetooth_disconnected));
    }

    private void hfpDisConnected() {
        XMToast.showToast(context, context.getResources().getString(R.string.hfp_disconnected));
    }
}
