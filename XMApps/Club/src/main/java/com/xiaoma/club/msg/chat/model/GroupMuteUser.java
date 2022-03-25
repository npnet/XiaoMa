package com.xiaoma.club.msg.chat.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by LKF on 2019-2-25 0025.
 */
@Entity(primaryKeys = {"hxGroupId", "userHxAccount"})
public class GroupMuteUser {
    @NonNull
    private String hxGroupId;
    @NonNull
    private String userHxAccount;

    private boolean isMute;

    public GroupMuteUser(@NonNull String hxGroupId, @NonNull String userHxAccount, boolean isMute) {
        this.hxGroupId = hxGroupId;
        this.userHxAccount = userHxAccount;
        this.isMute = isMute;
    }

    public String getHxGroupId() {
        return hxGroupId;
    }

    public void setHxGroupId(@NonNull String hxGroupId) {
        this.hxGroupId = hxGroupId;
    }

    public String getUserHxAccount() {
        return userHxAccount;
    }

    public void setUserHxAccount(@NonNull String userHxAccount) {
        this.userHxAccount = userHxAccount;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }
}
