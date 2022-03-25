package com.xiaoma.service.common.manager;

import android.os.Bundle;
import android.os.RemoteException;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.AppHolder;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.ServiceConstants;
import com.xiaoma.ui.toast.XMToast;

/**
 * @author taojin
 * @date 2019/5/15
 */
public class ServiceBlueToothPhoneManager {

    private static ServiceBlueToothPhoneManager mInstance;

    private ServiceBlueToothPhoneManager() {
    }

    public static ServiceBlueToothPhoneManager getInstance() {
        if (mInstance == null) {
            synchronized (ServiceBlueToothPhoneManager.class) {
                if (mInstance == null) {
                    mInstance = new ServiceBlueToothPhoneManager();
                }
            }
        }
        return mInstance;
    }

    public void callPhone(String phoneNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM, phoneNumber);

        SourceInfo serviceSourceInfo = new SourceInfo(ServiceConstants.PACKAGE_NAME, ServiceConstants.SERVICE_PORT);
        SourceInfo blueToothSourceInfo = new SourceInfo(CenterConstants.BLUETOOTH_PHONE, CenterConstants.BLUETOOTH_PHONE_PORT);

        RequestHead requestHead = new RequestHead(blueToothSourceInfo, CenterConstants.BluetoothPhoneThirdAction.DIAL);

        Request request = new Request(serviceSourceInfo, requestHead, bundle);

        int requestCode = Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM_RESULT);
            }
        });

        if (requestCode == ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND) {
            XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.please_service_bluetooth_phone);
        }
    }
}
