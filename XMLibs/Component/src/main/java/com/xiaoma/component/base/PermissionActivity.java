package com.xiaoma.component.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.xiaoma.component.ComponentConstants;
import com.xiaoma.component.R;
import com.xiaoma.component.permission.IPermissionCheck;
import com.xiaoma.component.permission.PermissionHelper;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/17
 *     desc   :
 * </pre>
 */
public class PermissionActivity extends BaseActivity implements IPermissionCheck {

    protected static final int KEY_PERMISSION = 0x001;
    private PermissionHelper mPermissionHelper;
    private BaseApp mApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (BaseApp) BaseApp.getInstance();
        // 低版本直接调转授权成功
        if (getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
            requestPermissionsSuccess();
        } else {
            initPermission();
        }
    }

    private void initPermission() {
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.requestPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return KEY_PERMISSION;
    }

    @Override
    public String[] getPermissions() {
        return StringUtil.concatArray(PermissionHelper.MUST_PERMISSION, getNeedPermissions());
    }

    @Override
    public void requestPermissionsSuccess() {
        if (ConfigManager.getInstance().deviceIsEmpty()) {
            ConfigManager.getInstance().setDeviceInfo(ConfigManager.DeviceConfig.getIMEI(getApplication()));
        }
        if (TPUtils.get(this, ComponentConstants.FIRST_TIME_INTER, true)) {
            mApp.initLibs();
            mApp.startTinkerUpgradeService();
            TPUtils.put(this, ComponentConstants.FIRST_TIME_INTER, false);
        }
        if (mApp.firstActivity() == null) {
            finish();
            return;
        }
        startActivityAndFinish(mApp.firstActivity());
    }

    @Override
    public void requestPermissionsFail() {
        new AlertDialog
                .Builder(this)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.Permission_note)
                .setMessage(R.string.Permission_msg)
                .setPositiveButton(R.string.Permission_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPermissionHelper.requestPermissions(PermissionActivity.this);
                    }
                })
                .show();
    }

    private void startActivityAndFinish(Class<?> cls) {
        startActivity(new Intent(this, cls));
        overridePendingTransition(0, 0);
        finish();
    }


    private String[] getNeedPermissions() {
        return mApp.allNeedPermissions();
    }

}
