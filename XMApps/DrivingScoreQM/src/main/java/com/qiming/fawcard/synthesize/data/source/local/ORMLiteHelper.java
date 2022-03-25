package com.qiming.fawcard.synthesize.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.sql.SQLException;

public class ORMLiteHelper extends OrmLiteSqliteOpenHelper {

    private static final String DBNAME = "ormlite.db";  // 数据库名称
    private static final int DBVERSION = 1;             // 数据库版本号
    private static ORMLiteHelper mInstance;             // 单例

    private ORMLiteHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    public static ORMLiteHelper getInstance(Context context){
        if (mInstance == null) {
            mInstance = new ORMLiteHelper(context);
        }

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        // 安装时创建数据库Table
        try {
            TableUtils.createTable(connectionSource, DriveScoreHistoryEntity.class);
            TableUtils.createTable(connectionSource, DriveScoreHistoryDetailEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // 升级时将数据库Table删除后重建
        try {
            TableUtils.dropTable(connectionSource, DriveScoreHistoryEntity.class,true);
            TableUtils.dropTable(connectionSource, DriveScoreHistoryDetailEntity.class,true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
