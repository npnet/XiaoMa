package com.xiaoma.mapadapter.model;

import java.util.List;

/**
 * Poi搜索结果集
 * Created by minxiwen on 2017/12/12 0012.
 */

public class PoiResult {
    private int pageNum;
    private List<PoiInfo> poiInfoList;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<PoiInfo> getPoiInfoList() {
        return poiInfoList;
    }

    public void setPoiInfoList(List<PoiInfo> poiInfoList) {
        this.poiInfoList = poiInfoList;
    }
}
