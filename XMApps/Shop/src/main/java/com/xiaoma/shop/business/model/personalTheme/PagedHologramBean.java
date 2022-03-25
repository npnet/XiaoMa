package com.xiaoma.shop.business.model.personalTheme;

import com.xiaoma.shop.business.model.HoloListModel;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/20
 */
public class PagedHologramBean {

    private PageInfoBean pageInfo;
    private List<HoloListModel> skinVersions;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<HoloListModel> getSkinVersions() {
        return skinVersions;
    }

    public void setSkinVersions(List<HoloListModel> skinVersions) {
        this.skinVersions = skinVersions;
    }

    public static class PageInfoBean {
        /**
         * pageNum : 0
         * pageSize : 5
         * totalRecord : 2
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
