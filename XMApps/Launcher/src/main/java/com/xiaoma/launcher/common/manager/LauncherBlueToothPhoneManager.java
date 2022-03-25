package com.xiaoma.launcher.common.manager;

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
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.ui.toast.XMToast;

/**
 * @author taojin
 * @date 2019/5/15
 */
public class LauncherBlueToothPhoneManager {

    private static LauncherBlueToothPhoneManager mInstance;

    private LauncherBlueToothPhoneManager() {
    }

    public static LauncherBlueToothPhoneManager getInstance() {
        if (mInstance == null) {
            synchronized (LauncherBlueToothPhoneManager.class) {
                if (mInstance == null) {
                    mInstance = new LauncherBlueToothPhoneManager();
                }
            }
        }
        return mInstance;
    }

    public void callPhone(String phoneNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM, phoneNumber);
        int requestCode = Linker.getInstance().request(new Request(new SourceInfo(LauncherConstants.PACKAGE_NAME, LauncherConstants.LAUNCHER_PORT), new RequestHead(new SourceInfo(CenterConstants.BLUETOOTH_PHONE, CenterConstants.BLUETOOTH_PHONE_PORT), CenterConstants.BluetoothPhoneThirdAction.DIAL), bundle), new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM_RESULT);
            }
        });

        if (requestCode == ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND) {
            XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.please_launcher_bluetooth_phone);
        }
    }
}
