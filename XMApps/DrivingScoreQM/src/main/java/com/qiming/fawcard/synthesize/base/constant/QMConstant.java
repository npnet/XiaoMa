/**
 * Copyright (c) 2017 Qiming TecSoftware Co., Ltd. All Rights Reserved
 */
package com.qiming.fawcard.synthesize.base.constant;

public class QMConstant {
    //用于单元测试设置线程
    public static boolean isUnitTest = false;

    public static final int EXCEPTION_DATA = -10000;

    // 取得驾驶数据的时间间隔（五分钟->毫秒）
    public static final int TIMER_MILLISECOND = 3 * 60 * 1000;
//    public static final int TIMER_MILLISECOND = 5 * 1000;
    public static final int TIMER_MINUTE = 5;

    // 折线图显示的做多数据（2小时/5分钟）
    public static final long MAX_SHOW_DRIVER_INFO_COUNT = 2 * 60;

    // 分钟值（30）
    public static final int TIME_MINUTE_30 = 30;

    // 分钟值（59）
    public static final int TIME_MINUTE_59 = 59;

    // DataSet数
    public static final int LINE_CHAR_DATA_SET_COUNT = 2;

    // 折线图X轴两端预留间隔 (5分钟)
    public static final long XAXIS_MARGIN_TIME = 5;

    // 折线图平均时速DataSet的index
    public static final int AVG_SPEED_SET_INDEX = 0;

    // 折线图平均油耗DataSet的index
    public static final int AVG_FUEL_CONSUMER_SET_INDEX = 1;

    // 折现图手指点击的最大有效范围
    public static final float MAX_HIGHLIGHT_DISTANCE = 50f;

    // 折现图X轴字体大小
    public static final float XAXIS_TEXT_SIZE = 26f;

    // 折线图线宽度
    public static final float LINE_WIDTH = 2f;

    // 折线图X轴偏移量
    public static final float XAXIS_Y_OFFSET = 20f;

    // DashedHighlight长度
    public static final float DASHED_HIGHLIGHT_LINE_LENGTH = 10f;

    // DashedHighlight间隔
    public static final float DASHED_HIGHLIGHT_SPACE_LENGTH = 5f;

    // DashedHighlight相位
    public static final float DASHED_HIGHLIGHT_PHASE = 0f;

    // 折线图Label预留高度
    public static final float LABEL_MARGIN_HEIGHT = 5f;

    // “拖动”手势的最小移动距离
    public static final float  DRAG_TRIGGER_DISTANCE = 3f;

    // 平均时速最小值
    public static final double MIN_AVG_SPEED = 0;

    // 平均时速最大值 一汽确认最大值为260 原为327.65
    public static final double MAX_AVG_SPEED = 260;

    // 平均油耗最小值
    public static final double MIN_AVG_FUEL_CONSUMER = 0;

    //　平均油耗最大值
    public static final double MAX_AVG_FUEL_CONSUMER = 49.9;

    // 驾驶评分服务键值
    public static final String DRIVER_SERVICE_KEY = "key";

    // 启动驾驶评分服务
    public static final int DRIVER_SERVICE_START = 1;

    // 关闭驾驶评分服务
    public static final int DRIVER_SERVICE_END = 2;

    public static final int DRIVE_SCORE_100 = 100;
    public static final int DRIVE_SCORE_90 = 90;
    public static final int DRIVE_SCORE_80 = 80;
    public static final int DRIVE_SCORE_70 = 70;
    public static final int DRIVE_SCORE_60 = 60;

    // 广播
    public static final String ACTION_DRIVE_STOP = "android.intent.action.ACTION_DRIVE_STOP";
    public static final String ACTION_DRIVE_START = "android.intent.action.ACTION_DRIVE_START";
    // 全局LOG输出TAG
    public static final String TAG = "QiMing";
    // 请求失败的消息类型
    public enum RequestFailMessage {
        UNKNOWN,            // 未知
        NETWORK_DISCONNECT, // 网络未连接
        NETWORK_BADSIGNAL,  // 网络信号差
        SERVER_ERROR;       // 服务器返回错误

    }

    // 发送到主线程的消息类型
    public static final int MESSAGE_REQUEST_FAILED = 0;
    public static final int MESSAGE_DRIVE_START = 1;
    public static final int MESSAGE_DRIVE_INFO_UPDATE = 2;
    public static final int MESSAGE_DRIVE_TIME_UPDATE = 3;
    public static final int MESSAGE_DRIVE_INFO_LIST_UPDATE = 4;
    public static final int MESSAGE_DRIVE_SCORE_UPDATE = 5;
    public static final int MESSAGE_LOADING_HIDE = 6;

    // 接口请求失败的类型
    public static final int ERROR_CODE_CHECK_AVN = 1;  // getTspToken 出错
    public static final int ERROR_CODE_GET_DRIVE_SNAP = 2; // 获取驾驶快照信息出错
    public static final int ERROR_CODE_GET_LAST_DRIVE_SCORE = 3; // 获取上次行程的评分
    public static final int ERROR_CODE_GET_LAST_WEEK_DRIVE_INFO = 4; // 获取上周的驾驶信息
    public static final int ERROR_CODE_UPLOAD_DRIVE_SCORE = 5; // 上传驾驶评分 获取金币奖励


    // 记录的是上一次驾驶的时间是当时月份的第几周
    public static final String WEEK_LAST_DRIVE = "week_last_drive";
    // 最大保存的历史记录条数
    public static final int MAX_HISTORY_RECORD = 40;
    // 行程结束的20分钟缓冲时间
    public static final int TRAVEL_COMPLETE_BUFFER_TIME = 5 * 60 * 1000;
    // TODO: 2019/08/02 为方便测试 时间间隔暂修改为1分钟 后期还原
//    public static final int TRAVEL_COMPLETE_BUFFER_TIME = 30 * 1000;
    // 每次行程开始记录 开始的时间 当行程结束则置0 可以值==0为依据 判断行程是否正常结束
    public static final String LATEST_TRAVEL_START_TIME = "lasted_travel_start_time";
    // 本地记录的行程开始的时间(点火时间) 和 tsp 记录的行程开始时间的差
    public static final Long TRAVEL_START_LEAD_TIME = 5 * 60 * 1000L;
    // 有效行程的行驶里数 只保存有效行程
    public static final Double VALIDE_TRAVEL_DESTANCE = 2.0;

    public static final String TRAVEL_STATUS = "travel_status"; // 行程状态 开始 结束
    public static final String ENGINE_STATUS = "engine_status"; // 引擎状态 点火 熄火 默认
    public static final int DEFAULT_ENGINE_STATUS = -1; // 默认状态
    public static final int STATUS_LAUNCHED = 1;  // 点火
    public static final int STATUS_SHUT_DOWN = 0; // 熄火
}
