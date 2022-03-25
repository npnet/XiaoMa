package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.search.model.HighLightWord;
import com.kaolafm.opensdk.api.search.model.SearchProgramBean;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMSearchProgramBean extends XMBean<SearchProgramBean> {

    public XMSearchProgramBean(SearchProgramBean searchProgramBean) {
        super(searchProgramBean);
    }

    public long getId() {
        return getSDKBean().getId();
    }

    public String getImg() {
        return getSDKBean().getImg();
    }

    public long getOldId() {
        return getSDKBean().getOldId();
    }

    public String getPlayUrl() {
        return getSDKBean().getPlayUrl();
    }

    public int getSource() {
        return getSDKBean().getSource();
    }

    public String getSourceName() {
        return getSDKBean().getSourceName();
    }

    public int getType() {
        return getSDKBean().getType();
    }

    public String getAlbumName() {
        return getSDKBean().getAlbumName();
    }

    public List<XMCompare> getComperes() {
        List<SearchProgramBean.Compere> comperes = getSDKBean().getComperes();
        if (comperes != null && !comperes.isEmpty()) {
            List<XMCompare> xmCompareList = new ArrayList<>(comperes.size());
            for (SearchProgramBean.Compere compere : comperes) {
                xmCompareList.add(new XMCompare(compere));
            }

            return xmCompareList;
        }
        return null;
    }

    public long getDuration() {
        return getSDKBean().getDuration();
    }

    public String getFreq() {
        return getSDKBean().getFreq();
    }

    public String getCallback() {
        return getSDKBean().getCallback();
    }

    public List<XMHighLightWord> getHightLight() {
        List<HighLightWord> highLight = getSDKBean().getHightLight();
        if (highLight != null && !highLight.isEmpty()) {
            List<XMHighLightWord> xmHighLightWordList = new ArrayList<>(highLight.size());
            for (HighLightWord highLightWord : highLight) {
                xmHighLightWordList.add(new XMHighLightWord(highLightWord));
            }
            return xmHighLightWordList;
        }
        return null;
    }

    public static class XMCompare extends XMBean<SearchProgramBean.Compere> {
        public XMCompare(SearchProgramBean.Compere compere) {
            super(compere);
        }

        public String getName() {
            return getSDKBean().getName();
        }

        public String getDes() {
            return getSDKBean().getDes();
        }

        public String getImg() {
            return getSDKBean().getImg();
        }

    }
}
