package com.xiaoma.launcher.recommend.model;

import java.util.List;

/**
 * @Auther: huojie
 * @Date: 2019/1/2 0002 15:31
 * @Description:
 */
public class Recommend {

    /**
     * tid : 213456489896545
     * cd : 448971636
     * uid : 1073758947469074432
     * iccid : 89860918710000022349
     * imei : 867012039872830
     * cid : AA1080
     * rec : [{"type":"map","index":1,"datas":[{"startLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"CURRENT_POI"},"endLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"世界之窗地铁站A出口"}},{"startLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"CURRENT_POI"},"endLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"世界之窗地铁站B口"}}]},{"type":"music","index":2,"datas":[{"songid":"123132","src":"kuwo","name":"大海-张雨生","image":"http://www.baidu.com","mp4url":"http://www.baidu.com/dahai.mp4"},{"songid":"123132","src":"kuwo","name":"大海-伍佰","image":"http://www.baidu.com","mp4url":"http://www.baidu.com/dahai.mp4"}]}]
     */

    private String tid;
    private int cd;
    private String uid;
    private String iccid;
    private String imei;
    private String cid;
    private List<RecBean> rec;

    public Recommend(String tid, int cd, String uid, String iccid, String imei, String cid, List<RecBean> rec) {
        this.tid = tid;
        this.cd = cd;
        this.uid = uid;
        this.iccid = iccid;
        this.imei = imei;
        this.cid = cid;
        this.rec = rec;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
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

    public List<RecBean> getRec() {
        return rec;
    }

    public void setRec(List<RecBean> rec) {
        this.rec = rec;
    }


}
