package com.xiaoma.xting.player.model;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/9
 */
public class LocalFMBean {

    private String fromClock;
    private String toClock;
    private boolean netAvaliableTF;
    private String title;
    private String coverUrl;

    public String getFromClock() {
        return fromClock;
    }

    public void setFromClock(String fromClock) {
        this.fromClock = fromClock;
    }

    public String getToClock() {
        return toClock;
    }

    public void setToClock(String toClock) {
        this.toClock = toClock;
    }

    public boolean isNetAvaliableTF() {
        return netAvaliableTF;
    }

    public void setNetAvaliableTF(boolean netAvaliableTF) {
        this.netAvaliableTF = netAvaliableTF;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
