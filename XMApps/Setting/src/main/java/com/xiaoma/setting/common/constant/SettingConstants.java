package com.xiaoma.setting.common.constant;


/**
 * @author: iSun
 * @date: 2018/10/15 0015
 */
public class SettingConstants {
    //接口测试Log开关
    public final static boolean SHOW_DEBUG_LOG = true;
    public final static boolean DOOR_POWER = true;//暗门开关
    public final static int[] DOOR_PASSWORD = {3, 2, 3, 4, 1};//暗门密码
    public static final long DOOR_INTERVALS = 700;//暗门间隔时间
    public static String PACKAGE_SYSTEM_SETTING = "com.android.settings";

    /*-------------BluetoothReceiver---------------------*/
    public static String BT_A2DP_SINK_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED";
    public static String BT_HEADSETCLINET_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED";
    public static String BT_PBAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.pbapclient.profile.action.CONNECTION_STATE_CHANGED";
    public static String BT_MAP_CLIENT_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.mapmce.profile.action.CONNECTION_STATE_CHANGED";
    public static String BT_AVRCP_CONTROLLER_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.avrcp-controller.profile.action.CONNECTION_STATE_CHANGED";

    /*-------------DialogDispatch---------------------*/
    public static final int BT_DEVICE_PAIRING_VARIANT_PIN = 0;
    public static final int BT_DEVICE_PAIRING_VARIANT_PIN_16_DIGITS = 7;
    public static final int BT_DEVICE_PAIRING_VARIANT_PASSKEY = 1;
    public static final int BT_DEVICE_PAIRING_VARIANT_CONSENT = 3;
    public static final int BT_DEVICE_PAIRING_VARIANT_OOB_CONSENT = 6;
    public static final int BT_DEVICE_PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    public static final int BT_DEVICE_PAIRING_VARIANT_DISPLAY_PIN = 5;
    public static final int BT_DEVICE_PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;

    /*-------------BluetoothService---------------------*/

    public static final int COMMON_USER_NO_LOGIN = -1;
    public static final int FACTORY_USER_NO_LOGIN = -2;
    public static final int COMMON_USER = 1;

    public static final String ACTION_JSON="action_json";
    public static final String WINDOW_BEAN ="window_bean";

    // 车辆设置--安全模式

    // 前防撞预警/主动制动
    public static final int SAFE_STRIKE_WARNING = 1001;
    // 车道偏离警示
    public static final int SAFE_DIVERGE_WARNING = 1002;
    // 车道保持模式
    public static final int SAFE_LANE_KEEPING_MODE = 1003;
    // 交通标志识别
    public static final int SAFE_TRAFFIC_SIGN_RECOGNITION = 1004;
    // 驾驶员注意力提醒
    public static final int SAFE_DRIVER_ATTENTION_REMINDER = 1005;
    // 电子驻车自动夹紧
    public static final int SAFE_ELECTRONIC_BRAKE = 1025;
    // 后排安全带提醒
    public static final int SAFE_REAR_SEATS_BELT_REMINDER = 1006;
    //外后视镜随动rearveiwMirror
    public static final int REAR_VIEW_MIRROR = 1007;

    // 车辆设置--舒适模式
    // 遥控解锁模式
    public static final int COMFORT_TELECONTROL_UNLOCK_MODE = 1007;
    // 门锁控制-熄火自动解锁
    public static final int COMFORT_EXIT_AUTOMATIC_LOCK = 1008;
    // 门锁控制-走进自动解锁
    public static final int COMFORT_APPROACH_AUTO_UNLOCK = 1009;
    // 门锁控制-走远自动闭锁
    public static final int COMFORT_GO_FAR_AUTO_LOCK = 1010;
    // 门锁控制-锁车自动关窗
    public static final int COMFORT_LOCK_CAR_WITH_CLOSE_WINDOW = 1011;
    // 门锁控制-随速闭锁
    public static final int COMFORT_SPEED_LOCK_CONTROL = 1026;
    // 行李箱智能开启
    public static final int COMFORT_TRUNK_AUTO_OPEN = 1012;
    // 锁车外后视镜自动折叠
    public static final int COMFORT_DOOR_LOCK_REARVIEW_MIRROR_AUTO_FOLD = 1013;
    // 座椅迎宾退让
    public static final int COMFORT_SEAT_GIVE_AWAY = 1014;
    // 灯光设置
    // 回家模式
    public static final int LAMPLIGHT_GO_HOME_MODE = 1015;
    // 室内灯延时设置
    public static final int LAMPLIGHT_INDOOR_LIGHT_DELAY_SETTING = 1016;
    // 迎宾灯设置
    public static final int LAMPLIGHT_WELCOME_LIGHT_SETTING = 1017;
    // 智能远光
    public static final int LAMPLIGHT_SMART_HEAD_LIGHT = 1018;
    // 氛围灯
    public static final int LAMPLIGHT_ATMOSPHERE_LIGHT = 1019;
    // 音乐情景随动
    public static final int LAMPLIGHT_SCENE_LIGHT = 1020;
    // 人脸识别系统
    public static final int FACE_RECOGNIGE_SYSTEM = 1021;
    // 人脸识别灵敏度调整
    public static final int FACE_SENSITIVITY_ADJUST = 1025;
    // 疲劳提醒
    public static final int FACE_FATIGUE_NOTIFICATION = 1022;
    // 视野分散提醒
    public static final int FACE_SIGHT_NOTIFICATION = 1023;
    // 不良驾驶行为提醒
    public static final int FACE_BAD_ATION_NOTIFICATION = 1024;
    //驾驶员状态监测
    public static final int FACE_DAW = 1025;

    public static final String OS_VERSION = "os_version";
    public static final String MCU_VERSION = "mcu_version";


}
