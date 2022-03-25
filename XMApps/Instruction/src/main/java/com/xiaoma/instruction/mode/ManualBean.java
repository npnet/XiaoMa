package com.xiaoma.instruction.mode;

import java.io.Serializable;

public class ManualBean implements Serializable {

    /**
     * id : 1
     * menuName : 超级桌面
     * menuNameUs : superDesk
     * channelId : TT0000
     */

    private String id;
    private String menuName;
    private String menuNameUs;
    private String channelId;
    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuNameUs() {
        return menuNameUs;
    }

    public void setMenuNameUs(String menuNameUs) {
        this.menuNameUs = menuNameUs;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
