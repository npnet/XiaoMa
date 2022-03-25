package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

public  class FilmSessionBean implements Serializable {
        /**
         * filmId : 5734
         * filmName : 毒液：致命守护者
         * filmAttrs : IMAX3D/中国巨幕3D/3D
         * duration : 112min
         * shows : [{"showCode":"597065216","showDate":"2018-12-06","showTime":"1315","hallCode":"","hallName":"6号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065219","showDate":"2018-12-06","showTime":"1520","hallCode":"","hallName":"6号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065218","showDate":"2018-12-06","showTime":"1725","hallCode":"","hallName":"6号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065266","showDate":"2018-12-06","showTime":"1825","hallCode":"","hallName":"3号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065221","showDate":"2018-12-06","showTime":"1930","hallCode":"","hallName":"6号厅(激光)","price":"48.9","standPrice":"85.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065220","showDate":"2018-12-06","showTime":"2135","hallCode":"","hallName":"6号厅(激光)","price":"48.9","standPrice":"85.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065268","showDate":"2018-12-06","showTime":"2235","hallCode":"","hallName":"3号厅(激光)","price":"42.9","standPrice":"73.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""}]
         */

        private String filmId;
        private String filmName;
        private String filmAttrs;
        private String duration;
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