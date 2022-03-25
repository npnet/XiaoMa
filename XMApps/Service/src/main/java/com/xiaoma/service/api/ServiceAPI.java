package com.xiaoma.service.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by ZSH on 2018/12/6 0006.
 */

public final class ServiceAPI {

    public static final String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    //-------------------------首页相关接口start--------------------------
    /**
     * 用户公里数上报
     */
    public static final String UPLOAD_DRIVE_DISTANCE = BASE_URL + "carserve/driveDistanceUpload";

    /**
     * 车服务获取首页信息(包含公里数、时间提醒)
     */
    public static final String GET_FIRST_PAGE_SHOW = BASE_URL + "carserve/getFirstPageShow";

    /**
     * 获取ICALL和BCALL接口
     */
    public static final String GET_SERVCIE_PHONE = BASE_URL + "carserve/getServciePhone";
    //-------------------------首页相关接口end--------------------------



    //-------------------------预约订单相关接口start--------------------------
    /**
     * 根据vin获取预约订单的集合
     */
    public static final String GET_APPOINTMENT_LIST_BY_VIN = BASE_URL + "carserve/getAppointmentListByVIN";

    /**
     * 根据预约单编号取消服务订单
     */
    public static final String CANCEL_APPOINTMENT_BY_BILLNO = BASE_URL + "carserve/cancelAppointmentByBillNo";
    //-------------------------预约订单相关接口end--------------------------



    //------------------------保养计划相关接口start--------------------------
    /**
     * 用户拉取对应保养计划的所有推荐保养项目
     */
    public static final String GET_RECOMMEND_UPKEEP_OPTIONS = BASE_URL + "carserve/getRecommendUpkeepOptions";

    /**
     * 获取养车计划以及用户当前所在养车的阶段
     */

    public static final String GET_UPKEEP_PLANS = BASE_URL + "carserve/getUpkeepPlans";
    //-------------------------保养计划相关接口end--------------------------




    //-------------------------预约养车服务相关接口start--------------------------
    /**
     * 获取城市列表
     */
    public static final String GET_PROVINCE_AND_CITY = BASE_URL + "carserve/getProvinceAndCity";

    /**
     * 获取cityName下的4s店列表
     */
    public static final String QUERY_DEALER_BY_CITY = BASE_URL + "carserve/queryDealerByCity";

    /**
     * 获取4s店的可预约时间段
     */
    public static final String GET_APPOINTMENT_TIME = BASE_URL + "carserve/getAppointmentTime";

    /**
     * 获取项目列表
     */
    public static final String GET_FOURS_UPKEEP_OPTIONS = BASE_URL + "carserve/getFoursUpkeepOptions";

    /**
     * 提交预约订单
     */
    public static final String SUBMIT_APPOINTMENT_ORDER = BASE_URL + "carserve/submitAppointmentOrder";
    //-------------------------预约养车服务相关接口end--------------------------

}
