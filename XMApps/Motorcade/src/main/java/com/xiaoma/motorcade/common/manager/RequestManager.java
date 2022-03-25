package com.xiaoma.motorcade.common.manager;

import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.IsMyFriendResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.ModelCallback;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: loren
 * Date: 2019/1/2 0002
 */

public class RequestManager {
    private static String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getBusiness();
    }

    /**
     * 根据口令申请进入车队
     */
    public static void requestAddGroup(String command, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.REQUEST_ADD_MOTORCADE;
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", command);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 创建车队
     */
    public static void createMotorcade(String motorcadeName, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CREATE_MOTORCADE;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamName", motorcadeName);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 修改车队名称
     */
    public static void ChangeMotorcadeName(String newName, long id, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CHANGE_MOTORCADE_NAME;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamName", newName);
        params.put("carTeamId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 修改用户昵称
     */
    public static void changeNickName(String newName, long id, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CHANGE_MOTORCADE_NAME;
        Map<String, Object> params = new HashMap<>();
        params.put("nickName", newName);
        params.put("carTeamId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 修改是否接收语音
     */
    public static void ChangeReceiveVoice(int reciveVoice, String id, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CHANGE_MOTORCADE_NAME;
        Map<String, Object> params = new HashMap<>();
        params.put("reciveVoice", reciveVoice);
        params.put("carTeamId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 退出车队
     */
    public static void quitMotorcade(String id, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.QUIT_MOTORCADE;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 抢麦
     */
    public static void robMicrophone(long carTeamId, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.ROB_MICROPHONE;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", carTeamId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取车队成员
     */
    public static void getTeamMemebers(String id, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CAR_TEAM_MEMEBERS;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", id);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取当前用户信息
     */
    public static void getUser(String hxAccount, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.USER_DETAIL_INFO;
        Map<String, Object> params = new HashMap<>();
        params.put("hxAccount", hxAccount);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取车队当前是否有会议室
     */
    public static void getMeeting(long carTeamId, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CREATE_OR_JOIN_MEETING;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", carTeamId);
        params.put("type", 1);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 本地创建会议成功后向后台汇报账号密码
     */
    public static void reportMeeting(long carTeamId, String meetingId, String pass, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CREATE_OR_JOIN_MEETING;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", carTeamId);
        params.put("account", meetingId);
        params.put("password", pass);
        params.put("type", 0);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 上报在线状态
     */
    public static void reportOnline(long carTeamId, boolean isOnline, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.REPORT_ONLINE;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", carTeamId);
        params.put("status", isOnline ? 1 : 0);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取当前用户所在车队数量
     */
    public static void getTeamCount(StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CAR_TEAM_COUNT;
        Map<String, Object> params = new HashMap<>();
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 取消抢麦
     */
    public static void cancelRobMicrophone(long carTeamId, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CANCEL_ROB;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", carTeamId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 释放麦
     */
    public static void releaseRobMicrophone(long carTeamId, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.RELEASE_ROB;
        Map<String, Object> params = new HashMap<>();
        params.put("carTeamId", carTeamId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 车队列表
     */
    public static void requestCarList(String userId, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.CAR_TEAM_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取当前用户所在车队数量
     */
    public static void addFriend(long fromId, long toId, StringCallback callback) {
        String url = getPreUrl() + NetWorkConstants.REQUEST_ADD_FRIEND;
        Map<String, Object> params = new HashMap<>();
        params.put("fromId", fromId);
        params.put("toId", toId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 判断对方是否是好友
     */
    public static void isMyFriend(String uid, final SimpleCallback<Boolean> callback) {
        String myUid = LoginManager.getInstance().getLoginUserId();
        String url = getPreUrl() + NetWorkConstants.IS_MY_FRIEND;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", myUid);
        params.put("otherId", uid);
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                IsMyFriendResult result = GsonHelper.fromJson(response.body(), IsMyFriendResult.class);
                if (result != null) {
                    callback.onSuccess(result.isMyFriend());
                } else {
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onError(-1, "");
            }
        });
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
            MotorcadeRepo.getInstance().getUserRepo().put(user);
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
            MotorcadeRepo.getInstance().getGroupRepo().put(groupInfo);
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
}