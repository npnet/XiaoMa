package com.xiaoma.launcher.main.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author taojin
 * @date 2019/1/2
 */
public class AppModel implements MultiItemEntity {

    private String name;
    private String imgUrl;
    private int itemType = 0;
    private String content;
    private String left;
    private String right;


    @Override
    public int getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
