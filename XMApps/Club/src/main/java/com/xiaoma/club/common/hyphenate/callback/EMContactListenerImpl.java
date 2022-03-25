package com.xiaoma.club.common.hyphenate.callback;

import com.hyphenate.EMContactListener;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.constant.ContactEventTag;

import org.simple.eventbus.EventBus;

/**
 * 联系人监听器，监听联系变化，包括添加好友的申请，对方删除好友的通知, 对方同意好友请求，对方拒绝好友请求。
 */
public class EMContactListenerImpl implements EMContactListener {
    private static final String TAG = "EMContactListenerImpl";

    private static class EMContactListenerImplHolder {
        static final EMContactListenerImpl instance = new EMContactListenerImpl();
    }

    public static EMContactListenerImpl getInstance() {
        return EMContactListenerImplHolder.instance;
    }

    /**
     * 增加联系人时回调此方法
     *
     * @param username 增加的联系人
     */
    @Override
    public void onContactAdded(String username) {
        LogUtil.logI(TAG, "onContactAdded( username: %s )", username);
        EventBus.getDefault().post(username, ContactEventTag.ON_CONTACT_ADDED);
    }

    /**
     * 被删除时回调此方法
     *
     * @param username 删除的联系人
     */
    @Override
    public void onContactDeleted(String username) {
        LogUtil.logI(TAG, "onContactDeleted( username: %s )", username);
        EventBus.getDefault().post(username, ContactEventTag.ON_CONTACT_DELETED);
    }

    /**
     * 收到好友邀请
     *
     * @param username 发起加为好友用户的名称
     * @param reason   对方发起好友邀请时发出的文字性描述
     */
    @Override
    public void onContactInvited(String username, String reason) {
        LogUtil.logI(TAG, "onContactInvited( username: %s, reason: %s )", username, reason);
        EventBus.getDefault().post(username, ContactEventTag.ON_RECEVVE_FRIEND);
    }

    /**
     * 好友请求被同意
     *
     * @param username
     */
    @Override
    public void onFriendRequestAccepted(String username) {
        LogUtil.logI(TAG, "onFriendRequestAccepted( username: %s )", username);
        EventBus.getDefault().post(username, ContactEventTag.ON_REQUEST_AGREE);
    }

    /**
     * 好友请求被拒绝
     *
     * @param username
     */
    @Override
    public void onFriendRequestDeclined(String username) {
        LogUtil.logI(TAG, "onFriendRequestDeclined( username: %s )", username);
    }
}
