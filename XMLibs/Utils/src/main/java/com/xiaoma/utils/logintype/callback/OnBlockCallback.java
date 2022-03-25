package com.xiaoma.utils.logintype.callback;

import com.xiaoma.utils.logintype.manager.LoginType;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/07
 * @Describe:  逻辑处理回调
 */

public abstract class OnBlockCallback {
    /**
     * 密码验证
     * @param loginType
     * @return
     */
    public boolean onKeyVerification(LoginType loginType) {return false;}

    /**
     * 创建子账号
     * @param loginType
     * @return
     */
    public boolean onChooseAccount(LoginType loginType) {
        return false;
    }

    /**
     * show Toast
     * @param loginType
     * @return
     */
    public boolean onShowToast(LoginType loginType) {return false;}

    /**
     * 默认处理，不做修改
     * @param loginType
     */
    public void handle(LoginType loginType) {}
}
