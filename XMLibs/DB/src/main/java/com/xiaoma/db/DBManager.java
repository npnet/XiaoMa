package com.xiaoma.db;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.utils.ConfigMD5Utils;

import java.io.File;

/**
 * @author zs
 * @date 2018/9/11 0011.
 */
public class DBManager {
    private static DBManager instance;
    private Context appContext;
    private IDatabase globalDB;
    private IDatabase localUserDB;
    private String userId;
    private IDatabase userDB;
    private RoomDatabase userRoomDB;
    private Class userRoomDBClass;

    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    public void with(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
    }

    public void initGlobalDB() {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null");
        }
        getDBManager().init(appContext);
    }

    public void initUserDB(long userId) {
        if (userId <= 0) {
            return;
        }
        initUserDB(String.valueOf(userId));
    }

    public void initUserDB(String userId) {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(userId) || userId.trim().isEmpty()) {
            return;
        }
        if (userId.equals(this.userId)) {
            return;
        }
        getUserDBManager(userId).init(appContext);
    }

    public void initGlobalDBCascade() {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null");
        }
        getDBManager().initCascade(appContext);
    }

    public void initUserDBCascade(long userId) {
        if (userId <= 0) {
            return;
        }
        initUserDBCascade(String.valueOf(userId));
    }

    public void initUserDBCascade(String userId) {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(userId) || userId.trim().isEmpty()) {
            return;
        }
        if (userId.equals(this.userId)) {
            return;
        }
        getUserDBManager(userId).initCascade(appContext);
    }

    public void onUserLogout() {
        this.userId = null;
        this.userDB = null;
    }

    public IDatabase getDBManager() {
        if (globalDB == null) {
            synchronized (LiteOrmDBManager.class) {
                if (globalDB == null) {
                    globalDB = new LiteOrmDBManager();
                }
            }
        }
        return globalDB;
    }

    public IDatabase getUserDBManager(long userId) {
        if (userId <= 0) {
            return null;
        }
        String userIdStr = String.valueOf(userId);
        return getUserDBManager(userIdStr);
    }

    public IDatabase getUserDBManager(String userId) {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(userId) || userId.trim().isEmpty()) {
            return null;
        }
        if (userId.equals(this.userId) && userDB != null) {
            //这里感觉不需要,但是为了以防万一
            if (!this.userDB.isDBManagerInitSuccess()) {
                this.userDB.init(appContext);
            }
            return this.userDB;
        }
        synchronized (LiteOrmDBManager.class) {
            this.userId = userId;
            this.userDB = null;
            File userDBFolder = ConfigManager.FileConfig.getUserDBFolder(appContext, String.valueOf(userId));
            this.userDB = new LiteOrmDBManager(userDBFolder.getAbsolutePath());
            //可能初次进入时,状态为登出状态,导致对用的intUserDBManager没有进入初始化完成,
            //然后使用的时候,用户状态为登陆状态,这时候,获取dbmanger的时候就会获取到的实例是没有初始化LiteOrm的Manager
            //这里添加判断,如果没有成功,就添加主动再初始化一次
            if (!this.userDB.isDBManagerInitSuccess()) {
                this.userDB.init(appContext);
            }
        }
        return this.userDB;
    }

    /**
     * 给DBManager 增加Room数据库框架的支持，不同用户的数据也存在不同的数据库中
     * <p>
     * 注意：每个App内传入的 dataBase 必须在App内全局唯一，也即在一个App内只能使用一个RoomDatabase来读写用户相关的数据。
     *
     * @param userId   userId
     * @param dataBase dataBase
     * @param <T>      dataBase.class
     * @return RoomDataBase
     */
    public <T extends RoomDatabase> T getUserRoomDB(String userId, Class<T> dataBase) {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null");
        }

        if (userRoomDBClass != null && !userRoomDBClass.getCanonicalName().equals(dataBase.getCanonicalName())) {
            throw new IllegalArgumentException("last class is "
                    + userRoomDBClass.getSimpleName() + ", new class is " + dataBase.getSimpleName());
        }

        String db_name = ConfigMD5Utils.getStringMD5(userId);
        if (userRoomDB != null && db_name.equals(userRoomDB.getOpenHelper().getDatabaseName())) {
            return (T) userRoomDB;
        } else {
            userRoomDBClass = dataBase;
            userRoomDB = Room.databaseBuilder(appContext, dataBase, db_name).allowMainThreadQueries().build();
        }
        return (T) userRoomDB;
    }
}
