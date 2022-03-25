package com.xiaoma.utils.logintype.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.R;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.bean.AccountType;
import com.xiaoma.utils.logintype.bean.LoginTypeConfig;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginTypeModel;

import java.util.Map;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/06
 * @Describe: 所有登录类型的基类
 *  函数介绍：
 *  1. readConfig: 读取配置文件，将其转化为java bean
 *  2. generateConfig: 构建配置文件
 *  3. buildNewLoginTypeConfig: 创建一个新的配置文件
 *  4. isClearData: 是否清理数据
 *  5. resetLoginType: 重置登录类型
 *  6. setNeedPsw: 设置是否需要密码验证
 *  7. getLoginTypeConfig: 获取登录类型配置 (Java Bean)
 *  8. resetLoginType: 重置登录类型
 */

public abstract class LoginType {
    protected Map<String, String> mConfigs;

    protected AccountType mAccountType;
    protected LoginTypeConfig mLoginTypeConfig;
    private String initLoginTypeConfigJson;

    //一般用于登录后的初始化
    public LoginType(AccountType accountType) {
        this(accountType, "");
    }
    //一般用于登录时的初始化
    public LoginType(AccountType accountType, String userId) {
        if (accountType == null) {//一般为null 都是登录的时候
            mAccountType = buildAccount();
            generateConfig(userId);
        } else {// 这里一般都是登录后 其他app初始化AccountType的时候
            mAccountType = accountType;
            readConfig();
        }
    }

    /**
     * 读取配置
     */
    private void readConfig() {
        // 读取配置文件
        String configFilePath = ConfigManager.FileConfig.getLoinTypeCfgFile().getAbsolutePath();
        String read = FileUtils.read(configFilePath);// read msg from local file
        if (!TextUtils.isEmpty(read)) {// not null 说明上次有记录
            mLoginTypeConfig = GsonHelper.fromJson(read, LoginTypeConfig.class);
            if (mLoginTypeConfig == null) {
                buildNewLoginTypeConfig("");
            }
        } else {// 如果本地没有记录，则构建新的配置
            buildNewLoginTypeConfig("");
        }
        initLoginTypeConfigJson =  GsonHelper.toJson(mLoginTypeConfig);
    }

    /**
     * 构建配置 config
     *
     * @param userId
     */
    private void generateConfig(String userId) {
//        if (mLoginTypeConfig != null) return;
        // 读取配置文件
        String configFilePath =ConfigManager.FileConfig.getLoinTypeCfgFile().getAbsolutePath();
        String read = FileUtils.read(configFilePath);// read msg from local file
        if (!TextUtils.isEmpty(read)) {// not null 说明上次有记录
            mLoginTypeConfig = GsonHelper.fromJson(read, LoginTypeConfig.class);
            if (mLoginTypeConfig != null) {
                KLog.i("filOut| "+"[generateConfig]->change the login type");
                String currentType = mLoginTypeConfig.getCurrentType();// 记录 上一次登录的类型
                String currentUsrId = mLoginTypeConfig.getCurrentUsrId();// 记录 上一次登录的UserId
                mLoginTypeConfig.setCurrentType(mAccountType.getLoginType());// 更新当前登录类型
                mLoginTypeConfig.setCurrentUsrId(userId);// 更新当前登录UserId
                mLoginTypeConfig.setHistoryType(currentType);// 设置上次登录类型
                mLoginTypeConfig.setHistoryUsrId(currentUsrId);// 设置上次登录UserId
                mLoginTypeConfig.buildApps();
                mLoginTypeConfig.reset();//将所有app重置成登录后第一次启动

            } else {// 如果本地没有记录，则构建新的配置
                buildNewLoginTypeConfig(userId);
            }
        } else {// 如果本地没有记录，则构建新的配置
            buildNewLoginTypeConfig(userId);
        }
        initLoginTypeConfigJson = GsonHelper.toJson(mLoginTypeConfig);
        FileUtils.writeCover(initLoginTypeConfigJson, configFilePath);//更新
    }

    /**
     * 构建一个全新的 LoginTypeConfig
     */
    private void buildNewLoginTypeConfig(String usrId) {
        mLoginTypeConfig = new LoginTypeConfig();
        mLoginTypeConfig.setHistoryType("");
        mLoginTypeConfig.setCurrentType(mAccountType.getLoginType());
        mLoginTypeConfig.setHistoryUsrId("");
        mLoginTypeConfig.setCurrentUsrId(usrId);
    }

    // 是否清理数据
    public boolean isClearData() {//游客(访客)和工厂模式、离线模式需要清理数据
        return !LoginTypeModel.LOGIN_NORMAL_MODEL.equals(getLoginTypeConfig().getCurrentType());
    }

    protected String writeToLocal(AccountType accountType) {
        String content = GsonHelper.toJson(accountType);
        //        content = AESUtils.encryp(content, LoginTypeConstant.LOGIN_TYPE_KEY);//加密
        FileUtils.writeCover(content, ConfigManager.FileConfig.getLoginTypeFile());
        return content;
    }

    public String setNeedPsw(boolean need) {
        if (mAccountType == null) return "";
        mAccountType.setNeedPsw(need);
        return writeToLocal(mAccountType);
    }

    public LoginTypeConfig getLoginTypeConfig() {
        // 这里要检查本地文件是否发生变化来决定是否重新构建对象
        String configFilePath = ConfigManager.FileConfig.getLoinTypeCfgFile().getAbsolutePath();
        String read = FileUtils.read(configFilePath);// read msg from local file
        if(!TextUtils.isEmpty(read) && !read.equals(initLoginTypeConfigJson)){
            initLoginTypeConfigJson =read;
            mLoginTypeConfig = GsonHelper.fromJson(initLoginTypeConfigJson, LoginTypeConfig.class);
        }
        return mLoginTypeConfig;
    }

    public void resetLoginType(){
        if(mAccountType == null) return;
        mAccountType.setLoginType(LoginTypeModel.LOGIN_NORMAL_MODEL);
        writeToLocal(mAccountType);
    }

    public String getPrompt(Context context) {
        if (context == null) return "";
        return context.getString(R.string.account_permission_prompt);
    }

    public boolean judgeUse(String condition) {
        return judgeUse(condition, null);
    }

    protected abstract AccountType buildAccount();

    //初始化访问范围配置
    public abstract void initPurviewConfig();

    // 判断功能是否能使用
    public abstract boolean judgeUse(String condition, OnBlockCallback callback);

}
