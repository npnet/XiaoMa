package com.xiaoma.travel.main.model;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/06
 *     desc   :
 * </pre>
 */
public class MainTabBean {
    private String name;
    private int imageId;

    public MainTabBean(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
