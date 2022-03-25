package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.UpdateBatch;
import com.ximalaya.ting.android.opensdk.model.album.UpdateBatchList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMUpdateBatchList extends XMBean<UpdateBatchList> {
    public XMUpdateBatchList(UpdateBatchList updateBatchList) {
        super(updateBatchList);
    }

    public List<XMUpdateBatch> getList() {
        List<UpdateBatch> list = getSDKBean().getList();
        List<XMUpdateBatch> xmList = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (UpdateBatch updateBatch : list) {
                if (updateBatch == null) {
                    continue;
                }
                xmList.add(new XMUpdateBatch(updateBatch));
            }
        }
        return xmList;
    }

    public void setList(List<XMUpdateBatch> xmList) {
        if (xmList == null) {
            getSDKBean().setList(null);
            return;
        }
        List<UpdateBatch> list = new ArrayList<>();
        for (XMUpdateBatch xmUpdateBatch : xmList) {
            if (xmUpdateBatch == null) {
                continue;
            }
            list.add(xmUpdateBatch.getSDKBean());
        }
        getSDKBean().setList(list);
    }

}
