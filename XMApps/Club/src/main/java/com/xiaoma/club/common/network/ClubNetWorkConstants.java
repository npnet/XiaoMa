package com.xiaoma.club.common.network;

/**
 * Author: loren
 * Date: 2019/1/2 0002
 */

public class ClubNetWorkConstants {

    public static final String GET_HOT_GROUP_LIST = "qun/queryRecommendQun";
    public static final String SEARCH_ALL_RESULT_LIST = "qun/queryUserAndQunByFuzzyWord";
    public static final String EDIT_USER_SIGN = "user/updatePersonalSignature";
    public static final String GET_USER_BY_ID = "user/getUserById.action";
    public static final String GET_USER_BY_HXID = "user/getUserFromHxV2.action";
    public static final String GET_GROUP_BY_HXID = "qun/getQunFromGroupIdV2.action";
    public static final String GET_GROUP_MUTE_LIST_HXID = "qun/queryQunMuteList";
    public static final String QUERY_GROUP_BY_ID = "qun/queryQunByRoomId";
    public static final String EDIT_GROUP_SIGN = "qun/updateQunBrief";
    public static final String QUERY_GROUP_MEMBER_BY_ID = "qun/queryQunUserListByRoomId";
    public static final String QUERY_GROUP_ACTIVITY_BY_ID = "qun/getQunActivities";
    public static final String REQUEST_FACE_QUICK_LIST = "qun/getFastRepelys";
    public static final String REQUEST_ADD_GROUP = "qun/addQun";
    public static final String IS_MY_FRIEND = "user/isMyFriend.action";
    public static final String REQUEST_ADD_FRIEND = "user/applyFriend";
    public static final String REQUEST_NEWFRIEND_LIST = "message/getUserMessagesV2.action";
    public static final String APPROVE_NEWFRIEND = "user/agreeFriendApply";
    public static final String KICK_OUT_MEMBER = "qun/deleteUserFromQun";
    public static final String CONTACT_GROUP_LIST = "qun/getMyQunByUserId.action";
    public static final String CONTACT_QUIT_GROUP = "qun/quitQun";
    public static final String CONTACT_FRIEND_LIST = "user/friendListV2.action";
    public static final String CONTACT_DEL_FRIEND = "user/deleteFriend";
    public static final String IS_IN_TEAM = "isInCarTeam";
}
