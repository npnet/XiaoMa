package com.xiaoma.motorcade.setting.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.xiaoma.motorcade.common.model.GroupMemberInfo;

/**
 * 简介:
 *
 * @author lingyan
 */
@Entity(foreignKeys = @ForeignKey(entity = GroupMemberInfo.class,
        parentColumns = "id", childColumns = "memberId",
        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE,
        deferred = true))
public class MemberInfo {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long memberId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
