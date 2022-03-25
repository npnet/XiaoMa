package com.xiaoma.launcher.splash.model;

public  class ListBean {

    /**
     * id : 391
     * date : 2019-06-07
     * festival : 端午节
     * greetings : 端午节快乐
     * video : http://www.carbuyin.net/by2/filePath/aea57205-0897-4dc4-8f13-7e47c2b60b69.mp4
     * videoSize : 384
     * videoTime : null
     * md5String : 4ffac8964b49ba826d99cd8ef190eec0
     */

    private int id;
    private String date;
    private String festival;
    private String greetings;
    private String video;
    private int videoSize;
    private String md5String;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public String getMd5String() {
        return md5String;
    }

    public void setMd5String(String md5String) {
        this.md5String = md5String;
    }
}