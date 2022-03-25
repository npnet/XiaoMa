package com.xiaoma.club.discovery.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.club.R;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.discovery.model.SearchResultInfo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/25 0025
 */

public class SearchVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<Integer>>> searchCounts;
    private MutableLiveData<XmResource<List<GroupCardInfo>>> groupResults;
    private MutableLiveData<XmResource<List<User>>> friendResults;

    public SearchVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<Integer>>> getSearchCounts() {
        if (searchCounts == null) {
            searchCounts = new MutableLiveData<>();
        }
        return searchCounts;
    }

    public MutableLiveData<XmResource<List<GroupCardInfo>>> getGroupResults() {
        if (groupResults == null) {
            groupResults = new MutableLiveData<>();
        }
        return groupResults;
    }

    public MutableLiveData<XmResource<List<User>>> getFriendResults() {
        if (friendResults == null) {
            friendResults = new MutableLiveData<>();
        }
        return friendResults;
    }

    public void searchAll(final String keyWord) {
        getSearchCounts().setValue(XmResource.<List<Integer>>loading());
        ClubRequestManager.searchAllResultList(keyWord, new CallbackWrapper<SearchResultInfo>() {
            @Override
            public SearchResultInfo parse(String data) throws Exception {
                XMResult<SearchResultInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<SearchResultInfo>>() {
                }.getType());
                if (result != null) {
                    return result.getData();
                }
                return null;
            }

            @Override
            public void onSuccess(SearchResultInfo model) {
                super.onSuccess(model);
                if (model != null) {
                    fetchCount(model);
                    fetchGroup(model.getQuns());
                    fetchFriend(model.getUsers());
                } else {
                    getSearchCounts().postValue(XmResource.<List<Integer>>failure(getApplication().getString(R.string.data_empty_club)));
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getSearchCounts().postValue(XmResource.<List<Integer>>error(msg));
            }
        });
    }

    private void fetchCount(SearchResultInfo model) {
        List<Integer> counts = new ArrayList<>();
        counts.add(0, model.getGroupCount());
        counts.add(1, model.getUserCount());
        getSearchCounts().postValue(XmResource.response(counts));
    }

    private void fetchGroup(final List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos == null || groupCardInfos.isEmpty()) {
            getGroupResults().postValue(XmResource.<List<GroupCardInfo>>failure(getApplication().getString(R.string.data_empty_club)));
            return;
        }
        ClubRepo.getInstance().getGroupRepo().insertAll(groupCardInfos);
        getGroupResults().postValue(XmResource.response(groupCardInfos));
        // 缓存群组数据
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                ClubRepo.getInstance().getGroupRepo().insertAll(groupCardInfos);
            }
        });
    }

    private void fetchFriend(final List<User> users) {
        if (users == null || users.isEmpty()) {
            getFriendResults().postValue(XmResource.<List<User>>failure(getApplication().getString(R.string.data_empty_club)));
            return;
        }
        ClubRepo.getInstance().getUserRepo().insertAll(users);
        getFriendResults().postValue(XmResource.response(users));
        // 缓存用户数据
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                ClubRepo.getInstance().getUserRepo().insertAll(users);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchCounts = null;
        groupResults = null;
        friendResults = null;
    }

}
