package com.xiaoma.config.bean;

/**
 * @author: iSun
 * @date: 2019/8/5 0005
 */
public enum SourceType {
    NONE,
    AM,
    FM,
    NET_FM,
    NET_MUSIC,
    USB_MUSIC,
    BT_MUSIC;

    public final SourceType getSourceType(String type) {
        if ("AM".equalsIgnoreCase(type)) {
            return AM;
        }
        if ("FM".equalsIgnoreCase(type)) {
            return FM;
        }
        if ("NET_FM".equalsIgnoreCase(type)) {
            return NET_FM;
        }
        if ("NET_MUSIC".equalsIgnoreCase(type)) {
            return NET_MUSIC;
        }
        if ("USB_MUSIC".equalsIgnoreCase(type)) {
            return USB_MUSIC;
        }
        if ("BT_MUSIC".equalsIgnoreCase(type)) {
            return BT_MUSIC;
        }
        if ("NONE".equalsIgnoreCase(type)) {
            return NONE;
        }
        return NET_MUSIC;
    }

    public String getSourceDesc(SourceType type) {
        switch (type) {
            case AM:
                return "AM";
            case FM:
                return "FM";
            case NET_FM:
                return "NET_FM";
            case NET_MUSIC:
                return "NET_MUSIC";
            case BT_MUSIC:
                return "BT_MUSIC";
            case USB_MUSIC:
                return "USB_MUSIC";
            case NONE:
                return "NONE";
        }
        return "NET_MUSIC";
    }
}
