package com.qiming.fawcard.synthesize.base.system.service;


import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.CommonInfoHolder;
import com.qiming.fawcard.synthesize.base.application.QmApplication;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.broadcast.DriveScoreBroadcastReceiver;
import com.qiming.fawcard.synthesize.base.system.callback.DriverInfoCallback;
import com.qiming.fawcard.synthesize.base.system.callback.MyLifecycleHandler;
import com.qiming.fawcard.synthesize.base.util.CalendarUtils;
import com.qiming.fawcard.synthesize.base.util.ConvertUtil;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreDialogShieldActivity;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreContract;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScorePresenter;
import com.qiming.fawcard.synthesize.dagger2.component.ComponentHolder;
import com.qiming.fawcard.synthesize.dagger2.module.DriveServiceModule;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;


public class DriverService extends Service implements DriveScoreContract.Service {
    // 取得驾驶数据计时器
    private Timer mTimer;
    private PeriodTask mTimerTask;

    private DriverInfoCallback mCallback;
    @Inject
    DriveScoreBroadcastReceiver mReceiver;

    private static final String TAG = QMConstant.TAG;

    // 点火时的时间
    private long mStartTimeCount = -1L;
    // 是否处于点火状态的标志位
    private boolean mIsStarting = false;

    @Inject
    DriveScorePresenter mDriveScorePresenter;

    // 数据缓存
    private List<DriveScoreHistoryDetailEntity> mCache = new ArrayList<>();
    private Timer mTravelCompleteTimer;
    private TravelCompleteTask mTravelCompleteTask;
    private boolean isLastTravelUnusual; // 上次行程是否异常

    @Override
    public void onCreate() {
        super.onCreate();

        ComponentHolder.getInstance().getDriveServiceComponent(new DriveServiceModule(this)).inject(this);
        registerReceiver();
        justifyLastTravelFinish();
    }

    private void justifyLastTravelFinish() {
        int latestTravelStartTime = TPUtils.get(this, QMConstant.LATEST_TRAVEL_START_TIME, 0);
        if (latestTravelStartTime != 0) { // 上次行程未结束
//            if (mDriveScorePresenter != null) {
//                mDriveScorePresenter.getDriveScore();
//                isLastTravelUnusual = true; // 上次行程异常
//            }
            isLastTravelUnusual = true; // 上次行程异常
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        KLog.d(TAG, "registerReceiver: ");
        IntentFilter filter = new IntentFilter();
        filter.addAction(QMConstant.ACTION_DRIVE_START);
        filter.addAction(QMConstant.ACTION_DRIVE_STOP);
        this.registerReceiver(mReceiver, filter);
    }

    /**
     * 解绑广播
     */
    private void unRegisterReceiver() {
        KLog.d(TAG, "unRegisterReceiver: ");
        if (mReceiver != null) {
            this.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onDestroy() {
        KLog.d(TAG, "onDestroy: ");
        super.onDestroy();
        unRegisterReceiver();
        stopTimer();
        stopTravelCompleteTimer();
    }

    /**
     * 取得驾驶快照的回调函数
     *
     * @param data 驾驶快照数据
     */
    @Override
    public void updateDriveInfo(DriveScoreHistoryDetailEntity data) {
        KLog.d(TAG, "updateDriveInfo: ");
        if (data == null) {
            return;
        }

//        int size = mCache.size();
//        if ((!data.isValid) && (size > 0)) {
//            DriveScoreHistoryDetailEntity lastData = mCache.get(size - 1);
//            data.avgFuel = lastData.avgFuel;
//            data.avgSpeed = lastData.avgSpeed;
//        }

        KLog.d(TAG, "平均车速: " + data.avgSpeed + "平均油耗: " + data.avgFuel);

        calcDriveTime();
        mCache.add(data);

        if (mCallback != null) {
            mCallback.dataChange(data);
            mCallback.onDriveTime(mStartTimeCount);
        }
    }

    /**
     * 取得上次驾驶评分数据的回调函数
     *
     * @param data 驾驶评分信息
     */
    @Override
    public void updateDriveScore(DriveScoreHistoryEntity data) {
        KLog.d(TAG, "updateDriveScore: ");
        //保存缓存数据到DB
        mDriveScorePresenter.updateOrDeleteTRavelRecord(data, mCache);
        if (data == null || !isValidRecord(data)) {
            // 行驶不满足两公里，不进行保存和提醒
            initData();
            KLog.e(QMConstant.TAG, "行程已结束且为无效行程-记录删除-页面初始化");
            return;
        }

        KLog.e(QMConstant.TAG, "行程已结束且为有效行程");
        // 显示驾驶评分对话框
        showDriverScoreDialog();

        long curDirveTimeSecond = ConvertUtil.minuteToSecond(mStartTimeCount);
        if (data.travelTime < curDirveTimeSecond) {
            data.travelTime = curDirveTimeSecond;
        }

        if (mCallback != null) {
            mCallback.onDriverScore(data);
        }

        // 清空缓存中的数据
        initData();
    }

    /**
     * 取得Token的回调函数
     *
     * @param token 取得的Token值
     */
    @Override
    public void updateToken(String token) {
        KLog.d(TAG, "updateToken: token = " + token);
        CommonInfoHolder.getInstance().setToken(token);
    }

    /**
     * 取得TSPToken的回调函数
     *
     * @param tspToken 取得的TSOToken值
     */
    @Override
    public void updateTSPToken(String tspToken, int currentType) {
        KLog.d(TAG, "updateTSPToken: tspToken = " + tspToken);
        CommonInfoHolder.getInstance().setTSPToken(tspToken);

        KLog.d(TAG, "updateTSPToken: mIsStarting = " + mIsStarting + ",token = " + tspToken);
    }

    /**
     * 失败通知
     *
     * @param msg 失败的类型
     */
    @Override
    public void onRequestFailed(QMConstant.RequestFailMessage msg, int errorCode) {
        KLog.d(TAG, "onRequestFailed: 错误类型:" + msg);

        if (errorCode == QMConstant.ERROR_CODE_GET_DRIVE_SNAP) // 只有获取驾驶快照的接口报错 驾驶时间才需要增加
            calcDriveTime();
        if (mCallback != null) {
            mCallback.onDriveTime(mStartTimeCount);
            mCallback.onRequestFailed(msg);
        }
    }

    @Override
    public void getLastWeekDriveScore(String score) {
        if (TextUtils.isEmpty(score)) return;
        if (score.contains(".")) {
            score = score.substring(0, String.valueOf(score).indexOf("."));
        }
//        String str = String.valueOf(score).substring(0, String.valueOf(score).indexOf("."));
        int curScore = Integer.parseInt(score);
        String driverCommon = getDriverCommonByScore(curScore);
        KLog.d(TAG, "getLastWeekDriveScore: 驾驶分数 = " + curScore);

        Boolean result = MyLifecycleHandler.isApplicationInForeground();
        KLog.d(TAG, "当前评分App是否在前台：" + result);
        Intent intent = new Intent(DriverService.this, DriveScoreDialogShieldActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("curScore", curScore);
        intent.putExtra("driverCommon", driverCommon);
        intent.putExtra("isForeground", result);
        startActivity(intent);
    }

    public class DriverBinder extends Binder {
        public DriverService getService() {
            return DriverService.this;
        }

        public void restartGetDriverInfo() { // 此方法为所有接口出错后重新加载的入口
            KLog.d(TAG, "restartGetDriverInfo: 是否已经点火" + mIsStarting);
            mCallback.hideLoading();
            if (QmApplication.getEngineStatus() == QMConstant.STATUS_LAUNCHED && !isDriveInfoTimerRunning()) { // 已经点火了 请求数据失败
                KLog.d(TAG, "restartGetDriverInfo: 已经点火了 请求数据失败");
                startTimer();
            } else { // 未点火

            }

        }

        public void retryRequestData() {
            mDriveScorePresenter.retryRequestData();
        }

        public void stopGetDriverInfo() {
            KLog.d(TAG, "stopGetDriverInfo: ");
            stopTimer();
        }

        public boolean isStarting() {
            return mIsStarting;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        String vin = intent.getStringExtra("vin");
        int engineStatus = intent.getIntExtra("status", QMConstant.DEFAULT_ENGINE_STATUS);
        QmApplication.setEngineStatus(engineStatus);
        KLog.d(TAG, "onBind: Vin = " + vin + " status = " + engineStatus);

        if (vin != null && !"".equals(vin)) {
            CommonInfoHolder.getInstance().setVin(vin);
        }

        if (engineStatus == QMConstant.STATUS_LAUNCHED) {
            mIsStarting = true;

//            if (mDriveScorePresenter != null) {
//                mDriveScorePresenter.checkAvn();
//            }
            carLaunchered();
        }

        return new DriverBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d(TAG, "onUnbind:");
        stopTimer();
        initData();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d(TAG, "onStartCommand: ");
        if (intent == null) return super.onStartCommand(null, flags, startId);
        int driverStatus = intent.getIntExtra(QMConstant.DRIVER_SERVICE_KEY, 0);
        if (driverStatus == QMConstant.DRIVER_SERVICE_START) {
            KLog.d(TAG, "Service收到点火广播：");
            carLaunchered();
        } else if (driverStatus == QMConstant.DRIVER_SERVICE_END) {
            KLog.d(TAG, "Service收到熄火广播：");
            carShutDown(); // 开始20分钟倒计时 中间再次点火 则接着上一次的行程
        } else {
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 车子熄火 开启20分钟倒计时
     */
    private void carShutDown() {
        KLog.e(QMConstant.TAG,"carShutDown mIsStarting = "+mIsStarting);
        stopTimer(); // 熄火后不再获取数据
        if (!mIsStarting) return;// 行程没有开始 熄火事件不做处理
        KLog.d(TAG, "carShutDown: 开始25分钟倒计时,mTravelCompleteTimer = " + mTravelCompleteTimer);
        restartTravelCompleteTimer();
    }

    private void restartTravelCompleteTimer() {
        if (mTravelCompleteTimer != null) stopTravelCompleteTimer();
        mTravelCompleteTimer = new Timer();
        mTravelCompleteTask = new TravelCompleteTask();
        mTravelCompleteTimer.schedule(mTravelCompleteTask, QMConstant.TRAVEL_COMPLETE_BUFFER_TIME, QMConstant.TRAVEL_COMPLETE_BUFFER_TIME);
    }

    /**
     * 车子启动 判断是否正在倒计时
     */
    private void carLaunchered() {
//        KLog.d(TAG, "mIsStarting :" + mIsStarting + ",timer :" + mTimer);
//        if (mIsStarting /*&& isDriveInfoTimerRunning()*/) { // 上次行程还在继续 isDriveInfoTimerRunning有可能在请求出错时关闭
//            if (!isDriveInfoTimerRunning()) {
//                startTimer();
//            }
//            KLog.d(TAG, "TravelTimer :" + mTravelCompleteTimer);
//            if (isTravelCompleteTimerRunning()) { // 25分钟倒计时还未结束
////                stopTravelCompleteTimer();
//                getLastDriveScore();
//                return;
//            }
//        } else if (isLastTravelUnusual) {
//            // 上次行程异常 获取数据 判断行程是否结束 未结束则合并 结束则开始新行程
//            getLastDriveScore();
//            // TODO: 2019/08/17 此处看是否有需求增加页面的loading等待显示
//            return;
//        }
//        KLog.e(TAG, "开始行程");
//        setTravelStart();// 行程开始

        //===============================================================
//        if (mIsStarting) { // 行程已经开始 熄火后点火
//            getLastDriveScore();
//        } else { // 行程未开始-开始行程
        KLog.d(TAG, "mIsStarting :" + mIsStarting + ",timer :" + mTimer);
        getLastDriveScore();
//        }
    }

    /**
     * 当点火时 判断上次行程的倒计时还在 则请求上次驾驶评分 对比行程开始时间 若拿到了此次行程的数据 则行程结束（停止倒计时） 开始新的行程
     * 若没有 则合并行程
     */
    private void stopTravelCompleteTimer() {
        if (mTravelCompleteTimer == null) return;
        mTravelCompleteTimer.cancel(); // 取消倒计时 合为一次行程
        mTravelCompleteTimer = null;
        mTravelCompleteTask = null;
    }

    /**
     * 行程结束
     */
    @Override
    public void setTravleFinish() {
        KLog.e(TAG, "行程结束");
        stopTimer(); // 停止每五分钟获取车速的倒计时
        stopTravelCompleteTimer(); // 停止25分钟倒计时
        mIsStarting = false;
        isLastTravelUnusual = false;
        // 行程结束 清空行程开始时间
        TPUtils.put(this, QMConstant.LATEST_TRAVEL_START_TIME, 0);
    }

    /**
     * 行程开始
     */
    @Override
    public void setTravelStart() {
        KLog.e(TAG, "行程开始");
        mIsStarting = true;
        isLastTravelUnusual = false;
        // 行程开始 就生成一条历史记录 保存数据库
        mDriveScorePresenter.createTravelRecord();
        startDriverService();
        // 保存此次行程的开始时间
        TPUtils.put(this, QMConstant.LATEST_TRAVEL_START_TIME, System.currentTimeMillis());
    }

    /**
     * 继续行程
     * 关机-开机 且上次行程未结束 开启每5分钟获取一次数据 继续行程
     */
    public void continueTravel() {
        KLog.e(TAG, "行程继续");
        mIsStarting = true;
        isLastTravelUnusual = false;
        stopTravelCompleteTimer();
//        if (mCache.size() > 0 && mCallback != null) {
//            mCallback.onDriverInfosNotify(mCache);
//            mCallback.onDriveTime(mStartTimeCount);
//        }
        if (isDriveInfoTimerRunning()) {
            reStartTimer();
        } else {
            startTimer();
        }
    }

    /**
     * 合并行程
     * 如果是 点火-熄火后 25分钟内点火 直接取消25分钟倒计时即可
     * 如果是点火-熄火-关机-开机 25分钟没有结束 则添加关机之前的驾驶数据 再开始行程
     *
     * @param travelTimeMinute
     */
    @Override
    public void mergeTravel(int travelTimeMinute) { // 大概时间 不准确 行程未结束 获取不到时间
        KLog.e(TAG, "合并行程");
//        if (isTravelCompleteTimerRunning()) {// 没有关机 熄火-点火 25分钟内行程没有结束
//            stopTravelCompleteTimer();
//            if (!isDriveInfoTimerRunning())
//                reStartTimer();
//        } else { // 熄火-关机-开机-点火
//            mStartTimeCount = travelTimeMinute;
//            continueTravel();
//        }

//        continueTravel();
        mIsStarting = true;
        isLastTravelUnusual = false;
        stopTravelCompleteTimer();
        mStartTimeCount = travelTimeMinute;
        if (mCache.size() > 0 && mCallback != null) {
            mCallback.onDriverInfosNotify(mCache);
            mCallback.onDriveTime(mStartTimeCount);
        }
        if (isDriveInfoTimerRunning()) {
            reStartTimer();
        } else {
            startTimer();
        }
    }

    @Override
    public void addCacheData(List<DriveScoreHistoryDetailEntity> lastedDriveData) {
        if (mCache == null) mCache = new ArrayList<>();
        mCache.addAll(0, lastedDriveData);
    }

    @Override
    public boolean isLastTravelUnusual() {
        return isLastTravelUnusual;
    }

    @Override
    public boolean isTravelCompleteTimerRunning() {
        return mTravelCompleteTimer != null && mTravelCompleteTask != null;
    }


    public class TravelCompleteTask extends TimerTask {

        @Override
        public void run() {
            KLog.e("25分钟倒计时结束");
//            mIsStarting = false;
            stopDriverService();
            cancel();
            mTravelCompleteTimer = null;
            mTravelCompleteTask = null;
        }
    }

    private void calcDriveTime() {
        if (mStartTimeCount == -1) {
            mStartTimeCount = 0;
        } else {
            mStartTimeCount += QMConstant.TIMER_MINUTE;
        }
    }

    private void startDriverService() {
        initData();
        if (mCallback != null) {
            mCallback.onDriveStart();
        }

        if (isDriveInfoTimerRunning()) {
            reStartTimer();
        } else {
            startTimer();
        }
    }

    private void initData() {
        mStartTimeCount = -1;
        if (mCache.size() > 0) {
            mCache.clear();
        }
    }

    private void stopDriverService() {
        KLog.e("stopDriverService");
        // 关闭取得驾驶数据的Timer
        stopTimer();

        // 取得最后一次驾驶数据
        getLastDriveScore();
    }

    /**
     * 取得驾驶快照数据
     */
    private void getSnapshot() {
        KLog.d(TAG, "getSnapshot: ");
        if (mDriveScorePresenter != null) {
            mDriveScorePresenter.getDriveInfo();
        }
    }

    /**
     * 取得上一次的驾驶评分数据
     */
    private void getLastDriveScore() {
        KLog.d(TAG, "getLastDriveScore: ");
        if (mDriveScorePresenter != null) {
            mDriveScorePresenter.getDriveScore();
//            mDriveScorePresenter.getLastWeekDriveScore();
        }
    }

    /**
     * 切换到驾驶评分界面
     */
    public void showDriverScoreDialog() {
//        if (!shouldShowScoreDialog()) return;
        // 请求接口获取周数据 平均驾驶分
        mDriveScorePresenter.getLastWeekDriveScore();
    }

    /**
     * 第一次没有驾驶过 不获取数据 不弹窗 但保存周信息
     * 每周第一次驾驶时 弹窗
     * 记录当前是本月的第几周
     * 假设上次驾驶是第二周
     * 若不是同一周 则获取接口信息 覆盖当前是第几周
     * 若是同一周
     * 则有可能是本月的同一周 不需要请求驾驶数据 不需要覆盖第几周信息
     * 也有可能是次月的同一周 此时上周肯定没有驾驶数据 不需要获取驾驶数据 不需要覆盖第几周信息
     * 评分请求钛马周数据中的评分
     *
     * @return
     */
    private boolean shouldShowScoreDialog() {
        int lastDriveWeek = TPUtils.get(this, QMConstant.WEEK_LAST_DRIVE, -1);
        if (lastDriveWeek == -1) { // 第一次驾驶
            TPUtils.put(this, QMConstant.WEEK_LAST_DRIVE, CalendarUtils.getWeekOfMonth());
            return false;
        }
        boolean shouldShowDialog = lastDriveWeek != CalendarUtils.getWeekOfMonth();
        if (shouldShowDialog) { // 若不是同一周
            TPUtils.put(this, QMConstant.WEEK_LAST_DRIVE, CalendarUtils.getWeekOfMonth());
        }
        return shouldShowDialog;
    }

    private String getDriverCommonByScore(int score) {
        String common = null;

        if (score == QMConstant.DRIVE_SCORE_100) {
            common = getResources().getString(R.string.pop_score_100);
            upLoadScore();
        } else if (score >= QMConstant.DRIVE_SCORE_90 && score < QMConstant.DRIVE_SCORE_100) {
            common = getResources().getString(R.string.pop_score_99);
        } else if (score >= QMConstant.DRIVE_SCORE_80 && score < QMConstant.DRIVE_SCORE_90) {
            common = getResources().getString(R.string.pop_score_89);
        } else if (score >= QMConstant.DRIVE_SCORE_70 && score < QMConstant.DRIVE_SCORE_80) {
            common = getResources().getString(R.string.pop_score_79);
        } else if (score >= QMConstant.DRIVE_SCORE_60 && score < QMConstant.DRIVE_SCORE_70) {
            common = getResources().getString(R.string.pop_score_69);
        } else if (score < QMConstant.DRIVE_SCORE_60) {
            common = getResources().getString(R.string.pop_score_59);
        } else {

        }

        return common;
    }

    /**
     * 上传车币到服务器
     */
    private void upLoadScore() {
        KLog.d(TAG, "upLoadScore: ");
        if (mDriveScorePresenter != null) {
            mDriveScorePresenter.reportDriveScore();
        }
    }

    /**
     * 判断是否为一次有效的驾驶记录
     *
     * @param data 驾驶数据
     * @return true:有效 false:无效
     */
    private boolean isValidRecord(DriveScoreHistoryEntity data) {
        return /*data.isValid && */data.travelDist >= QMConstant.VALIDE_TRAVEL_DESTANCE;
    }

    /**
     * 重新启动取得驾驶快照的计时器
     */
    @Override
    public void reStartTimer() {
        stopTimer();
        startTimer();
    }

    public boolean isTravelStart() {
        return mIsStarting;
    }

    /**
     * 判断当前计时器是否运行
     *
     * @return
     */
    @Override
    public boolean isDriveInfoTimerRunning() {
        return mTimer != null && mTimerTask != null;
    }

    /**
     * 启动任务计时器，每5分钟取得一次驾驶快照数据
     */
    private void startTimer() {
        KLog.d(TAG, "startTimer: timer = " + mTimer);
        if (isDriveInfoTimerRunning()) stopTimer();
        mTimer = new Timer();
        mTimerTask = new PeriodTask();
        mTimer.schedule(mTimerTask, 0, QMConstant.TIMER_MILLISECOND);
    }

    public class PeriodTask extends TimerTask {
        @Override
        public void run() {
            String tspToken = CommonInfoHolder.getInstance().getTSPToken();
            if (tspToken == null || "".equals(tspToken)) {
                mDriveScorePresenter.checkAvn(QMConstant.ERROR_CODE_GET_DRIVE_SNAP);
            } else {
                getSnapshot();
            }
        }
    }

    /**
     * 停止定时取得驾驶快照数据的计时器
     */
    private void stopTimer() {
        KLog.d(TAG, "stopTimer: ");
        if (mTimer == null || mTimerTask == null) {
            return;
        }

        mTimer.cancel();
        mTimer = null;

        mTimerTask.cancel();
        mTimerTask = null;
    }

    public DriverInfoCallback getCallback() {
        return mCallback;
    }

    /**
     * 绑定Service对象的回调注册
     *
     * @param callback
     */
    public void setDriverInfoCallback(DriverInfoCallback callback) {
        mCallback = callback;

        KLog.d(TAG, "setDriverInfoCallback: size = " + mCache.size());
        if (mCallback != null) {
            mCallback.onBindSuccess();
            if (mCache.size() > 0) {
                mCallback.onDriverInfosNotify(mCache);
                mCallback.onDriveTime(mStartTimeCount);
            }
        }

        if (mIsStarting && !isDriveInfoTimerRunning()) {
            startTimer();
        }
    }

    public List<DriveScoreHistoryDetailEntity> getCache() {
        return mCache;
    }
}
