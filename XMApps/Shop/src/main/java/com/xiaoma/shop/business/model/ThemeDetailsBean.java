package com.xiaoma.shop.business.model;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */
public class ThemeDetailsBean {

    private String description;
    private float size;
    private int downCount;
    private List<String> thumbnails;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public List<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<String> thumbnails) {
        this.thumbnails = thumbnails;
    }
}
