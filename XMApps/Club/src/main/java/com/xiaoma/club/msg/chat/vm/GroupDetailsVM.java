package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.xiaoma.club.ClubConstant;
import com.xiaoma.club.R;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.model.GroupActivityInfo;
import com.xiaoma.club.msg.chat.model.GroupBasicInfo;
import com.xiaoma.club.msg.chat.model.GroupMemberInfo;
import com.xiaoma.club.msg.chat.ui.GroupDetailsActivityFragment;
import com.xiaoma.club.msg.chat.ui.GroupDetailsMemberFragment;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: loren
 * Date: 2019/1/7 0007
 */

public class GroupDetailsVM extends BaseViewModel {

    private MutableLiveData<GroupCardInfo> group;
    private MutableLiveData<String> identity;
    private MutableLiveData<List<GroupMemberInfo>> members;
    private MutableLiveData<List<GroupActivityInfo>> activityInfos;
    private static final String KICK_OUT_TYPE_FOREVER = "2";
    private static final int MUTE_PAGE = 0;
    private static final int MUTE_PAGE_SIZE = Integer.MAX_VALUE;

    public GroupDetailsVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<GroupCardInfo> getGroup() {
        if (group == null) {
            group = new MutableLiveData<>();
        }
        return group;
    }

    public MutableLiveData<String> getIdentity() {
        if (identity == null) {
            identity = new MutableLiveData<>();
        }
        return identity;
    }

    public MutableLiveData<List<GroupMemberInfo>> getMembers() {
        if (members == null) {
            members = new MutableLiveData<>();
        }
        return members;
    }

    public MutableLiveData<List<GroupActivityInfo>> getActivityInfos() {
        if (activityInfos == null) {
            activityInfos = new MutableLiveData<>();
        }
        return activityInfos;
    }

    public void requestGroupBasicInfo(String id, final SimpleCallback<GroupCardInfo> callBack) {
        GroupCardInfo group = ClubRepo.getInstance().getGroupRepo().get(id);
        if (group != null) {
            if (callBack != null) {
                callBack.onSuccess(group);
                return;
            } else {
                getGroup().postValue(group);
            }
        }
        ClubRequestManager.queryGroupById(id, new CallbackWrapper<GroupBasicInfo>() {
            @Override
            public GroupBasicInfo parse(String data) throws Exception {
                XMResult<GroupBasicInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<GroupBasicInfo>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(GroupBasicInfo model) {
                if (model == null) {
                    return;
                }
                if (callBack != null) {
                    callBack.onSuccess(model.getQun());
                } else {
                    getGroup().postValue(model.getQun());
                    getIdentity().postValue(model.getUserRole());
                }
                if (model.getQun() != null) {
                    ClubRepo.getInstance().getGroupRepo().insert(model.getQun());
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callBack != null) {
                    callBack.onError(0, null);
                }
            }
        });
    }

    public void getCurrentDensity(final String groupHxId, final SimpleCallback<String> callBack) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(groupHxId);
                    if (group != null) {
                        String userHxId = UserUtil.getCurrentUser().getHxAccount();
                        if (userHxId.equals(group.getOwner())) {
                            //当前用户是群主
                            callBack.onSuccess(ClubConstant.GroupIdentity.GROUP_OWNER);
                            return;
                        }
                        for (int i = 0; i < group.getAdminList().size(); i++) {
                            if (userHxId.equals(group.getAdminList().get(i))) {
                                //当前用户是管理
                                callBack.onSuccess(ClubConstant.GroupIdentity.GROUP_MANAGER);
                                return;
                            }
                        }
                        callBack.onSuccess(ClubConstant.GroupIdentity.GROUP_MEMBER);
                    }
                } catch (HyphenateException e) {
                    callBack.onError(0, null);
                    e.printStackTrace();
                }
            }
        });
    }

    public void quitGroup(String hxId, final SimpleCallback<Void> callBack) {
        GroupCardInfo group = getGroup().getValue();
        if (group == null) {
            group = ClubRepo.getInstance().getGroupRepo().get(hxId);
        }
        if (group == null) {
            callBack.onError(0, null);
            return;
        }
        ClubRequestManager.quitGroup(group.getId(), new CallbackWrapper<ClubBaseResult>() {
            @Override
            public ClubBaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                if (model != null && model.isSuccess()) {
                    callBack.onSuccess(null);
                } else {
                    callBack.onError(0, null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                callBack.onError(0, null);
            }
        });
    }

    public void requestGroupMember(final String id, final GroupDetailsMemberFragment.GroupDetailMemberCallBack callBack) {
        ClubRequestManager.queryGroupMemberById(id, new CallbackWrapper<List<GroupMemberInfo>>() {
            @Override
            public List<GroupMemberInfo> parse(String data) throws Exception {
                XMResult<List<GroupMemberInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<GroupMemberInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(List<GroupMemberInfo> model) {
                super.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    //排序，群主在第一，管理其次
//                    sortMember(model);
                    //标记该群禁言名单（群主、管理权限）
                    fetchMuteData(id, model);
                    callBack.onSuccess(model);
                } else {
                    callBack.onFailed();
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                callBack.onFailed();
//                XMToast.toastException(getApplication(), R.string.net_work_error);
            }
        });
    }

    public void requestGroupMember(final String id) {
        ClubRequestManager.queryGroupMemberById(id, new CallbackWrapper<List<GroupMemberInfo>>() {
            @Override
            public List<GroupMemberInfo> parse(String data) throws Exception {
                XMResult<List<GroupMemberInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<GroupMemberInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(List<GroupMemberInfo> model) {
                super.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    //排序，群主在第一，管理其次
//                    sortMember(model);
                    //标记该群禁言名单（群主、管理权限）
                    fetchMuteData(id, model);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(getApplication(), R.string.net_work_error);
            }
        });
    }

    private void sortMember(List<GroupMemberInfo> model) {
        Collections.sort(model, new Comparator<GroupMemberInfo>() {
            @Override
            public int compare(GroupMemberInfo o1, GroupMemberInfo o2) {
                try {
                    Integer identity1 = Integer.valueOf(o1.getUserRole());
                    Integer identity2 = Integer.valueOf(o2.getUserRole());
                    return identity2.compareTo(identity1);
                } catch (Exception e) {
                    KLog.e("String cast to int error");
                }
                return 0;
            }
        });
    }

    public void fetchMuteData(final String groupId, final List<GroupMemberInfo> model) {
        //获取当前群被禁言成员列表
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                Set<String> muteList = null;
                try {
                    Map<String, Long> muteMap = EMClient.getInstance().groupManager().fetchGroupMuteList(groupId, MUTE_PAGE, MUTE_PAGE_SIZE);
                    if (muteMap != null) {
                        muteList = muteMap.keySet();
                        if (!muteList.isEmpty()) {
                            for (GroupMemberInfo groupMemberInfo : model) {
                                if (muteList.contains(groupMemberInfo.getHxAccount())) {
                                    groupMemberInfo.setMute(true);
                                } else {
                                    groupMemberInfo.setMute(false);
                                }
                            }
                        }
                        // 拉取结果加到缓存中
                        ClubRepo.getInstance().getGroupMuteUserRepo().putMuteUsers(groupId, new HashSet<>(muteList));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getMembers().postValue(model);
                }
            }
        });
    }

    public void forbidSpeak(final String hxId, final String groupHxId, final SimpleCallback<Void> callBack) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                try {
                    IMUtils.muteGroupMember(groupHxId, hxId, 24 * 60 * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onError(0, null);
                    return;
                }
                requestGroupMember(groupHxId);
                callBack.onSuccess(null);
            }
        });
    }

    public void unForbidSpeak(final String hxId, final String groupHxId, final SimpleCallback<Void> callBack) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                try {
                    IMUtils.unMuteGroupMembers(groupHxId, hxId);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onError(0, null);
                    return;
                }
                requestGroupMember(groupHxId);
                callBack.onSuccess(null);
            }
        });
    }

    public void kickOut(final long userId, final String groupHxId, final SimpleCallback<Void> kickOutCallBack) {
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                requestGroupBasicInfo(groupHxId, new SimpleCallback<GroupCardInfo>() {
                    @Override
                    public void onSuccess(GroupCardInfo result) {
                        if (result != null) {
                            doNext(result.getId());
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        kickOutCallBack.onError(errorCode, errorMessage);
                    }
                });
            }
        }).next(new Work<Long>() {
            @Override
            public void doWork(Long groupId) {
                if (groupId != 0) {
                    requestToKickOut(groupHxId, groupId, userId, kickOutCallBack);
                }
            }
        }).start();

    }

    private void requestToKickOut(final String groupHxId, long groupId, long userId, final SimpleCallback<Void> kickOutCallBack) {
        ClubRequestManager.kickOutMember(groupId, userId, KICK_OUT_TYPE_FOREVER, new CallbackWrapper<ClubBaseResult>() {
            @Override
            public ClubBaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                if (model != null) {
                    if (model.isSuccess()) {
                        kickOutCallBack.onSuccess(null);
                        requestGroupMember(groupHxId);
                    } else {
                        kickOutCallBack.onError(0, null);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                kickOutCallBack.onError(0, null);
            }
        });
    }


    public void requestGroupActivity(String id, final GroupDetailsActivityFragment.GroupDetailCallBack callBack) {
        ClubRequestManager.queryGroupActivityById(id, new CallbackWrapper<List<GroupActivityInfo>>() {
            @Override
            public List<GroupActivityInfo> parse(String data) throws Exception {
                XMResult<List<GroupActivityInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<GroupActivityInfo>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(List<GroupActivityInfo> model) {
                super.onSuccess(model);
                callBack.onSuccess(model);
                if (model != null && !model.isEmpty()) {
                    getActivityInfos().postValue(model);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                callBack.onFailed();
            }
        });
    }

    private void sortActivity(List<GroupActivityInfo> list) {
        //排序活动，进行中》未开始》已结束，按开始时间降序
        Collections.sort(list, new Comparator<GroupActivityInfo>() {
            @Override
            public int compare(GroupActivityInfo o1, GroupActivityInfo o2) {
                try {
                    String onLineFlag1 = o1.getOnLineFlag();
                    String onLineFlag2 = o2.getOnLineFlag();
                    if (onLineFlag1.equals(onLineFlag2)) {
                        return -Long.compare(o1.getBeginTime(), o2.getBeginTime());
                    } else if (onLineFlag1.equals(ClubConstant.GroupActivity.ACTIVITY_ONGOING) && !onLineFlag2.equals(ClubConstant.GroupActivity.ACTIVITY_ONGOING)) {
                        return -1;
                    } else if (onLineFlag1.equals(ClubConstant.GroupActivity.ACTIVITY_FUTURE) && onLineFlag2.equals(ClubConstant.GroupActivity.ACTIVITY_ONGOING)) {
                        return 1;
                    } else if (onLineFlag1.equals(ClubConstant.GroupActivity.ACTIVITY_END) && !onLineFlag2.equals(ClubConstant.GroupActivity.ACTIVITY_END)) {
                        return 1;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    KLog.e("compare activity error");
                }
                return 0;
            }
        });
        getActivityInfos().postValue(list);
    }
}
