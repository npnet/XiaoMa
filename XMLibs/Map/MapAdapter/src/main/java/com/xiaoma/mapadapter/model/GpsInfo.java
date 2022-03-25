package com.xiaoma.mapadapter.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vincenthu
 * Date: 2016/12/6
 * Time: 14:43
 */
@Table("gpsinfo")
public class GpsInfo implements Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    private long cd;
    private double lat;
    private double lon;
    private String c;     //城市
    private String p;     //省份
    private float s;      //速度
    private float ac;    //精度
    private float be;   //方向角
    private String dis;   //地区
    private int gac;   // 卫星信号强度
    private int sa;     //卫星数目
    private int lt;    //定位类型
    private double al;  //海拔

    public GpsInfo() {

    }

    public GpsInfo(long currentTimeMillis, Double geoLat, Double geoLng, String province, String city,
                   float speed, float accuracy, float bearing, String district, int satellites,
                   int gpsAccuracyStatus, int locationType, double altitude) {
        this.cd = currentTimeMillis;
        this.lat = geoLat;
        this.lon = geoLng;
        this.c = city;
        this.p = province;
        this.s = speed;
        this.ac = accuracy;
        this.be = bearing;
        this.dis = district;
        this.sa = satellites;
        this.gac = gpsAccuracyStatus;
        this.lt = locationType;
        this.al = altitude;
    }

    public long getCd() {
        return cd;
    }

    public void setCd(long cd) {
        this.cd = cd;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = s;
    }

    public float getAc() {
        return ac;
    }

    public void setAc(float ac) {
        this.ac = ac;
    }

    public float getBe() {
        return be;
    }

    public void setBe(float be) {
        this.be = be;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public int getGac() {
        return gac;
    }

    public void setGac(int gac) {
        this.gac = gac;
    }

    public int getSa() {
        return sa;
    }

    public void setSa(int sa) {
        this.sa = sa;
    }

    public int getLt() {
        return lt;
    }

    public void setLt(int lt) {
        this.lt = lt;
    }

    public double getAl() {
        return al;
    }

    public void setAl(double al) {
        this.al = al;
    }
}
