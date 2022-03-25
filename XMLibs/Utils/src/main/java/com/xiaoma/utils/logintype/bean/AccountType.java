package com.xiaoma.utils.logintype.bean;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/07
 * @Describe: 账号类型
 */

public class AccountType {

    private String loginType;//登录类型
    private boolean needPsw;//是否需要密码

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public boolean isNeedPsw() {
        return needPsw;
    }

    public void setNeedPsw(boolean needPsw) {
        this.needPsw = needPsw;
    }

    @Override
    public String toString() {
        return "AccountType{" +
                "loginType='" + loginType + '\'' +
                ", needPsw=" + needPsw +
                '}';
    }
}
