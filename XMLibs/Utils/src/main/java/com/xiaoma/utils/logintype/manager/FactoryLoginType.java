package com.xiaoma.utils.logintype.manager;

import android.text.TextUtils;

import com.xiaoma.utils.logintype.bean.AccountType;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.constant.LoginTypeModel;

import java.util.HashMap;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/06
 * @Describe: 工厂模式--登录
 */

public class FactoryLoginType extends LoginType {

    private String[] config_arr = {
            LoginCfgConstant.PERSONAL_ACCOUNT_MANAGEMENT,//个人中心 账号管理
            LoginCfgConstant.USER_CHOOSE//个人中心 账号管理
    };

    //一般用于登录后的初始化
    public FactoryLoginType(AccountType accountType) {
        this(accountType, "");
    }

    // 一般用于登录时初始化
    public FactoryLoginType(AccountType accountType, String userId) {
        super(accountType, userId);
        initPurviewConfig();
    }

    @Override
    public AccountType buildAccount() {
        AccountType accountType = new AccountType();
        accountType.setLoginType(LoginTypeModel.LOGIN_FACTORY_MODEL);
        writeToLocal(accountType);
        return accountType;
    }

    @Override
    public void initPurviewConfig() {
        mConfigs = new HashMap<>();
        if (config_arr == null || config_arr.length <= 0) return;
        for (int i = 0; i < config_arr.length; i++) {
            String config = config_arr[i];
            mConfigs.put(config, config);
        }
    }

    @Override
    public boolean judgeUse(String condition, OnBlockCallback callback) {
        if (TextUtils.isEmpty(condition)) return true;
        String result = mConfigs.get(condition);
        // 为 null ,说明没有功能限制
        if (TextUtils.isEmpty(result)) return true;
        // 不为 null ,说明有功能限制
        if (callback == null) return false;
        //吐司,onShowToast 返回true，说明逻辑已经自己处理，逻辑不在往后
        if (callback.onShowToast(this)) return false;
        callback.handle(this);
        return false;
    }

}
