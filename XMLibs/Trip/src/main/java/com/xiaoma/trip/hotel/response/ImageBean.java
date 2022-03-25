package com.xiaoma.trip.hotel.response;

import java.io.Serializable;

/**
 * Created by zhushi.
 * Date: 2018/12/6
 */
public class ImageBean implements Serializable {
    private static final long serialVersionUID = 4797337488783236492L;

    /**
     * imageUrl : http://pic.cnbooking.net:10541/upload/2012/02/16/23-12-37-404.131613622.jpg
     * imageId :
     * imageName :
     */

    private String imageUrl;
    private String imageId;
    private String imageName;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
