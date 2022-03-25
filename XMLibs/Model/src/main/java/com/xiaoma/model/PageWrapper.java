package com.xiaoma.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kaka
 * on 19-1-14 下午4:16
 * <p>
 * desc: 用于处理分页列表接口的包装器
 * </p>
 */
public class PageWrapper<T extends Serializable> implements Serializable {
    /**
     * 数据总数
     */
    private int total;
    /**
     * 数据总分页数
     */
    private int totalPages;
    /**
     * 当前页数
     */
    private int currentPage;
    /**
     * 分页数据量
     */
    private int pageSize;
    /**
     * 数据内容
     */
    private List<T> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
