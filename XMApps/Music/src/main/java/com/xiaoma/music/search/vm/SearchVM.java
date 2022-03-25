package com.xiaoma.music.search.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.db.DBManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.search.model.SearchBean;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.Priority;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/09
 *     desc   :
 * </pre>
 */
public class SearchVM extends BaseViewModel {
    public static final int ERROR_CODE = -1;
    private static final String RECOMMEND_URL = "推荐的url";
    private MutableLiveData<List<SearchBean>> mSearchHistoryList;
    private MutableLiveData<XmResource<List<String>>> mALLRecommendList;

    public SearchVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<List<SearchBean>> getSearchHistoryList() {
        if (mSearchHistoryList == null) {
            mSearchHistoryList = new MutableLiveData<>();
        }
        return mSearchHistoryList;
    }

    public MutableLiveData<XmResource<List<String>>> getAllRecommendList() {
        if (mALLRecommendList == null) {
            mALLRecommendList = new MutableLiveData<>();
        }
        return mALLRecommendList;
    }

    public void fetchRecommendList() {
        OnlineMusicFactory.getKWAudioFetch().fetchSearchHotKeywords(new OnAudioFetchListener<List<String>>() {
            @Override
            public void onFetchSuccess(List<String> strings) {
                getAllRecommendList().postValue(XmResource.response(strings));
            }

            @Override
            public void onFetchFailed(String msg) {
                getAllRecommendList().postValue(XmResource.<List<String>>error(msg));
            }
        });

    }

    public void fetchSearchResult(String keyword, final SimpleCallback<List<SearchBean>> callback) {
        final Priority priority = Priority.HIGH;
        Map<String, Object> map = new HashMap<>();
        map.put("keyword", keyword);
        XmHttp.getDefault().getString(RECOMMEND_URL, map, new CallbackWrapper<List<SearchBean>>(priority, callback) {
            @Override
            public List<SearchBean> parse(String data) throws Exception {
                final JSONObject respObj = new JSONObject(data);
                return GsonHelper.fromJsonToList(respObj.optJSONArray("data").toString(), SearchBean[].class);
            }
        }, priority);
    }


    public List<SearchBean> getHistoryList() {
        KLog.i(DBManager.getInstance().getDBManager().queryAll(SearchBean.class).toString());
        List<SearchBean> list = DBManager.getInstance().getDBManager().queryAll(SearchBean.class);
        if (!list.isEmpty()){
            Collections.reverse(list);
        }
        return list;
    }

    public void insertHistory2DB(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        keyword = keyword.trim().replaceAll("\\t|\\r|\\n", "");
        List<SearchBean> searchBeans = getHistoryList();
        if (!searchBeans.isEmpty()) {
            for (int i = 0; i < searchBeans.size(); i++) {
                if (keyword.equals(searchBeans.get(i).name)){
                    DBManager.getInstance().getDBManager().delete(searchBeans.get(i));
                }
            }
        }
        DBManager.getInstance().getDBManager().save(new SearchBean(keyword));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mSearchHistoryList = null;
        mALLRecommendList = null;
    }

    public void clearAllHistory() {
        DBManager.getInstance().getDBManager().deleteAll(SearchBean.class);
        getSearchHistoryList().setValue(null);
    }
}
