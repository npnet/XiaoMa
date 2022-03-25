package com.xiaoma.xting.search.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMHotWord;
import com.xiaoma.xting.sdk.bean.XMHotWordList;
import com.xiaoma.xting.search.model.SearchBean;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/09
 *     desc   :
 * </pre>
 */
public class SearchVM extends BaseViewModel {
    private MutableLiveData<List<SearchBean>> mSearchHistoryList;

    public SearchVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<List<SearchBean>> getSearchHistoryList() {
        if (mSearchHistoryList == null) {
            mSearchHistoryList = new MutableLiveData<>();
        }
        return mSearchHistoryList;
    }


    public MutableLiveData<XmResource<List<XMHotWord>>> getRecommendList() {
        final MutableLiveData<XmResource<List<XMHotWord>>> liveData = new MutableLiveData<>();
        OnlineFMFactory.getInstance().getSDK().getHotWords(10, new XMDataCallback<XMHotWordList>() {
            @Override
            public void onSuccess(@Nullable XMHotWordList data) {
                KLog.i(data.toString());
                if (data != null) {
                    liveData.setValue(XmResource.response(data.getHotWordList()));
                } else {
                    liveData.setValue(XmResource.<List<XMHotWord>>failure("null"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                liveData.setValue(XmResource.<List<XMHotWord>>error(code, msg));
            }
        });
        return liveData;
    }

    public List<SearchBean> getHistoryList() {
        List<SearchBean> list=XtingUtils.getDBManager().queryAll(SearchBean.class);
        Collections.reverse(list);
        return list;
    }

    public void insertHistory2DB(String keyword) {
        XtingUtils.getDBManager().save(new SearchBean(keyword));
    }

}
