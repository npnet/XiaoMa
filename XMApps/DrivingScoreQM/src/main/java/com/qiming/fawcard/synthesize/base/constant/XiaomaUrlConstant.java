package com.qiming.fawcard.synthesize.base.constant;

/**
 * Created by My on 2018/8/1.
 */

public class XiaomaUrlConstant {
    //小马云平台测试环境服务器地址
    private static final String SERVER_URL = "http://www.carbuyin.cn:19092/";

    public static String getFullPath(String path) {
        return SERVER_URL + path;
    }

    /**
     * 获取小马云token
     */
    public static final String XIAOMA_TOKEN = "qiming/token";

    /**
     * 驾驶评分上报接口
     */
    public static final String UPLOAD_SCORE = "rest/qiming/driving/report.action";

    /**
     * 通过VIN码获取TSP的token
     */
    public static final String TSP_TOKEN = "rest/qiming/getTspToken";

    /**
     * 通过VIN码获取tboxSn
     */
    public static final String TBOXSN = "rest/qiming/getTboxSnByVin";
}