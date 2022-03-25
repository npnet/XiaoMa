package com.qiming.fawcard.synthesize.data.entity;

import com.qiming.fawcard.synthesize.base.BaseResponse;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/6/2.
 */

public class SnapShotResponse extends BaseResponse {

    private ResultEntity result;
    private String pageCount = "";
    private String pageIndex = "";
    private String pageSize = "";
    private String totalCount = "";
    private String extMessage = "";
    private String scrollId = "";

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }


    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public ResultEntity getResult() {
        return result;
    }

    public String getPageCount() {
        return pageCount;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getTotalCount() {
        return totalCount;
    }


    public String getExtMessage() {
        return extMessage;
    }

    public String getScrollId() {
        return scrollId;
    }

    public  class ResultEntity {
        private HashMap<String, DetailEntity> mapData;
        private long reportTime;

        public HashMap<String, DetailEntity> getMapData() {
            return mapData;
        }

        public void setMapData(HashMap<String, DetailEntity> mapData) {
            this.mapData = mapData;
        }

        public void setReportTime(long reportTime) {
            this.reportTime = reportTime;
        }


        public long getReportTime() {
            return reportTime;
        }

        public  class DetailEntity {
//                        private String val;
            private Object val;
//            private long pkg_ts;
//            private String ecate_code;
//            private long recv_ts;
//            private int sts;
//            private String scate_code;
//            private String event_code;
//            private String send_type;
//            private String ext_data;
//            private String pkg_id;
            private String statusUnit;

            public String getVal() {
                return val.toString();
            }

            public void setVal(Object val) {
                this.val = val;
            }


            //            public void setVal(String val) {
//                this.val = val;
//            }
//
//            public String getVal() {
//                return val;
//            }

//            public void setPkg_ts(long pkg_ts) {
//                this.pkg_ts = pkg_ts;
//            }
//
//            public void setEcate_code(String ecate_code) {
//                this.ecate_code = ecate_code;
//            }
//
//            public void setRecv_ts(long recv_ts) {
//                this.recv_ts = recv_ts;
//            }
//
//            public void setSts(int sts) {
//                this.sts = sts;
//            }
//
//            public void setScate_code(String scate_code) {
//                this.scate_code = scate_code;
//            }
//
//            public void setEvent_code(String event_code) {
//                this.event_code = event_code;
//            }
//
//            public void setSend_type(String send_type) {
//                this.send_type = send_type;
//            }
//
//            public void setExt_data(String ext_data) {
//                this.ext_data = ext_data;
//            }
//
//            public void setPkg_id(String pkg_id) {
//                this.pkg_id = pkg_id;
//            }

//            public long getPkg_ts() {
//                return pkg_ts;
//            }
//
//            public String getEcate_code() {
//                return ecate_code;
//            }
//
//            public long getRecv_ts() {
//                return recv_ts;
//            }
//
//            public int getSts() {
//                return sts;
//            }
//
//            public String getScate_code() {
//                return scate_code;
//            }
//
//            public String getEvent_code() {
//                return event_code;
//            }
//
//            public String getSend_type() {
//                return send_type;
//            }
//
//            public String getExt_data() {
//                return ext_data;
//            }
//
//            public String getPkg_id() {
//                return pkg_id;
//            }

            public String getStatusUnit() {
                return statusUnit;
            }

            public void setStatusUnit(String statusUnit) {
                this.statusUnit = statusUnit;
            }
        }

    }
}
