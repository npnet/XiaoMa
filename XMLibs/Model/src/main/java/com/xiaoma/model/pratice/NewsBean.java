package com.xiaoma.model.pratice;

import java.util.List;

/**
 * @author taojin
 * @date 2019/6/6
 */
public class NewsBean {

    private int totalNum;
    private List<NewsChannelBean> channelList;


    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<NewsChannelBean> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<NewsChannelBean> channelList) {
        this.channelList = channelList;
    }
}
