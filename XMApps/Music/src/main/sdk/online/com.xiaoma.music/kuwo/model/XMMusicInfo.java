package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.quku.MusicInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMMusicInfo extends XMBean<MusicInfo> {
    public XMMusicInfo(MusicInfo musicInfo) {
        super(musicInfo);
    }

    public XMMusic getMusic() {
        final Music music = getSDKBean().getMusic();
        return new XMMusic(music);
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public void setName(String var1) {
        getSDKBean().setName(var1);
    }

    public String getArtist() {
        return getSDKBean().getArtist();
    }

    public void setArtist(String var1) {
        getSDKBean().setArtist(var1);
    }

    public String getAlbum() {
        return getSDKBean().getAlbum();
    }

    public void setAlbum(String var1) {
        getSDKBean().setAlbum(var1);
    }

    public String getPath() {
        return getSDKBean().getPath();
    }

    public void setPath(String var1) {
        getSDKBean().setPath(var1);
    }

    public String getPicPath() {
        return getSDKBean().getPicPath();
    }

    public void setPicPath(String var1) {
        getSDKBean().setPicPath(var1);
    }

    public int getDuration() {
        return getSDKBean().getDuration();
    }

    public void setDuration(int var1) {
        getSDKBean().setDuration(var1);
    }

    public String getTag() {
        return getSDKBean().getTag();
    }

    public void setTag(String var1) {
        getSDKBean().setTag(var1);
    }

    public int getKmark() {
        return getSDKBean().getKmark();
    }

    public void setKmark(String var1) {
        getSDKBean().setKmark(var1);
    }

    public long getRid() {
        return getSDKBean().getRid();
    }

    public void setRid(long var1) {
        getSDKBean().setRid(var1);
    }

    public String getFormat() {
        return getSDKBean().getFormat();
    }

    public void setFormat(String var1) {
        getSDKBean().setFormat(var1);
    }

    public int getHot() {
        return getSDKBean().getHot();
    }

    public void setHot(String var1) {
        getSDKBean().setHot(var1);
    }

    public String getRes() {
        return getSDKBean().getRes();
    }

    public void setRes(String var1) {
        getSDKBean().setRes(var1);
    }

    public boolean isHasMv() {
        return getSDKBean().isHasMv();
    }

    public void setHasMv(String var1) {
        getSDKBean().setHasMv(var1);
    }

    public String getMvQuality() {
        return getSDKBean().getMvQuality();
    }

    public void setMvQuality(String var1) {
        getSDKBean().setMvQuality(var1);
    }

    public String getMinfo() {
        return getSDKBean().getMinfo();
    }

    public void setMinfo(String var1) {
        getSDKBean().setMinfo(var1);
    }

    public int getQuality() {
        return getSDKBean().getQuality();
    }

    public void setQuality(int var1) {
        getSDKBean().setQuality(var1);
    }

    public String getNavi() {
        return getSDKBean().getNavi();
    }

    public void setNavi(String var1) {
        getSDKBean().setNavi(var1);
    }

    public String getTrend() {
        return getSDKBean().getTrend();
    }

    public void setTrend(String var1) {
        getSDKBean().setTrend(var1);
    }

    public String getUploader() {
        return getSDKBean().getUploader();
    }

    public void setUploader(String var1) {
        getSDKBean().setUploader(var1);
    }

    public String getUptime() {
        return getSDKBean().getUptime();
    }

    public void setUptime(String var1) {
        getSDKBean().setUptime(var1);
    }

    public String getAudioid() {
        return getSDKBean().getAudioid();
    }

    public void setAudioid(String var1) {
        getSDKBean().setAudioid(var1);
    }

    public String getFloatAdId() {
        return getSDKBean().getFloatAdId();
    }

    public void setFloatAdId(String var1) {
        getSDKBean().setFloatAdId(var1);
    }

    public void setChargeType(int var1) {
        getSDKBean().setChargeType(var1);
    }

    public int getChargeType() {
        return getSDKBean().getChargeType();
    }
}
