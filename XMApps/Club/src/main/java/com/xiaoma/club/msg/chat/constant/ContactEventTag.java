package com.xiaoma.club.msg.chat.constant;

/**
 * Created by LKF on 2019-1-3 0003.
 * 联系人变化的事件Tag
 */
public interface ContactEventTag {
    /**
     * fun(String hxAccount)
     */
    String ON_CONTACT_ADDED = "onContactAdded";
    /**
     * fun(String hxAccount)
     */
    String ON_REQUEST_AGREE = "onRequestAgree";
    /**
     * fun(String hxAccount)
     */
    String ON_CONTACT_DELETED = "onContactDeleted";
    /**
     * fun(String hxAccount)
     */
    String ON_RECEVVE_FRIEND = "onReceiveFriend";
}
