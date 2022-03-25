package com.xiaoma.music.kuwo.proxy;

import android.support.annotation.WorkerThread;

import com.xiaoma.music.kuwo.impl.ILogin;
import com.xiaoma.music.kuwo.listener.OnKuwoPhoneCodeFetchListener;
import com.xiaoma.music.kuwo.listener.OnPayTokenListener;
import com.xiaoma.music.kuwo.manager.KuwoLoginWrapper;
import com.xiaoma.music.kuwo.model.XMKwUserInfo;
import com.xiaoma.music.kuwo.observer.IVipMessageObserver;

import cn.kuwo.mod.userinfo.login.LoginResult;
import cn.kuwo.mod.userinfo.login.QrCodeResult;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class XMKWLoginProxy implements ILogin {

    public static XMKWLoginProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final XMKWLoginProxy instance = new XMKWLoginProxy();
    }

    private ILogin login;

    private XMKWLoginProxy() {
        login = new KuwoLoginWrapper();
    }

    @Override
    public void sendPhoneCode(String phone, OnKuwoPhoneCodeFetchListener listener) {
        login.sendPhoneCode(phone, listener);
    }

    @Override
    public void autoLogin() {
        login.autoLogin();
    }

    @Override
    public void loginByMobile(String pPhone, String pTm, String pCode) {
        login.loginByMobile(pPhone, pTm, pCode);
    }

    @Override
    public void login(String username, String password) {
        login.login(username, password);
    }

    @Override
    public void logout() {
        login.logout();
    }

    @Override
    public boolean isUserLogon() {
        return login.isUserLogon();
    }

    @Override
    public XMKwUserInfo getUserInfo() {
        return login.getUserInfo();
    }

    @Override
    public boolean isCarVipUser() {
        return login.isCarVipUser();
    }

    @Override
    @WorkerThread
    public QrCodeResult getQrCodeImage() {
        //需要在子线程调用
        return login.getQrCodeImage();
    }

    @Override
    @WorkerThread
    public LoginResult checkResult() {
        //需要在子线程调用
        return login.checkResult();
    }

    @Override
    public void getPayToken(OnPayTokenListener listener) {
        login.getPayToken(listener);
    }

    @Override
    public void fetchVipInfo() {
        login.fetchVipInfo();
    }

    @Override
    public void attachVipMessage(IVipMessageObserver vipMessageObserver) {
        login.attachVipMessage(vipMessageObserver);
    }

    @Override
    public void detachVipMessage(IVipMessageObserver vipMessageObserver) {
        login.detachVipMessage(vipMessageObserver);
    }
}
