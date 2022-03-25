package com.xiaoma.club.contact.model;


import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/13 0017
 */

public class NewFriendInfo implements Serializable {

    public static final int IS_APPROVED = 7;

    private long id;
    private long createDate;
    private long modifyDate;
    private int state;
    private int messageType;
    private long fromId;
    private String fromName;
    private String fromHxAccount;
    private long toId;
    private String toName;
    private String toHxAccount;
    private int isRead;
    private String frompic;
    private String topic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromHxAccount() {
        return fromHxAccount;
    }

    public void setFromHxAccount(String fromHxAccount) {
        this.fromHxAccount = fromHxAccount;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToHxAccount() {
        return toHxAccount;
    }

    public void setToHxAccount(String toHxAccount) {
        this.toHxAccount = toHxAccount;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getFrompic() {
        return frompic;
    }

    public void setFrompic(String frompic) {
        this.frompic = frompic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isApproved() {
        return messageType == IS_APPROVED;
    }
}
