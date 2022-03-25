package com.xiaoma.assistant.model.parser;

import com.xiaoma.utils.GsonHelper;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class FmBean {

    /**
     * waveband : fm
     * code : 98
     */

    public String waveband;
    public String code;

    public String toJson() {
        return GsonHelper.toJson(this);
    }
}
