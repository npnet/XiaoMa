package com.xiaoma.assistant.model;

import java.util.ArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/13
 * Desc：
 */

public class StksSelect {
    String type;
    ArrayList<StksSelectList> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<StksSelectList> getList() {
        return list;
    }

    public void setList(ArrayList<StksSelectList> list) {
        this.list = list;
    }
}
