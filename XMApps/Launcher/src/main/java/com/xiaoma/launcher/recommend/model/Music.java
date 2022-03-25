package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

/**
 * @Auther: huojie
 * @Date: 2019/1/2 0002 15:51
 * @Description:音乐
 */
public class Music extends DatasBean {

    /**
     * songid : 123132
     * src : kuwo
     * name : 大海-张雨生
     * image : http://www.baidu.com
     * mp4url : http://www.baidu.com/dahai.mp4
     */

    private String songid;
    private String src;
    private String name;
    private String image;
    private String mp4url;

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMp4url() {
        return mp4url;
    }

    public void setMp4url(String mp4url) {
        this.mp4url = mp4url;
    }

    @Override
    public String toString() {
        return "Music{" +
                "songid='" + songid + '\'' +
                ", src='" + src + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", mp4url='" + mp4url + '\'' +
                '}';
    }
}
