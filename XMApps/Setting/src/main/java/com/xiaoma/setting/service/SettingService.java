package com.xiaoma.setting.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.xiaoma.carlib.manager.XmCarUpdateManager;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.other.ui.ota.OTAUpdateConfirmDialog;
import com.xiaoma.setting.other.ui.ota.OTAUpdateResultDialog;
import com.xiaoma.setting.other.ui.ota.OTAUpdatingDialog;

/**
 * Created by Administrator on 2018/12/5 0005.
 */

public class SettingService extends Service implements XmCarUpdateManager.OnConferStateListener {

    private final String TAG = SettingService.class.getSimpleName();

    private String mPackageName;
    private OTAUpdateConfirmDialog installDialog;
    private OTAUpdateResultDialog installSuccessDialog;
    private OTAUpdateResultDialog installFailedDialog;
    private OTAUpdatingDialog mUpdatingDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "SettingService onCreate");
        XmCarUpdateManager.getInstance().init(this);
        XmCarUpdateManager.getInstance().setOnConferStateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "SettingService onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showAskInstallDialog(String content) {
        Log.e(TAG, "showAskInstallDialog:" + content);
        if (installDialog == null) {
            installDialog = new OTAUpdateConfirmDialog(this, R.style.custom_dialog2);
        }
        dismissOtherDialog(installDialog);
        if (!installDialog.isShowing()) {
            installDialog.show();
        }
        installDialog.setCancelable(false);
        installDialog.setContent(content);
        installDialog.setConfirmSureListener(getString(R.string.installation), new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.confirmVersionSearch})
            @ResId({R.id.tv_left_button})
            public void onClick(View v) {
                XmCarUpdateManager.getInstance().confirmUpdate();
                installDialog.dismiss();
            }
        });
        installDialog.setConfirmCancelListener(getString(R.string.next_time), new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.cancelVersionUpdate})
            @ResId({R.id.tv_right_button})
            public void onClick(View v) {
                XmCarUpdateManager.getInstance().cancelUpdate();
                installDialog.dismiss();
            }
        });
    }

    private void showInstallSuccessDialog(String content) {
        Log.e(TAG, "showInstallSuccessDialog:" + content);
        if (installSuccessDialog == null) {
            installSuccessDialog = new OTAUpdateResultDialog(this, R.style.custom_dialog2);
        }
        dismissOtherDialog(installSuccessDialog);
        installSuccessDialog.setCancelable(false);
        if (!installSuccessDialog.isShowing()) {
            installSuccessDialog.show();
        }
        installSuccessDialog.setContent(getString(R.string.Installation_result_prompt), content, "");
        installSuccessDialog.setOneButon(getString(R.string.close), new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.confirmVersionSearch})
            @ResId({R.id.tv_button})
            public void onClick(View v) {
                installSuccessDialog.dismiss();
            }
        }, View.VISIBLE);
    }

    private void showInstallErrorDialog(String content, String tip, int buttonVisible) {
        Log.e(TAG, "showInstallErrorDialog:" + content);
        if (installFailedDialog == null) {
            installFailedDialog = new OTAUpdateResultDialog(this, R.style.custom_dialog2);
        }
        dismissOtherDialog(installFailedDialog);
        installFailedDialog.setCancelable(false);
        if (!installFailedDialog.isShowing()) {
            installFailedDialog.show();
        }
        installFailedDialog.setContent(getString(R.string.Installation_result_prompt), content, tip);
        installFailedDialog.setOneButon(getString(R.string.close), new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.confirmVersionSearch})
            @ResId({R.id.tv_button})
            public void onClick(View v) {
                installFailedDialog.dismiss();
            }
        }, View.VISIBLE);
    }

    private void showUpdatingDialog(String ecuName) {
        Log.e(TAG, "showUpdatingDialog:" + ecuName);
        if (mUpdatingDialog == null) {
            mUpdatingDialog = new OTAUpdatingDialog(this, R.style.custom_dialog2);
        }
        dismissOtherDialog(mUpdatingDialog);
        mUpdatingDialog.setCancelable(false);
        if (!mUpdatingDialog.isShowing()) {
            mUpdatingDialog.show();
        }
        mUpdatingDialog.setContent(getString(R.string.on_upgrading) + ecuName + getString(R.string.controller));
    }

    @Override
    public void onUpdateRequest(String packageName, int updateTime) {
        Log.e(TAG, "onUpdateRequest");
        showAskInstallDialog(getString(R.string.controller_new_package) + packageName + getString(R.string.download_completed) + updateTime + getString(R.string.minute));
        this.mPackageName = packageName;
    }

    @Override
    public void onInstallSuccess(String ecuName) {
        Log.e(TAG, "onInstallSuccess");
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        showInstallSuccessDialog(ecuName + getString(R.string.controller_update_successed) + mPackageName + getString(R.string.update_completed));
    }

    @Override
    public void onInstallErrorCanUseSystem(String ecuName) {
        Log.e(TAG, "onInstallErrorCanUseSystem");
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        showInstallErrorDialog(ecuName + getString(R.string.controller_upadte_failure) + mPackageName + getString(R.string.update_failed), getString(R.string.please_contact_customer_service), View.VISIBLE);
    }

    @Override
    public void onInstallErrorNotUseSystem(String ecuName) {
        Log.e(TAG, "onInstallErrorNotUseSystem");
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        showInstallErrorDialog(ecuName + getString(R.string.controller_upadte_failure) + mPackageName + getString(R.string.update_failed), getString(R.string.please_contact_customer_service_shop), View.GONE);
    }

    @Override
    public void onInstallFailed(String content) {
        Log.e(TAG, "onInstallFailed");
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        showInstallErrorDialog(getString(R.string.you_have_one_upgrade_task) + content + getString(R.string.failed_to_start_the_upgrade_process), getString(R.string.if_you_have_problems), View.VISIBLE);
    }

    @Override
    public void onInstalling(String ecuName) {
        Log.e(TAG, "onInstalling");
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        showUpdatingDialog(ecuName);
    }

    private void dismissOtherDialog(Dialog dialog) {
        if (installDialog != null && dialog != installDialog) installDialog.dismiss();
        if (installSuccessDialog != null && dialog != installSuccessDialog)
            installSuccessDialog.dismiss();
        if (installFailedDialog != null && dialog != installFailedDialog)
            installFailedDialog.dismiss();
        if (mUpdatingDialog != null && dialog != mUpdatingDialog) mUpdatingDialog.dismiss();

    }

}
