package com.xiaoma.service.common.constant;

import com.xiaoma.service.R;

/**
 * Created by Thomas on 2018/11/5 0005
 */

public class ServiceConstants {
    //保养即将到期默认天数
    public static final int TIME_CAUTION_STATUS = 20;
    //保养即将到期默认公里数
    public static final int DISTANCE_CAUTION_STATUS = 100;

    public static final String OPEN_ACTIVITY_CARRING_DATA = "open_activity";
    public static final String RESERVATION_MAINTENANCE = "预约保养";
    public static final String RESERVATION_SERVICE = "预约维修";
    public static final String RESERVATION_LACQUER = "预约喷漆";

    public static final String ORDER_RECEIVED = "已提交";
    public static final String ORDER_COMPLETED = "已确认";
    public static final String ORDER_CANCEL = "已取消";
    public static int SERVICE_PORT = 1090;
    public static final String PACKAGE_NAME = "com.xiaoma.service";

    public static final String RING_PATH = "android.resource://com.xiaoma.service/" + R.raw.call_ring;
    public static final String IBCALL_SERVICE = "com.xiaoma.service.common.service.TboxCallWindowService";
    public static final String BLUETOOTH_CALL = "bluetooth_call";
    public static final String INCOMING_CALL = "incoming_call";
    public static final String WHEEL_HANGUP_IBCALL = "wheel_hangup_ibcall";
}
