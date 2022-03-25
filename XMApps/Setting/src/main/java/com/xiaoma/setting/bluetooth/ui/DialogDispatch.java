package com.xiaoma.setting.bluetooth.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.common.views.BltPairConfirmDialog;
import com.xiaoma.setting.common.views.ErrorDialog;
import com.xiaoma.setting.common.views.ProgressDialog;
import com.xiaoma.utils.log.KLog;


/**
 * @Author ZiXu Huang
 * @Data 2018/10/26
 */
public class DialogDispatch implements DialogInterface.OnClickListener {
    private String pairingKey;
    private Context context;
    private AlertDialog confirmDialog;
    private DialogButtonClickedListener listener;
    private Handler handler = new Handler() /*{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                AlertDialog dialog = (AlertDialog) msg.obj;
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    if (listener != null) {
                        listener.onNegativeButtonClicked();
                    }
                }
            } else if (msg.what == 1) {
                AlertDialog dialog = (AlertDialog) msg.obj;
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }*/;

    private CountDownTimer countDownTimer;
    private ProgressDialog inPairingDialog;
    private ProgressDialog connectDialog;
    private ProgressDialog progressDialog;
    private BltPairConfirmDialog bltPairConfirmDialog;
    private int type;

    public DialogDispatch(Context context) {
        this.context = context;
    }

    public DialogDispatch() {
    }

    public void setOnDialogButtonClicked(DialogButtonClickedListener listener) {
        this.listener = listener;
    }

    public void showPairDialog(Context context, Intent intent) {
        if (connectDialog != null && connectDialog.isShowing()) {
            connectDialog.dismiss();
        }
        pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR) + "";
        pairingKey = addOIfNeed(pairingKey);
        KLog.d("hzx", "配对码: " + pairingKey);
        DialogType dialogType = getDialogType(intent);
        switch (dialogType) {
            case USER_ENTRY_DIALOG:
                showEntryDialog(context);
                break;
            case DISPLAY_PASSKEY_DIALOG:
                showDisplayPassKeyDialog(context);
                break;
            case CONFIRMATION_DIALOG:
                showConfirmDialog(context);
                break;
            case INVALID_DIALOG_TYPE:
                KLog.d("invalid pairing type");
                break;
        }
    }

    private String addOIfNeed(String pairingKey) {
        if (pairingKey.length() >= 6) {
            return pairingKey;
        }
        pairingKey = 0 + pairingKey;
        return addOIfNeed(pairingKey);
    }

    private DialogType getDialogType(Intent intent) {
        DialogType dialogType;
        type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
        switch (type) {
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_PIN:
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_PIN_16_DIGITS:
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_PASSKEY:
                dialogType = DialogType.USER_ENTRY_DIALOG;
                break;
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_PASSKEY_CONFIRMATION:
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_CONSENT:
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_OOB_CONSENT:
                dialogType = DialogType.CONFIRMATION_DIALOG;
                break;
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_DISPLAY_PASSKEY:
            case SettingConstants.BT_DEVICE_PAIRING_VARIANT_DISPLAY_PIN:
                dialogType = DialogType.DISPLAY_PASSKEY_DIALOG;
                break;
            default:
                dialogType = DialogType.INVALID_DIALOG_TYPE;
        }
        return dialogType;
    }

    public void showInPairingDialog() {
        inPairingDialog = new ProgressDialog(context);
        inPairingDialog.show();
        countDown(inPairingDialog);
        inPairingDialog.setMessage(String.format(context.getString(R.string.in_pairing_remain), 60));
    }

    public void showPairFailedDialog() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
        if (inPairingDialog != null && inPairingDialog.isShowing()) {
            inPairingDialog.dismiss();
        }
        ErrorDialog errorDialog = new ErrorDialog(context);
        errorDialog.show();
        errorDialog.setErrorMsg(R.string.pair_failed);
    }

    public void showConnectOrDisconnectDialog(@StringRes int message) {
        if (connectDialog == null) {
            connectDialog = new ProgressDialog(context);
        }
        connectDialog.setCancelable(false);
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.show();
        connectDialog.setMessage(context.getString(message));
        dismissDialogByTime(connectDialog, 3000, 1);
    }

    public void dismissConnectOrDisconnectDialog() {
        if (connectDialog != null && connectDialog.isShowing()) {
            connectDialog.dismiss();
        }
    }


    private void countDown(final ProgressDialog progressDialog) {
        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressDialog.setMessage(String.format(context.getString(R.string.in_pairing_remain), millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                listener.onNegativeButtonClicked();
                showPairFailedDialog();
            }
        };
        countDownTimer.start();
    }

    public void dismissInPairingDialog() {
        if (inPairingDialog != null && inPairingDialog.isShowing()) {
            inPairingDialog.dismiss();
            countDownTimer.cancel();
        }
    }

    public void showConfirmDialog(Context context) {
        bltPairConfirmDialog = new BltPairConfirmDialog(context);
        bltPairConfirmDialog.show();
        bltPairConfirmDialog.setTitle(R.string.notification);
        bltPairConfirmDialog.setMsg(String.format(context.getString(R.string.confirm_pin), pairingKey));
        bltPairConfirmDialog.setOnConfirmClickedListener(new BltPairConfirmDialog.OnConfirmClickedListener() {
            @Override
            public void onConfirmClicked() {
                bltPairConfirmDialog.dismiss();
                if (listener != null) listener.onPositiveButtonClicked(type);
            }

            @Override
            public void onCancel() {
                bltPairConfirmDialog.dismiss();
                if (listener != null) listener.onNegativeButtonClicked();
            }
        });
        dismissDialogByTime(confirmDialog, 10000, 0);
    }

    public void dismissConfirmDialog() {
        if (bltPairConfirmDialog != null && bltPairConfirmDialog.isShowing()) {
            bltPairConfirmDialog.dismiss();
        }
    }

    private void dismissDialogByTime(final Dialog dialog, int delayTime, final int category) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    if (category == 0) {
                        if (listener != null) {
                            listener.onNegativeButtonClicked();
                        }
                    } else if (category == 1) {
                        dialog.dismiss();
                    }
//                    if (connectFailed) {
//                        XMToast.toastException(context, R.string.blt_connect_failed);
//                        connectFailed = false;
//                    }
                }
            }
        }, delayTime);
    }

    private void showDisplayPassKeyDialog(Context context) {

    }

    private void showEntryDialog(Context context) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (listener != null) listener.onPositiveButtonClicked(type);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            if (listener != null) listener.onNegativeButtonClicked();
        }
        dialog.dismiss();
    }

    public void showAutoDismissDialog(@StringRes int msg, @DrawableRes int img) {
        showAutoDismissDialog(null, msg, img);
    }

    public void showAutoDismissDialog(Context context, @StringRes int msg, @DrawableRes int img) {
        Context curContext = null;
        if (context == null) {
            curContext = this.context;
        } else {
            curContext = context;
        }
        ErrorDialog autoDismissDialog = new ErrorDialog(curContext);
        autoDismissDialog.show();
        if (msg != ErrorDialog.DEFAULT_VALUE) {
            autoDismissDialog.setErrorMsg(msg);
        }
        if (img != ErrorDialog.DEFAULT_VALUE) {
            autoDismissDialog.setErrorImg(img);
        }
    }

    public void showOpenBtDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setMessage(R.string.open_blt);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissOpenBtDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public enum DialogType {
        USER_ENTRY_DIALOG,
        CONFIRMATION_DIALOG,
        DISPLAY_PASSKEY_DIALOG,
        INVALID_DIALOG_TYPE
    }

    public interface DialogButtonClickedListener {
        void onPositiveButtonClicked(int type);

        void onNegativeButtonClicked();
    }
}
