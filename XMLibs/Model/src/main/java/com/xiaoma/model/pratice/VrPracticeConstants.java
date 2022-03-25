package com.xiaoma.model.pratice;

/**
 * Created by Thomas on 2018/10/18 0018
 */

public interface VrPracticeConstants {

    int MAX_SKILL_SIZE = 10;

    //yomi执行动作
    int YOMI_TTS_SOME_WORDS = 1;
    int YOMI_PLAY_SOME_MUSIC = 2;
    int YOMI_TTS_WEATHER = 3;
    int YOMI_GUIDE = 4;
    int YOMI_PLAY_RADIO = 5;
    int YOMI_CAR_CONTROL = 6;
    int YOMI_MEDIA_VOLUME = 7;
    int YOMI_RECORD = 8;
    int YOMI_NEWS = 9;

    //skilltype
    String SKILL_TTS = "tts";
    String SKILL_MUSIC = "music";
    String SKILL_WEATHER = "weather";
    String SKILL_GUIDE = "gps";
    String SKILL_RADIO = "radio";
    String SKILL_SETTINGS = "control";
    String SKILL_VOLUME = "volume";
    String SKILL_RECORD = "record";
    String SKILL_NEWS = "news";

    //skill result
    int SKILL_SUCCESS_CODE = 0;
    int SKILL_INVALID_CODE = -1;//技能无效
    int SKILL_NO_EXCUTE_CODE = -2;//技能没被执行
    String SKILL_SUCCESS = "success";
    String SKILL_FAILED = "failed";

    String ACTION_JSON = "action_json";
    String ACTION_POSITION = "action_position";

    String WINDOW_BEAN = "window_bean";

    //event
    String EVENT_REFRESH_SKILL = "event_refresh_skill";

    String SKILL_REQUEST_CODE = "skill_request_code";

    String PACKAGE_NAME = "com.xiaoma.launcher";
    String SKILL_CLASS_NAME = "com.xiaoma.vrpractice.ui.AddSkillActivity";

    /**
     * FM/AM 硬件系统中 FM和AM都统一使用 kHz为单位，
     * 所以FM的一般单位 MHz 需要乘以1000来使用，而AM的一般单位本就为kHz
     */
    interface FMAM {
        int TYPE_FM = 1;
        int TYPE_AM = 0;
        int FM_START = 87500;
        int FM_END = 108000;
        int AM_START = 531;
        int AM_END = 1602;
    }

    int MUSIC_AND_RADIO_HISTORY = 0;
    int MUSIC_AND_RADIO_SEARCH = 1;

    String SKILL_LIST = "skill_list";

}
