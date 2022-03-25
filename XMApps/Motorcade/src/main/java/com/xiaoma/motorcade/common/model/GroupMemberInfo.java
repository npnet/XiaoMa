package com.xiaoma.motorcade.common.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

@Entity
public class GroupMemberInfo implements Serializable {

    private String nickName; //成员昵称
    private int online;      //是否在线，0：不在线，1：在线
    private String qunRole;
    private String header;
    @PrimaryKey
    @SerializedName("userId")
    private long id;
    private long qunId;
    private String hxAccount;//注意，这个环信id不是车信的环信id！

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getQunRole() {
        return qunRole;
    }

    public void setQunRole(String qunRole) {
        this.qunRole = qunRole;
    }

    public long getId() {
        return id;
    }

    public void setId(long userId) {
        this.id = userId;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getQunId() {
        return qunId;
    }

    public void setQunId(long qunId) {
        this.qunId = qunId;
    }

    public String getHxAccount() {
        return hxAccount;
    }

    public void setHxAccount(String hxAccount) {
        this.hxAccount = hxAccount;
    }
}
