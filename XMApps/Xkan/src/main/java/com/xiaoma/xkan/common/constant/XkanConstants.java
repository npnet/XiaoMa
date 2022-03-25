package com.xiaoma.xkan.common.constant;

/**
 * Created by Thomas on 2018/11/5 0005
 */

public interface XkanConstants {

    /**
     * 图片索引
     */
    String PHOTO_INDEX = "photo_index";

    /**
     * 视频索引
     */
    String VIDEO_INDEX = "video_index";

    int MAIN_TAB_INDEX_MAIN = 0;
    int MAIN_TAB_INDEX_VIDEO = 1;
    int MAIN_TAB_INDEX_PICTURE = 2;
    int MAIN_TAB_INDEX_USB_CONN_STATE = 3;

    String MAIN_TAB_INDEX_MAIN_FRAGMENT_TAG = "MainFragment";
    String MAIN_TAB_INDEX_VIDEO_FRAGMENT_TAG = "VideoFragment";
    String MAIN_TAB_INDEX_PICTURE_FRAGMENT_TAG = "PictureFragment";
    String MAIN_TAB_INDEX_USB_CONN_STATE_FRAGMENT_TAG = "UsbConnStateFragment";

    //文件类型 三种：文件夹 、视频 、图片
    int FILE_TYPE_DEC = 0;
    int FILE_TYPE_VIDEO = 1;
    int FILE_TYPE_PIC = 2;
    int FILE_TYPE_PIC_GIF = 3;


    String FROM_TYPE = "from_type";
    String FROM_ALL = "from_all";
    String FROM_VIDEO = "from_video";
    String FROM_PHOTO = "from_photo";
    String FILE_PATH = "file_path";

    String UPDATE_DATA = "update_data";
    String XKAN_EVENT = "xkan_event";
    String XKAN_USB_REMOVE = "xkan_usb_remove";
    String RELEASE_MEDIAINFO = "release_mediainfo";
    String XKAN_HIDE_MENU_EVENT = "xkan_hide_menu_event";
    String XKAN_HIDE_MENU = "xkan_hide_menu";

    String XKAN_MAIN_PIC_POS = "xkan_main_pic_pos";
    String XKAN_MAIN_VIDEO_POS = "xkan_main_video_pos";
    String XKAN_VIDEO_POS = "xkan_video_pos";
    String XKAN_PIC_POS = "xkan_pic_pos";

    //语音助手dialog
    String SHOW_VOICE_ASSISTANT_DIALOG = "show_voice_assistant_dialog";
    String DISMISS_VOICE_ASSISTANT_DIALOG = "dismiss_voice_assistant_dialog";

    String REFRESH_SCROLL = "refresh_scroll";
    String REFRESH_USB_FRAG_VIEW = "refresh_usb_frag_view";

    String USB_INSERT="usb_insert";
    String USB_MOUNTED="usb_mounted";
    String USB_NO_MOUNTED="usb_no_mounted";
}
