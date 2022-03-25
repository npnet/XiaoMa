package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaData;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaDataList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMMetaDataList extends XMBean<MetaDataList> {
    public XMMetaDataList(MetaDataList metaDataList) {
        super(metaDataList);
    }

    public List<XMMetaData> getMetaDatas() {
        List<MetaData> metaDatas = getSDKBean().getMetaDatas();
        List<XMMetaData> xmMetaData = new ArrayList<>();
        if (metaDatas != null && !metaDatas.isEmpty()) {
            for (MetaData metaData : metaDatas) {
                if (metaData == null) {
                    continue;
                }
                xmMetaData.add(new XMMetaData(metaData));
            }
        }
        return xmMetaData;
    }

    public void setMetaDatas(List<XMMetaData> xmMetaDatas) {
        if (xmMetaDatas == null) {
            getSDKBean().setMetaDatas(null);
            return;
        }
        List<MetaData> metaDatas = new ArrayList<>();
        for (XMMetaData xmMetaData : xmMetaDatas) {
            if (xmMetaData == null) {
                continue;
            }
            metaDatas.add(xmMetaData.getSDKBean());
        }
        getSDKBean().setMetaDatas(metaDatas);
    }

}
