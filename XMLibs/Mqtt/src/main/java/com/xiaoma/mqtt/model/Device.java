package com.xiaoma.mqtt.model;

/**
 * @Auther: huojie
 * @Date: 2019/1/2 0002 16:45
 * @Description:
 */
public class Device {
    /**
     * 消息id
     */
    private String mid;

    /**
     * 用于追踪推荐的id
     */
    private String tid;

    /**
     * 创建时间
     */
    private long cd;

    /**
     * 用户id
     */
    private String uid;

    /**
     * iccid,SIM卡卡号
     */
    private String iccid;

    /**
     * 设备唯一ID
     */
    private String imei;

    /**
     * 渠道号
     */
    private String cid;

    /**
     * os版本号
     */
    private String ov;

    /**
     * 设备类型
     */
    private String dm;

    /**
     * 程序版本号
     */
    private String vc;

    /**
     * 工作模式 默认为1 生活模式
     */
    private int pat = 1;

    public int getPat() {
        return pat;
    }

    public void setPat(int pat) {
        this.pat = pat;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public long getCd() {
        return cd;
    }

    public void setCd(long cd) {
        this.cd = cd;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getOv() {
        return ov;
    }

    public void setOv(String ov) {
        this.ov = ov;
    }

    public String getDm() {
        return dm;
    }

    public void setDm(String dm) {
        this.dm = dm;
    }

    public String getVc() {
        return vc;
    }

    public void setVc(String vc) {
        this.vc = vc;
    }
}
