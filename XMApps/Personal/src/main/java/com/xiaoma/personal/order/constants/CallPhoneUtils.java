package com.xiaoma.personal.order.constants;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.personal.R;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/20 0020 19:36
 *       desc：拨打电话
 * </pre>
 */
public class CallPhoneUtils {


    public static void callBluetoothPhone(
            final FragmentActivity activity,
            String phoneNumber,
            final CallPhoneCallback callback) {


        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.contains("/")) {
            phoneNumber = phoneNumber.substring(0, phoneNumber.indexOf("/"));
        }else {
            XMToast.showToast(activity,R.string.not_phone_number);
            return;
        }

        final String finalTempNumber = phoneNumber;
        OrderDialogFactory.createCallPhoneOrCancelPredestineDialog(activity,
                activity.getString(R.string.order_dialog_call_phone_desc),
                phoneNumber,
                activity.getString(R.string.order_dialog_call_phone),
                new DialogHandlerCallback() {
                    @Override
                    public void handle() {
                        CallPhoneUtils.callPhone(activity, finalTempNumber, callback);
                    }
                });
    }

    private static void callPhone(Context context, String phoneNum, final CallPhoneCallback callPhoneCallback) {
        int localPort = 8887;
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM, phoneNum);
        Linker.getInstance().request(new Request(new SourceInfo(context.getPackageName(), localPort), new RequestHead(new SourceInfo(
                        CenterConstants.BLUETOOTH_PHONE,
                        CenterConstants.BLUETOOTH_PHONE_PORT),
                        CenterConstants.BluetoothPhoneThirdAction.DIAL), bundle),
                new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) throws RemoteException {
                        Bundle extra = response.getExtra();
                        boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM_RESULT);
                        if (!success) {
                            callPhoneCallback.failed(response);
                        }
                    }
                });
    }
}
