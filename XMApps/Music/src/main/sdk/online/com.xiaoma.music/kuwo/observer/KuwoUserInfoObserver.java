package com.xiaoma.music.kuwo.observer;

import com.xiaoma.music.kuwo.listener.OnUserInfoChangeListener;

import cn.kuwo.core.observers.IUserInfoMgrObserver;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class KuwoUserInfoObserver implements IUserInfoMgrObserver {

    private OnUserInfoChangeListener listener;

    public KuwoUserInfoObserver(OnUserInfoChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void IUserInfoMgrObserver_OnLogin(boolean b, String s, String s1) {
        listener.onLogin(b, s, s1);
    }

    @Override
    public void IUserInfoMgrObserver_OnUserStatusChange(boolean b, String s) {
        listener.onUserStatusChange(b, s);
    }

    @Override
    public void IUserInfoMgrObserver_OnLogout(boolean b, String s, int i) {
        listener.onLogout(b, s, i);
    }

    @Override
    public void IUserInfoMgrObserver_OnSendRegSms(boolean b, String s, String s1) {
        listener.onSendRegSms(b, s, s1);
    }

    @Override
    public void IUserInfoMgrObserver_OnReg(boolean b, String s, String s1) {
        listener.onReg(b, s, s1);
    }
}
