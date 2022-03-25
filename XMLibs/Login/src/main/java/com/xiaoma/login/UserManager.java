package com.xiaoma.login;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.business.receive.UserReceiver;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ProcessUtils;
import com.xiaoma.utils.encrypt.AESUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.manager.FactoryLoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.manager.TravellerLoginType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaoma.login.common.LoginConstants.USER_AES_KEY;

/**
 * Created by youthyj on 2018/9/7.
 */
public class UserManager {
    private static UserManager instance;
    private Context appContext;
    private User currentUser;
    private List<OnUserUpdateListener> listeners = new ArrayList<>();

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    private UserManager() {
    }

    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        registerReceiver();
        prepareUser();
        listenForLogin();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginConstants.ACTION_ON_USER_UPDATE);
        appContext.registerReceiver(new UserReceiver(), filter);
    }

    private void prepareUser() {
        final String userId = LoginManager.getInstance().getLoginUserId();
        User user = getUser(userId);
        if (user != null) {
            setCurrentUser(user);
        } else {
            setCurrentUser(null);
            return;
        }

        //游客模式不要从后台更新
        if (user.getId() == LoginConstants.TOURIST_USER_ID) {
            return;
        }

        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                    @Override
                    public void run() {
                        fetchUserByUserId(userId, new UserCallback() {
                            @Override
                            public void onSuccess(User user) {
                                notifyUserUpdate(user);
                            }

                            @Override
                            public void onFailure(int errCode, String errMsg) {
                                // 本地无用户数据 + 通过userId获取用户失败, 执行注销操作
                                //！！！！！！！此处不能直接登出，会造成登录状态缺失，打个日志就可以了！！！！！！！！！
                                KLog.e("prepare user fetch failure");
//                                LoginManager.getInstance().logout();
                            }
                        });
                    }
                });
                return false;
            }
        });
    }

    private void listenForLogin() {
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User user) {
                if (!ProcessUtils.isMainProcess(appContext)) {
                    return;
                }
                notifyUserUpdate(user);
            }

            @Override
            public void onLogout() {
                setCurrentUser(null);
            }
        });
    }


    public User getUser(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        User memory = getUserByMemory(userId);
        if (memory != null) {
            return memory;
        }
        return getUserByFile(userId);
    }

    public User getCurrentUser() {
        String userId = LoginManager.getInstance().getLoginUserId();
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        return getUser(userId);
    }

    public void fetchUserByUserId(String userId, final UserCallback callback) {
        if (callback == null) {
            return;
        }
        if (TextUtils.isEmpty(userId)) {
            callback.onFailure(-1, "user id is empty");
            return;
        }
        String prefix = ConfigManager.EnvConfig.getEnv().getBusiness();
        String url = prefix + "user/getUserById.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);

        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (!response.isSuccessful()) {
                    callback.onFailure(-1, "network error");
                    return;
                }
                String data = response.body();
                XMResult<User> result = GsonHelper.fromJson(data, new TypeToken<XMResult<User>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    callback.onFailure(-1, "user parse error");
                    return;
                }
                User user = result.getData();
                if (user == null) {
                    callback.onFailure(-1, "user parse error");
                    return;
                }
                callback.onSuccess(user);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    private User getUserByMemory(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        if (currentUser == null) {
            return null;
        }
        if (userId.equals(String.valueOf(currentUser.getId()))) {
            return currentUser;
        }
        return null;
    }

    private User getUserByFile(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        File userFile = ConfigManager.FileConfig.getUserFile(userId);
        String aesStr = FileUtils.read(userFile);
        String userJson = AESUtils.decrypt(aesStr, USER_AES_KEY);
        User user = GsonHelper.fromJson(userJson, User.class);
        if (user != null) {
            if (!userId.equals(String.valueOf(user.getId()))) {
                return null;
            }
        }
        return user;
    }

    /**
     * 仅供Login模块使用，其他地方请勿使用！！！
     *
     * @param user user
     */
    protected void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * 从网络更新用户信息，分发全局。
     *
     * @param user 从网络更新的User数据
     */
    public void notifyUserUpdate(User user) {
        notifyUserUpdate(user, false);
    }

    /**
     * 更新用户数据
     *
     * @param user          要更新的用户数据
     * @param isForceUpdate 是否
     */
    public void notifyUserUpdate(User user, boolean isForceUpdate) {
        if ("null".equals(user.getCommonKey())) {
            user.setCommonKey(null);
        } else if ("null".equals(user.getBluetoothKey())) {
            user.setBluetoothKey(null);
        }
        if (!isForceUpdate) {
            user = UserUtil.merge(user);
        }
        setCurrentUser(user);
        saveUserToFile(user);
        sendUpdateBroadcast(user);
        if (!(LoginTypeManager.getInstance().getLoginType() instanceof TravellerLoginType)
                && !(LoginTypeManager.getInstance().getLoginType() instanceof FactoryLoginType)) {
            // 游客模式,工厂模式 User不存数据库
            UserBindManager.getInstance().saveCacheUser(user);
        }
    }

    private void saveUserToFile(User user) {
        if (user == null) {
            return;
        }
        String userId = String.valueOf(user.getId());
        String content = GsonHelper.toJson(user);
        content = AESUtils.encryp(content, USER_AES_KEY);
        File userFile = ConfigManager.FileConfig.getUserFile(userId);
        FileUtils.writeCover(content, userFile);
    }

    private void sendUpdateBroadcast(User user) {
        String data = "";
        if (user != null) {
            data = String.valueOf(user.getId());
        }
        Intent intent = new Intent();
        intent.setAction(LoginConstants.ACTION_ON_USER_UPDATE);
        intent.putExtra("userId", data);
        intent.putExtra("data", (Parcelable) user);
        appContext.sendBroadcast(intent);
    }

    public void onUserUpdate(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        setCurrentUser(getUserByFile(userId));
        callbackUserUpdate();
    }


    private void callbackUserUpdate() {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }
        for (OnUserUpdateListener l : listeners) {
            if (l == null) {
                return;
            }
            try {
                l.onUpdate(currentUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addOnUserUpdateListener(OnUserUpdateListener l) {
        if (l == null || listeners.contains(l)) {
            return false;
        }
        return listeners.add(l);
    }

    public boolean removeOnUserUpdateListener(OnUserUpdateListener l) {
        return listeners.remove(l);
    }

    public interface OnUserUpdateListener {
        void onUpdate(@Nullable User user);
    }

    public interface UserCallback {
        void onSuccess(User user);

        void onFailure(int errCode, String errMsg);
    }
}
