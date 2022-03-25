package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;
import com.ximalaya.ting.android.opensdk.model.ranks.RankItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMRank extends XMBean<Rank> {
    public XMRank(Rank rank) {
        super(rank);
    }

    /**
     * 用于获取具体榜单内容的key，它作为入参用于获取具体某个排行榜内容
     */
    public String getRankKey() {
        return  getSDKBean().getRankKey();
    }

    public void setRankKey(String rankKey) {
        getSDKBean().setRankKey(rankKey);
    }

    /**
     * 榜单标题
     */
    public String getRankTitle() {
        return  getSDKBean().getRankTitle();
    }

    public void setRankTitle(String rankTitle) {
        getSDKBean().setRankTitle(rankTitle);
    }

    /**
     * 榜单类型，1-节目榜单
     */
    public int getRankType() {
        return  getSDKBean().getRankType();
    }

    public void setRankType(int rankType) {
        getSDKBean().setRankType(rankType);
    }

    /**
     * 榜单副标题
     */
    public String getRankSubTitle() {
        return  getSDKBean().getRankSubTitle();
    }

    public void setRankSubTitle(String rankSubTitle) {
        getSDKBean().setRankSubTitle(rankSubTitle);
    }

    /**
     * 榜单计算周期，单位为天
     * @return
     */
    public int getRankPeriod() {
        return  getSDKBean().getRankPeriod();
    }

    public void setRankPeriod(int rankPeriod) {
        getSDKBean().setRankPeriod(rankPeriod);
    }

    /**
     * 榜单计算周期类型，比如“日榜”、“周榜”等
     */
    public String getRankPeriodType() {
        return  getSDKBean().getRankPeriodType();
    }

    public void setRankPeriodType(String rankPeriodType) {
        getSDKBean().setRankPeriodType(rankPeriodType);
    }

    /**
     * 该榜单内条目总数，比如100
     */
    public int getRankItemNum() {
        return  getSDKBean().getRankItemNum();
    }

    public void setRankItemNum(int rankItemNum) {
        getSDKBean().setRankItemNum(rankItemNum);
    }

    /**
     * 该榜单相对其他榜单的排序值，值越小越靠前
     */
    public int getRankOrderNum() {
        return  getSDKBean().getRankOrderNum();
    }

    public void setRankOrderNum(int rankOrderNum) {
        getSDKBean().setRankOrderNum(rankOrderNum);
    }

    /**
     * 榜单封面图URL
     */
    public String getCoverUrl() {
        return  getSDKBean().getCoverUrl();
    }

    public void setCoverUrl(String coverUrl) {
        getSDKBean().setCoverUrl(coverUrl);
    }

    /**
     * 榜单所属分类ID
     */
    public long getCategoryId() {
        return  getSDKBean().getCategoryId();
    }

    public void setCategoryId(long categoryId) {
        getSDKBean().setCategoryId(categoryId);
    }

    /**
     * 榜单内容类型，album-专辑，track-声音
     */
    public String getRankContentType() {
        return  getSDKBean().getRankContentType();
    }

    public void setRankContentType(String rankCOntentType) {
        getSDKBean().setRankContentType(rankCOntentType);
    }

    /**
     * 榜单内排名第一的条目的ID
     */
    public long getRankFirstItemId() {
        return  getSDKBean().getRankFirstItemId();
    }

    public void setRankFirstItemId(long rankFirstItemId) {
        getSDKBean().setRankFirstItemId(rankFirstItemId);
    }

    /**
     * 榜单内排名第一的条目的标题
     */
    public String getRankFirstItemTitle() {
        return  getSDKBean().getRankFirstItemTitle();
    }

    public void setRankFirstItemTitle(String rankFirstItemTitle) {
        getSDKBean().setRankFirstItemTitle(rankFirstItemTitle);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    /**
     * 榜单首页显示的条目列表,对应RankItem
     */
    public List<XMRankItem> getRankItemList() {
        List<RankItem> rankItemList = getSDKBean().getRankItemList();
        ArrayList<XMRankItem> xmRankItems = new ArrayList<>();
        if (rankItemList != null && !rankItemList.isEmpty()) {
            for (RankItem rankItem : rankItemList) {
                if (rankItem == null) {
                    continue;
                }
                xmRankItems.add(new XMRankItem(rankItem));
            }
        }
        return xmRankItems;
    }

    public void setRankItemList(List<XMRankItem> xmRankItemList) {
        if (xmRankItemList == null) {
            getSDKBean().setRankItemList(null);
            return;
        }
        List<RankItem> rankItemList = new ArrayList<>();
        for (XMRankItem xmRankItem : xmRankItemList) {
            if (xmRankItem == null) {
                continue;
            }
            rankItemList.add(xmRankItem.getSDKBean());
        }
        getSDKBean().setRankItemList(rankItemList);
    }

    public long getRankListId() {
        return getSDKBean().getRankListId();
    }

    public void setRankListId(long rankListId) {
        getSDKBean().setRankListId(rankListId);
    }
}
