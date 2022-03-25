package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

import java.util.List;

public class RecBean {
    /**
     * type : map
     * index : 1
     * datas : [{"startLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"CURRENT_POI"},"endLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"世界之窗地铁站A出口"}},{"startLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"CURRENT_POI"},"endLoc":{"city":"CURRENT_CITY","type":"LOC_POI","poi":"世界之窗地铁站B口"}}]
     */

    private String type;
    private int index;
    private List<DatasBean> datas;

    public RecBean() {

    }

    public RecBean(String type, int index, List<DatasBean> datas) {
        this.type = type;
        this.index = index;
        this.datas = datas;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }


}