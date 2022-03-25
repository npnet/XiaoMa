package com.xiaoma.songname.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.songname.common.manager.RequestManager;
import com.xiaoma.songname.model.SortListBean;
import com.xiaoma.songname.model.UserSignBean;

public class SortVM extends BaseViewModel {

    private MutableLiveData<XmResource<SortListBean>> mRankUserList;
    private MutableLiveData<XmResource<UserSignBean>> mUserSign;
    private MutableLiveData<XmResource<String>> mAddFriend;

    public SortVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<SortListBean>> getRankUserInfoList() {
        if (mRankUserList == null) {
            mRankUserList = new MutableLiveData<>();
        }
        return mRankUserList;
    }

    public MutableLiveData<XmResource<UserSignBean>> getUserSign() {
        if (mUserSign == null) {
            mUserSign = new MutableLiveData<>();
        }
        return mUserSign;
    }

    public MutableLiveData<XmResource<String>> addFriendLiveData() {
        if (mAddFriend == null) {
            mAddFriend = new MutableLiveData<>();
        }
        return mAddFriend;
    }

    public void fetchRankUserInfoList(String uid) {
        getRankUserInfoList().setValue(XmResource.<SortListBean>loading());
        RequestManager.getInstance().getRankingList(uid, new ResultCallback<XMResult<SortListBean>>() {
            @Override
            public void onSuccess(XMResult<SortListBean> result) {
                getRankUserInfoList().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getRankUserInfoList().setValue(XmResource.<SortListBean>failure(msg));
            }
        });
    }

    public void fetchUserSign(String uid, String otherId) {
        RequestManager.getInstance().getUserSign(uid, otherId, new ResultCallback<XMResult<UserSignBean>>() {

            @Override
            public void onSuccess(XMResult<UserSignBean> result) {
                getUserSign().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getUserSign().setValue(XmResource.<UserSignBean>failure(msg));
            }
        });
    }

    public void addFriend(String fromId, String toId) {
        RequestManager.getInstance().requestAddFriend(fromId, toId, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                addFriendLiveData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                addFriendLiveData().setValue(XmResource.<String>failure(msg));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRankUserList = null;
    }
}
