package com.xiaoma.bluetooth.phone.main.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.BluetoothStateListener;
import com.xiaoma.bluetooth.phone.common.listener.PhoneStateChangeListener;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothStateManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.utils.OpenAppUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.utils.RouteUtils;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

/**
 * @author: iSun
 * @date: 2018/11/19 0019
 */
public abstract class BaseBluetoothFragment extends VisibleFragment implements BluetoothStateListener {

    public static final String TAG = "BaseBluetoothFragment";
    private PhoneStateChangeListener phoneListener;
    private View tipsView;
    public MainActivity activity;
    private Handler handler = new Handler();
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        context = activity;
        init();
    }

    public void init() {
        tipsView = View.inflate(getContext(), R.layout.view_tips, null);
        phoneListener = new PhoneStateChangeListener() {
            @Override
            public void onPhoneStateChange(ContactBean bean, State state) {
                onPhoneDataChang(bean, state);
            }
        };
        if (needPhoneStateCallback()) {
            PhoneStateManager.getInstance(getContext()).addPhoneListener(phoneListener);
        }
        BluetoothStateManager.getInstance(getContext()).addListener(this);
        EventBus.getDefault().register(this);
    }

    //PhoneManager状态改变
    protected void onPhoneDataChang(ContactBean bean, State state){

    }

    private void unregister() {
        if (needPhoneStateCallback()) {
            PhoneStateManager.getInstance(getContext()).removePhoneListener(phoneListener);
        }
        BluetoothStateManager.getInstance(getContext()).removeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregister();
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RouteUtils.REQUEST_FLOAT_WINDOW_PERMISSION_CODE) {
            if (!Settings.canDrawOverlays(getActivity())) {
                XMToast.showToast(getActivity(), R.string.system_alert_window_permission_reject);
            } else {
                RouteUtils.startWindowServiceDelayed(getActivity(), handler);
            }
        }
    }

    public void handleHangUpPage() {
        PhoneStateManager.getInstance(mContext).setIsHangUp(true);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PhoneStateManager.getInstance(mContext).setIsHangUp(false);
                KLog.d("hzx","showPageForPhone");
                activity.showPageForPhone();
            }
        };
        handler.postDelayed(runnable, BluetoothPhoneConstants.HANGUP_PAGE_DISMISS_DURATION);
    }

    public void sendHangUpBroadcast(ContactBean bean) {
        PhoneStateManager.getInstance(context).dispatchState(State.IDLE.getValue(), bean);
    }

    @Override
    public void onBlueToothConnected() {
        KLog.d(TAG, "onBlueToothConnected: ");
    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        KLog.d(TAG, "onHfpConnected: ");
    }

    @Override
    public void onBlueToothDisConnected(BluetoothDevice device) {
        KLog.d(TAG, "onBlueToothDisConnected: ");
    }

    @Override
    public void onBlueToothDisabled() {
        KLog.d(TAG, "onBlueToothDisabled: ");
    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
        KLog.d(TAG, "onHfpDisConnected: ");
    }

    @Override
    public void onPbapConnected() {

    }

    @Override
    public void onPbapDisconnected() {

    }

    public void connectBluetooth() {
        if (OpenAppUtils.openApp(CenterConstants.SETTING, context.getString(R.string.please_install_settings_first))) {
            NodeUtils.jumpTo(context, CenterConstants.SETTING, "com.xiaoma.setting.main.ui.MainActivity", NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.BLUETOOTH_SETTINGS);
        }
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {

    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {

    }

    public boolean needPhoneStateCallback(){
        return true;
    }

    protected boolean isDeviceConnected(BluetoothDevice device){
        return BlueToothPhoneManagerFactory.getInstance().isDeviceConnected(device);
    }

    protected boolean isDeviceDisconnected(BluetoothDevice device){
        return BlueToothPhoneManagerFactory.getInstance().isDeviceDisconnected(device);
    }

}
