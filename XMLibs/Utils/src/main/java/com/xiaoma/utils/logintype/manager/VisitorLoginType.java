package com.xiaoma.utils.logintype.manager;

import android.content.Context;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.R;
import com.xiaoma.utils.logintype.bean.AccountType;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.constant.LoginTypeModel;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/10
 * @Describe: 访客模式--登录(无用户信息的游客模式),依赖于是否有用户信息，可随时切换至游客模式
 */

public class VisitorLoginType extends TravellerLoginType {

    public VisitorLoginType(AccountType accountType) {
        this(accountType, "");
    }

    public VisitorLoginType(AccountType accountType, String userId) {
        super(accountType, userId);
    }

    @Override
    public AccountType buildAccount() {
        AccountType accountType = new AccountType();
        accountType.setLoginType(LoginTypeModel.LOGIN_VISITOR_MODEL);
        writeToLocal(accountType);
        return accountType;
    }

    @Override
    public void initPurviewConfig() {
        super.initPurviewConfig();
        //无用户信息的游客模式不能开关4G开关
        mConfigs.put(LoginCfgConstant.SWITCH_4G, LoginCfgConstant.SWITCH_4G);
        mConfigs.put(LoginCfgConstant.CREATE_USER, LoginCfgConstant.CREATE_USER);
    }

    @Override
    public String getPrompt(Context context) {
        if (context == null) return "";
        if (!ConfigManager.FileConfig.isUserValid()) {
            return context.getString(R.string.account_permission_prompt_user_invalid);
        } else {
            return super.getPrompt(context);
        }
    }

    @Override
    public boolean judgeUse(String condition, OnBlockCallback callback) {
        if (LoginCfgConstant.SWITCH_4G.equals(condition)
                || LoginCfgConstant.CREATE_USER.equals(condition)) {
            //针对访客模式下 切换4G 和 创建账户时 的特殊处理(可用否依赖于是否有用户信息)
            return ConfigManager.FileConfig.isUserValid();
        } else {
            return super.judgeUse(condition, callback);
        }

    }
}
