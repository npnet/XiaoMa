package com.xiaoma.assistant.model.parser;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */

public class StockBean implements Serializable {
    private String name;
    private String code;
    private String category;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
