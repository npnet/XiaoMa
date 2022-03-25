package com.xiaoma.club.discovery.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.discovery.model.SearchHistoryInfo;
import com.xiaoma.club.discovery.repo.SearchHistoryInfoRepo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/25 0025
 */

public class SearchHistoryVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<SearchHistoryInfo>>> historyList;
    private SearchHistoryInfoRepo mRepo = ClubRepo.getInstance().getSearchHistoryInfoRepo();

    public SearchHistoryVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<SearchHistoryInfo>>> getHistoryList() {
        if (historyList == null) {
            historyList = new MutableLiveData<>();
        }
        return historyList;
    }

    public void getSearchHistory(LifecycleOwner owner) {
        mRepo.queryAll().observe(owner, new Observer<List<SearchHistoryInfo>>() {
            @Override
            public void onChanged(@Nullable List<SearchHistoryInfo> list) {
                getHistoryList().setValue(XmResource.response(list));
            }
        });
    }


    public void insertSearchHistory(LifecycleOwner owner, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        keyword = keyword.trim().replaceAll("[\\t\\r\\n]", "");
        if (TextUtils.isEmpty(keyword))
            return;
        mRepo.insertKeyword(keyword);
        getSearchHistory(owner);
    }


    public void clearAllHistory() {
        mRepo.clearAll();
        getHistoryList().setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        historyList = null;
    }
}
