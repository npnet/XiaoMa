package com.xiaoma.mapadapter.model;

/**
 * 搜索范围Model
 * Created by minxiwen on 2017/12/12 0012.
 */

public class QueryBound {
    //范围中心点
    private LatLonPoint centerPoint;
    //范围半径
    private int radius;
    //是否按照距离排序
    private boolean isDistanceSort;

    public QueryBound(LatLonPoint centerPoint, int radius, boolean isDistanceSort) {
        this.centerPoint = centerPoint;
        this.radius = radius;
        this.isDistanceSort = isDistanceSort;
    }

    public LatLonPoint getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(LatLonPoint centerPoint) {
        this.centerPoint = centerPoint;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isDistanceSort() {
        return isDistanceSort;
    }

    public void setDistanceSort(boolean distanceSort) {
        isDistanceSort = distanceSort;
    }
}
