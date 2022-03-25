package com.xiaoma.club.msg.chat.constant;

/**
 * Created by LKF on 2018-12-27 0027.
 */
public interface ChatMsgViewType {
    int VIEW_TYPE_NONE = 0;
    int VIEW_TYPE_UNKNOWN_MSG = 1;

    int VIEW_TYPE_TXT_SEND = 2;
    int VIEW_TYPE_TXT_RECEIVE = 3;

    int VIEW_TYPE_VOICE_SEND = 4;
    int VIEW_TYPE_VOICE_RECEIVE = 5;

    int VIEW_TYPE_FACE_SEND = 6;
    int VIEW_TYPE_FACE_RECEIVE = 7;

    int VIEW_TYPE_LOCATION_SEND = 8;
    int VIEW_TYPE_LOCATION_RECEIVE = 9;

    int VIEW_TYPE_RED_PACKET_SEND = 10;
    int VIEW_TYPE_RED_PACKET_RECEIVE = 11;

    int VIEW_TYPE_SHARE_SEND = 12;
    int VIEW_TYPE_SHARE_RECEIVE = 13;

    int VIEW_TYPE_SYSTEM_NOTIFICATION = 14;

    int VIEW_TYPE_MSG_TIME = 15;
}
