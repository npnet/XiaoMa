package com.xiaoma.setting.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.setting.R;
import com.xiaoma.setting.Setting;
import com.xiaoma.setting.bluetooth.Factory.BluetoothControllerFactory;
import com.xiaoma.setting.bluetooth.btinterface.BluetoothConnectStateInterface;
import com.xiaoma.setting.bluetooth.btinterface.BluetoothServiceInterface;
import com.xiaoma.setting.bluetooth.btinterface.BluetoothStateInterface;
import com.xiaoma.setting.bluetooth.ui.BluetoothControllerAgent;
import com.xiaoma.setting.bluetooth.ui.BluetoothReceiver;
import com.xiaoma.setting.bluetooth.ui.DialogDispatch;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.screentool.ScreenControlUtil;

import static com.xiaoma.setting.common.constant.SettingConstants.BT_A2DP_SINK_ACTION_CONNECTION_STATE_CHANGED;
import static com.xiaoma.setting.common.constant.SettingConstants.BT_AVRCP_CONTROLLER_ACTION_CONNECTION_STATE_CHANGED;
import static com.xiaoma.setting.common.constant.SettingConstants.BT_HEADSETCLINET_ACTION_CONNECTION_STATE_CHANGED;
import static com.xiaoma.setting.common.constant.SettingConstants.BT_MAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED;
import static com.xiaoma.setting.common.constant.SettingConstants.BT_PBAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/24
 */
public class BluetoothService extends Service implements BluetoothReceiver.BluetoothStateCallback,
        BluetoothReceiver.BluetoothTurnOnStateCallBack, DialogDispatch.DialogButtonClickedListener, XmCarVendorExtensionManager.ValueChangeListener {

    private BluetoothReceiver bluetoothReceiver;
    private BluetoothStateInterface btState;
    private BluetoothConnectStateInterface btConnectState;
    private DialogDispatch dialogDispatch = new DialogDispatch();
    private BluetoothDevice device;
    private String pairingKey;
    private BluetoothControllerAgent bluetoothController;
    private BluetoothServiceInterface btServiceInterface;
    private static volatile boolean receiverCanUnRegister = false;
    public static final int PBAP_CLIENT = 17;
    public static final int MAP_CLIENT = 18;
    public BluetoothAdapter bltAdapter = BluetoothServiceManager.getInstance().getBluetoothAdapter();
    private BluetoothDevice connectedDevice;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("hzx", "onBind");
        return new BluetoothBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d("hzx", "onCreate");
        bluetoothController = BluetoothControllerFactory.getInstance().getBluetoothControllerAgent(this);

        bluetoothReceiver = new BluetoothReceiver(this, this, this);
        dialogDispatch.setOnDialogButtonClicked(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BT_A2DP_SINK_ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BT_HEADSETCLINET_ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BT_MAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BT_AVRCP_CONTROLLER_ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BT_PBAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, intentFilter);
        receiverCanUnRegister = true;

        XmCarVendorExtensionManager.getInstance().addValueChangeListener(this);
    }

    public BluetoothControllerAgent getBluetoothController() {
        return bluetoothController;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOnBtStateCallback(BluetoothStateInterface stateInterface) {
        btState = stateInterface;
    }

    public void setOnBtConnectStateCallback(BluetoothConnectStateInterface connectStateCallback) {
        btConnectState = connectStateCallback;
    }

    public void setBtPairInterface(BluetoothServiceInterface btPairInterface) {
        this.btServiceInterface = btPairInterface;
    }

    @Override
    public void onBtDisconnected(BluetoothDevice device, boolean isShowDisconnectDialog) {
        if (isShowDisconnectDialog) {
//            dialogDispatch.showAutoDismissDialog(this, R.string.disconnect_blt_success, R.drawable.icon_success);
            if (bluetoothController.isDeviceDisconnected(device)) {
                XMToast.toastSuccess(Setting.getContext(), R.string.disconnect_blt_success, false);
            }
        }
        if (btConnectState != null && bluetoothController.isDeviceDisconnected(device)) {
            btConnectState.onBtDisconnected(device, isShowDisconnectDialog);
        }
    }

    @Override
    public void onBtA2dpSinkConnected(BluetoothDevice device, boolean isShowConnectDialog) {
        ScreenControlUtil.sendTurnOnScreenBroadCast(getApplicationContext());
        if (bluetoothController.filterConnection(device) && bluetoothController.isFirstProfileConnected(device))
//            dialogDispatch.showAutoDismissDialog(this, R.string.blt_connect_success, R.drawable.icon_success);
            XMToast.toastSuccess(Setting.getContext(), R.string.blt_connect_success, false);
        if (btConnectState != null)
            btConnectState.onBtConnected(device, isShowConnectDialog);
    }

    @Override
    public void onBtHeadSetClientConnected(BluetoothDevice device) {
        if (bluetoothController.filterConnection(device) && bluetoothController.isFirstProfileConnected(device)) {
            XMToast.toastSuccess(Setting.getContext(), R.string.blt_connect_success, false);
        }
        if (btConnectState != null) {
            btConnectState.onBtConnected(device, true);
        }
    }

    @Override
    public void onBtPbapClientConnected(BluetoothDevice device) {
        if (btConnectState == null) {
            return;
        }
        bluetoothController.filterConnection(device, PBAP_CLIENT);
    }

    @Override
    public void onBtMapClientConnected(BluetoothDevice device) {
        if (btConnectState == null) {
            return;
        }
        bluetoothController.filterConnection(device, MAP_CLIENT);
    }

    @Override
    public void onBltBonded(BluetoothDevice device) {
        dialogDispatch.dismissInPairingDialog();
        if (btState != null)
            btState.onBtBonded(device);
    }

    @Override
    public void onDeleteBondedDevice() {
        if (btConnectState == null) {
            return;
        }
        btConnectState.onDeleteBondedDevice();
    }

    @Override
    public void onPairingFailed() {
        if (btConnectState == null) {
            return;
        }
        dialogDispatch.dismissConfirmDialog();
        btConnectState.onPairingFailed();
    }

    @Override
    public void onPairRequested(Intent intent) {
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR) + "";
        dialogDispatch.showPairDialog(Setting.getContext(), intent);
    }

    @Override
    public void onBltTurnOnSuccess() {
        if (btState != null)
            btState.onBtTurnOnSuccess();
    }

    @Override
    public void onBltClose() {
        if (btState != null)
            btState.onBtClose();
    }

    @Override
    public void onBltTurnOnFailed() {
        if (btState != null)
            btState.onBtTurnOnFailed();
    }

    @Override
    public void onPositiveButtonClicked(int type) {
        if (btState != null)
            btState.onPairingConfirm(type, device, pairingKey);
    }

    @Override
    public void onNegativeButtonClicked() {
        if (device != null) {
//            device.cancelPairingUserInput();
            btServiceInterface.cancelPairingUserInput(device);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d("hzx", "onUnbind");
        if (bluetoothController != null) {
            bluetoothController.onDestroy();
        }
        unregisterReceiver(bluetoothReceiver);
        return super.onUnbind(intent);
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.ID_WORK_MODE_STATUS) {
            Log.d("hzx", "acc 信号： " + value);
            int result = (int) value;
            if (result == SDKConstants.WorkMode.STANDBY) {
                connectedDevice = bluetoothController.getConnectedDevice();
                bluetoothController.disconnectAll();
            } else if (result == SDKConstants.WorkMode.NORMAL) {
                if (connectedDevice != null) {
                    bluetoothController.connect(connectedDevice);
                }
            }
        }
    }

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
}
