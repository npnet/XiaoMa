package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

public class ConfirmOrderBean implements Serializable {

    /**
     * orderNum : 201902151608093862213
     * cinemaName : 深圳海岸影城
     * orderName : 疯狂的外星人
     * filmName : 疯狂的外星人
     * showDate : 2019-02-15
     * ticketCount : 1
     * voucherValue :
     * orderStatus : 0
     * orderDesc : 下单成功
     * payStatus : 0
     * payDesc : 未支付
     * orderTime : 2019-02-15 16:08:11
     * orderType : 1
     * cinemaId : 8463
     * cinemaCode : 87018331
     * hallName : 5号厅
     * filmId : 6034
     * filmType : 科幻,喜剧
     * proImg : http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_HB1_65240.jpg
     * language : 汉语普通话
     * price : 45.5
     * totalMoney : 45.5
     * payCount : 1
     * payGenCount : 1
     * filmInfoResult : {"filmId":"6034","imgUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_HB1_65240.jpg","duration":"116","filmType":"科幻,喜剧","actor":"黄渤,沈腾,马修·莫里森","director":"宁浩","language":"汉语普通话","showDate":"2019-02-05","filmArea":"cn","showCount":"11578","cinemaCount":"11578","description":"耿浩（黄渤 饰）与一心想发大财的好兄弟大飞（沈腾 饰），经营着各自惨淡的\u201c事业\u201d，然而\u201c天外来客\u201d的意外降临，打破了二人平静又拮据的生活。神秘的西方力量也派出\u201c哼哈二将\u201d在全球搜查外星人行踪。啼笑皆非的跨物种对决，别开生面的\u201c星战\u201d，在中国某海边城市激情上演。","userCount":"0","viewPic":"http://image.189mv.cn/images/vedio/3228649e31c8ba38aa09ca1564215ccf_PIC_25885.jpg","filmPics":[{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64533.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64534.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64535.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64536.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64537.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64538.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64539.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64540.jpg"}]}
     * tickets : []
     */

    private String orderNum;
    private String cinemaName;
    private String orderName;
    private String filmName;
    private String showDate;
    private String ticketCount;
    private String voucherValue;
    private String orderStatus;
    private String orderDesc;
    private String payStatus;
    private String payDesc;
    private String orderTime;
    private String orderType;
    private String cinemaId;
    private String cinemaCode;
    private String hallName;
    private String filmId;
    private String filmType;
    private String proImg;
    private String language;
    private String price;
    private String totalMoney;
    private String payCount;
    private String payGenCount;
    private FilmInfoResultBean filmInfoResult;
    private List<?> tickets;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(String voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayDesc() {
        return payDesc;
    }

    public void setPayDesc(String payDesc) {
        this.payDesc = payDesc;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaCode() {
        return cinemaCode;
    }

    public void setCinemaCode(String cinemaCode) {
        this.cinemaCode = cinemaCode;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }

    public String getProImg() {
        return proImg;
    }

    public void setProImg(String proImg) {
        this.proImg = proImg;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getPayCount() {
        return payCount;
    }

    public void setPayCount(String payCount) {
        this.payCount = payCount;
    }

    public String getPayGenCount() {
        return payGenCount;
    }

    public void setPayGenCount(String payGenCount) {
        this.payGenCount = payGenCount;
    }

    public FilmInfoResultBean getFilmInfoResult() {
        return filmInfoResult;
    }

    public void setFilmInfoResult(FilmInfoResultBean filmInfoResult) {
        this.filmInfoResult = filmInfoResult;
    }

    public List<?> getTickets() {
        return tickets;
    }

    public void setTickets(List<?> tickets) {
        this.tickets = tickets;
    }

    public static class FilmInfoResultBean {
        /**
         * filmId : 6034
         * imgUrl : http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_HB1_65240.jpg
         * duration : 116
         * filmType : 科幻,喜剧
         * actor : 黄渤,沈腾,马修·莫里森
         * director : 宁浩
         * language : 汉语普通话
         * showDate : 2019-02-05
         * filmArea : cn
         * showCount : 11578
         * cinemaCount : 11578
         * description : 耿浩（黄渤 饰）与一心想发大财的好兄弟大飞（沈腾 饰），经营着各自惨淡的“事业”，然而“天外来客”的意外降临，打破了二人平静又拮据的生活。神秘的西方力量也派出“哼哈二将”在全球搜查外星人行踪。啼笑皆非的跨物种对决，别开生面的“星战”，在中国某海边城市激情上演。
         * userCount : 0
         * viewPic : http://image.189mv.cn/images/vedio/3228649e31c8ba38aa09ca1564215ccf_PIC_25885.jpg
         * filmPics : [{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64533.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64534.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64535.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64536.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64537.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64538.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64539.jpg"},{"picUrl":"http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64540.jpg"}]
         */

        private String filmId;
        private String imgUrl;
        private String duration;
        private String filmType;
        private String actor;
        private String director;
        private String language;
        private String showDate;
        private String filmArea;
        private String showCount;
        private String cinemaCount;
        private String description;
        private String userCount;
        private String viewPic;
        private List<FilmPicsBean> filmPics;

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

        public static class FilmPicsBean {
            /**
             * picUrl : http://image.189mv.cn/images/filmlib/3228649e31c8ba38aa09ca1564215ccf_JZV_64533.jpg
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
}
