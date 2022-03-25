package com.xiaoma.carlib.constant;

import android.car.VehicleAreaSeat;
import android.car.hardware.CarSensorManager;
import android.car.hardware.CarVendorExtensionManager;
import android.car.hardware.cabin.CarCabinManager;
import android.car.hardware.hvac.CarHvacManager;
import android.car.hardware.vendor.ALCLevel;
import android.car.hardware.vendor.ArkamysSwitch;
import android.car.hardware.vendor.BlowLv;
import android.car.hardware.vendor.BlowMode;
import android.car.hardware.vendor.CanCommon;
import android.car.hardware.vendor.CanOnOff1;
import android.car.hardware.vendor.CanOnOff2;
import android.car.hardware.vendor.CanOnOff3;
import android.car.hardware.vendor.CanOnOff4;
import android.car.hardware.vendor.ComeHomeMode;
import android.car.hardware.vendor.DisplayScreen;
import android.car.hardware.vendor.DisplayScreenMode;
import android.car.hardware.vendor.DoorUnlock;
import android.car.hardware.vendor.FCWOnOff;
import android.car.hardware.vendor.FcwSensitivity;
import android.car.hardware.vendor.GearMode;
import android.car.hardware.vendor.HuInteractReq;
import android.car.hardware.vendor.InformationToneLever;
import android.car.hardware.vendor.InteractMode;
import android.car.hardware.vendor.LDWSensitivity;
import android.car.hardware.vendor.LampLightDalay;
import android.car.hardware.vendor.LaneChangeFlickerTime;
import android.car.hardware.vendor.Language;
import android.car.hardware.vendor.LdwLkaLcMode;
import android.car.hardware.vendor.MirrorState;
import android.car.hardware.vendor.ParkingMediaLevel;
import android.car.hardware.vendor.RobotBrightnessReq;
import android.car.hardware.vendor.RobotMode;
import android.car.hardware.vendor.SoundEffects;
import android.car.hardware.vendor.SoundField;
import android.car.hardware.vendor.SpeechBlowLv;
import android.car.hardware.vendor.SpeechBlowMode;
import android.car.hardware.vendor.SpeechDoorLock;
import android.car.hardware.vendor.SpeechFrsRec;
import android.car.hardware.vendor.SpeechOnOff;
import android.car.hardware.vendor.SpeechOnOff2;
import android.car.hardware.vendor.SrMode;
import android.car.hardware.vendor.TiredSensitivity;
import android.car.hardware.vendor.WinType;

import com.fsl.android.ota.other.CustomAction;
import com.fsl.android.tbox.other.Constants;

/**
 * @author: iSun
 * @date: 2018/10/22 0022
 */
public class SDKConstants {
    public static int DEFAULT_INT = -1;
    public static String DEFAULT_STRING = "";
    public static float DEFAULT_FLOAT = -1.0f;


    public static final int SpeechOnOff_1_ON_REQ = SpeechOnOff.ON_REQ;
    public static final int SpeechOnOff_1_INACTIVE_REQ = SpeechOnOff.INACTIVE_REQ;
    //CarVendorExtensionManager
    public static int ALC_LEVEL = CarVendorExtensionManager.ID_ALC_LEVEL;//车速音量补偿, int type 获取和设置车辆辆音量补偿的级别（一级、二级、三级或关闭）.
    public static int AMBIENT_LIGHT_BRIGHTNESS = CarVendorExtensionManager.ID_ATMOSPHERE_LIGHT_BRIGHTNESS;//氛围灯亮度, int type 设置和获取获取氛围灯亮度（0-10） The value TBD.
    public static int AMBIENT_LIGHT_BY_RHYTHM = CarVendorExtensionManager.ID_ATMOSPHERE_LIGHT_MOTION;//氛围灯音乐律动状态, int type 设置和获取氛围灯音乐律动状态（开/关） The value TBD.
    public static int AMBIENT_LIGHT_COLOR = CarVendorExtensionManager.ID_ATMOSPHERE_LIGHT_COLOR;//氛围灯颜色, int type 设置和获取氛围灯颜色（TBD） The value TBD.
    public static int AMBIENT_LIGHT_SWITCH = CarVendorExtensionManager.ID_ATMOSPHERE_LIGHT_SWITCH;//氛围灯开关状态, int type 获取和设置氛围灯开关状态（关/亮度&色度） The value TBD.
    public static final int ARKAMYS_3D = CarVendorExtensionManager.ID_ARKAMYS_3D_SWITCH;//Arkamys 3D, int type 获取和设置Arkamys3D音效（开、关） The value must be Arkamys.OFF or Arkamys.DRIVER
    public static int AUDIO_INPUT_SWITCH = CarVendorExtensionManager.ID_AUDIO_INPUT_SOURCE;//切换音频源输入, int type 获取和设置输入的音频源（麦克风/收音机） The value must be SongReco.OFF or SongReco.ON
    public static int BEEP_SWITCH = CarVendorExtensionManager.ID_BEEP_SWITCH;//按键音开闭, int type 获取和设置按键音开关（开/关） The value must be .ON or .OFF
    public static int BOOT_MUSIC = CarVendorExtensionManager.ID_BOOT_MUSIC_SWITCH;//开关机音效, int type 获取和设置开关机音效（开、关）.
    public static final int DAW = CarVendorExtensionManager.ID_DAW_STATE;//DAW 驾驶员注意力提醒, int type 设置和获取DAW 驾驶员注意力提醒（开/关） The value must be IfcState.Daw#OFF or IfcState.Daw#STANDBY or IfcState.Daw#ACTIVE The value TBD.
    public static final int FADER_BALANCE = CarVendorExtensionManager.ID_FADER_BALANCE_SOUND_FIELD;//衰减平衡, int type 设置和获取衰减平衡值 The value 0x00XY, 其中: X表示BALANCE, Y表示FADER. fader:1 -- 8 -- 15   balance:1 -- 8 -- 15
    public static int FCW_AEB_SWITCH = CarVendorExtensionManager.ID_FCW_SWITCH;//前防碰撞预警/紧急制动状态开关, int type 获取和设置前防碰撞预警/紧急制动状态开关（关/开） The value must be IfcState.Fcw#OFF or IfcState.Fcw#ACTIVE or IfcState.Fcw#STANDBY or IfcState.Fcw#ACTIVE_FCW or IfcState.Fcw#ACTIVE_AEB
    public static int FCW_SENSITIVITY = CarVendorExtensionManager.ID_FCW_SENSITIVITY;//前防碰撞预警灵敏度, int type 获取和设置前防碰撞预警灵敏度（中/高/低） The value must be IfcState.FcwSensitivity#LOW or IfcState.FcwSensitivity#NORMAL or IfcState.FcwSensitivity#HIGH
    public static int IHC = CarVendorExtensionManager.ID_IHC_STATE;//IHC 智能远光状态, int type 设置和获取IHC 智能远光状态（开/关） The value must be IfcState.Ihc#OFF or IfcState.Ihc#STANDBY or IfcState.Ihc#ACTIVE
    public static int INTERIOR_LIGHT_DELAY = CarVendorExtensionManager.ID_INTERIOR_LIGHT_DELAY;//室内灯延时设置, int type 设置和获取室内灯延时（10s/20s/30s） The value TBD.
    public static int ISA = CarVendorExtensionManager.ID_ISA_STATE;//ISA 交通标志识别状态, int type 设置和获取ISA 交通标志识别状态（开/关） The value must be IfcState.Isa#OFF or IfcState.Isa#STANDBY or IfcState.Isa#ACTIVE
    public static int LDW_SENSITIVITY = CarVendorExtensionManager.ID_LDW_SENSITIVITY;//LDW 车道偏移警示灵敏度, int type 设置和获取LDW车道偏移警示灵敏度 The value must be IfcState.LdwSensitivity#NORMAL or IfcState.LdwSensitivity#HIGH or IfcState.LdwSensitivity#LOW
    public static int LKA_MODE = CarVendorExtensionManager.ID_LDW_LKA_LC_MODE_SET;//The value must be IfcState.Mode#INACTIVE or IfcState.Mode#LDW or IfcState.Mode#LKA or IfcState.Mode#LC
    public static int MODE_GO_HOME = CarVendorExtensionManager.ID_MODE_GO_HOME;//离车/回家模式智慧灯光开关状态, int type 获取和设置离车/回家模式智慧灯光开关状态（关/15s模式/30s模式/60s模式） The value must be BcmState.GoHome#OFF or BcmState.GoHome#M1 or BcmState.GoHome#M2 or BcmState.GoHome#M3
    public static int MODE_LEAVE_HOME = /*CarVendorExtensionManager.ID_MODE_LEAVE_HOME*/0;//登车/离家模式智慧灯光开关状态, int type 获取和设置登车/离家模式智慧灯光开关状态（关/15s模式/30s模式/60s模式） The value must be
    public static int PARKING_MEDIA_LEVEL = CarVendorExtensionManager.ID_PARKING_MEDIA_LEVEL;//泊车媒体音量, int type 获取和设置泊车媒体音量的级别（静音、弱化、正常）.
    public static int REAR_BELT_WORNING_SWITCH = CarCabinManager.ID_REAR_BELT_WARNING_SWITCH;//后排安全带未系提醒开关, int type 获取和设置后排安全带未系提醒开关（开/关） The value must be IcState.RearBeltWarning#ON or IcState.RearBeltWarning#OFF
    public static final int SOUND_EFFECTS = CarVendorExtensionManager.ID_SOUND_EFFECTS;//音效, int type 获取和设置当前的音效设置（标准、流行、古典、爵士） The value must be SoundEffects.STANDARD or SoundEffects.POP or SoundEffects.CLASSIC or SoundEffects.JAZZ
    public static final int MULTIFUNC_SWITCH = CarVendorExtensionManager.ID_MULTIFUNC_SWITCH;// 仪表方控按键
    public static int STT = CarVendorExtensionManager.ID_STT_STATE;//STT 怠速起停主开关, int type 设置和获取STT 怠速起停主开关（开/关） The value must be IfcState.Stt#OFF or IfcState.Stt#STANDBY or IfcState.Stt#ACTIVE
    public static int SWC_KEY_MODE = CarVendorExtensionManager.ID_SWC_KEY_MODE;//盘控按键模式, int type 获取和设置盘控按键模式（MMI、快捷键） The value must be KeyMode.MMI or KeyMode.SHORTCUT
    public static int VEHICLE_INFOMATION_LEVEL = CarVendorExtensionManager.ID_VEHICLE_INFORMATION_LEVEL;//信息提示音, int type 获取和设置车辆提示音的级别（一级或是二级），包括RCTA、APA、倒车雷达、前碰撞预警与主动制动和后排安全带未系提醒等.
    public static int WELCOME_LAMP_SWITCH = CarVendorExtensionManager.ID_WELCOME_LIGHT_SWITCH;//迎宾灯/照地灯开关状态, int type 获取和设置迎宾灯开关状态（关/开） The value TBD.
    public static int WELCOME_LAMP_TIME = CarVendorExtensionManager.ID_WELCOME_LIGHT_TIME;//迎宾灯的时间值, int type 设置和获取迎宾灯的时间值（10s/20s/30s） The value TBD.
    public static int WELCOME_LIGHT_BY_RHYTHM = CarVendorExtensionManager.ID_WELCOME_LIGHT_MOTION;//离车登车灯光律动开关, int type 设置和获取离车登车灯光律动开关（开/关） The value TBD.
    public static int WELCOME_SEAT = CarVendorExtensionManager.ID_WELCOME_SEAT;//座椅迎宾退让, int type 获取和设置座椅迎宾退让（关/开） The value TBD.
    public static int AUTOMATIC_TRUNK = CarVendorExtensionManager.ID_AUTOMATIC_TRUNK;//行李箱自动开启
    public static int RESET_TIRE_PRESSURE = CarVendorExtensionManager.ID_RESET_TIRE_PRESSURE;//胎压复位
    public static int EPB = CarVendorExtensionManager.ID_EPB_STATE;//自动夹紧
    public static int EPB_WORKING_STATE = CarVendorExtensionManager.ID_EPB_WORKING_STATE;// 电子驻车状态
    public static int SELF_CLOSING_WINDOW = CarVendorExtensionManager.ID_WINDOWS_AUTO_CLOSE;//锁车自动关窗
    public static int MIRROR_AUTOMATIC_FOLDING = CarCabinManager.ID_MIRROR_FOLD;//后视镜自动折叠
    public static int LANE_CHANGE_FLICKER = CarVendorExtensionManager.ID_LANE_CHANGE_FLICKER;//变道闪烁
    public static int SPEED_AUTOMATIC_LOCK = CarVendorExtensionManager.ID_SPEED_AUTOMATIC_LOCK;//随速闭锁  BcmState.SpeedAutoLock#OFF or BcmState.SpeedAutoLock#ON
    public static int LEAVE_CAR_AUTOMATIC_LOCK = CarVendorExtensionManager.ID_LEAVE_CAR_AUTOMATIC_LOCK;//离车自动落锁  BcmState.AutoLock#OFF or BcmState.AutoLock#ON
    public static int REMOTE_CONTROL_UNLOCK_MODE = CarCabinManager.ID_REMOTE_CONTROL_UNLOCK_MODE;//遥控解锁模式   BcmState.DoorLock#LOCK_ALL_DOOR_REQ or BcmState.DoorLock#LOCK_DRIVER_DOOR_REQ
    public static final int ENGINE_STATE = CarVendorExtensionManager.ID_ENGINE_STATE;//发动机开关状态  BcmState.EngineState#OFF or BcmState.EngineState#ON
    public static int MUSIC_SCENE_FOLLOW_COLOR = CarVendorExtensionManager.ID_ATMOSPHERE_LIGHT_COLOR;//情景随动颜色  MusicFollow.Color#COLOR_1 or MusicFollow.Color#COLOR_12
    public static int MUSIC_SCENE_FOLLOW = CarVendorExtensionManager.ID_MUSIC_SCENE_FOLLOW;//情景随动开关  MusicFollow.State#DISABLE or MusicFollow.State#ENABLE
    public static int ID_VIN_INFO = CarVendorExtensionManager.ID_VIN_INFO;//获取车机的vin码
    public static int ID_WORK_MODE_STATUS = CarVendorExtensionManager.ID_WORK_MODE_STATUS;//ACC OFF/ON
    public static final int MEDIA_VOLUME = 0;//媒体音量
    public static final int PHONE_VOLUME = 1;//电话音量
    public static final int TTS_VOLUME = 2;//TTS音量
    public static final int BT_MEDIA_VOLUME = 3;//蓝牙音乐音量

    //语言设置
    public static int LANGUAGE = CarVendorExtensionManager.ID_LANGUAGE_TYPE;
    public static int LANGUAGE_EN = Language.ENGLISH;
    public static int LANGUAGE_CH = Language.CHINESE;
    public static final int DISPLAYSCREENMODE_DAY = DisplayScreenMode.DAY;
    public static final int DISPLAYSCREENMODE_NIGHT = DisplayScreenMode.NIGHT;
    public static final int DISPLAYSCREENMODE_AUTO = DisplayScreenMode.AUTO;


    //其他设置
    public static int ID_DISPLAY_MODE = CarVendorExtensionManager.ID_DISPLAY_MODE; //获取当前的显示模式（自动、白天、黑夜）
    public static int ID_AUTO_DISPLAY_LEVEL = 0; //自动模式亮度
    public static int ID_DAY_DISPLAY_LEVEL = CarVendorExtensionManager.ID_DISPLAY_BRIGHTNESS_DAY_LEVEL; //白天模式亮度
    public static int ID_NIGHT_DISPLAY_LEVEL = CarVendorExtensionManager.ID_DISPLAY_BRIGHTNESS_NIGHT_LEVEL; //黑夜模式亮度
    public static int ID_KEYBOARD_LEVEL = CarVendorExtensionManager.ID_KEYBOARD_BRIGHTNESS_LEVEL; //按键亮度
    public static int ID_BAN_VIDEO = CarVendorExtensionManager.ID_BAN_VIDEO_SWITCH;   //行车看视频
    public static final int ID_THEME = CarVendorExtensionManager.ID_THEME;  //主题
    public static final int ID_SCREEN_STATUS = CarVendorExtensionManager.ID_SCREEN_STATUS;  //关闭屏幕
    public static int ID_ILL_STATUS = CarVendorExtensionManager.ID_ILL_STATUS;  //小灯状态
    public static int ID_ARKAMYS_BASS_BOOST_SWITCH = CarVendorExtensionManager.ID_ARKAMYS_BASS_BOOST_SWITCH;  //虚拟低音炮

    //车身和空调设置
    //空调控制
    public static final int ID_SPEECH_AC_PWR = CarHvacManager.ID_SPEECH_AC_PWR;
    public static final int ID_AC_MODE = CarHvacManager.ID_AC_MODE;

    //吹风模式切换，包括除霜模式，吹脚除霜模式，吹脚模式，吹脸和吹脚模式，吹脸模式
    public static final int ID_SPEECH_BLOW_MODE = CarHvacManager.ID_SPEECH_BLOW_MODE;
    public static final int ID_BLW_MODE = CarHvacManager.ID_BLW_MODE;

    //设置为具体温度
    public static final int ID_ZONED_TEMP_SETPOINT = CarHvacManager.ID_ZONED_TEMP_SETPOINT;
    public static final int ID_SPEECH_TEMP_LEFT = CarHvacManager.ID_SPEECH_TEMP_LEFT;
    public static final int ID_SPEECH_TEMP_RIGHT = CarHvacManager.ID_SPEECH_TEMP_RIGHT;

    //获取当前温度值
    public static final int ID_TEMP_L = CarHvacManager.ID_TEMP_L;
    public static final int ID_TEMP_R = CarHvacManager.ID_TEMP_R;

    //当前风量
    public static final int ID_SPEECH_BLOW_LV = CarHvacManager.ID_SPEECH_BLOW_LV;
    public static final int ID_BLW_LV = CarHvacManager.ID_BLW_LV;

    //打开/关闭压缩机
    public static final int ID_SPEECH_COMPRESSOR = CarHvacManager.ID_SPEECH_COMPRESSOR;

    public static final int ID_SPEECH_DEF = CarVendorExtensionManager.ID_SPEECH_DEF;

    //切换循环模式，包括内循环和外循环
    public static final int ID_SPEECH_FRS_REC = CarHvacManager.ID_SPEECH_FRS_REC;

    //打开/关闭自动模式
    public static final int ID_SPEECH_AUTO = CarHvacManager.ID_SPEECH_AUTO;


    //天窗控制
    public static final int ID_WINDOW_POS = CarCabinManager.ID_WINDOW_POS;
    public static final int ID_SPEECH_SMA_SLIDE_POS = CarCabinManager.ID_SPEECH_SMA_SLIDE_POS;
    public static final int ID_SMA_POS = CarCabinManager.ID_SMA_POS;

    //遮阳伞控制
    public static final int ID_SPEECH_SSM_SLIDE_POS = CarCabinManager.ID_SPEECH_SSM_SLIDE_POS;
    public static final int ID_SSM_POS = CarCabinManager.ID_SSM_POS;

    //打开/关闭后视镜加热
    public static final int ID_MIRROR_DEFROSTER_ON = CarHvacManager.ID_MIRROR_DEFROSTER_ON;

    //打开/关闭后风窗电加热
    public static final int ID_SPEECH_REAR_WIN_HEAT = CarHvacManager.ID_SPEECH_REAR_WIN_HEAT;

    //座椅加热控制
    public static final int ID_SPEECH_FL_SEAT_HEAT = CarHvacManager.ID_SPEECH_FL_SEAT_HEAT;
    public static final int ID_SPEECH_FR_SEAT_HEAT = CarHvacManager.ID_SPEECH_FR_SEAT_HEAT;

    //灯光控制
    public static final int ID_SPEECH_INSIDE_LIGHT = CarVendorExtensionManager.ID_SPEECH_INSIDE_LIGHT;
    public static final int ID_INTERIOR_LIGHT_SWITCH = CarVendorExtensionManager.ID_INTERIOR_LIGHT_SWITCH;

    //雨刷控制
    public static final int ID_SPEECH_FRONT_WIPER = CarCabinManager.ID_SPEECH_FRONT_WIPER;  //控制雨刷单次刮刷
    public static final int ID_SPEECH_FRONT_WASH = CarCabinManager.ID_SPEECH_FRONT_WASH;  //控制雨刷单次洗涤

    //是否有车速
    public static final int ID_SPEED_INFO = CarVendorExtensionManager.ID_SPEED_INFO;

    //门锁控制
    public static final int ID_SPEECH_DOOR_LOCK = CarCabinManager.ID_SPEECH_DOOR_LOCK;  //打开/关闭司机门车锁
    public static final int ID_SPEECH_LUGGAGE_DOOR = CarCabinManager.ID_SPEECH_LUGGAGE_DOOR;  //打开后备箱
    /*------------临时注释开始---------------*/
   /* public static final int ID_LUGGAGE_DOOR_STATE = CarVendorExtensionManager.ID_LUGGAGE_DOOR_STATE; //获取后备箱状态

    //车窗控制
    public static final int ID_WIN_STATE = CarCabinManager.ID_WIN_STATE;  //获取车窗状态
    public static final int ID_SPEECH_FL_WIN = CarCabinManager.ID_SPEECH_FL_WIN;  //打开/关闭司机车窗
    public static final int ID_SPEECH_FR_WIN = CarCabinManager.ID_SPEECH_FR_WIN;  //打开/关闭副司机车窗
    public static final int ID_SPEECH_RL_WIN = CarCabinManager.ID_SPEECH_RL_WIN; //打开/关闭左后窗
    public static final int ID_SPEECH_RR_WIN = CarCabinManager.ID_SPEECH_RR_WIN; //打开/关闭右后窗*/
    /*------------临时注释结束---------------*/

    public static final int ID_LUGGAGE_DOOR_STATE = 0; //获取后备箱状态

    //车窗控制
    public static final int ID_WIN_STATE = CarCabinManager.ID_WIN_STATE;  //获取全车窗状态
    public static final int ID_FL_WIN_DOOR_STATE = CarCabinManager.ID_FL_WIN_DOOR_STATE;  //获取左前车窗状态
    public static final int ID_FR_WIN_DOOR_STATE = CarCabinManager.ID_FR_WIN_DOOR_STATE;  //获取右前车窗状态
    public static final int ID_RL_WIN_DOOR_STATE = CarCabinManager.ID_RL_WIN_DOOR_STATE;  //获取左后车窗状态
    public static final int ID_RR_WIN_DOOR_STATE = CarCabinManager.ID_RR_WIN_DOOR_STATE;  //获取右后车窗状态
    public static final int ID_SPEECH_FL_WIN = CarCabinManager.ID_SPEECH_WIN_CTRL;  //打开/关闭司机车窗
    public static final int ID_SPEECH_FR_WIN = CarCabinManager.ID_SPEECH_WIN_CTRL;  //打开/关闭副司机车窗
    public static final int ID_SPEECH_RL_WIN = CarCabinManager.ID_SPEECH_WIN_CTRL; //打开/关闭左后窗
    public static final int ID_SPEECH_RR_WIN = CarCabinManager.ID_SPEECH_WIN_CTRL; //打开/关闭右后窗

    public static final int ID_WINDOW_LOCK = CarCabinManager.ID_SPEECH_WIN_CTRL;

    //空调调节模式
    public static final int FACE_MODEL = SpeechBlowMode.FACE_MODE;
    public static final int FOOT_DEF_MODEL = SpeechBlowMode.FOOT_DEF_MODE;
    public static final int FOOT_MODEL = SpeechBlowMode.FOOT_MODE;
    public static final int FACE_FOOT_MODEL = SpeechBlowMode.B_L_MODE;//参数不明确
    public static final int DEF_MODE = SpeechBlowMode.DEF_MODE;//参数不明确


    //全息影像
    public static final int ID_ROB_VERSION = CarVendorExtensionManager.ID_ROB_VERSION;
    public static final int ID_ROB_ACTION_MODE = CarVendorExtensionManager.ID_ROB_ACTION_MODE;
    public static final int ID_ROB_BRIGHTNESS = CarVendorExtensionManager.ID_ROB_BRIGHTNESS;
    public static final int ID_ROB_CHARACTER_MODE = CarVendorExtensionManager.ID_ROB_CHARACTER_MODE;// 全息动作时长
    public static final int ID_ROB_SWITCH = CarVendorExtensionManager.ID_ROB_SWITCH;
    public static final int ID_ROB_FACE_DIR = CarVendorExtensionManager.ID_ROB_FACE_DIR;
    public static final int SWITCH_ROBOT_ON = RobotMode.ON;
    public static final int SWITCH_ROBOT_OFF = RobotMode.OFF;

    //360环视
    //打开或关闭360全景接口
    public static final int ID_AVS_SWITCH = CarVendorExtensionManager.ID_AVS_SWITCH;
    //判断全景是否处于前台
    public static final int ID_CAMERA_STATUS = CarVendorExtensionManager.ID_CAMERA_STATUS;

    //平均油耗 TODO
    public static final int ID_LONG_TIME_FUEL_CONSUMPTION = CarVendorExtensionManager.ID_LONG_TIME_FUEL_CONSUMPTION;
    //    public static final int ID_LONG_TIME_FUEL_CONSUMPTION = 0;
    //续航里程
    public static final int ID_ODOMETER_RESIDUAL = CarVendorExtensionManager.ID_ODOMETER_RESIDUAL;
    //总里程
    public static final int SENSOR_TYPE_ODOMETER = CarSensorManager.SENSOR_TYPE_ODOMETER;

    //燃油不足
    public static final int ID_FUEL_WARNING = CarVendorExtensionManager.ID_FUEL_WARNING;


    //通过网络等途径同步时间
    public static final int ID_SYNC_TIME_ON = CarVendorExtensionManager.ID_SYNC_TIME_ON;

    public static final int ID_NEAR_DOOR_AUTO_UNLOCK_MODE = CarVendorExtensionManager.ID_NEAR_DOOR_AUTO_UNLOCK_MODE;//近车自动解锁
    public static final int ID_FAR_DOOR_AUTO_LOCK_MODE = CarVendorExtensionManager.ID_FAR_DOOR_AUTO_LOCK_MODE;//离车自动落锁


    public static final int ID_CONFIG = CarVendorExtensionManager.ID_CAR_MODEL;//车辆配置信息

    //语音门锁控制
    public static final int SpeechDoorLock_ALL_DOOR_LK_REQ = SpeechDoorLock.ALL_DOOR_LK_REQ;
    public static final int SpeechDoorLock_ALL_DOOR_UNLK_REQ = SpeechDoorLock.ALL_DOOR_UNLK_REQ;
    public static final int SpeechDoorLock_DRV_DOOR_LK_REQ = SpeechDoorLock.DRV_DOOR_LK_REQ;
    public static final int SpeechDoorLock_DRV_DOOR_UNLK_REQ = SpeechDoorLock.DRV_DOOR_UNLK_REQ;

    // 人脸识别相关
    public static final int ID_DMS_STATE = CarVendorExtensionManager.ID_DMS_STATE;
    public static final int ID_TIRED_REMIND_STATE = CarVendorExtensionManager.ID_TIRED_REMIND_STATE;
    public static final int ID_DISTRACTION_REMIND_STATE = CarVendorExtensionManager.ID_DISTRACTION_REMIND_STATE;
    public static final int ID_BAD_DRIVING_STATE = CarVendorExtensionManager.ID_BAD_DRIVING_STATE;


    //疲劳提醒灵敏度
    public static final int ID_TIRED_SENSITIVE = CarVendorExtensionManager.ID_TIRED_SENSITIVE;
    //Wifi加密方式
    public static final int WifiEncryption_EAP = Constants.WifiEncryption.EAP;
    public static final int WifiEncryption_FT_PSK = Constants.WifiEncryption.FT_PSK;
    public static final int WifiEncryption_NO = Constants.WifiEncryption.NO;
    public static final int WifiEncryption_WEP = Constants.WifiEncryption.WEP;
    public static final int WifiEncryption_WPA = Constants.WifiEncryption.WPA;
    public static final int WifiEncryption_WPA2 = Constants.WifiEncryption.WPA2;

    //总里程信息
    public static final int ID_ODOMETER = CarVendorExtensionManager.ID_ODOMETER;

    //双屏互动
    public static final int ID_INTERACT_MODE = CarVendorExtensionManager.ID_INTERACT_MODE;   //接收仪表交互模式
    public static final int ID_PWR_MODE = CarVendorExtensionManager.ID_PWR_MODE;   //IGN_ON/OFF
    public static final int ID_MEDIA_MENU_REQ = CarVendorExtensionManager.ID_MEDIA_MENU_REQ;   //媒体菜单等级
    public static final int ID_NAVI_DISPLAY_REQ = CarVendorExtensionManager.ID_NAVI_DISPLAY_REQ;   //导航显示请求
    public static final int ID_NAVI_FULL_SCREEN_REQ = CarVendorExtensionManager.ID_NAVI_FULL_SCREEN_REQ;   //全屏导航显示请求
    public static final int ID_IC_MENU_DISPLAY_REQ = CarVendorExtensionManager.ID_IC_MENU_DISPLAY_REQ;   //概要信息显示隐藏
    public static final int ID_NAVI_DISPLAY_IN_IC = CarVendorExtensionManager.ID_NAVI_DISPLAY_IN_IC;  //接受仪表导航显示请求
    public static final int ID_HU_INTERFACE_REQ = CarVendorExtensionManager.ID_HU_INTERFACE_REQ;   //发送仪表交互模式

    public static final int ID_RESTORE_CMD = CarVendorExtensionManager.ID_RESTORE_CMD;   //恢复出厂设置
    public static final int ID_TIME_INFO = CarVendorExtensionManager.ID_TIME_INFO;      //时间设置
    public static final int ID_SHOW_TIME_INFO = CarVendorExtensionManager.ID_CAN_TIME_INFO;      //显示时间设置

    public static final int OS_LOCAL_VERSION = CustomAction.VERSION_TYPE.OS_LOCAL_VERSION; // 系统版本
    public static final int MCU_LOCAL_VERSION = CustomAction.VERSION_TYPE.MCU_LOCAL_VERSION; // MCU版本
    public static final int ID_INTELLIGENT_MIRROR_SWITCH = CarVendorExtensionManager.ID_INTELLIGENT_MIRROR_SWITCH; // 设置外后视镜自动折叠
    public static final int ID_INTELLIGENT_MIRROR_SWITCH_GET = CarVendorExtensionManager.ID_INTELLIGENT_MIRROR_STATE; // 获取外后视镜自动折叠状态

    public static final int REAR_MIRROR_LEFT = MirrorState.PRESSED; // 左后视镜随动
    public static final int REAR_MIRROR_RIGHT = MirrorState.PRESSED; // 右后视镜随动

    //欠过压的ID，欠过压保护，关屏关功放(来自新途的定义)
    public static final int ID_VOLTAGE_UNSTABLE = CarVendorExtensionManager.ID_VOL_MODE_STATUS; // 欠过压

    public static class VALUE {
        //        安全带
        public static final int REAR_BELT_ON_REQ = CanOnOff4.ON_REQ;
        public static final int REAR_BELT_ON = CanOnOff4.ON;
        public static final int REAR_BELT_OFF_REQ = CanOnOff4.OFF_REQ;

        //主动制动 开关
        public static final int FCW_OFF_REQ = FCWOnOff.OFF_REQ;
        public static final int FCW_ON_REQ = FCWOnOff.ON_REQ;
        public static final int FCW_OFF = FCWOnOff.OFF;
        public static final int FCW_ON = FCWOnOff.ACTIVE;
        public static final int FCW_STANDBY = FCWOnOff.STANDBY;
        //主动制动 灵敏度
        public static final int FCW_LOW_REQ = FcwSensitivity.LOW_REQ;
        public static final int FCW_NORMAL_REQ = FcwSensitivity.NORMAL_REQ;
        public static final int FCW_HIGH_REQ = FcwSensitivity.HIGH_REQ;
        public static final int REAR_BELT_OFF = CanOnOff4.OFF_REQ;
        public static final int FCW_LOW = FcwSensitivity.LOW;
        public static final int FCW_NORMAL = FcwSensitivity.NORMAL;
        public static final int FCW_HIGH = FcwSensitivity.HIGH;


        //车道保持
//        public static final int LKA_MODE_INACTIVE = IfcState.Mode.INACTIVE;
        public static final int LKA_MODE_LDW = LdwLkaLcMode.LDW;
        public static final int LKA_MODE_LDW_REQ = LdwLkaLcMode.LDW_REQ;
        public static final int LKA_MODE_LKA = LdwLkaLcMode.LKA;
        public static final int LKA_MODE_LKA_REQ = LdwLkaLcMode.LKA_REQ;
        public static final int LKA_MODE_LC = LdwLkaLcMode.LC;
        public static final int LKA_MODE_LC_REQ = LdwLkaLcMode.LC_REQ;

        //ISA交通标志
        public static final int IFC_OFF = CanOnOff1.OFF_REQ;
        public static final int IFC_ON = CanOnOff1.ACTIVE;
        public static final int IFC_STANDBY = CanOnOff1.STANDBY;
        public static final int IFC_ACTIVE = CanOnOff1.ON_REQ;

        //变道闪烁
        public static final int LANE_CHANGE = 0;

        //驾驶员注意力提醒
        public static final int DAW_OFF_REQ = CanOnOff1.OFF_REQ;
        public static final int DAW_OFF = CanOnOff1.OFF;
        public static final int DAW_ON_REQ = CanOnOff1.ON_REQ;
        public static final int DAW_ON = CanOnOff1.ACTIVE;
        public static final int DAW_STANDBY = CanOnOff1.STANDBY;


        //车道偏离
        public static final int LDW_LOW_REQ = LDWSensitivity.LOW_REQ;
        public static final int LDW_LOW = LDWSensitivity.LOW;
        public static final int LDW_NORMAL = LDWSensitivity.NORMAL;
        public static final int LDW_NORMAL_REQ = LDWSensitivity.NORMAL_REQ;
        public static final int LDW_HIGH = LDWSensitivity.HIGH;
        public static final int LDW_HIGH_REQ = LDWSensitivity.HIGH_REQ;

        //自动夹紧
        public static final int EPB_OFF_REQ = CanOnOff2.OFF_REQ;
        public static final int EPB_ON_REQ = CanOnOff2.ON_REQ;
        public static final int EPB_OFF = CanOnOff2.OFF;
        public static final int EPB_ON = CanOnOff2.ON;

        //锁车自动关窗
        public static final int AUTO_CLOSE_WINDOW_OFF = CanOnOff3.OFF_REQ;
        public static final int AUTO_CLOSE_WINDOW_ON = CanOnOff3.ON_REQ;

        //行李箱
        public static final boolean TRUNK_ON = true;
        public static final boolean TRUNK_OFF = false;

        //后视镜自动折叠
        public static final int REARMIRROR_OFF = CanOnOff1.OFF_REQ;
        public static final int REARMIRROR_ON = CanOnOff1.ON_REQ;

        //回家模式
        public static final int GO_HOME_M1 = ComeHomeMode.MODE_1;
        public static final int GO_HOME_M1_REQ = ComeHomeMode.MODE_1_REQ;
        public static final int GO_HOME_M2_REQ = ComeHomeMode.MODE_2_REQ;
        public static final int GO_HOME_M2 = ComeHomeMode.MODE_2;
        public static final int GO_HOME_M3 = ComeHomeMode.MODE_3;
        public static final int GO_HOME_M3_REQ = ComeHomeMode.MODE_3_REQ;
        public static final int GO_HOME_OFF = ComeHomeMode.OFF;
        public static final int GO_HOME_OFF_REQ = ComeHomeMode.OFF_REQ;

        //ihc
        public static final int IHC_OFF = CanOnOff2.OFF;
        public static final int IHC_OFF_REQ = CanOnOff2.OFF_REQ;
        public static final int IHC_ON = CanOnOff2.ON;
        public static final int IHC_ON_REQ = CanOnOff2.ON_REQ;

        //氛围灯
        public static final int ATMOSPHERE_LIGHT_OFF = CanCommon.OFF;
        public static final int ATMOSPHERE_LIGHT_OFF_REQ = CanCommon.OFF;
        public static final int ATMOSPHERE_LIGHT_ON = CanCommon.ON;
        public static final int ATMOSPHERE_LIGHT_ON_REQ = CanCommon.ON;

        //车辆信息提示音
        public static final int INFORMATION_TONE_LEVER_NORMAL = InformationToneLever.NORMAL;
        public static final int INFORMATION_TONE_LEVER_LARGER = InformationToneLever.LARGER;

        //车速音量补偿
        public static final int SPEEDGAIN_OFF = ALCLevel.OFF;
        public static final int SPEEDGAIN_LOW = ALCLevel.LV1;
        public static final int SPEEDGAIN_MID = ALCLevel.LV2;
        public static final int SPEEDGAIN_HIGH = ALCLevel.LV3;

        // 泊车媒体音量等级
        public static final int PARKING_MEDIA_MUTE = ParkingMediaLevel.MUTE;     // 静音
        public static final int PARKING_MEDIA_REDUCE = ParkingMediaLevel.REDUCE; // 弱化
        public static final int PARKING_MEDIA_NORMAL = ParkingMediaLevel.NORMAL; // 正常

        //ARKAMYS 3D音效
        public static final int ARKAMYS_3D_OFF = ArkamysSwitch.OFF;
        public static final int ARKAMYS_3D_ALL_ON = ArkamysSwitch.ALL_ON;
        public static final int ARKAMYS_3D_DRV_ON = ArkamysSwitch.DRV_ON;


        //最佳听音位
        //暂时为0
        public static final int FRONT_MASTER = 0;
        public static final int FRONT_SLAVE = 1;
        public static final int REAR_MASTER = 2;
        public static final int REAR_SLAVE = 3;
        public static final int CENTER = 4;
        public static final int CUSTOMIZE = 5;

        //声场模式
        public static final int STANDARD = SoundField.STANDARD;
        public static final int CINEMA = SoundField.CINEMA;
        public static final int ODEUM = SoundField.ODEUM;

        //开关机音效
        public static final boolean BOOT_MUSIC_ON = true;
        public static final boolean BOOT_MUSIC_OFF = false;

        //音效模式
        public static final int SOUND_EFFECTS_STANDARD = SoundEffects.STANDARD;
        public static final int SOUND_EFFECTS_POP = SoundEffects.POP;
        public static final int SOUND_EFFECTS_CLASSIC = SoundEffects.CLASSIC;
        public static final int SOUND_EFFECTS_JAZZ = SoundEffects.JAZZ;
        public static final int SOUND_EFFECTS_USER = SoundEffects.USER;

        //音效
        public static final int LEVEL_0 = SoundEffects.LEVEL_0;
        public static final int LEVEL_1 = SoundEffects.LEVEL_1;
        public static final int LEVEL_2 = SoundEffects.LEVEL_2;
        public static final int LEVEL_3 = SoundEffects.LEVEL_3;
        public static final int LEVEL_4 = SoundEffects.LEVEL_4;
        public static final int LEVEL_5 = SoundEffects.LEVEL_5;
        public static final int LEVEL_6 = SoundEffects.LEVEL_6;
        public static final int LEVEL_7 = SoundEffects.LEVEL_7;
        public static final int LEVEL_8 = SoundEffects.LEVEL_8;
        public static final int LEVEL_9 = SoundEffects.LEVEL_9;
        public static final int LEVEL_10 = SoundEffects.LEVEL_10;
        public static final int LEVEL_11 = SoundEffects.LEVEL_11;
        public static final int LEVEL_12 = SoundEffects.LEVEL_12;
        public static final int LEVEL_13 = SoundEffects.LEVEL_13;
        public static final int LEVEL_14 = SoundEffects.LEVEL_14;

        //泊车媒体音量
        public static final int VEHICLEHINTS_MUTE = ALCLevel.OFF;
        public static final int VEHICLEHINTS_REDUCE = ALCLevel.LV1;
        public static final int VEHICLEHINTS_NORMAL = ALCLevel.LV2;

        //信息提示音
        public static final int VehicleHints_LV1 = InformationToneLever.NORMAL;
        public static final int VehicleHints_LV2 = InformationToneLever.LARGER;

        //亮度
        public static final int DAY_DISPLAY_LEVEL0 = DisplayScreen.LEVEL0;
        public static final int DAY_DISPLAY_LEVEL1 = DisplayScreen.LEVEL1;
        public static final int DAY_DISPLAY_LEVEL2 = DisplayScreen.LEVEL2;
        public static final int DAY_DISPLAY_LEVEL3 = DisplayScreen.LEVEL3;
        public static final int DAY_DISPLAY_LEVEL4 = DisplayScreen.LEVEL4;
        public static final int DAY_DISPLAY_LEVEL5 = DisplayScreen.LEVEL5;
        public static final int DAY_DISPLAY_LEVEL6 = DisplayScreen.LEVEL6;
        public static final int DAY_DISPLAY_LEVEL7 = DisplayScreen.LEVEL7;
        public static final int DAY_DISPLAY_LEVEL8 = DisplayScreen.LEVEL8;
        public static final int DAY_DISPLAY_LEVEL9 = DisplayScreen.LEVEL9;
        public static final int DAY_DISPLAY_LEVEL10 = DisplayScreen.LEVEL10;

        //屏幕显示模式
        public static final int DISPLAY_AUTO = DisplayScreen.AUTO; //自动显示模式
        public static final int DISPLAY_DAY = DisplayScreen.DAY; //白天显示模式
        public static final int DISPLAY_NIGHT = DisplayScreen.NIGHT; //黑夜显示模式

        //行车看视频
        public static final boolean BAN_VIDEO = true;    //禁止行车看视频
        public static final boolean UN_BAN_VIDEO = false;    //不禁止行车看视频

        //随速闭锁
        public static final int AUTOLOCK_OFF = /*BcmState.SpeedAutoLock.OFF*/0;
        public static final int AUTOLOCK_ON = /*BcmState.SpeedAutoLock.ON*/0;

        //离车自动落锁
        public static final int LEAVE_AUTO_LOCK_OFF = CanOnOff4.OFF;
        public static final int LEAVE_AUTO_LOCK_ON = CanOnOff4.ON;

        //走近自动解锁
        public static final int APPROACH_AUTO_UNLOCK_OFF = CanOnOff4.OFF;
        public static final int APPROACH_AUTO_UNLOCK_ON = CanOnOff4.ON;

        //遥控解锁模式
        public static final int LOCK_ALL_DOOR_REQ = DoorUnlock.ALL_DOOR_REQ;
        public static final int LOCK_ALL_DOOR = DoorUnlock.ALL_DOOR;
        public static final int LOCK_DRIVER_DOOR_REQ = DoorUnlock.DRIVER_DOOR_REQ;
        public static final int LOCK_DRIVER_DOOR = DoorUnlock.DRIVER_DOOR;

        //发动机状态
        public static final int ENGINESTATE_OFF = EngineState.ENGINE_OFF;
        public static final int ENGINESTATE_ON = EngineState.ENGINE_RUNNING;

        //情景随动颜色
        /*public static final int MUSIC_FOLLOW_COLOR_1 = MusicFollowColor.COLOR_1;
        public static final int MUSIC_FOLLOW_COLOR_2 = MusicFollowColor.COLOR_2;
        public static final int MUSIC_FOLLOW_COLOR_3 = MusicFollowColor.COLOR_3;
        public static final int MUSIC_FOLLOW_COLOR_4 = MusicFollowColor.COLOR_4;
        public static final int MUSIC_FOLLOW_COLOR_5 = MusicFollowColor.COLOR_5;
        public static final int MUSIC_FOLLOW_COLOR_6 = MusicFollowColor.COLOR_6;
        public static final int MUSIC_FOLLOW_COLOR_7 = MusicFollowColor.COLOR_7;
        public static final int MUSIC_FOLLOW_COLOR_8 = MusicFollowColor.COLOR_8;
        public static final int MUSIC_FOLLOW_COLOR_9 = MusicFollowColor.COLOR_9;
        public static final int MUSIC_FOLLOW_COLOR_10 = MusicFollowColor.COLOR_10;
        public static final int MUSIC_FOLLOW_COLOR_11 = MusicFollowColor.COLOR_11;
        public static final int MUSIC_FOLLOW_COLOR_12 = MusicFollowColor.COLOR_12;*/
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
        public static final boolean MUSIC_FOLLOW_OFF =  /*SwitchState.OFF*/true;
        public static final boolean MUSIC_FOLLOW_ON = /*SwitchState.ON*/true;

        //胎压复位
        // TODO: 2018/12/5 0005 胎压复位值
        public static final int RESET_TIRE = 0;

        //关闭屏幕状态
        public static boolean SCREEN_OFF = false;
        public static boolean SCREEN_ON = true;

        //座椅迎宾
        public static final int WELCOME_SEAT_OFF = /*BcmState.WelcomeSeat.OFF*/0;
        public static final int WELCOME_SEAT_ON = /*BcmState.WelcomeSeat.ON*/0;

        //离车登车灯光律动
        public static final boolean EELCOME_LIGHT_MOTION_OFF = false;
        public static final boolean EELCOME_LIGHT_MOTION_ON = true;

        //室内灯延时
        public static final int INTERIOR_LIGHT_DELAY_10S = LampLightDalay.DELAY1;
        public static final int INTERIOR_LIGHT_DELAY_10S_REQ = LampLightDalay.DELAY1_REQ;
        public static final int INTERIOR_LIGHT_DELAY_20S = LampLightDalay.DELAY2;
        public static final int INTERIOR_LIGHT_DELAY_20S_REQ = LampLightDalay.DELAY2_REQ;
        public static final int INTERIOR_LIGHT_DELAY_30S = LampLightDalay.DELAY3;
        public static final int INTERIOR_LIGHT_DELAY_30S_REQ = LampLightDalay.DELAY3_REQ;

        //变道闪烁
        public static final int LANE_CHANGE_TIME_1 = LaneChangeFlickerTime.TIME_1;
        public static final int LANE_CHANGE_TIME_3 = LaneChangeFlickerTime.TIME_3;

        //迎宾灯开关
        public static final int WELCOM_LIGHT_OFF_REQ = CanOnOff2.OFF_REQ;
        public static final int WELCOM_LIGHT_OFF = CanOnOff2.OFF;
        public static final int WELCOM_LIGHT_ON_REQ = CanOnOff2.ON_REQ;
        public static final int WELCOM_LIGHT_ON = CanOnOff2.ON;

        //降噪模式
        public static final int SR_NOISE_CLEAN = SrMode.NOISE_CLEAN;

        //仪表当前TAB位置获取及TAB切换通知
//        public static final int InteractMode_HU_RESET = InteractMode.HU_RESET;
//        public static final int InteractMode_IGN_SWITCH_REQ = InteractMode.IGN_SWITCH_REQ;
        public static final int InteractMode_INACTIVE_REQ = InteractMode.INACTIVE_REQ;
        public static final int InteractMode_MEDIA = InteractMode.MEDIA;
        public static final int InteractMode_MEDIA_REQ = InteractMode.MEDIA_REQ;
        public static final int InteractMode_NAVI = InteractMode.NAVI;
        public static final int InteractMode_NAVI_REQ = InteractMode.NAVI_REQ;
        public static final int InteractMode_TEL = InteractMode.TEL;
        public static final int InteractMode_TEL_REQ = InteractMode.TEL_REQ;
        public static final int HuInteractReq_TEL_REQ = HuInteractReq.TEL_REQ;
        public static final int HuInteractReq_NAVI_REQ = HuInteractReq.NAVI_REQ;
        public static final int HuInteractReq_INACTIVE_REQ = HuInteractReq.INACTIVE_REQ;
        public static final int InteractMode_INACTIVE = InteractMode.INACTIVE;

        //左侧仪表投屏
        public static final int CanCommon_ON = CanCommon.ON;
        public static final int CanCommon_OFF = CanCommon.OFF;


        //吹风模式切换
        public static final int SpeechBlowMode_B_L_MODE = SpeechBlowMode.B_L_MODE;
        public static final int SpeechBlowMode_B_L_MODE_FAIL = SpeechBlowMode.B_L_MODE_FAIL;
        public static final int SpeechBlowMode_B_L_MODE_SUCC = SpeechBlowMode.B_L_MODE_SUCC;
        public static final int SpeechBlowMode_DEF_MODE = SpeechBlowMode.DEF_MODE;
        public static final int SpeechBlowMode_DEF_MODE_FAIL = SpeechBlowMode.DEF_MODE_FAIL;
        public static final int SpeechBlowMode_DEF_MODE_SUCC = SpeechBlowMode.DEF_MODE_SUCC;
        public static final int SpeechBlowMode_FACE_MODE = SpeechBlowMode.FACE_MODE;
        public static final int SpeechBlowMode_FACE_MODE_FAIL = SpeechBlowMode.FACE_MODE_FAIL;
        public static final int SpeechBlowMode_FACE_MODE_SUCC = SpeechBlowMode.FACE_MODE_SUCC;
        public static final int SpeechBlowMode_FOOT_DEF_MODE = SpeechBlowMode.FOOT_DEF_MODE;
        public static final int SpeechBlowMode_FOOT_DEF_MODE_FAIL = SpeechBlowMode.FOOT_DEF_MODE_FAIL;
        public static final int SpeechBlowMode_FOOT_DEF_MODE_SUCC = SpeechBlowMode.FOOT_DEF_MODE_SUCC;
        public static final int SpeechBlowMode_FOOT_MODE = SpeechBlowMode.FOOT_MODE;
        public static final int SpeechBlowMode_FOOT_MODE_FAIL = SpeechBlowMode.FOOT_MODE_FAIL;
        public static final int SpeechBlowMode_FOOT_MODE_SUCC = SpeechBlowMode.FOOT_MODE_SUCC;
        public static final int SpeechBlowMode_INACTIVE = SpeechBlowMode.INACTIVE;
        public static final int SpeechBlowMode_INACTIVE_REQ = SpeechBlowMode.INACTIVE_REQ;
        public static final int B_L = BlowMode.B_L;
        public static final int DEF = BlowMode.DEF;
        public static final int FACE = BlowMode.FACE;
        public static final int FOOT = BlowMode.FOOT;
        public static final int FOOT_DEF = BlowMode.FOOT_DEF;
        public static final int INACTIVE = BlowMode.INACTIVE;

        //风量的值
        public static final int SpeechBlowLv_FAILURE = SpeechBlowLv.FAILURE;
        public static final int SpeechBlowLv_INACTIVE = SpeechBlowLv.INACTIVE;
        public static final int SpeechBlowLv_INACTIVE_REQ = SpeechBlowLv.INACTIVE_REQ;
        public static final int SpeechBlowLv_LV_1 = SpeechBlowLv.LV_1;
        public static final int SpeechBlowLv_LV_2 = SpeechBlowLv.LV_2;
        public static final int SpeechBlowLv_LV_3 = SpeechBlowLv.LV_3;
        public static final int SpeechBlowLv_LV_4 = SpeechBlowLv.LV_4;
        public static final int SpeechBlowLv_LV_5 = SpeechBlowLv.LV_5;
        public static final int SpeechBlowLv_LV_6 = SpeechBlowLv.LV_6;
        public static final int SpeechBlowLv_LV_7 = SpeechBlowLv.LV_7;
        public static final int SpeechBlowLv_SUCCESS = SpeechBlowLv.SUCCESS;
        public static final int BlowLv_LV_1 = BlowLv.LV_1;
        public static final int BlowLv_LV_2 = BlowLv.LV_2;
        public static final int BlowLv_LV_3 = BlowLv.LV_3;
        public static final int BlowLv_LV_4 = BlowLv.LV_4;
        public static final int BlowLv_LV_5 = BlowLv.LV_5;
        public static final int BlowLv_LV_6 = BlowLv.LV_6;
        public static final int BlowLv_LV_7 = BlowLv.LV_7;
        public static final int BlowLv_NO_SELECT = BlowLv.NO_SELECT;

        //空调、压缩机, 空调自动模式, 座椅加热 取值
        public static final int SpeechOnOff2_INACTIVE = SpeechOnOff2.INACTIVE;
        public static final int SpeechOnOff2_INACTIVE_REQ = SpeechOnOff2.INACTIVE_REQ;
        public static final int SpeechOnOff2_OFF_FAIL = SpeechOnOff2.OFF_FAIL;
        public static final int SpeechOnOff2_OFF_REQ = SpeechOnOff2.OFF_REQ;
        public static final int SpeechOnOff2_OFF_SUCCESS = SpeechOnOff2.OFF_SUCCESS;
        public static final int SpeechOnOff2_ON_FAIL = SpeechOnOff2.ON_FAIL;
        public static final int SpeechOnOff2_ON_REQ = SpeechOnOff2.ON_REQ;
        public static final int SpeechOnOff2_ON_SUCCESS = SpeechOnOff2.ON_SUCCESS;


        //切换循环模式取值
        public static final int SpeechFrsRec_FRS_FAIL = SpeechFrsRec.FRS_FAIL;
        public static final int SpeechFrsRec_FRS_REQ = SpeechFrsRec.FRS_REQ;
        public static final int SpeechFrsRec_FRS_SUC = SpeechFrsRec.FRS_SUC;
        public static final int SpeechFrsRec_INACTIVE = SpeechFrsRec.INACTIVE;
        public static final int SpeechFrsRec_INACTIVE_REQ = SpeechFrsRec.INACTIVE_REQ;
        public static final int SpeechFrsRec_REC_FAIL = SpeechFrsRec.REC_FAIL;
        public static final int SpeechFrsRec_REC_REQ = SpeechFrsRec.REC_REQ;
        public static final int SpeechFrsRec_REC_SUCC = SpeechFrsRec.REC_SUCC;

        //灯光控制 取值
        public static final int CanOnOff2_INACTIVE_REQ = CanOnOff2.INACTIVE_REQ;
        public static final int CanOnOff2_OFF = CanOnOff2.OFF;
        public static final int CanOnOff2_OFF_REQ = CanOnOff2.OFF_REQ;
        public static final int CanOnOff2_ON = CanOnOff2.ON;
        public static final int CanOnOff2_ON_REQ = CanOnOff2.ON_REQ;

        //车窗状态 取值
        public static final int WinType_FL = WinType.FL;
        public static final int WinType_FR = WinType.FR;
        public static final int WinType_RL = WinType.RL;
        public static final int WinType_RR = WinType.RR;

        //媒体音量
        public static final int ID_MEDIA_VOLUME = CarVendorExtensionManager.ID_MEDIA_VOLUME;
        //蓝牙音乐音量
        public static final int ID_BT_MEDIA_VOLUME = CarVendorExtensionManager.ID_BT_MEDIA_VOLUME;
        //电话音量
        public static final int ID_BT_VOLUME = CarVendorExtensionManager.ID_BT_VOLUME;
        //语音播报音量
        public static final int ID_TTS_VOLUME = CarVendorExtensionManager.ID_NAVI_TTS_VOLUME;

        public static final int MEDIA_MAX_VALUE = 40;
        public static final int MEDIA_MIN_VALUE = 1;

        //音乐情景随动开关
        public static final int MUSIC_SCENE_FOLLOW_ON = CanCommon.ON;

        public static final int SPEED_AUTOMATIC_LOCK_ON = CanOnOff1.ACTIVE;
        public static final int LEAVE_CAR_AUTOMATIC_LOCK_ON = CanOnOff4.ON;

        public static final int SELF_CLOSING_WINDOW_ON = CanOnOff3.ON;
        public static final int AUTOMATIC_TRUNK_ON = CanOnOff3.ON;
        public static final int MIRROR_AUTOMATIC_FOLDING_ON = CanOnOff2.ON;
        public static final int WELCOME_SEAT_TURN_ON = CanOnOff4.ON;
        public static final int MUSIC_SCENE_FOLLOW = CanCommon.ON;

        //空调风速
        public static final int FAN_SPEED_LEVEL_1 = SpeechBlowLv.LV_1;
        public static final int FAN_SPEED_LEVEL_2 = SpeechBlowLv.LV_2;
        public static final int FAN_SPEED_LEVEL_3 = SpeechBlowLv.LV_3;
        public static final int FAN_SPEED_LEVEL_4 = SpeechBlowLv.LV_4;
        public static final int FAN_SPEED_LEVEL_5 = SpeechBlowLv.LV_5;
        public static final int FAN_SPEED_LEVEL_6 = SpeechBlowLv.LV_6;
        public static final int FAN_SPEED_LEVEL_7 = SpeechBlowLv.LV_7;

        //通用的状态常量
        public static final int CAN_ON_OFF2_OFF = CanOnOff2_OFF;
        public static final int CAN_ON_OFF2_ON = CanOnOff2_ON;
        public static final int CAN_ON_OFF2_ON_REQ = CanOnOff2_ON_REQ;
        public static final int CAN_ON_OFF2_OFF_REQ = CanOnOff2_OFF_REQ;


        public static final int TIRED_HIGH = TiredSensitivity.HIGH;
        public static final int TIRED_HIGH_REQ = TiredSensitivity.HIGH_REQ;
        public static final int TIRED_INACTIVE_REQ = TiredSensitivity.INACTIVE_REQ;
        public static final int TIRED_LOW = TiredSensitivity.LOW;
        public static final int TIRED_LOW_REQ = TiredSensitivity.LOW_REQ;
        public static final int TIRED_NORMAL = TiredSensitivity.NORMAL;
        public static final int TIRED_NORMAL_REQ = TiredSensitivity.NORMAL_REQ;

        //外后视镜自动折叠
        public static final int REAR_VEIW_ENABLE_REQ = CanOnOff2.ON_REQ;      //RearviewMirror
        public static final int REAR_VEIW_DISABLE_REQ = CanOnOff2.OFF_REQ;      //RearviewMirror
        public static final int REAR_VEIW_ENABLE = CanOnOff2.ON;     //RearviewMirror
        public static final int REAR_VEIW_DISABLE = CanOnOff2.OFF;      //RearviewMirror


        //全息


        public static final int INCREASE_REQ = RobotBrightnessReq.INCREASE_REQ;//增加
        public static final int DECREASE_REQ = RobotBrightnessReq.DECREASE_REQ;//减小

        public static final int MIRROR_LEFT_CONFIRM = CarVendorExtensionManager.ID_LEFT_MIRROR_CONFIRM;//记录左后视镜位置
        public static final int MIRROR_RIGHT_CONFIRM = CarVendorExtensionManager.ID_RIGHT_MIRROR_CONFIRM;//记录右后视镜位置

        //工作模式
        public static final int WORK_MODE_STANDBY = WorkMode.STANDBY;

    }


    public static class WorkMode {
        public static final int NORMAL = android.car.hardware.vendor.WorkMode.NORMAL;
        public static final int STANDBY = android.car.hardware.vendor.WorkMode.STANDBY;
    }


    public static class Area {
        public static final int SEAT_ALL = VehicleAreaSeat.SEAT_ROW_1_LEFT |
                VehicleAreaSeat.SEAT_ROW_1_RIGHT | VehicleAreaSeat.SEAT_ROW_2_LEFT |
                VehicleAreaSeat.SEAT_ROW_2_CENTER | VehicleAreaSeat.SEAT_ROW_2_RIGHT;

        //温度area
        public static final int HAVC_LEFT = VehicleAreaSeat.SEAT_ROW_1_LEFT |
                VehicleAreaSeat.SEAT_ROW_2_LEFT | VehicleAreaSeat.SEAT_ROW_2_CENTER;
        public static final int HAVC_RIGHT = VehicleAreaSeat.SEAT_ROW_1_RIGHT | VehicleAreaSeat.SEAT_ROW_2_RIGHT;
        public static final int HAVC_ALL = HAVC_LEFT | HAVC_RIGHT;

        //驾驶座位
        private static final int DRIVER_ZONE_ID = VehicleAreaSeat.SEAT_ROW_1_LEFT |
                VehicleAreaSeat.SEAT_ROW_2_LEFT | VehicleAreaSeat.SEAT_ROW_2_CENTER;
        private static final int PASSENGER_ZONE_ID = VehicleAreaSeat.SEAT_ROW_1_RIGHT |
                VehicleAreaSeat.SEAT_ROW_2_RIGHT;
    }

    public static class CarSenSor {
        //CarEvent分发事件，车速，档位数据ID
        public static final int GEAR_ID = 1000000001;
        public static final int WHEEL_ANGEL_ID = 1000000009;
    }

    public static class EngineState {
        //CarEvent分发事件，车速，档位数据ID
        public static final int ENGINE_RUNNING = android.car.hardware.vendor.EngineState.RUNNING;
        public static final int ENGINE_OFF = android.car.hardware.vendor.EngineState.OFF;
    }

    public static class WifiMode {
        public static final int WIFI_MODE_ID = 1000000002;
        public static final int AP = Constants.WifiMode.AP; //AP 对应的是热点模式 Constants.WifiMode.AP
        public static final int OFF = Constants.WifiMode.OFF; // 关闭 Constants.WifiMode.OFF
        public static final int STA = Constants.WifiMode.STA; //STA 对应的是wifi模式 Constants.WifiMode.STA
    }

    public static class SIMMode {
        public static final int SIM_MODE_ID = 1000000004;
        public static final int SIM_SWITCH_ID = 1000000005;
        public static final int TWO_G = Constants.NetworkType.TWO_G;//SIM卡2G网络
        public static final int THREE_G = Constants.NetworkType.THREE_G;//SIM卡3G网络
        public static final int FOUR_G = Constants.NetworkType.FOUR_G;//SIM卡4G网络
        public static final int SIM_CARD_ERROR = Constants.NetworkType.SIM_CARD_ERROR;//SIM卡异常
        public static final int N0_SERVICE = Constants.NetworkType.N0_SERVICE;//SIM卡无服务
        public static final int SIM_RSSI_MAX = 96;//SIM卡信号强度最大值
    }

    public static class RequestAck {
        public static final int RESULT_ERROR = Constants.RequestAck.RESULT_ERROR;  //Constants.RequestAck.RESULT_ERROR;
        public static final int RESULT_SUCCESS = Constants.RequestAck.RESULT_SUCCESS;//Constants.RequestAck.RESULT_SUCCESS;
        public static final int RESULT_FAILED = Constants.RequestAck.RESULT_FAILED;//Constants.RequestAck.RESULT_FAILED;
    }

    public static class CallType {
        public static final int BCALL = Constants.CallType.BCALL;
        public static final int ICALL = Constants.CallType.ICALL;
    }

    public static class CallOperation {
        public static final int ANSWER = Constants.CallOperation.ANSWER;
        public static final int HANDUP = Constants.CallOperation.HANGUP;
        public static final int DIAL = Constants.CallOperation.DIAL;
    }

    public static class WifiOperation {
        public static final int CONNECT = Constants.WifiOperator.CONNECT;
        public static final int DISCONNECT = Constants.WifiOperator.DISCONNECT;
        public static final int DELETE = Constants.WifiOperator.DELETE;
    }

    public static class WifiSavedState {
        public static final int SAVED = Constants.WiFiSavedState.SAVED;
        public static final int UNSAVED = Constants.WiFiSavedState.UNSAVED;
    }

    public static class WifiEncryption {
        public static int EAP = Constants.WifiEncryption.EAP;
        public static int FT_PSK = Constants.WifiEncryption.FT_PSK;
        public static int NO = Constants.WifiEncryption.NO;
        public static int WAPI = Constants.WifiEncryption.WAPI;
        public static int WPA = Constants.WifiEncryption.WPA;
        public static int WPA2 = Constants.WifiEncryption.WPA2;
    }

    public static class WifiConnectStatus {
        public static int CONNECTED = Constants.ConnectStatus.CONNECTED;
        public static int DISCONNECTED = Constants.ConnectStatus.DISCONNECTED;
        public static int AUTHENTICATION_FAILED = Constants.ConnectStatus.AUTHENTICATION_FAILED;
        public static int IP_ACQUISITION_FAILED = Constants.ConnectStatus.IP_ACQUISITION_FAILED;
    }

    // WIFI相关的事件分发的ID
    public static class WifiAboutEventId {
        public static int ID_WIFI_LIST_EVENT = 1000000003;
        public static int ID_WIFI_CONNECT_STATUS_EVENT = 1000000006;
        public static int ID_WIFI_AP_INFO = 1000000007;
        public static int ID_WIFI_AP_ACCOUNT_INFO = 1000000009;
    }

    public static class TboxCallState {
        public static int ID_TBOX_CALL_STATE = 1000000008;
        public static final int ANSWER = Constants.TBOXCallState.ANSWER;
        public static final int HANGING_UP = Constants.TBOXCallState.HANGING_UP;
        public static final int HANGUP_EXPIRE_FAIL = Constants.TBOXCallState.HANGUP_EXPIRE_FAIL;
        public static final int DIALING = Constants.TBOXCallState.DIALING;
        public static final int RING_BACK = Constants.TBOXCallState.RING_BACK;
    }

    /**
     * 档位定义常量
     */
    public static class CarGearMode {
        public static final int GEAR_P = GearMode.GEAR_P;
        public static final int GEAR_R = GearMode.GEAR_R;
        public static final int GEAR_N = GearMode.GEAR_N;
        public static final int GEAR_1 = GearMode.GEAR_1;
        public static final int GEAR_2 = GearMode.GEAR_2;
        public static final int GEAR_3 = GearMode.GEAR_3;
        public static final int GEAR_4 = GearMode.GEAR_4;
        public static final int GEAR_5 = GearMode.GEAR_5;
        public static final int GEAR_6 = GearMode.GEAR_6;
        public static final int GEAR_7 = GearMode.GEAR_7;
        public static final int GEAR_8 = GearMode.GEAR_8;
        public static final int NO_CONN = GearMode.NO_CONN;
        public static final int INVALID = GearMode.INVALID;
    }
}
