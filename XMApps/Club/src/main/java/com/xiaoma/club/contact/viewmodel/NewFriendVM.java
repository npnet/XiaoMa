package com.xiaoma.club.contact.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.club.contact.model.NewFriendInfo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/27 0027
 */

public class NewFriendVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<NewFriendInfo>>> newFriendList;

    public NewFriendVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<NewFriendInfo>>> getNewFriendList() {
        if (newFriendList == null) {
            newFriendList = new MutableLiveData<>();
        }
        return newFriendList;
    }

    public void getFriendRequestRecord() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        newFriendList = null;
    }
}
