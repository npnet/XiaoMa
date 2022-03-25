package com.xiaoma.login;

import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;

import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.db.FaceDao;
import com.xiaoma.login.db.UserBindDatabase;
import com.xiaoma.login.db.UserDao;
import com.xiaoma.login.sdk.CarKey;
import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.login.sdk.FaceId;
import com.xiaoma.model.User;

import java.util.Iterator;
import java.util.List;

/**
 * Created by KY
 * on 2019/1/10
 * <p>
 * desc: 缓存的本地用户，及钥匙人脸绑定关系管理类
 */
public class UserBindManager {
    private static UserBindManager instance;
    private UserBindDatabase mDB;

    public static UserBindManager getInstance() {
        if (instance == null) {
            synchronized (UserBindManager.class) {
                if (instance == null) {
                    instance = new UserBindManager();
                }
            }
        }
        return instance;
    }

    private UserBindManager() {
    }

    private synchronized UserBindDatabase getmDB() {
        if (mDB == null) {
            mDB = Room.databaseBuilder(AppHolder.getInstance().getAppContext().getApplicationContext(),
                    UserBindDatabase.class, ConfigManager.FileConfig.getLocalUserDBFile(AppHolder.getInstance().getAppContext()).getAbsolutePath())
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }
        return mDB;
    }

    public FaceDao getFaceDB() {
        return getmDB().faceDao();
    }

    public UserDao getUserDB() {
        return getmDB().userDao();
    }

    /**
     * 在车机开机时用于判断当前的车钥匙是否已经绑定过用户，如果绑定过则返回绑定的User
     *
     * @return 绑定的用户
     */
    public User getKeyBoundUser() {
        CarKey carKey = CarKeyFactory.getSDK().getCarKey();

        if (carKey == null) {
            return null;
        }

        for (User user : getCachedUser()) {
            if (user != null
                    && (carKey.getStr().equals(user.getBluetoothKeyId()) || carKey.getStr().equals(user.getNormalKeyId()))) {
                return user;
            }
        }

        return null;
    }

    /**
     * 在车机开机时用于判断当前的人脸是否已经绑定过用户，如果绑定过则返回绑定的User
     *
     * @return 绑定的用户
     */
    public User getFaceBoundUser(FaceId faceId) {
        if (faceId == null) {
            return null;
        }

        for (User user : getCachedUser()) {
            if (user != null && user.getFaceId() == faceId.getValue()) {
                return user;
            }
        }

        return null;
    }

    public FaceId getAvailableFaceId() {
        List<Integer> usedFaceIds = getUserDB().queryUsedFaceId();
        return FaceId.getAvailableByUsed(usedFaceIds);
    }

    /**
     * 直接保存，或覆盖用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    public boolean saveCacheUser(User user) {
        if (user.getId() == LoginConstants.TOURIST_USER_ID
                || user.getType() != 1) {
            return false;
        }

        if ("null".equals(user.getCommonKey())) {
            user.setCommonKey(null);
        } else if ("null".equals(user.getBluetoothKey())) {
            user.setBluetoothKey(null);
        }

        return getUserDB().saveCacheUser(user) > 0;
    }

    /**
     * 直接保存，或覆盖用户信息
     *
     * @param users 用户信息
     * @return 是否成功
     */
    public boolean saveCacheUsers(final List<User> users) {
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getId() == LoginConstants.TOURIST_USER_ID) {
                iterator.remove();
            }

            if ("null".equals(user.getCommonKey())) {
                user.setCommonKey(null);
            } else if ("null".equals(user.getBluetoothKey())) {
                user.setBluetoothKey(null);
            }

            User currentUser = UserManager.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getId() == user.getId()) {
                UserManager.getInstance().notifyUserUpdate(user);
            }
        }

        getmDB().runInTransaction(new Runnable() {
            @Override
            public void run() {
                getUserDB().removeAllCacheUser();
                getUserDB().saveCacheUsers(users);
            }
        });
        return true;
    }

    /**
     * 用于在主账户管理子账户时删除了子账户的同时删除本地的子账户缓存
     */
    public boolean removeCacheUser(long userId) {
        User user = new User();
        user.setId(userId);
        return getUserDB().removeCacheUserById(userId) > 0;
    }

    /**
     * 用于在主账户管理子账户时删除了子账户的同时删除本地的子账户缓存
     */
    public boolean removeAllCacheUser() {
        return getUserDB().removeAllCacheUser() > 0;
    }

    /**
     * 获取缓存在本地的所有账户，包括主账户，子账户
     *
     * @return 缓存在本地的User
     */
    public List<User> getCachedUser() {
        return getUserDB().getCachedUsers();
    }

    /**
     * 获取缓存在本地的特定账户
     *
     * @return 缓存在本地的User
     */
    public User getCachedUserById(long userId) {
        return getUserDB().getCachedUserById(userId);
    }

    /**
     * 获取缓存在本地的子账户
     *
     * @return 缓存在本地的子账户
     */
    public List<User> getCachedSubUser() {
        return getUserDB().getCachedSubUser();
    }
    /**
     * 解绑钥匙和账户的绑定关系
     *
     * @param keyId 钥匙Id
     */
    public boolean unBindKey(long userId, @NonNull String keyId, boolean isBlueTooth) {
        boolean isBle = CarKey.isBle(keyId);
        if (isBle) {
            return getUserDB().unBindBLEKey(userId) > 0;
        } else {
            return getUserDB().unBindNormalKey(userId) > 0;
        }
    }

    /**
     * 解绑账户绑定的人脸id
     */
    public boolean unBindFace(long userId) {
        return getUserDB().unBindFace(userId) > 0;
    }
}
