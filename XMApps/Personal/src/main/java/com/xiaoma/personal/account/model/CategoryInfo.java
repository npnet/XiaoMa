package com.xiaoma.personal.account.model;

/**
 *  @author Gillben
 *  date: 2018/12/03
 *
 *  个人中心首页 分类标签info
 */
public class CategoryInfo {

    private int imageResId;
    private String title;
    private int itemFlag;

    public CategoryInfo(int imageResId, String title, int itemFlag) {
        this.imageResId = imageResId;
        this.title = title;
        this.itemFlag = itemFlag;
    }

    public int getItemFlag() {
        return itemFlag;
    }

    public void setItemFlag(int itemFlag) {
        this.itemFlag = itemFlag;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
