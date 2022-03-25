package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.BaseQukuItemList;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMBaseQukuItemList extends XMBean<BaseQukuItemList> {
    public XMBaseQukuItemList(BaseQukuItemList baseQukuItemList) {
        super(baseQukuItemList);
    }

    public String getMoreType() {
        return getSDKBean().getMoreType();
    }

    public void setMoreType(String type) {
        getSDKBean().setMoreType(type);
    }


    public int getListenCnt() {
        return getSDKBean().getListenCnt();
    }

    public void setListenCnt(String listenCnt) {
        getSDKBean().setListenCnt(listenCnt);
    }

    public void addChild(XMBaseQukuItem item) {
        getSDKBean().addChild(item.getSDKBean());
    }

    public void addChildren(Collection<XMBaseQukuItem> var1) {
        if (var1 != null && var1.size() >= 1) {
            Iterator var2 = var1.iterator();

            while (var2.hasNext()) {
                XMBaseQukuItem var3 = (XMBaseQukuItem) var2.next();
                this.addChild(var3);
            }

        }
    }

    public int getChildCount() {
        return getSDKBean().getChildCount();
    }

    public List<XMBaseQukuItem> getChindren() {
        List<BaseQukuItem> chindren = getSDKBean().getChindren();
        List<XMBaseQukuItem> xmBaseQukuItems = new ArrayList<>();
        for (BaseQukuItem baseQukuItem : chindren) {
            xmBaseQukuItems.add(new XMBaseQukuItem(baseQukuItem));
        }
        return xmBaseQukuItems;
    }

    public void clearChindren() {
        getSDKBean().clearChindren();
    }

    public XMBaseQukuItem getFirstChild() {
        final BaseQukuItem firstChild = getSDKBean().getFirstChild();
        return new XMBaseQukuItem(firstChild);
    }

    public void setName(String name) {
        getSDKBean().setName(name);
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public String getDescript() {
        return getSDKBean().getDescript();
    }

    public void setDescript(String descript) {
        getSDKBean().setDescript(descript);
    }

    public String getDigest() {
        return getSDKBean().getDigest();
    }

    public void setDigest(String digest) {
        getSDKBean().setDigest(digest);
    }

    public String getClassify() {
        return getSDKBean().getClassify();
    }

    public void setClassify(String classify) {
        getSDKBean().setClassify(classify);
    }
}
