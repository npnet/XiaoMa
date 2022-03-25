package com.xiaoma.instruction.mode;

import java.io.Serializable;

public class ManualItemBean implements Serializable {

    /**
     * id : 50
     * menuName : 超级桌面
     * menuNameUs : superDesk
     * content : 超级桌面超级桌面
     * contentUs : superDeskSuperDesk
     * icon :
     * bigIcon :
     * channelId : TT0000
     */

    private String id;
    private String menuName;
    private String menuNameUs;
    private String content;
    private String contentUs;
    private String icon;
//    private String bigIcon;
    private String channelId;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentUs() {
        return contentUs;
    }

    public void setContentUs(String contentUs) {
        this.contentUs = contentUs;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

//    public String getBigIcon() {
//        return bigIcon;
//    }
//
//    public void setBigIcon(String bigIcon) {
//        this.bigIcon = bigIcon;
//    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
