package com.xiaoma.personal.manager.ui;

import android.text.TextUtils;
import android.view.View;

import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.password.BasePasswordActivity;
import com.xiaoma.login.business.ui.password.forget.ForgetPasswdActivity;
import com.xiaoma.login.business.ui.verify.DeactivateActivity;
import com.xiaoma.login.common.PasswordPageConstants;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.network.ErrorCodeConstants;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.common.util.PasswordOperationType;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

/**
 * Created by Gillben on 2019/1/9 0009
 * <p>
 * desc: 修改密码
 */
@PageDescComponent(EventConstants.PageDescribe.AccountChangePasswd)
public class ModifyPasswordActivity extends BasePasswordActivity {


    private int mStatus;  //0-默认输入密码状态，1-第一次输入新密码，2-第二次输入新密码
    private String cacheModifyPassword;  //缓存第一次输入密码，与二次输入作比较

    @Override
    protected boolean showForget() {
        return true;
    }

    @Override
    protected boolean isNext() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        switchStatus(0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == com.xiaoma.login.R.id.forget_passwd) {
            User currentUser = UserManager.getInstance().getCurrentUser();
            ForgetPasswdActivity.startForResult(this, currentUser,
                    PasswordPageConstants.RESET_PASSWD_PAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void finishInputThenConfirm() {
        if (!NetworkUtils.isConnected(this)) {
            XMToast.toastException(this, R.string.network_error_retry);
            return;
        }

        User currentUser = UserManager.getInstance().getCurrentUser();
        String password = getPasswordText();

        switch (mStatus) {
            case 0:
                if (UserUtil.checkPasswd(password, currentUser.getPassword())) {
                    switchStatus(++mStatus);
                } else {
                    XMToast.toastException(ModifyPasswordActivity.this, R.string.passwd_error);
                    switchStatus(0);
                }
                break;
            case 1:
                cacheModifyPassword = getPasswordText();
                switchStatus(++mStatus);
                break;
            case 2:
                if (password.equals(cacheModifyPassword)) {
                    verifyOrModifyPassword(currentUser.getId(), password, PasswordOperationType.MODIFY.getValue());
                } else {
                    XMToast.toastException(this, R.string.two_password_not_same);
                    switchStatus(1);
                }
                break;
        }
    }


    private void verifyOrModifyPassword(long userId, String password, int type) {
        showProgressDialog(R.string.base_loading);
        RequestManager.verifyOrModifyUserPassword(userId, password, type, new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> result) {
                dismissProgress();
                User currentUser = UserManager.getInstance().getCurrentUser();
                currentUser.setPassword(UserUtil.getPasswdHash(cacheModifyPassword));
                UserManager.getInstance().notifyUserUpdate(currentUser);
                XMToast.toastSuccess(ModifyPasswordActivity.this, R.string.change_passwd_success);
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                dismissProgress();
                if (code == ErrorCodeConstants.LOGIN_PASSWD_ERROR_CODE) {
                    // 密码出错
                    XMToast.toastException(ModifyPasswordActivity.this, msg);
                } else if (code == ErrorCodeConstants.ERROR_DISABLE_LOGIN_CODE) {
                    DeactivateActivity.start(ModifyPasswordActivity.this);
                } else {
                    if (code == -1 || !NetworkUtils.isConnected(getApplication()) || TextUtils.isEmpty(msg)) {
                        msg = getString(com.xiaoma.login.R.string.no_network);
                    }
                    XMToast.toastException(ModifyPasswordActivity.this, msg);
                }
                cleanPasswordText();
            }
        });
    }

    private void switchStatus(int status) {
        switch (status) {
            case 0:
                changeStatus(R.string.next_text, R.string.input_password_then_modify);
                mForgetPasswd.setVisibility(View.VISIBLE);
                mStatus = 0;
                break;
            case 1:
                changeStatus(R.string.one_again_input, R.string.input_four_passwords);
                mForgetPasswd.setVisibility(View.GONE);
                mStatus = 1;
                break;
            case 2:
                changeStatus(R.string.setup_new_password, R.string.input_four_passwords_again);
                mForgetPasswd.setVisibility(View.GONE);
                mStatus = 2;
                break;
        }
    }

    private void changeStatus(int buttonTextId, int descTextId) {
        changeStatus(buttonTextId, descTextId, true);
    }

    private void changeStatus(int buttonTextId, int descTextId, boolean clean) {
        setButtonText(buttonTextId);
        setDescText(descTextId);
        if (clean) {
            cleanPasswordText();
        }
    }

    @Override
    public void onBackPressed() {
        if (mStatus != 0) {
            switchStatus(--mStatus);
        } else {
            super.onBackPressed();
        }

    }
}
