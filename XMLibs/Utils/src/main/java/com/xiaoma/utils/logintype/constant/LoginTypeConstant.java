package com.xiaoma.utils.logintype.constant;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/10
 * @Describe: 登录类型常量：一些常量标识
 */

public class LoginTypeConstant {

    /******************** static final field **********************/
    public static final String KEY_LOGIN_METHOD = "key_login_method";
    public static final String KEY_LOGIN_USER_ID = "key_login_user_id";



    public static final String CREATECHILDACCOUNTACTIVITY = "com.xiaoma.login.business.ui.subaccount.CreateSubAccountActivity";
    public static final String CHOOSE_ACCOUNT_ACTIVITY = "com.xiaoma.login.business.ui.ChooseUserActivity";
    public static final String CHOOSE_ACCOUNT_PRO_ACTIVITY = "com.xiaoma.login.business.ui.ChooseUserProActivity";
    public static final String SUBVERIFYPSWACTIVITY= "com.xiaoma.login.business.ui.password.SubVerifyPswActivity";

    public static final String BROADCAST_ACTION_SWITCH_USER_CLEAR = "broadcast_action_switch_user_clear";

    /***************** static field *******************/
    public static boolean isInitiativeClose = false;

    public static boolean ignoreLoginType=false;
}
