package com.xiaoma.trip.common;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.common.model
 *  @file_name:      CollectItems
 *  @author:         Rookie
 *  @create_time:    2019/2/22 16:53
 *  @description：   TODO             */

import java.util.List;

public class CollectItems {

    private PageInfoBean pageInfo;
    private List<CollectionsBean> collections;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<CollectionsBean> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionsBean> collections) {
        this.collections = collections;
    }

    public static class PageInfoBean {
        /**
         * pageNum : 0
         * pageSize : 10
         * totalRecord : 10
         * totalPage : 1
         */

        private int pageNum;
        private int pageSize;
        private int totalRecord;
        private int totalPage;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }
    }

    public static class CollectionsBean {
        /**
         * id : 13
         * createDate : 1550828561000
         * uid : 1050220216212271104
         * type : Film
         * collectionId : 28706
         * collectionJson : {"address":"东城区崇外大街18号国瑞购物中心首层","buyTimeLimit":0,"cinemaCode":"4394","cinemaId":"28706","cinemaName":"北京百老汇影城国瑞购物中心店","countyCode":"110101","countyName":"东城区","distance":"2.0","facilitys":"[]","imgUrl":"","latitude":"39.897474379","longitude":"116.4201623864","minPrice":"28","mobile":"","newBuyTimeLimit":0,"spCode":"sp1007","terminal":"3"}
         */

        private int id;
        private long createDate;
        private long uid;
        private String type;
        private String collectionId;
        private String collectionJson;

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

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(String collectionId) {
            this.collectionId = collectionId;
        }

        public String getCollectionJson() {
            return collectionJson;
        }

        public void setCollectionJson(String collectionJson) {
            this.collectionJson = collectionJson;
        }

    }


}
