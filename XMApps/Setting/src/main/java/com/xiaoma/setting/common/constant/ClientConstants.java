package com.xiaoma.setting.common.constant;

public class ClientConstants {

    public static final int SETTING_PHONE_PORT = 7070;

    public static class ApiAction{
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

    public static class DataKey{
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
}
