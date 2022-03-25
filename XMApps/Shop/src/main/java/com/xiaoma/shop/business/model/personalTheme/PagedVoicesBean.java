package com.xiaoma.shop.business.model.personalTheme;

import com.xiaoma.shop.business.model.SkusBean;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/18
 */
public class PagedVoicesBean {


    /**
     * skus : [{"id":236,"createDate":1539586589000,"modifyDate":1539586589000,"category":"音频","price":0.01,"icon":"http://www.carbuyin.net/sl/filePath/58b646fc-4083-47d7-81a4-6f5ca4a1f131.png","typeIcon":"","themeName":"童声","voiceParam":"vinn","xfParameter":0,"orderList":120,"logoUrl":"http://www.carbuyin.net/sl/filePath/58b646fc-4083-47d7-81a4-6f5ca4a1f131.png","isBuy":false,"channelId":"AA1090","status":0,"isDefault":false,"scorePrice":0,"source":0},{"id":237,"createDate":1539586589000,"modifyDate":1539586589000,"category":"音频","price":0.01,"icon":"http://www.carbuyin.net/sl/filePath/df1b6bab-2ebe-40fc-acab-2152b887bfca.png","typeIcon":"","themeName":"台湾普通话","voiceParam":"aisxlin","xfParameter":0,"orderList":110,"logoUrl":"http://www.carbuyin.net/sl/filePath/df1b6bab-2ebe-40fc-acab-2152b887bfca.png","isBuy":false,"channelId":"AA1090","status":0,"isDefault":false,"scorePrice":0,"source":0},{"id":238,"createDate":1539586589000,"modifyDate":1539586589000,"category":"音频","price":9.9,"icon":"http://www.carbuyin.net/sl/filePath/5b369ccd-fc87-4411-bfee-3debe13210fc.png","typeIcon":"","themeName":"小新","voiceParam":"xiaoxin","xfParameter":0,"orderList":100,"logoUrl":"http://www.carbuyin.net/sl/filePath/5b369ccd-fc87-4411-bfee-3debe13210fc.png","isBuy":false,"channelId":"AA1090","status":0,"isDefault":false,"scorePrice":0,"source":0},{"id":239,"createDate":1539586589000,"modifyDate":1539586589000,"category":"音频","price":9.9,"icon":"http://www.carbuyin.net/sl/filePath/375b9e61-eb82-47d2-af3c-2d3182252005.png","typeIcon":"","themeName":"鸭先生","voiceParam":"aisduck","xfParameter":0,"orderList":90,"logoUrl":"http://www.carbuyin.net/sl/filePath/375b9e61-eb82-47d2-af3c-2d3182252005.png","isBuy":false,"channelId":"AA1090","status":0,"isDefault":false,"scorePrice":0,"source":0},{"id":240,"createDate":1539586589000,"modifyDate":1539586589000,"category":"音频","price":9.9,"icon":"http://www.carbuyin.net/sl/filePath/d44fa7e4-e04d-44c9-a9fd-d598977b073b.png","typeIcon":"","themeName":"小丸子","voiceParam":"xiaowanzi","xfParameter":0,"orderList":80,"logoUrl":"http://www.carbuyin.net/sl/filePath/d44fa7e4-e04d-44c9-a9fd-d598977b073b.png","isBuy":false,"channelId":"AA1090","status":0,"isDefault":false,"scorePrice":0,"source":0},{"id":241,"createDate":1539586589000,"modifyDate":1539586589000,"category":"音频","price":0,"icon":"http://www.carbuyin.net/sl/filePath/5f7eb222-8ab1-4356-bfc4-e57d5540d07b.png","typeIcon":"","themeName":"男声","voiceParam":"xiaofeng","xfParameter":0,"orderList":70,"logoUrl":"http://www.carbuyin.net/sl/filePath/5f7eb222-8ab1-4356-bfc4-e57d5540d07b.png","isBuy":false,"channelId":"AA1090","status":0,"isDefault":false,"scorePrice":0,"source":0}]
     * pageInfo : {"pageNum":0,"pageSize":20,"totalRecord":6,"totalPage":1}
     */

    private PageInfoBean pageInfo;
    private List<SkusBean> skus;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<SkusBean> getSkus() {
        return skus;
    }

    public void setSkus(List<SkusBean> skus) {
        this.skus = skus;
    }

    public static class PageInfoBean {
        /**
         * pageNum : 0
         * pageSize : 20
         * totalRecord : 6
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


}
