package com.xiaoma.network;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class ErrorCodeConstants {

    //网络错误
    //    NetworkNotAvilable("Network Is Not Avilable", "未连接网络"),
    //    NetworkUnstable("Service UnreachNetwork Is Unstable", "无法访问网络"),
    //    NetworkDisabled("Current Network Is Disabled By Your Setting", "已禁用该网络类型");
    public static final int HTTP_NET_ERROR = 10001;
    //服务器错误，此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
    //    ServerInnerError("Server Inner Exception", "服务器内部异常"),
    //    ServerRejectClient("Server Reject Client Exception", "服务器拒绝或无法提供服务"),
    //    RedirectTooMuch("Server RedirectTooMuch", "重定向次数过多");
    public static final int HTTP_SERVER_ERROR = -1;
    //客户端请求错误
    //    UrlIsNull("Url is Null!", "Url 为空!"),
    //    IllegalScheme("illegal scheme!", "非法的Scheme"),
    //    ContextNeeded("(Detect or Disable Network, etc) Need Context.", "（探测和禁用网络等）需要 Context"),
    //    PermissionDenied("Missing NETWORK-ACCESS Permission in Manifest?", "未获取访问网络权限"),
    //    SomeOtherException("Client Exception", "客户端发生异常");
    public static final int HTTP_CLIENT_ERROR = 10003;
    //无返回错误
    public static final int RETURN_NOTHING_ERROR = 10005;
    //用户列表为空错误
    public static final int USERLIST_EMPTY_ERROR = 10007;
    //Token为空错误
    public static final int TOKEN_NULL_ERROR = 10008;
    //用户为空错误
    public static final int USER_NULL_ERROR = 10009;
    //JSON解析错误
    public static final int JSON_ANALYSIS_ERROR = 10010;
    //文件不存在错误
    public static final int FILE_NOT_FOUND_ERROR = 10011;
    //订单号为空错误
    public static final int ORDER_NUMBER_EMPTY_ERROR = 10012;
    //账号为空错误
    public static final int ACCOUNT_EMPTY_ERROR = 10013;


    /**
     * 服务器错误码 ,此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
     **/
    //Token过期错误，此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
    public static final int TOKEN_EXPIRED_ERROR = 9999;
    //用户名已经存在错误，此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
    public static final int USER_NAME_EXISTED_ERROR = 1028;
    //车牌号已经存在错误，此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
    public static final int CAR_NUMBER_EXISTED_ERROR = 1029;
    //车架号已经存在错误，此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
    public static final int FRAME_NUMBER_EXISTED_ERROR = 1056;
    //发动机号已经存在错误，此errorCode和服务器返回BaseResult的resultCode对应，不可随意更改
    public static final int ENGINE_NUMBER_EXISTED_ERROR = 1057;
    //激活失败
    public static final int ACTIVE_FAILED_ERROR = 1099;
    //操作成功
    public static final int RESULT_SUCCESS = 1;
    //1001	老用户验证码错误
    public static final int OLDUSER_IDENTIFY_CODE_ERROR = 1001;
    //1002	新用户验证码错误
    public static final int NEWUSER_IDENTIFY_CODE_ERROR = 1002;
    //1003	注册环信用户失败
    public static final int HX_REGISTER_ERROR = 1003;
    //1004	注册云通讯用户失败
    public static final int YUNCHAT_REGISTER_ERROR = 1004;
    //1005	非群主不能解散群
    public static final int DISSOLVE_GROUP_POWER_ERROR = 1005;
    //1006	找不到该用户
    public static final int NOT_FOUND_USER_ERROR = 1006;
    //1007	找不到该群
    public static final int NOT_FOUND_GROUP_ERROR = 1007;
    //1008	用户不能重复加群
    public static final int REPEAT_GROUP_ERROR = 1008;
    //1009	用户建群失败
    public static final int CREATE_GROUP_ERROR = 1009;
    //1010	用户不在该群
    public static final int USER_NOT_FOUND_IN_GROUP_ERROR = 1010;
    //1011	快速建群失败
    public static final int QUICK_CREATE_GROUP_ERROR = 1011;
    //1012	推送导航信息失败
    public static final int PUSH_LOCATION_FAILED_ERROR = 1012;
    //1013	拉取导航信息失败
    public static final int GET_LOCATION_FAILED_ERROR = 1013;
    //1014	注册米糠云分机号失败
    public static final int REGISTER_MIKANG_FAILED_ERROR = 1014;
    //1015	未找到该环信号对应的用户
    public static final int HX_ACCOUNT_NOT_FOUND_ERROR = 1015;
    //1016	修改环信群名称失败
    public static final int RENAME_HX_GROUP_FAILED_ERROR = 1016;
    //1017	上传用户头像失败
    public static final int PUSH_HEAD_IMG_FAILED_ERROR = 1017;
    //1018	已做过问卷调查
    public static final int HAD_FINISH_SURVEY_ERROR = 1018;
    //1019	未完成全部答题
    public static final int NOT_FINISH_SURVEY_ERROR = 1019;
    //1020	已经创建相关账号
    public static final int ACCOUNT_HAD_CREATED_ERROR = 1020;
    //1021	未找到该用户
    public static final int USER_NOT_FOUND_ERROR = 1021;
    //1022	用户未完成初始化
    public static final int UNFINISH_INIT_ERROR = 1022;
    //1023	用户已完成初始化
    public static final int FINISH_INIT_ERROR = 1023;
    //1024	操作失败
    public static final int RESULT_ERROR = 1024;
    //1025	权限不足
    public static final int PERMISSION_DENIED_ERROR = 1025;
    //1026	数据列表为空
    public static final int LIST_EMPTY_ERROR = 1026;
    //1027	未找到该数据
    public static final int DATA_NOT_FOUND_ERROR = 1027;
    //1030	超过当日申请好友最大次数
    public static final int OVER_MAX_APPLY_ERROR = 1030;
    //1031  数据已删除
    public static final int DATA_BEEN_DELETED_ERROR = 1031;
    //1041 短信超过最大次数
    public static final int SMS_SEND_OVER_MAX = 1041;
    //1080 获取验证码失败
    public static final int SMS_SEND_ERROR = 1080;
    //1062 白名单用户
    public static final int SMS_SEND_WHITE_LIST = 1062;


    //用户非法
    public static final int ILLEGAL_USER = 1043;

    public static final int ERROR_1048_CODE = 1048;

    public static final int BIND_KEY_USER_NOT_FOUND = 40001;

    // 输错密码达到最大次数限制
    public static final int ERROR_DISABLE_LOGIN_CODE = 40062;

    public static final int LOGIN_PASSWD_ERROR_CODE = 40056;//密码校验错误

    public static final int USER_HAS_CHANGED_CODE = 1060;

    public static final int USER_NOT_EXIST_CODE = 1006; // 用户不存在

    public static final int USER_DISABLE_CODE = 40064; // 用户被禁用

    public static final int USER_LOGOFF_CODE = 1606; // 用户已注销
}
