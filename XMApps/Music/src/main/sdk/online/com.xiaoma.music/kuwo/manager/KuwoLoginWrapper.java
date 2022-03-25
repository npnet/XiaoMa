package com.xiaoma.music.kuwo.manager;

import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.kuwo.impl.ILogin;
import com.xiaoma.music.kuwo.listener.OnKuwoPhoneCodeFetchListener;
import com.xiaoma.music.kuwo.listener.OnPayTokenListener;
import com.xiaoma.music.kuwo.model.XMKwUserInfo;
import com.xiaoma.music.kuwo.model.XMPhoneCodeResult;
import com.xiaoma.music.kuwo.observer.IVipMessageObserver;

import cn.kuwo.base.bean.UserInfo;
import cn.kuwo.core.messagemgr.MessageID;
import cn.kuwo.core.messagemgr.MessageManager;
import cn.kuwo.mod.userinfo.UserInfoHelper;
import cn.kuwo.mod.userinfo.login.BaseScanQrCodeMgr;
import cn.kuwo.mod.userinfo.login.LoginResult;
import cn.kuwo.mod.userinfo.login.QrCodeResult;
import cn.kuwo.open.KwApi;
import cn.kuwo.open.OnGetPayTokentListener;
import cn.kuwo.open.base.PhoneCodeResult;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class KuwoLoginWrapper implements ILogin {

    @Override
    public void sendPhoneCode(String phone, OnKuwoPhoneCodeFetchListener listener) {
        KwApi.sendPhoneCode(phone, getPhoneCodeListener(listener));
    }

    @Override
    public void loginByMobile(String pPhone, String pTm, String pCode) {
        UserInfoHelper.loginByMobile(pPhone, pTm, pCode);
    }

    @Override
    public void login(String username, String password) {
        UserInfoHelper.login(username, password);
    }

    @Override
    public void autoLogin() {
        UserInfoHelper.autoLogin();
    }

    @Override
    public void logout() {
        UserInfoHelper.logout();
    }

    @Override
    public boolean isUserLogon() {
        return UserInfoHelper.isUserLogon();
    }

    @Override
    public XMKwUserInfo getUserInfo() {
        final UserInfo userInfo = UserInfoHelper.getUserInfo();
        return new XMKwUserInfo(userInfo);
    }

    public boolean isCarVipUser() {
        if (!isUserLogon()) {
            return false;
        }
        return UserInfoHelper.isCarVipUser();
    }

    private KwApi.OnFetchPhoneCodeListener getPhoneCodeListener(final OnKuwoPhoneCodeFetchListener listener) {
        return new KwApi.OnFetchPhoneCodeListener() {
            @Override
            public void onFetched(PhoneCodeResult phoneCodeResult) {
                listener.onFetch(new XMPhoneCodeResult(phoneCodeResult));
            }
        };
    }

    @Override
    public QrCodeResult getQrCodeImage() {
        return BaseScanQrCodeMgr.getInstance().getQrCodeImage();
    }

    @Override
    public LoginResult checkResult() {
        return BaseScanQrCodeMgr.getInstance().checkResult();
    }

    @Override
    public void getPayToken(OnPayTokenListener listener) {
        KwApi.getPayToken(MusicConstants.KwConstants.KW_APP_ID, MusicConstants.KwConstants.KW_APP_KEY, new OnGetPayTokentListener() {
            @Override
            public void onFetch(int i, String s, String s1) {
                listener.onFetch(i, s, s1);
            }
        });
    }

    @Override
    public void fetchVipInfo() {
        UserInfoHelper.fetchVipInfo();
    }

    @Override
    public void attachVipMessage(IVipMessageObserver vipMessageObserver) {
        MessageManager.getInstance().attachMessage(MessageID.OBSERVER_VIP, vipMessageObserver);
    }

    @Override
    public void detachVipMessage(IVipMessageObserver vipMessageObserver) {
        MessageManager.getInstance().detachMessage(MessageID.OBSERVER_VIP, vipMessageObserver);
    }
}
