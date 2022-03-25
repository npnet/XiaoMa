package com.xiaoma.wechat.model;

/**
 * Created by qiuboxiang on 2019/5/21 15:23
 * Desc: 车载微信联系人
 */
public class WeChatContact {

    private String id;
    private String nick;
    private String remark;
    private String headerImg;

    public WeChatContact(String id, String nick, String remark, String headerImg) {
        this.id = id;
        this.nick = nick;
        this.remark = remark;
        this.headerImg = headerImg;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHeaderImg() {
        return this.headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

}
