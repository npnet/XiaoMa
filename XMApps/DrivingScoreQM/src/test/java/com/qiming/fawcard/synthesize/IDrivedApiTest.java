package com.qiming.fawcard.synthesize;

import android.util.Log;

import com.google.gson.Gson;
import com.qiming.fawcard.synthesize.base.BaseRequest;
import com.qiming.fawcard.synthesize.base.BaseResponse;
import com.qiming.fawcard.synthesize.base.CommonInfoHolder;
import com.qiming.fawcard.synthesize.base.RxJavaRule;
import com.qiming.fawcard.synthesize.base.constant.QMUrlConstant;
import com.qiming.fawcard.synthesize.base.constant.XiaomaUrlConstant;
import com.qiming.fawcard.synthesize.base.util.SignatureUtil;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreCheckAvnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTboxSnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTokenResponse;
import com.qiming.fawcard.synthesize.data.entity.DrivedRequest;
import com.qiming.fawcard.synthesize.data.entity.DrivedResponse;
import com.qiming.fawcard.synthesize.data.entity.SnapShotResponse;
import com.qiming.fawcard.synthesize.data.entity.UploadScoreResponse;
import com.qiming.fawcard.synthesize.data.entity.VehicleSnapShotRequest;
import com.qiming.fawcard.synthesize.data.source.remote.IDrivedApi;
import com.xiaoma.utils.log.KLog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class IDrivedApiTest {
    private static final String TAG = "IDrivedApiTest";

    private String mToken;
    private String mTboxSn;
    private String mVin;
    private String mTspToken;

    private IDrivedApi mIDrivedApi;

    @Rule
    public RxJavaRule rule = new RxJavaRule();

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        mIDrivedApi = IDrivedApiService.createIDrivedApi();
        mVin = "disnet";
        mTspToken = "disnet";
    }

    @Test
    public void drivedApiTest(){
        getTokenTest();
        getTboxSnByVinTest();
        checkAvnTest();
        getLastDriveInfoTest();
        getVehicleInfoTest();
        uploadScoreTest();
    }

    private void getTokenTest() {
        KLog.d(TAG, "-----------getTokenTest start----------");
        // 设置请求参数
        final String url = XiaomaUrlConstant.getFullPath(XiaomaUrlConstant.XIAOMA_TOKEN);

        mIDrivedApi.getToken(url)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Observer<DriveScoreTokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(DriveScoreTokenResponse response) {
                        KLog.d(TAG, "getToken: onNext");
                        mToken = response.getToken();
                        KLog.d(TAG, "getToken->token: " + mToken);
                        KLog.d(TAG, "getToken->ResultCode: " + response.getResultCode());
                        KLog.d(TAG, "getToken->ResultMessage: " + response.getResultMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.d(TAG, "getToken: onError");
                        KLog.e(TAG, "getToken: " + e.toString());
                    }

                    @Override
                    public void onComplete() {}
                });
        KLog.d(TAG, "-----------getTokenTest end----------");
    }

    private void getTboxSnByVinTest() {
        KLog.d(TAG, "-----------getTboxSnByVinTest start----------");
        // 设置请求参数
        final String url = XiaomaUrlConstant.getFullPath(XiaomaUrlConstant.TBOXSN);
        Map<String, Object> data = new HashMap<>();
        data.put("vin", CommonInfoHolder.getInstance().getVin());
        Map<String, String> signMap = new HashMap<>();
        signMap.put("data", new Gson().toJson(data));
        signMap.put("sign", SignatureUtil.getSign(signMap, "qiming"));  // 签名算法的key是固定值：qiming

        // 执行网络请求
        mIDrivedApi.getTboxSnByVin(url, mToken, signMap)
                   .subscribeOn(Schedulers.io())                 // 切换到IO线程进行网络请求
                   .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                   .subscribe(new Observer<DriveScoreTboxSnResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DriveScoreTboxSnResponse responseBody) {
                        // 将vin和tboxsn保存起来（每次更新tsp token时都会用到）
                        KLog.d(TAG, "getTboxSnByVin: onNext");

                        mVin = responseBody.data.vin;
                        mTboxSn = responseBody.data.tboxSN;
                        KLog.d(TAG, "getTboxSnByVin->mVin: " + mVin);
                        KLog.d(TAG, "getTboxSnByVin->mTboxSn: " + mTboxSn);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.d(TAG, "getTboxSnByVin: onError");
                        KLog.e(TAG, "getTboxSnByVin: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        KLog.d(TAG, "-----------getTboxSnByVinTest end----------");
    }

    private void checkAvnTest() {
        KLog.d(TAG, "-----------checkAvnTest start----------");
        // 设置请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("vin", mVin);
        requestMap.put("avnSn", mTboxSn);
        requestMap.put("appId", "AVN001");    // 车机端使用固定值：AVN001
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.CHECKAVN)
                .build();

        // 执行网络请求
        mIDrivedApi.checkAvn(request.getFullPath(), request.getQuery(), requestMap)
                   .subscribeOn(Schedulers.io())                   // 切换到IO线程进行网络请求
                   .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                   .subscribe(new Observer<DriveScoreCheckAvnResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DriveScoreCheckAvnResponse responseBody) {
                        // 将tsp token保存起来（在getDriveInfo和getDriveScore时还会用到）
                        KLog.d(TAG, "checkAvn: onNext");

                        mTspToken = responseBody.getToken();
                        KLog.d(TAG, "checkAvn->mTspToken: " + mTspToken);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        KLog.d(TAG, "checkAvn: onError");
                        KLog.e(TAG, "checkAvn: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        KLog.d(TAG, "-----------checkAvnTest end----------");
    }

    private void getLastDriveInfoTest() {
        KLog.d(TAG, "-----------getLastDriveInfoTest start----------");
        // 设置请求参数
        final DrivedRequest drivedRequest = new DrivedRequest();
        drivedRequest.setVin(mVin);
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_LAST_DRIVE_INFO)
                .query("vin", mVin)
                .query("token", mTspToken)
                .build();

        // 执行网络请求
        mIDrivedApi.getLastDriveInfo(request.getFullPath(), request.getQuery(), drivedRequest)
                   .subscribeOn(Schedulers.io())                 // 切换到IO线程进行网络请求
                   .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                   .subscribe(new Observer<DrivedResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DrivedResponse drivedResponse) {
                        KLog.d(TAG, "getLastDriveInfo : onNext");
                        if (drivedResponse.status == BaseResponse.Status.FAILED) {
                            KLog.d(TAG, "getLastDriveInfo : BaseResponse.Status.FAILED");
                            // 检查tsp token是否过期
                            KLog.d(TAG, "getLastDriveInfo->drivedResponse.errorCode:" + drivedResponse.errorCode);
                            // 返回失败时，依然通知Service更新（无效数据）
                        } else {
                            KLog.d(TAG, "getLastDriveInfo : BaseResponse.Status.SUCCEED");
                            // 返回成功时，通知Service更新（有效数据）
                            KLog.d(TAG, "getLastDriveInfo->vin: " + drivedResponse.result.get(0).vin);
                            KLog.d(TAG, "getLastDriveInfo->score: " + drivedResponse.result.get(0).score);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        KLog.d(TAG, "getLastDriveInfo: onError");
                        KLog.e(TAG, "getLastDriveInfo: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        KLog.d(TAG, "-----------getLastDriveInfoTest end----------");
    }

    private void getVehicleInfoTest() {
        KLog.d(TAG, "-----------getVehicleInfoTest start----------");
        // 设置请求参数
        VehicleSnapShotRequest requestParams = new VehicleSnapShotRequest();
        requestParams.setVin(mVin);
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_REMOTE_INQUIRY)
                .query("vin", mVin)
                .query("token", mTspToken)
                .build();

        // 这里request.getFullPath()的值 https://znwl-uat-hgjc.faw.cn/vehicle-status/public/condition/getSnapshotV2
        // 因为接口变更需要替换成 https://znwl-uat-hgjc.faw.cn/d058-gateway/vehicle-status/public/condition/getSnapshotV2
        // 不能直接在QMUrlConstant定义成常量的原因是签名计算的url部分中d058-gateway不参与计算
        final String url = "https://znwl-uat-hgjc.faw.cn/d058-gateway/vehicle-status/public/condition/getSnapshotV2";

        // 执行网络请求
        mIDrivedApi.getVehicleInfo(url, request.getQuery(), requestParams)
                   .subscribeOn(Schedulers.io())                 // 切换到IO线程进行网络请求
                   .observeOn(AndroidSchedulers.mainThread())      // 切换到主线程进行UI更新
                   .subscribe(new Observer<SnapShotResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(SnapShotResponse responseBody) {
                        KLog.d(TAG, "getVehicleInfo : onNext");
                        if (responseBody.status == BaseResponse.Status.FAILED) {
                            KLog.d(TAG, "getVehicleInfo : BaseResponse.Status.FAILED");
                            // 检查tsp token是否过期
                            KLog.d(TAG, "getVehicleInfo->drivedResponse.errorCode:" + responseBody.errorCode);
                            // 返回失败时，依然通知Service更新（无效数据）
                        } else {
                            // 返回成功时，通知Service更新（有效数据）
                            KLog.d(TAG, "getVehicleInfo : BaseResponse.Status.SUCCEED");
                            KLog.d(TAG, "getVehicleInfo->PageCount: " + responseBody.getPageCount());
                            KLog.d(TAG, "getVehicleInfo->PageIndex: " + responseBody.getPageIndex());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        KLog.d(TAG, "getVehicleInfo: onError");
                        KLog.e(TAG, "getVehicleInfo: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        KLog.d(TAG, "-----------getVehicleInfoTest end----------");
    }

    private void uploadScoreTest() {
        KLog.d(TAG, "-----------uploadScoreTest start----------");
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
        mIDrivedApi.uploadScore(url, token, signMap)
                   .subscribeOn(Schedulers.io())             // 切换到IO线程进行网络请求
                   .observeOn(AndroidSchedulers.mainThread())  // 切换到主线程进行UI更新
                   .subscribe(new Observer<UploadScoreResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UploadScoreResponse responseBody) {
                        KLog.d(TAG, "uploadScore: onNext");
                        // 请求成功时，通知Service更新
                        // 通知service是否上传成功？
                        KLog.d(TAG, "uploadScore->resultCode: " + responseBody.resultCode);
                        KLog.d(TAG, "uploadScore->resultMessage: " + responseBody.resultMessage);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 请求失败时，通知Service失败
                        KLog.d(TAG, "uploadScore: onError");
                        KLog.e(TAG, "uploadScore: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        KLog.d(TAG, "-----------uploadScoreTest end----------");
    }
}
