package com.xiaoma.bluetooth.phone.common.sdk;

import android.os.Bundle;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.utils.log.KLog;
import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2019/2/13
 */
public class BluetoothPhoneClient extends Client {

    private IBlueToothPhoneManager manager;
    private static BluetoothPhoneClient mInstance;

    private BluetoothPhoneClient() {
        super(BlueToothPhone.getContext(), CenterConstants.BLUETOOTH_PHONE_PORT);
        manager = BlueToothPhoneManagerFactory.getInstance();
    }

    public static BluetoothPhoneClient getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothPhoneClient.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothPhoneClient();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected void onReceive(int action, Bundle data) {
        KLog.d("Third Message action: " + action);
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        KLog.d("Third Message action: " + action);
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        KLog.d("Third Message action: " + action);
    }

    public void notifySyncSuccess(List<ContactBean> historyCallList) {

    }
}
