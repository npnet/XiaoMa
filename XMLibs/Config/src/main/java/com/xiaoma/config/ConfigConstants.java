package com.xiaoma.config;

import java.io.File;

/**
 * Created by youthyj on 2018/9/7.
 */
public class ConfigConstants {

    private ConfigConstants() throws Exception {
        throw new Exception();
    }

    public static final String VR_PRACTICE_ACTION = "vr_practice_action";

    public static final String VR_PRACTICE_CONTENT = "vr_practice_content";

    public static final String CONFIG_FILE_SUFFIX = ".xmcfg";

    public static final String PRIVATE_NAME = "Private";

    public static final String XIAOMA_FOLDER_NAME = "com.xiaoma";

    public static final String GLOBAL_CONFIG_FOLDER_NAME = "global";

    public static final String LOCAL_CONFIG_FOLDER_NAME = "local";

    public static final String LOCAL_USER_DB_FILE_NAME = "local_user.db";

    public static final String LOCAL_ENV_CONFIG_FILE_NAME = "env" + CONFIG_FILE_SUFFIX;

    public static final String LOCAL_IDS_CONFIG_FILE_NAME = "ids" + CONFIG_FILE_SUFFIX;

    public static final String TBOX_CONFIG_FILE_NAME = "tbox" + CONFIG_FILE_SUFFIX;

    public static final String LOG_FOLDER_NAME = "log";

    public static final String LOG_UPLOAD_FOLDER_NAME = LOG_FOLDER_NAME + File.separator + "zip";

    public static final String THREE_D_NAME = "3D";

    public static final String ICH_NAME = "ICH";

    public static final String ICL_NAME = "ICL";

    public static final String HU_NAME = "HU";

    public static final String CRASH_FOLDER_NAME = "crash";

    public static final String SKIN_FOLDER_NAME = "skin";

    public static final String SOUND_EFF_FOLDER_NAME = "soundEff";

    public static final String HOLO_DOWNLOAD_FOLDER_NAME = "holo_download";

    public static final String SKIN_DOWNLOAD_FOLDER_NAME = "skin_download";

    public static final String SKIN_UNZIP_FOLDER_NAME = "skin_unzip";

    public static final String SHOP_SOURCE_NAME = "shop";

    public static final String SHOP_ASSISTANT_NAME = "assistant";

    public static final String SHOP_HOLOGRAM_NAME = "hologram";

    public static final String HU_SOUND_EFF_DOWNLOAD_FOLDER_NAME = "hu_sound_eff_download";

    public static final String LCD_SOUND_EFF_DOWNLOAD_FOLDER_NAME = "lcd_sound_eff_download";

    public static final String SHOP_TRY_SOUND_NAME = "try_sound";

    public static final String PET_FOLDER = "pet";

    public static final String PET_INFO_FILE_NAME = "PetInfo" + CONFIG_FILE_SUFFIX;

    public static final String LOGIN_TYPE_NAME = "login_type";

    public static final String PATCH_FOLDER_NAME = "patch";

    public static final String PLUGIN_FOLDER_NAME = "plugin";

    public static final String AD_FOLDER_NAME = "ad";
    public static final String VIDEO_FOLDER_NAME = "video";

    public static final String RECOVERY_FOLDER_NAME = "recovery";

    public static final String CHANNEL_FOLDER_NAME = BuildConfig.CAR_CHANNEL_ID;

    public static final String UPDATE_FOLDER_NAME = "update/" + ConfigManager.ApkConfig.getBuildVersionName();

    public static final String ACTIVE_FOLDER_NAME = "active";

    public static final String ACTIVE_FILE_NAME = "active" + CONFIG_FILE_SUFFIX;

    public static final String USER_VALID_FILE_NAME = "user_valid" + CONFIG_FILE_SUFFIX;

    public static final String LOGIN_STATUS_FILE_NAME = "LoginStatus" + CONFIG_FILE_SUFFIX;

    public static final String LOGIN_TYPE_FILE_NAME = "login_type" + CONFIG_FILE_SUFFIX;

    public static final String LOGIN_CONFIG_FILE_NAME = "login_config" + CONFIG_FILE_SUFFIX;

    public static final String LOGIN_FOLDER_NAME = "login";

    public static final String SCREEN_CAPTURE_FOLDER_NAME = "screen_capture";

    public static final String CONFIG_FOLDER_NAME = "config";

    public static final String PROPERTIES_NAME = "properties.xmcfg";

    public static final String USER_FILE_NAME = "user" + CONFIG_FILE_SUFFIX;

    public static final String APP_STORE_DETAIL_ACTIVITY = "com.xiaoma.app.ui.activity.AppDetailsActivity";

    public static final String APP_STORE_TYPE_KEY = "app_store_type_key";

    public static final String APP_STORE_PACKAGENAME_KEY = "app_store_packagename_key";

    public static final int APP_STORE_TYPE_XIAOMA_APP = 1;

    public static final int APP_STORE_TYPE_XIAOMA_APP_WITHOUT_DATA = 2;

    public static final String APP_STORE_PACKAGE_NAME = "com.xiaoma.app";

    public static final String APP_UPDATE_PROCESS_NAME = "com.xiaoma.update.service";

    public static final String STORAGE_ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";

    public static final String GUIDE_FILE_NAME = "guide" + CONFIG_FILE_SUFFIX;

    public static final String GLOBAL_SKIN_CONFIG = "skin.xmcfg";
    public static final String GLOBAL_AUDIO_VEHICLE_CONFIG = "audio_vehicle.xmcfg";
    public static final String GLOBAL_INSTRUMENT_VEHICLE_CONFIG = "instrument_vehicle.xmcfg";

    public static final String VIDEO_MP4 = ".mp4";
    public static final String VIDEO_MPG = ".mpg";
    public static final String VIDEO_M4V = ".m4v";
    public static final String VIDEO_AVI = ".avi";
    public static final String VIDEO_MOV = ".mov";
    public static final String VIDEO_MKV = ".mkv";
    public static final String VIDEO_FLV = ".flv";
    public static final String VIDEO_3GP = ".3gp";


    public static final String PICTURE_JPG = ".jpg";
    public static final String PICTURE_JPEG = ".jpeg";
    public static final String PICTURE_PNG = ".png";
    public static final String PICTURE_WEBP = ".webp";
    public static final String PICTURE_GIF = ".gif";
    public static final String PICTURE_BMP = ".bmp";

    public final static String NAVIBARWINDOW_OPEN_ACTION = "navibarwindow_open_action";
    public final static String NAVIBARWINDOW_CLOSE_ACTION = "navibarwindow_close_action";

    /*
       用户开启车服务icall 和 bcall
     */

    public static final String XIAOMA_ASSISTANT_ICALL_ACTION = "xiaoma_assistant_icall_action";
    public static final String XIAOMA_ASSISTANT_BCALL_ACTION = "xiaoma_assistant_bcall_action";

    public class CompressType {
        /*位置压缩批量上报*/
        public static final int LOCATION_COMPRESS = 1;
        /*操作日志压缩批量上报*/
        public static final int EVENT_COMPRESS = 2;
        /*流量统计压缩批量上报*/
        public static final int TRAFFIC_COMPRESS = 3;
    }

}
