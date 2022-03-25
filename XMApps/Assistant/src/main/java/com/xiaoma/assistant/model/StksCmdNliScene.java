package com.xiaoma.assistant.model;

import java.util.ArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/13
 * Desc：
 */

public class StksCmdNliScene {
    int id;
    ArrayList<StksCmdDimension> dimension;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<StksCmdDimension> getDimension() {
        return dimension;
    }

    public void setDimension(ArrayList<StksCmdDimension> dimension) {
        this.dimension = dimension;
    }
}
