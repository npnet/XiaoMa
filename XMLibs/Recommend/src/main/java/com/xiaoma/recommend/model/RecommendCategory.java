package com.xiaoma.recommend.model;

import java.io.Serializable;

/**
 * @author: iSun
 * @date: 2018/12/11 0011
 */
public class RecommendCategory implements Serializable {
    private String id;//资源id
    private String title;
    private int type; //类型：1 电台 2音乐 3美食 4酒店 5景点 6电影 7停车场 8加油站 9 4S店
    private String name;
    private String pic_url; //封面地址
    private String source_url;//音视频地址
    private String reserve1; //预留字段1
    private String reserve2; //预留字段2


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }
}
