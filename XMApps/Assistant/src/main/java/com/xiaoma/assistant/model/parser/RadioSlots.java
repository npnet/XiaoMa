package com.xiaoma.assistant.model.parser;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class RadioSlots {

    public String code;
    public String waveband;
    public String nameOrig;
    public String name;
    public String vName;
    public String program;
    public String broadcaster;
    public String intention;
    public String category;
    public String artist;
    public String insType;
    public String mediaSource;
    public LocationBean location;

    public class LocationBean {
        /**
         * province : 山东省
         * provinceAddr : 山东
         * type : LOC_BASIC
         */
        public String province;
        public String provinceAddr;
        public String type;
    }
}
