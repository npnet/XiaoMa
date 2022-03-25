package com.xiaoma.club.msg.chat.model;

import com.xiaoma.club.contact.model.GroupCardInfo;

/**
 * Author: loren
 * Date: 2019/2/27 0027
 */

public class GroupBasicInfo {

    String userRole;
    GroupCardInfo qun;

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public GroupCardInfo getQun() {
        return qun;
    }

    public void setQun(GroupCardInfo qun) {
        this.qun = qun;
    }
}
