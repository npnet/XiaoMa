package com.xiaoma.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_LOCAL_AM;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_LOCAL_FM;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_NET_FM;

/**
 * @author youthyJ
 * @date 2018/11/7
 */
public class AudioConstants {
    public static final String ACTION_PLAYER_PREPARED = "com.xiaoma.action.PLAYER_PREPARED";
    public static final String SERVICE_LOCATION = "com.xiaoma.xting";

    private AudioConstants() throws Exception {
        throw new Exception();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Options.PLAY,
            Options.PAUSE,
            Options.NEXT,
            Options.PREVIOUS,
            Options.FAVORITE,
            Options.SWITCH_PLAY_MODE,
            Options.COLLECT,
            Options.CANCEL_COLLECT
    })
    public @interface AudioOption {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AudioStatus.LOADING,
            AudioStatus.PLAYING,
            AudioStatus.PAUSING,
            AudioStatus.STOPPED,
            AudioStatus.EXIT,
            AudioStatus.ERROR
    })
    public @interface AudioState {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ConnectStatus.USB_MOUNTED,
            ConnectStatus.USB_REMOVE,
            ConnectStatus.USB_NOT_MOUNTED,
            ConnectStatus.USB_UNSUPPORT,
            ConnectStatus.USB_SCAN_FINISH,
            ConnectStatus.USB_SCAN_FINISH_WITH_NO_MUSIC,
            ConnectStatus.USB_SCAN_FINISH_AUTO,
            ConnectStatus.BLUETOOTH_CONNECTED,
            ConnectStatus.BLUETOOTH_DISCONNECTED,
            ConnectStatus.BLUETOOTH_SINK_CONNECTED,
            ConnectStatus.BLUETOOTH_SINK_DISCONNECTED,
    })
    public @interface ConnectState {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AudioTypes.NONE,
            XTING_LOCAL_FM,
            AudioTypes.XTING_NET_FM,
            AudioTypes.MUSIC_ONLINE_KUWO,
            AudioTypes.MUSIC_LOCAL_USB,
            AudioTypes.MUSIC_LOCAL_BT,
            AudioTypes.XTING_NET_RADIO,
            AudioTypes.XTING_KOALA_ALBUM,
            AudioTypes.XTING
    })
    public @interface AudioType {
    }

    public interface Action {

        int SEARCH = 0;
        int PLAYER_CONNECT = 1;
        int PLAYER_CONTROL = 2;
        int GET_AUDIO_SOURCE_TYPE = 3;
        int SEARCH_MUSIC_BY_ALBUM = 4;
        int SEARCH_MUSIC_BY_MUSIC_TYPE = 5;
        int SEARCH_MUSIC_BY_NAME_AND_SINGER = 6;
        int SEARCH_MUSIC_BY_SINGER_AND_CHORUS = 7;
        int SEARCH_MUSIC_BY_RANKING_LIST_TYPE = 8;
        int PLAY = 9;
        int BT_CONNECT_STATE = 10;
        int LAUNCHER_USB_HISTORY = 22;
        int CANCEL_SCAN = 23;

        interface Option {
            int PLAY = 11;
            int PAUSE = 12;
            int NEXT = 13;
            int PREVIOUS = 14;
            int FAVORITE = 15;
            int SWITCH_PLAY_MODE = 16;
            int COLLECT = 17;
            int CANCEL_COLLECT = 18;
            int PLAYER_STATUS = 19;
            int SEEK = 20;
            int CURRENT_PLAY_MODE = 21;
        }
    }

    public interface BundleKey {
        String ACTION = "action";
        String SEARCH_CATEGORY_ID = "search_category_id";

        String EXTRA = "extra";
        String EXTRA_2 = "extra_2";
        String AUDIO_INFO = "audio_info";
        String AUDIO_PROGRESS = "audio_progress";
        String AUDIO_STATE = "audio_state";
        String AUDIO_FAVORITE = "audio_favorite";
        String AUDIO_PLAYMODE = "audio_play_mode";
        String AUDIO_DATA_SOURCE = "data_source";

        String AUDIO_RESPONSE_CODE = "audio_response_code";
        String AUDIO_LIST = "audio_list";
        String HAVE_HISTORY = "have_history";

        String CONNECT_STATE = "connect_state";
        String KW_MUSIC_ID = "kw_music_id";
        String KW_IMAGE_URL = "ke_image_url";
        String BT_CONNECT_STATE = "bt_connect_state";

        String PAGE_INFO = "page_info";
        String CURRENT_PAGE = "current_page";

        String AUDIO_PLAYING_INDEX = "playing_index";

        String SEARCH_ACTION = "search_action";

        String AUDIO_CONTROL_CODE = "control_code";

        String RESULT = "result";

        String SONG = "song";
        String SINGER = "singer";
        String WANT = "want";
        String ALBUM = "album";
        String MUSIC_TYPE = "music_type";
        String CHORUS = "chorus";
        String AUDIO_TYPE = "audio_type";
        String RANKING_LIST_TYPE = "ranking_list_type";
        String SONG_ID = "song_id";
        String SONG_LIST = "song_list";

        String LOCAL_FM_TYPE = "local_fm_type";// 参见LocalFMTyep

        String XiangTingType = "xiangTingType";
        String MusicType = "musicType";
    }

    public static final class MusicType {
        public static final int USB = 0;
        public static final int BLUE = 1;
        public static final int ONLINE = 2;
    }

    public static final class XiangTingType {
        public static final int FM = XTING_LOCAL_FM;
        public static final int AM = XTING_LOCAL_AM;
        public static final int ONLINE = XTING_NET_FM;
    }

    public static final class LocalFMTyep {
        public static final int FM = 1;
        public static final int AM = 0;
    }

    public static final class PlayStatusCode {
        public static final int SUCCESS = 1;
        public static final int USB_NOT_MOUNTED = 2;
        public static final int BLUETOOTH_NOT_MOUNTED = 3;
    }


    @IntDef({AudioResponseCode.DEFAULT, AudioResponseCode.SUCCESS,
            AudioResponseCode.FAIL, AudioResponseCode.ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioResponseCode {
        int DEFAULT = 0;
        int SUCCESS = 1;
        int FAIL = 2;
        int ERROR = 3;
    }

    /**
     * 音频状态
     */
    public static final class AudioStatus {
        public static final int PLAYING = 1;
        public static final int PAUSING = 2;
        public static final int LOADING = 3;
        public static final int STOPPED = 4;
        public static final int EXIT = 999;
        public static final int ERROR = 990;
    }

    public static final class ConnectStatus {
        public static final int USB_MOUNTED = 5;
        public static final int USB_REMOVE = 6;
        public static final int USB_NOT_MOUNTED = 7;//无usb设备挂载
        public static final int USB_UNSUPPORT = 8;//usb设备挂载错误，不支持
        public static final int BLUETOOTH_CONNECTED = 9;
        public static final int BLUETOOTH_DISCONNECTED = 10;
        public static final int BLUETOOTH_SINK_CONNECTED = 11;
        public static final int BLUETOOTH_SINK_DISCONNECTED = 12;
        public static final int USB_SCAN_FINISH = 13;
        public static final int USB_SCAN_FINISH_AUTO = 14;
        public static final int USB_SCAN_FINISH_WITH_NO_MUSIC = 15;
    }

    @IntDef({SearchAction.DEFAULT, SearchAction.CURRENT, SearchAction.PAGE_LIST, SearchAction.FAVORITE,
            SearchAction.SEARCH_RESULT, SearchAction.FETCH_IMAGE, SearchAction.HAVE_HISTORY, SearchAction.SEARCH_MUSIC_BY_NAME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SearchAction {
        int DEFAULT = 0;
        int CURRENT = 1; //获取当前列表
        int PAGE_LIST = 2; //获取分页数据列表
        int FAVORITE = 3; // 获取收藏列表
        int SEARCH_RESULT = 5;//获取桌面分类音频result
        int FETCH_IMAGE = 6;
        int HAVE_HISTORY = 7;
        int SEARCH_MUSIC_BY_NAME = 8;
    }

    @IntDef({AudioPlayMode.SEQUENTIAL, AudioPlayMode.LIST_LOOP,
            AudioPlayMode.SINGLE_LOOP, AudioPlayMode.SHUFFLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioPlayMode {
        int SEQUENTIAL = 0;
        int LIST_LOOP = 1;
        int SINGLE_LOOP = 2;
        int SHUFFLE = 3;
    }


    @IntDef({KwAudioPlayMode.MODE_SINGLE_CIRCLE, KwAudioPlayMode.MODE_ALL_ORDER,
            KwAudioPlayMode.MODE_ALL_CIRCLE, KwAudioPlayMode.MODE_ALL_RANDOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KwAudioPlayMode {
        int MODE_SINGLE_CIRCLE = 0;
        int MODE_ALL_ORDER = 1;
        int MODE_ALL_CIRCLE = 2;
        int MODE_ALL_RANDOM = 3;
    }


    public static final class Options {
        public static final int PLAY = 1;
        public static final int PAUSE = 2;
        public static final int NEXT = 3;
        public static final int PREVIOUS = 4;
        public static final int FAVORITE = 5;
        public static final int SWITCH_PLAY_MODE = 6;
        public static final int COLLECT = 7;
        public static final int CANCEL_COLLECT = 8;
    }

    @IntDef({PlayAction.DEFAULT, PlayAction.PLAY_LAUNCHER_ITEM, PlayAction.HISTORY, PlayAction.PLAY_LIST_AT_INDEX,
            PlayAction.PLAY_ALBUM_AT_INDEX, PlayAction.PLAY_KW_FAVORITE_LIST, PlayAction.PLAY_REC_MUSIC, PlayAction.PLAY_REC_MUSIC_NAME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayAction {
        int DEFAULT = 0;//播放当前显示的
        int HISTORY = 1; //播放上次最后历史记录
        int PLAY_LAUNCHER_ITEM = 2; // 当点击桌面item时候
        int PLAY_LIST_AT_INDEX = 3; // 点击播放列表播放
        int PLAY_ALBUM_AT_INDEX = 4; //点击播放专辑的时候,电台收藏的是专辑，音乐收藏的单曲
        int PLAY_KW_FAVORITE_LIST = 5;//点击音乐收藏播放
        int PLAY_REC_MUSIC = 6;//点击听歌识曲ID 播放
        int PLAY_REC_MUSIC_NAME = 7;//点击听歌识曲Name播放
    }

    /**
     * 这的常量主要用与区分不同的音频类型
     * <p>
     * <p>
     * !!!接入时增加Type，请将其加入下方的范围限定注解中!!!
     */
    public static final class AudioTypes {
        public static final int NONE = 0;
        public static final int XTING_NET_FM = 1; //想听在线专辑
        public static final int XTING_LOCAL_FM = 2;
        public static final int MUSIC_ONLINE_KUWO = 3;
        public static final int MUSIC_LOCAL_USB = 4;
        public static final int MUSIC_LOCAL_BT = 5;

        public static final int XTING_NET_RADIO = 6;  // 想听在线广播

        public static final int MUSIC_KUWO_RADIO = 7;//音乐私人FM

        public static final int XTING_KOALA_ALBUM = 8;
        public static final int XTING_LOCAL_AM = 9;

        public static final int XTING = 10;
    }

    /**
     * 音频来源
     */
    @IntDef({OnlineInfoSource.DEFAULT, OnlineInfoSource.XMLY, OnlineInfoSource.LAUNCHER,
            OnlineInfoSource.KUWO, OnlineInfoSource.KUWO_RADIO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OnlineInfoSource {
        int DEFAULT = 0;
        int XMLY = 1;// xmly直接数据
        int LAUNCHER = 2; //桌面数据
        int KUWO = 3;//超级音乐中在线电台数据
        int KUWO_RADIO = 4;//酷我音乐私人FM
    }

    @IntDef({AudioControlCode.DEFAULT, AudioControlCode.TOP, AudioControlCode.MIDDLE, AudioControlCode.BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioControlCode {
        int DEFAULT = 0; //表示当前列表为空
        int TOP = 1;// 点击上一曲,表示已经加载到顶部
        int MIDDLE = 2; // 表示正常状态,可以继续进行加载
        int BOTTOM = 3; //点击下一曲,表示已经加载到底部
    }


    @IntDef({AudioSubscribeState.DEFAULT, AudioSubscribeState.SUBSCRIBE,
            AudioSubscribeState.UNSUBSCRIBE, AudioSubscribeState.UNABLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioSubscribeState {

        int DEFAULT = 0; //默认 不适用
        int SUBSCRIBE = 1;  // 收藏成功
        int UNSUBSCRIBE = 2; // 成功取消收藏
        int UNABLE = 3; // 不支持收藏
    }
}
