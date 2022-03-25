package com.xiaoma.motorcade.common.im;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.EnvType;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.model.ShareLocationParam;
import com.xiaoma.utils.BuildConfig;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

public class IMUtils {
    private final static String TAG = "IMUtils";

    // 体验环境
    private static final String HX_APP_KEY_TEST = "xiaomalixing#xiaomaexperience";

    // 正式环境(带消息撤回功能)
    private static final String HX_APP_KEY_RELEASE = "xiaomalixing#xiaomazhuomian";

    public static synchronized void hxImInit(final Context context, boolean isFactoryUser) {
        // 初始化环信
        final EMClient emClient = EMClient.getInstance();
        emClient.init(context, createEMOptions(
                EnvType.OFFICE == ConfigManager.EnvConfig.getEnvType() ?
                        HX_APP_KEY_RELEASE : HX_APP_KEY_TEST,
                isFactoryUser));
        emClient.setDebugMode(BuildConfig.DEBUG);
    }

    private static EMOptions createEMOptions(String hxAppKey, boolean isFactoryUser) {
        EMOptions options = new EMOptions();
        // 默认好友请求是自动同意的，如果要手动同意需要在初始化SDK时设置此标记为false
        options.setAcceptInvitationAlways(false);
        // 如果用到已读的回执需要把这个flag设置成true
        options.setRequireAck(true);
        // 如果用到已发送的回执需要把这个flag设置成true
        options.setRequireDeliveryAck(false);
        //因工厂模式测试完并不会执行logout操作，防止正式用户使用不会一开机就登录环信
        options.setAutoLogin(!isFactoryUser);
        options.setAppKey(hxAppKey);
        return options;
    }

    public static synchronized void loginHx(String account, String password, final EMCallBack callBack) {
        if (EMClient.getInstance().isConnected()) {
            if (!TextUtils.isEmpty(account) && account.equals(EMClient.getInstance().getCurrentUser())) {
                KLog.d(TAG, "HxLogin isConnected, no login more ->");
                if (callBack != null) callBack.onSuccess();
                return;
            } else {
                loginOutHx();
            }
        }
        // 当前没有已登录的环信ID 或者 小马用户的环信ID和环信SDK中的ID不一致时,重新登录环信
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            EMClient.getInstance().login(account, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    KLog.d(TAG, "HxLogin onSuccess ->");
                    // 根据环信文档:登录成功后需要调用以下两个方法同步群组和会话
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    try {
                        EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (callBack != null) callBack.onSuccess();
                }

                @Override
                public void onError(int code, String message) {
                    KLog.e(TAG, StringUtil.format("HxLogin onError -> [ code: %d, message: %s ]", code, message));
                    if (callBack != null) callBack.onError(code, message);
                }

                @Override
                public void onProgress(int progress, String status) {
                    KLog.d(TAG, StringUtil.format("HxLogin onProgress -> [ progress: %d, status: %s ]", progress, status));
                    if (callBack != null) callBack.onProgress(progress, status);
                }
            });
        } else if (TextUtils.isEmpty(account)) {
            KLog.e(TAG, StringUtil.format("HxLogin onError -> [ code: %d, message: %s ]", EMError.INVALID_USER_NAME, "account is empty"));
            if (callBack != null) callBack.onError(EMError.INVALID_USER_NAME, "account is empty");
        } else if (TextUtils.isEmpty(password)) {
            KLog.e(TAG, StringUtil.format("HxLogin onError -> [ code: %d, message: %s ]", EMError.INVALID_PASSWORD, "password is empty"));
            if (callBack != null) callBack.onError(EMError.INVALID_PASSWORD, "password is empty");
        }
    }

    public static void loginOutHx(final EMCallBack callBack) {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                KLog.e(TAG, "HxLoginOut onSuccess -> ");
                if (callBack != null) callBack.onSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
                KLog.e(TAG, StringUtil.format("HxLoginOut onProgress -> [ progress: %d, status: %s ]", progress, status));
                if (callBack != null) callBack.onProgress(progress, status);
            }

            @Override
            public void onError(int code, String message) {
                KLog.e(TAG, StringUtil.format("HxLoginOut onError -> [ code: %d, message: %s ]", code, message));
                if (callBack != null) callBack.onError(code, message);
            }
        });
    }

    public static void loginOutHx() {
        EMClient.getInstance().logout(true);
    }


    public static void sendLocationCMDMessage(String chatId, ShareLocationParam param) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(MotorcadeConstants.IMMessageType.LOCATION_SHARE);
        cmdMsg.setTo(chatId);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setAttribute(MotorcadeConstants.LOCATION_SHARE_PARAM, GsonHelper.toJson(param));
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    //退出位置共享时透传给其他用户
    public static void sendOutLocationCMDMessage(String chatId) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(MotorcadeConstants.IMMessageType.LOCATION_OUT);
        cmdMsg.setTo(chatId);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }
}