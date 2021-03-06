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
            //?????????????????????,????????????????????????
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
            //?????????????????????,?????????????????????,???????????????intUserDBManager???????????????????????????,
            //?????????????????????,???????????????????????????,?????????,??????dbmanger???????????????????????????????????????????????????LiteOrm???Manager
            //??????????????????,??????????????????,?????????????????????????????????
            if (!this.userDB.isDBManagerInitSuccess()) {
                this.userDB.init(appContext);
            }
        }
        return this.userDB;
    }

    /**
     * ???DBManager ??????Room??????????????????????????????????????????????????????????????????????????????
     * <p>
     * ???????????????App???????????? dataBase ?????????App?????????????????????????????????App?????????????????????RoomDatabase?????????????????????????????????
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
