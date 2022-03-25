package com.xiaoma.motorcade.common.manager;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.xiaoma.model.User;
import com.xiaoma.motorcade.BuildConfig;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.main.model.MototrcadeGroup;
import com.xiaoma.motorcade.main.repo.MainRepo;
import com.xiaoma.motorcade.setting.model.MemberInfo;
import com.xiaoma.motorcade.setting.repo.CarTeamMembersRepo;

import java.lang.ref.WeakReference;

/**
 * Created by LKF on 2019-2-22 0022.
 * 车信数据仓库,目前采用Room实现,数据仓库的管理器集中在此管理类中
 */
@Database(entities = {
        User.class, GroupCardInfo.class,GroupMemberInfo.class,
        MototrcadeGroup.class,MemberInfo.class
},
        version = BuildConfig.VERSION_CODE,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MotorcadeRepo extends RoomDatabase {
    private static final String DB_NAME = "XiaoMaRoomDb.db";
    private static MotorcadeRepo sInstance;
    private static WeakReference<Context> sContextRef;

    public static void init(Context context) {
        sContextRef = new WeakReference<>(context.getApplicationContext());
    }

    public static MotorcadeRepo getInstance() {
        if (sInstance == null) {
            synchronized (MotorcadeRepo.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            sContextRef.get(), MotorcadeRepo.class, DB_NAME)
                            .allowMainThreadQueries()// 允许在主线程读写
                            .fallbackToDestructiveMigration()// 发生错误时,重建数据库,而不是抛出异常
                            .build();
                }
            }
        }
        return sInstance;
    }

    abstract public UserRepo getUserRepo();

    abstract public GroupRepo getGroupRepo();

    abstract public GroupMemberRepo getMemberRepo();

    abstract public MainRepo getMainRepo();

    abstract public CarTeamMembersRepo getCarTeamListRepo();
}
