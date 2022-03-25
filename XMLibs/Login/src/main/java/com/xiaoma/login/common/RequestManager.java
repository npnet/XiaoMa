package com.xiaoma.login.common;

import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.BuildConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.common.model.CurrentDate;
import com.xiaoma.login.common.model.OnlyCode;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youthyJ
 * @date 2018/10/15
 */
public class RequestManager {

    private static final String prefix = ConfigManager.EnvConfig.getEnv().getBusiness();
    private static final String prefix_notoken = prefix.replace("/rest/", "/notoken/");

    private static final String LOGIN_BY_IMEI_URL = prefix + "user/registerOrLoginByImei.action";
    private static final String GET_USER_BY_ICCID_URL = prefix + "user/getUserByIccid.action";
    private static final String CHECK_PHONE_VIN_URL = prefix_notoken + "user/checkUserPhoneVin.action";
    private static final String GET_SMS_CODE_URL = prefix_notoken + "user/sendSmsV2.action";
    private static final String CREATE_CHILD_ACCOUNT_URL = prefix + "user/addSubUser";
    private static final String BIND_CAR_KEY_URL = prefix + "user/bindKey";
    private static final String FACTORY_LOGIN_URL = prefix + "user/getFactoryUser.action";
    private static final String GET_USER_BY_ID = prefix + "user/getUserById.action";
    private static final String VERIFY_MODIFY_PASSWORD_URL = prefix + "user/checkOrEditPassword";
    private static final String userEditUrl = prefix + "user/edit.action";
    private static final String PULL_SERVER_NEW_DATE_URL = prefix + "getNowDate";
    private static final String ACTIVE_URL = prefix + "production/active.action";
    private static final String CHECK_USER_NAME_URL = prefix + "user/checkNameUsable";
    private static final String GET_USER_LIST_BY_ICCID_URL = prefix + "user/getExtendUserList";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void registerOrLoginByImei(String imei,
                                             final ResultCallback<XMResult<User>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("imei", imei);
        request(LOGIN_BY_IMEI_URL, params, callback);
    }

    public static void getUserByIccid(String iccid,
                                      final ResultCallback<XMResult<User>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("iccid", iccid);
        request(GET_USER_BY_ICCID_URL, params, callback);
    }

    //短信验证码校验
    public static void checkPhoneCode(String phone, String vin,
                                      final ResultCallback<XMResult<String>> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("vin", vin);
        request(CHECK_PHONE_VIN_URL, params, callback);
    }

    /**
     * 验证密码
     * 该接口其实为验证或或修改密码接口，这里只用于验证
     *
     * @param userId   用户Id
     * @param password 用户密码
     */
    public static void verifyUserPassword(long userId,
                                          String password,
                                          ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        params.put("password", password);
        params.put("type", 0);
        request(VERIFY_MODIFY_PASSWORD_URL, params, callback);
    }

    /**
     * 工厂模式登录
     *
     * @param phone    phone
     * @param vin      vin
     * @param callback callback
     */
    public static void factoryLogin(String phone, String vin,
                                    final ResultCallback<XMResult<User>> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", vin);
        request(FACTORY_LOGIN_URL, params, callback);
    }

    //获取用户登录验证码
    public static void getVerifyCode(String phone,
                                     final ResultCallback<XMResult<Object>> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        request(GET_SMS_CODE_URL, params, callback);
    }

    /**
     * 绑定钥匙到用户
     *
     * @param uid      uid
     * @param carKey   carKey
     * @param passwd   passwd
     * @param type     钥匙type
     * @param callback callback
     */
    public static void bindKey(String uid, String carKey, String passwd, int type,
                               final ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("key", carKey);
        params.put("type", type);
        params.put("password", passwd);
        request(BIND_CAR_KEY_URL, params, callback);
    }

    /**
     * 创建子账户
     *
     * @param password 密码
     * @deprecated
     */
    public static void createSubAccount(String password,
                                        ResultCallback<XMResult<User>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("firstPassword", password);
        params.put("secondPassword", password);
        request(CREATE_CHILD_ACCOUNT_URL, params, callback);
    }

    /**
     * 创建子账户
     *
     * @param password 密码
     */
    public static void createSubAccount(String password,
                                        String commonKey,
                                        String bluetoothKey,
                                        String faceId,
                                        String name,
                                        int gender,
                                        String age,
                                        String birthday,
                                        int relation,
                                        String phone,
                                        ResultCallback<XMResult<User>> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("firstPassword", password);
        params.put("secondPassword", password);
        params.put("commonKey", commonKey);
        params.put("bluetoothKey", bluetoothKey);
        params.put("faceId", faceId);
        params.put("name", name);
        params.put("gender", gender);
        params.put("age", age);
        params.put("birthday", birthday);
        params.put("relation", relation);
        params.put("phone", phone);
        request(CREATE_CHILD_ACCOUNT_URL, params, callback);
    }

    private static <Bean> void request(String url, Map params, final
    ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp xmHttp = XmHttp.getDefault();
        xmHttp.setRetryCount(2);
        xmHttp.getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null) {
                    callback.onFailure(-1, "result parse failure");
                    return;
                } else if (!result.isSuccess()) {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    /**
     * POST请求
     *
     * @param url      接口地址
     * @param params   表单数据
     * @param callback 回调
     * @param <Bean>   实体类
     */
    public static <Bean> void postRequest(String url, Map params, final
    ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp xmHttp = XmHttp.getDefault();
        xmHttp.setRetryCount(2);
        xmHttp.postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()
                            [0]).getActualTypeArguments()[0];
                    String data = response.body();
                    XMResult<Bean> result = GsonHelper.fromJson(data, type);
                    if (result == null || !result.isSuccess()) {
                        callback.onFailure(-1, "result parse failure");
                        return;
                    }
                    callback.onSuccess(result);
                } catch (Exception e) { //捕捉数据处理过程中可能出现的异常
                    callback.onFailure(-1, "some exceptions have occurred");
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(-1, "error");
            }
        });
    }

    public static void getUserById(long id, ResultCallback<XMResult<User>> loginCallback) {
        request(GET_USER_BY_ID, Collections.<String, Object>singletonMap("id", id), loginCallback);
    }

    public static void changeUserName(String name, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("name", name);
        request(userEditUrl, params, callback);
    }

    public static void changeUserGender(int gender, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(2);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("gender", String.valueOf(gender));
        request(userEditUrl, params, callback);
    }

    public static void changeUserAge(Long birthDayLong, ResultCallback<XMResult<OnlyCode>> callback) {
        HashMap<String, Object> params = new HashMap<>(3);
        params.put("id", LoginManager.getInstance().getLoginUserId());
        params.put("birthDayLong", String.valueOf(birthDayLong));
        params.put("birthDay", simpleDateFormat.format(new Date(birthDayLong)));
        request(userEditUrl, params, callback);
    }

    /**
     * 拉取服务器最新事件作为年龄设置的基准
     */
    public static void pullServerNewDate(ResultCallback<XMResult<CurrentDate>> callback) {
        request(PULL_SERVER_NEW_DATE_URL, null, callback);
    }

    //激活车机
    public static void activeCar(ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>(6);
        params.put("deviceiccid", ConfigManager.DeviceConfig.getICCID(AppHolder.getInstance().getAppContext()));
        params.put("deviceimei", ConfigManager.DeviceConfig.getIMEI(AppHolder.getInstance().getAppContext()));
        params.put("channelId", ConfigManager.ApkConfig.getChannelID());
        params.put("carType", "");
        params.put("osVersion", ConfigManager.DeviceConfig.getOSVersion());
        params.put("versionCode", BuildConfig.VERSION_CODE);
        request(ACTIVE_URL, params, callback);
    }

    public static void checkUserName(String name, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("name", name);
        request(CHECK_USER_NAME_URL, params, callback);
    }

    public static void resetPasswd(String id, String passwd, ResultCallback<XMResult<String>> callback) {
        HashMap<String, Object> params = new HashMap<>(3);
        params.put("uid", id);
        params.put("password", passwd);
        params.put("type", 1);
        request(VERIFY_MODIFY_PASSWORD_URL, params, callback);
    }

    public static void getUserListByIccid(String iccid, ResultCallback<XMResult<List<User>>> callback) {
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("iccid", iccid);
        request(GET_USER_LIST_BY_ICCID_URL, params, callback);
    }
}
