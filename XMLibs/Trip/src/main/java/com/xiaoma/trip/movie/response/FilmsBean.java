package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

public class FilmsBean implements Serializable{
    /**
     * filmId : 7175
     * imgUrl : http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_HB1_64785.jpg
     * duration : 117
     * dataType : 2
     * title : 22年后的自白
     * filmType : 剧情,悬疑,犯罪
     * actor : 藤原龙也,伊藤英明,夏帆
     * director : 入江悠
     * language : 日语
     * showDate : 2019-01-11
     * filmArea : cn
     * showCount : 0
     * cinemaCount : 0
     * description : 1995年，东京发生了连环绞杀案，五人被害，凶手无影无踪，该案成为悬案。2017年，就在该案早已过了诉讼有效期之时，一位名叫曾根崎雅人（藤原龙也饰）的男子出版了一本《我是杀人犯》的告白书，声称自己是22年前连环杀人案的凶手，并嘲笑警方无能。此举震惊了整个日本社会，面对受害者家属的愤怒，警方却束手无策。作为当年调查案件的刑警，牧村航（伊藤英明饰）多年来从未放弃追捕真凶，对于曾根崎雅人的话他将信将疑，一场激烈的猫鼠游戏在两人之间展开。
     * oneWord : 全民追凶
     * filmScore : 8.2
     * userCount : 0
     * viewPic : http://image.189mv.cn/images/vedio/82bbd6a63d8361ffef4449f7d3e621a7_PIC_25785.jpg
     * filmDetailForm : {"id":"2080","filmId":"7175","filmName":"22年后的自白","dbScore":"","iMDBScore":"","filmScore":"8.2","oneWord":"全民追凶","adminId":"wangn","addTime":"2019-01-02 11:28:22","trailersUrl":"","userCount":"12685","filmLabel":"","filmMark":"","orderImg":"","jumpTypeId":"0","webLink":"","link":""}
     * filmPics : [{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64204.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64205.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64206.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64207.jpg"}]
     * viewFiles : []
     */
    private int status;//收藏状态  0取消收藏，1收藏
    private String filmId;
    private String imgUrl;
    private String duration;
    private String dataType;
    private String title;
    private String filmType;
    private String actor;
    private String director;
    private String language;
    private String showDate;
    private String filmArea;
    private String showCount;
    private String cinemaCount;
    private String description;
    private String oneWord;
    private String filmScore;
    private String userCount;
    private String viewPic;
    private FilmDetailFormBean filmDetailForm;
    private List<FilmPicsBean> filmPics;
    private List<?> viewFiles;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getFilmArea() {
        return filmArea;
    }

    public void setFilmArea(String filmArea) {
        this.filmArea = filmArea;
    }

    public String getShowCount() {
        return showCount;
    }

    public void setShowCount(String showCount) {
        this.showCount = showCount;
    }

    public String getCinemaCount() {
        return cinemaCount;
    }

    public void setCinemaCount(String cinemaCount) {
        this.cinemaCount = cinemaCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOneWord() {
        return oneWord;
    }

    public void setOneWord(String oneWord) {
        this.oneWord = oneWord;
    }

    public String getFilmScore() {
        return filmScore;
    }

    public void setFilmScore(String filmScore) {
        this.filmScore = filmScore;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public String getViewPic() {
        return viewPic;
    }

    public void setViewPic(String viewPic) {
        this.viewPic = viewPic;
    }

    public FilmDetailFormBean getFilmDetailForm() {
        return filmDetailForm;
    }

    public void setFilmDetailForm(FilmDetailFormBean filmDetailForm) {
        this.filmDetailForm = filmDetailForm;
    }

    public List<FilmPicsBean> getFilmPics() {
        return filmPics;
    }

    public void setFilmPics(List<FilmPicsBean> filmPics) {
        this.filmPics = filmPics;
    }

    public List<?> getViewFiles() {
        return viewFiles;
    }

    public void setViewFiles(List<?> viewFiles) {
        this.viewFiles = viewFiles;
    }


    public static class FilmPicsBean implements Serializable{
        /**
         * picUrl : http://image.189mv.cn/images/filmlib/82bbd6a63d8361ffef4449f7d3e621a7_JZV_64204.jpg
         */

        private String picUrl;

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }
}