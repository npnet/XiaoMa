package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.util.ConvertUtil;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHistoryContract;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;
import com.xiaoma.utils.log.KLog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DriveScoreHistoryPresenter implements DriveScoreHistoryContract.Presenter {
    private final DriveScoreHistoryDao mDriveScoreHistoryDao;
    private final DriveScoreHistoryDetailDao mDriveScoreDetailDao;
    private final ArrayList<ViewModel> mViewModels = new ArrayList<>();

    /**
     * 构造函数.
     * 构造函数.
     *
     * @param historyDao 数据处理对象（历史记录）
     * @param detailDao  数据处理对象（历史详情）
     */
    public DriveScoreHistoryPresenter(DriveScoreHistoryDao historyDao, DriveScoreHistoryDetailDao detailDao) {
        mDriveScoreHistoryDao = historyDao;
        mDriveScoreDetailDao = detailDao;
        init();
    }

    /**
     * 初始化.
     */
    private void init() {
        Long startTime = System.currentTimeMillis();
        KLog.e(QMConstant.TAG, "startTime = " + startTime);
        mViewModels.clear();

        List<DriveScoreHistoryEntity> list = mDriveScoreHistoryDao.queryFinishedHistoryList();
        if (list == null) {
            KLog.d("list is null");
            return;
        }
        for (DriveScoreHistoryEntity history : list) {
            mViewModels.add(changeModel(history));
        }
        Long endTime = System.currentTimeMillis();
        KLog.e(QMConstant.TAG, "endTime = " + endTime);
        KLog.e(QMConstant.TAG, "spendTime = " + (endTime - startTime));
    }

    /**
     * 类型转换.
     *
     * @param driveScoreHistory 数据模型
     * @return 视图模型
     */
    private ViewModel changeModel(DriveScoreHistoryEntity driveScoreHistory) {
        return new ViewModel(driveScoreHistory);
    }

    @Override
    public void delete(DriveScoreHistoryEntity data) {
        // 删除历史记录
        mDriveScoreHistoryDao.delete(data);
        // 删除相关的历史详情
        mDriveScoreDetailDao.deleteHistoryDetail(data.id);
    }

    @Override
    public int getCount() {
        return mViewModels.size();
    }

    @Override
    public ViewModel get(int index) {
        return mViewModels.get(index);
    }

    /**
     * 日期是否变化.
     *
     * @param post 下标
     * @return 是否变化
     */
    public boolean getChange(int post) {
        if (!validPosition(post)) {
            return true;
        }
        if (0 == post) {
            return true;
        }
        return !mViewModels.get(post).date.equals(mViewModels.get(post - 1).date);
    }

    /**
     * 下标是否有效.
     *
     * @param post 下标
     * @return 是否有效
     */
    private boolean validPosition(int post) {
        if (post < 0 || post >= mViewModels.size()) {
            KLog.d("post not valid");
            return false;
        }
        return true;
    }

    /**
     * 数据更新
     */
    public void update() {
        init();
    }

    /**
     * 表示模型.
     */
    public class ViewModel {
        private final DriveScoreHistoryEntity driveScoreHistoryEntity;
        private String date = "";
        private String time = "";

        public DriveScoreHistoryEntity getDriveScoreHistoryEntity() {
            return driveScoreHistoryEntity;
        }

        public int getId() {
            return driveScoreHistoryEntity.id;
        }

        public int getScore() {
            return driveScoreHistoryEntity.score.intValue();
        }

        public Long getAccNum() {
            return driveScoreHistoryEntity.accNum;
        }

        public Long getDecNum() {
            return driveScoreHistoryEntity.decNum;
        }

        public Long getTurnNum() {
            return driveScoreHistoryEntity.turnNum;
        }

        public Long getStartTime() {
            return driveScoreHistoryEntity.startTime;
        }

        public Long getEndTime() {
            return driveScoreHistoryEntity.endTime;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        /**
         * 构造函数.
         *
         * @param history 历史记录实体类
         */
        ViewModel(DriveScoreHistoryEntity history) {
            driveScoreHistoryEntity = history;

            try {
                date = ConvertUtil.longToString(history.startTime, "yyyy年MM月dd日");
                time = ConvertUtil.longToString(history.startTime, "HH:mm");
            } catch (ParseException e) {
                KLog.e("set date ParseException", e, e.getMessage());
            }
        }
    }
}
