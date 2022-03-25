package com.xiaoma.music.kuwo.listener;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public interface OnUserInfoChangeListener {
    void  onLogin(boolean success, String msg, String retErrtype);

    void  onUserStatusChange(boolean success, String msg);

    void  onLogout(boolean success, String msg, int logoutType);

    void  onSendRegSms(boolean success, String msg, String var3);

    void  onReg(boolean success, String msg, String var3);
    
}
