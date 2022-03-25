package com.xiaoma.shop.business.model;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 */
public class HologramClothing {

    private String imageUrl;
    private String name;

    public HologramClothing(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
