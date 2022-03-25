package com.xiaoma.club.msg.chat.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LKF on 2019-1-30 0030.
 * 环信群禁言用户
 */
public class MuteHxUser {
    @SerializedName("expire")
    private long muteExpire;

    @SerializedName("user")
    private String hxAccount;

    public String getHxAccount() {
        return hxAccount;
    }

    public void setHxAccount(String hxAccount) {
        this.hxAccount = hxAccount;
    }

    public long getMuteExpire() {
        return muteExpire;
    }

    public void setMuteExpire(long muteExpire) {
        this.muteExpire = muteExpire;
    }
}
