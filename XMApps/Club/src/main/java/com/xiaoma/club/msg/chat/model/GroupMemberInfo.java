package com.xiaoma.club.msg.chat.model;


import com.xiaoma.model.User;

import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupMemberInfo extends User implements Serializable {

    private String userRole;
    private boolean isMute;

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }
}
