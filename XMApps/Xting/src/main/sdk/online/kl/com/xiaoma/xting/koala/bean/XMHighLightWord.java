package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.search.model.HighLightWord;
import com.xiaoma.adapter.base.XMBean;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMHighLightWord extends XMBean<HighLightWord> {

    public XMHighLightWord(HighLightWord highLightWord) {
        super(highLightWord);
    }

    public String getField() {
        return getSDKBean().getField();
    }

    public int getOffset() {
        return getSDKBean().getOffset();
    }

    public int getStart() {
        return getSDKBean().getStart();
    }

    public String getToken() {
        return getSDKBean().getToken();
    }
}
