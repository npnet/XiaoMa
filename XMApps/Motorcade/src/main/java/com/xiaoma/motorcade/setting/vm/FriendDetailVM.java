package com.xiaoma.motorcade.setting.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.BaseResult;
import com.xiaoma.motorcade.setting.model.UserDetailInfo;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;

/**
 * 简介:
 *
 * @author lingyan
 */
public class FriendDetailVM extends BaseViewModel {
    private MutableLiveData<XmResource<UserDetailInfo>> mUser = new MutableLiveData<>();

    public FriendDetailVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<UserDetailInfo>> getUser() {
        if (mUser == null) {
            mUser = new MutableLiveData<>();
        }
        return mUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mUser = null;
    }

    public void getUserDetails(String hxId) {
        if (!NetworkUtils.isConnected(this.getApplication())) {
            return;
        }
        RequestManager.getUser(hxId, new CallbackWrapper<UserDetailInfo>() {
            @Override
            public UserDetailInfo parse(String data) throws Exception {
                XMResult<UserDetailInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<UserDetailInfo>>() {
                }.getType());
                if (result != null) {
                    return result.getData();
                }
                return null;
            }

            @Override
            public void onSuccess(UserDetailInfo model) {
                super.onSuccess(model);
                if (model != null) {
                    getUser().postValue(XmResource.response(model));
                } else {
                    getUser().postValue(XmResource.<UserDetailInfo>failure("empty"));
                }

            }

            @Override
            public void onError(int code, String msg) {
                getUser().postValue(XmResource.<UserDetailInfo>error("null"));
            }
        });
    }

    public void requestAddFriend(final Context context, long fromId, long toId) {
        RequestManager.addFriend(fromId, toId, new CallbackWrapper<BaseResult>() {

            @Override
            public BaseResult parse(String data) throws Exception {
                BaseResult baseResult = GsonHelper.fromJson(data, BaseResult.class);
                return baseResult;
            }

            @Override
            public void onSuccess(BaseResult model) {
                if (model != null) {
                    if (model.isSuccess()) {
                        XMToast.toastSuccess(context, R.string.request_add_friend);
                    } else if (model.isFriend()) {
                        XMToast.toastSuccess(context, R.string.request_is_friend);
                    } else {
                        if (!TextUtils.isEmpty(model.getResultMessage())) {
                            String toast = "";
                            if (model.getResultMessage().contains("发送请求过于频繁")) {
                                toast = context.getString(R.string.frequnsy_too_many);
                            } else {
                                toast = model.getResultMessage();
                            }
                            XMToast.toastException(context, toast);
                        }
                    }
                } else {
                    XMToast.toastException(context, R.string.request_add_friend_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(context, R.string.net_work);
            }
        });
    }

}
