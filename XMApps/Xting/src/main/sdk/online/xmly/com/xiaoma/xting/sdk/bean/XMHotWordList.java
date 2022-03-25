package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMHotWordList extends XMBean<HotWordList> {
    public XMHotWordList(HotWordList hotWordList) {
        super(hotWordList);
    }

    public List<XMHotWord> getHotWordList() {
        List<HotWord> hotWordList = getSDKBean().getHotWordList();
        List<XMHotWord> xmHotWords = new ArrayList<>();
        if (hotWordList != null && !hotWordList.isEmpty()) {
            for (HotWord hotWord : hotWordList) {
                if (hotWord == null) {
                    continue;
                }
                xmHotWords.add(new XMHotWord(hotWord));
            }
        }
        return xmHotWords;
    }

    public void setHotWordList(List<XMHotWord> xmHotWordList) {
        if (xmHotWordList == null) {
            getSDKBean().setHotWordList(null);
            return;
        }
        List<HotWord> hotWordList = new ArrayList<>();
        for (XMHotWord xmHotWord : xmHotWordList) {
            if (xmHotWord == null) {
                continue;
            }
            hotWordList.add(xmHotWord.getSDKBean());
        }
        getSDKBean().setHotWordList(hotWordList);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
