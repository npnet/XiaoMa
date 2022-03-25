package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.club.common.viewmodel.ViewState;
import com.xiaoma.component.base.BaseViewModel;

/**
 * Created by LKF on 2019-1-9 0009.
 */
public class SearchLocationVM extends BaseViewModel {
    private final MutableLiveData<String> mKeyword = new MutableLiveData<>();
    private final MutableLiveData<ViewState> mViewState = new MutableLiveData<>();

    public SearchLocationVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getKeyword() {
        return mKeyword;
    }

    public MutableLiveData<ViewState> getViewState() {
        return mViewState;
    }
}
