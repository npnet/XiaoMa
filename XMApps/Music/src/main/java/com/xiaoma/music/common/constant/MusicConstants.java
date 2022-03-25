package com.xiaoma.music.common.constant;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.EnvType;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
public class MusicConstants {

    private static final String KW_TEST_ENV_APP_ID = "aig7hvv3hs42";
    private static final String KW_TEST_ENV_KEY = "tykhig85av";
    private static final String KW_RELEASE_ENV_APP_ID = "efhm74pt0teg";
    private static final String KW_RELEASE_ENV_KEY = "5jz7ggs6v0";

    public static final boolean SHOW_ONLINE_TABLE = true;
    public static final boolean SHOW_LOCAL_TEST = false;


    public static final int MUSIC_HISTORY_GRID_NUM = 2;
    public static final String FIRST_ENTER_SEARCH = "first_in_search";
    public static final String MUSIC_IMG_COVER = "music_img_cover";
    public static final String MUSIC_SEARCH_KEY = "music_search_key";
    public static final String TP_FIRST_START_APP = "firstStartMusic";
    public static final String TP_MUSIC_SELECT_TAGS = "tp_music_select_tags";
    public static final String AUDIO_FOCUS_SOURCE = "audio_focus_source_key";

    public class TableType {
        public static final int ONLINE_MUSIC = 0;
        public static final int LOCAL_MUSIC = 1;
        public static final int MINE_MUSIC = 2;
    }

    public static final String AUDIO_MP3 = "mp3";
    public static final String AUDIO_AAC = "m4a";
    public static final String AUDIO_WAV = "wav";
    public static final String AUDIO_WMA = "wma";

    public class NetWorkConstants {
        public static final String GET_ALL_PREFERENCE = "D058/queryAllMusicTags";
        public static final String ADD_USER_PREFERENCE = "D058/addMusicTagToUser";
        public static final String SEARCH_LAUNCHER_MUSIC_LIST = "audio/getMusicListById.action";
        public static final String SEARCH_MUSIC_BY_KEY = "music/search.action";
        public static final String REQUEST_MUSIC_TAG_BY_ID = "music/getTagByMusicId.action";
        public static final String REQUEST_KWVIP_PRICE_LIST = "boutiqueMusic/getKuWoVipProductsList.action";
        public static final String REQUEST_BUY_KWVIP_ORDER = "boutiqueMusic/createdOrder.action";
        public static final String REQUEST_CHECK_KWVIP_ORDER_STATE = "boutiqueMusic/getOrderStatusByOrderId.action";
    }

    public interface VPConstants {
        String GET_MUSIC_LIST = "music/searchByName.action";
    }

    public interface KwConstants {
        //目前酷我只提供了测试环境
        EnvType ENV_TYPE = ConfigManager.EnvConfig.getEnvType();
        String KW_APP_ID = ENV_TYPE == EnvType.TEST ? KW_TEST_ENV_APP_ID : KW_RELEASE_ENV_APP_ID;
        String KW_APP_KEY = ENV_TYPE == EnvType.TEST ? KW_TEST_ENV_KEY : KW_RELEASE_ENV_KEY;
        boolean KW_SDK_ENV_FLAG = ENV_TYPE == EnvType.TEST;
    }
}
