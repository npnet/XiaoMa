package com.xiaoma.login.common;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.carlib.manager.WifiObserver;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

public class UserUtil {
    public static final String TAG = UserUtil.class.getSimpleName();
    // 密码hash的盐
    private static final String salt = "salt";
    private static int retryCount = 0;

    /**
     * 合并服務器user信息，和本地的user信息
     * 防止在本地保存的用戶信息被服務器覆蓋
     *
     * @param serverUser
     * @return
     */
    public static User merge(User serverUser) {
        if (serverUser == null) return null;
        User cachedUser = UserBindManager.getInstance().getCachedUserById(serverUser.getId());
        if (cachedUser != null) {
            if (TextUtils.isEmpty(serverUser.getCommonKey()) && !TextUtils.isEmpty(cachedUser.getCommonKey())) {
                serverUser.setCommonKey(cachedUser.getCommonKey());
            }
            if (TextUtils.isEmpty(serverUser.getBluetoothKey()) && !TextUtils.isEmpty(cachedUser.getBluetoothKey())) {
                serverUser.setBluetoothKey(cachedUser.getBluetoothKey());
            }
            if (cachedUser.getFaceId() > 0) {
                serverUser.setFaceId(cachedUser.getFaceId());
            }
            if (TextUtils.isEmpty(serverUser.getPassword()) && !TextUtils.isEmpty(cachedUser.getPassword())) {
                serverUser.setPassword(cachedUser.getPassword());
            }
        }
        return serverUser;
    }

    public static String getPasswdHash(String passwd) {
//        return MD5Utils.getStringMD5(MD5Utils.getStringMD5(passwd) + salt);
        return MD5Utils.getStringMD5(passwd);
    }

    public static boolean checkPasswd(String passwd, String hash) {
//        return MD5Utils.getStringMD5(MD5Utils.getStringMD5(passwd) + salt).equals(hash);
        return MD5Utils.getStringMD5(passwd).equals(hash);
    }

    public static void fetchUsers() {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                Application appContext = AppHolder.getInstance().getAppContext();
                String iccid = ConfigManager.DeviceConfig.getICCID(appContext);
                RequestManager.getUserListByIccid(iccid, new ResultCallback<XMResult<List<User>>>() {
                    @Override
                    public void onSuccess(XMResult<List<User>> result) {
                        List<User> userList = result.getData();
                        List<User> mergeUsers = new ArrayList<>();
                        if (result.isSuccess() && !CollectionUtil.isListEmpty(userList)) {
                            for (User user : userList) {
                                mergeUsers.add(merge(user));
                            }
                            UserBindManager.getInstance().saveCacheUsers(mergeUsers);
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
            }
        });
    }

    public static void fetchUserValid(final CallBack callback) {
        if (ConfigManager.FileConfig.isUserValid()) return;
        Log.d(TAG, "start fetchUserValid");
        String iccid = ConfigManager.DeviceConfig.getICCID(AppHolder.getInstance().getAppContext());
        RequestManager.getUserByIccid(iccid, new ResultCallback<XMResult<User>>() {
            @Override
            public void onSuccess(XMResult<User> result) {
                Log.d(TAG, "onSuccess: get user info success");
                if (ConfigManager.FileConfig.isUserValid()) return;
                retryCount = 0;
                ConfigManager.FileConfig.validUser();
                fetchUsers();
                if (callback != null) {
                    callback.onSuccess();
                } else {
                    XMToast.toastSuccess(AppHolder.getInstance().getAppContext(),
                            R.string.get_user_info_success);
                }
                WifiObserver.getInstance().removeForUserListener();
                Log.d(TAG, "onSuccess: fetch valid user success");
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d(TAG, "onFailure: get user info fail");
                if (ConfigManager.FileConfig.isUserValid()) return;
                if (retryCount < 3) {
                    retryCount++;
                    ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!ConfigManager.FileConfig.isUserValid()) {
                                Log.d(TAG, "retry get user info times:" + retryCount);
                                fetchUserValid(callback);
                            }
                        }
                    }, 2000, Priority.HIGH);
                } else {
                    retryCount = 0;
                    if (callback != null) {
                        callback.onFail(code, msg);
                    } else {
                        XMToast.toastException(AppHolder.getInstance().getAppContext(),
                                R.string.get_user_info_fail);
                    }
                    Log.d(TAG, "onFailure: fetch valid user fail");
                }
            }
        });
    }

    public interface CallBack {
        void onSuccess();

        void onFail(int code, String msg);
    }
}
