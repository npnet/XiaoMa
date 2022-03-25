package com.xiaoma.travel.view.citypick.model;

import java.io.Serializable;

/**
 * @author wutao
 * @date 2018/11/6
 */
public class Contact implements Serializable {
    private String mName;
    private int mType;

    public Contact(String name, int type) {
        mName = name;
        mType = type;
    }

    public String getmName() {
        return mName;
    }

    public int getmType() {
        return mType;
    }

}
