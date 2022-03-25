package com.xiaoma.club;

/**
 * Created by LKF on 2018/10/9 0009.
 */
public class ClubConstant {
    public static final String DISCOVERY_SEARCH_KEY = "discovery_search_key";

    public class EventBusConstant {
        public static final String CLEAN_SHAKEN = "clean_shaken_animation";
    }

    public class GroupIdentity {
        //后台确定的用户在群组里的身份标识
        public static final String GROUP_OWNER = "3";
        public static final String GROUP_MANAGER = "2";
        public static final String GROUP_MEMBER = "1";
    }

    public class GroupActivity {
        //后台确定的群组活动的状态标识
        public static final String ACTIVITY_END = "-1";//已结束
        public static final String ACTIVITY_ONGOING = "1";//进行中
        public static final String ACTIVITY_FUTURE = "0";//未开始
    }

    public class Hologram {
        /**
         * 收到红包消息，抢到红包，拆红包后的3D全息动作ID
         */
        public static final int OBTAIN_RED_PACKET_3D_ACTION_ID = 49;
    }
}