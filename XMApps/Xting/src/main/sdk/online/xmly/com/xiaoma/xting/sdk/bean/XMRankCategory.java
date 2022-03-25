package com.xiaoma.xting.sdk.bean;

import com.xiaoma.xting.online.model.INamed;
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/17
 */
public class XMRankCategory extends XMRank implements Serializable, INamed {

    public XMRankCategory(Rank rank) {
        super(rank);
    }

    public static List<XMRankCategory> convert(List<XMRank> xmRanks) {
        if (xmRanks == null || xmRanks.size() == 0) return null;
        ArrayList<XMRankCategory> rankCategoryBeans = new ArrayList<>(xmRanks.size());
        for (XMRank xmRank : xmRanks) {
            rankCategoryBeans.add(new XMRankCategory(xmRank.getSDKBean()));
        }
        return rankCategoryBeans;
    }

    @Override
    public String getName() {
        return getRankTitle();
    }

    public long getRankListId() {
        return getSDKBean().getRankListId();
    }
}
