package com.xiaoma.club.contact.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.xiaoma.club.R;
import com.xiaoma.club.common.constant.UnreadMsgEventTag;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.model.ContactGroup;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.contact.model.NewFriendInfo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: loren
 * Date: 2018/12/27 0027
 */

public class ContactVM extends BaseViewModel {

    private MutableLiveData<Integer> newFriendCount = new MutableLiveData<>();
    private MutableLiveData<Map<String, Integer>> contactCounts;
    private MutableLiveData<XmResource<List<GroupCardInfo>>> groupList;
    private MutableLiveData<XmResource<List<User>>> friendList;
    private Map<String, Integer> counts = new HashMap<>();
    private static final int LIMIT = 1000;
    public static final String[] COUNT_KEY = new String[]{"group_count", "friend_count"};

    public ContactVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getNewFriendCount() {
        return newFriendCount;
    }

    public MutableLiveData<Map<String, Integer>> getContactCounts() {
        if (contactCounts == null) {
            contactCounts = new MutableLiveData<>();
        }
        return contactCounts;
    }


    public MutableLiveData<XmResource<List<GroupCardInfo>>> getGroupList() {
        if (groupList == null) {
            groupList = new MutableLiveData<>();
        }
        return groupList;
    }

    public MutableLiveData<XmResource<List<User>>> getFriendList() {
        if (friendList == null) {
            friendList = new MutableLiveData<>();
        }
        return friendList;
    }

    public void requestNewFriendList(String userId) {
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
                if (model != null && !model.isEmpty()) {
                    int count = 0;
                    for (NewFriendInfo info : model) {
                        if (!info.isApproved()) {
                            count++;
                        }
                    }
                    getNewFriendCount().postValue(count);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
            }

        });
    }

    public void setGroupCount(int count) {
        counts.put(COUNT_KEY[0], count);
        getContactCounts().postValue(counts);
    }

    public void setFriendCount(int count) {
        counts.put(COUNT_KEY[1], count);
        getContactCounts().postValue(counts);
    }

    public void getAllContact(final String userId) {
        counts = new HashMap<>();
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                getContactGroup(userId, new SimpleCallback<Integer>() {

                    @Override
                    public void onSuccess(Integer result) {
                        doNext();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        doNext();
                    }
                });
            }
        }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                getContactFriend(userId);
            }
        }).start();
    }

    public void getContactGroup(String userId, final SimpleCallback<Integer> callBack) {
        ClubRequestManager.requestGroupContact(userId, new CallbackWrapper<List<GroupCardInfo>>() {
            @Override
            public List<GroupCardInfo> parse(String data) throws Exception {
                XMResult<List<GroupCardInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<GroupCardInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(final List<GroupCardInfo> model) {
                super.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    // 存入群组数据库
                    ClubRepo.getInstance().getGroupRepo().insertAll(model);
                    counts.put(COUNT_KEY[0], model.size());
                    getGroupList().postValue(XmResource.response(model));
                    if (callBack != null) {
                        callBack.onSuccess(model.size());
                    } else {
                        getContactCounts().postValue(counts);
                    }
                } else {
                    getGroupList().postValue(XmResource.<List<GroupCardInfo>>failure(getApplication().getString(R.string.data_empty_club)));
                    counts.put(COUNT_KEY[0], 0);
                    if (callBack != null) {
                        callBack.onError(0, null);
                    } else {
                        getContactCounts().postValue(counts);
                    }
                }
                // 缓存联系人群组数据
                ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                    @Override
                    public void run() {
                        ClubRepo.getInstance().getContactGroupsRepo().saveContactGroups(model);
                    }
                });
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                getGroupList().postValue(XmResource.<List<GroupCardInfo>>error(msg));
                counts.put(COUNT_KEY[0], 0);
                if (callBack != null) {
                    callBack.onError(0, null);
                } else {
                    getContactCounts().postValue(counts);
                }
            }
        });
    }

    public void getContactFriend(String userId) {
        ClubRequestManager.requestFriendContact(userId, new CallbackWrapper<List<User>>() {
            @Override
            public List<User> parse(String data) throws Exception {
                XMResult<List<User>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<User>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(final List<User> model) {
                super.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    ClubRepo.getInstance().getUserRepo().insertAll(model);
                    counts.put(COUNT_KEY[1], model.size());
                    getFriendList().postValue(XmResource.response(model));
                    // 缓存联系人数据
                    ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                        @Override
                        public void run() {
                            ClubRepo.getInstance().getContactUserRepo().saveContactUsers(model);
                        }
                    });
                } else {
                    counts.put(COUNT_KEY[1], 0);
                    getFriendList().postValue(XmResource.<List<User>>failure(getApplication().getString(R.string.data_empty_club)));
                }
                getContactCounts().postValue(counts);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                counts.put(COUNT_KEY[1], 0);
                getFriendList().postValue(XmResource.<List<User>>error(msg));
                getContactCounts().postValue(counts);
            }
        });
    }

    public void quitGroup(final long groupId, final String hxId) {
        ClubRequestManager.quitGroup(groupId, new CallbackWrapper<ClubBaseResult>() {
            @Override
            public ClubBaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                if (model != null && model.isSuccess()) {
                    //退群成功清除通讯录数据库中对应群
                    ClubRepo.getInstance().getContactGroupsRepo().delete(new ContactGroup(UserUtil.getCurrentUid(), groupId));
                    //退出部落由于环信异步问题概率性出现导致去刷新未读消息数的时候还未退群成功，退出的部落里的未读消息还显示在当前消息里，此时再手动调一次
                    //若已退群成功了getConversation会空指针，故catch一下
                    try {
                        EMClient.getInstance().chatManager().getConversation(hxId).markAllMessagesAsRead();
                    } catch (Exception e) {
                        KLog.d("EMClient getConversation null");
                        e.printStackTrace();
                    } finally {
                        EventBus.getDefault().post(true, UnreadMsgEventTag.MAIN_UNREAD_EVENT_TAG);
                        getContactGroup(String.valueOf(UserUtil.getCurrentUser().getId()), null);
                    }
                } else {
                    XMToast.toastException(getApplication(), R.string.quit_group_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(getApplication(), R.string.quit_group_failed);
            }
        });
    }

    public void deleteFriend(String otherUserId) {
        ClubRequestManager.deleteFriend(otherUserId, new CallbackWrapper<ClubBaseResult>() {
            @Override
            public ClubBaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                if (model != null && model.isSuccess()) {
                    // 2019-1-24 20:19:58: 产品确定不需要发送删除的消息
                    /*final EMMessage message = EMMessage.createTxtSendMessage(getApplication().getString(R.string.chat_remove_friend_notify), otherUserHxAcount);
                    IMUtils.sendMessage(message);*/
                    //没有刷新会话未读消息数，发个eventbus
                    EventBus.getDefault().post(true, UnreadMsgEventTag.MAIN_UNREAD_EVENT_TAG);
                    getContactFriend(String.valueOf(UserUtil.getCurrentUser().getId()));
                } else {
                    XMToast.toastException(getApplication(), R.string.delete_friend_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(getApplication(), R.string.delete_friend_failed);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        newFriendCount = null;
        contactCounts = null;
        groupList = null;
        friendList = null;
    }
}
