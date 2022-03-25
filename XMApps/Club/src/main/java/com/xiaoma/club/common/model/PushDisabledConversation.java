package com.xiaoma.club.common.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by LKF on 2019-3-29 0029.
 * 禁用消息推送的环信群组或用户
 */
@Entity(primaryKeys = {"userHxId", "disabledHxId"})
public class PushDisabledConversation {
    @NonNull
    private String userHxId;
    @NonNull
    private String disabledHxId;

    public PushDisabledConversation(@NonNull String userHxId, @NonNull String disabledHxId) {
        this.userHxId = userHxId;
        this.disabledHxId = disabledHxId;
    }

    @NonNull
    public String getUserHxId() {
        return userHxId;
    }

    public void setUserHxId(@NonNull String userHxId) {
        this.userHxId = userHxId;
    }

    @NonNull
    public String getDisabledHxId() {
        return disabledHxId;
    }

    public void setDisabledHxId(@NonNull String disabledHxId) {
        this.disabledHxId = disabledHxId;
    }
}
