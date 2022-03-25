package com.xiaoma.personal.manager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.password.PasswdTexts;
import com.xiaoma.login.business.ui.password.PasswdVerifyActivity;
import com.xiaoma.login.business.ui.verify.DeactivateActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.login.sdk.CarKey;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.network.ErrorCodeConstants;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Gillben on 2019/1/9 0009
 * <p>
 * desc: 解绑钥匙
 */
@PageDescComponent(EventConstants.PageDescribe.BindKeyVerify)
public class BindKeyVerifyActivity extends PasswdVerifyActivity {

    private ConfirmDialog mConfirmBindDialog;
    private CarKey mCarKey;

    public static void startForResult(Activity activity, Bundle bundle, int requestCode) {
        startForResult(activity,bundle,requestCode,PasswdTexts.BIND,BindKeyVerifyActivity.class);
    }

    public static void startForResult(Fragment fragment, Bundle bundle, int requestCode) {
        startForResult(fragment,bundle,requestCode,PasswdTexts.BIND,BindKeyVerifyActivity.class);
    }

    @Override
    protected void initView() {
        super.initView();
        setTexts(PasswdTexts.BIND);
        mCarKey = (CarKey) getIntent().getSerializableExtra(LoginConstants.IntentKey.CAR_KEY);
    }

    @Override
    protected void finishInputThenConfirm() {
        if (!NetworkUtils.isConnected(this)) {
            XMToast.toastException(this, R.string.network_error_retry);
            return;
        }
        if (UserUtil.checkPasswd(getPasswordText(), mUser.getPassword())) {
            mConfirmBindDialog = new ConfirmDialog(this);
            mConfirmBindDialog.setContent(getString(R.string.confirm_bind_key))
                    .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mConfirmBindDialog.dismiss();
                        }
                    }).setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConfirmBindDialog.dismiss();
                    bindKey();
                }
            }).show();
        } else {
            cleanPasswordText();
            XMToast.toastException(this, R.string.passwd_error);
        }
    }

    private void bindKey() {
        showProgressDialog(R.string.base_loading);
        if (mUser != null && mCarKey != null) {
            int type = mCarKey.isBle() ? 1 : 0;
            RequestManager.bindKey(String.valueOf(mUser.getId()), mCarKey.getStr(),
                    getPasswordText(), type,
                    new ResultCallback<XMResult<String>>() {
                        @Override
                        public void onSuccess(XMResult<String> result) {
                            if (result.isSuccess()) {
                                bindLocal(mUser);
                            } else {
                                XMToast.toastException(BindKeyVerifyActivity.this, result.getResultMessage());
                            }
                            dismissProgress();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            dismissProgress();
                            if (code == ErrorCodeConstants.LOGIN_PASSWD_ERROR_CODE) {
                                // 密码出错
                                XMToast.toastException(BindKeyVerifyActivity.this, msg);
                            } else if (code == ErrorCodeConstants.ERROR_DISABLE_LOGIN_CODE) {
                                DeactivateActivity.start(BindKeyVerifyActivity.this);
                            } else {
                                if (code == -1 || !NetworkUtils.isConnected(getApplication()) || TextUtils.isEmpty(msg)) {
                                    msg = getString(com.xiaoma.login.R.string.no_network);
                                }
                                XMToast.toastException(BindKeyVerifyActivity.this, msg);
                            }
                            cleanPasswordText();
                        }
                    });
        } else {
            KLog.w("bindKey ===== userId: " + mUser + "    key: " + mCarKey);
            XMToast.showToast(this, R.string.no_network);
            finish();
        }
    }

    private void bindLocal(User currentUser) {
        if (!mCarKey.isBle()) {
            currentUser.setNormalKeyId(mCarKey.getStr());
        } else {
            currentUser.setBluetoothKeyId(mCarKey.getStr());
        }
        UserManager.getInstance().notifyUserUpdate(currentUser);
        XMToast.toastSuccess(this, R.string.bind_success);
        BindKeyVerifyActivity.this.setResult(RESULT_OK);
        BindKeyVerifyActivity.this.finish();
    }
}
