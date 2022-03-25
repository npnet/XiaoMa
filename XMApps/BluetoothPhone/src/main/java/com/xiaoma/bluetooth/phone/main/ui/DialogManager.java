package com.xiaoma.bluetooth.phone.main.ui;

import android.content.Context;

import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.views.ErrorDialog;
import com.xiaoma.bluetooth.phone.common.views.ProgressDialog;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/12
 */
public class DialogManager {
    private Context context;
    private ProgressDialog progressDialog;

    public DialogManager(Context context) {
        this.context = context;
    }

    public void showRefreshSuccessDialog() {
        try {
            if (canNotShow())
                return;
            ErrorDialog errorDialog = new ErrorDialog(context);
            errorDialog.setShowDuration(3000);
            errorDialog.show();
            errorDialog.setErrorImg(R.drawable.icon_success);
            errorDialog.setErrorMsg(R.string.sync_success);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void showInRefreshDialog() {
        try {
            if (canNotShow())
                return;
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog.show();
            progressDialog.setMessage(R.string.in_syncing);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void showFreshFailedDialog() {
        try {
            if (canNotShow())
                return;
            ErrorDialog errorDialog = new ErrorDialog(context);
            errorDialog.show();
            errorDialog.setErrorMsg(R.string.sync_failed);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void dismissInRefreshDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean canNotShow() {
        // 通话状态不显示同步弹窗
        return PhoneStateManager.getInstance(context).isCallState();
    }
}
