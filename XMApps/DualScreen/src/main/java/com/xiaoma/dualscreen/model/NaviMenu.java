package com.xiaoma.dualscreen.model;

/**
 * @author: iSun
 * @date: 2019/7/27 0027
 */
public enum NaviMenu {
    NAVI_2D(0),
    NAVI_3D(1);


    private int type;

    NaviMenu(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }}
