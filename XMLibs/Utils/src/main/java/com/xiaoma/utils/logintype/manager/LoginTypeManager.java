package com.xiaoma.utils.logintype.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.R;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.bean.AccountType;
import com.xiaoma.utils.logintype.bean.LoginTypeConfig;
import com.xiaoma.utils.logintype.callback.AbsClearDataListener;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginTypeConstant;
import com.xiaoma.utils.logintype.constant.LoginTypeModel;

import java.io.File;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/06
 * @Describe: 登录类型管理类：供外部调用,处理一些与登录类型相关的逻辑
 * 函数介绍：
 * 1. init: 登录类型初始化，通常调用于登录成功后
 * 2. delete: 删除登录类型配置文件
 * 3. getLoginType: 获取当前登录类型，通常有<>VisitorLoginType: 访客模式(无用户信息),TravellerLoginType: 游客模式(有用户信息),
 * SubKeyBindLoginType: 子账号登录, FactoryLoginType: 工厂模式登录</>
 * 4. judgeUse: 判断该功能是否能使用
 * 5. setNeedPsw: 设置是否需要密码验证
 * 6. getPrompt: 获取提示语
 * 7. isVisitorType: 是否是访客模式
 * 8. resetLoginType: 重置登录模式
 * 9. canSendRequest: 是否能发送请求
 * 10.chooseAccount: 选择账号登录
 * 11.keyVerificationAndStartApp: 验证密码，并在密码验证成功后，启动App
 * 12.keyVerificationAndStartAct: 验证密码，并在密码验证成功后，启动某个Activity
 * 13.keyVerify: 密码验证，准备一些密码验证时需要的数据
 * 14.jumpAfterKeyVerify: 密码验证成功后的跳转逻辑
 * 15.setChooseAccount: 准备一些选择账登录时需要的数据
 * 16.writeLocal: 写入本地数据
 * 17.getClearDataListener: 获取清理数据的所有Listener
 * 18.registerClearDataListener: 注册清理数据Listener
 * 19.isClearAllData: 是否清理所有数据
 * 20.sendNormalBroadcastForClearData/sendStickBroadcastForClearData: 发送清理数据的广播
 * 21.isLoginTypeConfigExist  是否存在LoginTypeConfig文件
 * 22.historyUserIdIsEmpty 历史用户ID是否为空
 */

public class LoginTypeManager {
    private LoginType currentLoginType;
    private String initialAccountTypeJson;
    private LoginTypeHelper mLoginTypeHelper;

    private LoginTypeManager() {
        mLoginTypeHelper = new LoginTypeHelper();
    }

    public static LoginTypeManager getInstance() {
        return Holder.mTypeManager;
    }

    public void init(LoginType loginType) {
        this.currentLoginType = loginType;
        File loginTypeFile = ConfigManager.FileConfig.getLoginTypeFile();
        initialAccountTypeJson = FileUtils.read(loginTypeFile);
    }

    public void delete() {
        File loginTypeFile = ConfigManager.FileConfig.getLoginTypeFile();
        FileUtils.delete(loginTypeFile);
    }

    public LoginType getLoginType() {
        File loginTypeFile = ConfigManager.FileConfig.getLoginTypeFile();
        String localAccountTypeJson = FileUtils.read(loginTypeFile);

        // 本地不存在账户类型信息,直接返回主账户类型
        if (TextUtils.isEmpty(localAccountTypeJson)) {
            currentLoginType = new NormalLoginType(null);
            return currentLoginType;
        }
        // 已初始化过,直接返回账户类型
        if (localAccountTypeJson.equals(initialAccountTypeJson) && currentLoginType != null) {
            return currentLoginType;
        }
        // 重新配置登录类型
        initialAccountTypeJson = localAccountTypeJson;
        AccountType accountType = GsonHelper.fromJson(initialAccountTypeJson, AccountType.class);
        if (accountType == null) {
            accountType = new AccountType();
        }
        if (TextUtils.isEmpty(accountType.getLoginType())) {
            accountType.setLoginType(LoginTypeModel.LOGIN_NORMAL_MODEL);
        }

        switch (accountType.getLoginType()) {
            // 普通账户登录
            default:
            case LoginTypeModel.LOGIN_NORMAL_MODEL:
                currentLoginType = new NormalLoginType(accountType);
                break;
            // 工厂模式
            case LoginTypeModel.LOGIN_FACTORY_MODEL:
                currentLoginType = new FactoryLoginType(accountType);
                break;
            // 游客模式
            case LoginTypeModel.LOGIN_TRAVELLER_MODEL:
                currentLoginType = new TravellerLoginType(accountType);
                break;
            // 访客模式
            case LoginTypeModel.LOGIN_VISITOR_MODEL:
                currentLoginType = new VisitorLoginType(accountType);
                break;
        }
        return currentLoginType;
    }

    /**
     * @param condition 条件
     * @return 返回true，说明功能能使用，返回false，说明功能不能使用
     */
    public boolean judgeUse(String condition) {
        return judgeUse(condition, null);
    }

    /**
     * @param condition 条件
     * @param callback  回调
     * @return 返回true，说明功能能使用，返回false，说明功能不能使用
     */
    public boolean judgeUse(String condition, OnBlockCallback callback) {
        currentLoginType = getLoginType();
        return currentLoginType.judgeUse(condition, callback);
    }

    /**
     * 设置是否需要密码
     *
     * @param need
     */
    public void setNeedPsw(boolean need) {
        initialAccountTypeJson = getLoginType().setNeedPsw(need);
    }


    public static String getPrompt(Context context) {
        if (context == null) return "";
        return context.getString(R.string.account_permission_prompt);
    }

    public boolean canUse(String condition, final OnBlockCallback callback) {
        if (TextUtils.isEmpty(condition)) return true;
        return LoginTypeManager.getInstance().judgeUse(condition, new OnBlockCallback() {
            @Override
            public void handle(LoginType loginType) {
                if (callback == null) return;
                callback.handle(loginType);
            }
        });
    }

    /**
     * 是否是访客模式
     *
     * @return
     */
    public boolean isVisitorType() {
//        return getLoginType() instanceof VisitorLoginType;
        return false;
    }

    /**
     * 重置登录模式
     */
    public void resetLoginType() {
        getLoginType().resetLoginType();
    }

    /**
     * 是否能发送请求（访客模式（无用户信息请求下无法使用在线功能））
     *
     * @param context
     * @return 不是访客模式（无用户信息）或者访客模式（有用户信息）且是WIFI环境下 为：true，可以发送请求
     */
    public boolean canSendRequest(Context context) {
        if (context == null) return true;
        return !isVisitorType() || (isVisitorType() && NetworkUtils.isConnected(context));
    }

    /**
     * 选择帐号登录
     *
     * @param context     上下文
     * @param packageName 登录成功后的跳转的app的包名
     */
    public void chooseAccount(Context context, String packageName) throws Exception{
        chooseAccount(context, null, packageName);
    }

    /**
     * 选择帐号登录
     *
     * @param context     上下文
     * @param bundle
     * @param packageName 登录成功后的跳转的app的包名
     */
    public void chooseAccount(Context context, Bundle bundle, String packageName) throws Exception {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context, LoginTypeConstant.CHOOSE_ACCOUNT_ACTIVITY);
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        //            setChooseAccount(packageName);
    }

    /**
     * 密码验证 并 启动app
     *
     * @param context     上下文
     * @param extraBundle 额外的bundle数据
     * @param packageName 需要启动的app的包名
     */
    public void keyVerificationAndStartApp(Context context, Bundle extraBundle, String packageName) {
        Intent intent = new Intent();
        String keyVerifyActName = LoginTypeConstant.SUBVERIFYPSWACTIVITY;
        ComponentName componentName = new ComponentName(context, keyVerifyActName);
        intent.setComponent(componentName);
        intent.putExtras(extraBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            keyVerify(true, packageName, null);
        } catch (Exception e) {
            KLog.d("activity start error :" + e);
        }
    }

    /**
     * 密码验证 并 启动act
     *
     * @param context     上下文
     * @param extraBundle 额外的bundle数据
     * @param actName     需要启动的act的全类名
     */
    public void keyVerificationAndStartAct(Context context, Bundle extraBundle, String actName) {
        Intent intent = new Intent();
        String keyVerifyActName = LoginTypeConstant.SUBVERIFYPSWACTIVITY;
        ComponentName componentName = new ComponentName(context, keyVerifyActName);
        intent.setComponent(componentName);
        intent.putExtras(extraBundle);
        try {
            context.startActivity(intent);
            keyVerify(false, null, actName);
        } catch (Exception e) {
            KLog.d("activity start error :" + e);
        }
    }

    /**
     * 设置创建子账号
     *
     * @param pkgName 需要启动app的包名
     */
    private void setChooseAccount(String pkgName) {
        mLoginTypeHelper.reset();//重置
        mLoginTypeHelper.setChooseAccount(true);
        mLoginTypeHelper.setStartAfterChooseAccountAppPkg(pkgName);
    }

    /**
     * 密码验证
     *
     * @param isJumpApp 是否启动应用
     * @param appPkg    app包名
     * @param actName   activity全类名
     */
    private void keyVerify(boolean isJumpApp, String appPkg, String actName) {
        mLoginTypeHelper.reset();
        mLoginTypeHelper.setKeyVerify(true);
        mLoginTypeHelper.setJumpApp(isJumpApp);
        mLoginTypeHelper.setStartAfterKeyVerifyActName(actName);
        mLoginTypeHelper.setStartAfterKeyVerifyAppPkg(appPkg);
    }

    /**
     * 密码验证成功后的跳转
     *
     * @param context 上下文
     */
    public void jumpAfterKeyVerify(Context context) {
        // 如果不是子账号模式下的密码验证
        if (!mLoginTypeHelper.isKeyVerify()) return;
        LoginTypeManager.getInstance().setNeedPsw(false);//将需要密码修改成false
        if (mLoginTypeHelper.isJumpApp()) { //启动app
            if (TextUtils.isEmpty(mLoginTypeHelper.getStartAfterKeyVerifyAppPkg())) return;
            LaunchUtils.launchApp(context, mLoginTypeHelper.getStartAfterKeyVerifyAppPkg());
        } else {
            if (TextUtils.isEmpty(mLoginTypeHelper.getStartAfterKeyVerifyActName())) return;
            LaunchUtils.launchApp(context, context.getPackageName(), mLoginTypeHelper.getStartAfterKeyVerifyActName());
        }
        mLoginTypeHelper.reset();
    }

    public static void writeLocal(Object obj, String filePath) {
        if (obj == null || filePath == null) return;
        String content = GsonHelper.toJson(obj);
        //        content = AESUtils.encryp(content, LoginTypeConstant.LOGIN_TYPE_KEY);//加密
        FileUtils.writeCover(content, filePath);

    }

    public List<AbsClearDataListener> getClearDataListener() {
        return mLoginTypeHelper.getAbsClearDataListeners();
    }

    /**
     * 注册是否清理app里面的数据
     */
    public void registerClearDataListener(AbsClearDataListener listener) {
        mLoginTypeHelper.setAbsClearDataListeners(listener);
    }

    /**
     * 是否存在LoginTypeConfig文件
     *
     * @return
     */
    private boolean isLoginTypeConfigExist() {
        return getLoginType().getLoginTypeConfig() != null && getLoginType().getLoginTypeConfig().getApps() != null;
    }

    /**
     * 历史用户ID是否为空
     */
    private boolean historyUserIdIsEmpty() {
        return TextUtils.isEmpty(getLoginType().getLoginTypeConfig().getHistoryUsrId());
    }

    /**
     * 是否清理所有数据
     *
     * @return
     */
    public boolean isClearAllData() {
        return !(getLoginType() instanceof NormalLoginType);
    }

    public boolean isClearSwicthData() {
        if (isClearAllData()) return true;
        LoginTypeConfig loginTypeConfig = getLoginType().getLoginTypeConfig();
        try {
            if (loginTypeConfig.getCurrentUsrId().equals(loginTypeConfig.getHistoryUsrId())) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public void unRegisterClearDataListener(AbsClearDataListener listener) {
        if (mLoginTypeHelper.getAbsClearDataListeners() == null
                || !mLoginTypeHelper.getAbsClearDataListeners().contains(listener)) {
            return;
        }
        mLoginTypeHelper.getAbsClearDataListeners().remove(listener);
    }

    @SuppressLint("MissingPermission")
    public void sendBroadcastForClearData(Context context, long userId, String loginMethod) {
        Intent intent = new Intent(LoginTypeConstant.BROADCAST_ACTION_SWITCH_USER_CLEAR);
        intent.putExtra(LoginTypeConstant.KEY_LOGIN_METHOD, loginMethod);
        intent.putExtra(LoginTypeConstant.KEY_LOGIN_USER_ID, userId);
        context.sendBroadcast(intent);
    }

    private static class Holder {
        public static LoginTypeManager mTypeManager = new LoginTypeManager();
    }
}
