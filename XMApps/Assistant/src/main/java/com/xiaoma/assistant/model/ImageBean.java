package com.xiaoma.assistant.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/18 0018
 */
public class ImageBean implements Serializable {


    private List<ImagesBean> images;

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * src : http://img.zcool.cn/community/010a7b5c947caba801208f8b027021.jpg@1280w_1l_2o_100sh.jpg
         * thumb : https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1487880235,3505238944&fm=11&gp=0.jpg
         */

        private String src;
        private String thumb;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}
