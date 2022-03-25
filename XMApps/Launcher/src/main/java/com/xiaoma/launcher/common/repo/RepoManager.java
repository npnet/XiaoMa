package com.xiaoma.launcher.common.repo;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.app.model.LauncherApp;
import com.xiaoma.launcher.app.repo.LauncherAppRepo;

@SuppressLint("StaticFieldLeak")
@Database(entities = {LauncherApp.class},
        version = 1,
        exportSchema = false)
public abstract class RepoManager extends RoomDatabase {
    private static final String DB_NAME = "XiaoMaLauncherRoomDB.db";
    private static RepoManager sInstance = Room.databaseBuilder(
            AppHolder.getInstance().getAppContext(),
            RepoManager.class, DB_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build();

    public static RepoManager getInstance() {
        return sInstance;
    }

    public abstract LauncherAppRepo getLauncherAppRepo();
}
