package com.xiaoma.club.msg.chat.model;


import java.io.Serializable;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class GroupActivityInfo implements Serializable {

    /**
     * "id": 1,
     * "content": "飚起来",
     * "qunId": 1084718113788014592,
     * "createDate": 1540279319000,
     * "modifyDate": null,
     * "channelId": "AA1090",
     * "beginTime": 1540106486000,
     * "endTime": 1540970523000,
     * "linkUrl": null,
     * "enableStatus": "1",
     * "noticeType": "1",
     * "noticePic": "",
     * "onLineFlag": "-1",
     * "noticeName": "飙车活动"
     */

    long id;
    String content;
    long qunId;
    long beginTime;
    long endTime;
    String onLineFlag;
    String enableStatus;
    String noticeType;
    String noticeName;
    String linkUrl;
    String noticePic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getQunId() {
        return qunId;
    }

    public void setQunId(long qunId) {
        this.qunId = qunId;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getOnLineFlag() {
        return onLineFlag;
    }

    public void setOnLineFlag(String onLineFlag) {
        this.onLineFlag = onLineFlag;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeName() {
        return noticeName;
    }

    public void setNoticeName(String noticeName) {
        this.noticeName = noticeName;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getNoticePic() {
        return noticePic;
    }

    public void setNoticePic(String noticePic) {
        this.noticePic = noticePic;
    }
}
