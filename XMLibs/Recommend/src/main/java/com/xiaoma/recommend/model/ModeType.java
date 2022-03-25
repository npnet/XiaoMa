package com.xiaoma.recommend.model;

/**
 * @author: iSun
 * @date: 2018/12/18 0018
 */
public enum ModeType {
    LEISURE("1"),//休闲
    COMMUTING("2"),//通勤
    TOURISM("3"),//旅游
    ENTERTAINMENT("4")//娱乐
    ;

    private String value;

    ModeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
