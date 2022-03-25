package com.xiaoma.event.constant;

/**
 * 事件id
 *
 * @author zs
 * @date 2018/9/13 0013.
 */
public class XMEventKey {

    public static class Launcher {
        public static final String KEY = "1000000";
        public static final String OPEN_ASSISTANT = "1100000";
        public static final String OPEN_NAVI = "1200000";
        public static final String OPEN_XTING = "1300000";
        public static final String OPEN_CLUB = "1400000";
        public static final String CLICK_MORE = "1500000";
        public static final String CLICK_EDIT = "1600000";
        public static final String CLICK_APP = "1700000";
        public static final String CLICK_ADV = "1800000";

        public static final String CLICK_XTING_APP_VIEW_OPERATION = "1010000";
        public static final String CLICK_MERRY_GO_ROUND_NOTIFICATION = "1011000";
        public static final String CLOSE_MERRY_GO_ROUND_NOTIFICATION = "1012000";
        public static final String NOTICE_SHOW = "10010000";
        public static final String NOTICE_CLICK = "10020000";
    }

    public static class Assistant {
        public static final String KEY = "2000000";
        public static final String SEARCH_MUSIC = "2100000";
        public static final String MUSIC_ITEM_CLICK = "2110000";
        public static final String SEARCH_RADIO = "2200000";
        public static final String RADIO_ITEM_CLICK = "2210000";
        public static final String SEARCH_NAVI = "2300000";
        public static final String CLICK_NAVI_ITEM = "2310000";
        public static final String SEARCH_WEATHER = "2400000";
        public static final String SEARCH_ANSWER = "2500000";
        public static final String SEARCH_OPEN_APP = "2600000";
        public static final String SEARCH_OTHER = "2700000";

        public static final String SEARCH_CLICK_VOICE_ICON = "2800000";
        public static final String SEARCH_CLICK_QUESTION_ICON = "2900000";
        public static final String SEARCH_QUIT_ASSISTANT = "2010000";
        public static final String CLICK_LOCATION_INFO = "2021000";
        public static final String SEARCH_LOCATION = "2020000";

        public static final String VOICE_TO_SELECT_MUSIC = "2110000";
        public static final String VOICE_TO_SELECT_RADIO = "2210000";
        public static final String VOICE_TO_SELECT_NAVI = "2310000";
        public static final String VOICE_TO_CLOSE_APP = "2610000";
        public static final String SECOND_VIEW_SHOWING = "2030000";
        public static final String SECOND_VIEW_EXIT = "2031000";
        public static final String VOICE_SEARCH_FLIGHT = "2040000";
        public static final String VOICE_SEARCH_TRAIN = "2050000";
        public static final String CLICK_TO_TRAIN_DETAIL = "2051000";
        public static final String VOICE_TO_TRAIN_DETAIL = "2052000";
        public static final String CLICK_ITEM_TO_CLUB_NEARBY_CAR = "2060000";
        public static final String VOICE_ITEM_TO_CLUB_NEARBY_CAR = "2061000";
        public static final String CLICK_ITEM_TO_CLUB_NEARBY_GROUP = "2070000";
        public static final String VOICE_ITEM_TO_CLUB_NEARBY_GROUP = "2071000";
        public static final String VOICE_SEARCH_OIL = "2080000";
        public static final String VOICE_SEARCH_CONSTELLATION = "2090000";
        public static final String VOICE_SEARCH_LIMIT = "2130000";
        public static final String VOICE_SEARCH_WEATHER = "2140000";
        public static final String VOICE_SEARCH_STOCK = "2150000";
        public static final String VOICE_SEARCH_TRAFFIC = "2160000";
        public static final String VOICE_SEARCH_TIME = "2170000";
        public static final String VOICE_SEARCH_DATE = "2171000";
        public static final String VOICE_TRANSLATE = "2172000";
        public static final String KUWO_PLAY_INFO = "2173000";
    }

    public static class XTing {
        public static final String KEY = "3000000";
        public static final String PROGRAM_PAGE = "3100000";
        public static final String PROGRAM_CATEGORY_PAGE = "3110000";
        public static final String PROGRAM_SECOND_CATEGORY = "3120000";
        public static final String PROGRAM_DETAIL_PAGE = "3111000";
        public static final String PROGRAM_DETAIL_SWITCH_PLAY_MODEL = "3111100";
        public static final String PROGRAM_DETAIL_DO_FAVORITE = "3111200";
        public static final String PROGRAM_DETAIL_OPEN_LIST = "3111300";
        public static final String PROGRAM_DETAIL_OPEN_LIST_SELECTE_PROGRAM = "3111310";
        public static final String PROGRAM_DETAIL_SWITCH_PLAY_STATE = "3111400";
        public static final String PROGRAM_DETAIL_LAST_PROGRAM = "3111500";
        public static final String PROGRAM_DETAIL_QUIT = "3111600";

        public static final String PROGRAM_DETAIL_NEXT_PROGRAM = "3111700";
        //
        public static final String BROADCAST_PAGE = "3200000";
        public static final String BROADCAST_CATEGORY_PAGE = "3210000";
        public static final String BROADCAST_PROVINCE_PAGE = "3212000";
        public static final String BROADCAST_DETAIL_PAGE = "3211000";
        public static final String BROADCAST_DETAIL_DO_FAVORITE = "3211100";
        public static final String BROADCAST_DETAIL_OPEN_LIST = "3211200";

        public static final String BROADCAST_DETAIL_QUIT = "3211300";
        //
        public static final String FAVORITE_PAGE = "3300000";
        public static final String FAVORITE_CLICK = "3310000";
        public static final String NOTIFICATION_CLICK = "3400000";
        public static final String SHORT_CUT_PLAY = "3500000";

        //想听新版需要的打点事件
        public static final String MAIN_PAGE = "3610000";
        public static final String CHANGE_HOT_PGC = "3611000";
        public static final String PLAY_HOT_PGC = "3612000";
        public static final String PLAY_SUBSCRIBE_PROGRAM = "3613000";
        public static final String SUBSCRIBE_PAGE = "3620000";
        public static final String CATEGOFY_FIRST_PAGE = "3630000";
        public static final String CATEGOFY_SECONDE_PAGE = "3631000";
        public static final String BROADCAST_FIRST_PAGE = "3640000";
        public static final String BROADCAST_SECOND_PAGE = "3641000";
        public static final String BROADCAST_PROVINCES_PAGE = "3642000";
        public static final String PLAY_PAGE = "3650000";
        public static final String PRE_AUDIO = "3661000";
        public static final String NEXT_AUDIO = "3662000";
        public static final String PLAY_PAUSE_AUDIO = "3663000";
        public static final String CHANGE_PLAY_MODE = "3664000";
        public static final String SUBSCRIBE_PROGRAM = "3670000";
        public static final String UNSUBSCRIBE_PROGRAM = "3671000";
        public static final String CLICK_RIGHT_LIST_ITEM = "3651000";
        public static final String CLICK_SHARE_MUSIC = "3680000";


    }

    public static class Club {
        public static final String KEY = "4000000";
        public static final String DISCOVERY_PAGE = "4100000";
        public static final String NEARBY_CAR_PAGE = "4110000";
        public static final String NEARBY_CAR_FILTER = "4111000";
        public static final String NEARBY_CAR_LOOK_ALL = "4112000";
        public static final String NEARBY_CAR_ADD_FRIEND = "4113000";
        public static final String NEARBY_CAR_START_CONVERSATION = "4114000";
        public static final String NEARBY_CAR_QUIT = "4115000";

        public static final String NEARBY_GROUP_PAGE = "4120000";
        public static final String NEARBY_GROUP_JOIN_GROUP = "4121000";
        public static final String NEARBY_GROUP_QUIT = "4122000";

        public static final String SEARCH_PAGE = "4130000";
        public static final String SEARCH_SEARCH = "4131000";
        public static final String SEARCH_BY_VOICE = "4132000";
        public static final String SEARCH_START_CONVERSATION = "4133000";
        public static final String SEARCH_ADD_FRIEND = "4134000";
        public static final String SEARCH_JOIN_GROUP = "4135000";
        public static final String SEARCH_QUIT = "4136000";
        public static final String SEARCH_VOICE_CALL = "4137000";


        public static final String TALK_SHOW_PAGE = "4200000";
        public static final String TALK_SHOW_TAB = "4210000";
        public static final String TALK_SHOW_DETAIL = "4211000";
        public static final String TALK_SHOW_AUTO_PLAY = "4211100";
        public static final String TALK_SHOW_ITEM_PLAY = "4211200";
        public static final String TALK_SHOW_ARGEEN_CLICK = "4211300";
        public static final String TALK_SHOW_PLAYING_REPLY = "4211400";
        public static final String TALK_SHOW_REPLY_TALK_SHOW = "4211500";
        public static final String REPLY_CLICK_FINISH_BUTTUN = "4211510";
        public static final String REPLY_CLICK_FINISH_BUTTUN_PLAY = "4211511";
        public static final String REPLY_CLICK_FINISH_BUTTUN_SEND = "4211512";
        public static final String REPLY_CLICK_FINISH_BUTTUN_CANCLE = "4211513";
        public static final String REPLY_CLICK_FINISH_BUTTON_RESULT = "4211514";
        public static final String TALK_SHOW_SHARE_TALK_SHOW = "4211600";
        public static final String TALK_SHOW_SHARE_TALK_SHOW_SELECTE_CONTACT = "4211610";
        public static final String TALK_SHOW_SHARE_TALK_SHOW_SC_QUIT_BY_SHARE = "4211611";
        public static final String TALK_SHOW_SHARE_TALK_SHOW_TO_CONTACT_LIST = "4211620";
        public static final String TALK_SHOW_SHARE_TALK_SHOW_SEARCH_BY_VOICE = "4211630";
        public static final String TALK_SHOW_SHARE_TALK_SHOW_QUIT = "4211640";


        public static final String DAILY_TOPIC_TAB = "4220000";
        public static final String DAILY_TOPIC_DETAIL = "4221000";
        public static final String DAILY_TOPIC_DETAIL_PLAY_TITLE = "4221100";
        public static final String DAILY_TOPIC_DETAIL_PUBLIC_RED = "4221200";
        public static final String DAILY_TOPIC_DETAIL_PUBLIC_BLUE = "4221300";
        public static final String DAILY_TOPIC_DETAIL_AURO_PLAY = "4221400";
        public static final String DAILY_TOPIC_DETAIL_SELECTE_ITEM_PLAY = "4221500";
        public static final String DAILY_TOPIC_DETAIL_ARGEEN_CLICK = "4221600";
        public static final String DAILY_TOPIC_DETAIL_QUIT = "4221700";


        public static final String SEND_TALK_SHOW = "4230000";

        public static final String MY_TALK_SHOW_LIST = "4240000";
        public static final String MY_TALK_SHOW_LIST_TALK_SHOW_DETAIL = "4241000";
        public static final String MY_TALK_SHOW_LIST_PLAY = "4242000";
        public static final String MY_TALK_SHOW_LIST_QUIT = "4243000";
        public static final String MY_TALK_SHOW_LIST_DELETE = "4244000";
        public static final String LISTENER_TALK_SHOW = "4250000";
        public static final String PLAY_TALK_SHOW = "4260000";


        public static final String LIVE_SHOW_PAGE = "4300000";
        public static final String LIVE_SHOW_DETAIL = "4310000";
        public static final String LIVE_SHOW_REPLY = "4311000";
        public static final String LIVE_SHOW_SHARE = "4312000";
        public static final String LIVE_SHOW_PLAY = "4313000";
        public static final String LIVE_SHOW_AUTO_PLAY_REPLY = "4314000";
        public static final String LIVE_SHOW_SELECTE_PLAY_REPLY = "4315000";
        public static final String LIVE_SHOW_CLICK_AGREE = "4316000";
        public static final String LIVE_SHOW_QUIT = "4317000";
        public static final String LIVE_SHOW_DELETE = "4318000";
        public static final String LIVE_SHOW_CANNEL = "4320000";
        public static final String LIVE_SHOW_CANNEL_SUBSCIBE = "4321000";
        public static final String LIVE_SHOW_CANNEL_JOIN = "4322000";
        public static final String LIVE_SHOW_CANNEL_CANCLE_SEARCH = "4323000";
        public static final String LIVE_SHOW_CANNEL_SEARCH_BY_VOICE = "4324000";
        public static final String LIVE_SHOW_CANNEL_QUIT = "4325000";


        public static final String CONVERSATION_PAGE = "4400000";
        public static final String PERSON_INFO_PAGE = "4410000";
        public static final String PERSON_INFO_CHANGE_HEAD_ICON = "4411000";
        public static final String PERSON_INFO_CHANGE_HEAD_ICON_SELECTE_CLASSICS = "4411100";
        public static final String PERSON_INFO_CHANGE_HEAD_ICON_BY_LOCAL = "4411200";
        public static final String PERSON_INFO_CHANGE_USER_NAME = "4412000";
        public static final String PERSON_INFO_CHANGE_SEX = "4413000";
        public static final String PERSON_INFO_CHANGE_AGE = "4414000";
        public static final String PERSON_INFO_CHANGE_CAR_TYPE = "4415000";
        public static final String PERSON_INFO_CHANGE_CAR_NUMBER = "4416000";
        public static final String PERSON_INFO_CLICK_REMOVE = "4417000";
        public static final String PERSON_INFO_QUIT = "4418000";


        public static final String CONTACT_PAGE = "4420000";
        public static final String CONTACT_SEARCH_FRIEND = "4421000";
        public static final String CONTACT_SEARCH_FRIEND_BY_VOICE = "4421200";
        public static final String CONTACT_CLICK_NEW_FRIEND = "4422000";
        public static final String CONTACT_CLICK_NEW_FRIEND_ARGEE = "4422100";
        public static final String CONTACT_CLICK_NEW_FRIEND_QUIT = "4422200";
        public static final String CONTACT_CLICK_GROUP = "4423000";
        public static final String CONTACT_CLICK_GROUP_START_CHAT = "4423100";
        public static final String CONTACT_CLICK_GROUP_QUIT = "4423200";
        public static final String CONTACT_SEARCH_FRIEND_BY_FIRST = "4426000";
        public static final String CONTACT_QUIT = "4427000";

        public static final String CREATE_CONVERSATION = "4430000";
        public static final String CREATE_CONVERSATION_CLICK_SURE = "4433000";
        public static final String CREATE_CONVERSATION_QUIT = "4434000";

        public static final String CHAT_PAGE = "4440000";
        public static final String CHAT_PAGE_ENTER_SHARE = "4441000";
        public static final String CHAT_PAGE_SHARE_CONTENT = "4441100";
        public static final String CHAT_PAGE_SEND_PERSON_INFO = "4442000";
        public static final String CHAT_PAGE_SEND_IMAGE = "4443000";
        public static final String CHAT_PAGE_VOICE_CALL = "4444000";

        public static final String CHAT_PAGE_SEND_LOCATION = "4445000";
        public static final String CHAT_PAGE_SEND_LOCATION_MOVE_MAP = "4445100";
        public static final String CHAT_PAGE_SEND_LOCATION_CLICK_RESTORATION = "4445200";
        public static final String CHAT_PAGE_SEND_LOCATION_SEARCH_BY_VOICE = "4445300";
        public static final String CHAT_PAGE_SEND_LOCATION_SEND_LOCATION = "4445400";
        public static final String CHAT_PAGE_SEND_LOCATION_QUIT = "4445500";

        public static final String CHAT_PAGE_SHARE_LOCATION = "4446000";
        public static final String CHAT_PAGE_SHARE_LOCATION_CLICK_FRIEND_ICON = "4446100";
        public static final String CHAT_PAGE_SHARE_LOCATION_CLICK_RESTORATION = "4446200";
        public static final String CHAT_PAGE_SHARE_LOCATION_SEND_VOICE = "4446300";
        public static final String CHAT_PAGE_SHARE_LOCATION_QUIT = "4446400";
        public static final String CHAT_PAGE_SHARE_LOCATION_OVER = "4446500";
        public static final String CHAT_GROUP_SETTING_PAGE = "4447000";
        public static final String CHAT_GROUP_SETTING_ADD_GROUP_TAG = "4447100";
        public static final String CHAT_GROUP_SETTING_ADD_GROUP_MEMBER = "4447200";
        public static final String CHAT_GROUP_SETTING_CHECK_ALL_MEMBER = "4447300";
        public static final String CHAT_GROUP_SETTING_CHANGE_GROUP_NAME = "4447400";
        public static final String CHAT_GROUP_SETTING_AUTO_PLAY = "4447500";
        public static final String CHAT_GROUP_SETTING_SAVE_TO_CONTACT = "4447600";
        public static final String CHAT_GROUP_SETTING_SET_TOP_CAR = "4447700";
        public static final String CHAT_GROUP_SETTING_QUIT_AND_DELETE = "4447800";
        public static final String CHAT_GROUP_SETTING_CLICK_MEMBER_HEAD = "4447900";
        public static final String CHAT_GROUP_SETTING_QUIT = "4447010";
        public static final String CHAT_SINGLE_SETTING_PAGE = "4448000";
        public static final String CHAT_SINGLE_SETTING_CREATE_GROUP = "4448100";
        public static final String CHAT_SINGLE_SETTING_CLICK_FRIEND_HEAD = "4448200";
        public static final String CHAT_SINGLE_SETTING_CHECK_PHOTO = "4448300";
        public static final String CHAT_SINGLE_SETTING_ATTO_PLAY = "4448400";
        public static final String CHAT_SINGLE_SETTING_QUIT_AND_DELETE = "4448500";
        public static final String CHAT_SINGLE_SETTING_QUIT = "4448600";
        public static final String CHAT_CHECK_LOCATION = "4449000";
        public static final String CHAT_CHECK_LOCATION_SHART_NAVI = "4449100";
        public static final String CHAT_CHECK_LOCATION_QUIT = "4449200";
        public static final String CHAT_VOICE_CALL = "4440200";
        public static final String CHAT_VOICE_CALL_HANG_UP = "4440210";
        public static final String CHAT_VOICE_CALL_EVALUATE = "4440220";
        public static final String CHAT_VOICE_CALL_ANSWER = "4440230";
        public static final String CHAT_VOICE_CALL_REJECT = "4440240";


        public static final String CHAT_DELETE_CHAT = "4450000";


        public static final String MORE_PAGE = "4500000";
        public static final String SAFE_DRIVE_RANK_PAGE = "4510000";
        public static final String SAFE_DRIVE_RANK_QUIT = "4511000";
        public static final String TALK_SHOW_RANK_PAGE = "4520000";
        public static final String TALK_SHOW_RANK_QUIT = "4521000";
        public static final String FUEL_SAVING_RANK_PAGE = "4530000";
        public static final String FUEL_SAVING_RANK_QUIT = "4531000";
        public static final String AD_PAGE = "4540000";
        public static final String AD_QUIT = "4541000";
        public static final String NOTIFICATION_PAGE = "4550000";
        public static final String NOTIFICATION_QUIT = "4551000";
        public static final String SATISFACTION_PAGE = "4560000";
        public static final String SUBMIT_SATISFACTION = "4561000";
        public static final String SUBMIT_SATISFACTION_QUIT = "4562000";

        public static final String NEARBYY_GROUP_FILTER = "4123000";
        public static final String NEARBYY_GROUP_FILTER_CLEAR = "4124000";
        public static final String LIVE_SHOW_PLAY_OR_PAUSE = "4319000";
        public static final String LIVE_SHOW_NEXT = "4319010";
        public static final String LIVE_SHOW_PRE = "4319020";
        public static final String LIVE_SHOW_OPEN_LIST = "4319030";
        public static final String LIVE_SHOW_SELECTE_LIST_ITEM = "4319040";
        public static final String CLICK_OPEN_LIVE_SHOW = "4330000";
        public static final String QUICKLY_OPEN_LIVE_SHOW = "4330100";
        public static final String OPEN_RED_PACKET_PAGE = "4419000";
        public static final String SET_PRIVATE = "4447020";
        public static final String NO_DISTURBING = "4448700";
        public static final String SEND_NORMAL_RED_PACKET = "4460000";
        public static final String SEND_LOCATION_RED_PACKET = "4461000";
        public static final String LAST_LOCATION_RED_PACKET = "4461100";
        public static final String RED_PACKET_MAP = "4470000";
        public static final String RED_PACKET_MAP_CLICK = "4471000";
        public static final String CLICK_NORMAL_RED_PACKET = "4480000";
        public static final String CLICK_LOCATION_RED_PACKET = "4490000";
        public static final String NAVI_TO_LOCATION_RED_PACKET = "4491000";


    }

    public static class APP {
        public static final String KEY = "5000000";
        public static final String APP_PAGE = "5100000";
        public static final String OPEN_APP = "5110000";
        public static final String DOWNLOAD_APP = "5120000";
        public static final String UPDATE_APP = "5130000";
        public static final String INSTALL_APP = "5140000";
        public static final String APP_DETAIL_PAGE = "5150000";
        public static final String APP_DETAIL_OPEN_APP = "5151000";
        public static final String APP_DETAIL_DOWNLOAD_APP = "5152000";
        public static final String APP_DETAIL_UPDATE_APP = "5153000";
        public static final String APP_DETAIL_INSTALL_APP = "5154000";
        public static final String APP_DETAIL_QUIT = "5155000";

        public static final String LIGHT_APP_PAGE = "5200000";
        public static final String OPEN_LIGHT_APP = "5210000";
        public static final String LIGHT_APP_LAST_PAGE = "5211000";
        public static final String LIGHT_APP_NEXT_PAGE = "5212000";
        public static final String LIGHT_APP_GO_HOME = "5213000";
        public static final String LIGHT_APP_REFLRUSH = "5214000";
        public static final String LIGHT_APP_QUIT = "5215000";
        public static final String LIGHT_APP_DETAIL_PAGE = "5220000";
        public static final String LIGHT_APP_DETAIL_OPEN = "5221000";
        public static final String LIGHT_APP_DETAIL_QUIT = "5222000";

        public static final String DOWNLOAD_PAGE = "5300000";
        public static final String DOWNLOAD_CONTUIN_DOWNLOAD_APP = "5310000";
        public static final String DOWNLOAD_CANCLE_DOWNLOAD_APP = "5320000";
        public static final String DOWNLOAD_PAUSE_DOWNLOAD_APP = "5330000";
        public static final String DOWNLOAD_INSTALL_APP = "5340000";
        public static final String DOWNLOAD_DELETD_APP = "5350000";
        public static final String DOWNLOAD_QUIT = "5360000";
    }

    public static class ThemeMall {
        public static final String THEME_MALL = "9200000";
        public static final String SUBJECT_TO_VIEW_MORE_OF_THE_SKIN = "9211000";
        public static final String THEME_FOR_MORE_SOUND = "9212000";
        public static final String CLICK_INTO_THE_SKIN = "9213000";
        public static final String SOUND_PURCHASE_WAY = "9214000";
        public static final String SWITCH_VOICE = "9215000";
        public static final String EXIT_THE_THEME_MALL = "9216000";
        public static final String VOICE_AUDITION = "9217000";
        public static final String SKIN_THEME_PAGE = "9231000";
        public static final String SKIN_THEME_BUY_WAY = "9231100";
        public static final String SKIN_THEME_DOWNLOAD_SUCCESS = "9231200";
        public static final String SKIN_THEME_DOWNLOAD_FAILED = "9231300";
        public static final String SWITCH_SKIN_THEME_SUCCESS = "9231400";
        public static final String SWITCH_SKIN_THEME_FAILED = "9231500";
        public static final String EXIT_SKIN_THEME_DETAIL = "9231600";
        public static final String EXIT_SKIN_THEME = "9232000";


    }

    public static class IntegralAccount {
        public static final String INTEGRAL_ACCOUNT = "9300000";
        public static final String THE_TASK_LIST = "9311000";
        public static final String INTEGRAL_RULES = "9312000";
        public static final String EXIT_THE_INTEGRAL_RULES = "9312100";
        public static final String VIEW_POINTS_RECORD = "9313000";
        public static final String EXIT_POINTS_RECORD = "9313100";
        public static final String DAILY_CHECK = "9314000";
        public static final String EXIT_POINTS_ACCOUNT = "9315000";
        public static final String CHECK_TASK_DETAIL = "9315000";

    }

    public static class Service {
        public static final String KEY = "6000000";
        public static final String SECOND_CAR_PAGE = "6100000";
        public static final String SECOND_CAR_BUY = "6110000";
        public static final String SECOND_CAR_APPOINTMENT_LIST = "6120000";
        public static final String SECOND_CAR_QUIT = "6130000";


        public static final String CAR_REPAIR_PAGE = "6200000";
        public static final String CAR_REPAIR_BUY = "6210000";
        public static final String CAR_REPAIR_APPOINTMENT_LIST = "6220000";
        public static final String CAR_REPAIR_QUIT = "6230000";


        public static final String ASSISTANT_PAGE = "6300000";
        public static final String ASSISTANT_WANT = "6310000";
        public static final String ASSISTANT_APPOINTMENT_LIST = "6320000";
        public static final String ASSISTANT_QUIT = "6330000";

        public static final String CONSULT_PAGE = "6400000";
        public static final String CAR_CONSUEL = "6410000";
        public static final String CAR_SERVICE = "6420000";
        public static final String CAR_SERVICE_QUIT = "6430000";


        public static final String FOUR_S_LIST = "6500000";
        public static final String FOUR_S_LIST_NAVI = "6510000";
        public static final String FOUR_S_LIST_CHECK_CUNSELOR = "6520000";
        public static final String FOUR_S_LIST_CHECK_CUNSELOR_HEAD_ICON = "6521000";
        public static final String FOUR_S_LIST_CHECK_CUNSELOR_PAGE_QUIT = "6521100";
        public static final String FOUR_S_LIST_CHECK_CUNSELOR_START_CHAT = "6522000";
        public static final String FOUR_S_LIST_CHECK_CUNSELOR_VOICE_CALL = "6523000";
        public static final String CHAT_PAGE_CHECK_CUNSELOR_CARD = "6522100";
        public static final String CHAT_PAGE_CHECK_CUNSELOR_CARD_GRADE = "6522110";
        public static final String CHAT_PAGE_CHECK_CUNSELOR_CARD_NAVI = "6522120";
        public static final String CHAT_PAGE_CHECK_CUNSELOR_CARD_QUIT = "6522130";
        public static final String CHAT_PAGE_CHECK_CUNSELOR_CARD_CAll = "6522140";
        public static final String CHAT_PAGE_SEND_VOICE = "6522200";
        public static final String CHAT_PAGE_QUIT = "6522300";
        public static final String FOUR_S_LIST_PAGE_QUIT = "6530000";

        public static final String FLOW_CONSULT_PAGE = "6600000";

        public static final String SEND_LOCATION_MESSAGE = "6522400";
        public static final String SEARCH_POI_FROM_MAP = "6522410";
        public static final String SEARCH_POI_FROM_VOICE = "6522430";
        public static final String SEND_LOCATION_PAGE_RESTORATION = "6522420";
        public static final String SEND_LOCATION = "6522440";
        public static final String EXIT_SEND_LOCATION = "6522450";
        public static final String START_SHARE_LOCATION = "6522500";
        public static final String SHARE_LOCATION_CLICK_HEAD = "6522510";
        public static final String SHARE_LOCATION_CLICK_RESTORATION = "6522520";
        public static final String SHARE_LOCATION_SEND_VOICE = "6522530";
        public static final String SHARE_LOCATION_EXIT = "6522540";
        public static final String SHARE_LOCATION_CLOSE = "6522550";
        public static final String CLICK_APPOINTANT_NAVI = "6522600";
        public static final String CLICK_LOCATION_MESSAGE = "6522700";
        public static final String DELETE_CONVERSATION = "6524000";


    }

    public static class FactoryActivities {
        public static final String KEY = "7000000";
        public static final String ADV_PAGE = "7100000";
        public static final String ADV_PAGE_QUIT = "7110000";
        public static final String NOTIFY_PAGE = "7200000";
        public static final String NOTIFY_PAGE_QUIT = "7210000";
        public static final String QUESTION_PAGE = "7300000";
        public static final String QUESTION_PAGE_QUIT = "7310000";
        public static final String CLICK_QUESTION_ITEM = "7320000";
        public static final String FINISH_QUESTION = "7330000";
        public static final String CANCLE_QUESTION = "7340000";

    }

    public static class Personal {
        public static final String PERSONAL_CENTER = "9100000";
        public static final String PERSONAL_CENTER_EXIT = "9150000";

        public static final String VEHICLE_INFO = "9110000";
        public static final String MODIFY_ENGINE_NUMBER = "9111000";
        public static final String MODIFY_PLATE_NUMBER = "9112000";
        public static final String MODIFY_VIN = "9113000";
        public static final String VEHICLE_INFO_EXIT = "9114000";

        public static final String NAVIGATION_SETTING = "9120000";
        public static final String NAVIGATION_CHOOSE_DEFAULT = "9121000";
        public static final String NAVIGATION_SETTING_EXIT = "9122000";

        public static final String SKIN_SETTING = "9130000";
        public static final String SKIN_THEME = "9131000";
        public static final String SKIN_BUY_THE_WAY = "9131100";
//            public static final String SKIN_DOWNLOAD_SUCCESS = "9131200";
//            public static final String SKIN_DOWNLOAD_FALIED = "9131300";
//            public static final String SKIN_SWITCH_SUCCESS = "9131400";
//            public static final String SKIN_SWITCH_FALIED = "9131500";
//            public static final String SKIN_THEME_EXIT = "9131600";
//            public static final String SKIN_SETTING_EXIT = "9132000";

        public static final String VOICE_SETTING = "9140000";
        public static final String VOICE_BUY_THE_WAY = "9141000";
        public static final String VOICE_SWITCH = "9142000";
        public static final String VOICE_SETTING_EXIT = "9143000";
        public static final String VOICE_LISTEN = "9144000";

        public static final String SELECTED_CAR_BRAND = "10000000";
        public static final String SELECTED_CAR_TYPE = "10000001";

    }

    public static class Common {
        public static final String ADVERTISEMENT_DISPLAY_COUNT = "8200000";
        public static final String ADVERTISEMENT_WTACHING_EXSPENSE = "8300000";
        public static final String MD5_DECRYPTION_SUCCESS = "8500001";
        public static final String MD5_DECRYPTION_FAILED = "8500002";
        public static final String VIEW_DISPLAY_EXSPENSE = "8600000";

        public static final String KWMUSIC_DATA = "8000004";
    }

    public static class ADPage {
        public static final String AD = "9400000";
        public static final String AD_EXIT = "9410000";

        public static final String AD_START_DIALOG = "0";//启动弹框进入广告
        public static final String AD_LAUNCHER = "1";//Launcher页面点击进入广告
        public static final String AD_FACTORY_ACTIVITY = "2";//厂家活动点击进入广告
    }
}
