package com.xiaoma.motorcade.common.constants;

import com.xiaoma.model.annotation.PageDescComponent;

/**
 * Created by ZYao.
 * Date ：2019/1/10 0010
 */
public class MotorcadeConstants {
    public static final String HX_TEST_ACCOUNT = "asfsa";

    public static final String TP_FIRST_START_APP = "first_in_app";
    public static final String LOCATION_SHARE_PARAM = "LOCATION_SHARE_PARAM";
    public static final String MOTORCADE_LIST = "查看车队列表";
    public static final String RECEIVE_OTHERS_STATUS = "是否收听其他车主语音开关";


    public static class IMMessageType {
        public static final String LOCATION_SHARE = "motorcade_location_share";
        public static final String LOCATION_OUT = "motorcade_location_out";
    }

    public static class EventTag {
        public static final String OPEN_MAP = "open_map";
        public static final String JUMP_SHARE = "jump_share";
    }

    public static class NormalClick {
        public static final String create = "创建车队";
        public static final String join = "加入车队";
        public static final String shareCommand = "分享口令";
        public static final String teamName = "车队名称";
        public static final String nick = "用户昵称";
        public static final String exit = "退出车队";
    }

    public static class PageDesc {
        public static final String mainActivity = "车队主页面";
        public static final String mainFragment = "主页面";
        public static final String settingActivity = "车队设置页面";
        public static final String motorcadeConference = "会议室页面";
    }

}
