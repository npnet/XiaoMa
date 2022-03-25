package com.xiaoma.club.msg.chat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Author: loren
 * Date: 2019/1/14 0014
 */
@Entity
public class FaceQuickInfo {

    /**
     * "id": 2,
     * "type": "1",//1：表情 ； 2：快速回复语
     * "createDate": 1540279620000,
     * "channelId": "AA1090",
     * "enableStatus": "1",
     * "orderLevel": 1,
     * "expressionUrl": "http://www.carbuyin.net/by2/appImg/1c7149b1-8923-49d4-9968-4a366e7bb0da.png",//表情url
     * "content": ""//快速回复内容
     */

    @PrimaryKey
    long id;
    String type;
    int orderLevel;
    String expressionUrl;
    String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int orderLevel) {
        this.orderLevel = orderLevel;
    }

    public String getExpressionUrl() {
        return expressionUrl;
    }

    public void setExpressionUrl(String expressionUrl) {
        this.expressionUrl = expressionUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
