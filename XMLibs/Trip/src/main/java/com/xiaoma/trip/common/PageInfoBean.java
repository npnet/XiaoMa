package com.xiaoma.trip.common;

import java.io.Serializable;

/**
 * 分页信息
 * Created by zhushi.
 * Date: 2018/12/4
 */
public class PageInfoBean implements Serializable {
    /**
     * pageNum : 1
     * pageSize : 2
     * totalRecord : 6638
     * totalPage : 3319
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
