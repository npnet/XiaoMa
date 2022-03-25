package com.xiaoma.mapadapter.model;

/**
 * Poi查询配置参数
 * Created by minxiwen on 2017/12/12 0012.
 */

public class QueryOption {
    private String queryContent;
    private String category;
    private String city;
    private int pageNum;
    private int pageSize;

    public QueryOption(String queryContent, String category, String city) {
        this.queryContent = queryContent;
        this.category = category;
        this.city = city;
    }

    public String getQueryContent() {
        return queryContent;
    }

    public void setQueryContent(String queryContent) {
        this.queryContent = queryContent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

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
}
