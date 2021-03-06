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
        // ??????????????????
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
        // ??????????????????
        final String url = XiaomaUrlConstant.getFullPath(XiaomaUrlConstant.TBOXSN);
        Map<String, Object> data = new HashMap<>();
        data.put("vin", CommonInfoHolder.getInstance().getVin());
        Map<String, String> signMap = new HashMap<>();
        signMap.put("data", new Gson().toJson(data));
        signMap.put("sign", SignatureUtil.getSign(signMap, "qiming"));  // ???????????????key???????????????qiming

        // ??????????????????
        mIDrivedApi.getTboxSnByVin(url, mToken, signMap)
                   .subscribeOn(Schedulers.io())                 // ?????????IO????????????????????????
                   .observeOn(AndroidSchedulers.mainThread())      // ????????????????????????UI??????
                   .subscribe(new Observer<DriveScoreTboxSnResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DriveScoreTboxSnResponse responseBody) {
                        // ???vin???tboxsn???????????????????????????tsp token??????????????????
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
        // ??????????????????
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("vin", mVin);
        requestMap.put("avnSn", mTboxSn);
        requestMap.put("appId", "AVN001");    // ???????????????????????????AVN001
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.CHECKAVN)
                .build();

        // ??????????????????
        mIDrivedApi.checkAvn(request.getFullPath(), request.getQuery(), requestMap)
                   .subscribeOn(Schedulers.io())                   // ?????????IO????????????????????????
                   .observeOn(AndroidSchedulers.mainThread())      // ????????????????????????UI??????
                   .subscribe(new Observer<DriveScoreCheckAvnResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DriveScoreCheckAvnResponse responseBody) {
                        // ???tsp token??????????????????getDriveInfo???getDriveScore??????????????????
                        KLog.d(TAG, "checkAvn: onNext");

                        mTspToken = responseBody.getToken();
                        KLog.d(TAG, "checkAvn->mTspToken: " + mTspToken);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ????????????????????????Service??????
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
        // ??????????????????
        final DrivedRequest drivedRequest = new DrivedRequest();
        drivedRequest.setVin(mVin);
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_LAST_DRIVE_INFO)
                .query("vin", mVin)
                .query("token", mTspToken)
                .build();

        // ??????????????????
        mIDrivedApi.getLastDriveInfo(request.getFullPath(), request.getQuery(), drivedRequest)
                   .subscribeOn(Schedulers.io())                 // ?????????IO????????????????????????
                   .observeOn(AndroidSchedulers.mainThread())      // ????????????????????????UI??????
                   .subscribe(new Observer<DrivedResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DrivedResponse drivedResponse) {
                        KLog.d(TAG, "getLastDriveInfo : onNext");
                        if (drivedResponse.status == BaseResponse.Status.FAILED) {
                            KLog.d(TAG, "getLastDriveInfo : BaseResponse.Status.FAILED");
                            // ??????tsp token????????????
                            KLog.d(TAG, "getLastDriveInfo->drivedResponse.errorCode:" + drivedResponse.errorCode);
                            // ??????????????????????????????Service????????????????????????
                        } else {
                            KLog.d(TAG, "getLastDriveInfo : BaseResponse.Status.SUCCEED");
                            // ????????????????????????Service????????????????????????
                            KLog.d(TAG, "getLastDriveInfo->vin: " + drivedResponse.result.get(0).vin);
                            KLog.d(TAG, "getLastDriveInfo->score: " + drivedResponse.result.get(0).score);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ????????????????????????Service??????
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
        // ??????????????????
        VehicleSnapShotRequest requestParams = new VehicleSnapShotRequest();
        requestParams.setVin(mVin);
        BaseRequest request = new BaseRequest.Builder()
                .path(QMUrlConstant.GET_REMOTE_INQUIRY)
                .query("vin", mVin)
                .query("token", mTspToken)
                .build();

        // ??????request.getFullPath()?????? https://znwl-uat-hgjc.faw.cn/vehicle-status/public/condition/getSnapshotV2
        // ????????????????????????????????? https://znwl-uat-hgjc.faw.cn/d058-gateway/vehicle-status/public/condition/getSnapshotV2
        // ???????????????QMUrlConstant??????????????????????????????????????????url?????????d058-gateway???????????????
        final String url = "https://znwl-uat-hgjc.faw.cn/d058-gateway/vehicle-status/public/condition/getSnapshotV2";

        // ??????????????????
        mIDrivedApi.getVehicleInfo(url, request.getQuery(), requestParams)
                   .subscribeOn(Schedulers.io())                 // ?????????IO????????????????????????
                   .observeOn(AndroidSchedulers.mainThread())      // ????????????????????????UI??????
                   .subscribe(new Observer<SnapShotResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(SnapShotResponse responseBody) {
                        KLog.d(TAG, "getVehicleInfo : onNext");
                        if (responseBody.status == BaseResponse.Status.FAILED) {
                            KLog.d(TAG, "getVehicleInfo : BaseResponse.Status.FAILED");
                            // ??????tsp token????????????
                            KLog.d(TAG, "getVehicleInfo->drivedResponse.errorCode:" + responseBody.errorCode);
                            // ??????????????????????????????Service????????????????????????
                        } else {
                            // ????????????????????????Service????????????????????????
                            KLog.d(TAG, "getVehicleInfo : BaseResponse.Status.SUCCEED");
                            KLog.d(TAG, "getVehicleInfo->PageCount: " + responseBody.getPageCount());
                            KLog.d(TAG, "getVehicleInfo->PageIndex: " + responseBody.getPageIndex());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ????????????????????????Service??????
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
        // ??????????????????
        final String url = XiaomaUrlConstant.getFullPath(XiaomaUrlConstant.UPLOAD_SCORE);
        String token = mToken;
        Map<String, Object> data = new HashMap<>();
        data.put("channelId", "AA1090");    // ????????????????????????AA1090
        data.put("vin", mVin);
        Map<String, String> signMap = new HashMap<>();
        signMap.put("data", new Gson().toJson(data));
        signMap.put("sign", SignatureUtil.getSign(signMap, "qiming"));  // ???????????????key???????????????qiming

        // ??????????????????
        mIDrivedApi.uploadScore(url, token, signMap)
                   .subscribeOn(Schedulers.io())             // ?????????IO????????????????????????
                   .observeOn(AndroidSchedulers.mainThread())  // ????????????????????????UI??????
                   .subscribe(new Observer<UploadScoreResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UploadScoreResponse responseBody) {
                        KLog.d(TAG, "uploadScore: onNext");
                        // ????????????????????????Service??????
                        // ??????service?????????????????????
                        KLog.d(TAG, "uploadScore->resultCode: " + responseBody.resultCode);
                        KLog.d(TAG, "uploadScore->resultMessage: " + responseBody.resultMessage);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ????????????????????????Service??????
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
