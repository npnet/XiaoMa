package com.xiaoma.component.nodejump;

/**
 * Created by kaka
 * on 19-3-14 下午3:46
 * <p>
 * desc: #a
 * </p>
 */
public class NodeConst {
    public static final String ACTION_NODES_JUMP = "ACTION_NODES_JUMP";
    public static final String JUMP_NODES = "JUMP_NODES";
    public static final String ACTIVITY_URI = "ACTIVITY_URI";
    public static final String ACTIVITY_PKG = "ACTIVITY_PKG";
    public static final String ACTIVITY_CLZ = "ACTIVITY_CLZ";

    public interface Xting {
        String PRE = "XTING_";
        String ACT_MAIN = PRE + "ACT_MAIN";
        String FGT_HOME = PRE + "FGT_HOME";
        String FGT_MANUAL = PRE + "FGT_MANUAL";

        String FGT_NET = PRE + "FGT_NET";
        String FGT_NET_INDEX = PRE + "FGT_INDEX";
        String FGT_NET_RECOMMENDED = PRE + "FGT_NET_RECOMMENDED";
        String FGT_NET_RANK = PRE + "FGT_NET_RANK";
        String FGT_NET_CATEGORY = PRE + "FGT_NET_CATEGORY";

        String FGT_PLAYER_ONLINE_DETAILS = PRE + "FGT_PLAYER_ONLINE_DETAILS";
        String OPEN_PLAY_LIST = PRE + "OPEN_PLAY_LIST"; //打开播放列表
        String CLOSE_PLAY_LIST = PRE + "CLOSE_PLAY_LIST"; //关闭播放列表

        String FGT_LOCAL = PRE + "FGT_LOCAL";
        String FGT_LOCAL_SEARCH = PRE + "FGT_LOCAL_SEARCH";//进行搜台
        String FGT_LOCAL_FM = PRE + "FGT_LOCAL_FM";
        String FGT_LOCAL_AM = PRE + "FGT_LOCAL_AM";

        String FGT_MY = PRE + "FGT_MY";
        String FGT_MY_HISTORY = PRE + "FGT_MY_HISTORY";
        String FGT_MY_COLLECT = PRE + "FGT_MY_COLLECT";

        String ACT_PLAYER = PRE + "ACT_PLAYER";
        String ACT_SEARCH = PRE + "ACT_SEARCH";
        String ACT_SEARCH_RESULT = PRE + "ACT_SEARCH_RESULT";

    }

    public interface Setting {
        String PRE = "SETTING_";
        String ASSISTANT_ACTIVITY = PRE + "MAIN_ACTIVITY";
        String ASSISTANT_FRAGMENT = PRE + "ASSISTANT_FRAGMENT";
        String BLUETOOTH_CONNECT_FRAGMENT = PRE + "BLUETOOTH_CONNECT_FRAGMENT";
        String BLUETOOTH_SETTINGS = PRE + "BLUETOOTH_SETTINGS";
        String WIFI_SETTINGS = PRE + "WIFI_SETTINGS";
    }

    public interface BluetoothPhone {
        String PRE = "BLUETOOTHPHONE_";
        String MAIN_ACTIVITY = PRE + "MAIN_ACTIVITY";
        String CALL_RECORDS = PRE + "CALL_RECORDS";
        String CONTACT_AND_COLLECTION = PRE + "CONTACT_AND_COLLECTION";
        String CONTACT = PRE + "CONTACT";
        String COLLECTION = PRE + "COLLECTION";
        String MISSED_CALL = PRE + "MISSED_CALL";
    }

    public interface MUSIC {
        String PRE = "MUSIC_";
        String MAIN_ACTIVITY = PRE + "MAIN_ACTIVITY";
        String MAIN_FRAGMENT = PRE + "MAIN_FRAGMENT";
        String MINE_FRAGMENT = PRE + "MINE_FRAGMENT";
        String MUSIC_THUMB_FRAGMENT = PRE + "MUSIC_THUMB_FRAGMENT";
        String PLAYER_ACTIVITY = PRE + "PLAYER_ACTIVITY";
        String PLAYER_FRAGMENT = PRE + "PLAYER_FRAGMENT";
        String OPEN_PLAY_LIST = PRE + "OPEN_PLAY_LIST"; //打开播放列表
        String CLOSE_PLAY_LIST = PRE + "CLOSE_PLAY_LIST"; //关闭播放列表
        String OPEN_PLAY_LYRIC = PRE + "OPEN_PLAY_LYRIC"; //打开歌词
        String OPEN_COLLECTION_LIST = PRE + "OPEN_COLLECTION_LIST";
        String OPEN_VIP_CENTER = PRE + "OPEN_VIP_CENTER";//打开会员中心

        String ONLINE_FRAGMENT = PRE + "ONLINE_FRAGMENT";//在线音乐
        String LOCAL_FRAGMENT = PRE + "LOCAL_FRAGMENT";//本地音乐
        String BLUETOOTH_FRAGMENT = PRE + "BLUETOOTH_FRAGMENT";//蓝牙音乐
        String USB_FRAGMENT = PRE + "USB_FRAGMENT";//usb音乐
    }


    public interface LAUNCHER {
        String PRE = "LAUNCHER_";
        String MAIN_ACTIVITY = PRE + "MAIN_ACTIVITY";
        String DESK_SERVICE = PRE + "DESK_SERVICE";
        String VOICE_TRAINING = PRE + "VOICE_TRAINING";
    }

    public interface  SHOP{
        String PRE = "SHOP_";
        String ASSISTANT_ACTIVITY = PRE + "MAIN_ACTIVITY";
        String BUY_FLOW= PRE +"BUY_FLOW";
    }

    public interface  XKAN{
        String PRE = "XKAN_";
        String ASSISTANT_ACTIVITY = PRE + "MAIN_ACTIVITY";
        String OPEN_VIDEO= PRE +"OPEN_VIDEO";
        String OPEN_PICTURE= PRE +"OPEN_PICTURE";
    }
}
