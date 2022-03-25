package com.qiming.fawcard.synthesize;

import com.qiming.fawcard.synthesize.base.BaseResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreCheckAvnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTboxSnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTokenResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTspTokenResponse;
import com.qiming.fawcard.synthesize.data.entity.DrivedRequest;
import com.qiming.fawcard.synthesize.data.entity.DrivedResponse;
import com.qiming.fawcard.synthesize.data.entity.SnapShotResponse;
import com.qiming.fawcard.synthesize.data.entity.UploadScoreResponse;
import com.qiming.fawcard.synthesize.data.entity.VehicleSnapShotRequest;
import com.qiming.fawcard.synthesize.data.source.remote.IDrivedApi;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

// IDrivedApiMock
public class DrivedApiMock implements IDrivedApi {

    DrivedResponse dirveScoreResponse;
    DriveScoreTboxSnResponse driveScoreTboxSnResponse;
    DriveScoreCheckAvnResponse driveScoreCheckAvnResponse;
    DriveScoreTokenResponse driveScoreTokenResponse;
    SnapShotResponse snapShotResponse;
    UploadScoreResponse uploadScoreResponse;
    DriveScoreTspTokenResponse driveScoreTspTokenResponse;

    // 通过参数构造Response
    public void setDriveScoreTboxSnResponse(String resultCode, String resultMessage, String vin, String tboxSN){
        driveScoreTboxSnResponse = new DriveScoreTboxSnResponse();
        driveScoreTboxSnResponse.resultCode = resultCode;
        driveScoreTboxSnResponse.resultMessage = resultMessage;
        driveScoreTboxSnResponse.data = driveScoreTboxSnResponse.new TboxSn();
        driveScoreTboxSnResponse.data.vin = vin;
        driveScoreTboxSnResponse.data.tboxSN = tboxSN;
    }

    public void setDriveScoreCheckAvnResponse(String token){
        driveScoreCheckAvnResponse = new DriveScoreCheckAvnResponse();
        driveScoreCheckAvnResponse.setToken(token);
    }

    public void setDriveScoreTokenResponse(String token, String resultCode, String resultMsg){
        driveScoreTokenResponse = new DriveScoreTokenResponse();
        driveScoreTokenResponse.setToken(token);
        driveScoreTokenResponse.setResultCode(resultCode);
        driveScoreTokenResponse.setResultMessage(resultMsg);
    }

    public void setSnapShotResponse(SnapShotResponse.ResultEntity result,
                                    String pageCount, String pageIndex, String pageSize,
                                    String totalCount, String extMessage, String scrollId,
                                    BaseResponse.Status stat, String errCode, String errMsg){

        snapShotResponse = new SnapShotResponse();

        SnapShotResponse.ResultEntity entity = snapShotResponse.new ResultEntity();
        HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = new HashMap<String, SnapShotResponse.ResultEntity.DetailEntity>();
        SnapShotResponse.ResultEntity.DetailEntity avgFuel = entity.new DetailEntity();
        SnapShotResponse.ResultEntity.DetailEntity avgSpeed = entity.new DetailEntity();
        result.setMapData(mapData);
        snapShotResponse.setResult(result);
        snapShotResponse.setPageCount(pageCount);
        snapShotResponse.setPageIndex(pageIndex);
        snapShotResponse.setPageSize(pageSize);
        snapShotResponse.setTotalCount(totalCount);
        snapShotResponse.setExtMessage(extMessage);
        snapShotResponse.setScrollId(scrollId);
        snapShotResponse.status = stat;
        snapShotResponse.errorCode = errCode;
        snapShotResponse.errorMessage = errMsg;
    }

    public void setUploadScoreResponse(String resCode, String resMsg){
        uploadScoreResponse = new UploadScoreResponse();
        uploadScoreResponse.resultCode = resCode;
        uploadScoreResponse.resultMessage = resMsg;
    }

    // 外部创建Response对象
    public void setDriveScoreTboxSnResponse(DriveScoreTboxSnResponse res){ driveScoreTboxSnResponse = res; }
    public void setDriveScoreCheckAvnResponse(DriveScoreCheckAvnResponse res){ driveScoreCheckAvnResponse = res; }
    public void setDriveScoreTokenResponse(DriveScoreTokenResponse res){ driveScoreTokenResponse = res; }
    public void setSnapShotResponse(SnapShotResponse res){ snapShotResponse = res; }
    public void setDriveScore(DrivedResponse res){ dirveScoreResponse = res; }
    public void setTokenResponse(DriveScoreTokenResponse res){ driveScoreTokenResponse = res; }
    public void setUploadScoreResponse(UploadScoreResponse res){ uploadScoreResponse = res; }

    @Override
    public Observable<DrivedResponse> getLastDriveInfo(String url, Map<String, Object> query, DrivedRequest request) {
        if(dirveScoreResponse == null){
            dirveScoreResponse = new DrivedResponse();
            List<DrivedResponse.Bean> result = new ArrayList<>();
            DrivedResponse.Bean bean = new DrivedResponse.Bean();
            bean.startTime = 1508849859848L;
            bean.endTime = 2608849859848L;
            bean.travelTime = 3508849859848L;
            bean.travelOdograph = 120.0;
            bean.score = 99.0;
            bean.rapidAccelerateNum = 2L;
            bean.rapidDecelerationNum = 2L;
            bean.sharpTurnNum = 3L;
            bean.avgSpeed = 60.0;
            bean.avgFuelConsumer = 3.0;
            result.add(bean);
            dirveScoreResponse.setResult(result);
        }

        Observable<DrivedResponse> observable = Observable.just(dirveScoreResponse);
        return observable;
    }

    @Override
    public Observable<DrivedResponse> getDriveInfoList(String url, Map<String, Object> query, DrivedRequest request) {
        return null;
    }

    @Override
    public Observable<DrivedResponse> getDriveInfoWeek(String url, Map<String, Object> query, DrivedRequest request) {
        return null;
    }

    @Override
    public Observable<DrivedResponse> getDriveInfoHistory(String url, Map<String, Object> query, DrivedRequest request) {
        return null;
    }

    @Override
    public Observable<SnapShotResponse> getVehicleInfo(String path, Map<String, Object> query, VehicleSnapShotRequest request) {
        KLog.d("test", "getVehicleInfo " );
        if(snapShotResponse == null){
            snapShotResponse = new SnapShotResponse();
            SnapShotResponse.ResultEntity entity = snapShotResponse.new ResultEntity();
            HashMap<String, SnapShotResponse.ResultEntity.DetailEntity> mapData = new HashMap<String, SnapShotResponse.ResultEntity.DetailEntity>();
            SnapShotResponse.ResultEntity.DetailEntity avgFuel = entity.new DetailEntity();
            SnapShotResponse.ResultEntity.DetailEntity avgSpeed = entity.new DetailEntity();
            SnapShotResponse.ResultEntity result = new SnapShotResponse().new ResultEntity();
            result.setMapData(mapData);
            snapShotResponse.setResult(result);
            snapShotResponse.setPageCount("1");
            snapShotResponse.setPageIndex("2");
            snapShotResponse.setPageSize("30");
            snapShotResponse.setTotalCount("5");
            snapShotResponse.setExtMessage("msg01");
            snapShotResponse.setScrollId("sc01");
            snapShotResponse.status = BaseResponse.Status.SUCCEED;
            snapShotResponse.errorCode = "err01";
            snapShotResponse.errorMessage = "errMsg01";
        }

        Observable<SnapShotResponse> observable = Observable.just(snapShotResponse);
        return observable;
    }

    @Override
    public Observable<UploadScoreResponse> uploadScore(String url, String token, Map<String, String> data) {
        KLog.d("test", "uploadScore");
        if(uploadScoreResponse == null){
            uploadScoreResponse = new UploadScoreResponse();
            uploadScoreResponse.resultCode = "sucessed";
            uploadScoreResponse.resultMessage = "ok";
        }

        Observable<UploadScoreResponse> observable = Observable.just(uploadScoreResponse);
        return observable;
    }

    @Override
    public Observable<DriveScoreTokenResponse> getToken(String url) {
        KLog.d("test", "getToken");
        if(driveScoreTokenResponse == null){
            driveScoreTokenResponse = new DriveScoreTokenResponse();
            driveScoreTokenResponse.setToken("tk001");
            driveScoreTokenResponse.setResultCode("resCode");
            driveScoreTokenResponse.setResultMessage("resMsg");
        }

        Observable<DriveScoreTokenResponse> observable = Observable.just(driveScoreTokenResponse);
        return observable;
    }

    @Override
    public Observable<DriveScoreTspTokenResponse> getTspToken(String url, String token, Map<String, String> data) {
        KLog.d("test", "getTspToken");
        if(driveScoreTspTokenResponse == null){
            DriveScoreTspTokenResponse response = new DriveScoreTspTokenResponse();
            response.data = "ScoreTspTokenResponse";
        }

        Observable<DriveScoreTspTokenResponse> observable = Observable.just(driveScoreTspTokenResponse);
        return observable;
    }

    @Override
    public Observable<DriveScoreTboxSnResponse> getTboxSnByVin(String url, String token, Map<String, String> data) {
        if(driveScoreTboxSnResponse == null){
            driveScoreTboxSnResponse = new DriveScoreTboxSnResponse();
            driveScoreTboxSnResponse.resultCode = "resTBoxSn001";
            driveScoreTboxSnResponse.resultMessage = "resMsg002";
            driveScoreTboxSnResponse.data = driveScoreTboxSnResponse.new TboxSn();
            driveScoreTboxSnResponse.data.vin = "vin003";
            driveScoreTboxSnResponse.data.tboxSN = "tboxSN004";
        }

        Observable<DriveScoreTboxSnResponse> observable = Observable.just(driveScoreTboxSnResponse);
        return observable;
    }

    @Override
    public Observable<DriveScoreCheckAvnResponse> checkAvn(String path, Map<String, Object> query, Map<String, String> requestMap) {
        if(driveScoreCheckAvnResponse == null){
            driveScoreCheckAvnResponse = new DriveScoreCheckAvnResponse();
            driveScoreCheckAvnResponse.setToken("tk001");
        }

        Observable<DriveScoreCheckAvnResponse> observable = Observable.just(driveScoreCheckAvnResponse);
        return observable;
    }
}