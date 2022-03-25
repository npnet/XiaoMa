package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.quku.BaseQukuItem;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMBaseOnlineSection extends XMBean<BaseOnlineSection> {
    public XMBaseOnlineSection(BaseOnlineSection baseOnlineSection) {
        super(baseOnlineSection);
    }
    public String getMoreType() {
        return getSDKBean().getMoreType();
    }

    public void setMoreType(String var1) {
        getSDKBean().setMoreType(var1);
    }


    public void add(XMBaseQukuItem var1) {
        getSDKBean().add(var1.getSDKBean());
    }


    public int getOnlineInfoSize() {
        return  getSDKBean().getOnlineInfoSize();
    }

    public long getMid() {
        return  getSDKBean().getMid();
    }

    public void setMid(long var1) {
        getSDKBean().setMid(var1);
    }

    public String getType() {
        return  getSDKBean().getType();
    }

    public void setType(String var1) {
        getSDKBean().setType(var1);
    }

    public String getLabel() {
        return  getSDKBean().getLabel();
    }

    public void setLabel(String var1) {
        getSDKBean().setLabel(var1);
    }

    public String getMdigest() {
        return  getSDKBean().getMdigest();
    }

    public void setMdigest(String var1) {
        getSDKBean().setMdigest(var1);
    }

    public List<XMBaseQukuItem> getOnlineInfos() {
        List<BaseQukuItem> onlineInfos = getSDKBean().getOnlineInfos();
        List<XMBaseQukuItem> list = new ArrayList<>();
        for (BaseQukuItem onlineInfo : onlineInfos) {
            if (onlineInfo==null){
                continue;
            }
            list.add(new XMBaseQukuItem(onlineInfo));
        }
        return  list;
    }

    public void setOnlineInfos(List<XMBaseQukuItem> var1) {
        List<BaseQukuItem> list = new ArrayList<>();
        for (XMBaseQukuItem xmBaseQukuItem : var1) {
            if (xmBaseQukuItem==null){
                continue;
            }
            list.add(xmBaseQukuItem.getSDKBean());
        }
        getSDKBean().setOnlineInfos(list);
    }

    public int getStart() {
        return  getSDKBean().getStart();
    }

    public void setStart(int var1) {
        getSDKBean().setStart(var1);
    }

    public int getCount() {
        return  getSDKBean().getCount();
    }

    public void setCount(int var1) {
        getSDKBean().setCount(var1);
    }

    public int getTotal() {
        return  getSDKBean().getTotal();
    }

    public void setTotal(int var1) {
        getSDKBean().setTotal(var1);
    }

    public String getName() {
        return  getSDKBean().getName();
    }

    public void setName(String var1) {
        getSDKBean().setName(var1);
    }

    public String getAppDesc() {
        return  getSDKBean().getAppDesc();
    }

    public void setAppDesc(String var1) {
        getSDKBean().setAppDesc(var1);
    }

    public String getAppUrl() {
        return  getSDKBean().getAppUrl();
    }

    public void setAppUrl(String var1) {
        getSDKBean().setAppUrl(var1);
    }

    public String getAdText() {
        return  getSDKBean().getAdText();
    }

    public void setAdText(String var1) {
        getSDKBean().setAdText(var1);
    }

    public String getAndroidUrl() {
        return  getSDKBean().getAndroidUrl();
    }

    public void setAndroidUrl(String var1) {
        getSDKBean().setAndroidUrl(var1);
    }

    public String getAction() {
        return  getSDKBean().getAction();
    }

    public void setAction(String var1) {
        getSDKBean().setAction(var1);
    }

    public String getImg() {
        return  getSDKBean().getImg();
    }

    public void setImg(String var1) {
        getSDKBean().setImg(var1);
    }

    public String getArUrl() {
        return  getSDKBean().getArUrl();
    }

    public void setArUrl(String var1) {
        getSDKBean().setArUrl(var1);
    }

    public String getAdType() {
        return  getSDKBean().getAdType();
    }

    public void setAdType(String var1) {
        getSDKBean().setAdType(var1);
    }

    public String getMdata() {
        return  getSDKBean().getMdata();
    }

    public void setMdata(String var1) {
        getSDKBean().setMdata(var1);
    }

    public String getHasClassfy() {
        return  getSDKBean().getHasClassfy();
    }

    public void setHasClassfy(String var1) {
        getSDKBean().setHasClassfy(var1);
    }

    public int getImgRes() {
        return  getSDKBean().getImgRes();
    }

    public void setImgRes(int var1) {
        getSDKBean().setImgRes(var1);
    }
}
