package com.xiaoma.vr.model;

/**
 * Created by qiuboxiang on 2019/7/25 12:35
 * Desc:
 */
public class AppState {

    private boolean isMusicForeground;
    private boolean isRadioForeground;
    private boolean isInternetRadioForeground;
    private boolean isNaviForeground = true;
    private boolean isWeixinForeground;
    private boolean isMusicPlaying;
    private boolean isRadioPlaying;
    private boolean isInternetRadioPlaying;
    private @NaviState String naviState = NaviState.noNavi;
    private @RadioType String lastRadioType = RadioType.None;

    public boolean isMusicForeground() {
        return isMusicForeground;
    }

    public void setMusicForeground(boolean musicForeground) {
        isMusicForeground = musicForeground;
    }

    public boolean isRadioForeground() {
        return isRadioForeground;
    }

    public void setRadioForeground(boolean radioForeground) {
        isRadioForeground = radioForeground;
    }

    public boolean isInternetRadioForeground() {
        return isInternetRadioForeground;
    }

    public void setInternetRadioForeground(boolean internetRadioForeground) {
        isInternetRadioForeground = internetRadioForeground;
    }

    public boolean isNaviForeground() {
        return isNaviForeground;
    }

    public void setNaviForeground(boolean naviForeground) {
        isNaviForeground = naviForeground;
    }

    public boolean isWeixinForeground() {
        return isWeixinForeground;
    }

    public void setWeixinForeground(boolean weixinForeground) {
        isWeixinForeground = weixinForeground;
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public void setMusicPlaying(boolean musicPlaying) {
        isMusicPlaying = musicPlaying;
    }

    public boolean isRadioPlaying() {
        return isRadioPlaying;
    }

    public void setRadioPlaying(boolean radioPlaying) {
        isRadioPlaying = radioPlaying;
    }

    public boolean isInternetRadioPlaying() {
        return isInternetRadioPlaying;
    }

    public void setInternetRadioPlaying(boolean internetRadioPlaying) {
        isInternetRadioPlaying = internetRadioPlaying;
    }

    public String getNaviState() {
        return naviState;
    }

    public void setNaviState(String naviState) {
        this.naviState = naviState;
    }

    public String getLastRadioType() {
        return lastRadioType;
    }

    public void setLastRadioType(String lastRadioType) {
        this.lastRadioType = lastRadioType;
    }

}
