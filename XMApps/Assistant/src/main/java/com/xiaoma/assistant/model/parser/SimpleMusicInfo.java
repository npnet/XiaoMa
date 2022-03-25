package com.xiaoma.assistant.model.parser;


import com.xiaoma.utils.GsonHelper;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/13
 * Desc:音乐精简model
 */
public class SimpleMusicInfo implements Serializable {


    /**
     * song : 沉默是金
     * artist : 张国荣
     */

    private String song = "";
    private String artist = "";
    private String moreArtist = ""; //合唱者
    private String category = "";
    private String album = "";
    private String source = "";
    private String sourceType = "";
    private String genre = "";
    private String mediaSource = "";
    private String insType = "";
    private String lang = "";
    private String tags = "";
    private String exclude_artist = "";
    private String exclude_song = "";
    private String area = "";

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMoreArtist() {
        return moreArtist;
    }

    public void setMoreArtist(String moreArtist) {
        this.moreArtist = moreArtist;
    }

    public String getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(String mediaSource) {
        this.mediaSource = mediaSource;
    }

    public String getInsType() {
        return insType;
    }

    public void setInsType(String insType) {
        this.insType = insType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getExclude_artist() {
        return exclude_artist;
    }

    public void setExclude_artist(String exclude_artist) {
        this.exclude_artist = exclude_artist;
    }

    public String getExclude_song() {
        return exclude_song;
    }

    public void setExclude_song(String exclude_song) {
        this.exclude_song = exclude_song;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "SimpleMusicInfo{" +
                "song='" + song + '\'' +
                ", artist='" + artist + '\'' +
                ", moreArtist='" + moreArtist + '\'' +
                ", category='" + category + '\'' +
                ", album='" + album + '\'' +
                ", source='" + source + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", genre='" + genre + '\'' +
                ", mediaSource='" + mediaSource + '\'' +
                ", insType='" + insType + '\'' +
                ", lang='" + lang + '\'' +
                ", exclude_artist='" + exclude_artist + '\'' +
                ", exclude_song='" + exclude_song + '\'' +
                '}';
    }

    public static SimpleMusicInfo parseFromJson(String json) {
        return GsonHelper.fromJson(json, SimpleMusicInfo.class);
    }


    public static String toJson(SimpleMusicInfo info) {
        return GsonHelper.toJson(info);
    }


    public boolean isEmpty() {
        return getArtist().isEmpty() && getSong().isEmpty() && getCategory().isEmpty() && getAlbum().isEmpty();
    }
}
