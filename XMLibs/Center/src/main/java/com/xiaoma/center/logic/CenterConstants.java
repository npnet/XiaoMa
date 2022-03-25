package com.xiaoma.center.logic;

import com.xiaoma.center.logic.agent.CenterService;

/**
 * @author youthyJ
 * @date 2019/1/15
 */
public class CenterConstants {
    public static final String DEPEND_PACKAGE_NAME = "com.xiaoma.launcher";
    public static final String SERVICE_CLASS_NAME = CenterService.class.getName();
    public static final int LOCAL_LINKER_PORT = 0;

    /**
     * 用于发送 Launcher 初始化Center成功的广播Action
     */
    public static final String RECEIVER_ACTION = "LauncherOn";
    public static final String BLUETOOTH_PHONE = "com.xiaoma.bluetooth.phone";
    public static final String MUSIC = "com.xiaoma.music";
    public static final String XTING = "com.xiaoma.xting";
    public static final String SETTING = "com.xiaoma.setting";
    public static final String AIR_CONDITIONER = "com.xiaoma.air.conditioner";
    public static final String VEHICLE_CONDITION = "com.xiaoma.vehicle.condition";
    public static final String CAR_CONTROL = "com.xiaoma.car.control";
    public static final String LAUNCHER = "com.xiaoma.launcher";
    public static final String XKAN = "com.xiaoma.xkan";
    public static final String SHOP = "com.xiaoma.shop";
    public static final String CAR_SERVICE = "com.xiaoma.service";
    public static final String CAR_SERVICE_RECEIVER = "com.xiaoma.service.common.receiver.IBCallReceiver";
    public static final String BACK_HOME = "com.xiaoma.launcher";
    public static final String BACK_HOME_CLASS = "com.xiaoma.launcher.common.receiver.IBCallReceiver";
    public static final String ASSISTANT = "com.xiaoma.assistant";

    public static final int BLUETOOTH_PHONE_PORT = 100;
    public static final int LAUNCHER_PORT = 1080;
    public static final int ASSISTANT_PORT = 1070;
    public static final int SETTING_PORT = 7070;
    public static final int AIR_CONDITIONER_PORT = 7080;
    public static final int VEHICLE_CONDITION_PORT = 7090;
    public static final int XTING_PORT = 110;
    public static final int CAR_CONTROL_PORT = 7100;
    public static final int MUSIC_PORT = 688;
    public static final int DUALSCREEN_PORT = 6060;

    public static final String SHOW_VOICE_ASSISTANT_DIALOG = "show_voice_assistant_dialog";
    public static final String DISMISS_VOICE_ASSISTANT_DIALOG = "dismiss_voice_assistant_dialog";
    public static final String FROM = "FROM";
    public static final String DATE = "DATE";
    public static final String ASSISTANT_MARK = "ASSISTANT_MARK"; //语音启动mark
    public static final String ASSISTANT_WAKE_FLOW = "ASSISTANT_WAKE_FLOW";
    public static final String IN_A_CALL = "IN_A_CALL"; //通话中
    public static final String INCOMING_CALL = "INCOMING_CALL"; //来电
    public static final String END_OF_CALL = "END_OF_CALL"; //通话结束
    public static final String IN_A_IBCALL = "IN_A_IBCALL"; //I-Call或B-Call通话中
    public static final String END_OF_IBCALL = "END_OF_IBCALL"; //I-Call或B-Call通话结束
    public static final String WHEEL_HANGUP_IBCALL = "WHEEL_HANGUP_IBCALL"; //方控按下挂断
    public static final String INCOMING_CALL_TTS_COMPLETED = "INCOMING_CALL_TTS_COMPLETED"; //来电播报完毕
    public static final String CLICK_BLUE_PHONE = "click_blue_phone"; //打开蓝牙电话的动作
    public static final String OPEN_NEGATIVE_SCREEN = "navibarwindow_open_action"; //打开负一屏

    public static final String BLUETOOTH_PHONE_DIED = "BLUETOOTH_PHONE_DIED"; //蓝牙电话挂瓢

    public static final String NAVIGATE_TO_GAS_STATION = "NAVIGATE_TO_GAS_STATION";
    public static final String NAVIGATE_TO_COFFEE_HOUSE = "NAVIGATE_TO_COFFEE_HOUSE";
    public static final String NAVIGATE_TO_RESTING_AREA = "NAVIGATE_TO_RESTING_AREA";
    public static final String NAVIGATE_TO_SERVICE_AREA = "NAVIGATE_TO_SERVICE_AREA";
    public static final String GAS_STATION = "加油站";
    public static final String COFFEE_HOUSE = "咖啡厅";
    public static final String RESTING_AREA = "休息区";
    public static final String SERVICE_AREA = "咖啡厅";

    //是否支持EOL测试
    public static final boolean SUPPORT_EOL = true;
    // 指令分发 port action
    //目前已经Xting端口使用情况 10 -> 桌面, 110 -> 语音助手, 2 -> EOL测试
    public static final int INSTRUCTION_DISTRIBUTE_PORT_XTING = 2;

    public static final int ACTION_GET_CURRENT_TUNER_STATE = 1;
    public static final int ACTION_SET_TUNER_FREQUENCY = 2;
    public static final int ACTION_SET_TUNER_BAND = 3;
    public static final int ACTION_SET_TUNER_FAVORITE = 4;
    public static final int ACTION_SET_TUNER_SEEK = 5;
    public static final int ACTION_SET_TUNER_AUTO_STORE = 6;

    public enum ChangeAcTempOperation {
        TURN_UP(1),
        TURN_DOWN(2),
        SPECIFIC_NUM(3),
        MAX(4),
        MIN(5);

        private int operation;

        ChangeAcTempOperation(int operation) {
            this.operation = operation;
        }

        public int getOperation() {
            return operation;
        }
    }

    public enum ChangeAcWindDirectionModelOperation {
        FACE_MODEL("吹脸模式"),
        FOOT_MODEL("吹脚模式"),
        FACE_FOOT_MODEL("吹脸吹脚"),
        DEFROST_FOOT("除霜吹脚");

        private String operation;

        ChangeAcWindDirectionModelOperation(String operation) {
            this.operation = operation;
        }

        public String getOperation() {
            return operation;
        }
    }

    public enum ChangeAcModelOperation {
        AUTO("自动"),
        MANUAL("手动"),
        REFRIGERATION("制冷"),
        DEHUMIDIFICATION("除湿"),
        HEAT("制热"),
        WIND("送风"),
        INNER_CYCLE("内循环"),
        OUT_CYCLE("外循环"),
        BACK_WINDOW_HEAT("后窗加热"),
        TO_FRONT_WINDOW("吹前窗"),
        AIR_OUTLET("出风口");

        private String operation;

        ChangeAcModelOperation(String operation) {
            this.operation = operation;
        }

        public String getOperation() {
            return operation;
        }
    }

    public enum ChangeAcWindSpeed {
        HIGH_SPEED(1),
        LOW_SPEED(2),
        INCREASE_SPEED(3),
        REDUCE_SPEED(4),
        MIN_SPEED(5),
        MEDIUM_SPEED(6),
        MAX_SPEED(7),
        AUTO_SPEED(8),
        SPECIFIC_SPEED(9);

        private int operation;

        ChangeAcWindSpeed(int operation) {
            this.operation = operation;
        }
    }

    public enum ChangeDefogModel {
        FRONT_DEFOG("前除霜"),
        BACK_DEFOG("后除霜"),
        DEFOG("除霜"),
        DEMIST("除雾");

        private String operation;

        ChangeDefogModel(String operation) {
            this.operation = operation;
        }

        public String getOperation() {
            return operation;
        }
    }

    public enum SwitchCamera {
        RIGHT_BACK(1),
        RIGHT(2),
        RIGHT_FRONT(3),
        FRONT(4),
        LEFT_FRONT(5),
        LEFT(6),
        LEFT_BACK(7),
        BACK(8);

        private int operate;

        SwitchCamera(int operate) {
            this.operate = operate;
        }
    }

    public enum SwitchCameraModel {
        TWO_D(1),
        THREE_D(2);

        private int operate;

        SwitchCameraModel(int operate) {
            this.operate = operate;
        }
    }

    public static class Assistant {
        public static final String RESULT = "result";
    }

    public static class AssistantKey {

    }

    public static class AssistantSmartHomeAction {
        public static final int SCENE_CONTROL_ACTION = 10;
        public static final int WHICH_DEVICES_ONLINE = 11;
        public static final int REFRESH_SCENE_LIST = 12;
        public static final int XIAOMI_SMARTHOME_MESSAGE = 13;
    }

    public static class AssistantSmartHomeBundleKey {
        public static final String SCENE_NAME = "scene_name";
        public static final String XIAOMI_SMARTHOME_MESSAGE = "xiaomi_smarthome_message";
        public static final String SCENE_NAME_CALLBACK = "scene_name_callback";
        public static final String SCENE_NAME_DEVICES_ONLINE = "which_devices_online";
        public static final String SCENE_NAME_DEVICES_ONLINE_CALLBACK = "which_devices_online_callback";
        public static final String SCENE_NAME_REFRESH_SCENE = "refresh_scene_list";
        public static final String SCENE_NAME_REFRESH_SCENE_CALLBACK = "refresh_scene_list_callback";
        public static final String XIAOMI_SMARTHOME_CALLBACK = "xiaomi_smarthome_callback";
    }

    public static class LauncherThirdAction {
        public static final int CREATE_SCHEDULE = 1;
        public static final int QUERY_SCHEDULE = 2;
        public static final int QUERY_NAVI_STATE = 3;
    }

    public static class LauncherThirdBundleKey {
        public static final String SCHEDULE_BEAN = "schedule_bean";
        public static final String SCHEDULE_BEANS = "schedule_beans";
        public static final String RESULT = "result";
    }

    public static class BluetoothPhoneThirdAction {
        public static final String PHONE_ACTION = "phone_action";
        public static final int DIAL = 1;
        public static final int SEND_DTMF = 2;
        public static final int HOLD_CALL = 3;
        public static final int ACCEPT_CALL = 4;
        public static final int REJECT_CALL = 5;
        public static final int TERMINAL_CALL = 6;
        public static final int GET_AUDIO_STATE = 7;
        public static final int CONNECT_AUDIO = 8;
        public static final int DISCONNECT_AUDIO = 9;
        public static final int GET_ALL_CONTACT = 10;
        public static final int DIAL_BACK = 11;
        public static final int REDIAL = 12;
        public static final int SYNCHRONIZE_CONTACT_BOOK = 13;
        public static final int IS_CONTACT_BOOK_SYNCHRONIZED = 14;
        public static final int GET_DIAL_BACK_NUMBER = 15;
        public static final int GET_REDIAL_NUMBER = 16;
        public static final int REGISTER_PHONE_STATE_CALLBACK = 17;
        public static final int GET_CALL_HISTORY = 18;
        public static final int IS_BLUETOOTH_CONNECTED = 19;
        public static final int CALL_HISTORY = 20;
    }

    public static class BluetoothPhoneThirdBundleKey {
        public static final String DIAL_NUM = "dial_num";
        public static final String DIAL_NUM_RESULT = "dial_num_result";
        public static final String SEND_DTMF = "send_DTMF";
        public static final String SEND_DTMF_RESULT = "send_DTMF_result";
        public static final String HOLD_CALL_RESULT = "hold_call_result";
        public static final String ACCEPT_CALL_RESULT = "accept_call_result";
        public static final String REJECT_CALL_RESULT = "reject_call_result";
        public static final String TERMINAL_CALL_RESULT = "terminal_call_result";
        public static final String GET_AUDIO_STATE_RESULT = "get_audio_state_result";
        public static final String CONNECT_AUDIO_RESULT = "connect_audio_result";
        public static final String DISCONNECT_AUDIO_RESULT = "disconnect_audio_result";
        public static final String GET_CALL_CONTACTS_RESULT = "get_all_contacts_result";
        public static final String DIAL_CALL_BACK_RESULT = "dial_call_back_result";
        public static final String REDIAL_RESULT = "redial_result";
        public static final String RESULT = "result";
        public static final String CONTACT_BEAN_LIST = "contact_bean_list";
        public static final String STATE_ARRAY = "state_array";
        public static final String GET_CALL_HISTORY = "get_call_history";
        public static final String BLUETOOTH_CONNECT_STATE = "bluetooth_connect_state";
    }

    public static class SettingThirdBundleKey {
        public static final String DISPLAY_MODEL = "display_model";
        public static final String BRIGHTNESS = "brightness";
        public static final String VOLUME = "volume";
        public static final String ADD_VOLUME = "add_volume";
        public static final String IS_CLOSE_SETTING = "is_close_setting";
        public static final String SELECT_FRAGMENT = "select_fragment";

        //屏幕
        public static final String KEY_DISPLAY_MODE = "display_mode";
        public static final String KEY_DISPLAY_LEVEL = "display_level";
        public static final String KEY_KEYBOARD_LEVEL = "keyboard_level";
        public static final String KEY_BAN_VIDEO_STATUS = "ban_video_status";
        //声音
        public static final String KEY_SOUND_VALUE = "key_sound_value";
        public static final String KEY_SOUND_EFFETCS_MODE = "key_sound_effetcs_mode";
        public static final String KEY_ARKAMYS3D_STATUS = "key_arkamys3D_status";
        public static final String KEY_SOUNDFIELD_MODE = "key_soundfield_mode";
        public static final String KEY_SOUNDFIELD_POSITION = "key_soundfield_position";
        public static final String KEY_ON_OFF_MUSIC_STATUS = "key_on_off_music_status";
        public static final String KEY_CAR_INFO_SOUND = "key_car_info_sound";
        public static final String KEY_CAR_SPEED_VOLUME = "key_car_speed_volume";
        public static final String KEY_PARK_MEDIA_VOLUME = "key_park_media_volume";
        public static final String KEY_INTERNET_TYPE = "key_internet_type";
        //车身
        public static final String KEY_FCW_AEB = "key_fcw_aeb";
        public static final String KEY_ISA = "key_isa";
        public static final String KEY_DAW = "key_daw";
        public static final String KEY_EPB = "key_epb";
        public static final String KEY_SEAT_BELT = "key_seat_belt";
        public static final String KEY_SPEED_LOCK_CONTROL = "key_speed_lock_control";
        public static final String KEY_LEAVE_LOCK = "key_leave_lock";
        public static final String KEY_SELF_CLOSING_WINDOW = "key_self_closing_window";
        public static final String KEY_TRUNK = "key_trunk";
        public static final String KEY_REARVIEW_MIRROR = "key_rearview_mirror";
        public static final String KEY_WELCOME_SEAT = "key_welcome_seat";
        public static final String KEY_LEAVE_LIGHT = "key_leave_light";
        public static final String KEY_WELCOME_LIGHT = "key_welcome_light";
        public static final String KEY_IHC = "key_ihc";
        public static final String KEY_AMBIENT_LIGHT = "key_ambient_light";
        public static final String KEY_SCENE_LIGHT = "key_scene_light";
        public static final String KEY_GET_ID_VIN_INFO = "key_id_vin_info";
        public static final String KEY_GET_DRIVE_DISTANCE = "key_drive_distance";
    }

    public static class VehicleConditionThirdBundleKey {
        public static final String TOTAL_MILEAGE_RESULT = "total_mileage_result";
        public static final String MEAN_FUEL_CONSUMPTION_RESULT = "mean_fuel_consumption_result";
        public static final String REMAINING_MILEAGE_RESULT = "remaining_mileage_result";
        public static final String TIRE_PRESSURE = "tire_pressure";
        public static final String TIRE_PRESSURE_RESULT = "tire_pressure_result";
        public static final String ENGINE_STATE = "engine_state";
        public static final String ENGINE_STATE_RESULT = "engine_state_result";
        public static final String AC_STATE = "ac_state";
        public static final String AC_STATE_RESULT = "ac_state_result";
        public static final String WATER_TEMPERATURE = "water_temperature";
        public static final String WATER_TEMPERATURE_RESULT = "water_temperature_result";
        public static final String SKID_STATE = "skid_state";
        public static final String SKID_STATE_RESULT = "skid_state_result";
        public static final String BRAKE_STATE = "brake_state";
        public static final String BRAKE_STATE_RESULT = "brake_state_result";
        public static final String OVER_ALL = "over_all";
        public static final String OVER_ALL_RESULT = "over_all_result";
    }

    public static class AirConditionerThirdBundleKey {
        public static final String IS_TURN_ON_AC = "is_turn_on_ac";
        public static final String AC_TURN_ON_OR_OFF_RESULT = "ac_turn_on_or_off_result";
        public static final String AC_OPERATION = "ac_operation";
        public static final String AC_SPECIFIC_TEMPERATURE = "ac_specific_temperature";
        public static final String AC_TEMPERATURE_CHANGE_RESULT = "ac_temperature_change_result";
        public static final String AC_TEMPERATURE_CHANGE_RESULT_TEXT = "ac_temperature_change_result_text";
        public static final String AC_WIND_DIRECTION_MODEL_CHANGE = "ac_wind_direction_model_change";
        public static final String AC_WIND_DIRECTION_MODEL_CHANGE_RESULT = "ac_wind_direction_model_change_result";
        public static final String AC_MODEL_CHANGE = "ac_model_change";
        public static final String AC_MODEL_IS_OPEN = "ac_model_is_open";
        public static final String AC_MODEL_CHANGE_RESULT = "ac_model_change_result";
        public static final String AC_DEFOG_MODEL_CHANGE = "ac_defog_model_change";
        public static final String AC_DEFOG_MODEL_CHANGE_RESULT = "ac_defog_model_change_result";
        public static final String AC_DEFOG_MODEL_IS_OPEN = "ac_defog_model_is_open";
        public static final String AC_WIND_SPEED_CHANGE = "ac_wind_speed_change";
        public static final String AC_WIND_SPEED_VALUE = "ac_wind_speed_value";
        public static final String AC_WIND_SPEED_CHANGE_RESULT = "ac_wind_speed_change_result";
        public static final String AC_WIND_SPEED_CHANGE_RESULT_TEXT = "ac_wind_speed_change_result_text";
    }

    public static class AirConditionThirdAction {
        public static final int AIR_CONDITIONER_STATE = 1;
        public static final int AIR_CONDITION_OPERATION = 2;
        public static final int AIR_CONDITIONER_WIND_DIRECTION_MODEL_CHANGE = 3;
        public static final int AIR_CONDITIONER_MODEL_CHANGE = 4;
        public static final int AIR_CONDITIONER_DEFOG_MODEL_CHANGE = 5;
        public static final int AIR_CONDITIONER_WIND_SPEED_CHAGE = 6;
    }

    public static class SettingThirdAction {
        public static final int CLOSE_SETTING = 400;
        public static final int SELECT_SPECIFIC_FRAGMENT = 500;

        //屏幕
        public static final int GET_DISPLAY_MODE = 0;
        public static final int SET_DISPLAY_MODE = 1;
        public static final int GET_DISPLAY_LEVEL = 2;
        public static final int SET_DISPLAY_LEVEL = 3;
        public static final int GET_KEYBOARD_LEVEL = 4;
        public static final int SET_KEYBOARD_LEVEL = 5;
        public static final int GET_BAN_VIDEO_STATUS = 6;
        public static final int SET_BAN_VIDEO_STATUS = 7;
        public static final int CLOSE_SCREEN = 8;
        //声音
        public static final int GET_SOUND_VALUE = 9;
        public static final int SET_SOUND_VALUE = 10;
        public static final int GET_SOUND_EFFETCS_MODE = 11;
        public static final int SET_SOUND_EFFETCS_MODE = 12;
        public static final int SET_ARKAMYS3D_STATUS = 13;
        public static final int GET_ARKAMYS3D_STATUS = 14;
        public static final int SET_SOUNDFIELD_MODE_OR_POSITION = 15;
        public static final int GET_SOUNDFIELD_MODE = 16;
        public static final int GET_SOUNDFIELD_POSITION = 17;
        public static final int SET_ON_OFF_MUSIC_STATUS = 18;
        public static final int GET_ON_OFF_MUSIC_STATUS = 19;
        public static final int SET_CAR_INFO_SOUND = 20;
        public static final int GET_CAR_INFO_SOUND = 21;
        public static final int SET_CAR_SPEED_VOLUME = 22;
        public static final int GET_CAR_SPEED_VOLUME = 23;
        public static final int SET_PARK_MEDIA_VOLUME = 24;
        public static final int GET_PARK_MEDIA_VOLUME = 25;
        //连接
        public static final int GET_WIFY_CONNECTION = 26;
        public static final int CONNECT_WIFY = 27;
        public static final int OPEN_WIFY = 28;
        public static final int CLOSE_WIFY = 29;
        public static final int GET_BLUETOOTH_CONNECTION = 30;
        public static final int CONNECT_BLUETOOTH = 31;
        public static final int OPEN_BLUETOOTH = 32;
        public static final int CLOSE_BLUETOOTH = 33;
        public static final int GET_HOTSPOT = 34;
        public static final int CONNECT_HOTSPOT = 35;
        public static final int OPEN_HOTSPOT = 36;
        public static final int CLOSE_HOTSPOT = 37;
        public static final int SET_INTERNET_TYPE = 38;
        //车身
        public static final int SET_FCW_AEB_STATE = 39;
        public static final int SET_ISA_STATE = 40;
        public static final int SET_DAW_STATE = 41;
        public static final int SET_EPB_STATE = 42;
        public static final int SET_SEAT_BELT_STATE = 43;
        public static final int SET_SPEED_LOCK_CONTROL = 44;
        public static final int SET_LEAVE_LOCK_STATE = 45;
        public static final int SET_SELF_CLOSING_WINDOW_STATE = 46;
        public static final int SET_TRUNK_STATE = 47;
        public static final int SET_REARVIEW_MIRROR_STATE = 48;
        public static final int SET_WELCOME_SEAT_STATE = 49;
        public static final int SET_LEAVE_LIGHT_STATE = 50;
        public static final int SET_WELCOME_LIGHT_STATE = 51;
        public static final int SET_IHC_STATE = 52;
        public static final int SET_AMBIENT_LIGHT_STATE = 53;
        public static final int SET_SCENE_LIGHT_STATE = 54;
        public static final int GET_ID_VIN_INFO = 55;
        public static final int GET_DRIVE_DISTANCE = 56;
    }

    public static class VehicleThirdAction {
        public static final int TOTAL_MILEAGE = 1;
        public static final int MEAN_FUEL_CONSUMPTION = 2;
        public static final int REMAINING_MILEAGE = 3;
        public static final int TIRE_PRESSURE = 4;
        public static final int ENGINE_STATE = 5;
        public static final int AC_STATE = 6;
        public static final int WATER_TEMPERTURE = 7;
        public static final int BRAKE_STATE = 7;
        public static final int SKID_STATE = 8;
        public static final int OVER_ALL = 9;
    }

    public static class XtingThirdAction {
        public static final int PLAY_RADIO_STATION_COLLECTION = 1;
        public static final int COLLECT_RADIO_STATION = 2;
        public static final int CANCEL_COLLECT_RADIO_STATION = 3;
        public static final int PRE_RADIO_STATION = 4;
        public static final int NEXT_RADIO_STATION = 5;
        public static final int PAUSE_RADIO_STATION = 6;
        public static final int PLAY_RADIO_STATION = 7;
        public static final int SCAN_RADIO_STATION = 8;
        public static final int GET_RADIO_STATION_INFO = 10;
        public static final int PLAY_LCOAL_RADIO_STATION = 11;
        public static final int PLAY_ONLINE_RADIO_STATION = 12;
        public static final int PLAY_RADIO_STATION_BY_FREQUENCY = 13;
        public static final int PLAY_AM = 16;
        public static final int PLAY_FM = 17;
        public static final int GET_HAS_STATIONS = 35;

        //网络电台：
        public static final int PLAY_RADIO_STATION_BY_NAME = 14;
        public static final int PLAY_RADIO_STATION_BY_TYPE = 15;
        public static final int PLAY_PROGRAM_COLLECTION = 18;
        public static final int COLLECT_PROGRAM = 19;
        public static final int CANCEL_COLLECT_PROGRAM = 20;
        public static final int PRE_PROGRAM = 21;
        public static final int NEXT_PROGRAM = 22;
        public static final int PAUSE_PROGRAM = 23;
        public static final int CONTINUE_PLAY_PROGRAM = 24;
        public static final int SWITCH_PLAY_MODE_TO_ORDER = 26;
        public static final int SWITCH_PLAY_MODE_TO_RANDOM = 27;
        public static final int SWITCH_PLAY_MODE_TO_CYCLE = 28;
        public static final int SWITCH_PLAY_MODE_TO_LOOP = 29;
        public static final int PLAY_PROGRAM = 30;
        public static final int PLAY_ALBUM = 31;
        public static final int SET_SOUND_VALUE = 32;
        public static final int PLAY_PROGRAM_BY_ID = 33;
        public static final int PLAY_RADIO_BY_ID = 34;
        public static final int GET_PLAY_STATE = 35; //用于语音助手获取电台当前播放状态
    }

    public static class XtingThirdBundleKey {
        public static final String RESULT = "result";
        public static final String PROGRAM_NAME = "program_name";
        public static final String RADIO_STATION_NAME = "radio_station_name";
        public static final String RADIO_STATION_TYPE = "radio_station_type";
        public static final String RADIO_STATION_FREQUENCY = "radio_frequency";
        public static final String AM_OR_FM = "am_or_fm";
        public static final String KEY_SOUND_VALUE = "key_sound_value";
        public static final String ADD_VOLUME = "add_volume";
        public static final String ALBUM_ID = "album_id";
        public static final String PROGRAM_ID = "program_id";
        public static final String PROGRAM_INDEX = "program_index";
        public static final String RADIO_ID = "radio_id";
        public static final String RADIO_INDEX = "radio_index";
        public static final String RADIO_LOCAL_TTS = "radio_local_tts";
    }

    public static class CarControlThirdAction {
        public static final int SKY_WINDOW_CONTROL = 1;
        public static final int CAR_WINDOW_CONTROL = 2;
        public static final int SUN_SHADOW_CONTROL = 3;
        public static final int AMBIENT_LIGHT_CONTROL = 4;
        public static final int TRUNK_CONTROL = 5;
        public static final int REVER_IMAGE_CONTROL = 6;
        public static final int WIPER_CONTROL = 7;
        public static final int GALLERY_LIGHT_CONTROL = 8;
        public static final int PANORANIC_360_CONTROL = 9;
        public static final int SWITCH_CAMERAL_CONTROL = 10;
        public static final int SWITCH_CAMERA_MODEL_CONTROL = 11;
    }

    public static class CarControlThirdBundleKey {
        public static final String IS_OPEN_SKY_WINDOW = "is_open_sky_window";
        public static final String IS_OPEN_SKY_WINDOW__RESULT = "is_open_sky_window_result";
        public static final String IS_OPEN_CAR_WINDOW = "is_open_car_window";
        public static final String IS_OPEN_CAR_WINDOW_RESULT = "is_open_car_window_result";
        public static final String IS_OPEN_SUN_SHADOW = "is_open_car_shadow";
        public static final String IS_OPEN_SUN_SHADOW_RESULT = "is_open_car_shadow_result";
        public static final String IS_OPEN_AMBIENT_LIGHT = "is_open_ambient_light";
        public static final String IS_OPEN_AMBIENT_LIGHT_RESULT = "is_open_ambient_light_result";
        public static final String IS_OPEN_TRUNK = "is_open_trunk";
        public static final String IS_OPEN_TRUNK_RESULT = "is_open_trunk_result";
        public static final String IS_OPEN_REVERSE_IMAGE = "is_open_reverse_image";
        public static final String IS_OPEN_REVERSE_IMAGE_RESULT = "is_open_reverse_image_result";
        public static final String IS_OPEN_WIPER = "is_open_wiper";
        public static final String IS_OPEN_WIPER_RESULT = "is_open_wiper_result";
        public static final String IS_OPEN_GALLERY_LIGHT = "is_open_galley_light";
        public static final String IS_OPEN_GALLERY_LIGHT_RESULT = "is_open_galley_light_RESULT";
        public static final String IS_OPEN_PANORAMIC_360 = "is_open_panoramic_360";
        public static final String IS_OPEN_PANORAMIC_360_RESULT = "is_open_panoramic_360_RESULT";
        public static final String CAMERA_SWITCH = "camera_switch";
        public static final String CAMERA_SWITCH_RESULT = "camera_switch_result";
        public static final String CAMERA_MODEL_SWITCH = "camera_model_switch";
        public static final String CAMERA_MODEL_SWITCH_RESULT = "camera_model_switch_result";

    }

    public interface Xting3DAction {
        int ACTION_TO_CATEGORY_OR_PLAY_LIST = 14;  //打开节目分类
        int ACTION_TO_COLLECT = 40; //打开收藏界面
        int ACTION_OPEN_LISTEN_TO_RECOGNIZE = 38; //打开听歌识曲
    }

    public interface XKan3DAction {
        int OPEN_MEDIA_FRAGMENT = 26;
        int NEXT_IMG = 16;
        int PRE_IMG = 16;
        int ZOOM_IMG = 31;
        int REDUCE_IMG = 26;
        int ROTATE_IMG = 26;
    }

    public class HotelConstants {
        public static final String HOTEL_ID_TAG = "HOTEL_ID_TAG";
        public static final String HOTEL_NAME_TAG = "HOTEL_NAME_TAG";
        public static final String HOTEL_ADDRESS_TAG = "HOTEL_ADDRESS_TAG";
        public static final String HOTEL_LAT_TAG = "HOTEL_LAT_TAG";
        public static final String HOTEL_LON_TAG = "HOTEL_LON_TAG";
        public static final String HOTEL_PHONE_TAG = "HOTEL_PHONE_TAG";
        public static final String HOTEL_ICON_URL_TAG = "HOTEL_ICON_URL_TAG";
    }

    public class QueryCollectConstants {
        public static final String REQUEST_COLLECT_DATA = "com.xiaoma.request_collect_data";
        public static final String RESPOND_COLLECT_DATA = "com.xiaoma.respond_collect_data";
        public static final String QUERY_TYPE = "QUERY_TYPE";
        public static final String QUERY_TYPE_HOME = "QUERY_TYPE_HOME";
        public static final String QUERY_TYPE_COMPANY = "QUERY_TYPE_COMPANY";
        public static final String RESPOND_LONGITUDE = "RESPOND_LONGITUDE";
        public static final String RESPOND_LATITUDE = "RESPOND_LATITUDE";
        public static final String RESPOND_ADDRESS = "RESPOND_ADDRESS";
    }

    //    EOL测试
    public interface EOLContract {

        interface Action {
            int ENV_TEST = 1;
            int LVDS_SHOW = 2;
            int LVDS_HIDE = 3;
            int LVDS_STATE = 4;
        }

        interface Key {
            String EXTRA = "extra";
        }
    }

    public interface IPCPort {

        interface DualScreen {
            String PKG = "com.xiaoma.dualscreen";
            int EOL = 1;
        }
    }
}
