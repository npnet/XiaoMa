package com.xiaoma.club.discovery.model;

import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.model.ModelConstants;

import java.util.List;

/**
 * Author: loren
 * Date: 2019/1/11 0011
 */

public class HotGroupResultInfo {
    private int resultCode;
    private String resultMessage;
    private PageData data;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public PageData getData() {
        return data;
    }

    public void setData(PageData data) {
        this.data = data;
    }

    public class PageData {
        List<GroupCardInfo> dataList;
        int currentPage;
        int totalPages;
        int total;
        int pageSize;

        public List<GroupCardInfo> getDataList() {
            return dataList;
        }

        public void setDataList(List<GroupCardInfo> dataList) {
            this.dataList = dataList;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    public boolean isSuccess() {
        return resultCode == ModelConstants.ResultCode.RESULT_OK;
    }
}
