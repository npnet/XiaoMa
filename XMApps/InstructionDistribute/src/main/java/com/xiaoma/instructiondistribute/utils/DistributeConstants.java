package com.xiaoma.instructiondistribute.utils;

import com.xiaoma.center.logic.CenterConstants;

public class DistributeConstants {
    //    public static final int INSTUCTION_DISTRIBUTE_PORT_XTING = 1;
//    public static final int ACTION_GET_CURRENT_TUNER_STATE = 1;
//    public static final int ACTION_SET_TUNER_FREQUENCY = 2;
//    public static final int ACTION_SET_TUNER_BAND = 3;
//    public static final int ACTION_SET_TUNER_FAVORITE = 4;
//    public static final int ACTION_SET_TUNER_SEEK = 5;
//    public static final int ACTION_SET_TUNER_AUTO_STORE = 6;
    public static final String PKG_INSTRCTION_DISTRIBUTE = "com.xiaoma.instructiondistribute";

    /*-------------DialogDispatch---------------------*/
    public static final int BT_DEVICE_PAIRING_VARIANT_PIN = 0;
    public static final int BT_DEVICE_PAIRING_VARIANT_PIN_16_DIGITS = 7;
    public static final int BT_DEVICE_PAIRING_VARIANT_PASSKEY = 1;
    public static final int BT_DEVICE_PAIRING_VARIANT_CONSENT = 3;
    public static final int BT_DEVICE_PAIRING_VARIANT_OOB_CONSENT = 6;
    public static final int BT_DEVICE_PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    public static final int BT_DEVICE_PAIRING_VARIANT_DISPLAY_PIN = 5;
    public static final int BT_DEVICE_PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;

    // remoteApp
    public static final String XTING = "com.xiaoma.xting";
    public static final String MUSIC = "com.xiaoma.music";

    // 本app client对应的port
    public static final int PORT_EOL_XTING = CenterConstants.INSTRUCTION_DISTRIBUTE_PORT_XTING;
    public static final int PORT_BLUETOOTH_AUDIO = 5;
    public static final int PORT_USB_AUDIO = 4;


    // 蓝牙
    public static final int ACTION_SET_BLUETOOTH_AUDIO_PAUSE = 12;
    public static final int ACTION_SET_BLUETOOTH_AUDIO_PLAY = 9;
    public static final int ACTION_GET_BLUETOOTH_AUDIO_PLAY_PAUSE = 19;
    public static final int ACTION_SET_BLUETOOTH_AUDIO_NEXT = 13;
    public static final int ACTION_SET_BLUETOOTH_AUDIO_PREVIOUS = 14;
    public static final int ACTION_SET_BT_PAIR_MODE = 15;
    public static final int ACTION_GET_BT_PAIR_MODE = 16;
    public static final int ACTION_SET_BT_CALL_ACTIVE_DEACTIVE = 20;
    public static final int ACTION_GET_BT_CALL_ACTIVE_DEACTIVE = 21;
    public static final int ACTION_CLEAR_BT_PAIRED_LIST = 22;
    public static final int ACTION_GET_BT_MODULE_VERSION = 14;
    public static final int ACTION_SET_BT_PAIR_WITH_DEVICE = 23;
    public static final int ACTION_GET_BT_PAIR_WITH_DEVICE = 24;
    // usb音乐
    public static final int ACTION_SET_USB_AUDIO_PLAY = 9;
    public static final int ACTION_SET_USB_AUDIO_PAUSE = 12;
    public static final int ACTION_GET_USB_AUDIO_PLAY_PAUSE = 19;
    public static final int ACTION_SET_USB_AUDIO_PLAY_MODE = 16;
    public static final int ACTION_GET_USB_AUDIO_PLAY_MODE = 21;
    public static final int ACTION_SET_USB_AUDIO_NEXT = 13;
    public static final int ACTION_SET_USB_AUDIO_PREVIOUS = 14;
    public static final int ACTION_SEEK_TO_POSITION = 20;
    public static final int ACTION_SET_EQ = 5;
    public static final int ACTION_GET_EQ = 6;
    public static final int ACTION_SET_USB_PICTURE_OPERATION = 54;
    public static final int ACTION_SET_USB_PICTURE_SHOW_TYPE = 55;
    public static final int ACTION_GET_USB_PICTURE_SHOW_TYPE = 56;
    public static final int ACTION_SET_USB_PIC_PREVIOUS_NEXT = 57;
    public static final int ACTION_SET_USB_VIDEO_PAUSE_PLAY = 58;
    public static final int ACTION_GET_USB_VIDEO_PAUSE_PLAY = 59;
    public static final int ACTION_SET_USB_VIDEO_PREVIOUS_NEXT = 60;

    // 恢复出厂设置
    public static final int ACTION_FACTORY_RESET = 4;
    public static final int ACTION_SET_BLUETOOTH = 10;
    public static final int ACTION_GET_BLUETOOTH = 11;
    public static final int ACTION_GET_FADER_LEVEL = 28;
    public static final int ACTION_SET_FADER_LEVEL = 29;
    public static final int ACTION_GET_BALANCE_LEVEL = 30;
    public static final int ACTION_SET_BALANCE_LEVEL = 31;
    public static final int ACTION_GET_MUTE = 32;
    public static final int ACTION_SET_MUTE = 33;
    public static final int ACTION_GET_SPEED_VOLUME = 34;
    public static final int ACTION_SET_SPEED_VOLUME = 35;
    public static final int ACTION_GET_EQ_SETTING = 36;
    public static final int ACTION_SET_EQ_SETTING = 37;
    public static final int ACTION_GET_BEST_POSITION = 38;
    public static final int ACTION_SET_BEST_POSITION = 39;
    public static final int ACTION_GET_SOUND_FIELD_STATUS = 40;
    public static final int ACTION_SET_SOUND_FIELD_STATUS = 41;
    public static final int ACTION_GET_ARKAMYS_3D = 42;
    public static final int ACTION_SET_ARKAMYS_3D = 43;
    public static final int ACTION_GET_STREAM_VOLUME = 44;
    public static final int ACTION_SET_STREAM_VOLUME = 45;
    public static final int ACTION_SET_TFT_ILLUMINATION = 69;
    public static final int ACTION_GET_TFT_ILLUMINATION = 70;
    public static final int ACTION_SET_TFT_DISPLAY_PATTERN = 71;
    public static final int ACTION_GET_TFT_DISPLAY_PATTERN = 72;
    public static final int ACTION_SET_TEST_SCREEN_ILLUMINATION = 73;
    public static final int ACTION_GET_TEST_SCREEN_ILLUMINATION = 74;
    public static final int ACTION_SET_TEST_MFD_ILLUMINATION = 75;
    public static final int ACTION_GET_TEST_MFD_ILLUMINATION = 76;
    public static final int ACTION_SET_LCD_LVDS_OUTPUT = 78;
    public static final int ACTION_GET_LCD_LVDS_OUTPUT = 79;
    public static final int ACTION_SET_BLUETOOTH_ADDRESS = 12;
    public static final int ACTION_GET_BLUETOOTH_ADDRESS = 13;

    // 显示不同类型key
    public static final String DISPLAY_TYPE = "displayType";
    public static final int BACK_TO_HOME_PAGE = 0;
    public static final int TEST_WHITE_SCREEN = 1;
    public static final int TEST_BLACK_SCREEN = 2;
    public static final int TEST_RED_SCREEN = 3;
    public static final int TEST_GREEN_SCREEN = 4;
    public static final int TEST_BLUE_SCREEN = 5;
    public static final int EGIHT_COLOR_BAR_SCREEN = 6;
    public static final int BIG_CHESS_SCREEN = 7;
    public static final int FISH_SCREEN = 8;
    public static final int GRAY_BLACK_SCREEN = 9;
    public static final int H_GRAY_SCALE_SCREEN = 10;
    public static final int MID_GRAY_SCREEN = 11;
    public static final int V_BW_SCREEN = 12;
    public static final int V_GRAY_SCREEN = 13;
    public static final int RESERVED_E = 14;
    public static final int RESERVED_F = 15;
    public static final int WRITE_LCD_LVDS_OUTPUT = 16;

    public static final String USB_AUDIO_SIZE = "usb_size"; // 扫描到u盘中音频的个数
    public static final int ERROR_CODE_EOL = 1000; // 所有接口通用错误码
    public static final int UNCERTAIN_CODE_EOL = 0; // eol 待确定的回传值
}
