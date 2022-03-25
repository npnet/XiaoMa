package com.xiaoma.service.order.model;

import java.io.Serializable;

public class OrderBean implements Serializable {
    private static final long serialVersionUID = -5551782204703497223L;
    /**
     * pkey : 1004
     * vbillno : F201812051622037066118
     * vType01 : Y
     * vType02 : N
     * vType03 : N
     * vCustomName : 含山县人民政府
     * vMoblie : 19944030199
     * appointmentDate : 2018-12-06
     * appointmentTime : 8:00-9:00
     * salerId : A00004496
     * vVin : LFPH4ABC848A05090
     * vDealer : SGD108
     * companyName : 深圳市德路宝汽车有限公司
     * vProvince : 广东省
     * vCity : 深圳市
     * vRemark : 来自小马测试的车辆维修订单-1543998374883
     * vStatus : 已提交
     * vStatusCode : 01
     * orderType : 预约保养
     */

    private String pkey;
    private String vbillno;
    private String vType01;
    private String vType02;
    private String vType03;
    private String vCustomName;
    private String vMoblie;
    private String appointmentDate;
    private String appointmentTime;
    private String salerId;
    private String vVin;
    private String vDealer;
    private String companyName;
    private String vProvince;
    private String vCity;
    private String vRemark;
    private String vStatus;
    private String vStatusCode;
    private String orderType;
    private Long createDate;
    private String address;
    private String tel;
    private double lat;
    private double lng;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getVbillno() {
        return vbillno;
    }

    public void setVbillno(String vbillno) {
        this.vbillno = vbillno;
    }

    public String getVType01() {
        return vType01;
    }

    public void setVType01(String vType01) {
        this.vType01 = vType01;
    }

    public String getVType02() {
        return vType02;
    }

    public void setVType02(String vType02) {
        this.vType02 = vType02;
    }

    public String getVType03() {
        return vType03;
    }

    public void setVType03(String vType03) {
        this.vType03 = vType03;
    }

    public String getVCustomName() {
        return vCustomName;
    }

    public void setVCustomName(String vCustomName) {
        this.vCustomName = vCustomName;
    }

    public String getVMoblie() {
        return vMoblie;
    }

    public void setVMoblie(String vMoblie) {
        this.vMoblie = vMoblie;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getSalerId() {
        return salerId;
    }

    public void setSalerId(String salerId) {
        this.salerId = salerId;
    }

    public String getVVin() {
        return vVin;
    }

    public void setVVin(String vVin) {
        this.vVin = vVin;
    }

    public String getVDealer() {
        return vDealer;
    }

    public void setVDealer(String vDealer) {
        this.vDealer = vDealer;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVProvince() {
        return vProvince;
    }

    public void setVProvince(String vProvince) {
        this.vProvince = vProvince;
    }

    public String getVCity() {
        return vCity;
    }

    public void setVCity(String vCity) {
        this.vCity = vCity;
    }

    public String getVRemark() {
        return vRemark;
    }

    public void setVRemark(String vRemark) {
        this.vRemark = vRemark;
    }

    public String getVStatus() {
        return vStatus;
    }

    public void setVStatus(String vStatus) {
        this.vStatus = vStatus;
    }

    public String getVStatusCode() {
        return vStatusCode;
    }

    public void setVStatusCode(String vStatusCode) {
        this.vStatusCode = vStatusCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
