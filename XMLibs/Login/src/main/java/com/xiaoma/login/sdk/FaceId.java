package com.xiaoma.login.sdk;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.util.List;

@Table("FaceId")
public enum FaceId {
    FaceId1(1),
    FaceId2(2),
    FaceId3(3),
    FaceId4(4),
    FaceId5(5),
    ;

    @PrimaryKey(AssignType.BY_MYSELF)
    private int value;

    FaceId(int value) {
        this.value = value;
    }

    public static FaceId getAvailableByUsed(List<Integer> usedFaceIds) {
        for (FaceId value : FaceId.values()) {
            if (!usedFaceIds.contains(value.getValue())) {
                return value;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public static FaceId valueOf(int value) {
        for (FaceId faceId : FaceId.values()) {
            if (faceId.getValue() == value) {
                return faceId;
            }
        }
        return null;
    }

    public static boolean isValid(int faceId) {
        for (FaceId face : FaceId.values()) {
            if (face.value == faceId) {
                return true;
            }
        }
        return false;
    }
}
