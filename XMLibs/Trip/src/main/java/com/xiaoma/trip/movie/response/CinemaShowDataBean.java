package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

/**
 * 影院排期
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class CinemaShowDataBean implements Serializable {


    /**
     * showDate : 2018-12-04
     * dateArray : 2018-12-04
     * cinema : {"cinemaId":"34492","cinemaName":"深圳中影星美国际影城","cinemaCode":"9626","spCode":"sp1007","countyCode":"440306","countyName":"宝安区","address":"宝安区西乡大道与新湖路交汇处天虹购物中心3层","longitude":"113.863825","latitude":"22.57539","imgUrl":"","isWanda":"0","terminal":"3","cinemaLinkId":"","buyTimeLimit":0,"newBuyTimeLimit":25,"films":[{"filmId":"6388","filmName":"摘金奇缘","filmAttrs":"","imgUrl":"http://image.189mv.cn/images/filmlib/a8b5aa02d8304077d472a77a407b379b_HB1_62750.jpg","duration":"120min","shows":[{"showCode":"596236336","showDate":"2018-12-04","showTime":"1635","hallCode":"","hallName":"5号厅","price":"45.0","standPrice":"78.0","filmType":"0","filmAttr":"2D","language":"原版","filmPk":""}]},{"filmId":"6387","filmName":"无敌破坏王2：大闹互联网","filmAttrs":"IMAX3D/中国巨幕3D/3D","imgUrl":"http://image.189mv.cn/images/filmlib/74f55fca89138eb88cf8688c7b2881fa_HB1_63537.jpg","duration":"90min","shows":[{"showCode":"596236337","showDate":"2018-12-04","showTime":"1610","hallCode":"","hallName":"3号厅","price":"45.0","standPrice":"78.0","filmType":"1","filmAttr":"3D","language":"原版","filmPk":""}]}]}
     */

    private String showDate;
    private String dateArray;
    private CinemaBean cinema;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getDateArray() {
        return dateArray;
    }

    public void setDateArray(String dateArray) {
        this.dateArray = dateArray;
    }

    public CinemaBean getCinema() {
        return cinema;
    }

    public void setCinema(CinemaBean cinema) {
        this.cinema = cinema;
    }

    public static class CinemaBean implements Serializable  {
        /**
         * cinemaId : 34492
         * cinemaName : 深圳中影星美国际影城
         * cinemaCode : 9626
         * spCode : sp1007
         * countyCode : 440306
         * countyName : 宝安区
         * address : 宝安区西乡大道与新湖路交汇处天虹购物中心3层
         * longitude : 113.863825
         * latitude : 22.57539
         * imgUrl :
         * isWanda : 0
         * terminal : 3
         * cinemaLinkId :
         * buyTimeLimit : 0
         * newBuyTimeLimit : 25
         * films : [{"filmId":"6388","filmName":"摘金奇缘","filmAttrs":"","imgUrl":"http://image.189mv.cn/images/filmlib/a8b5aa02d8304077d472a77a407b379b_HB1_62750.jpg","duration":"120min","shows":[{"showCode":"596236336","showDate":"2018-12-04","showTime":"1635","hallCode":"","hallName":"5号厅","price":"45.0","standPrice":"78.0","filmType":"0","filmAttr":"2D","language":"原版","filmPk":""}]},{"filmId":"6387","filmName":"无敌破坏王2：大闹互联网","filmAttrs":"IMAX3D/中国巨幕3D/3D","imgUrl":"http://image.189mv.cn/images/filmlib/74f55fca89138eb88cf8688c7b2881fa_HB1_63537.jpg","duration":"90min","shows":[{"showCode":"596236337","showDate":"2018-12-04","showTime":"1610","hallCode":"","hallName":"3号厅","price":"45.0","standPrice":"78.0","filmType":"1","filmAttr":"3D","language":"原版","filmPk":""}]}]
         */

        private String cinemaId;
        private String cinemaName;
        private String cinemaCode;
        private String spCode;
        private String countyCode;
        private String countyName;
        private String address;
        private String longitude;
        private String latitude;
        private String imgUrl;
        private String isWanda;
        private String terminal;
        private String cinemaLinkId;
        private int buyTimeLimit;
        private int newBuyTimeLimit;
        private List<FilmsBean> films;

        public String getCinemaId() {
            return cinemaId;
        }

        public void setCinemaId(String cinemaId) {
            this.cinemaId = cinemaId;
        }

        public String getCinemaName() {
            return cinemaName;
        }

        public void setCinemaName(String cinemaName) {
            this.cinemaName = cinemaName;
        }

        public String getCinemaCode() {
            return cinemaCode;
        }

        public void setCinemaCode(String cinemaCode) {
            this.cinemaCode = cinemaCode;
        }

        public String getSpCode() {
            return spCode;
        }

        public void setSpCode(String spCode) {
            this.spCode = spCode;
        }

        public String getCountyCode() {
            return countyCode;
        }

        public void setCountyCode(String countyCode) {
            this.countyCode = countyCode;
        }

        public String getCountyName() {
            return countyName;
        }

        public void setCountyName(String countyName) {
            this.countyName = countyName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getIsWanda() {
            return isWanda;
        }

        public void setIsWanda(String isWanda) {
            this.isWanda = isWanda;
        }

        public String getTerminal() {
            return terminal;
        }

        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }

        public String getCinemaLinkId() {
            return cinemaLinkId;
        }

        public void setCinemaLinkId(String cinemaLinkId) {
            this.cinemaLinkId = cinemaLinkId;
        }

        public int getBuyTimeLimit() {
            return buyTimeLimit;
        }

        public void setBuyTimeLimit(int buyTimeLimit) {
            this.buyTimeLimit = buyTimeLimit;
        }

        public int getNewBuyTimeLimit() {
            return newBuyTimeLimit;
        }

        public void setNewBuyTimeLimit(int newBuyTimeLimit) {
            this.newBuyTimeLimit = newBuyTimeLimit;
        }

        public List<FilmsBean> getFilms() {
            return films;
        }

        public void setFilms(List<FilmsBean> films) {
            this.films = films;
        }

        public static class FilmsBean implements Serializable {
            /**
             * filmId : 6388
             * filmName : 摘金奇缘
             * filmAttrs :
             * imgUrl : http://image.189mv.cn/images/filmlib/a8b5aa02d8304077d472a77a407b379b_HB1_62750.jpg
             * duration : 120min
             * filmType: "喜剧,剧情,传记",
             * filmScore: "9.0",
             * minPrice: 36,
             * shows : [{"showCode":"596236336","showDate":"2018-12-04","showTime":"1635","hallCode":"","hallName":"5号厅","price":"45.0","standPrice":"78.0","filmType":"0","filmAttr":"2D","language":"原版","filmPk":""}]
             */

            private String filmId;
            private String filmName;
            private String filmAttrs;
            private String imgUrl;

            public String getFilmType() {
                return filmType;
            }

            public void setFilmType(String filmType) {
                this.filmType = filmType;
            }

            public String getFilmScore() {
                return filmScore;
            }

            public void setFilmScore(String filmScore) {
                this.filmScore = filmScore;
            }

            public String getMinPrice() {
                return minPrice;
            }

            public void setMinPrice(String minPrice) {
                this.minPrice = minPrice;
            }

            private String duration;
            private String filmType;
            private String filmScore;
            private String minPrice;
            private List<ShowBean> shows;

            public String getFilmId() {
                return filmId;
            }

            public void setFilmId(String filmId) {
                this.filmId = filmId;
            }

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

            public List<ShowBean> getShows() {
                return shows;
            }

            public void setShows(List<ShowBean> shows) {
                this.shows = shows;
            }
        }
    }
}
