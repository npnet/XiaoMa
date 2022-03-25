package com.xiaoma.club.common.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.contact.model.NewFriendInfo;
import com.xiaoma.club.contact.ui.NewFriendDrawerFragment;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.GsonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LKF on 2018/10/10 0010.
 */
public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> mNewMessageCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> mTabSelectIndex = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> haveNewFriend = new MutableLiveData<>();
    private final MutableLiveData<List<NewFriendInfo>> newFriendlist = new MutableLiveData<>();
    private static final int LIMIT = 1000;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getNewMessageCount() {
        return mNewMessageCount;
    }

    public MutableLiveData<Integer> getTabSelectIndex() {
        return mTabSelectIndex;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public MutableLiveData<Boolean> getHaveNewFriend() {
        return haveNewFriend;
    }

    public MutableLiveData<List<NewFriendInfo>> getNewFriendlist() {
        return newFriendlist;
    }

    /**
     * 更新未读消息
     */
    public void updateUnreadMsgCount() {
        SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
            @Override
            public void doWork(Object lastResult) {
                doNext(EMClient.getInstance().chatManager().getUnreadMessageCount());
            }
        }).next(new Work<Integer>() {
            @Override
            public void doWork(Integer newMsgCount) {
                getNewMessageCount().setValue(newMsgCount);
            }
        }).start();
    }

    public void requestNewFriendList(String userId, final NewFriendDrawerFragment.NewFriendDrawerCallBack callBack) {
        ClubRequestManager.requestNewFriendList(userId, LIMIT, new CallbackWrapper<List<NewFriendInfo>>() {
            @Override
            public List<NewFriendInfo> parse(String data) throws Exception {
                XMResult<List<NewFriendInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<NewFriendInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(List<NewFriendInfo> model) {
                super.onSuccess(model);
                if (callBack != null) {
                    callBack.onSuccess(model);
                }
                if (model != null && !model.isEmpty()) {
                    isHaveNewFriend(model);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callBack != null) {
                    callBack.onFailed();
                }
                super.onError(code, msg);
            }

        });
    }

    private void isHaveNewFriend(List<NewFriendInfo> model) {
        List<NewFriendInfo> notApproved = new ArrayList<>();
        boolean haveNotApprove = false;
        for (NewFriendInfo info : model) {
            if (!info.isApproved()) {
                haveNotApprove = true;
                model.remove(info);
                notApproved.add(info);
                break;
            } else {
                haveNotApprove = false;
            }
        }
        getHaveNewFriend().postValue(haveNotApprove);
        sortListPost(model, notApproved);
    }

    private void sortListPost(List<NewFriendInfo> model, List<NewFriendInfo> notApproved) {
        //排序，规则：待同意的在上面，按收到时间排序
        sort(model);
        sort(notApproved);
        if (notApproved != null && !notApproved.isEmpty()) {
            model.addAll(0, notApproved);
        }
        getNewFriendlist().postValue(model);
    }

    private void sort(List<NewFriendInfo> model) {
        if (model == null || model.size() < 2) {
            return;
        }
        //按时间降序
        Collections.sort(model, new Comparator<NewFriendInfo>() {
            @Override
            public int compare(@NonNull NewFriendInfo o1, @NonNull NewFriendInfo o2) {
                return -Long.compare(o1.getCreateDate(), o2.getCreateDate());
            }
        });
    }

    public void addNewFriendMsg(String userId) {
        getHaveNewFriend().postValue(true);
        requestNewFriendList(userId, null);
    }

    public void approveNewFriend(long msgId, final SimpleCallback<Void> callback) {
        ClubRequestManager.approveNewFriend(msgId, new CallbackWrapper<ClubBaseResult>() {
            @Override
            public ClubBaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                if (callback != null) {
                    if (model != null && model.isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        int code = -1;
                        try {
                            code = model != null ? Integer.parseInt(model.getResultCode()) : -1;
                        } catch (Exception ignored) {
                        }
                        callback.onError(code, "");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (callback != null) {
                    callback.onError(code, msg);
                }
            }
        });
    }

    public void requestCurrentUser(String hxId) {
        ClubRequestManager.getUserByHxAccount(hxId, new SimpleCallback<User>() {
            @Override
            public void onSuccess(User model) {
                if (model != null) {
                    getUser().postValue(model);
                    UserManager.getInstance().notifyUserUpdate(model);
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }
}
