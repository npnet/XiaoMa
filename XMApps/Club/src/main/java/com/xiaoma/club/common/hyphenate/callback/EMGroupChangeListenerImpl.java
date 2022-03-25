package com.xiaoma.club.common.hyphenate.callback;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

/**
 * 群组相关的监听器，侦听群组相关的事件，例如加群申请，希望加入某个群，这个群的群主同意或者拒绝，被踢群等事件。
 */
public class EMGroupChangeListenerImpl implements EMGroupChangeListener {
    private static class EMGroupChangeListenerImplHolder {
        static final EMGroupChangeListenerImpl instance = new EMGroupChangeListenerImpl();
    }

    public static EMGroupChangeListenerImpl getInstance() {
        return EMGroupChangeListenerImplHolder.instance;
    }

    /**
     * 当前用户收到加入群组邀请
     * @param groupId 要加入的群的id
     * @param groupName 要加入的群的名称
     * @param inviter 邀请人的id
     * @param reason 邀请加入的reason
     */
    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        //接收到群组加入邀请
    }

    /**
     * 用户申请加入群
     * @param groupId 要加入的群的id
     * @param groupName 要加入的群的名称
     * @param applyer 申请人的username
     * @param reason 申请加入的reason
     */
    @Override
    public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {
        //用户申请加入群
    }

    /**
     * 加群申请被对方接受
     * @param groupId 	群组的id
     * @param groupName 群组的名字
     * @param accepter 同意人得username
     */
    @Override
    public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
        //加群申请被同意
    }

    /**
     * 加群申请被拒绝
     * @param groupId 群组id
     * @param groupName 群组名字
     * @param decliner 拒绝人得username
     * @param reason 拒绝理由
     */
    @Override
    public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
        //加群申请被拒绝
    }

    /**
     * 群组邀请被接受
     * @param groupId 群组id
     * @param inviter 群组名字
     * @param reason 同意的原因说明
     */
    @Override
    public void onInvitationAccepted(String groupId, String inviter, String reason) {
        //群组邀请被同意
    }

    /**
     * 群组邀请被拒绝
     * @param groupId 群组id
     * @param invitee 群组名字
     * @param reason 拒绝原因
     */
    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {
        //群组邀请被拒绝
    }

    /**
     * 当前登录用户被管理员移除出群组
     * @param groupId 群组id
     * @param groupName 群组名字
     */
    @Override
    public void onUserRemoved (String groupId, String groupName) {

    }

    /**
     * 群组被解散。 sdk 会先删除本地的这个群组，之后通过此回调通知应用，此群组被删除了
     * @param groupId 群组id
     * @param groupName 群组名字
     */
    @Override
    public void onGroupDestroyed (String groupId, String groupName) {

    }

    /**
     * 自动同意加入群组 sdk会先加入这个群组，并通过此回调通知应用 参考com.hyphenate.chat.EMOptions#setAutoAcceptGroupInvitation(boolean value)
     * @param groupId 群组id
     * @param inviter 邀请者
     * @param inviteMessage 邀请信息
     */
    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
        //接收邀请时自动加入到群组的通知
    }

    /**
     * 有成员被禁言，此处不同于blacklist
     * @param groupId 产生禁言的群组id
     * @param mutes 被禁言的成员列表 Map.entry.key 是禁言的成员id，Map.entry.value是禁言动作存在的时间。
     * @param muteExpire 禁言时长
     */
    @Override
    public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
        //成员禁言的通知
    }

    /**
     * 有成员从禁言列表中移除，恢复发言权限，此处不同于blacklist
     * @param groupId 产生禁言的群组id
     * @param mutes 有成员从群组禁言列表中移除
     */
    @Override
    public void onMuteListRemoved(String groupId, final List<String> mutes) {
        //成员从禁言列表里移除通知
    }

    /**
     * 添加成员管理员权限
     * @param groupId 	添加管理员权限对应的群组
     * @param administrator    被添加为管理员的成员
     */
    @Override
    public void onAdminAdded(String groupId, String administrator) {
        //增加管理员的通知
    }

    /**
     * 取消某管理员权限
     * @param groupId 	取消管理员权限事件发生的群id
     * @param administrator 被取消管理员权限的成员
     */
    @Override
    public void onAdminRemoved(String groupId, String administrator) {
        //管理员移除的通知
    }

    /**
     * 转移群组所有者权限
     * @param groupId 	转移群组所有者权限的群id
     * @param newOwner 新的群组所有者
     * @param oldOwner 原群组所有者
     */
    @Override
    public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
        //群所有者变动通知
    }

    /**
     * 群组加入新成员事件
     * @param groupId 群组id
     * @param member 新成员id
     */
    @Override
    public void onMemberJoined(final String groupId, final String member) {
        //群组加入新成员通知
    }

    /**
     * 群组成员主动退出事件
     * @param groupId 群组id
     * @param member 退出的成员的id
     */
    @Override
    public void onMemberExited(final String groupId, final String member) {
        //群成员退出通知
    }

    /**
     * 群公告更改事件
     * @param groupId 群组id
     * @param announcement 更新的公告内容
     */
    @Override
    public void onAnnouncementChanged(String groupId, String announcement) {
        //群公告变动通知
    }

    /**
     * 群组增加共享文件事件
     * @param groupId 群组id
     * @param sharedFile 增加的文件
     */
    @Override
    public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
        //增加共享文件的通知
    }

    /**
     * 群组删除共享文件事件
     * @param groupId 群组id
     * @param fileId 删除文件的id
     */
    @Override
    public void onSharedFileDeleted(String groupId, String fileId) {
        //群共享文件删除通知
    }
}
