package com.xiaoma.club.common.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiaoma.club.common.model.ClubEventConstants.ClubScoreType.CHECK_GROUP_ACTIVITY;
import static com.xiaoma.club.common.model.ClubEventConstants.ClubScoreType.SEND_GROUP_MESSAGE;

/**
 * Author: loren
 * Date: 2019/2/15 0015
 */

public interface ClubEventConstants {

    interface PageDescribe {
        String mainActivity = "主页面";
        String mainFragment = "主页面";
        String groupActivity = "部落活动页面:";
        String discoveryFragment = "发现";
        String msgFragment = "消息";
        String contactFragment = "通讯录";
        String contactGroupFragment = "部落通讯录页面";
        String groupConversationFragment = "部落消息页面";
        String friendConversationFragment= "车友消息页面";
        String searchHistoryAFragment = "搜索历史页面";
        String searchActivity = "搜索页面";
        String groupDetailMainAFragment = "部落详情主页面";
        String sendFaceFragment = "发送表情页面";
        String settingDrawerFragment = "个人设置页面";
        String contactFriendFragment = "车友通讯录页面";
        String chatActivity = "聊天页面";
        String groupChatOptFragment = "部落聊天选择页面";
        String singleChatOptFragment = "车友聊天选择页面";
        String sendLocationActivity = "发送定位页面";
    }


    interface NormalClick {
        String activityClick = "查看部落活动:";
        String discover = "发现";
        String msg = "消息";
        String contact = "通讯录";
        String setting = "设置";
        String deleteGroupConversation = "删除聊天对话";
        String serachHistoryTab = "车信搜索历史标签";
        String exitGroup = "退出部落";
        String messageNotifySwitch = "打开/关闭消息通知";
        String sendFace = "发送表情";
        String chatPushSwicth = "私信和部落聊天推送开关";
        String autoPlaySwicth = "语聊自动播放开关";
        String voiceChat = "语音聊天消息";
        String deleteGroup = "删除部落";
        String returnButton = "缩小按钮";
        String groupDetail = "群详情按钮";
        String chatVoiceButton = "聊天语音按钮";
        String faceButton = "表情按钮";
        String positionShareButton = "位置分享";
        String redPacketButton = "发送红包";
        String voiceMsg = "语音消息";
        String faceMsg = "表情消息";
        String positionMsg = "位置消息";
        String redPacketMsg = "红包消息";
        String friendIcon = "车友头像";
        String chatInGroup = "部落会话";
        String chatWithFriend = "车友会话";
        String deleteConversation = "删除会话";
        String groupConctact = "通讯录部落";
        String friendConctact = "通讯录车友";
        String searchNow = "立即搜索";
        String sendLoaction = "发送位置";
    }


    /**
     * 部落热度计分上报场景type，
     * 请与常规打点上报区分
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CHECK_GROUP_ACTIVITY, SEND_GROUP_MESSAGE})
    @interface ClubScoreType {

        //用户查看部落活动
        int CHECK_GROUP_ACTIVITY = 0;
        //用户发送一条部落消息
        int SEND_GROUP_MESSAGE = 1;

    }
}
