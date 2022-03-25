package com.xiaoma.club.common.hyphenate.callback;

import com.hyphenate.EMChatRoomChangeListener;

import java.util.List;

/**
 * 聊天室状态监听
 */
public class EMChatRoomChangeListenerImpl implements EMChatRoomChangeListener {
    private static class EMChatRoomChangeListenerImplHolder {
        static final EMChatRoomChangeListenerImpl instance = new EMChatRoomChangeListenerImpl();
    }

    public static EMChatRoomChangeListenerImpl getInstance() {
        return EMChatRoomChangeListenerImplHolder.instance;
    }

    /**
     * 聊天室被解散。
     * @param roomId 聊天室id
     * @param roomName 聊天室名称
     */
    @Override
    public void onChatRoomDestroyed(final String roomId, final String roomName) {

    }

    /**
     * 聊天室加入新成员事件
     * @param roomId 聊天室id
     * @param participant 新成员username
     */
    @Override
    public void onMemberJoined(final String roomId, final String participant) {
    }

    /**
     * 聊天室成员主动退出事件
     * @param roomId 	聊天室id
     * @param roomName 聊天室名字
     * @param participant 退出的成员的username
     */
    @Override
    public void onMemberExited(final String roomId, final String roomName, final String participant) {

    }

    /**
     * 聊天室人员被移除
     * @param reason 被聊天室管理员移除或由于当前设备断网被服务器移出聊天室 com.hyphenate.chat.adapter.EMAChatRoomManagerListener#BE_KICKED com.hyphenate.chat.adapter.EMAChatRoomManagerListener#BE_KICKED_FOR_OFFLINE
     * @param roomId 聊天室id
     * @param roomName 聊天室名字
     * @param participant 被移除人员的username
     */
    @Override
    public void onRemovedFromChatRoom(final int reason, final String roomId, final String roomName, final String participant) {

    }


    /**
     * 有成员被禁言
     * @param chatRoomId 聊天室id
     * @param mutes 禁言的成员
     * @param expireTime 禁言有效期，单位是毫秒。
     */
    @Override
    public void onMuteListAdded(final String chatRoomId, final List<String> mutes, final long expireTime) {

    }

    /**
     * 成员从禁言列表中移除
     * @param chatRoomId 聊天室id
     * @param mutes 从禁言列表中移除的成员名单
     */
    @Override
    public void onMuteListRemoved(final String chatRoomId, final List<String> mutes) {

    }

    /**
     * 有成员提升为管理员权限
     * @param chatRoomId 聊天室id
     * @param admin 提升的管理员
     */
    @Override
    public void onAdminAdded(final String chatRoomId, final String admin) {

    }

    /**
     * 移除管理员权限
     * @param chatRoomId 聊天室id
     * @param admin 被移除的管理员
     */
    @Override
    public void onAdminRemoved(final String chatRoomId, final String admin) {

    }

    /**
     * 转移拥有者
     * @param chatRoomId 聊天室id
     * @param newOwner 新所有者
     * @param oldOwner 原聊天室所有者
     */
    @Override
    public void onOwnerChanged(final String chatRoomId, final String newOwner, final String oldOwner) {

    }

    /**
     * 聊天室公告更改事件
     * @param chatRoomId 聊天室id
     * @param announcement 更新的公告内容
     */
    @Override
    public void onAnnouncementChanged(String chatRoomId, String announcement) {

    }
}
