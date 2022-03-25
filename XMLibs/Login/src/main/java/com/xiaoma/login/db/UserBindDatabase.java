package com.xiaoma.login.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.xiaoma.model.User;

/**
 * Created by kaka
 * on 19-5-19 下午2:41
 * <p>
 * desc: 本数据库是保存在/sdcard/com.xiaoma/local/{车机iccid的MD5值}/local_user.db数据库中
 * </p>
 */
@Database(entities = {User.class, FaceStore.class}, version = 301, exportSchema = false)
public abstract class UserBindDatabase extends RoomDatabase {
    /**
     * 管理缓存在本地的用户绑定信息
     *
     * @return UserDao
     */
    public abstract UserDao userDao();

    /**
     * PAD版用于模拟人脸使用
     *
     * @return FaceDao
     */
    public abstract FaceDao faceDao();
}
