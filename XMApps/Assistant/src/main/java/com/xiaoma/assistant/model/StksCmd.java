package com.xiaoma.assistant.model;

import java.util.ArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/13
 * Desc：
 */
public class StksCmd {
    String type;
    ArrayList<String> nliFieldSearch;
    String nliScene;
    ArrayList<StksCmdNliScene> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getNliFieldSearch() {
        return nliFieldSearch;
    }

    public void setNliFieldSearch(ArrayList<String> nliFieldSearch) {
        this.nliFieldSearch = nliFieldSearch;
    }

    public String getNliScene() {
        return nliScene;
    }

    public void setNliScene(String nliScene) {
        this.nliScene = nliScene;
    }

    public ArrayList<StksCmdNliScene> getList() {
        return list;
    }

    public void setList(ArrayList<StksCmdNliScene> list) {
        this.list = list;
    }
}
