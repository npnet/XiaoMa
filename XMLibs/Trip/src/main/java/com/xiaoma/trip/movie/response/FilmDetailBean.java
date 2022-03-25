package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

/**
 * 影片详情
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class FilmDetailBean implements Serializable {

    /**
     * filmId : 5734
     * imgUrl : http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_HB1_62150.jpg
     * duration : 112
     * dataType : 2
     * title : 毒液：致命守护者
     * filmType : 科幻,动作,惊悚
     * actor : 汤姆·哈迪,米歇尔·威廉姆斯,里兹·阿迈德
     * director : 鲁本·弗雷斯彻
     * language : 英语
     * showDate : 2018-11-09
     * filmArea : cn
     * showCount : 0
     * cinemaCount : 0
     * description : 2018漫威压轴巨制，蜘蛛侠最强劲敌“毒液”强势来袭！曾主演《敦刻尔克》《盗梦空间》等口碑大片的肌肉型男汤姆·哈迪在本片中饰演“毒液”的宿主–埃迪·布洛克。身为记者的埃迪在调查生命基金会老板卡尔顿·德雷克（里兹·阿迈德饰）的过程中，事业遭受重创，与未婚妻安妮·韦英（米歇尔·威廉姆斯饰）的关系岌岌可危，并意外被外星共生体入侵，历经挣扎对抗，最终成为拥有强大超能力，无人可挡的“毒液”。
     * filmScore : 8.5
     * userCount : 0
     * viewPic : http://image.189mv.cn/images/vedio/b6ae1262a7e5ab21d72c73ec1241f9f5_PIC_25228.jpg
     * filmPics : [{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62174.png"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62175.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62176.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62177.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62178.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62179.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62180.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62181.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62182.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62183.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_63158.jpg"}]
     * viewFiles : []
     */

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
    private String filmScore;
    private String userCount;
    private String viewPic;
    private List<FilmPicsBean> filmPics;
    private List<?> viewFiles;

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

    public static class FilmPicsBean {
        /**
         * picUrl : http://image.189mv.cn/images/filmlib/b6ae1262a7e5ab21d72c73ec1241f9f5_JZV_62174.png
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
