package com.xiaoma.login.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by kaka
 * on 19-5-19 下午2:43
 * <p>
 * desc: #a
 * </p>
 */
@Entity(tableName = "FaceStore")
public class FaceStore {
    @PrimaryKey
    private int faceId;
    private int face;

    public FaceStore(int faceId, int face) {
        this.faceId = faceId;
        this.face = face;
    }

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }
}
