package com.xiaoma.mapadapter.model;

/**
 * 地图类型枚举， 切换地图类型时使用
 * Created by minxiwen on 2017/12/11 0011.
 */

public enum MapType {
    MAP_BD(1),//百度地图
    MAP_GD(2);//高德地图
    int state;

    MapType(int state) {
        this.state = state;
    }
}
