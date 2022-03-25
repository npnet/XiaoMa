package com.xiaoma.autotracker.model;

/**
 * @author taojin
 * @date 2018/12/7
 * @desc 控件类型
 */
public enum ViewType {

    BUTTON(1),
    IMAGEVIEW(2),
    TEXTVIEW(3),
    RADIOBUTTON(4),
    CHECKBOX(5);

    private int value;

    ViewType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
