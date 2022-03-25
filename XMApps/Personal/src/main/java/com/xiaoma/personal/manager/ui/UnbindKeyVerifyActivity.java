package com.xiaoma.personal.manager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

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
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Gillben on 2019/1/9 0009
 * <p>
 * desc: 解绑钥匙
 */
@PageDescComponent(EventConstants.PageDescribe.UnBindKeyVerify)
public class UnbindKeyVerifyActivity extends PasswdVerifyActivity {

    private CarKey mCarKey;

    public static void startForResult(Activity activity, Bundle bundle, int requestCode) {
        startForResult(activity,bundle,requestCode,PasswdTexts.UNBIND,UnbindKeyVerifyActivity.class);
    }

    public static void startForResult(Fragment fragment, Bundle bundle, int requestCode) {
        startForResult(fragment,bundle,requestCode,PasswdTexts.UNBIND,UnbindKeyVerifyActivity.class);
    }

    @Override
    protected void initView() {
        super.initView();
        setTexts(PasswdTexts.UNBIND);
        mCarKey = (CarKey) getIntent().getSerializableExtra(LoginConstants.IntentKey.CAR_KEY);
    }

    @Override
    protected void finishInputThenConfirm() {
        if (!NetworkUtils.isConnected(this)) {
            XMToast.toastException(this, R.string.network_error_retry);
            return;
        }
        if (UserUtil.checkPasswd(getPasswordText(), mUser.getPassword())) {
            unbindKey();
        } else {
            cleanPasswordText();
            XMToast.toastException(this, R.string.passwd_error);
        }
    }

    private void unbindKey() {
        showProgressDialog(R.string.base_loading);
        final User currentUser = UserManager.getInstance().getCurrentUser();
        if (mUser != null && mCarKey != null) {
            int keyType = mCarKey.isBle() ? 1 : 0;
            RequestManager.unbindKey(mUser.getId(), keyType,
                    getPasswordText(), new ResultCallback<XMResult<OnlyCode>>() {
                        @Override
                        public void onSuccess(XMResult<OnlyCode> result) {
                            if (result.isSuccess()) {
                                unbindLocal(mUser);
                            } else {
                                XMToast.toastException(UnbindKeyVerifyActivity.this, result.getResultMessage());
                            }
                            dismissProgress();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            dismissProgress();
                            if (code == ErrorCodeConstants.LOGIN_PASSWD_ERROR_CODE) {
                                // 密码出错
                                XMToast.toastException(UnbindKeyVerifyActivity.this, msg);
                            } else if (code == ErrorCodeConstants.ERROR_DISABLE_LOGIN_CODE) {
                                DeactivateActivity.start(UnbindKeyVerifyActivity.this);
                            } else {
                                if (code == -1 || !NetworkUtils.isConnected(getApplication()) || TextUtils.isEmpty(msg)) {
                                    msg = getString(com.xiaoma.login.R.string.no_network);
                                }
                                XMToast.toastException(UnbindKeyVerifyActivity.this, msg);
                            }
                            cleanPasswordText();
                        }
                    });
        } else {
            KLog.w("unbindKey ===== userId: " + currentUser + "    keyNumber: " + mCarKey);
            XMToast.showToast(this, R.string.no_network);
            finish();
        }
    }

    private void unbindLocal(User currentUser) {
        if (!mCarKey.isBle()) {
            currentUser.setNormalKeyId(null);
        } else {
            currentUser.setBluetoothKeyId(null);
        }
        UserManager.getInstance().notifyUserUpdate(currentUser,true);
        XMToast.toastSuccess(this, R.string.unbind_success);
        UnbindKeyVerifyActivity.this.setResult(RESULT_OK);
        UnbindKeyVerifyActivity.this.finish();
    }
}
