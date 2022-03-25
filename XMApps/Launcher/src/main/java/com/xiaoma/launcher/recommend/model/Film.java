package com.xiaoma.launcher.recommend.model;

import java.util.List;

/**
 * @Auther: huojie
 * @Date: 2019/1/3 0003 13:14
 * @Description:影院
 */
public class Film {
    /**
     * filmId : 6494
     * imgUrl : https://gw.alicdn.com/tfscom/i4/TB1ozmJnwHqK1RjSZFkXXX.WFXa_.jpg
     * duration :
     * dataType : 1
     * title : 2018日本新片展
     * filmType :
     * actor :
     * director :
     * language :
     * showDate : 2018-12-31
     * filmArea :
     * showCount : 0
     * cinemaCount : 0
     * description :
     * filmScore : 8.5
     * userCount : 0
     * viewPic :
     * filmPics : [{"picUrl":"i2/TB1jyeMnxTpK1RjSZFMXXbG_VXa_.jpg"},{"picUrl":"i2/TB1S.qGnpzqK1RjSZFoXXbfcXXa_.jpg"},{"picUrl":"i3/TB10IWJnyrpK1RjSZFhXXXSdXXa_.jpg"},{"picUrl":"i1/TB1RhqInpzqK1RjSZSgXXcpAVXa_.jpg"},{"picUrl":"i4/TB1wkyBnCzqK1RjSZPxXXc4tVXa_.jpg"}]
     * viewFiles : []
     */

    private String filmId; //影片Id
    private String filmName; //影片名称
    private String filmAttrs; //影片属性
    private String imgUrl; //电影图片
    private String duration; //影片时长
    private String dataType; //数据类型  0：全部；1：正在热映影片；2：即将上映影片
    private String title; //标题
    private String filmType; //影片类型
    private String actor; //主演
    private String director; //导演
    private String language; //影片语言
    private String showDate; //上映时间
    private String filmArea; //影片地区
    private String ticketingRate; //购票率
    private String showCount; //排期数量
    private String cinemaCount; //影院数量
    private String description; //简介
    private String oneWord; //影片简介
    private String filmScore; //评分
    private String review; //影评
    private String shareCont; //分享内容
    private String filmStatus; //影片状态  1即将上映 2正在热映 0其他（下架）
    private String userCount; //想看人数
    private String viewFileld; //片花编号
    private String viewPic; //片花预览图
    private List<FilmPicsBean> filmPics;
    private List<ViewFile> viewFiles;
    private List<Show> shows;

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getFilmAttrs() {
        return filmAttrs;
    }

    public void setFilmAttrs(String filmAttrs) {
        this.filmAttrs = filmAttrs;
    }

    public String getTicketingRate() {
        return ticketingRate;
    }

    public void setTicketingRate(String ticketingRate) {
        this.ticketingRate = ticketingRate;
    }

    public String getOneWord() {
        return oneWord;
    }

    public void setOneWord(String oneWord) {
        this.oneWord = oneWord;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getShareCont() {
        return shareCont;
    }

    public void setShareCont(String shareCont) {
        this.shareCont = shareCont;
    }

    public String getFilmStatus() {
        return filmStatus;
    }

    public void setFilmStatus(String filmStatus) {
        this.filmStatus = filmStatus;
    }

    public String getViewFileld() {
        return viewFileld;
    }

    public void setViewFileld(String viewFileld) {
        this.viewFileld = viewFileld;
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


    public List<ViewFile> getViewFiles() {
        return viewFiles;
    }

    public void setViewFiles(List<ViewFile> viewFiles) {
        this.viewFiles = viewFiles;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public static class FilmPicsBean {
        /**
         * picUrl : i2/TB1jyeMnxTpK1RjSZFMXXbG_VXa_.jpg
         */

        private String picUrl;

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }

    public static class ViewFile{
        private String viewFileld; //片花编号
        private String viewPic; //片花预览图

        public String getViewFileld() {
            return viewFileld;
        }

        public void setViewFileld(String viewFileld) {
            this.viewFileld = viewFileld;
        }

        public String getViewPic() {
            return viewPic;
        }

        public void setViewPic(String viewPic) {
            this.viewPic = viewPic;
        }
    }

    public static class Show{
        private String showCode; //排期编号
        private String showDate; //排期日期
        private String showTime; //排期时间 9:30 为 0930；12:00 为 1200
        private String hallCode; //影厅编号
        private String hallName; //影厅名称
        private String price; //销售价
        private String standPrice; //门市价
        private String filmType; //影片类型  0-2D;1-3D;2-IMAX_2D;3-IMAX_3D
        private String filmAttr; //影片属性
        private String language; //影片语言
        private String filmPk; //万达系统影片ID
        private String endDate; //结束上映时间
        private String activityId;// 活动id

        public String getShowCode() {
            return showCode;
        }

        public void setShowCode(String showCode) {
            this.showCode = showCode;
        }

        public String getShowDate() {
            return showDate;
        }

        public void setShowDate(String showDate) {
            this.showDate = showDate;
        }

        public String getShowTime() {
            return showTime;
        }

        public void setShowTime(String showTime) {
            this.showTime = showTime;
        }

        public String getHallCode() {
            return hallCode;
        }

        public void setHallCode(String hallCode) {
            this.hallCode = hallCode;
        }

        public String getHallName() {
            return hallName;
        }

        public void setHallName(String hallName) {
            this.hallName = hallName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStandPrice() {
            return standPrice;
        }

        public void setStandPrice(String standPrice) {
            this.standPrice = standPrice;
        }

        public String getFilmType() {
            return filmType;
        }

        public void setFilmType(String filmType) {
            this.filmType = filmType;
        }

        public String getFilmAttr() {
            return filmAttr;
        }

        public void setFilmAttr(String filmAttr) {
            this.filmAttr = filmAttr;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getFilmPk() {
            return filmPk;
        }

        public void setFilmPk(String filmPk) {
            this.filmPk = filmPk;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }
    }
}
