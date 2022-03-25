package com.xiaoma.service.common.manager;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.service.api.ServiceAPI;
import com.xiaoma.service.main.model.HotLineBean;
import com.xiaoma.service.main.model.MaintainBean;
import com.xiaoma.service.main.model.MaintenancePeriodBean;
import com.xiaoma.service.order.model.OrderBean;
import com.xiaoma.service.order.model.OrderTime;
import com.xiaoma.service.order.model.ProgramBean;
import com.xiaoma.service.order.model.ProvinceBean;
import com.xiaoma.service.order.model.ShopBean;
import com.xiaoma.service.plan.model.MaintenancePlanBean;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class RequestManager {
    private RequestManager() {
    }

    private static class InstanceHolder {
        static final RequestManager sInstance = new RequestManager();
    }

    public static RequestManager getInstance() {
        return InstanceHolder.sInstance;
    }

    /**
     * 用户公里数上报
     *
     * @param driveDistance
     * @param callback
     */
    public void uploadDriveDistance(Double driveDistance, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("driveDistance", driveDistance);
        request(ServiceAPI.UPLOAD_DRIVE_DISTANCE, params, callback);
    }

    /**
     * 车服务获取首页信息(包含公里数、时间提醒)
     *
     * @param callback
     */
    public void getFirstPageShow(String vin,ResultCallback<XMResult<MaintenancePeriodBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("vin", vin);
        request(ServiceAPI.GET_FIRST_PAGE_SHOW, params, callback);
    }

    /**
     * 获取ICALL和BCALL
     *
     * @param callback
     */
    public void getServicePhone(ResultCallback<XMResult<HotLineBean>> callback) {
        request(ServiceAPI.GET_SERVCIE_PHONE, null, callback);
    }

    /**
     * 获取预约订单的集合
     *
     * @param callback
     */
    public void getAppointmentListByVIN(String vin,ResultCallback<XMResult<List<OrderBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("vin", vin);
        request(ServiceAPI.GET_APPOINTMENT_LIST_BY_VIN, params, callback);
    }

    /**
     * 取消服务订单
     *
     * @param vbillno
     * @param callback
     */
    public void cancelAppointmentByBillNo(String vbillno, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("vbillno", vbillno);
        request(ServiceAPI.CANCEL_APPOINTMENT_BY_BILLNO, params, callback);
    }

    /**
     * 获取推荐保养项目列表
     *
     * @param callback
     */
    public void getRecommendUpkeepOptions(String vin,ResultCallback<XMResult<List<MaintainBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("vin", vin);
        request(ServiceAPI.GET_RECOMMEND_UPKEEP_OPTIONS, params, callback);
    }

    /**
     * 获取养车计划
     *
     * @param vin
     * @param callback
     */
    public void getUpkeepPlans(String vin,ResultCallback<XMResult<MaintenancePlanBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("vin", vin);
        request(ServiceAPI.GET_UPKEEP_PLANS, params, callback);
    }

    /**
     * 获取城市列表
     *
     * @param callback
     */
    public void getProvinceAndCity(ResultCallback<XMResult<List<ProvinceBean>>> callback) {
        request(ServiceAPI.GET_PROVINCE_AND_CITY, null, callback);
    }

    /**
     * 获取cityName下的4s店列表
     *
     * @param cityName
     * @param callback
     */
    public void queryDealerByCity(String cityName, ResultCallback<XMResult<List<ShopBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("cityName", cityName);
        request(ServiceAPI.QUERY_DEALER_BY_CITY, params, callback);
    }

    /**
     * 获取4s店的可预约时间段
     *
     * @param callback
     */
    public void getAppointmentTime(ResultCallback<XMResult<List<OrderTime>>> callback) {
        request(ServiceAPI.GET_APPOINTMENT_TIME, null, callback);
    }

    /**
     * 获取项目列表
     *
     * @param callback
     */
    public void getFoursUpkeepOptions(ResultCallback<XMResult<List<ProgramBean>>> callback) {
        request(ServiceAPI.GET_FOURS_UPKEEP_OPTIONS, null, callback);
    }

    /**
     * 提交预约订单
     *
     * @param typeId
     * @param province
     * @param city
     * @param vDealer
     * @param vDate
     * @param timePhase
     * @param realName
     * @param mobile
     * @param callback
     */
    public void submitAppointmentOrder(String typeId, String vin,String province, String city, String vDealer, String vDate,
                                       String timePhase, String realName, String mobile, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("typeId", typeId);
        params.put("vin", vin);
        params.put("province", province);
        params.put("city", city);
        params.put("vDealer", vDealer);
        params.put("vDate", vDate);
        params.put("timePhase", timePhase);
        params.put("realName", realName);
        params.put("mobile", mobile);
        request(ServiceAPI.SUBMIT_APPOINTMENT_ORDER, params, callback);
    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }
                if (!result.isSuccess()) {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(response.code(), response.getException().getMessage());
            }
        });
    }
}
