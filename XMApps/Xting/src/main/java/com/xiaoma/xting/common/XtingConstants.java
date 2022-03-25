package com.xiaoma.xting.common;

import android.support.annotation.IntDef;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.config.ConfigManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XtingConstants {

    public static final String XTING_ROOM_DB_NAME = "xting_room_db_name";

    private XtingConstants() throws Exception {
        throw new Exception();
    }

    public static final String TP_FIRST_START_APP = "firstStartApp";
    public static final String FIRST_ENTER_SEARCH = "FIRST_ENTER_SEARCH";
    public static final int MAX_COUNT_100 = 100;
    public static final String EMPTY_DATA = "null";
    public static final String TP_XTING_SELECT_TAGS = "xtingSelectedTags";
    public static final int PAGE_COUNT = 20;
    public static final String TP_KEY_DATA_SOURCE = "data_source";
    public static final String TP_KEY_CATEGORY = "data_category";
    public static final String TP_KEY_LOCAL_FM_LAST_PLAY = "local_fm_last_play";
    public static final String TP_KEY_LOCAL_FM_LAST_PLAY_FM = "local_fm_last_play_fm";
    public static final String TP_KEY_LOCAL_FM_LAST_PLAY_AM = "local_fm_last_play_am";
    public static final String TP_KEY_LOCAL_FM_CLOSE_MANUAL = "local_fm_close_manual";
    public static final String TP_KEY_XTING_LAST_PLAY_TYPE = "xting_last_play_type";

    public static final int INT_INVALID = -1;

    public interface ErrorMsg {
        String NO_NETWORK = "no network";
        String NULL_DATA = "null data";
    }

    public static boolean LOCAL_FM_TO_XMLY_TOGGLE = true;

    public interface WeekDay {
        int SUNDAY = 0;
        int MONDAY = 1;
        int TUESDAY = 2;
        int WEDNESDAY = 3;
        int THURSDAY = 4;
        int FRIDAY = 5;
        int SATURDAY = 6;
    }

    /**
     * FM/AM 硬件系统中 FM和AM都统一使用 kHz为单位，
     * 所以FM的一般单位 MHz 需要乘以1000来使用，而AM的一般单位本就为kHz
     */
    public static class FMAM {
        public static final int TYPE_FM = 1;
        public static final int TYPE_AM = 0;

        static int FM_ASIA_START = 87500;
        static int FM_ASIA_END = 108000;
        static int AM_ASIA_START = 531;
        static int AM_ASIA_END = 1602;

        static int FM_EUROPE_START = 87500;
        static int FM_EUROPE_END = 108000;
        static int AM_EUROPE_START = 522;
        static int AM_EUROPE_END = 1629;

        static int FM_AMERACIA_START = 87500;
        static int FM_AMERACIA_END = 108000;
        static int AM_AMERACIA_START = 530;
        static int AM_AMERACIA_END = 1710;

        public static int AM_ASIA_STEP = 9;
        static int AM_EUROPE_STEP = 9;
        static int AM_AMERACIA_STEP = 10;

        private static int mFMStart = 0;
        private static int mAMStart = 0;
        private static int mFMEnd = 0;
        private static int mAMEnd = 0;
        private static int mAMStep = 0;

        public static int getFMStart() {
            if (mFMStart != 0) return mFMStart;
            int salesArea = XmCarConfigManager.getSalesArea();
            switch (salesArea) {
                case 0:
                    //亚洲
                    mFMStart = FM_ASIA_START;
                    return FM_ASIA_START;
                case 1:
                    //欧洲
                    mFMStart = FM_EUROPE_START;
                    return FM_EUROPE_START;
                case 2:
                    //美洲
                    mFMStart = FM_AMERACIA_START;
                    return FM_AMERACIA_START;
                default:
                    mFMStart = FM_ASIA_START;
                    return FM_ASIA_START;
            }
        }

        public static int getFMEnd() {
            if (mFMEnd != 0) return mFMEnd;
            int salesArea = XmCarConfigManager.getSalesArea();
            switch (salesArea) {
                case 0:
                    //亚洲
                    mFMEnd = FM_ASIA_END;
                    return FM_ASIA_END;
                case 1:
                    //欧洲
                    mFMEnd = FM_EUROPE_END;
                    return FM_EUROPE_END;
                case 2:
                    //美洲
                    mFMEnd = FM_AMERACIA_END;
                    return FM_AMERACIA_END;
                default:
                    mFMEnd = FM_ASIA_END;
                    return FM_ASIA_END;
            }
        }

        public static int getAMStart() {
            if (mAMStart != 0) return mAMStart;
            int salesArea = XmCarConfigManager.getSalesArea();
            switch (salesArea) {
                case 0:
                    //亚洲
                    mAMStart = AM_ASIA_START;
                    return AM_ASIA_START;
                case 1:
                    //欧洲
                    mAMStart = AM_EUROPE_START;
                    return AM_EUROPE_START;
                case 2:
                    //美洲
                    mAMStart = AM_AMERACIA_START;
                    return AM_AMERACIA_START;
                default:
                    mAMStart = AM_ASIA_START;
                    return AM_ASIA_START;
            }
        }

        public static int getAMEnd() {
            if (mAMEnd != 0) return mAMEnd;
            int salesArea = XmCarConfigManager.getSalesArea();
            switch (salesArea) {
                case 0:
                    //亚洲
                    mAMEnd = AM_ASIA_END;
                    return AM_ASIA_END;
                case 1:
                    //欧洲
                    mAMEnd = AM_EUROPE_END;
                    return AM_EUROPE_END;
                case 2:
                    //美洲
                    mAMEnd = AM_AMERACIA_END;
                    return AM_AMERACIA_END;
                default:
                    mAMEnd = AM_ASIA_END;
                    return AM_ASIA_END;
            }
        }

        public static int getAMStep() {
            if (mAMStep != 0) return mAMStep;
            int salesArea = XmCarConfigManager.getSalesArea();
            switch (salesArea) {
                case 0:
                    //亚洲
                    mAMStep = AM_ASIA_STEP;
                    return AM_ASIA_STEP;
                case 1:
                    //欧洲
                    mAMStep = AM_EUROPE_STEP;
                    return AM_EUROPE_STEP;
                case 2:
                    //美洲
                    mAMStep = AM_AMERACIA_STEP;
                    return AM_AMERACIA_STEP;
                default:
                    mAMStep = AM_ASIA_STEP;
                    return AM_ASIA_STEP;
            }
        }
    }

    public static final int MUSIC_AND_RADIO_HISTORY = 0;
    public static final int MUSIC_AND_RADIO_SEARCH = 1;
    public static final String ACTION_JSON = "action_json";

    //获取电台搜索列表
    public static String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    public static final String GET_RADIO_LIST = BASE_URL + "audio/getRadioListByTitleNew.action";
//    public static final String GET_RADIO_LIST="http://111.230.137.157:18082/rest/audio/getRadioListByTitle.action";

    public static final boolean SUPPORT_ONLINE_FM = true;

    public interface URL_SUFFIX {
        String PREFERENCE_QUERY_TAGS = "D058/queryAllRadioTags";
        String PREFERENCE_UPDATE_TAGS = "D058/addRadioTagsToUser";
        String AUDIO_LIST = "audio/getRadioListById.action";
        String ADUIO_RECOMMEND = "xmly/getRecommendTracksV2.action";
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CollectType.LOCAL_RADIO, CollectType.ONLINE_RADIO,
            CollectType.ONLINE_TRACK, CollectType.ONLINE_SCHEDULE})
    public @interface CollectType {
        int ONLINE_RADIO = 1;
        int ONLINE_TRACK = 2;
        int ONLINE_SCHEDULE = 3;
        int LOCAL_RADIO = 4;
    }


    public interface LastPlayType {
        int ONLINE_XMLY = 1;
        int LOCAL_RADIO = 2;
    }
}
