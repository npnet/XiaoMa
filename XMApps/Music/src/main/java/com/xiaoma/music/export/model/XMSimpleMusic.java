package com.xiaoma.music.export.model;

import android.os.Parcel;

import com.xiaoma.model.XMResult;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/2/26 0026
 */
public class XMSimpleMusic extends XMResult<XMSimpleMusic.DataBean> {

    protected XMSimpleMusic(Parcel in) {
        super(in);
    }

    public static class DataBean {
        private boolean last;
        private int totalPages;
        private int totalElements;
        private int size;
        private int number;
        private boolean first;
        private int numberOfElements;
        private List<ContentBean> content;

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean {
            /**
             * id : 6
             * createDate : 1535575269000
             * modifyDate : 1535575269000
             * songId : 40969271
             * name : 因为有你
             * heat : 53
             * singerId : 228836
             * singerName : 曾惜
             * singerCoverUrl : http://www.carbuyin.net/by_spider/spider/kw_music/a72d00de-c19d-4b4b-b16f-d140685d8ea8.png
             */

            private int id;
            private long createDate;
            private long modifyDate;
            private int songId;
            private String name;
            private int heat;
            private int singerId;
            private String singerName;
            private String singerCoverUrl;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public long getCreateDate() {
                return createDate;
            }

            public void setCreateDate(long createDate) {
                this.createDate = createDate;
            }

            public long getModifyDate() {
                return modifyDate;
            }

            public void setModifyDate(long modifyDate) {
                this.modifyDate = modifyDate;
            }

            public int getSongId() {
                return songId;
            }

            public void setSongId(int songId) {
                this.songId = songId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getHeat() {
                return heat;
            }

            public void setHeat(int heat) {
                this.heat = heat;
            }

            public int getSingerId() {
                return singerId;
            }

            public void setSingerId(int singerId) {
                this.singerId = singerId;
            }

            public String getSingerName() {
                return singerName;
            }

            public void setSingerName(String singerName) {
                this.singerName = singerName;
            }

            public String getSingerCoverUrl() {
                return singerCoverUrl;
            }

            public void setSingerCoverUrl(String singerCoverUrl) {
                this.singerCoverUrl = singerCoverUrl;
            }
        }
    }
}
