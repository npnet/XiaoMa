package com.xiaoma.bluetooth.phone.common.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.ui.toast.XMToast;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/21
 */
public class OpenAppUtils {

    public static boolean openApp(String packageName, String hintText) {
        if (checkPackInfo(packageName)) {
            OpenActivityUtils.openPackage(BlueToothPhone.getContext(), packageName, BluetoothPhoneConstants.SETTING_BLUETOOTH_VALUE);
            return true;
        } else {
            XMToast.showToast(BlueToothPhone.getContext(), hintText);
            return false;
        }
    }

    public static boolean checkPackInfo(String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = BlueToothPhone.getContext().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }
}
