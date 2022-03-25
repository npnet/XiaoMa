package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

/**
 * 影片bean
 * Created by zhushi.
 * Date: 2018/12/6
 */
public class FilmBean implements Serializable{
    /**
     * filmId : 6120
     * imgUrl : https://gw.alicdn.com/tfscom/i2/TB1oC1FGeuSBuNjy1XcXXcYjFXa_.jpg
     * duration :
     * dataType : 1
     * title : 2018国家大剧院国际歌剧电影展
     * filmType :
     * actor :
     * director :
     * language :
     * showDate : 2018-08-04
     * filmArea :
     * showCount : 0
     * cinemaCount : 0
     * description :
     * filmScore : 8.5
     * userCount : 0
     * viewPic :
     * filmPics : []
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
    private List<?> filmPics;
    private List<?> viewFiles;

    private List<ShowBean> shows;

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

    public List<?> getFilmPics() {
        return filmPics;
    }

    public void setFilmPics(List<?> filmPics) {
        this.filmPics = filmPics;
    }

    public List<?> getViewFiles() {
        return viewFiles;
    }

    public void setViewFiles(List<?> viewFiles) {
        this.viewFiles = viewFiles;
    }

    public List<ShowBean> getShows() {
        return shows;
    }

    public void setShows(List<ShowBean> shows) {
        this.shows = shows;
    }
}
