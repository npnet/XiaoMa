package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.TextUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.chat.model.IsMyFriendResult;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;

import java.util.Collection;

/**
 * Created by LKF on 2019-1-23 0023.
 */
public class FriendDetailVM extends BaseViewModel {
    private MutableLiveData<User> mUser = new MutableLiveData<>();

    public FriendDetailVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<User> getUser() {
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
        User user = ClubRepo.getInstance().getUserRepo().getByKey(hxId);
        if (user != null) {
            getUser().setValue(user);
        }
        if (!ClubNetWorkUtils.isConnected(this.getApplication())) {
            return;
        }
        ClubRequestManager.getUserByHxAccount(hxId, new com.xiaoma.network.callback.SimpleCallback<User>() {

            @Override
            public void onSuccess(User model) {
                if (model != null) {
                    getUser().postValue(model);
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    public void isMyFriend(final Context context, final String toHxId, final SimpleCallback<Boolean> callback) {
        final User currUser = UserUtil.getCurrentUser();
        if (currUser == null)
            return;
        if (!ClubNetWorkUtils.isConnected(context)) {
            final Collection<String> contacts = ClubRepo.getInstance().getFriendshipRepo().getFriendHxAccounts();
            if (contacts != null && contacts.contains(toHxId)) {
                callback.onSuccess(true);
            } else {
                callback.onSuccess(false);
            }
            return;
        }
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                getOtherIdByHxid(toHxId, new SimpleCallback<Long>() {
                    @Override
                    public void onSuccess(Long result) {
                        doNext(result);
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        callback.onError(errorCode, errorMessage);
                    }
                });
            }
        }).next(new Work<Long>() {
            @Override
            public void doWork(Long id) {
                ClubRequestManager.isMyFriend(String.valueOf(currUser.getId()), String.valueOf(id), new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        IsMyFriendResult result = GsonHelper.fromJson(response.body(), IsMyFriendResult.class);
                        if (result != null) {
                            callback.onSuccess(result.isMyFriend());
                            // 缓存好友关系
                            if (result.isMyFriend()) {
                                ClubRepo.getInstance().getFriendshipRepo().append(toHxId);
                            } else {
                                ClubRepo.getInstance().getFriendshipRepo().delete(toHxId);
                            }
                        } else {
                            callback.onSuccess(false);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        callback.onSuccess(false);
                    }
                });
            }
        }).start();
    }

    public void requestToAddFriend(final Context context, final String toHxId) {
        if (!ClubNetWorkUtils.isConnected(context)) {
            return;
        }
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                getOtherIdByHxid(toHxId, new SimpleCallback<Long>() {
                    @Override
                    public void onSuccess(Long result) {
                        doNext(result);
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        XMToast.toastException(context, R.string.request_add_friend_failed);
                    }
                });
            }
        }).next(new Work<Long>() {
            @Override
            public void doWork(Long id) {
                ClubRequestManager.requestAddFriend(String.valueOf(UserUtil.getCurrentUser().getId()), String.valueOf(id), new CallbackWrapper<ClubBaseResult>() {
                    @Override
                    public ClubBaseResult parse(String data) throws Exception {
                        return GsonHelper.fromJson(data, ClubBaseResult.class);
                    }

                    @Override
                    public void onSuccess(ClubBaseResult model) {
                        super.onSuccess(model);
                        if (model != null) {
                            if (model.isSuccess()) {
                                XMToast.showToast(context, R.string.request_add_friend);
                            } else if (model.isFrequently()) {
                                XMToast.showToast(context, model.getResultMessage());
                            } else if (model.isFriend()) {
                                XMToast.showToast(context, R.string.request_is_friend);
                            } else {
                                XMToast.toastException(context, R.string.request_add_friend_failed);
                            }
                        } else {
                            XMToast.toastException(context, R.string.request_add_friend_failed);
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        if (TextUtil.hasChinese(msg)) {
                            XMToast.showToast(context, msg);
                        } else {
                            XMToast.toastException(context, R.string.request_add_friend_failed);
                        }
                    }
                });
            }
        }).start();
    }


    public void getOtherIdByHxid(String hxId, final SimpleCallback<Long> callBack) {
        User user = getUser().getValue();
        if (user != null) {
            callBack.onSuccess(user.getId());
            return;
        }
        user = ClubRepo.getInstance().getUserRepo().getByKey(hxId);
        if (user != null) {
            callBack.onSuccess(user.getId());
            return;
        }
        if (!ClubNetWorkUtils.isConnected(this.getApplication())) {
            return;
        }
        ClubRequestManager.getUserByHxAccount(hxId, new com.xiaoma.network.callback.SimpleCallback<User>() {

            @Override
            public void onSuccess(User model) {
                if (model != null) {
                    callBack.onSuccess(model.getId());
                }
            }

            @Override
            public void onError(int code, String msg) {
                callBack.onError(0, null);
            }
        });
    }
}
