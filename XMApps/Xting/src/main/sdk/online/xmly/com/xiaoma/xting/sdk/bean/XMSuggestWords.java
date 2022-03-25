package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMSuggestWords extends XMBean<SuggestWords> {
    public XMSuggestWords(SuggestWords suggestWords) {
        super(suggestWords);
    }

    public int getAlbumTotalCount() {
        return getSDKBean().getAlbumTotalCount();
    }

    public void setAlbumTotalCount(int albumTotalCount) {
        getSDKBean().setAlbumTotalCount(albumTotalCount);
    }

    public List<XMAlbumResult> getAlbumList() {
        List<AlbumResult> albumList = getSDKBean().getAlbumList();
        List<XMAlbumResult> xmAlbumResults = new ArrayList<>();
        if (albumList != null && !albumList.isEmpty()) {
            for (AlbumResult albumResult : albumList) {
                if (albumResult == null) {
                    continue;
                }
                xmAlbumResults.add(new XMAlbumResult(albumResult));
            }
        }
        return xmAlbumResults;
    }

    public void setAlbumList(List<XMAlbumResult> xmAlbumList) {
        if (xmAlbumList == null) {
            getSDKBean().setAlbumList(null);
            return;
        }
        List<AlbumResult> albumList = new ArrayList<>();
        for (XMAlbumResult xmAlbumResult : xmAlbumList) {
            if (xmAlbumResult == null) {
                continue;
            }
            albumList.add(xmAlbumResult.getSDKBean());
        }
        getSDKBean().setAlbumList(albumList);
    }

    public int getKeywordTotalCount() {
        return getSDKBean().getKeywordTotalCount();
    }

    public void setKeywordTotalCount(int keywordTotalCount) {
        getSDKBean().setKeywordTotalCount(keywordTotalCount);
    }

    public List<XMQueryResult> getKeyWordList() {
        List<QueryResult> keyWordList = getSDKBean().getKeyWordList();
        List<XMQueryResult> xmKeyWordList = new ArrayList<>();
        if (keyWordList != null && !keyWordList.isEmpty()) {
            for (QueryResult queryResult : keyWordList) {
                if (queryResult == null) {
                    continue;
                }
                xmKeyWordList.add(new XMQueryResult(queryResult));
            }
        }
        return xmKeyWordList;
    }

    public void setKeyWordList(List<XMQueryResult> xmKeyWordList) {
        if (xmKeyWordList == null) {
            getSDKBean().setKeyWordList(null);
            return;
        }
        List<QueryResult> keyWordList = new ArrayList<>();
        for (XMQueryResult xmQueryResult : xmKeyWordList) {
            if (xmKeyWordList == null) {
                continue;
            }
            keyWordList.add(xmQueryResult.getSDKBean());
        }
        getSDKBean().setKeyWordList(keyWordList);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
