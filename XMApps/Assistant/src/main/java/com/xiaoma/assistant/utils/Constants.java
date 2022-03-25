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
        public static final int PAGE_ITEM_TYPE_NAVI = 0;//导航
        public static final int PAGE_ITEM_TYPE_SETUP_HOME_ADDRESS = 1;//设置家地址
        public static final int PAGE_ITEM_TYPE_SETUP_COMPANY_ADDRESS = 2;//设置公司地址
        public static final int PAGE_ITEM_TYPE_PSSING_POINT_SEARCH = 3;//沿途搜索

        //返回结果正确
        public static final int ERRNO = 0;
        //景点查询
        public static final String TRAVEL = "travel";
        public static final String FIND = "find";
        //美食查询
        public static final String RESTAURANT = "restaurant";
        //TTS
        public static final String TALK = "talk_service";
        public static final String TIME = "time";
        public static final String SYSPROFILE = "sysprofile";  //查询哪里人
        public static final String BAIKE = "baike";
        public static final String KG = "kg";
        //笑话
        public static final String JOKE = "audio.joke.play";
        public static final String JOKE_SEARCH = "audio.joke";
        //限行
        public static final String TRAFFIC = "traffic_limit";
        public static final String UNIVERSAL_SEARCH = "universal_search";
        //航班
        public static final String FLIGHT = "planeticket";
        public static final String FLIGHT_TICKET = "iov.flight.buyticket";
        //火车票
        public static final String TRAIN = "train_ticket";
        //天气
        public static final String WEATHER = "duer_weather";
        public static final String WEATHER_QUERY = "sys_weather";
        //股票
        public static final String STOCK_CN = "shanghai_shenzhen_stock";
        public static final String STOCK_CN_SEARCH = "shanghai_shenzhen_stock_search";
        public static final String STOCK_US = "us_stock";
        public static final String STOCK_US_SEARCH = "us_stock_search";
        public static final String STOCK_HK = "hongkong_stock";
        public static final String STOCK_HK_SEARCH = "hongkong_stock_search";
        public static final String STOCK_INDEX = "stock";
        public static final String STOCK_INDEX_SEARCH = "stock_search";
        //导航
        public static final String DOMAIN = "map";
        public static final String NEARBY = "nearby";
        public static final String ROUTE = "route";
        public static final String POI = "poi";
        public static final String DOMAIN_NAVIGATE_INSTRUCTION = "navigate_instruction";
        //沿途搜索
        public static final String CARD_TYPE_PASSING_POINT = "passing_point";
        public static final String INTENT_SEARCH = "search";
        //位置
        public static final String POSITION = "location";
        //歌曲
        public static final String MUSIC = "music";
        public static final String PLAY = "play";
        //图片
        public static final String IMAGE_SEARCH = "image_search";
        //电影
        public static final String FILM_SEARCH = "film_ticket_buyer";
        //停车场
        public static final String PARKING = "parking";
        public static final String PARKING_SEARCH = "parking.search_info";
    }


    public static class ParserConstant {

        /**
         * 设置家地址
         */
        public static final String NAVI_SET_HOME_ADDRESS = "SET_HOME";

        /**
         * 设置公司地址
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
        public static final String SETTING = "设置";
        public static final String WIFI = "wifi";
        public static final String MARK = "途记";
        public static final String WIFI_SETTING = "wifi设置";
        public static final String OPEN_ROBOTMODE = "打开全息影像";
        public static final String DATA_MANAGER = "日程管理";
        public static final String RADIO = "收音机";
        public static final String VOICE_ASSISTANT = "语音助理";
        public static final String SMART_HOME = "智能家居";
        public static final String CAR_HOME_NET = "车家互联";
        public static final String CALENDAR = "日历";
        public static final String OFF_ROBOTMODE = "关闭全息影像";
        public static final String BOARDING_LIGHTING = "登车照明";
        public static final String OUT_OF_CAR_LIGHTING = "离车照明";
        public static final String BLUETOOTH_SETTINGS = "蓝牙设置";
        public static final String BLUETOOTH = "蓝牙";
        public static final String RECOGNIZE_SONG = "听歌识曲";
        public static final String VR_PRACTICE = "语音训练";
        public static final String ELECTRONIC_DOG = "电子狗";
        public static final String ELECTRONIC_EYE = "电子眼";
        public static final String HOT_SPOT = "热点";
        public static final String SHARED_HOT_SPOT = "共享热点";
        public static final String WIFI_HOT_SPOT = "wifi热点";
        public static final String NET_HOT_SPOT = "网络共享";
        public static final String NET_HOT = "网络热点";
        public static final String HIGH_WIND = "高风";
        public static final String MEDIUM_WIND = "中风";
        public static final String LOW_WIND = "低风";
        public static final String FACE = "面";
        public static final String HEAD = "头";
        public static final String FOOT = "脚";
        public static final String FACE_FOOT = "吹面吹脚";
        public static final String AUTO = "自动";
        public static final String DEFOGGING = "后除雾";
        public static final String MANUAL = "手动";
        public static final String REFRIGERATION = "制冷";
        public static final String DEHUMIDIFICATION = "除湿";
        public static final String HEATING = "制热";
        public static final String AIR_SUPPLY = "送风";
        public static final String INNER_LOOP = "内循环";
        public static final String OUTSIDE_LOOP = "外循环";
        public static final String REAR_WINDOW_HEATING = "后窗加热";
        public static final String FRONT_WINDOW = "吹前窗";
        public static final String AIR_OUTLET = "出风口";
        public static final String COMPRESSOR = "压缩机";
        public static final String LOCAL_STATION = "本地电台";
        public static final String LOCAL_VIDEO = "本地视频";
        public static final String LOCAL_PICTURE = "本地图片";
        public static final String USB_PICTURE = "usb图片";
        public static final String USB_MUSIC = "usb音乐";
        public static final String BLUETOOTH_MUSIC = "蓝牙音乐";

        public static final String FRONT_DEFROG = "前除霜";
        public static final String BACK_DEFROG = "后除霜";
        public static final String DEFOG = "除霜";
        public static final String DEMIST = "除雾";

        public static final String MAIN_LOCK = "主驾门锁";
        public static final String LOCK = "锁";
        public static final String ALL_LOCK = "所有门锁";
        public static final String SKYLIGHT = "天窗";
        public static final String SKYLIGHT_START = "星星";
        public static final String SKYLIGHT_PERK = "天窗翘起";
        public static final String OPEN_FRONT_WINDOW = "前车窗";
        public static final String ALL_WINDOW = "所有窗户";
        public static final String REARVIEW_MIRROR_HEATING = "后视镜加热";
        public static final String WINDOW = "窗户";
        public static final String SUNSHADE = "遮阳帘";
        public static final String AMBIENT_LIGHT = "氛围灯";
        public static final String INSIDE_LIGHT = "室内灯";
        public static final String CEILING_LIGHT = "顶棚灯";
        public static final String READ_LIGHT = "阅读灯";
        public static final String TRUNK = "后备箱";
        public static final String BACKDOOR = "后背门";
        public static final String LOW_BEAM_LIGHTS = "近光灯";
        public static final String HIGH_BEAM = "远光灯";
        public static final String REVERSE_IMAGE = "倒车影像";
        public static final String WIPER = "雨刮器";
        public static final String FRONT_WIPER = "前雨刮器";
        public static final String FOG_LIGHT = "雾灯";
        public static final String GALLERY_LIGHT = "示廊灯";
        public static final String WARNING_LIGHT = "警示灯";
        public static final String AUTOMATIC_PARKING = "自动泊车";
        public static final String CRUISE = "巡航";
        public static final String PANORAMIC_360 = "360";
        public static final String SEAT = "座椅";
        public static final String MAIN_SEAT = "主驾座椅";
        public static final String FRONT_SEAT = "副驾座椅";
        public static final String LEFT_FRONT_WINDOW = "左前车窗";
        public static final String LEFT_BACK_WINDOW = "左后车窗";
        public static final String RIGHT_FRONT_WINDOW = "右前车窗";
        public static final String RIGHT_BACK_WINDOW = "右后车窗";

        public static final String SMOKING_MODE = "抽烟模式";

        public static final String RIGHT_BACK = "右后";
        public static final String RIGHT = "右";
        public static final String RIGHT_FRONT = "右前";
        public static final String BEFORE = "前";
        public static final String LEFT_FRONT = "左前";
        public static final String LEFT = "左";
        public static final String LEFT_REAR = "左后";
        public static final String REAR = "后";


        public static final String OIL_QUANTITY = "油量";
        public static final String TIRE_PRESSURE = "胎压";
        public static final String ENGINE = "发动机";
        public static final String AIR_CONDITIONING = "空调";
        public static final String WATER_TEMPERATURE = "水温";
        public static final String SKID = "刹车";
        public static final String BRAKE = "制动";
        public static final String OVERALL = "整体";
        public static final String FUEL_CONSUMPTION = "油耗";
        public static final String MILEAGE = "里程";
        public static final String REMAINING_MILEAGE = "续航里程";
        public static final String DISPLAY_MODEL = "显示模式";

        public static final String BLUETOOTH_PHONE = "蓝牙电话";
        public static final String NAVI = "导航";
        public static final String ICALL = "道路服务";
        public static final String BCALL = "道路救援";


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
