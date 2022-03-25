package com.xiaoma.assistant.model;

import java.util.ArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/13
 * Desc：
 */

public class StksSelectList {
    int id;
    ArrayList<StksSelectResult> searchResult;
    ArrayList<StksCmdDimension> dimension;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<StksSelectResult> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(ArrayList<StksSelectResult> searchResult) {
        this.searchResult = searchResult;
    }

    public ArrayList<StksCmdDimension> getDimension() {
        return dimension;
    }

    public void setDimension(ArrayList<StksCmdDimension> dimension) {
        this.dimension = dimension;
    }
}
