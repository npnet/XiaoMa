package com.xiaoma.autotracker.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by ZhangYao.
 * Date ：2017/1/17 0017
 */

@Table("antotarckinfo")
public class AutoTrackInfo implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long _id;

    /**
     * 事件类型（链路跳转/点击）
     * {@link TrackerEventType}
     */
    private int ot;
    /**
     * 页面事件类型
     * {@link AppViewScreen}
     */
    private int s;

    /**
     * 业务类型
     * {@link BusinessType}
     */
    private String bs;

    /**
     * 在线时长(online time)
     */
    private long nt;

    /**
     * uiName(event)
     */
    private String e;

    /**
     * uiPath(view_ui)
     */
    private String vu;
    //操作时间
    private long od;

    /**
     * 操作内容一般对应的是id（比如点击的是广告存的就是广告的id）
     * 在对应业务数据时，c为一个json字符串格式为{"id" : "xxxxxx","value" : "xxxxxxxx"}
     * json会拓展一些字段暂时定为h i j k l m n 留作上传某些业务需要的数据
     */
    private String c;

    //页面中文意思
    private String vn;
    /**
     * 控件类型 {@link ViewType}
     * (保留字段)
     */
    //用户id
    private String uid;

    //汽车时速
    private String sp;


    public AutoTrackInfo() {

    }

    public int getOt() {
        return ot;
    }

    public void setOt(int ot) {
        this.ot = ot;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public long getNt() {
        return nt;
    }

    public void setNt(long nt) {
        this.nt = nt;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getVu() {
        return vu;
    }

    public void setVu(String vu) {
        this.vu = vu;
    }

    public long getOd() {
        return od;
    }

    public void setOd(long od) {
        this.od = od;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getVn() {
        return vn;
    }

    public void setVn(String vn) {
        this.vn = vn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    @Override
    public String toString() {
        return "AutoTrackInfo{" +
                "_id=" + _id +
                ", ot=" + ot +
                ", s=" + s +
                ", bs='" + bs + '\'' +
                ", nt=" + nt +
                ", e='" + e + '\'' +
                ", vu='" + vu + '\'' +
                ", od=" + od +
                ", c='" + c + '\'' +
                ", vn='" + vn + '\'' +
                ", uid='" + uid + '\'' +
                ", sp='" + sp + '\'' +
                '}';
    }
}
