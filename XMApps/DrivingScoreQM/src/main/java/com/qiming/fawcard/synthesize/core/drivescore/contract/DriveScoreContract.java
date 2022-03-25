package com.qiming.fawcard.synthesize.core.drivescore.contract;

import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.util.List;

public class DriveScoreContract {
    // Presenter接口定义
    public interface Presenter {

        /**
         * 取得Token
         */
        void checkAvn(int type);

        /**
         * 用户每日驾驶评分达到100分时调用此接口，领取车币奖励(每日一次)
         */
        void reportDriveScore();

        /**
         * 每5分钟调用该接口，更新折线图
         */
        void getDriveInfo();

        /**
         * 熄火时调用该接口，取得驾驶评分结果
         */
        void getDriveScore();

        /**
         * 保存驾驶记录和驾驶详情
         *
         * @param history 驾驶记录
         * @param details 驾驶详情（缓存数据）
         */
        void updateOrDeleteTRavelRecord(DriveScoreHistoryEntity history, List<DriveScoreHistoryDetailEntity> details);

        /**
         * 获取上周一周的驾驶数据
         */
        void getLastWeekDriveScore();

        DriveScoreHistoryEntity createTravelRecord();

        void updateTravelRecord(DriveScoreHistoryEntity entity);

        void deleteTravelRecord();
    }

    // Service接口定义
    public interface Service {

        /**
         * 数据加入缓存，更新折线图
         *
         * @param data
         */
        void updateDriveInfo(DriveScoreHistoryDetailEntity data);

        /**
         * 弹窗评分、更新首页评分、保存驾驶记录和驾驶详情、清空折线图
         *
         * @param data
         */
        void updateDriveScore(DriveScoreHistoryEntity data);

        /**
         * 保存token
         *
         * @param token
         */
        void updateToken(String token);

        /**
         * 保存TSP token
         *
         * @param tspToken
         */
        void updateTSPToken(String tspToken,int currentType);

        /**
         * 服务器请求失败时，通知Service判断是否弹出加载失败对话框
         */
        void onRequestFailed(QMConstant.RequestFailMessage msg,int errorCode);

        /**
         * 获取到上周一周的驾驶数据
         */
        void getLastWeekDriveScore(String score);

        /**
         * 行程结束
         */
        void setTravleFinish();

        /**
         * 行程开始
         */
        void setTravelStart();

        /**
         * 合并行程
         * @param travelTime 已经行驶的时间
         */
        void mergeTravel(int travelTime);

        /**
         * 每5分钟获取一次驾驶快照的timer
         * @return
         */
        boolean isDriveInfoTimerRunning();

        boolean isTravelCompleteTimerRunning();

        /**
         * 当上次行程异常 再次开机 判断上次异常行程还未结束
         * 等待再次点火
         * 则获取熄火前保存的数据添加至cache 绘制在折线图
         * @param lastedDriveData
         */
        void addCacheData(List<DriveScoreHistoryDetailEntity> lastedDriveData);

        /**
         * 上次行程是否异常（即 行程没有结束 熄火就关机 数据不完整 不知道行程是否结束）
         * @return
         */
        boolean isLastTravelUnusual();

        void reStartTimer();

        boolean isTravelStart();

        void continueTravel();
    }
}
