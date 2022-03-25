package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.media.model.BaseMediaDetails;
import com.kaolafm.opensdk.api.media.model.Host;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMBaseMediaDetails<T extends BaseMediaDetails> extends XMBean<T> {

    public XMBaseMediaDetails(T t) {
        super(t);
    }

    public long getId() {
        return getSDKBean().getId();
    }

    public void setId(long id) {
        getSDKBean().setId(id);
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public void setName(String name) {
        getSDKBean().setName(name);
    }

    public String getImg() {
        return getSDKBean().getImg();
    }

    public void setImg(String img) {
        getSDKBean().setImg(img);
    }

    public int getFollowedNum() {
        return getSDKBean().getFollowedNum();
    }

    public void setFollowedNum(int followedNum) {
        getSDKBean().setFollowedNum(followedNum);
    }

    public int getIsOnline() {
        return getSDKBean().getIsOnline();
    }

    public void setIsOnline(int isOnline) {
        getSDKBean().setIsOnline(isOnline);
    }

    public int getListenNum() {
        return getSDKBean().getListenNum();
    }

    public void setListenNum(int listenNum) {
        getSDKBean().setListenNum(listenNum);
    }

    public String getDesc() {
        return getSDKBean().getDesc();
    }

    public void setDesc(String desc) {
        getSDKBean().setDesc(desc);
    }

    public int getCommentNum() {
        return getSDKBean().getCommentNum();
    }

    public void setCommentNum(int commentNum) {
        getSDKBean().setCommentNum(commentNum);
    }

    public int getIsSubscribe() {
        return getSDKBean().getIsSubscribe();
    }

    public void setIsSubscribe(int isSubscribe) {
        getSDKBean().setIsSubscribe(isSubscribe);
    }

    public int getType() {
        return getSDKBean().getType();
    }

    public void setType(int type) {
        getSDKBean().setType(type);
    }

    public List<XMHost> getHost() {
        List<Host> hostList = getSDKBean().getHost();
        if (hostList == null || hostList.isEmpty()) {
            return null;
        }

        List<XMHost> xmHostList = new ArrayList<>(hostList.size());
        for (Host host : hostList) {
            xmHostList.add(new XMHost(host));
        }
        return xmHostList;
    }

    public void setHost(List<XMHost> host) {
        if (host == null || host.isEmpty()) {
            getSDKBean().setHost(null);
        }

        List<Host> hostList = new ArrayList<>(host.size());
        for (XMHost xmHost : host) {
            hostList.add(xmHost.getSDKBean());
        }

        getSDKBean().setHost(hostList);
    }

    public List<String> getKeyWords() {
        return getSDKBean().getKeyWords();
    }

    public void setKeyWords(List<String> keyWords) {
        getSDKBean().setKeyWords(keyWords);
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
