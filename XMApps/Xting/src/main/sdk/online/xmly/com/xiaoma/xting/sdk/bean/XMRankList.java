package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMRankList extends XMBean<RankList> {
    public XMRankList(RankList rankList) {
        super(rankList);
    }

    public List<XMRank> getRankList() {
        List<Rank> rankList = getSDKBean().getRankList();
        List<XMRank> xmRanks = new ArrayList<>();
        if (rankList != null && !rankList.isEmpty()) {
            for (Rank rank : rankList) {
                //移除付费专辑
                if (rank == null || rank.getRankTitle().contains("付费")) {
                    continue;
                }
                xmRanks.add(new XMRank(rank));
            }
        }
        return xmRanks;
    }

    public void setRankList(List<XMRank> xmRankList) {
        if (xmRankList == null) {
            getSDKBean().setRankList(null);
            return;
        }
        List<Rank> rankList = new ArrayList<>();
        for (XMRank xmRank : xmRankList) {
            if (xmRank == null) {
                continue;
            }
            rankList.add(xmRank.getSDKBean());
        }
        getSDKBean().setRankList(rankList);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
