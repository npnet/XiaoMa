package com.xiaoma.club.msg.chat.constant;

/**
 * Created by LKF on 2019-1-2 0002.
 */
public interface MessageKey {
    /**
     * 环信消息Id,比如"时间消息"会存储真正的环信消息Id,则采用此字段
     */
    String HX_MSG_ID = "messageId";
    /**
     * 消息类型,针对小马的自定义消息类型
     */
    String MSG_TYPE = "messageType";

    /**
     * 用户的昵称
     */
    String USER_NICKNAME = "userNickname";

    interface Voice {
        /**
         * 语音识别文字内容
         */
        String ASR_RESULT = "asrResult";
    }

    interface Location {
        String POI_NAME = "poiName";
    }

    interface Face {
        String FACE_URI = "faceUri";
    }

    interface RedPacket {
        /**
         * 红包ID
         */
        String PACKET_ID = "packetId";

        /**
         * 红包问候语
         */
        String GREETING = "greeting";
        /**
         * 金额
         */
        String MONEY = "money";
        /**
         * 红包个数
         */
        String COUNT = "count";
        /**
         * 位置名称
         */
        String POI_NAME = "poi_name";
        /**
         * 地址
         */
        String POI_ADDRESS = "poi_address";
        /**
         * 纬度
         */
        String LATITUDE = "latitude";
        /**
         * 经度
         */
        String LONGITUDE = "longitude";
        /**
         * 位置红包
         */
        String IS_LOCATION = "isLocationPacket";
        /**
         * 状态
         */
        String OPEN_STATUS = "openStatus";

    }

    interface Share {
        /**
         * 分享来源的应用包名
         */
        String FROM_PACK = "from_package";
        /**
         * 点击消息返回的activity
         */
        String BACK = "back_action";
        /**
         * 核心内容
         */
        String CORE = "core_key";
        /**
         * 分享的标题
         */
        String TITLE = "share_title";
        /**
         * 分享的内容
         */
        String CONTENT = "share_content";
        /**
         * 分享内容的图片
         */
        String URL = "share_url";
        /**
         * 分享车队的详细信息
         */
        String FROM_DETAIL = "from_detail";
        /**
         * 分享车队的id
         */
        String SHARE_CAR_ID = "share_car_id";

    }
}