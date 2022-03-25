package com.xiaoma.music.online.model;


import com.xiaoma.music.common.adapter.IGalleryData;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
public class BillboardBean implements Serializable, IGalleryData {

    private String title;
    private String coverUrl;
    private XMBillboardInfo xmBillboardInfo;

    public BillboardBean(String title, String coverUrl, XMBillboardInfo xmBillboardInfo) {
        this.title = title;
        this.coverUrl = coverUrl;
        this.xmBillboardInfo = xmBillboardInfo;
    }

    public static List<BillboardBean> convert2Billboard(List<XMBillboardInfo> list) {
        List<BillboardBean> billboardBeans = new ArrayList<>(list.size());
        for (XMBillboardInfo xmBillboardInfo : list) {
            billboardBeans.add(new BillboardBean(xmBillboardInfo.getSDKBean().getName()
                    , xmBillboardInfo.getSDKBean().getImageUrl(), xmBillboardInfo));
        }
        return billboardBeans;
    }

    public XMBillboardInfo getXmBillboardInfo() {
        return xmBillboardInfo;
    }

    @Override
    public String getCoverUrl() {
        return coverUrl;
    }

    @Override
    public String getTitleText() {
        return title;
    }

    @Override
    public String getFooterText() {
        return null;
    }

    @Override
    public String getBottomText() {
        return null;
    }
}
