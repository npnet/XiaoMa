package com.xiaoma.music.kuwo.impl;

import com.xiaoma.music.kuwo.listener.OnKuwoPhoneCodeFetchListener;
import com.xiaoma.music.kuwo.listener.OnPayTokenListener;
import com.xiaoma.music.kuwo.model.XMKwUserInfo;
import com.xiaoma.music.kuwo.observer.IVipMessageObserver;

import cn.kuwo.mod.userinfo.login.LoginResult;
import cn.kuwo.mod.userinfo.login.QrCodeResult;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/7/30
 *  description :
 * </pre>
 */
public interface ILogin {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    void sendPhoneCode(String phone, OnKuwoPhoneCodeFetchListener listener);

    /**
     * 手机号快捷登录，如果手机号是酷我用户则直接登录否则创建一个新的账号
     *
     * @param pPhone 登陆的手机号
     * @param pTm    发送验证码接口返回的tm参数
     * @param pCode  手机收到的验证码,发送验证码请查看{@linkplain cn.kuwo.open.KwApi}
     **/
    void loginByMobile(String pPhone, String pTm, String pCode);

    /**
     * 用户名密码登录
     *
     * @param username 用户名，邮箱和手机号登录
     * @param password 密码
     */
    void login(String username, String password);

    /**
     * 自动登录，在用户退出前已经登录过，重新进入程序时可调用这个接口进行自动登录
     **/
    void autoLogin();

    /**
     * 退出登录
     */
    void logout();

    /**
     * 用户是否已经登录
     *
     * @return 已经登录返回true 否则返回false
     */
    boolean isUserLogon();

    /**
     * 返回登录用户
     *
     * @return 登录用户
     */
    XMKwUserInfo getUserInfo();

    /**
     * 返回当前登录用户是否是vip
     */
    boolean isCarVipUser();

    /**
     * 手机端扫码登录
     *
     * @return 获取登录二维码
     */
    QrCodeResult getQrCodeImage();

    /**
     * 手机端扫码登录状态
     *
     * @return 检查扫码登录状态
     */
    LoginResult checkResult();

    /**
     * 支付token获取
     *
     * @return 酷我根据登录用户信息加密的token
     */
    void getPayToken(OnPayTokenListener listener);


    /**
     * 获取vip信息
     */
    void fetchVipInfo();


    /**
     * 注册vip信息回调
     *
     * @param vipMessageObserver {@link IVipMessageObserver}
     */
    void attachVipMessage(IVipMessageObserver vipMessageObserver);

    /**
     * 解注册vip信息回调
     *
     * @param vipMessageObserver {@link IVipMessageObserver}
     */
    void detachVipMessage(IVipMessageObserver vipMessageObserver);
}
