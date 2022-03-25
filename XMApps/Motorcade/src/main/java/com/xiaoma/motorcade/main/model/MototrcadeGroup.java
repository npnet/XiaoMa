package com.xiaoma.motorcade.main.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.xiaoma.motorcade.common.model.GroupCardInfo;

/**
 * 简介:
 *
 * @author lingyan
 */
@Entity(foreignKeys = @ForeignKey(entity = GroupCardInfo.class,
        parentColumns = "id", childColumns = "carTeamId",
        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE,
        deferred = true))
public class MototrcadeGroup {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long carTeamId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCarTeamId() {
        return carTeamId;
    }

    public void setCarTeamId(long carTeamId) {
        this.carTeamId = carTeamId;
    }
}
