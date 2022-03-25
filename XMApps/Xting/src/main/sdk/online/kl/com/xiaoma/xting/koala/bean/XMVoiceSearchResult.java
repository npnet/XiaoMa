package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.search.model.SearchProgramBean;
import com.kaolafm.opensdk.api.search.model.VoiceSearchResult;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMVoiceSearchResult extends XMBean<VoiceSearchResult> {
    public XMVoiceSearchResult(VoiceSearchResult voiceSearchResult) {
        super(voiceSearchResult);
    }

    public int getDelayTime() {
        return getSDKBean().getDelayTime();
    }

    public int getPlayIndex() {
        return getSDKBean().getPlayIndex();
    }

    public int getPlayType() {
        return getSDKBean().getPlayType();
    }

    public List<XMSearchProgramBean> getProgramList() {
        List<SearchProgramBean> programList = getSDKBean().getProgramList();
        if (programList != null && !programList.isEmpty()) {
            List<XMSearchProgramBean> searchProgramBeanList = new ArrayList<>(programList.size());
            for (SearchProgramBean searchProgramBean : programList) {
                searchProgramBeanList.add(new XMSearchProgramBean(searchProgramBean));
            }
            return searchProgramBeanList;
        }
        return null;
    }
}
