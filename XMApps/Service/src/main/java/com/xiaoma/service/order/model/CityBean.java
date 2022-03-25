package com.xiaoma.service.order.model;

import java.io.Serializable;

/**
 * 城市model
 * Created by zhushi.
 * Date: 2018/11/16
 */
public class CityBean implements Serializable {
    private static final long serialVersionUID = -8535824317761775328L;
    private String name;
    private String letters;//显示拼音的首字母

    public CityBean() {

    }

    public CityBean(String name, String letters) {
        this.name = name;
        this.letters = letters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "name='" + name + '\'' +
                ", letters='" + letters + '\'' +
                '}';
    }
}
