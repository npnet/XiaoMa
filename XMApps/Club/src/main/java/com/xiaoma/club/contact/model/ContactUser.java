package com.xiaoma.club.contact.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import com.xiaoma.model.User;

/**
 * Created by LKF on 2019-2-27 0027.
 */
@Entity(primaryKeys = {"uid", "contactUid"},
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id", childColumns = "contactUid",
                onUpdate = ForeignKey.CASCADE,
                deferred = true))
public class ContactUser {
    private long uid;
    private long contactUid;

    public ContactUser(long uid, long contactUid) {
        this.uid = uid;
        this.contactUid = contactUid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getContactUid() {
        return contactUid;
    }

    public void setContactUid(long contactUid) {
        this.contactUid = contactUid;
    }
}
