package com.xiaoma.assistant.model.result;

import com.xiaoma.model.ModelConstants;

/**
 * Created by qiuboxiang on 2019/3/8 19:50
 * Desc:
 */
public abstract class SpecialResult<T> {

    public String resultCode;
    public String resultMessage;

    public abstract T getData();
    public abstract boolean isEmptyData();

    public boolean isSuccess() {
        return resultCode.equals(String.valueOf(ModelConstants.ResultCode.RESULT_OK));
    }

    public static class PageInfoBean {
        /**
         * pageNum : 1
         * pageSize : 20
         * totalRecord : 403
         * totalPage : 21
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
