package com.qiming.fawcard.synthesize.data.source.local;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.xiaoma.utils.log.KLog;

import java.sql.SQLException;
import java.util.List;

public class DriveScoreHistoryDetailDao {

    private Dao<DriveScoreHistoryDetailEntity, Integer> mDao;   // 数据库访问对象
    public DriveScoreHistoryDetailDao(Context context) {
        try {
            mDao = ORMLiteHelper.getInstance(context).getDao(DriveScoreHistoryDetailEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一条数据
     * @param data
     */
    public void create(DriveScoreHistoryDetailEntity data){
        try {
            mDao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入多条数据
     * @param list
     */
    public void createList(List<DriveScoreHistoryDetailEntity> list){
        try {
            mDao.create(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新一条数据
     * @param data
     */
    public void update(DriveScoreHistoryDetailEntity data){
        try {
            mDao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条数据
     * @param data
     */
    public void delete(DriveScoreHistoryDetailEntity data){
        try {
            mDao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ID查询一条数据
     * @param id
     * @return
     */
    public DriveScoreHistoryDetailEntity queryForId(int id){
        DriveScoreHistoryDetailEntity data = null;
        try {
            data = mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 查询表中全部数据
     * @return
     */
    public List<DriveScoreHistoryDetailEntity> queryForAll(){
        List<DriveScoreHistoryDetailEntity> list = null;
        try {
            list = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询历史详情（画面线状图数据）
     * @param historyId
     * @return
     */
    public List<DriveScoreHistoryDetailEntity> queryHistoryDetail(int historyId){
        List<DriveScoreHistoryDetailEntity> list = null;
        try {
            list = mDao.queryBuilder().where().eq("history_id", historyId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 删除历史记录的同时，删除相关的历史详情
     * @param historyId
     */
    public void deleteHistoryDetail(int historyId){
        try {
            mDao.executeRaw("delete from drive_score_history_detail where history_id = " + historyId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据一次行为记录得获取对应得快照
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<DriveScoreHistoryDetailEntity> getDetailsByHistoryStartEndTime(long startTime, long endTime) {
        List<DriveScoreHistoryDetailEntity> result = null;
        try {
            result = mDao.queryBuilder().where().between("time", startTime, endTime).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据拉取的最后一次行为记录(快照时间戳 > 最后一次行为结束时间点)，获取当前行程的快照集合
     *
     * @param lastEndTime 拉取到的最后一次行为结束时间
     * @return 当前行程的快照集合
     */
    public List<DriveScoreHistoryDetailEntity> getCurrentDetailsByLastHistoryEndTime(long lastEndTime) {
        List<DriveScoreHistoryDetailEntity> result = null;
        try {
            result = mDao.queryBuilder().where().gt("time", lastEndTime).query();
            if (result == null) return null;
            for (int i = 0;i<result.size();i++){
                KLog.e(QMConstant.TAG,"最近一条行程记录之后的驾驶快照bean"+result.get(i));
            }
            KLog.e(QMConstant.TAG,"result = "+result.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据起止时间点，删除 end>= 时间戳 >=start 的所有快照
     *
     * @param startTime 起
     * @param endTime   止
     * @return 是否删除成功
     */
    public boolean deleteByStartEndTime(long startTime, long endTime) {
        int result = 0;
        try {
            result = mDao.executeRaw("delete from drive_score_history_detail where time between " + startTime + " and " + endTime);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result > 0;
    }

}
