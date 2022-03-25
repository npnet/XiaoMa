package com.xiaoma.utils.logintype.manager;

import com.xiaoma.utils.logintype.callback.AbsClearDataListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/22
 * @Describe: 登录类型帮助类，用于保存一些处理登录类型相关逻辑时的数据
 *
 */

public class LoginTypeHelper {

    private String startAfterChooseAccountAppPkg;//选择账号后启动的app的包名
    private boolean mIsChooseAccount = false;//是否 是启动选择账号
    private boolean mIsKeyVerify=false;//是否 密码验证
    private String startAfterKeyVerifyAppPkg;//密码验证成功后启动的app的包名
    private String startAfterKeyVerifyActName;// 密码验证成功后启动的具体act的全类名
    private boolean isJumpApp = false;//是否是启动app
    private List<AbsClearDataListener> mAbsClearDataListeners;



    public LoginTypeHelper() {
        mAbsClearDataListeners = new ArrayList<>();
    }

    public void setAbsClearDataListeners(AbsClearDataListener absClearDataListeners) {
        if(mAbsClearDataListeners == null || mAbsClearDataListeners.contains(absClearDataListeners)) return;
        mAbsClearDataListeners.add(absClearDataListeners);
    }
    public List<AbsClearDataListener> getAbsClearDataListeners() {
        return mAbsClearDataListeners;
    }
    public boolean isJumpApp() {
        return isJumpApp;
    }

    public void setJumpApp(boolean jumpApp) {
        isJumpApp = jumpApp;
    }

    public boolean isKeyVerify() {
        return mIsKeyVerify;
    }

    public void setKeyVerify(boolean keyVerify) {
        mIsKeyVerify = keyVerify;
    }

    public String getStartAfterKeyVerifyAppPkg() {
        return startAfterKeyVerifyAppPkg;
    }

    public void setStartAfterKeyVerifyAppPkg(String startAfterKeyVerifyAppPkg) {
        this.startAfterKeyVerifyAppPkg = startAfterKeyVerifyAppPkg;
    }

    public String getStartAfterKeyVerifyActName() {
        return startAfterKeyVerifyActName;
    }

    public void setStartAfterKeyVerifyActName(String startAfterKeyVerifyActName) {
        this.startAfterKeyVerifyActName = startAfterKeyVerifyActName;
    }

    public String getStartAfterChooseAccountAppPkg() {
        return startAfterChooseAccountAppPkg;
    }

    public void setStartAfterChooseAccountAppPkg(String startAfterCreateSubAppPkg) {
        this.startAfterChooseAccountAppPkg = startAfterCreateSubAppPkg;
    }

    public boolean isChooseAccount() {
        return mIsChooseAccount;
    }

    public void setChooseAccount(boolean createSub) {
        mIsChooseAccount = createSub;
    }

    // 重置
    public void reset() {
        startAfterChooseAccountAppPkg = null;
        startAfterKeyVerifyAppPkg = null;
        startAfterKeyVerifyActName = null;
        mIsChooseAccount = false;
        mIsKeyVerify = false;
        isJumpApp = false;
    }
}
