package com.qiming.fawcard.synthesize.data.entity;

/**
 * 项目名称：QM-Android
 * 类描述：
 * 创建人：cuijiaojiao
 * 创建时间：2017/7/20 上午10:52
 * 修改人：cuijiaojiao
 * 修改时间：2017/7/20 上午10:52
 * 修改备注：
 */

public class VehicleSnapShotRequest {
    private String vin = "";
    private String statusCateCode = "";
    private String sort = "";

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getStatusCateCode() {
        return statusCateCode;
    }

    public void setStatusCateCode(String statusCateCode) {
        this.statusCateCode = statusCateCode;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
