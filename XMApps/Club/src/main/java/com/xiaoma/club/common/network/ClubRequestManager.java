package com.xiaoma.club.common.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.discovery.model.SearchResultInfo;
import com.xiaoma.club.msg.chat.model.MuteHxUser;
import com.xiaoma.club.msg.chat.repo.GroupMuteUserRepo;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.ModelCallback;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.utils.GsonHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: loren
 * Date: 2019/1/2 0002
 */

public class ClubRequestManager {
    public static final String TAG = "ClubRequestManager";

    private static String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getBusiness();
    }

    /**
     * 获取推荐活跃部落列表
     */
    public static void getHotGroupList(int page, int pageSize, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.GET_HOT_GROUP_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", pageSize);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 模糊搜索
     */
    public static void searchAllResultList(String keyWord, ModelCallback<SearchResultInfo> callback) {
        String url = getPreUrl() + ClubNetWorkConstants.SEARCH_ALL_RESULT_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("word", keyWord);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 编辑用户个人签名
     */
    public static void editUserSign(String sign, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.EDIT_USER_SIGN;
        Map<String, Object> params = new HashMap<>();
        params.put("signature", sign);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 通过userId获取用户信息
     */
    public void getUserById(final long userId, SimpleCallback<User> callback) {
        final String url = getPreUrl() + ClubNetWorkConstants.GET_USER_BY_ID;
        XmHttp.getDefault().getString(url, Collections.<String, Object>singletonMap("id", userId), new UserCallbackProxy(callback));
    }

    /**
     * 通过环信账号查询User
     */
    public static void getUserByHxAccount(String hxAccount, final SimpleCallback<User> callback) {
        final String url = getPreUrl() + ClubNetWorkConstants.GET_USER_BY_HXID;
        XmHttp.getDefault().getString(url, Collections.<String, Object>singletonMap("hxAccount", hxAccount), new UserCallbackProxy(callback));
    }

    /**
     * 通过环信群ID获取群数据(后台字段里的roomId(其实是环信id))
     */
    public static void getGroupByHxId(String hxGroupId, final SimpleCallback<GroupCardInfo> callback) {
        final String url = getPreUrl() + ClubNetWorkConstants.GET_GROUP_BY_HXID;
        XmHttp.getDefault().getString(url, Collections.<String, Object>singletonMap("groupId", hxGroupId), new GroupInfoCallbackProxy(callback));
    }

    /**
     * 通过环信群ID查询禁言列表
     *
     * @param hxGroupId 环信群ID
     */
    public static void getGroupMuteListByHxId(String hxGroupId, SimpleCallback<Set<String>> callback) {
        final String url = getPreUrl() + ClubNetWorkConstants.GET_GROUP_MUTE_LIST_HXID;
        XmHttp.getDefault().getString(url, Collections.<String, Object>singletonMap("roomId", hxGroupId), new GroupMuteListCallbackProxy(callback, hxGroupId));
    }

    /**
     * 根据群id查询群的基本信息(也是后台字段里的roomId(其实是环信id)(无语...))(更改了数据结构，加入了当前用户在该群身份字段)
     */
    public static void queryGroupById(String id, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.QUERY_GROUP_BY_ID;
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 群主、管理修改群简介
     */
    public static void editGroupSign(long id, String sign, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.EDIT_GROUP_SIGN;
        Map<String, Object> params = new HashMap<>();
        params.put("qunId", id);
        params.put("qunBrief", sign);
        XmHttp.getDefault().postString(url, params, callback);
    }

    /**
     * 根据群id查询群的成员信息
     */
    public static void queryGroupMemberById(String id, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.QUERY_GROUP_MEMBER_BY_ID;
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 根据群id查询群的活动信息
     */
    public static void queryGroupActivityById(String id, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.QUERY_GROUP_ACTIVITY_BY_ID;
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取表情/快捷回复列表
     *
     * @param type 1：表情 ； 2：快速回复语
     */
    public static void requestFaceQuickList(String type, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.REQUEST_FACE_QUICK_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 申请加群
     */
    public static void requestAddGroup(long id, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.REQUEST_ADD_GROUP;
        Map<String, Object> params = new HashMap<>();
        params.put("qunId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 判断对方是否是好友
     */
    public static void isMyFriend(String fromId, String toId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.IS_MY_FRIEND;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", fromId);
        params.put("otherId", toId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 申请加好友
     */
    public static void requestAddFriend(String fromId, String toId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.REQUEST_ADD_FRIEND;
        Map<String, Object> params = new HashMap<>();
        params.put("fromId", fromId);
        params.put("toId", toId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取好友请求记录列表
     */
    public static void requestNewFriendList(String userId, int limit, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.REQUEST_NEWFRIEND_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 同意对方好友请求
     */
    public static void approveNewFriend(long messageId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.APPROVE_NEWFRIEND;
        Map<String, Object> params = new HashMap<>();
        params.put("messageId", messageId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 群主踢人
     */
    public static void kickOutMember(long groupId, long userId, String type, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.KICK_OUT_MEMBER;
        Map<String, Object> params = new HashMap<>();
        params.put("qunId", groupId);
        params.put("delUserId", userId);
        params.put("type", type);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 通讯录部落列表
     */
    public static void requestGroupContact(String userId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.CONTACT_GROUP_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 通讯录车友列表
     */
    public static void requestFriendContact(String userId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.CONTACT_FRIEND_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 退出部落
     */
    public static void quitGroup(final long groupId, SimpleCallback<ClubBaseResult> callback) {
        String url = getPreUrl() + ClubNetWorkConstants.CONTACT_QUIT_GROUP;
        Map<String, Object> params = new HashMap<>();
        params.put("qunId", groupId);
        XmHttp.getDefault().getString(url, params, new CallbackWrapper<ClubBaseResult>(callback) {
            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                GroupCardInfo group = ClubRepo.getInstance().getGroupRepo().get(groupId);
                if (group != null) {
                    LogUtil.logI(TAG, "quitGroup: Success: groupName: %s", group.getNick());
                    EMClient.getInstance().chatManager().deleteConversation(group.getHxGroupId(), true);
                } else {
                    LogUtil.logI(TAG, "quitGroup: Success: group is null");
                }
            }

            @Override
            public ClubBaseResult parse(String data) {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }
        });
    }

    /**
     * 删除车友
     */
    public static void deleteFriend(String otherUserId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.CONTACT_DEL_FRIEND;
        Map<String, Object> params = new HashMap<>();
        params.put("delUserId", otherUserId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 当前用户是否已加入车队
     */
    public static void isCarTeamMember(long userId, long teamId, StringCallback callback) {
        String url = getPreUrl() + ClubNetWorkConstants.IS_IN_TEAM;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", teamId);
        params.put("userId", userId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    private static abstract class ModelCallbackProxy<M> extends ModelCallback<M> {
        private SimpleCallback<M> mCallback;

        ModelCallbackProxy(SimpleCallback<M> callback) {
            mCallback = callback;
        }

        abstract protected void putStore(@NonNull M model);

        @Override
        final public void onSuccess(M model) {
            if (model != null) {
                putStore(model);
                if (mCallback != null) {
                    mCallback.onSuccess(model);
                }
            } else {
                if (mCallback != null) {
                    mCallback.onError(-1, "");
                }
            }
        }

        @Override
        final public void onError(int code, String msg) {
            if (mCallback != null) {
                mCallback.onError(-1, "");
            }
        }
    }

    private static class UserCallbackProxy extends ModelCallbackProxy<User> {
        UserCallbackProxy(SimpleCallback<User> callback) {
            super(callback);
        }

        @Override
        protected void putStore(@NonNull User user) {
            ClubRepo.getInstance().getUserRepo().insert(user);
        }

        @Override
        public User parse(String data) {
            final User user = GsonHelper.fromJson(data, User.class);
            if (user != null && user.getId() != 0) {
                return user;
            }
            final XMResult<User> result = GsonHelper.fromJson(data, new TypeToken<XMResult<User>>() {
            }.getType());
            if (result != null && result.isSuccess()) {
                return result.getData();
            }
            return null;
        }
    }

    private static class GroupInfoCallbackProxy extends ModelCallbackProxy<GroupCardInfo> {
        GroupInfoCallbackProxy(SimpleCallback<GroupCardInfo> callback) {
            super(callback);
        }

        @Override
        protected void putStore(@NonNull GroupCardInfo groupInfo) {
            ClubRepo.getInstance().getGroupRepo().insert(groupInfo);
        }

        @Override
        public GroupCardInfo parse(String data) {
            final GroupCardInfo groupCardInfo = GsonHelper.fromJson(data, GroupCardInfo.class);
            if (groupCardInfo != null && groupCardInfo.getId() != 0) {
                return groupCardInfo;
            }
            final XMResult<GroupCardInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<GroupCardInfo>>() {
            }.getType());
            if (result != null && result.isSuccess()) {
                return result.getData();
            }
            return null;
        }
    }

    private static class GroupMuteListCallbackProxy extends ModelCallbackProxy<Set<String>> {
        private String mHxGroupId;

        GroupMuteListCallbackProxy(SimpleCallback<Set<String>> callback, String hxGroupId) {
            super(callback);
            mHxGroupId = hxGroupId;
        }

        @Override
        protected void putStore(@NonNull Set<String> muteUsers) {
            LogUtil.logI("GroupMuteListCallbackProxy", "putStore( muteUsers: %s )", muteUsers);
            final GroupMuteUserRepo repo = ClubRepo.getInstance().getGroupMuteUserRepo();
            repo.putMuteUsers(mHxGroupId, muteUsers);
        }

        @Override
        public Set<String> parse(String data) {
            final Set<String> muteUsers = new HashSet<>();
            final XMResult<List<MuteHxUser>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<MuteHxUser>>>() {
            }.getType());
            if (result != null && result.isSuccess()) {
                final List<MuteHxUser> users = result.getData();
                if (users != null) {
                    for (final MuteHxUser user : users) {
                        if (!TextUtils.isEmpty(user.getHxAccount())) {
                            muteUsers.add(user.getHxAccount());
                        }
                    }
                }
            }
            LogUtil.logI("GroupMuteListCallbackProxy", "parse( data: %s )", data);
            return muteUsers;
        }
    }
}