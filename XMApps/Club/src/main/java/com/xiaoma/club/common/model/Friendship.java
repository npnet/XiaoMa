package com.xiaoma.club.common.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by LKF on 2019-2-22 0022.
 * 好友关系映射
 */
@Entity(primaryKeys = {"hxAccount", "otherHxAccount"})
public class Friendship {
    @NonNull
    private String hxAccount;
    @NonNull
    private String otherHxAccount;

    public Friendship(@NonNull String hxAccount, @NonNull String otherHxAccount) {
        this.hxAccount = hxAccount;
        this.otherHxAccount = otherHxAccount;
    }

    @NonNull
    public String getHxAccount() {
        return hxAccount;
    }

    public void setHxAccount(@NonNull String hxAccount) {
        this.hxAccount = hxAccount;
    }

    @NonNull
    public String getOtherHxAccount() {
        return otherHxAccount;
    }

    public void setOtherHxAccount(@NonNull String otherHxAccount) {
        this.otherHxAccount = otherHxAccount;
    }
}
