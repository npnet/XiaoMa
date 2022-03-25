package com.xiaoma.launcher.common.constant;


import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.player.AudioConstants;

/**
 * Created by Thomas on 2018/10/18 0018
 */

public class LauncherConstants {
    /**
     * 是否显示四维地图
     */
    public static final boolean IS_INIT_NAVIBAR_WINDOW = true;
    public static final String PANORAMIC_IMAGE = "全景影像";
    public static final String MARK_TEXT = "途记";
    public static final String TEAM = "车队";
    public static final String DRIVE_SCORE = "驾驶评分";
    public static final boolean IS_CONTAIN_PANORAMIC_IMAGE = true;
    public static final String ASSISTANT_GUIDE_VIEW_SHOW = "assistant_guide_view_show";
    public static final String MAIN_ACTIVITY_CLASS_NAME = "com.xiaoma.launcher.main.ui.MainActivity";
    public static final String PACKAGE_NAME = "com.xiaoma.launcher";
    public static final int PAGE_SIZE = 30;
    /**
     * 座位最大选择数量
     */
    public static final int FILM_SEAT_MAX_SELECTED = 4;
    public static final String CAMERA_STYLE = "camera_style";
    /**
     * 加载更多状态码
     */
    public static int COMPLETE = 0;
    public static int END = 1;
    public static int FAILED = 2;
    public final static String MQTT_ACTION = "com.xiaoma.mqtt";
    public static int LAUNCHER_PORT = 1080;
    public static final int LOCATION_ERROR = 5001;
    //首页栏目播放状态
    //无状态
    public static int NULL_STATE = 0;
    //播放状态
    public static int PLAY_STATE = AudioConstants.AudioStatus.PLAYING;
    //暂停状态
    public static int PAUSE_STATE = AudioConstants.AudioStatus.PAUSING;
    //加载中
    public static int LOADING_STATE = AudioConstants.AudioStatus.LOADING;
    public static final String QUERY = "query";
    public static final String CATEID = "cateid";
    public static final String SEARCH_KEY = "search_key";
    public static final String DISPLAY = "display";
    public static final String DISCLAIMER_STATUS = "disclaimer status";
    /*电影类型*/
    public static final String FILM_TYPE = "film_type";
    /*路径类型*/
    public static final String PATH_TYPE = "path_type";
    public static final String TURN_OFF_SCREEN_ACTION = "com.xiaoma.turnoff.screen.action";
    public static final String TURN_ON_SCREEN_ACTION = "com.xiaoma.turnon.screen.action";
    public final static String PERIOD_ACTION = "com.xiaoma.service.period.action";
    public final static String EXTRA_PERIOD_DATA = "period_data";
    public final static String IS_NEED_PERIOD = "is_need_period";
    public final static String PERIOD_STATE = "period_state";
    public final static String IS_FUEL_WARNING = "is_fuel_warning";
    public final static String SHOW_FUEL_WARNING = "show_fuel_warning";
    public final static String INSTALL_ACTION = "com.xiaoma.install.action";
    public final static String INSTALL_PATH = "install_path";
    public final static String INSTALL_FILE_MD5 = "install_file_md5";
    // 车载微信导航方式广播action
    public final static String START_NAVI_BY_POI = "start_navi_by_poi";
    public final static String START_NAVI_BY_KEY_WORDS = "start_navi_by_key";
    public final static String IS_BUSY_IBCALL = "is_busy_ibcall";

    public static class ActionExtras {
        public static final String FILMS_BEAN = "filmsBean";
        public static final String ALLOW_PREVIEW_TRAILER = "ALLOW_PREVIEW_TRAILER";
        public static final String MOVIE_TAG = "MOVIE_TAG";
        public static final String FROM = "FROM";
        public static final String DATE = "DATE";
        public static final String FROM_PAKAGE_NAEM = "com.xiaoma.assistant";
        public static final String MOVIE_NAME = "MOVIE_NAME";
        public static final String MOVIE_BUNDLE = "bundle";
        public static final String SHOW_BEAN = "showBean";
        public static final String NEARBY_CINEMAS_DETAILS_BEAN = "nearbyCinemasDetailsBean";
        public static final String LOCK_SEAT_RESPONSE_BEAN = "lockSeatResponseBean";
        public static final String CINEMAS_SHOW_BEAN = "cinemasShowBean";
        public static final String HALL_SEATS_INFO_BEAN = "hallSeatsInfoBean";
        public static final String ORDER_DETAIL_BEAN = "orderDetailBean";
    }

    public static class RecommendExtras {
        public static final String RECOMMEND_HOTEL_LIST = "recommend_hotel_list";
        public static final String RECOMMEND_FOOD_LIST = "recommend_food_list";
        public static final String RECOMMEND_ATTRACTION_LIST = "recommend_attraction_list";
        public static final String RECOMMEND_PARKING_LIST = "recommend_parking_list";
        public static final String RECOMMEND_FILM_LIST = "recommend_film_list";
    }

    public static class TravelConstants {
        public static final String FOOD_DATA_RECOMMEND_TYPE = "food_data_recommend_type";
        public static final String ATTRACTION_DATA_RECOMMEND_TYPE = "attraction_data_recommend_type";
        public static final String PARKING_DATA_RECOMMEND_TYPE = "parking_data_recommend_type";
        public static final String FILM_DATA_RECOMMEND_TYPE = "film_data_recommend_type";
        public static final String OIL_PARK_DATA_RECOMMEND_TYPE = "oil_park_data_recommend_type";
        public static final String OIL_PARK_DATA_POI_TYPE = "oil_park_data_poi_type";
        public static final String OIL_PARK_DATA_RECOMMEND_LON = "oil_park_data_recommend_lon";
        public static final String OIL_PARK_DATA_RECOMMEND_LAT = "oil_park_data_recommend_lat";
    }

    public static class CameraSize {
        public static final int CAMERA_WIDTH = 576;
        public static final int CAMERA_HEIGHT = 324;
    }

    public static class LauncherApp {
        public static final String LAUNCHER_MUSIC_CLASS = "com.xiaoma.music.MusicActivity";
        public static final String LAUNCHER_XTING_CLASS = "com.xiaoma.xting.XtingActivity";
        public static final String LAUNCHER_ACTIVITY_CLASS = "com.xiaoma.component.base.LauncherActivity";
        public static final String LAUNCHER_ACTIVITY_SERVICE_CLASS = "com.xiaoma.component.base.LauncherAppService";
        public static final String LAUNCHER_MUSIC_PACKAGE = "com.xiaoma.music";
        public static final String LAUNCHER_XTING_PACKAGE = "com.xiaoma.xting";
        public static final String LAUNCHER_SETTING_PACKAGE = "com.xiaoma.setting";
        public static final String LAUNCHER_DUALSCREEN_PACKAGE = "com.xiaoma.dualscreen";
        public static final String LAUNCHER_BLUETOOTH_PHONE_PACKAGE = "com.xiaoma.bluetooth.phone";
        public static final String LAUNCHER_PET_PACKAGE = "com.xiaoma.pet";
        public static final String LAUNCHER_PET_CLASS = "com.xiaoma.pet.ui.PetSplashActivity";
        public static final String LAUNCHER_ASSISTANT_PACKAGE = "com.xiaoma.assistant";
        public static final String LAUNCHER_ASSISTANT_SERVICE = "com.xiaoma.assistant.service.AssistantService";
        public static final String LAUNCHER_PERSONAL_PACKAGE = "com.xiaoma.personal";
        public static final String LAUNCHER_PERSONAL_CLASS = "com.xiaoma.personal.order.ui.MineOrderActivity";
        public static final String LAUNCHER_CARLAUNCHER_PACKAGE = "com.android.car.carlauncher";
        public static final String LAUNCHER_CARLAUNCHER_CLASS = "com.android.car.carlauncher.CarLauncher";
        public static final String LAUNCHER_APPSTORE_PACKAGE = "com.xiaoma.app";
        public static final String LAUNCHER_APPSTORE_SERVICE = "com.xiaoma.app.common.service.AppNotificationService";
        public static final String LAUNCHER_SERVICE_PACKAGE = "com.xiaoma.service";
        public static final String LAUNCHER_SERVICE_CARSERVICE = "com.xiaoma.service.common.service.CarNotificationService";
        public static final String LAUNCHER_QIMING_PACKAGE = "com.qiming.fawcard.synthesize";
        public static final String LAUNCHER_QIMING_VIN_KEY = "vin";
        public static final String LAUNCHER_QIMING_ENGINE_KEY = "status";
        public static final String LAUNCHER_QIMING_DRIVERSERVICE = "com.qiming.fawcard.synthesize.base.system.service.DriverService";
    }

    public static class EngineEvent {
        public static final String DRIVE_START_ACTION = "android.intent.action.ACTION_DRIVE_START";
        public static final String DRIVE_STOP_ACTION = "android.intent.action.ACTION_DRIVE_STOP";
    }

    public static final String COLLECT_FILM = "Film";
    public static final String COLLECT_HOTEL = "Hotel";
    public static final String COLLECT_FOOD = "Food";
    public static final String COLLECT_ATTR = "Attractions";

    //四种工作模式
    public static final String CAR_MODEL = "car_model";
    //针对星期六星期天的模式选择
    public static final String CAR_MODEL_REST = "car_model_rest";
    //针对周一到周五的模式选择
    public static final String CAR_MODEL_NORMAL = "car_model_normal";
    //周一周六是否显示过弹窗
    public static final String IS_HAVE_SHOWN = "is_have_shown";
    //刷新推荐显示
    public static final String REFRESH_RECOM = "refresh_recom";
    public static final int LIVE_MODEL = 1;
    public static final int WORK_MODEL = 2;
    public static final int TRAVEL_MODEL = 3;
    public static final int QUIET_MODEL = 4;

    //推荐dialog eventtag
    public static final String RECOMMEND_MESSAGE = "recommend_message";
    public static final String RECOMMEND_PLAY = "recommend_play";
    public static final int RECOMMEND_MUSIC = 1;
    public static final int RECOMMEND_RADIO = 2;

    //后台消息跳转类型
    public static final int HTTP_LINK = 1;//网络地址
    public static final int APP_PROTOCOL = 2;//应用协议地址
    public static final int NONE = -1;//无链接

    //语音助手dialog
    public static final String SHOW_VOICE_ASSISTANT_DIALOG = CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG;
    public static final String DISMISS_VOICE_ASSISTANT_DIALOG = CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG;

    //更新日程view
    public static final String UPDATE_STATUS = "update_status";
    public static final String BlUETOOTH_STATUS = "bluetooth_status";
    public static final String BlUETOOTH_TURN_ON_SUCCESS = "bluetooth_turn_on_success";
    public static final String BlUETOOTH_TURN_ON_FAILED = "bluetooth_turn_on_failed";
    public static final String BlUETOOTH_TURN_OFF_SUCCESS = "bluetooth_turn_off_success";
    public static final String BlUETOOTH_TURN_OFF_FAILED = "bluetooth_turn_off_failed";
    public static final String WIFI_WORK_PATTERN = "wifi_work_pattern";
    public static final String IS_DATA_SWITCH = "is_data_switch";

    //保存开机播报json
    public static final String VIDEO_JSON = "video_json";
    public static final String WHETHER_HAVE_MARK = "whether have mark";
    public static final String LAST_YEARS = "last_years";
    public static final String LAST_MONTH = "last_month";
    public static final String FIST_YEARS = "fist years";
    public static final String USER_IMG_PATH = "user_img_path";

    //途记相关
    public static final String MARK_SAVE_SUCCESS = "1";
    public static final String MARK_SAVE_SUCCESS_UPPERLIMIT = "10043";
    public static final String MARK_SAVE_ERROR_UPPERLIMIT = "10044";
    public static final String MARK_SAVE_ERROR = "10045";


    //跳转想听播放页面
    public static final String XTING_PLAYER_URL = "xiaoma://com.xiaoma.xting/playerDetail";
    public static final String ACTION_XTING = "com.xiaoma.xting.player";

    //跳转音乐播放页面
    public static final String MUSIC_PLAYER_URL = "xiaoma://com.xiaoma.music/playerDetail";
    public static final String ACTION_MUSIC = "com.xiaoma.music.player";

    //跳转setting主页
    public static final String SETTING_MAIN_URL = "xiaoma://com.xiaoma.setting/mainActivity";
    public static final String ACTION_SETTING = "com.xiaoma.setting.main";

    //3D动作
    public static final int TRUN_OFF_ACTION = 2;
    public static final int LEFT_ACTION = 20;
    public static final int RIGHT_ACTION = 21;
    public static final int SUN_ACTION = 41;
    public static final int RAIN_ACTION = 42;

    public static final int MEDIA_VOLUME = 0;//媒体音量
    public static final int PHONE_VOLUME = 1;//电话音量
    public static final int TTS_VOLUME = 2;//TTS音量
    public static final int BT_MEDIA_VOLUME = 3;//蓝牙音乐音量

    public static final String tourist_uid = "1058";

}
