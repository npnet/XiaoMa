package com.qiming.fawcard.synthesize.data.source.local;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.qiming.fawcard.synthesize.base.system.callback.HistoryRegistrationCenter;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.sql.SQLException;
import java.util.List;

public class DriveScoreHistoryDao {

    private Dao<DriveScoreHistoryEntity, Integer> mDao;   // 数据库访问对象

    public DriveScoreHistoryDao(Context context) {
        try {
            mDao = ORMLiteHelper.getInstance(context).getDao(DriveScoreHistoryEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一条数据
     *
     * @param data
     */
    public void create(DriveScoreHistoryEntity data) {
        try {
            mDao.create(data);
            HistoryRegistrationCenter.notifyUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入多条数据
     *
     * @param list
     */
    public void createList(List<DriveScoreHistoryEntity> list) {
        try {
            mDao.create(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新一条数据
     *
     * @param data
     */
    public void update(DriveScoreHistoryEntity data) {
        try {
            mDao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条数据
     *
     * @param data
     */
    public void delete(DriveScoreHistoryEntity data) {
        try {
            mDao.delete(data);
            HistoryRegistrationCenter.notifyUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ID查询一条数据
     *
     * @param id
     * @return
     */
    public DriveScoreHistoryEntity queryForId(int id) {
        DriveScoreHistoryEntity data = null;
        try {
            data = mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 查询表中全部数据
     *
     * @return
     */
    public List<DriveScoreHistoryEntity> queryForAll() {
        List<DriveScoreHistoryEntity> list = null;
        try {
            list = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询历史记录（画面列表数据，最新数据显示在上面）
     *
     * @return
     */
    public List<DriveScoreHistoryEntity> queryHistoryList() {
        List<DriveScoreHistoryEntity> list = null;
        try {
            list = mDao.queryBuilder().orderBy("start_time", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取已经结束了的行程记录
     * @return
     */
    public List<DriveScoreHistoryEntity> queryFinishedHistoryList() {
        List<DriveScoreHistoryEntity> list = null;
        try {
            list = mDao.queryBuilder().orderBy("start_time", false).where().eq("is_valid", true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取最新一条数据 按id查询
     * todo queryForFirst
     * @return
     */
    public DriveScoreHistoryEntity queryLastedHistoryRecord() {
        DriveScoreHistoryEntity driveScoreHistoryEntity = null;
        try {
            driveScoreHistoryEntity = mDao.queryBuilder().orderBy("id", false).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driveScoreHistoryEntity;
    }

    public Long queryAllDriveRecordCount() {
        Long count = 0L;
        try {
            count = mDao.queryBuilder().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
