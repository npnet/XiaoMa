package com.xiaoma.carlib.constant;


/**
 * @author: iSun
 * @date: 2018/10/22 0022
 */
public class SDKConstants {
    public static int DEFAULT_INT = -1;
    public static String DEFAULT_STRING = "";
    public static float DEFAULT_FLOAT = -1.0f;

    public static final int SpeechOnOff_1_ON_REQ = 0;
    public static final int SpeechOnOff_1_INACTIVE_REQ = 0;
    //CarVendorExtensionManager
    public static int ALC_LEVEL = 0;//车速音量补偿, int type 获取和设置车辆辆音量补偿的级别（一级、二级、三级或关闭）.
    public static int AMBIENT_LIGHT_BRIGHTNESS = 0;//氛围灯亮度, int type 设置和获取获取氛围灯亮度（0-10） The value TBD.
    public static int AMBIENT_LIGHT_BY_RHYTHM = 0;//氛围灯音乐律动状态, int type 设置和获取氛围灯音乐律动状态（开/关） The value TBD.
    public static int AMBIENT_LIGHT_COLOR = 0;//氛围灯颜色, int type 设置和获取氛围灯颜色（TBD） The value TBD.
    public static int AMBIENT_LIGHT_SWITCH = 0;//氛围灯开关状态, int type 获取和设置氛围灯开关状态（关/亮度&色度） The value TBD.
    public static int ARKAMYS_3D = 0;//Arkamys 3D, int type 获取和设置Arkamys3D音效（开、关） The value must be Arkamys.OFF or Arkamys.DRIVER
    public static int AUDIO_INPUT_SWITCH = 0;//切换音频源输入, int type 获取和设置输入的音频源（麦克风/收音机） The value must be SongReco.OFF or SongReco.ON
    public static int BEEP_SWITCH = 0;//按键音开闭, int type 获取和设置按键音开关（开/关） The value must be .ON or .OFF
    public static final int BEST_POSITION = 0;//最佳听音位和声场模式, int type 获取和设置最佳听音位或声场模式（主驾驶—副驾驶—后排右—后排左—中央 / 标准—歌剧院—音乐厅）. The value 0x00XY, 其中： X 表示最佳听音位,The value must be SoundField.FRONT_MASTER or SoundField.FRONT_SLAVE or SoundField.REAR_MASTER or SoundField.REAR_SLAVE or SoundField.CENTER Y 表示声场模式, The value must be SoundField.ODEUM or SoundField.CINEMA or SoundField.STANDARD
    public static int BOOT_MUSIC = 0;//开关机音效, int type 获取和设置开关机音效（开、关）.
    public static int CUSTOM_EFFECTS = 0;//自定义音效, int type 获取和设置自定义音效（0dB~14dB分别对应1~15） The zone must be SoundEffects.BAND_0 to SoundEffects.BAND_4 The value must be SoundEffects.LEVEL_0 to SoundEffects.LEVEL_14
    public static int DAW = 0;//DAW 驾驶员注意力提醒, int type 设置和获取DAW 驾驶员注意力提醒（开/关） The value must be IfcState.Daw#OFF or IfcState.Daw#STANDBY or IfcState.Daw#ACTIVE The value TBD.
    public static int FADER_BALANCE = 0;//衰减平衡, int type 设置和获取衰减平衡值 The value 0x00XY, 其中: X表示BALANCE, Y表示FADER. fader:1 -- 8 -- 15   balance:1 -- 8 -- 15
    public static int FCW_AEB_SWITCH = 0;//前防碰撞预警/紧急制动状态开关, int type 获取和设置前防碰撞预警/紧急制动状态开关（关/开） The value must be IfcState.Fcw#OFF or IfcState.Fcw#ACTIVE or IfcState.Fcw#STANDBY or IfcState.Fcw#ACTIVE_FCW or IfcState.Fcw#ACTIVE_AEB
    public static int FCW_SENSITIVITY = 0;//前防碰撞预警灵敏度, int type 获取和设置前防碰撞预警灵敏度（中/高/低） The value must be IfcState.FcwSensitivity#LOW or IfcState.FcwSensitivity#NORMAL or IfcState.FcwSensitivity#HIGH
    public static int IHC = 0;//IHC 智能远光状态, int type 设置和获取IHC 智能远光状态（开/关） The value must be IfcState.Ihc#OFF or IfcState.Ihc#STANDBY or IfcState.Ihc#ACTIVE
    public static int INTERIOR_LIGHT_DELAY = 0;//室内灯延时设置, int type 设置和获取室内灯延时（10s/20s/30s） The value TBD.
    public static int ISA = 0;//ISA 交通标志识别状态, int type 设置和获取ISA 交通标志识别状态（开/关） The value must be IfcState.Isa#OFF or IfcState.Isa#STANDBY or IfcState.Isa#ACTIVE
    public static int LDW_SENSITIVITY = 0;//LDW 车道偏移警示灵敏度, int type 设置和获取LDW车道偏移警示灵敏度 The value must be IfcState.LdwSensitivity#NORMAL or IfcState.LdwSensitivity#HIGH or IfcState.LdwSensitivity#LOW
    public static int LKA_MODE = 0;//The value must be IfcState.Mode#INACTIVE or IfcState.Mode#LDW or IfcState.Mode#LKA or IfcState.Mode#LC
    public static int MODE_GO_HOME = 0;//离车/回家模式智慧灯光开关状态, int type 获取和设置离车/回家模式智慧灯光开关状态（关/15s模式/30s模式/60s模式） The value must be BcmState.GoHome#OFF or BcmState.GoHome#M1 or BcmState.GoHome#M2 or BcmState.GoHome#M3
    public static int MODE_LEAVE_HOME = 0;//登车/离家模式智慧灯光开关状态, int type 获取和设置登车/离家模式智慧灯光开关状态（关/15s模式/30s模式/60s模式） The value must be
    public static int PARKING_MEDIA_LEVEL = 0;//泊车媒体音量, int type 获取和设置泊车媒体音量的级别（静音、弱化、正常）.
    public static int REAR_BELT_WORNING_SWITCH = 0;//后排安全带未系提醒开关, int type 获取和设置后排安全带未系提醒开关（开/关） The value must be IcState.RearBeltWarning#ON or IcState.RearBeltWarning#OFF
    public static int SOUND_EFFECTS = 0;//音效, int type 获取和设置当前的音效设置（标准、流行、古典、爵士） The value must be SoundEffects.STANDARD or SoundEffects.POP or SoundEffects.CLASSIC or SoundEffects.JAZZ
    public static int STT = 0;//STT 怠速起停主开关, int type 设置和获取STT 怠速起停主开关（开/关） The value must be IfcState.Stt#OFF or IfcState.Stt#STANDBY or IfcState.Stt#ACTIVE
    public static int SWC_KEY_MODE = 0;//盘控按键模式, int type 获取和设置盘控按键模式（MMI、快捷键） The value must be KeyMode.MMI or KeyMode.SHORTCUT
    public static int VEHICLE_INFOMATION_LEVEL = 0;//信息提示音, int type 获取和设置车辆提示音的级别（一级或是二级），包括RCTA、APA、倒车雷达、前碰撞预警与主动制动和后排安全带未系提醒等.
    public static int WELCOME_LAMP_SWITCH = 0;//迎宾灯/照地灯开关状态, int type 获取和设置迎宾灯开关状态（关/开） The value TBD.
    public static int WELCOME_LAMP_TIME = 0;//迎宾灯的时间值, int type 设置和获取迎宾灯的时间值（10s/20s/30s） The value TBD.
    public static int WELCOME_LIGHT_BY_RHYTHM = 0;//离车登车灯光律动开关, int type 设置和获取离车登车灯光律动开关（开/关） The value TBD.
    public static int WELCOME_SEAT = 0;//座椅迎宾退让, int type 获取和设置座椅迎宾退让（关/开） The value TBD.
    public static int AUTOMATIC_TRUNK = 0;//行李箱自动开启
    public static int RESET_TIRE_PRESSURE = 0;//胎压复位
    public static int EPB = 0;//自动夹紧
    public static int EPB_WORKING_STATE = 0;// 电子驻车状态
    public static int SELF_CLOSING_WINDOW = 0;//锁车自动关窗
    public static int MIRROR_AUTOMATIC_FOLDING = 0;//后视镜自动折叠
    public static int LANE_CHANGE_FLICKER = 0;//变道闪烁
    public static int SPEED_AUTOMATIC_LOCK = 0;//随速闭锁  BcmState.SpeedAutoLock#OFF or BcmState.SpeedAutoLock#ON
    public static int LEAVE_CAR_AUTOMATIC_LOCK = 0;//离车自动落锁  BcmState.AutoLock#OFF or BcmState.AutoLock#ON
    public static int REMOTE_CONTROL_UNLOCK_MODE = 0;//遥控解锁模式   BcmState.DoorLock#LOCK_ALL_DOOR_REQ or BcmState.DoorLock#LOCK_DRIVER_DOOR_REQ
    public static int ENGINE_STATE = 0;//发动机开关状态  BcmState.EngineState#OFF or BcmState.EngineState#ON
    public static int MUSIC_SCENE_FOLLOW_COLOR = 0;//情景随动颜色  MusicFollow.Color#COLOR_1 or MusicFollow.Color#COLOR_12
    public static int MUSIC_SCENE_FOLLOW = 0;//情景随动开关  MusicFollow.State#DISABLE or MusicFollow.State#ENABLE
    public static int ID_VIN_INFO = 0;//获取车机的vin码
    public static int ID_WORK_MODE_STATUS = 0;//ACC OFF/ON
    public static final int MEDIA_VOLUME = 0;//媒体音量
    public static final int PHONE_VOLUME = 1;//电话音量
    public static final int TTS_VOLUME = 2;//TTS音量
    public static final int BT_MEDIA_VOLUME = 3;//蓝牙音乐音量
    public static final int ID_SPEED_INFO = 0; //实时车速

    //语言设置
    public static int LANGUAGE = 0;
    public static int LANGUAGE_EN = 0;
    public static int LANGUAGE_CH = 0;
    public static final int DISPLAYSCREENMODE_DAY = 0;
    public static final int DISPLAYSCREENMODE_NIGHT = 1;
    public static final int DISPLAYSCREENMODE_AUTO = 2;

    //其他设置
    public static int ID_DISPLAY_MODE = 0; //获取当前的显示模式（自动、白天、黑夜）
    public static int ID_AUTO_DISPLAY_LEVEL = 0; //自动模式亮度
    public static int ID_DAY_DISPLAY_LEVEL = 0; //白天模式亮度
    public static int ID_NIGHT_DISPLAY_LEVEL = 0; //黑夜模式亮度
    public static int ID_KEYBOARD_LEVEL = 0; //按键亮度
    public static int ID_BAN_VIDEO = 0;   //行车看视频
    public static int ID_THEME = 0;  //主题
    public static int ID_SCREEN_STATUS = 0;  //关闭屏幕
    public static int ID_ILL_STATUS = 0;  //小灯状态
    public static int ID_ARKAMYS_BASS_BOOST_SWITCH = 0;  //虚拟低音炮

    public static final int ID_NEAR_DOOR_AUTO_UNLOCK_MODE = 0; //近车自动解锁
    public static final int ID_FAR_DOOR_AUTO_LOCK_MODE = 0;//离车自动落锁

    //空调调节模式
    public static final int FACE_MODEL = 0;
    public static final int FOOT_DEF_MODEL = 0;
    public static final int FOOT_MODEL = 0;
    public static final int FACE_FOOT_MODEL = 0;//参数不明确
    public static final int DEF_MODE = 0;//参数不明确

    //语音门锁控制
    public static final int SpeechDoorLock_ALL_DOOR_LK_REQ = 0;
    public static final int SpeechDoorLock_ALL_DOOR_UNLK_REQ = 0;
    public static final int SpeechDoorLock_DRV_DOOR_LK_REQ = 0;
    public static final int SpeechDoorLock_DRV_DOOR_UNLK_REQ = 0;

    //通用的状态常量
    public static final int CAN_ON_OFF2_OFF = 0;
    public static final int CAN_ON_OFF2_ON = 0;
    public static final int CAN_ON_OFF2_ON_REQ = 0;
    public static final int CAN_ON_OFF2_OFF_REQ = 0;

    // 人脸识别相关
    public static final int ID_DMS_STATE = 0;
    public static final int ID_TIRED_REMIND_STATE = 1;
    public static final int ID_DISTRACTION_REMIND_STATE = 2;
    public static final int ID_BAD_DRIVING_STATE = 3;

    //燃油不足
    public static final int ID_FUEL_WARNING = 0;
    //判断全景是否处于前台
    public static final int ID_CAMERA_STATUS = 0;

    //疲劳提醒灵敏度
    public static final int ID_TIRED_SENSITIVE = 4;
    //Wifi加密方式
    public static final int WifiEncryption_EAP = 5;
    public static final int WifiEncryption_FT_PSK = 3;
    public static final int WifiEncryption_NO = 0;
    public static final int WifiEncryption_WEP = 4;
    public static final int WifiEncryption_WPA = 1;
    public static final int WifiEncryption_WPA2 = 2;

    public static final int ID_TIME_INFO = 0;      //时间设置

    public static final int OS_LOCAL_VERSION = 0; // 系统版本
    public static final int MCU_LOCAL_VERSION = 1; // MCU版本

    //通过网络等途径同步时间
    public static final int ID_SYNC_TIME_ON = 0;
    public static final int ID_INTELLIGENT_MIRROR_SWITCH = 0; // 外后视镜自动折叠

    public static final int REAR_MIRROR_LEFT = 0; // 左后视镜随动
    public static final int REAR_MIRROR_RIGHT = 0; // 右后视镜随动

    //雨刷控制
    public static final int ID_SPEECH_FRONT_WIPER = 0;  //控制雨刷单次刮刷
    public static final int ID_SPEECH_FRONT_WASH = 0;  //控制雨刷单次洗涤


    public static class VALUE {
        //        安全带
        public static final int REAR_BELT_ON_REQ = 1;
        public static final int REAR_BELT_ON = 1;
        public static final int REAR_BELT_OFF_REQ = 2;
        public static final int REAR_BELT_OFF = 2;

        //主动制动 开关
        public static final int FCW_OFF_REQ = 1;
        public static final int FCW_OFF = 1;
        public static final int FCW_ON_REQ = 2;
        public static final int FCW_ON = 2;
        public static final int FCW_STANDBY = 3;

        //主动制动灵敏度
        public static final int FCW_LOW_REQ = 1;
        public static final int FCW_LOW = 1;
        public static final int FCW_NORMAL_REQ = 2;
        public static final int FCW_NORMAL = 2;
        public static final int FCW_HIGH_REQ = 3;
        public static final int FCW_HIGH = 3;


        //车道保持
//        public static final int LKA_MODE_INACTIVE = 0;
        public static final int LKA_MODE_LDW = 1;
        public static final int LKA_MODE_LDW_REQ = 1;
        public static final int LKA_MODE_LKA = 2;
        public static final int LKA_MODE_LKA_REQ = 2;
        public static final int LKA_MODE_LC = 3;
        public static final int LKA_MODE_LC_REQ = 3;

        //ISA交通标志
        public static final int IFC_OFF = 0;
        public static final int IFC_ON = 1;
        public static final int IFC_STANDBY = 2;
        public static final int IFC_ACTIVE = 3;

        //变道闪烁
        public static final int LANE_CHANGE = 0;

        //驾驶员注意力提醒
        public static final int DAW_OFF_REQ = 1;
        public static final int DAW_OFF = 1;
        public static final int DAW_ON_REQ = 2;
        public static final int DAW_ON = 2;
        public static final int DAW_STANDBY = 3;

        //车道偏离
        public static final int LDW_LOW = 0;
        public static final int LDW_NORMAL = 1;
        public static final int LDW_HIGH = 2;

        //自动夹紧
        public static final int EPB_OFF_REQ = 1;
        public static final int EPB_OFF = 1;
        public static final int EPB_ON_REQ = 2;
        public static final int EPB_ON = 2;

        //自动关窗
        public static final int AUTO_CLOSE_WINDOW_OFF = 0;
        public static final int AUTO_CLOSE_WINDOW_ON = 0;

        //行李箱
        public static final boolean TRUNK_ON = true;
        public static final boolean TRUNK_OFF = false;

        //后视镜
        public static final int REARMIRROR_OFF = 0;
        public static final int REARMIRROR_ON = 0;

        //回家模式
        public static final int GO_HOME_M1 = 1;
        public static final int GO_HOME_M1_REQ = 1;
        public static final int GO_HOME_M2_REQ = 2;
        public static final int GO_HOME_M2 = 2;
        public static final int GO_HOME_M3 = 3;
        public static final int GO_HOME_M3_REQ = 3;
        public static final int GO_HOME_OFF = 4;
        public static final int GO_HOME_OFF_REQ = 4;

        //ihc
        public static final int IHC_OFF = 0;
        public static final int IHC_OFF_REQ = 0;
        public static final int IHC_ON = 1;
        public static final int IHC_ON_REQ = 1;

        //氛围灯
        public static final int ATMOSPHERE_LIGHT_OFF = 0;
        public static final int ATMOSPHERE_LIGHT_OFF_REQ = 0;
        public static final int ATMOSPHERE_LIGHT_ON = 1;
        public static final int ATMOSPHERE_LIGHT_ON_REQ = 1;

        //车辆信息提示音
        public static final int INFORMATION_TONE_LEVER_NORMAL = 0;
        public static final int INFORMATION_TONE_LEVER_LARGER = 1;

        //车速音量补偿
        public static final int SPEEDGAIN_OFF = 0;
        public static final int SPEEDGAIN_LOW = 1;
        public static final int SPEEDGAIN_MID = 2;
        public static final int SPEEDGAIN_HIGH = 3;

        // 泊车媒体音量等级
        public static final int PARKING_MEDIA_MUTE = 0;   // 静音
        public static final int PARKING_MEDIA_REDUCE = 1; // 弱化
        public static final int PARKING_MEDIA_NORMAL = 2; // 正常

        //ARKAMYS 3D音效
        public static final int ARKAMYS_3D_OFF = 0;
        public static final int ARKAMYS_3D_ALL_ON = 1;
        public static final int ARKAMYS_3D_DRV_ON = 2;

        //最佳听音位
        //暂时为0
        public static final int FRONT_MASTER = 0;
        public static final int FRONT_SLAVE = 1;
        public static final int REAR_MASTER = 2;
        public static final int REAR_SLAVE = 3;
        public static final int CENTER = 4;
        public static final int CUSTOMIZE = 5;

        //声场模式
        public static final int STANDARD = 0;
        public static final int CINEMA = 2;
        public static final int ODEUM = 3;

        //开关机音效
        public static final boolean BOOT_MUSIC_ON = true;
        public static final boolean BOOT_MUSIC_OFF = false;

        //音效模式
        public static final int SOUND_EFFECTS_STANDARD = 0;
        public static final int SOUND_EFFECTS_POP = 1;
        public static final int SOUND_EFFECTS_CLASSIC = 2;
        public static final int SOUND_EFFECTS_JAZZ = 3;
        public static final int SOUND_EFFECTS_USER = 4;

        //音效
        public static final int BAND_0 = 0;
        public static final int BAND_1 = 1;
        public static final int BAND_2 = 2;
        public static final int BAND_3 = 3;
        public static final int BAND_4 = 4;
        public static final int LEVEL_0 = 5;
        public static final int LEVEL_1 = 6;
        public static final int LEVEL_2 = 7;
        public static final int LEVEL_3 = 8;
        public static final int LEVEL_4 = 9;
        public static final int LEVEL_5 = 10;
        public static final int LEVEL_6 = 20;
        public static final int LEVEL_7 = 30;
        public static final int LEVEL_8 = 40;
        public static final int LEVEL_9 = 50;
        public static final int LEVEL_10 = 60;
        public static final int LEVEL_11 = 70;
        public static final int LEVEL_12 = 80;
        public static final int LEVEL_13 = 90;
        public static final int LEVEL_14 = 100;

        //泊车媒体音量
        public static final int VEHICLEHINTS_MUTE = 0;
        public static final int VEHICLEHINTS_REDUCE = 1;
        public static final int VEHICLEHINTS_NORMAL = 2;

        //信息提示音
        public static final int VehicleHints_LV1 = 0;
        public static final int VehicleHints_LV2 = 1;

        //亮度
        public static final int DAY_DISPLAY_LEVEL0 = 0;
        public static final int DAY_DISPLAY_LEVEL1 = 0;
        public static final int DAY_DISPLAY_LEVEL2 = 0;
        public static final int DAY_DISPLAY_LEVEL3 = 0;
        public static final int DAY_DISPLAY_LEVEL4 = 0;
        public static final int DAY_DISPLAY_LEVEL5 = 0;
        public static final int DAY_DISPLAY_LEVEL6 = 0;
        public static final int DAY_DISPLAY_LEVEL7 = 0;
        public static final int DAY_DISPLAY_LEVEL8 = 0;
        public static final int DAY_DISPLAY_LEVEL9 = 0;
        public static final int DAY_DISPLAY_LEVEL10 = 0;

        //屏幕显示模式
        public static final int DISPLAY_AUTO = 0; //自动显示模式
        public static final int DISPLAY_DAY = 0; //白天显示模式
        public static final int DISPLAY_NIGHT = 0; //黑夜显示模式

        //行车看视频
        public static final boolean BAN_VIDEO = true;    //禁止行车看视频
        public static final boolean UN_BAN_VIDEO = false;    //不禁止行车看视频

        //主题
        public static final int THEME_ZHIHUI = 0;// 智慧
        public static final int THEME_QINGSHE = 1;// 轻奢
        public static final int THEME_DAOMENG = 2;// 盗梦
        public static final int THEME_DEFAULT = THEME_ZHIHUI;// 默认主题


        //随速闭锁
        public static final int AUTOLOCK_OFF = 0;
        public static final int AUTOLOCK_ON = 0;

        //离车自动落锁
        public static final int LEAVE_AUTO_LOCK_OFF = 0;
        public static final int LEAVE_AUTO_LOCK_ON = 0;

        //走近自动解锁
        public static final int APPROACH_AUTO_UNLOCK_OFF = 0;
        public static final int APPROACH_AUTO_UNLOCK_ON = 0;

        //遥控解锁模式
        public static final int LOCK_ALL_DOOR_REQ = 1;
        public static final int LOCK_ALL_DOOR = 1;
        public static final int LOCK_DRIVER_DOOR_REQ = 2;
        public static final int LOCK_DRIVER_DOOR = 2;

        //发动机状态
        public static final int ENGINESTATE_OFF = 0;
        public static final int ENGINESTATE_ON = 0;

        //情景随动颜色
        public static final int MUSIC_FOLLOW_COLOR_1 = 1;
        public static final int MUSIC_FOLLOW_COLOR_2 = 2;
        public static final int MUSIC_FOLLOW_COLOR_3 = 3;
        public static final int MUSIC_FOLLOW_COLOR_4 = 4;
        public static final int MUSIC_FOLLOW_COLOR_5 = 5;
        public static final int MUSIC_FOLLOW_COLOR_6 = 6;
        public static final int MUSIC_FOLLOW_COLOR_7 = 7;
        public static final int MUSIC_FOLLOW_COLOR_8 = 8;
        public static final int MUSIC_FOLLOW_COLOR_9 = 9;
        public static final int MUSIC_FOLLOW_COLOR_10 = 10;
        public static final int MUSIC_FOLLOW_COLOR_11 = 11;
        public static final int MUSIC_FOLLOW_COLOR_12 = 12;

        //情景随动开关
        public static final boolean MUSIC_FOLLOW_OFF = false;
        public static final boolean MUSIC_FOLLOW_ON = false;

        //胎压复位
        // TODO: 2018/12/5 0005 胎压复位值
        public static final int RESET_TIRE = 0;
        public static final int LDW_LOW_REQ = 0;
        public static final int LDW_NORMAL_REQ = 0;
        public static final int LDW_HIGH_REQ = 0;

        //关闭屏幕状态
        public static boolean SCREEN_OFF = false;
        public static boolean SCREEN_ON = true;

        //座椅迎宾
        public static final int WELCOME_SEAT_OFF = 0;
        public static final int WELCOME_SEAT_ON = 0;

        //离车登车灯光律动
        public static final boolean EELCOME_LIGHT_MOTION_OFF = false;
        public static final boolean EELCOME_LIGHT_MOTION_ON = true;

        //室内灯延时
        public static final int INTERIOR_LIGHT_DELAY_10S = 1;
        public static final int INTERIOR_LIGHT_DELAY_10S_REQ = 1;
        public static final int INTERIOR_LIGHT_DELAY_20S = 2;
        public static final int INTERIOR_LIGHT_DELAY_20S_REQ = 2;
        public static final int INTERIOR_LIGHT_DELAY_30S = 3;
        public static final int INTERIOR_LIGHT_DELAY_30S_REQ = 3;

        //变道闪烁
        public static final int LANE_CHANGE_TIME_1 = 0;
        public static final int LANE_CHANGE_TIME_3 = 1;

        //迎宾灯开关
        public static final int WELCOM_LIGHT_OFF_REQ = 0;
        public static final int WELCOM_LIGHT_OFF = 1;
        public static final int WELCOM_LIGHT_ON_REQ = 2;
        public static final int WELCOM_LIGHT_ON = 3;

        //降噪模式
        public static final int SR_NOISE_CLEAN = 1;

        //媒体音量
        public static final int ID_MEDIA_VOLUME = 1;
        //蓝牙音乐音量
        public static final int ID_BT_MEDIA_VOLUME = 2;
        //电话音量
        public static final int ID_BT_VOLUME = 3;
        //语音播报音量
        public static final int ID_TTS_VOLUME = 4;

        public static final int MEDIA_MAX_VALUE = 40;
        public static final int MEDIA_MIN_VALUE = 1;

        //音乐情景随动开关
        public static final int MUSIC_SCENE_FOLLOW_ON = 0;

        public static final int SPEED_AUTOMATIC_LOCK_ON = 0;
        public static final int LEAVE_CAR_AUTOMATIC_LOCK_ON = 0;

        public static final int SELF_CLOSING_WINDOW_ON = 0;
        public static final int AUTOMATIC_TRUNK_ON = 0;
        public static final int MIRROR_AUTOMATIC_FOLDING_ON = 0;
        public static final int WELCOME_SEAT_TURN_ON = 0;
        public static final int MUSIC_SCENE_FOLLOW = 0;

        //空调风速
        public static final int FAN_SPEED_LEVEL_1 = 1;
        public static final int FAN_SPEED_LEVEL_2 = 2;
        public static final int FAN_SPEED_LEVEL_3 = 3;
        public static final int FAN_SPEED_LEVEL_4 = 4;
        public static final int FAN_SPEED_LEVEL_5 = 5;
        public static final int FAN_SPEED_LEVEL_6 = 6;
        public static final int FAN_SPEED_LEVEL_7 = 7;

        //空调、压缩机, 空调自动模式, 座椅加热 取值
        public static final int SpeechOnOff2_INACTIVE = 0;
        public static final int SpeechOnOff2_INACTIVE_REQ = 0;
        public static final int SpeechOnOff2_OFF_FAIL = 0;
        public static final int SpeechOnOff2_OFF_REQ = 0;
        public static final int SpeechOnOff2_OFF_SUCCESS = 0;
        public static final int SpeechOnOff2_ON_FAIL = 0;
        public static final int SpeechOnOff2_ON_REQ = 0;
        public static final int SpeechOnOff2_ON_SUCCESS = 0;

        //切换循环模式取值
        public static final int SpeechFrsRec_FRS_FAIL = 0;
        public static final int SpeechFrsRec_FRS_REQ = 0;
        public static final int SpeechFrsRec_FRS_SUC = 0;
        public static final int SpeechFrsRec_INACTIVE = 0;
        public static final int SpeechFrsRec_INACTIVE_REQ = 0;
        public static final int SpeechFrsRec_REC_FAIL = 0;
        public static final int SpeechFrsRec_REC_REQ = 0;
        public static final int SpeechFrsRec_REC_SUCC = 0;

        public static final int TIRED_HIGH = 2;
        public static final int TIRED_HIGH_REQ = 0;
        public static final int TIRED_INACTIVE_REQ = 0;
        public static final int TIRED_LOW = 0;
        public static final int TIRED_LOW_REQ = 0;
        public static final int TIRED_NORMAL = 1;
        public static final int TIRED_NORMAL_REQ = 0;

        //通用的状态常量
        public static final int CAN_ON_OFF2_OFF = 0;
        public static final int CAN_ON_OFF2_ON = 0;
        public static final int CAN_ON_OFF2_ON_REQ = 0;
        public static final int CAN_ON_OFF2_OFF_REQ = 0;

        //仪表当前TAB位置获取及TAB切换通知
//        public static final int InteractMode_HU_RESET = InteractMode.HU_RESET;
//        public static final int InteractMode_IGN_SWITCH_REQ = InteractMode.IGN_SWITCH_REQ;
        public static final int InteractMode_INACTIVE_REQ = 0;
        public static final int InteractMode_MEDIA = 1;
        public static final int InteractMode_MEDIA_REQ = 2;
        public static final int InteractMode_NAVI = 3;
        public static final int InteractMode_NAVI_REQ = 4;
        public static final int InteractMode_TEL = 5;
        public static final int InteractMode_TEL_REQ = 6;
        public static final int HuInteractReq_TEL_REQ = 7;
        public static final int HuInteractReq_NAVI_REQ = 8;
        public static final int HuInteractReq_INACTIVE_REQ = 9;
        public static final int INACTIVE = 10;

        //左侧仪表投屏
        public static final int CanCommon_ON = 0;
        public static final int CanCommon_OFF = 0;

        //外后视镜自动折叠
        public static final int REAR_VEIW_ENABLE_REQ = 0;      //RearviewMirror
        public static final int REAR_VEIW_DISABLE_REQ = 1;      //RearviewMirror
        public static final int REAR_VEIW_ENABLE = 2;     //RearviewMirror
        public static final int REAR_VEIW_DISABLE = 3;      //RearviewMirror
    }

    public static class Area {
        public static final int SEAT_ALL = 0;

        //温度area
        public static final int HAVC_LEFT = 0;
        public static final int HAVC_RIGHT = 1;
        public static final int HAVC_ALL = 2;

        //驾驶座位
        private static final int DRIVER_ZONE_ID = 0;
        private static final int PASSENGER_ZONE_ID = 1;
    }

    public static class CarSenSor {
        //CarEvent分发事件，车速，档位数据ID
        public static final int GEAR_ID = 1000000001;
        public static final int WHEEL_ANGEL_ID = 1000000009;
    }

    public static class EngineState {
        //CarEvent分发事件，车速，档位数据ID
        public static final int ENGINE_RUNNING = 2;
        public static final int ENGINE_OFF = 0;
    }

    public static class WifiMode {
        public static final int WIFI_MODE_ID = 1000000002;
        public static final int AP = 1; //AP 对应的是热点模式
        public static final int OFF = 0; // 关闭
        public static final int STA = 2; //STA 对应的是wifi模式
    }

    public static class RequestAck {
        public static final int RESULT_ERROR = -1;
        public static final int RESULT_SUCCESS = 0;
        public static final int RESULT_FAILED = 1;
    }

    public static class WorkMode {
        public static final int NORMAL = 0;
        public static final int STANDBY = 1;
    }

    public static class CallOperation {
        public static final int ANSWER = 1;
        public static final int HANDUP = 2;
        public static final int DIAL = 0;
    }

    public static class SIMMode {
        public static final int SIM_SWITCH_ID = 1000000005;
    }

    public static class TboxCallState {
        public static int ID_TBOX_CALL_STATE = 1000000008;
        public static final int ANSWER = 2;
        public static final int HANGING_UP = 3;
        public static final int HANGUP_EXPIRE_FAIL = 4;
        public static final int DIALING = 0;
        public static final int RING_BACK = 1;
    }

    /**
     * 档位定义常量
     */
    public static class CarGearMode {
        public static final int GEAR_P = 0;
        public static final int GEAR_R = 1;
        public static final int GEAR_N = 2;
        public static final int GEAR_1 = 3;
        public static final int GEAR_2 = 4;
        public static final int GEAR_3 = 5;
        public static final int GEAR_4 = 6;
        public static final int GEAR_5 = 7;
        public static final int GEAR_6 = 8;
        public static final int GEAR_7 = 9;
        public static final int GEAR_8 = 10;
        public static final int NO_CONN = 14;
        public static final int INVALID = 15;
    }
}
