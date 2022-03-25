package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.BasePageResult;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public abstract class XMPageResult<XMBEAN extends XMBean<BEAN>, BEAN> extends XMBean<BasePageResult<List<BEAN>>> {

    public XMPageResult(BasePageResult<List<BEAN>> listBasePageResult) {
        super(listBasePageResult);
    }

    public int getHaveNext() {
        return getSDKBean().getHaveNext();
    }

    public int getNextPage() {
        return getSDKBean().getNextPage();
    }

    public int getHavePre() {
        return getSDKBean().getHavePre();
    }

    public int getPrePage() {
        return getSDKBean().getPrePage();
    }

    public int getCurrentPage() {
        return getSDKBean().getCurrentPage();
    }

    public int getCount() {
        return getSDKBean().getCount();
    }

    public int getSumPage() {
        return getSDKBean().getSumPage();
    }

    public int getPageSize() {
        return getSDKBean().getPageSize();
    }

    public List<XMBEAN> getDataList() {
        List<BEAN> dataList = getSDKBean().getDataList();
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }

        List<XMBEAN> xmList = new ArrayList<>(dataList.size());
        for (BEAN bean : dataList) {
            xmList.add(handleConverter(bean));
        }

        return xmList;
    }

    protected abstract XMBEAN handleConverter(BEAN bean);
}
