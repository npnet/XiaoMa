package com.xiaoma.xting.common.playerSource.info.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.xiaoma.component.AppHolder;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
@Database(entities = {SubscribeInfo.class, RecordInfo.class}, version = 5, exportSchema = false)
public abstract class PlayerInfoDataBase extends RoomDatabase {

    private static final String DB_XTING = "xmlyXting.db";

    private static PlayerInfoDataBase sInfoDataBase;

    public static PlayerInfoDataBase newSingleton() {
        if (sInfoDataBase == null) {
            synchronized (PlayerInfoDataBase.class) {
                if (sInfoDataBase == null) {
                    sInfoDataBase = Room.databaseBuilder(AppHolder.getInstance().getAppContext(), PlayerInfoDataBase.class, DB_XTING)
                            .allowMainThreadQueries()// 允许在主线程读写
                            .fallbackToDestructiveMigration()// 发生错误时,重建数据库,而不是抛出异常
                            .build();
                }
            }
        }
        return sInfoDataBase;
    }

    /**
     * 用于管理收藏
     *
     * @return SubscribeDao
     */
    public abstract SubscribeDao getSubscribeDao();

    /**
     * 用于管理播放记录
     *
     * @return RecordDao
     */
    public abstract RecordDao getRecordDao();

}
