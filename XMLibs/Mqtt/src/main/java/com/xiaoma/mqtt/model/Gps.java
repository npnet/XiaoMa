package com.xiaoma.mqtt.model;

/**
 * @Auther: huojie
 * @Date: 2019/1/2 0002 15:41
 * @Description:
 */
public class Gps extends DatasBean {

    /**
     * lon : 114.065718
     * lat : 32.986567
     * p : 河南省
     * c : 驻马店市
     * dis : 驿城区
     * s : 0.0
     * ac : 64.41126
     * be : -1.0
     * gac : 0
     * sa : -1
     * ge :P
     */

    private double lon;  //当前经度
    private double lat;  //当前纬度
    private String p;    //当前省份
    private String c;    //当前城市
    private String dis;  //当前区域
    private String s;    //当前车速
    private String ac;   //精确度
    private String be;   //方向
    private String gac;  //GPS信号强度
    private String sa;   //卫星个数
    private String ge;    //当前档位
    private String name;   //当前名称

    private double dlon;  //目的地经度
    private double dlat;  //目的地纬度
    private String dp;    //目的地省份
    private String dc;    //目的地城市
    private String ddis;  //目的地区域
    private String dname;  //目的地名称
    private String dtype;  //目的地类型


    public double getDlon() {
        return dlon;
    }

    public void setDlon(double dlon) {
        this.dlon = dlon;
    }

    public double getDlat() {
        return dlat;
    }

    public void setDlat(double dlat) {
        this.dlat = dlat;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getDdis() {
        return ddis;
    }

    public void setDdis(String ddis) {
        this.ddis = ddis;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getBe() {
        return be;
    }

    public void setBe(String be) {
        this.be = be;
    }

    public String getGac() {
        return gac;
    }

    public void setGac(String gac) {
        this.gac = gac;
    }

    public String getSa() {
        return sa;
    }

    public void setSa(String sa) {
        this.sa = sa;
    }

    public String getGe() {
        return ge;
    }

    public void setGe(String ge) {
        this.ge = ge;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }
}
