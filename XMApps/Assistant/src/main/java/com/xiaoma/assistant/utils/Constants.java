package com.xiaoma.assistant.utils;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/18
 * Desc:
 */
public class Constants {

    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_TYPE = "type";

    public static final int MUSIC_TYPE_ONLINE = 0;
    public static final int MUSIC_TYPE_LOCAL = 1;
    public final static int QUERY_VIOLATION_ACTION = 3;

    public final static String SERVICE_TRAFFIC_LIMIT_NOT_FOUND = "traffic_limit_not_found";
    public static final String CLOSE_APP = "close_app_";
    public static final String PACKAGE_NAME = "package_name";
    public static final String ASSISTANT_GUIDE_VIEW_SHOW = "assistant_guide_view_show";

    public static class ScenarioFactoryType {
        public static final String SOS = "SOS";
        public static final String NAVI = "NAVI";
        public static final String LOCATION = "LOCATION";
        public static final String MUSIC = "MUSIC";
        public static final String WEATHER = "WEATHER";
        public static final String ANSWER = "ANSWER";
        public static final String RADIO = "RADIO";
        public static final String PHONE = "PHONE";
        public static final String WEB = "WEB";
        public static final String WEBSEARCH = "WEBSEARCH";
        public static final String LIMIT = "LIMIT";
        public static final String INSTRUCTION = "INSTRUCTION";
        public static final String TRANSLATION = "TRANSLATION";
        public static final String TIME = "TIME";
        public static final String AIR = "AIR";
        public static final String VIOLATION = "VIOLATION";
        public static final String TRAFFIC = "TRAFFIC";
        public static final String GAS = "GAS";
        public static final String STOCK = "STOCK";
        public static final String TRAIN = "TRAIN";
        public static final String FLIGHT = "FLIGHT";
        public static final String CAR = "CAR";
        public static final String GROUP = "GROUP";
        public static final String CONSTELLATION = "CONSTELLATION";
        public static final String RESTAURANT = "RESTAURANT";
        public static final String VEHICLE = "VEHICLE";
        public static final String CAR_CONTROL = "CAR_CONTROL";
        public static final String APP_CONTROL = "APP_CONTROL";
        public static final String HISTORY_TODAY = "HISTORY_TODAY";
        public static final String VIDEO = "VIDEO";
        public static final String SMARTHOME = "SMARTHOME";
        public static final String ALLSMARTHOME = "ALL_SMARTHOME";
        public static final String FLOW = "FLOW";
        public static final String TRAVEL = "TRAVEL";
        public static final String SCHEDULE = "SCHEDULE";
        public static final String FM = "FM";
        public static final String IMAGE = "IMAGE";
        public static final String QUERY = "QUERY";
        public static final String ERROR = "ERROR";
        public static final String PARKING = "PARKING";
        public static final String WAKEUP = "WAKEUP";
        public static final String CARFAULT = "CARFAULT";
        public static final String NEWS = "NEWS";
        public static final String MESSAGE = "MESSAGE";
        public static final String PROGRAM = "PROGRAM";
        public static final String CINEMA = "CINEMA";
        public static final String HOTEL = "HOTEL";
        public static final String HELPER = "HELPER";
        public static final String QUERY_NAME = "QUERY_NAME";
    }


    public static class ThirdPartyNotify {
        public final static String VOICE_CALL_CMD_ID = "VOICE_CALL_CMD_ID";
        public final static String FM_CMD_ID = "FM_CMD_ID";
    }


    public static final class BDSemticConstants {
        public static final int PAGE_ITEM_TYPE_NAVI = 0;//??????
        public static final int PAGE_ITEM_TYPE_SETUP_HOME_ADDRESS = 1;//???????????????
        public static final int PAGE_ITEM_TYPE_SETUP_COMPANY_ADDRESS = 2;//??????????????????
        public static final int PAGE_ITEM_TYPE_PSSING_POINT_SEARCH = 3;//????????????

        //??????????????????
        public static final int ERRNO = 0;
        //????????????
        public static final String TRAVEL = "travel";
        public static final String FIND = "find";
        //????????????
        public static final String RESTAURANT = "restaurant";
        //TTS
        public static final String TALK = "talk_service";
        public static final String TIME = "time";
        public static final String SYSPROFILE = "sysprofile";  //???????????????
        public static final String BAIKE = "baike";
        public static final String KG = "kg";
        //??????
        public static final String JOKE = "audio.joke.play";
        public static final String JOKE_SEARCH = "audio.joke";
        //??????
        public static final String TRAFFIC = "traffic_limit";
        public static final String UNIVERSAL_SEARCH = "universal_search";
        //??????
        public static final String FLIGHT = "planeticket";
        public static final String FLIGHT_TICKET = "iov.flight.buyticket";
        //?????????
        public static final String TRAIN = "train_ticket";
        //??????
        public static final String WEATHER = "duer_weather";
        public static final String WEATHER_QUERY = "sys_weather";
        //??????
        public static final String STOCK_CN = "shanghai_shenzhen_stock";
        public static final String STOCK_CN_SEARCH = "shanghai_shenzhen_stock_search";
        public static final String STOCK_US = "us_stock";
        public static final String STOCK_US_SEARCH = "us_stock_search";
        public static final String STOCK_HK = "hongkong_stock";
        public static final String STOCK_HK_SEARCH = "hongkong_stock_search";
        public static final String STOCK_INDEX = "stock";
        public static final String STOCK_INDEX_SEARCH = "stock_search";
        //??????
        public static final String DOMAIN = "map";
        public static final String NEARBY = "nearby";
        public static final String ROUTE = "route";
        public static final String POI = "poi";
        public static final String DOMAIN_NAVIGATE_INSTRUCTION = "navigate_instruction";
        //????????????
        public static final String CARD_TYPE_PASSING_POINT = "passing_point";
        public static final String INTENT_SEARCH = "search";
        //??????
        public static final String POSITION = "location";
        //??????
        public static final String MUSIC = "music";
        public static final String PLAY = "play";
        //??????
        public static final String IMAGE_SEARCH = "image_search";
        //??????
        public static final String FILM_SEARCH = "film_ticket_buyer";
        //?????????
        public static final String PARKING = "parking";
        public static final String PARKING_SEARCH = "parking.search_info";
    }


    public static class ParserConstant {

        /**
         * ???????????????
         */
        public static final String NAVI_SET_HOME_ADDRESS = "SET_HOME";

        /**
         * ??????????????????
         */
        public static final String NAVI_SET_COMPANY_ADDRESS = "SET_COMPANY";

    }


    public static class MultiplePageDetailType {
        public static final int TRAIN_DEATAIL = 1;
        public static final int IMAGE_DEATAIL = 2;
    }

    public static class BundleKey {
        public static final String LIST = "LIST";
        public static final String POSITION = "POSITION";
    }


    public static class ParseKey {
        public static final String SETTING = "??????";
        public static final String WIFI = "wifi";
        public static final String MARK = "??????";
        public static final String WIFI_SETTING = "wifi??????";
        public static final String OPEN_ROBOTMODE = "??????????????????";
        public static final String DATA_MANAGER = "????????????";
        public static final String RADIO = "?????????";
        public static final String VOICE_ASSISTANT = "????????????";
        public static final String SMART_HOME = "????????????";
        public static final String CAR_HOME_NET = "????????????";
        public static final String CALENDAR = "??????";
        public static final String OFF_ROBOTMODE = "??????????????????";
        public static final String BOARDING_LIGHTING = "????????????";
        public static final String OUT_OF_CAR_LIGHTING = "????????????";
        public static final String BLUETOOTH_SETTINGS = "????????????";
        public static final String BLUETOOTH = "??????";
        public static final String RECOGNIZE_SONG = "????????????";
        public static final String VR_PRACTICE = "????????????";
        public static final String ELECTRONIC_DOG = "?????????";
        public static final String ELECTRONIC_EYE = "?????????";
        public static final String HOT_SPOT = "??????";
        public static final String SHARED_HOT_SPOT = "????????????";
        public static final String WIFI_HOT_SPOT = "wifi??????";
        public static final String NET_HOT_SPOT = "????????????";
        public static final String NET_HOT = "????????????";
        public static final String HIGH_WIND = "??????";
        public static final String MEDIUM_WIND = "??????";
        public static final String LOW_WIND = "??????";
        public static final String FACE = "???";
        public static final String HEAD = "???";
        public static final String FOOT = "???";
        public static final String FACE_FOOT = "????????????";
        public static final String AUTO = "??????";
        public static final String DEFOGGING = "?????????";
        public static final String MANUAL = "??????";
        public static final String REFRIGERATION = "??????";
        public static final String DEHUMIDIFICATION = "??????";
        public static final String HEATING = "??????";
        public static final String AIR_SUPPLY = "??????";
        public static final String INNER_LOOP = "?????????";
        public static final String OUTSIDE_LOOP = "?????????";
        public static final String REAR_WINDOW_HEATING = "????????????";
        public static final String FRONT_WINDOW = "?????????";
        public static final String AIR_OUTLET = "?????????";
        public static final String COMPRESSOR = "?????????";
        public static final String LOCAL_STATION = "????????????";
        public static final String LOCAL_VIDEO = "????????????";
        public static final String LOCAL_PICTURE = "????????????";
        public static final String USB_PICTURE = "usb??????";
        public static final String USB_MUSIC = "usb??????";
        public static final String BLUETOOTH_MUSIC = "????????????";

        public static final String FRONT_DEFROG = "?????????";
        public static final String BACK_DEFROG = "?????????";
        public static final String DEFOG = "??????";
        public static final String DEMIST = "??????";

        public static final String MAIN_LOCK = "????????????";
        public static final String LOCK = "???";
        public static final String ALL_LOCK = "????????????";
        public static final String SKYLIGHT = "??????";
        public static final String SKYLIGHT_START = "??????";
        public static final String SKYLIGHT_PERK = "????????????";
        public static final String OPEN_FRONT_WINDOW = "?????????";
        public static final String ALL_WINDOW = "????????????";
        public static final String REARVIEW_MIRROR_HEATING = "???????????????";
        public static final String WINDOW = "??????";
        public static final String SUNSHADE = "?????????";
        public static final String AMBIENT_LIGHT = "?????????";
        public static final String INSIDE_LIGHT = "?????????";
        public static final String CEILING_LIGHT = "?????????";
        public static final String READ_LIGHT = "?????????";
        public static final String TRUNK = "?????????";
        public static final String BACKDOOR = "?????????";
        public static final String LOW_BEAM_LIGHTS = "?????????";
        public static final String HIGH_BEAM = "?????????";
        public static final String REVERSE_IMAGE = "????????????";
        public static final String WIPER = "?????????";
        public static final String FRONT_WIPER = "????????????";
        public static final String FOG_LIGHT = "??????";
        public static final String GALLERY_LIGHT = "?????????";
        public static final String WARNING_LIGHT = "?????????";
        public static final String AUTOMATIC_PARKING = "????????????";
        public static final String CRUISE = "??????";
        public static final String PANORAMIC_360 = "360";
        public static final String SEAT = "??????";
        public static final String MAIN_SEAT = "????????????";
        public static final String FRONT_SEAT = "????????????";
        public static final String LEFT_FRONT_WINDOW = "????????????";
        public static final String LEFT_BACK_WINDOW = "????????????";
        public static final String RIGHT_FRONT_WINDOW = "????????????";
        public static final String RIGHT_BACK_WINDOW = "????????????";

        public static final String SMOKING_MODE = "????????????";

        public static final String RIGHT_BACK = "??????";
        public static final String RIGHT = "???";
        public static final String RIGHT_FRONT = "??????";
        public static final String BEFORE = "???";
        public static final String LEFT_FRONT = "??????";
        public static final String LEFT = "???";
        public static final String LEFT_REAR = "??????";
        public static final String REAR = "???";


        public static final String OIL_QUANTITY = "??????";
        public static final String TIRE_PRESSURE = "??????";
        public static final String ENGINE = "?????????";
        public static final String AIR_CONDITIONING = "??????";
        public static final String WATER_TEMPERATURE = "??????";
        public static final String SKID = "??????";
        public static final String BRAKE = "??????";
        public static final String OVERALL = "??????";
        public static final String FUEL_CONSUMPTION = "??????";
        public static final String MILEAGE = "??????";
        public static final String REMAINING_MILEAGE = "????????????";
        public static final String DISPLAY_MODEL = "????????????";

        public static final String BLUETOOTH_PHONE = "????????????";
        public static final String NAVI = "??????";
        public static final String ICALL = "????????????";
        public static final String BCALL = "????????????";


    }

    public static class SystemSetting {
        public static final String PACKAGE_NAME_SETTING = "com.xiaoma.setting";
        public static final int DISPLAY_MODEL_CHANGE = 0;
        public static final int DISPLAY_MODEL_DAYLIGHT = 1;
        public static final int DISPLAY_MODEL_NIGHT = 2;

        public static final int BRIGHTNESS_UP = 1;
        public static final int BRIGHTNESS_DOWN = 2;
        public static final int BRIGHTNESS_ON = 3;
        public static final int BRIGHTNESS_OFF = 4;

        public static final int VOLUME_UP = 1;
        public static final int VOLUME_DOWN = 2;
        public static final int VOLUME_MUTE = 3;
        public static final int VOLUME_MUTE_CANCELED = 4;
        public static final int VOLUME_SPECIFIC_NUM = 5;
        public static final int VOLUME_MAX = 6;
        public static final int VOLUME_MIN = 7;

        public static class FragmentPage {
            public static final int ASSISTANT = 1;
        }
    }

    public static class Launcher {
        public static final String PACKAGE_LAUNCHER = "com.xiaoma.launcher";
        public static final String PACKAGE_SERVICE = "com.xiaoma.service";
        public static final String PACKAGE_SETTING = "com.xiaoma.setting";
        public static final String ACTIVITY_MAIN = "com.xiaoma.launcher.main.ui.MainActivity";
        public static final String CAMERA_SERVICE = "com.xiaoma.launcher.mark.service.CameraService";
        public static final String WECHAT = "com.xylink.mc.faw.bab2019";
        public static final String PACKAGE_ASSISTANT = "com.xiaoma.assistant";
        public static final String SYSTEM_UI = "com.xiaoma.systemui";
    }

}
