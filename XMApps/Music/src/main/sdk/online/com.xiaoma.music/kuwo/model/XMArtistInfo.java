package com.xiaoma.music.kuwo.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.quku.ArtistInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
@SuppressLint("ParcelCreator")
public class XMArtistInfo extends XMBean<ArtistInfo>{
    public XMArtistInfo(ArtistInfo artistInfo) {
        super(artistInfo);
    }

    public String getDigest() {
        return getSDKBean().getDigest();
    }

    public void setDigest(String digest) {
        getSDKBean().setDigest(digest);
    }

    public String getIntro() {
        return getSDKBean().getIntro();
    }

    public void setIntro(String intro) {
        getSDKBean().setIntro(intro);
    }

    public String getArtistPage() {
        return getSDKBean().getArtistPage();
    }

    public void setArtistPage(String page) {
        getSDKBean().setArtistPage(page);
    }

    public int getAlbumCount() {
        return getSDKBean().getAlbumCount();
    }

    public void setAlbumCount(String count) {
        getSDKBean().setAlbumCount(count);
    }

    public void setAlbumCount(int count) {
        getSDKBean().setAlbumCount(count);
    }

    public void setMVCount(String count) {
        getSDKBean().setMVCount(count);
    }

    public void setMVCount(int count) {
        getSDKBean().setMVCount(count);
    }

    public int getMVCount() {
        return getSDKBean().getMVCount();
    }

    public int getMusicCount() {
        return getSDKBean().getMusicCount();
    }

    public void setMusicCount(String count) {
        getSDKBean().setMVCount(count);
    }

    public void setMusicCount(int count) {
        getSDKBean().setMVCount(count);
    }

    public int getRadioId() {
        return getSDKBean().getRadioId();
    }

    public void setRadioId(String id) {
        getSDKBean().setRadioId(id);
    }
}
