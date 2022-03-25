package com.xiaoma.service.common.manager;

import android.content.Context;
import android.content.Intent;

import com.xiaoma.component.AppHolder;
import com.xiaoma.service.R;
import com.xiaoma.service.common.service.TboxCallWindowService;
import com.xiaoma.ui.toast.XMToast;

/**
 * @author taojin
 * @date 2019/7/26
 */
public class IBCallManager {
    private final int ICALL = 1, BCALL = 2;


    //默认IBCALL不在通话中
    private boolean isIBCall = false;

    private IBCallManager() {

    }

    private static class InstanceHolder {
        static final IBCallManager sInstance = new IBCallManager();
    }

    public static IBCallManager getInstance() {
        return IBCallManager.InstanceHolder.sInstance;
    }

    public void setIBCall(boolean isIBCall) {
        this.isIBCall = isIBCall;
    }

    public boolean isIBCall() {
        return isIBCall;
    }

    public void handleIBCall(int callType, Context context) {
        if (CarDataManager.getInstance().getIsBluetoothCall()) {
            XMToast.showToast(context, R.string.please_hang_up_the_current_call);
            return;
        }

        if (isIBCall()) {
            XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.please_hang_up_the_current_call);
            return;
        }

        context.stopService(new Intent(context, TboxCallWindowService.class));
        if (callType == ICALL) {
            int code = CarDataManager.getInstance().operateICall();
            if (code == 0) {
                Intent intent = new Intent(context, TboxCallWindowService.class);
                intent.putExtra("call_type", callType);
                context.startService(intent);
            } else {
                XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.call_faliue);

            }
        } else if (callType == BCALL) {
            int code = CarDataManager.getInstance().operateBcall();
            if (code == 0) {
                Intent intent = new Intent(context, TboxCallWindowService.class);
                intent.putExtra("call_type", callType);
                context.startService(intent);
            } else {
                XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.call_faliue);
            }
        }
    }
}
