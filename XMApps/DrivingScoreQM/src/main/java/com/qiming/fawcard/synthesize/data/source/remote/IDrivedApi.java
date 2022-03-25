package com.qiming.fawcard.synthesize.data.source.remote;

import com.qiming.fawcard.synthesize.data.entity.DriveScoreCheckAvnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTboxSnResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTokenResponse;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreTspTokenResponse;
import com.qiming.fawcard.synthesize.data.entity.DrivedRequest;
import com.qiming.fawcard.synthesize.data.entity.DrivedResponse;
import com.qiming.fawcard.synthesize.data.entity.SnapShotResponse;
import com.qiming.fawcard.synthesize.data.entity.UploadScoreResponse;
import com.qiming.fawcard.synthesize.data.entity.VehicleSnapShotRequest;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by My on 2018/7/2.
 */

public interface IDrivedApi {

    /**
     * 查询最新行程快照数据
     *
     * @param url
     * @param query
     * @return
     */
    @Headers("Content-Type:application/json")
    @POST
    Observable<DrivedResponse> getLastDriveInfo(@Url String url, @QueryMap Map<String, Object> query, @Body DrivedRequest request);

    /**
     * 查询行程数据列表
     *
     * @param url
     * @param query
     * @return
     */
    @Headers("Content-Type:application/json")
    @POST
    Observable<DrivedResponse> getDriveInfoList(@Url String url, @QueryMap Map<String, Object> query, @Body DrivedRequest request);

    /**
     * 查询周统计数据
     *
     * @param url
     * @param query
     * @return
     */
    @Headers("Content-Type:application/json")
    @POST
    Observable<DrivedResponse> getDriveInfoWeek(@Url String url, @QueryMap Map<String, Object> query, @Body DrivedRequest request);

    /**
     * 查询历史统计数据
     *
     * @param url
     * @param query
     * @param request
     * @return
     */
    @Headers("Content-Type:application/json")
    @POST
    Observable<DrivedResponse> getDriveInfoHistory(@Url String url, @QueryMap Map<String, Object> query, @Body DrivedRequest request);

    /**
     * 获取车辆快照
     * （来自D077工程的IControlServiceApi.java）
     *
     * @param path    获取车辆快照路径
     * @param query   固定参数
     * @param request body参数
     * @return
     */
    @Headers("Content-Type:application/json")
    @POST
    Observable<SnapShotResponse> getVehicleInfo(@Url String path, @QueryMap Map<String, Object>
            query, @Body VehicleSnapShotRequest request);

    /**
     * 车机认证
     *
     * @param path    车机认证路径
     * @param query   请求参数
     * @param requestMap Form参数
     * @return
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST
    @FormUrlEncoded
    Observable<DriveScoreCheckAvnResponse> checkAvn(@Url String path, @QueryMap Map<String, Object>
            query, @FieldMap Map<String, String> requestMap);

    /**
     * 获取小马云token
     * @param url
     * @return
     */
    @GET
    Observable<DriveScoreTokenResponse> getToken(@Url String url);

    /**
     * 驾驶评分上报接口
     * @param url
     * @param token
     * @param data
     * @return
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST
    @FormUrlEncoded
    Observable<UploadScoreResponse> uploadScore(@Url String url, @Header("token") String token, @FieldMap Map<String, String> data);

    /**
     * 通过VIN码获取TSP的token
     * @param url
     * @param token
     * @param data
     * @return
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST
    @FormUrlEncoded
    Observable<DriveScoreTspTokenResponse> getTspToken(@Url String url, @Header("token") String token, @FieldMap Map<String, String> data);

    /**
     * 通过VIN码获取tboxSn
     * @param url
     * @param token
     * @param data
     * @return
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST
    @FormUrlEncoded
    Observable<DriveScoreTboxSnResponse> getTboxSnByVin(@Url String url, @Header("token") String token, @FieldMap Map<String, String> data);

}
