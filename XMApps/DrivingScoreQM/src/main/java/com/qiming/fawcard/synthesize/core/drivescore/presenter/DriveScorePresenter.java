package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.qiming.fawcard.synthesize.base.BaseRequest;
import com.qiming.fawcard.synthesize.base.BaseResponse;
import com.qiming.fawcard.synthesize.base.CommonInfoHolder;
import com.qiming.fawcard.synthesize.base.application.QmApplication;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.constant.QMUrlConstant;
import com.qiming.fawcard.synthesize.base.constant.XiaomaUrlConstant;
import com.qiming.fawcard.synthesize.base.util.CalendarUtils;
import com.qiming.fawcard.synthesize.base.util.MathUtils;
import com.qiming.fawcard.synthesize.base.util.SignatureUtil;
import com.qiming.fawcard.synthesize.base.util.ToastUtil;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreContract;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreCheckAvnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.entity.DrivedRequest;
import com.qiming.fawcard.synthesize.data.entity.DrivedResponse;
import com.qiming.fawcard.synthesize.data.entity.SnapShotResponse;
import com.qiming.fawcard.synthesize.data.entity.UploadScoreResponse;
import com.qiming.fawcard.synthesize.data.entity.VehicleSnapShotRequest;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;
import com.qiming.fawcard.synthesize.data.source.remote.IDrivedApi;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DriveScorePresenter implements DriveScoreContract.Presenter {
    private DriveScoreContract.Service mService;
    IDrivedApi mApi;

    private String mToken = "";     // 缓存的小马token（从小马接口getToken取得）
    private String mVin = "";       // 缓存的vin码（从小马接口getTboxSnByVin取得）
    private String mTboxSn = "";    // 缓存的tbox码（从小马接口getTboxSnByVin取得）
    private String mTspToken = "";  // 缓存的钛马token（从钛马接口checkAvn取得，失效时需要重新取得）

    public boolean isDebugMode = false; // 调试开关（离线测试用）
    Long lastTime = 0L;                 // 上次油耗车速的取得时间（离线测试用）
    private int errorCode;

    /**
     * mToken取得
     */
    public String getCacheToken() {
        return mToken;
    }

    /**
     * mVin取得
     */
    public String getCacheVin() {
        return mVin;
    }

    /**
     * mTboxSn取得
     */
    public String getCacheTboxSn() {
        return mTboxSn;
    }

    /**
     * mTspToken取得
     */
    public String getCacheTspToken() {
        return mTspToken;
    }

    /**
     * 构造函数
     *
     * @param mService
     */
    @Inject
    public DriveScorePresenter(DriveScoreContract.Service mService, IDrivedApi api) {
        this.mService = mService;
        this.mApi = api;
        getVinAndTboxSnInfo(); // 从user获取vin码 和 tboxSn
//        checkAvn();
    }

    private void getVinAndTboxSnInfo() {
        User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        mVin = user.getVin();
        mTboxSn = user.getTboxSN();
//        mVin = "LFBGE6076K1J00056";
//        mTboxSn = "7905030LD071XTJ190417A0052";
//        mVin = "LFBGE6075KJD57387";
//        mTboxSn = "7905030LD071XTJ190625A0015";
//        mVin = "LFBGE6074KJD47188";
//        mTboxSn = "00000000000000000000000000";

    }

    /**
     * 检查tsp token是否过期
     *
     * @param errorCode
     */
    private void checkTspToken(String errorCode, int currentType) {
        if ("D058-GATEWAY.0002".equals(errorCode)) {
            // 判断服务器返回的错误码是token过期时，通知Service本次请求失败
            // 然后自动调用checkAvn更新token，保证下次请求可以成功。
            ToastUtil.showToast(QmApplication.getContext(), "token error:" + errorCode);
            checkAvn(currentType);
            return;
        }
        // TODO: 2019/08/26 处理不同接口失败重试的操作
        mService.onRequestFailed(QMConstant.RequestFailMessage.UNKNOWN, QMConstant.ERROR_CODE_CHECK_AVN);
    }

    /**
     * 检查网络是否连接
     *
     * @return
     */
    private boolean hasNetwork() {
        return NetworkUtils.isConnected(QmApplication.getContext());
    }

    /**
     * 通知请求失败的消息类型
     *
     * @param e
     */
    private void notifyError(Throwable e, int errorCode) {
        this.errorCode = errorCode;
        QMConstant.RequestFailMessage msg;
        if (e instanceof SocketTimeoutException) {
            msg = QMConstant.RequestFailMessage.NETWORK_BADSIGNAL;  // 网络信号差
        } else if (e instanceof HttpException) {
            msg = QMConstant.RequestFailMessage.SERVER_ERROR;       // 服务器返回错误
        } else {
            msg = QMConstant.RequestFailMessage.UNKNOWN;            // 未知
        }

        ToastUtil.showToast(QmApplication.getContext(), "message:" + msg + " e = " + e.getMessage());
        // 发送失败通知
        mService.onRequestFailed(msg, errorCode);
    }

    public void retryRequestData() {
        switch (errorCode) {
            case QMConstant.ERROR_CODE_CHECK_AVN:
                checkAvn(QMConstant.ERROR_CODE_CHECK_AVN);
                break;
            case QMConstant.ERROR_CODE_GET_DRIVE_SNAP:
                mService.reStartTimer();
                getDriveInfo();
                break;
            case QMConstant.ERROR_CODE_UPLOAD_DRIVE_SCORE:
                reportDriveScore();
                break;
            case QMConstant.ERROR_CODE_GET_LAST_DRIVE_SCORE:
                getDriveScore();
                break;
            case QMConstant.ERROR_CODE_GET_LAST_WEEK_DRIVE_INFO:
                getLastWeekDriveScore();
                break;
        }
    }

    /**
     * 车机认证（取得tsp token）
     */
    @Override
    public void checkAvn(final int currentType) {
        if (isDebugMode) {
            mTspToken = "test";
            mToken = "test";
            mService.updateToken(mToken);
            mService.updateTSPToken(mTspToken, currentType);

            return;
        }

        // 检查网络
        if (!hasNetwork()) {
            return;
        }
        if (TextUtils.isEmpty(mVin) || TextUtils.isEmpty(mTboxSn)) {
            ToastUtil.showToast(QmApplication.getContext(), "vin or tboxsn is empty");
            Log.e(QMConstant.TAG, "vin or tboxsn is empty");
            return;
        }
        // 设置请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("vin", mVin);
        requestMap.put("avnSn", mTboxSn);
        requestMap.put("appId", "AVN001");    // 车机端使用固定值：AVN001
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.CHECKAVN)
                .build();

        String url = "https://znwl-uat-hgjc.faw.cn/" + QMUrlConstant.CHECKAVN;
        // 执行网络请求
        Observable<DriveScoreCheckAvnResponse> observable = mApi.checkAvn(url, request.getQuery(), requestMap);
        observable.subscribeOn(Schedulers.io())                 // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                .subscribe(new Observer<DriveScoreCheckAvnResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DriveScoreCheckAvnResponse responseBody) {
                        KLog.e(QMConstant.TAG, "checkAvn " + responseBody);
                        if (responseBody == null) {
                            ToastUtil.showToast(QmApplication.getContext(), "DriveScoreCheckAvnResponse = null");
                            return;
                        }
                        if (responseBody.status == BaseResponse.Status.FAILED) {
                            handleTokenError(responseBody.errorCode, responseBody.errorMessage);
                        } else {
                            // 将tsp token保存起来（在getDriveInfo和getDriveScore时还会用到）
                            mTspToken = responseBody.getToken();
                            KLog.d(QMConstant.TAG, "TSP Token :" + mTspToken);
                            // 通知Service启动Timer
                            handleTspTokenRegain(currentType);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        KLog.e(e.getMessage());
                        notifyError(e, QMConstant.ERROR_CODE_CHECK_AVN);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void handleTspTokenRegain(int currentType) {
        CommonInfoHolder.getInstance().setTSPToken(mTspToken);
        CommonInfoHolder.getInstance().setToken(mToken);
        switch (currentType) {
            case QMConstant.ERROR_CODE_GET_DRIVE_SNAP: // 驾驶快照
                getDriveInfo();
                break;
            case QMConstant.ERROR_CODE_GET_LAST_DRIVE_SCORE: // 驾驶评分
                getDriveScore();
                break;
            case QMConstant.ERROR_CODE_GET_LAST_WEEK_DRIVE_INFO: // 周弹框
                getLastWeekDriveScore();
                break;
        }
    }

    private void handleTokenError(String errorCode, String errorMessage) {
        switch (errorCode) {
            case "Vehicle-Customer.0001": //车辆vin码错误
                errorMessage = "车辆vin码错误";
                break;
            case "Vehicle-Customer.0019": //车辆没有车机
                errorMessage = "车辆没有车机";
                break;
            case "Vehicle-Customer.0020": //车机序列号错误
                errorMessage = "车机序列号错误";
                break;
            case "Vehicle-Customer.0021": //车机登录失败
                errorMessage = "车机登录失败";
                break;
        }
        String msg = "checkAvn failed !" + " errorCode:" + errorCode
                + " errorMessage:" + errorMessage;
        ToastUtil.showToast(QmApplication.getContext(), "checkAvn:" + msg);
    }

    /**
     * 用户每日驾驶评分达到100分时调用此接口，领取车币奖励(每日一次)
     */
    @Override
    public void reportDriveScore() {
        if (isDebugMode) {
            return;
        }

        // 检查网络
        if (!hasNetwork()) {
            return;
        }

        // 设置请求参数
        final String url = XiaomaUrlConstant.getFullPath(XiaomaUrlConstant.UPLOAD_SCORE);
        String token = mToken;
        Map<String, Object> data = new HashMap<>();
        data.put("channelId", "AA1090");    // 频道号是固定值：AA1090
        data.put("vin", mVin);
        Map<String, String> signMap = new HashMap<>();
        signMap.put("data", new Gson().toJson(data));
        signMap.put("sign", SignatureUtil.getSign(signMap, "qiming"));  // 签名算法的key是固定值：qiming

        // 执行网络请求
        Observable<UploadScoreResponse> observable = mApi.uploadScore(url, token, signMap);
        observable.subscribeOn(Schedulers.io())             // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  // 切换到主线程进行UI更新
                .subscribe(new Observer<UploadScoreResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UploadScoreResponse responseBody) {
                        // 请求成功时，通知Service更新
                        // 通知service是否上传成功？
                        if (responseBody == null) {
                            ToastUtil.showToast(QmApplication.getContext(), "UploadScoreResponse = null");
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        notifyError(e, QMConstant.ERROR_CODE_UPLOAD_DRIVE_SCORE);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 每5分钟调用该接口，更新折线图
     */
    @Override
    public void getDriveInfo() {
        // 调试模式下可以使用模拟数据进行离线测试
        if (isDebugMode) {
            DriveScoreHistoryDetailEntity data = getFakeDriveInfo();
            // 添加一条数据
            addDriveInfoRecord(data);
            mService.updateDriveInfo(data);

            return;
        }

        // 检查网络
        if (!hasNetwork()) {
            return;
        }

        if (TextUtils.isEmpty(mTspToken)) {
            checkAvn(QMConstant.ERROR_CODE_GET_DRIVE_SNAP);
            return;
        }

        // 设置请求参数
        VehicleSnapShotRequest requestParams = new VehicleSnapShotRequest();
        requestParams.setVin(mVin);
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_REMOTE_INQUIRY)
                .query("vin", mVin)
                .query("token", mTspToken)
                .query("nonce", UUID.randomUUID().toString() + System.currentTimeMillis())
                .build();

        // 这里request.getFullPath()的值 https://znwl-uat-hgjc.faw.cn/vehicle-status/public/condition/getSnapshotV2
        // 因为接口变更需要替换成 https://znwl-uat-hgjc.faw.cn/d058-gateway/vehicle-status/public/condition/getSnapshotV2
        // 不能直接在QMUrlConstant定义成常量的原因是签名计算的url部分中d058-gateway不参与计算
//        final String url = "https://znwl-uat-hgjc.faw.cn/d058-gateway/vehicle-status/public/condition/getSnapshotV2";

        // 执行网络请求
        Observable<SnapShotResponse> observable = mApi.getVehicleInfo(request.getPath(), request.getQuery(), requestParams);
        observable.subscribeOn(Schedulers.io())                 // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                .subscribe(new Observer<SnapShotResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(SnapShotResponse responseBody) {
                        if (responseBody == null) {
                            ToastUtil.showToast(QmApplication.getContext(), "SnapShotResponse = null");
                            return;
                        }
                        if (responseBody.status == BaseResponse.Status.FAILED) {
                            ToastUtil.showToast(QmApplication.getContext(), "getDriveInfo():drivedResponse.status = FAILED");
                            // 检查tsp token是否过期
                            checkTspToken(responseBody.errorCode, QMConstant.ERROR_CODE_GET_DRIVE_SNAP);

                            // 返回失败时，依然通知Service更新（无效数据）
//                            updateDriveInfo(null);
                        } else {
                            // 返回成功时，通知Service更新（有效数据）
                            updateDriveInfo(responseBody);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException) {
                            updateDriveInfo(null); // 0,0
                            return;
                        }
                        // 请求失败时，通知Service失败
                        notifyError(e, QMConstant.ERROR_CODE_GET_DRIVE_SNAP);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 熄火时调用该接口，取得驾驶评分结果
     */
    @Override
    public void getDriveScore() {
        // 调试模式下可以使用模拟数据进行离线测试
        if (isDebugMode) {
            DriveScoreHistoryEntity data = getFakeDriveScore();
            handleTestDriveScoreData(data);
//            mService.updateDriveScore(data);

            return;
        }

        // 检查网络
        if (!hasNetwork()) {
            return;
        }
        if (TextUtils.isEmpty(mTspToken)) {
            checkAvn(QMConstant.ERROR_CODE_GET_LAST_DRIVE_SCORE);
            return;
        }
        // 设置请求参数
        final DrivedRequest drivedRequest = new DrivedRequest();
        drivedRequest.setVin(mVin);
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_LAST_DRIVE_INFO)
                .query("vin", mVin)
                .query("token", mTspToken)
                .build();

//        String url = "https://znwl-uat-hgjc.faw.cn/d058-gateway/"+QMUrlConstant.GET_LAST_DRIVE_INFO;
        // 执行网络请求
        Observable<DrivedResponse> observable = mApi.getLastDriveInfo(request.getPath(), request.getQuery(), drivedRequest);
        observable.subscribeOn(Schedulers.io())                 // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                .subscribe(new Observer<DrivedResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DrivedResponse drivedResponse) {
                        KLog.e("getDriveScore onNext status :" + drivedResponse.status);
                        if (drivedResponse.status == BaseResponse.Status.FAILED) {
                            ToastUtil.showToast(QmApplication.getContext(), "getDriveScore():drivedResponse.status = FAILED");
                            // 检查tsp token是否过期
                            checkTspToken(drivedResponse.errorCode, QMConstant.ERROR_CODE_GET_LAST_DRIVE_SCORE);

                            // 返回失败时，依然通知Service更新（无效数据）
//                            updateDriveScore(null);
//                            handleLastDriveScore(null);
                        } else {
                            // 返回成功时，通知Service更新（有效数据）
//                            updateDriveScore(drivedResponse);
                            handleLastDriveScore(drivedResponse); // 此时如果为空 则是行程为结束 没有获取到行程记录
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        if (e instanceof SocketTimeoutException) {
//                            handleLastDriveScore(null);
//                            return;
//                        }
                        // 请求失败时，通知Service失败
                        KLog.e(e.getMessage());
                        notifyError(e, QMConstant.ERROR_CODE_GET_LAST_DRIVE_SCORE);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 获取上次行程的驾驶评分
     * 1.正常行程结束 获取行程的数据
     * 2.25分钟内 再次点火 获取驾驶数据
     * 3.开机 检测到上次行程异常 再次获取驾驶数据 判断行程是否结束
     *
     * @param data
     */
    private void handleLastDriveScore(DrivedResponse data) {
        KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分");
//        List<DriveScoreHistoryDetailEntity> historyDetailData = getCurrentTravelDetailData(data);
//        if (historyDetailData == null || historyDetailData.size() == 0) { // 没有数据
//            KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后没有数据了-行程结束了");
//            if (QmApplication.getEngineStatus() == QMConstant.STATUS_SHUT_DOWN) { // 熄火-25分钟倒计时结束
//
//                // 处理页面显示逻辑 和 历史记录保存
//                KLog.e(QMConstant.TAG, "熄火-25分钟倒计时结束");
//                updateDriveScore(data);
//                mService.setTravleFinish();
//                return;
//            }
//            if (QmApplication.getEngineStatus() == QMConstant.STATUS_LAUNCHED) { // 25分钟内点火
//                // 处理页面显示逻辑 和 历史记录保存
//                updateDriveScore(data);
//                // 结束本次行程
//                mService.setTravleFinish();
//                // 开始新的行程
//                mService.setTravelStart();
//                KLog.e(QMConstant.TAG, "点火状态-25分钟内再次点火-行程已结束-开始新行程");
//                return;
//            }
//
//            // todo 判断行程异常且已结束但无数据（此时会有无效数据存数据库）需要更新驾驶行为并开始新的行程
//            if (mService.isLastTravelUnusual()) {
//
//                // 处理页面显示逻辑 和 历史记录保存
//                updateDriveScore(data);
//                KLog.e(QMConstant.TAG, "行程异常且已结束但无数据");
//            }
//            // 开始新的行程
//            mService.setTravelStart();
//        } else {
//            KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后还有数据-行程未结束");
//            if (mService.isTravelStart()) { // 熄火25分钟以内
//                if (QmApplication.getEngineStatus() == QMConstant.STATUS_SHUT_DOWN) {
//                    return;
//                }
//                mService.continueTravel();// 时间不可用 因为获取到的是上一次行程的行驶时长
//                KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后还有数据-行程未结束-继续行程");
//                return;
//            }
//            // 上次行程异常且未结束
//            KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后还有数据-上次行程异常且未结束-合并行程");
//            mService.addCacheData(historyDetailData);
//            mService.mergeTravel(historyDetailData.size() * QMConstant.TIMER_MILLISECOND / 1000 / 60);// 时间不可用 因为获取到的是上一次行程的行驶时长
//        }
        // ================================以点火/熄火状态来判断==================================
        if (QmApplication.getEngineStatus() == QMConstant.STATUS_SHUT_DOWN) { // 引擎熄火状态
            // 25分钟倒计时结束 判断行程是否结束
            handleShutDownState(data);
            return;
        }
        // 引擎点火状态
        handleCarLaunchState(data);
    }

    /**
     * 处理车点火状态
     * 1.行程已开始
     *   * 25分钟倒计时未结束 点火
     *   * 25分钟倒计时结束   点火
     * 2.上次行程是否有异常
     * 3.行程开始  此时tsp后台 1.行程未开始 2.行程中 3.行程已结束 我们都开始新的行程
     * @param data
     */
    private void handleCarLaunchState(DrivedResponse data) {
        // 行程已开始 不知道此次行程是否有异常
        if (mService.isTravelStart()) {
            handleTravelStart(data);
            return;
        }
        if (mService.isLastTravelUnusual()) { // 上次行程异常
            handleLastTravelUnusual(data);
            return;
        }
        // 此时tsp后台 1.行程未开始 2.行程中 3.行程已结束 我们都开始新的行程
        mService.setTravelStart();
        KLog.e(QMConstant.TAG, "开始新的行程");
    }

    private void handleLastTravelUnusual(DrivedResponse data) {
        KLog.e(QMConstant.TAG, "开机-点火-上次行程异常");
        if (isTravelFinish(data)) { // 行程结束
            // 已结束 展示页面(是否弹个行程结束的提示) 开始新行程
            // 处理页面显示逻辑 和 历史记录保存
            updateDriveScore(data);
            // TODO: 2019/08/18 此时需要显示周弹框？
            // 结束本次行程
            mService.setTravleFinish();
            // 开始新的行程
            mService.setTravelStart();
            KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-行程已结束-开始新行程");
        } else { // 未结束 合并行程
            // 获取数据库行程 添加至cache中 并开启轮询
            DriveScoreHistoryEntity historyData = getLastedDriveHistoryData();
            if (historyData == null) {
                KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-合并行程-历史记录异常为空-开始新行程");
                mService.setTravelStart();
                return;
            }
            List<DriveScoreHistoryDetailEntity> lastedDriveData = getCurrentTravelDetailData(data);
            if (lastedDriveData == null || lastedDriveData.size() == 0) { // 有新的历史记录 但是还没有获取到详情数据 可能是五分钟还未到 没有获取到数据
                KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-合并行程-历史记录详情为空-开始新行程");
//                    mService.setTravelStart();
                mService.continueTravel();// 时间不可用 因为获取到的是上一次行程的行驶时长
                return;
            }
            mService.addCacheData(lastedDriveData);
            mService.mergeTravel(lastedDriveData.size() * (QMConstant.TIMER_MILLISECOND / 1000 / 60));// 时间不可用 因为获取到的是上一次行程的行驶时长
            KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-行程未结束-获取上次的驾驶数据-合并数据");
        }
    }

    private void handleTravelStart(DrivedResponse data) {
        // 25分钟后点火 25分钟之内和之外是否需要区分
        KLog.e(QMConstant.TAG, "点火状态-记录了行程的开始-行程未结束");
        if (mService.isTravelCompleteTimerRunning()) { // 25分钟倒计时未结束
            KLog.e(QMConstant.TAG, "点火状态-记录了行程的开始-行程未结束-25分钟倒计时未结束");
            if (isTravelFinish(data)) {
                // 已结束 展示页面(是否弹个行程结束的提示) 开始新行程
                // 处理页面显示逻辑 和 历史记录保存
                updateDriveScore(data);
                // 结束本次行程
                mService.setTravleFinish();
                // 开始新的行程
                mService.setTravelStart();
                KLog.e(QMConstant.TAG, "点火状态-25分钟内再次点火-行程已结束-开始新行程");
                return;
            }
            // 未结束
            mService.continueTravel(); // 继续行程
            KLog.e(QMConstant.TAG, "点火状态-25分钟内再次点火-行程未结束-继续行程");
        } else { // 25分钟倒计时结束
            if (isTravelFinish(data)) {
                // 已结束 展示页面(是否弹个行程结束的提示) 开始新行程
                // 处理页面显示逻辑 和 历史记录保存
                updateDriveScore(data);
                // 结束本次行程
                mService.setTravleFinish();
                // 开始新的行程
                mService.setTravelStart();
                KLog.e(QMConstant.TAG, "点火状态-25分钟之后再次点火-行程已结束-开始新行程");
                return;
            }
            KLog.e(QMConstant.TAG, "点火状态-记录了行程的开始-行程未结束-25分钟倒计时已结束-行程未结束-继续行程");
            mService.continueTravel(); // 继续行程
        }
    }

    private void handleShutDownState(DrivedResponse data) {
        if (!isTravelFinish(data)) { // 熄火状态 行程未结束 不做处理
            // TODO: 2019/08/26 25分钟倒计时结束 行程还是未结束 如何处理
            KLog.e(QMConstant.TAG, "熄火状态-25分钟倒计时结束-行程未结束");
            return;
        }
        mService.setTravleFinish();
        // 处理页面显示逻辑 和 历史记录保存
        updateDriveScore(data);
        KLog.e(QMConstant.TAG, "熄火状态-25分钟倒计时结束-行程结束");
        return;
    }

    /**
     * 获取上次行程的开始时间 如果跟正在进行中的行程的开始时间不一致 则此次行程还未结束
     * 若跟正在进行中的行程的开始时间一致  则此次行程已经结束
     *
     * @param data
     * @return
     */
    private boolean isTravelFinish(DrivedResponse data) {
        if (data == null || data.getResult() == null || data.getResult().get(0) == null) {
            KLog.e(QMConstant.TAG, "isTravelFinish第一次行程 且行程没有结束");
            return false;
        }
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
//        DrivedResponse.Bean bean = data.getResult().get(0);
//        Long startTime = bean.startTime;
//        Long lastedTravelStartTime = TPUtils.get(QmApplication.getContext(), QMConstant.LATEST_TRAVEL_START_TIME, 0L);
//        long leadTime = Math.abs(startTime - lastedTravelStartTime);
//        boolean isTravelFinish = leadTime < QMConstant.TRAVEL_START_LEAD_TIME;
//        KLog.e(QMConstant.TAG, "data.startTime = " + startTime + ",lastedTravelStartTime = " + lastedTravelStartTime + ",leadTime = " + leadTime + ",isTravelFinish = " + isTravelFinish);
//        return isTravelFinish; // 行程开始时间是本地记录的 与tsp后台记录的会有时间差
        List<DriveScoreHistoryDetailEntity> details = detailDao.getCurrentDetailsByLastHistoryEndTime(data.getResult().get(0).endTime);
        for (int i = 0; i < details.size(); i++) {
            KLog.e(QMConstant.TAG, "isTravelFinish当前行程的驾驶快照：\n" + details.get(i).toString());
        }
        KLog.e(QMConstant.TAG, "isTravelFinish" + (details == null || details.size() == 0));
        return (details == null || details.size() == 0);/* return true;*/
        /*return false;*/
    }

    private boolean isTravelFinishTest(DriveScoreHistoryEntity data) {
//        if (data == null) {
//            return true; // 结束
//        }
//        Long startTime = data.startTime;
//        Long lastedTravelStartTime = TPUtils.get(QmApplication.getContext(), QMConstant.LATEST_TRAVEL_START_TIME, 0L);
//        long leadTime = Math.abs(startTime - lastedTravelStartTime);
//        KLog.e(QMConstant.TAG,"data.startTime = "+startTime+",lastedTravelStartTime = "+lastedTravelStartTime+",leadTime = "+leadTime);
//        return leadTime < QMConstant.TRAVEL_START_LEAD_TIME; // 行程开始时间是本地记录的 与tsp后台记录的会有时间差
        return false;
    }

    /**
     * 保存驾驶记录和驾驶详情
     *
     * @param history 驾驶记录
     * @param details 驾驶详情（缓存数据）
     */
    @Override
    public void updateOrDeleteTRavelRecord(DriveScoreHistoryEntity history, List<DriveScoreHistoryDetailEntity> details) {

//        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
//        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
//        // 保证现有的历史记录个数不超过39
//        justifyHistoryRecordSize(historyDao, detailDao);
//        // 将历史记录保存到数据库中
//        historyDao.create(history);
//
//        // 关联历史记录和历史详情
//        for (DriveScoreHistoryDetailEntity each : details) {
//            each.historyId = history.id;
//        }
//
//        // 将历史详情保存到数据库中
//        detailDao.createList(details);
        // 现有逻辑是 每次行程开始就添加一条历史记录 每五分钟获取一次数据 就保存一次数据
        // 此时拿到驾驶评分 判断是否是有效数据 不是则删除 是 则保留 且更新
        if (history == null) return; // todo 不更新不删除
        if (history.travelDist < QMConstant.VALIDE_TRAVEL_DESTANCE) { // 无效
            deleteTravelRecord(); // 此时还未更新行程开始和结束时间 都为0
            KLog.e(QMConstant.TAG, "无效行程 删除数据");
        } else {
            updateTravelRecord(history);
            KLog.e(QMConstant.TAG, "有效行程 更新数据");
        }
    }

    /**
     * 该方法负责保持历史记录个数<=39
     *
     * @param historyDao
     * @param detailDao
     */
    private void justifyHistoryRecordSize(DriveScoreHistoryDao historyDao, DriveScoreHistoryDetailDao detailDao) {
        List<DriveScoreHistoryEntity> historyEntities = historyDao.queryHistoryList();
        if (historyEntities == null) return;
        if (historyEntities.size() < QMConstant.MAX_HISTORY_RECORD) {
            return;
        }
        // 历史记录>39个
        int deleteNum = historyEntities.size() - QMConstant.MAX_HISTORY_RECORD + 1; // 需要删除的记录的个数
        DriveScoreHistoryEntity[] needDeleteHistorys = new DriveScoreHistoryEntity[deleteNum];
        // 获取所有需要删除的记录对象
        for (int i = 0; i < deleteNum; i++) {
            needDeleteHistorys[i] = historyEntities.get(i);
        }
        // 遍历删除超出的历史记录
        for (int i = 0; i < needDeleteHistorys.length; i++) {
            DriveScoreHistoryEntity history = needDeleteHistorys[i];
            int historyId = history.id;
            historyDao.delete(history);
            detailDao.deleteHistoryDetail(historyId);
        }
    }

    // 更新折线图
    private void updateDriveInfo(SnapShotResponse response) {
        DriveScoreHistoryDetailEntity data = new DriveScoreHistoryDetailEntity();
        if (response == null || response.getResult() == null) {
            ToastUtil.showToast(QmApplication.getContext(), "updateDriveInfo: 获取驾驶快照接口未返回有效数据");
            // 无效对象
//            data.isValid = false;
            data.time = System.currentTimeMillis();
            data.avgSpeed = 0.0;
            data.avgFuel = 0.0;
        } else {
            // 有效对象
            setDriveInfo(response, data);
        }

        // 添加一条数据
        addDriveInfoRecord(data);

        // 通知更新
        mService.updateDriveInfo(data);
    }

    // 更新评分
    private void updateDriveScore(DrivedResponse response) {
        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        if (response == null || response.getResult() == null) {
            ToastUtil.showToast(QmApplication.getContext(), "updateDriveScore: 获取上次驾驶评分接口未返回有效数据");
            // 数据无效 即为接口请求失败 保存数据为 100分 0加速 0减速 0急转
//            data.isValid = true; // 如果未获取到驾驶评分时 为避免看不到行程 也设置数据有效
            data.startTime = 0L; // 必须为0 当第一次行程时 发生异常 且行程未结束 获取上一次驾驶记录为null，此时时间为1970年 标识此行程未结束
            data.endTime = 0L;
            data.travelTime = 0L;
            data.travelDist = 0.0;
            data.score = 100.0;
            data.accNum = 0L;
            data.decNum = 0L;
            data.turnNum = 0L;
            data.avgSpeed = 0.0;
            data.avgFuel = 0.0;
        } else {
            // 有效对象
            setDriveScore(response, data);
        }

        // 通知更新
        mService.updateDriveScore(data);
    }

    // 转换数据
    private void setDriveInfo(SnapShotResponse in, DriveScoreHistoryDetailEntity out) {
        HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = in.getResult().getMapData();

        // 当前油耗车速对应的时间
//        out.time = System.currentTimeMillis();
        out.time = in.getResult().getReportTime(); // 获取钛马后台生成的快照的生成时间 不同系统有时间差

        // 行程内平均油耗（短时平均油耗）
        SnapShotResponse.ResultEntity.DetailEntity avgFuel = mapData.get("V179");
        if (avgFuel != null) {
            out.avgFuel = MathUtils.roundString2Double(avgFuel.getVal(), 1);
        } else {
            out.avgFuel = 0.0;
        }
        KLog.e(QMConstant.TAG, "setDriveInfo 网络获取到的驾驶数据 平均油耗为：" + avgFuel.getVal());

        // 行程内平均车速（短时平均车速）
        SnapShotResponse.ResultEntity.DetailEntity avgSpeed = mapData.get("V180");
        if (avgSpeed != null) {
            out.avgSpeed = MathUtils.roundString2Double(avgSpeed.getVal(), 1);
        } else {
            out.avgSpeed = 0.0;
        }
        KLog.e(QMConstant.TAG, "setDriveInfo 网络获取到的驾驶数据 平均车速为：" + avgSpeed.getVal());
    }

    // 转换数据
    private void setDriveScore(DrivedResponse in, DriveScoreHistoryEntity out) {
        DrivedResponse.Bean bean = in.getResult().get(0);

        out.startTime = bean.startTime;
        out.endTime = bean.endTime;
        out.travelTime = bean.travelTime;
        out.travelDist = bean.travelOdograph;
        out.score = bean.score;
        out.accNum = bean.rapidAccelerateNum;
        out.decNum = bean.rapidDecelerationNum;
        out.turnNum = bean.sharpTurnNum;
        out.avgSpeed = bean.avgSpeed;
        out.avgFuel = bean.avgFuelConsumer;
    }

    // 测试用：生成一个max-min之间的随机数
    public static int randomRange(int min, int max) {
        Random rand = new Random();
        int result = rand.nextInt(max - min + 1) + min;
        return result;
    }

    // 测试用：模拟生成一条驾驶记录数据
    public DriveScoreHistoryDetailEntity getFakeDriveInfo() {
        DriveScoreHistoryDetailEntity data = new DriveScoreHistoryDetailEntity();
        if (lastTime == 0L) {
            data.time = System.currentTimeMillis();              // 第一次调用时使用系统时间
        } else {
            data.time = lastTime + 5 * 60 * 1000;                // 取得时间每次增加5分钟
        }
        data.avgSpeed = Double.valueOf(randomRange(0, 150));     // 车速在0-150之间
        data.avgFuel = Double.valueOf(randomRange(5, 20));       // 油耗在5-20之间

        // 更新上次取得时间
        lastTime = data.time;

        return data;
    }

    // 测试用：模拟生成一条驾驶详情数据
    public DriveScoreHistoryEntity getFakeDriveScore() {
        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        data.travelTime = Long.valueOf(randomRange(0, 300) * 60);   // 行驶时间在0-300分钟
        data.endTime = System.currentTimeMillis();                  // 结束时间是当前时间
        data.startTime = data.endTime - data.travelTime;            // 开始时间 = 结束时间 - 行驶时间
        data.travelDist = 10.0;                                     // 行驶距离固定为10公里
        data.accNum = Long.valueOf(randomRange(0, 3));              // 急加速次数在0-3次
        data.decNum = Long.valueOf(randomRange(0, 3));              // 急减速次数在0-3次
        data.turnNum = Long.valueOf(randomRange(0, 3));             // 急转弯次数在0-3次
        data.score = Double.valueOf(100 - (data.accNum + data.decNum + data.turnNum)); // 计算得分
        //data.score = 100.0;
        data.avgSpeed = Double.valueOf(randomRange(0, 150));        // 车速在0-150之间
        data.avgFuel = Double.valueOf(randomRange(5, 20));           // 油耗在5-20之间

        return data;
    }

    /**
     * 获取周数据接口
     * 正常情况只返回一条数据 为本周的平均数据
     */
    public void getLastWeekDriveScore() {
        if (isDebugMode) {
            mService.getLastWeekDriveScore("50.00");
            return;
        }
        if (TextUtils.isEmpty(mTspToken)) {
            checkAvn(QMConstant.ERROR_CODE_GET_LAST_WEEK_DRIVE_INFO);
            return;
        }
        final BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_LAST_WEEK_INFO)
                .query("vin", mVin)
                .query("token", mTspToken)
                .build();
        final DrivedRequest drivedRequest = new DrivedRequest();
        drivedRequest.setVin(mVin);
        drivedRequest.setStartTime(CalendarUtils.getLastWeekMondayDate().getTime() / 1000 * 1000); // 忽略毫秒位
        drivedRequest.setEndTime(CalendarUtils.getLastWeekSundayDate().getTime() / 1000 * 1000); // 忽略毫秒位
        drivedRequest.setSize(1L);

        // startTime  和 endTime / size 两个条件二选一 不设置条件则返回最近一周的驾驶数据
        mApi.getDriveInfoWeek(request.getPath(), request.getQuery(), drivedRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DrivedResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DrivedResponse drivedResponse) {
                        if (drivedResponse.status == BaseResponse.Status.FAILED) {
                            ToastUtil.showToast(QmApplication.getContext(), "getLastWeekDriveScore():drivedResponse.status = FAILED");
                            // 检查tsp token是否过期
                            checkTspToken(drivedResponse.errorCode, QMConstant.ERROR_CODE_GET_LAST_WEEK_DRIVE_INFO);
                            KLog.d("status failed");
                            return;
                        }
                        List<DrivedResponse.Bean> result = drivedResponse.getResult();
                        if (result == null && result.size() == 0) return;
                        KLog.d("last wyeek travel times is " + result.size());
                        Double totalOdograph = result.get(0).getTravelOdograph();
                        Double totalScore = result.get(0).getScore();
                        KLog.d("avg Odograph is " + totalOdograph + ",avg Score is " + totalScore);
                        // 获取到上周所有的行程的集合 自行计算平均分
                        // 若所有行程总里程数少于2公里 也不显示弹窗
                        if (totalOdograph < 2) return; // 不满足两公里
                        KLog.d("totalOdograph >= 2");
                        mService.getLastWeekDriveScore(String.valueOf(totalScore));
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        notifyError(e, QMConstant.ERROR_CODE_GET_LAST_WEEK_DRIVE_INFO);
                        KLog.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 处理测试数据
     *
     * @param data
     */
    private void handleTestDriveScoreData(DriveScoreHistoryEntity data) {
        KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分");
        List<DriveScoreHistoryDetailEntity> historyDetailData = getTestCurrentTravelDetailData(data);
        if (historyDetailData == null || historyDetailData.size() == 0) { // 没有数据
            KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后没有数据了-行程结束了");
            if (mService.isTravelStart()) { // 熄火-25分钟倒计时结束
                // 处理页面显示逻辑 和 历史记录保存
                mService.updateDriveScore(data);
                mService.setTravleFinish();
                return;
            }

            // todo 判断行程异常且已结束但无数据（此时会有无效数据存数据库）需要更新驾驶行为并开始新的行程
            if (mService.isLastTravelUnusual()) {
                // 处理页面显示逻辑 和 历史记录保存
                mService.updateDriveScore(data);
            }
            // 开始新的行程
            mService.setTravelStart();
        } else {
            KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后还有数据-行程未结束");
            if (mService.isTravelStart()) { // 熄火25分钟以内
                mService.continueTravel();// 时间不可用 因为获取到的是上一次行程的行驶时长
                KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后还有数据-行程未结束-继续行程");
                return;
            }
            // 上次行程异常且未结束
            KLog.e(QMConstant.TAG, "获取上次行程的驾驶评分-上次行程结束时间之后还有数据-上次行程异常且未结束-合并行程");
            mService.addCacheData(historyDetailData);
            mService.mergeTravel(historyDetailData.size() * QMConstant.TIMER_MILLISECOND / 1000 / 60);// 时间不可用 因为获取到的是上一次行程的行驶时长
        }
        // ================================以点火/熄火状态来判断==================================
//        if (QmApplication.getEngineStatus() == QMConstant.STATUS_SHUT_DOWN) { // 熄火状态
//            // 25分钟倒计时结束 行程结束
//            // 结束本次行程
//            mService.setTravleFinish();
//            // 处理页面显示逻辑 和 历史记录保存
//            mService.updateDriveScore(data);
//            return;
//        }
//        if (mService.isTravelCompleteTimerRunning()) { // 25分钟内再次点火
//            if (isTravelFinishTest(data)) { // 此次行程是否结束
//                // 已结束 展示页面(是否弹个行程结束的提示) 开始新行程
//                // 处理页面显示逻辑 和 历史记录保存
//                mService.updateDriveScore(data);
//                // 结束本次行程
//                // TODO: 2019/08/18 此时需要显示周弹框？
//                mService.setTravleFinish();
//                // 开始新的行程
//                mService.setTravelStart();
//            } else { // 未结束 合并行程
//                mService.mergeTravel(0);
//            }
//            return;
//        }
//        // 第一次点火 即 开机-点火
//        if (mService.isLastTravelUnusual()) { // 上次行程异常
//            if (isTravelFinishTest(data)) { // 行程结束
//                // 已结束 展示页面(是否弹个行程结束的提示) 开始新行程
//                // 处理页面显示逻辑 和 历史记录保存
//                mService.updateDriveScore(data);
//                // 结束本次行程
//                mService.setTravleFinish();
//                // 开始新的行程
//                mService.setTravelStart();
//            } else { // 未结束 合并行程
//                // 获取数据库行程 添加至cache中 并开启轮询
//                DriveScoreHistoryEntity historyData = getLastedDriveHistoryData();
//                if (historyData == null) {
//                    KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-合并行程-历史记录异常为空-开始新行程");
//                    mService.setTravelStart();
//                    return;
//                }
//                List<DriveScoreHistoryDetailEntity> lastedDriveData = getDriveHistoryDetailData(historyData);
//                if (lastedDriveData == null || lastedDriveData.size() == 0) { // 有新的历史记录 但是还没有获取到详情数据 可能是五分钟还未到 没有获取到数据
//                    KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-合并行程-历史记录详情为空-开始新行程");
////                    mService.setTravelStart();
//                    mService.mergeTravel(5);// 时间不可用 因为获取到的是上一次行程的行驶时长
//                    return;
//                }
//                mService.addCacheData(lastedDriveData);
//                mService.mergeTravel(lastedDriveData.size() * QMConstant.TIMER_MILLISECOND / 1000 / 60);// 时间不可用 因为获取到的是上一次行程的行驶时长
//                KLog.e(QMConstant.TAG, "开机-点火-上次行程异常-行程未结束-获取上次的驾驶数据-合并数据");
//            }
//            return;
//        }
        // 上次行程正常 点火 此种情况不会发生
    }

    /**
     * 行程开始时 生成一条历史数据
     */
    @Override
    public DriveScoreHistoryEntity createTravelRecord() {
        KLog.e(QMConstant.TAG, "创建一条历史数据");
        // 将历史记录保存到数据库中
        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
        // 最终更新此次行程数据时  判断历史记录的条数 以及此次行程是否是有效行程
        DriveScoreHistoryEntity historyRecord = historyDao.queryLastedHistoryRecord();
        if (historyRecord != null && historyRecord.endTime == 0) { // 上条行程为无效行程 直接删除
            deleteInvalidRecord(historyRecord);
        }
        DriveScoreHistoryEntity historyEntity = new DriveScoreHistoryEntity();
        historyDao.create(historyEntity);
        return historyEntity;
    }

    /**
     * 当获取到最终行程数据且为有效行程时更新数据
     *
     * @param entity
     */
    @Override
    public void updateTravelRecord(DriveScoreHistoryEntity entity) {
        KLog.e(QMConstant.TAG, "更新历史数据");
        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        justifyHistoryRecordSize(historyDao, detailDao); // 保证现有的历史记录个数不超过39
        DriveScoreHistoryEntity historyEntity = getLastedDriveHistoryData();
        if (historyEntity == null) {
            KLog.e(QMConstant.TAG, "更新历史数据-没有一条记录 return");
            return;
        }
        int historyId = historyEntity.id; // 将最新一条行程的id 赋给entity 才能更新行程的记录
        entity.id = historyId;
        entity.isValid = true;
        historyDao.update(entity);
    }

    /**
     * 删除历史数据
     */
    @Override
    public void deleteTravelRecord() {
        KLog.e(QMConstant.TAG, "删除无效的历史数据");
        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
        DriveScoreHistoryEntity historyEntity = getLastedDriveHistoryData();
        if (historyEntity == null) {
            return;
        }
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        detailDao.deleteHistoryDetail(historyEntity.id);
        historyDao.delete(historyEntity); // 删除历史记录
    }


    /**
     * 删除无效的历史数据
     *
     * @param historyRecord
     */
    public void deleteInvalidRecord(DriveScoreHistoryEntity historyRecord) {
        KLog.e(QMConstant.TAG, "删除无效的历史数据");
        if (historyRecord == null) {
            return;
        }
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
        detailDao.deleteHistoryDetail(historyRecord.id); // 删除快照
        historyDao.delete(historyRecord);
    }


    /**
     * 不用关联驾驶行为数据 按驾驶行为开始 结束时间去匹配快照
     *
     * @param driveDetail
     */
    private void addDriveInfoRecord(DriveScoreHistoryDetailEntity driveDetail) {
        if (driveDetail == null) return;
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        DriveScoreHistoryEntity historyEntity = getLastedDriveHistoryData();
        if (historyEntity == null) {
            historyEntity = createTravelRecord();
        }
        int historyId = historyEntity.id;
        driveDetail.historyId = historyId;
        detailDao.create(driveDetail);
        KLog.e(QMConstant.TAG, "添加一条驾驶详情数据，id = " + driveDetail.toString());
    }

    private List<DriveScoreHistoryDetailEntity> getDriveHistoryDetailData() {
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        DriveScoreHistoryEntity historyData = getLastedDriveHistoryData();
        if (historyData == null) {
            return null;
        }
        int historyId = historyData.id;
        return detailDao.queryHistoryDetail(historyId);
    }

    private List<DriveScoreHistoryDetailEntity> getDriveHistoryDetailData(DriveScoreHistoryEntity historyEntity) {
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        return detailDao.getDetailsByHistoryStartEndTime(historyEntity.startTime, historyEntity.endTime);
    }

    private DriveScoreHistoryEntity getLastedDriveHistoryData() {
        DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
        return historyDao.queryLastedHistoryRecord();
    }


    private List<DriveScoreHistoryDetailEntity> getDriveHistoryDetailData(DrivedResponse data) {
        if (data == null || data.getResult() == null) {
            return null;
        }
        DrivedResponse.Bean bean = data.getResult().get(0);
        if (bean == null) return null;
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        return detailDao.getDetailsByHistoryStartEndTime(bean.startTime, bean.endTime);
    }

    /**
     * 获取本次行程的所有快照数据
     * 如果本次行程为第一次行程 则获取表中所有的快照
     *
     * @param data
     * @return
     */
    private List<DriveScoreHistoryDetailEntity> getCurrentTravelDetailData(DrivedResponse data) {
        if (data == null || data.getResult() == null) {
            DriveScoreHistoryDao historyDao = new DriveScoreHistoryDao(QmApplication.getContext());
            DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
            Long recordCount = historyDao.queryAllDriveRecordCount(); // 获取历史记录所有记录的条数
            if (recordCount <= 1) // 第一次行程时 获取不到历史数据 则详情表中的数据都为第一次行程的数据点
                return detailDao.queryForAll();
            return null;
        }
        DrivedResponse.Bean bean = data.getResult().get(0);
        if (bean == null) return null;
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        return detailDao.getCurrentDetailsByLastHistoryEndTime(bean.endTime);
    }

    private List<DriveScoreHistoryDetailEntity> getTestCurrentTravelDetailData(DriveScoreHistoryEntity data) {
        if (data == null) return null;
        DriveScoreHistoryDetailDao detailDao = new DriveScoreHistoryDetailDao(QmApplication.getContext());
        return detailDao.getCurrentDetailsByLastHistoryEndTime(data.endTime);
    }

}
