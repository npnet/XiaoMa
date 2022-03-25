package com.xiaoma.login.common;

/**
 * Created by youthyj on 2018/9/12.
 */
public class LoginConstants {

    // 以下UserId为无主账户情况下与后台约定好的特殊UserID
    public static final long TOURIST_USER_ID = 1058;

    //start activity results
    public static final int RESULT_OFFLINE_LOGIN = 1001;

    private LoginConstants() throws Exception {
        throw new Exception();
    }

    public static final String XIAOMA_SETTING_PKG = "com.xiaoma.setting";
    public static final String XIAOMA_LAUNCHER_SPLASH_ACTION = "com.xiaoma.launcher.splash";
    public static final String XIAOMA_LAUNCHER_SPLASH_URI = "xiaoma://com.xiaoma.launcher:8080/splash";
    public static final String ACTION_ON_LOGIN = "com.xiaoma.login.ON_LOGIN";
    public static final String ACTION_ON_LOGOUT = "com.xiaoma.login.ON_LOGOUT";
    public static final String ACTION_ON_USER_UPDATE = "com.xiaoma.user.ON_USER_UPDATE";


    public static final class ParamsKey {
        public static final String PARAMS_KEY_IMEI = "KEY_IMEI";
        public static final String PARAMS_KEY_ICCID = "KEY_ICCID";
        public static final String PARAMS_KEY_VIN = "KEY_VIN";

        public static final String PARAMS_KEY_ACCOUNT = "KEY_ACCOUNT";
        public static final String PARAMS_KEY_PASSWORD = "KEY_PASSWORD";

        public static final String PARAMS_KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER";
        public static final String PARAMS_KEY_SMS_CODE = "KEY_SMS_CODE";

        public static final String PARAMS_KEY_QR_CODE_DATA = "KEY_QR_CODE_URL";
        public static final String PARAMS_KEY_REGISTER_STATUS = "KEY_REGISTER_STATUS";
    }

    public interface IntentKey {
        String USER = "user";
        String USER_ID = "user_id";
        String USER_PASSWD = "user_passwd";
        String CAR_KEY = "car_key";
        String FACE_ID = "face_id";
        String ERROR_CODE = "error_code";
        String SWITCH_ACCOUNT = "switch_account";
        String HIDE_NAV_BAR = "hide_nav_bar";
        String CANCELLATION = "cancellation";
        String PASSWD_TEXTS = "passwd_texts";
        String RECORD_TO_CREATE = "record_to_create";
        String RECORD_TO_CREATE_BOUND_FACEID = "record_to_create_bound_faceid";
    }

    public interface KeyBind {
        String SERVICE_LOCATION = "com.xiaoma.launcher";
        int SERVICE_PORT = 1080;

        interface Action {
            int UNBIND_KEY = 1;
            int REMOVE_USER = 2;
            int SAVE_USER = 3;
            int GET_USERS = 4;
            int GET_KEY_BIND_USER = 5;
            int CONNECT = 6;
        }

        interface BundleKey {
            String USER_ID = "user_id";
            String KEY_ID = "key_id";
            String IS_BLUETOOTH = "is_bluetooth";
            String USER = "user";
            String USERS = "users";
            String RESULT = "result";

            String APP_LOGIN_TYPE = "app_login_type";
            String APP_LOGIN_EXTRA = "app_login_extra"; //额外数据，用于判断启动那个app
            String APP_NEED_MODIFY_THEME = "app_need_modify_theme";//修改theme

        }
    }

    public interface UserField {
        String ID = "id";
        String BLUETOOTH_KEY_ID = "bluetoothKey";
        String NORMAL_KEY_ID = "commonKey";
        String FACE_ID = "faceId";
        String IS_MASTER_ACCOUNT = "masterUser";
    }

    public interface FaceRecRes {
        int FAIL = -1;
        int INVALID = 0;
        int SUCCESS = 1;
    }

    public static final String LOGIN_STATUS_AES_KEY = "xiaomalixing6666";
    public static final String USER_AES_KEY = "6666xiaomalixing";
}
