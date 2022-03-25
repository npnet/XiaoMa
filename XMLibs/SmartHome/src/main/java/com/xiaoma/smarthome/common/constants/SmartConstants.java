package com.xiaoma.smarthome.common.constants;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common
 *  @file_name:      SmartConstants
 *  @author:         Rookie
 *  @create_time:    2019/2/27 19:40
 *  @description：   TODO             */

import com.xiaoma.component.AppHolder;
import com.xiaoma.smarthome.R;

public interface SmartConstants {

    String KEY_MI_USER_INFO = "KEY_MI_USER_INFO";

    String DEVICE_TYPE = "1";
    String SCENE_LEAVE_HOME = "离家";
    String SCENE_ARRIVE_HOME = "回家";


    String GO_HOME = AppHolder.getInstance().getAppContext().getString(R.string.go_home_mode);
    String OUT_HOME = AppHolder.getInstance().getAppContext().getString(R.string.out_home_mode);

    int ERROR_CODE_UNLOGIN = 20;
    int ERROR_CODE_OTHERS = 10;
    int ERROR_CODE_TIMEOUT = 30;


    int TIME_OUT_TIME = 15000;

    String KEY_LOGIN_ACCOUNT = "key_login_account";
    String KEY_ARRIVE_ADDRESS_INFO = "key_arrive_address_info";
//    String KEY_LEAVE_ADDRESS_INFO = "key_leave_address_info";

    String KEY_ARRIVE_XIAOBAI_IS_AUTO = "key_arrive_xiaobai_is_auto";
    String KEY_LEAVE_XIAOBAI_IS_AUTO = "key_leave_xiaobai_is_auto";

    String KEY_ARRIVE_EXECUTE_CONDITION = "key_arrive_execute_condition";
    String KEY_LEAVE_EXECUTE_CONDITION = "key_leave_execute_condition";

    String REFRESH_SCENE = "refresh_scene";
    String REFRESH_XIAO_BAI_SCENE = "refresh_xiao_bai_scene";
    int TYPE_LEAVE_HOME = 1;
    int TYPE_ARRIVE_HOME = 2;

    //event
    String SCENE_LIST = "scene_list";
    String EXECUTION_SCENE = "execution_scene";
    String REFRESH_SCENE_LIST = "refresh_scene_list";
    String REFRESH_DEVICES = "refresh_devices";
    String REFRESH_SCENE_LIST_TAT = "refresh_scene_list_iat";
    String SHOW_SETTING_ADRESS_DIALOG = "show_setting_adress_dialog";

    //save object
    String HOME_BEAN = "home_bean";


    //触发开关，1表示开,0表示关，默认关
    int AUTO_EXCUTE_ON = 1;
    int AUTO_EXCUTE_OFF = 0;
    //触发规则，1表示大于，0表示小于
    int BEYOND = 1;
    int INNER = 0;

    String CM_LOGIN_TYPE = "android";
    String CM_APPKEY = "fawcar";

    String CM_LOGIN_FLAG = "login_cm";

    //云米登录过期
    int YUN_MI_LOGIN_EXPIRED = 919;
}
