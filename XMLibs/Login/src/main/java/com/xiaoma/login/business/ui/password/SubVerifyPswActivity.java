package com.xiaoma.login.business.ui.password;

import com.xiaoma.model.User;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/21
 * @Describe: 子账号 密码验证界面
 */

public class SubVerifyPswActivity extends PasswdVerifyActivity {

    @Override
    protected void verifySuccess(User user) {
        super.verifySuccess(user);
        LoginTypeManager.getInstance().jumpAfterKeyVerify(this);
    }
}
