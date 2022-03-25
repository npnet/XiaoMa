package com.xiaoma.assistant.constants;

/**
 * Created by ZYao.
 * Date ：2019/5/18 0018
 */
public class AssistantConstants {

    public static class RobActionKey {
        public static final int ROB_ACTION_NAVI_LOCATION = 15;
        public static final int ROB_ACTION_NAVI_SEARCH = 16;
        public static final int ROB_ACTION_NAVI = 18;
        public static final int ROB_ACTION_CANCEL_INFO = 19;
        public static final int ROB_ACTION_SELECT_LINE = 22;
        public static final int ROB_ACTION_STOP_NAVI = 23;
        public static final int ROB_ACTION_OPEN_APP = 31;
        public static final int ROB_ACTION_CLOSE_APP = 36;
        public static final int ROB_ACTION_CHANGE_CLOTHER = 3;
        public static final int ROB_ACTION_COLLECT_POI = 39;
        public static final int ROB_ACTION_NAVI_SET_HOME = 43;
        public static final int ROB_ACTION_VOLUME_SETTING = 34;
        public static final int ROB_ACTION_NIGHT_MODE = 48;
        public static final int ROB_ACTION_OPEN_WIFI = 35;
        public static final int ROB_ACTION_OPEN_BLUETOOTH = 35;
        public static final int ROB_ACTION_OPEN_WIFI_SETTING = 35;
        public static final int ROB_ACTION_ERROR_SPEAK = 43;
        public static final int ROB_ACTION_CLOSE_ASSISTANT = 36;

        public static final int ROB_ACTION_OPEN_NETSYN = 35;
        public static final int ROB_ACTION_OPEN_RMAFOLDING = 35;
        public static final int ROB_ACTION_OPEN_BRLBW = 35;
        public static final int ROB_ACTION_OPEN_COLWARNING = 35;
        public static final int ROB_ACTION_AIR_CONDITIONER_STATE = 35;
        public static final int ROB_ACTION_CLOSE_WIFI = 36;
        public static final int ROB_ACTION_CLOSE_BLUETOOTH = 36;
        public static final int ROB_ACTION_CLOSE_NETSYN = 36;
        public static final int ROB_ACTION_CLOSE_RMAFOLDING = 36;
        public static final int ROB_ACTION_CLOSE_BRLBW = 36;
        public static final int ROB_ACTION_CLOSE_COLWARNING = 36;
        public static final int ROB_ACTION_CARMESSAGE_VOLUME_SETTING = 29;
        public static final int ROB_ACTION_SYSTEM_SETTINGS = 29;
        public static final int ROB_ACTION_CLOSE_WINDOWS = 36;
        public static final int CHECK_HELP = 50;
        public static final int CALL = 25;
        public static final int IB_CALL = 9;
        public static final int HANG_UP = 36;
        public static final int CHECK_RECORDS = 24;
        public static final int SWITCH_RADIO_STATION = 29;
        public static final int PLAY_RADIO_STATION = 14;
        public static final int OPEN_SYSTEM_SETTING = 14;
        public static final int COLLECTION = 40;
        public static final int COLLECTION_PROGRAM = 47;
        public static final int PLAY_SEARCH_MUSIC = 47;
        public static final int PLAY_CONTROL = 16;
        public static final int PLAY_CONTROL_RANDOM1 = 14;
        public static final int PLAY_CONTROL_RANDOM2 = 16;
        public static final int PLAY_CONTROL_RANDOM3 = 27;
        public static final int ROB_ACTION_ANSWER_RANDOM1 = 10;
        public static final int ROB_ACTION_ANSWER_RANDOM2= 13;
        public static final int ROB_ACTION_ANSWER_RANDOM3 = 17;
        public static final int ROB_ACTION_WEATHER_SUN = 11;
        public static final int ROB_ACTION_WEATHER_CLOUD = 41;
        public static final int ROB_ACTION_WEATHER_RAIN = 42;
        public static final int ROB_ACTION_WEATHER_WIND = 45;
        public static final int PLAY_MUSIC = 38;
        public static final int PLAY_MUSIC_SOURCE = 38;
        public static final int OPEN_CALENDAR = 14;
        public static final int MUSIC_PLAY_CONTROL = 26;
        public static final int QUERY_FLOW = 8;
        public static final int DRIVING_INFORMATION = 8;
        public static final int OPEN_HOLOGRAPHIC_IMAGE = 1;
        public static final int CLOSE_HOLOGRAPHIC_IMAGE = 2;
        public static final int OPNE_TAKE_PHOTO = 12;
        public static final int SWITCH_SYSTEM_LANGUAGE = 29;
        public static final int CAR_CONTROL_OPEN = 35;
        public static final int CAR_CONTROL_CLOSE = 36;
        public static final int AIR_CONDITIONING_AUTOMATIC_MODE = 32;
        public static final int AIR_TEMPERATURE_RELATED_SETTING = 34;
        public static final int AIR_OPEN_RELATED_SETTING = 35;
        public static final int AIR_CLOSE_RELATED_SETTING = 36;
        public static final int AIR_CONTROL_RELATED_SETTING = 32;
        public static final int AIR_WIND_SPEED_RELATED_SETTING = 33;
    }

    public static class NaviShowState {
        public static final int VIEW_TRANS_2D_NORTH_UP = 0;
        public static final int VIEW_TRANS_2D_HEAD_UP = 1;
        public static final int VIEW_TRANS_3D_HEAD_UP = 2;
        public static final int VIEW_TRANS_AR = 3;
    }

    public static class RoutAvoidType {
        public static final int AVOID_ROUND = 1;
        public static final int FASTEST = 2;
        public static final int CHEAPEST = 3;
        public static final int NOT_HIGH_FIRST = 4;
        public static final int HIGH_FIRST = 5;
    }

    public static class SmartHomeType {
        public static final int LEAVE_HOME = 11;
        public static final int COME_BACK_HOME = 8;
        public static final int REFRESH_LIST = 16;
        public static final int MY_DEVICE = 16;
        public static final int SCENE_SWITCHING = 31;
        public static final int XIAOMI_SMARTHOME = 31;
    }

    public static class FlowType {
        public static final int QUERY_FLOW = 8;
        public static final int BUY_FLOW = 14;
    }

    public static class WakeUpMethod {
        public static final String HARDWARE_METHOD = "hardwareMethod";
        public static final String CLICK_METHOD = "clickMethod";
        public static final String SPEECH_METHOD = "wakeupMethod";
    }

    public static final int LINKER_PORT = 888;
    public static final String CLOTH_SWITCH = "cloth switch";
    public static final String ASSIATANT_JUP_SHOP_ACTION = "com.xiaoma.shop.assistan.jump";
    public static final String ASSIATANT_JUP_SHOP_URI = "xiaoma://com.xiaoma.shop/main_shop?ASSISTANT_WAKE_FLOW=ASSISTANT_WAKE_FLOW";

    public static class PHONE_TYPE {
        public static final int PBAP_NUMBER_TYPE_NULL = 0;
        public static final int PBAP_NUMBER_TYPE_PREF = 1;
        public static final int PBAP_NUMBER_TYPE_WORK = 2;
        public static final int PBAP_NUMBER_TYPE_HOME = 3;
        public static final int PBAP_NUMBER_TYPE_VOICE = 4;
        public static final int PBAP_NUMBER_TYPE_FAX = 5;
        public static final int PBAP_NUMBER_TYPE_MSG = 6;
        public static final int PBAP_NUMBER_TYPE_CELL = 7;
        public static final int PBAP_NUMBER_TYPE_PAGER = 8;
    }

}
