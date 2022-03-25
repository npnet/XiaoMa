package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMHotWord extends XMBean<HotWord> {
    public XMHotWord(HotWord hotWord) {
        super(hotWord);
    }

    public String getSearchword() {
        return getSDKBean().getSearchword();
    }

    public void setSearchword(String searchword) {
        getSDKBean().setSearchword(searchword);
    }

    public int getDegree() {
        return getSDKBean().getDegree();
    }

    public void setDegree(int degree) {
        getSDKBean().setDegree(degree);
    }

    public int getCount() {
        return getSDKBean().getCount();
    }

    public void setCount(int count) {
        getSDKBean().setCount(count);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
