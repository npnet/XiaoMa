package com.xiaoma.service.order.model;

import java.io.Serializable;

/**
 * 4s bean
 * Created by zhushi.
 * Date: 2018/11/16
 */
public class ShopBean implements Serializable {

    /**
     * PKEY : 590
     * VDEALER : DJL193
     * VDEALERNAME : 长春一驰汽车销售服务有限公司
     * VPROVINCE : 吉林
     * VCITY : 长春市
     * VTEL : 0431-89684881
     * TOTALSCORE : 0
     * PATHLNG : 125.241605
     * PATHLAT : 43.822519
     */

    private int PKEY;
    private String VDEALER;
    private String VDEALERNAME;
    private String VPROVINCE;
    private String VCITY;
    private String VTEL;
    private double PATHLNG;
    private double PATHLAT;
    private String VADDRESS;
    //店铺距离定位的距离
    private double locationDistance;

    public String getVADDRESS() {
        return VADDRESS;
    }

    public void setVADDRESS(String VADDRESS) {
        this.VADDRESS = VADDRESS;
    }

    public int getPKEY() {
        return PKEY;
    }

    public void setPKEY(int PKEY) {
        this.PKEY = PKEY;
    }

    public String getVDEALER() {
        return VDEALER;
    }

    public void setVDEALER(String VDEALER) {
        this.VDEALER = VDEALER;
    }

    public String getVDEALERNAME() {
        return VDEALERNAME;
    }

    public void setVDEALERNAME(String VDEALERNAME) {
        this.VDEALERNAME = VDEALERNAME;
    }

    public String getVPROVINCE() {
        return VPROVINCE;
    }

    public void setVPROVINCE(String VPROVINCE) {
        this.VPROVINCE = VPROVINCE;
    }

    public String getVCITY() {
        return VCITY;
    }

    public void setVCITY(String VCITY) {
        this.VCITY = VCITY;
    }

    public String getVTEL() {
        return VTEL;
    }

    public void setVTEL(String VTEL) {
        this.VTEL = VTEL;
    }

    public double getPATHLNG() {
        return PATHLNG;
    }

    public void setPATHLNG(double PATHLNG) {
        this.PATHLNG = PATHLNG;
    }

    public double getPATHLAT() {
        return PATHLAT;
    }

    public void setPATHLAT(double PATHLAT) {
        this.PATHLAT = PATHLAT;
    }

    public double getLocationDistance() {
        return locationDistance;
    }

    public void setLocationDistance(double locationDistance) {
        this.locationDistance = locationDistance;
    }
}
