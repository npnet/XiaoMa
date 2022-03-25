package com.xiaoma.process.constants;

public class XMApiConstants {

    public static final String SERVICE = "com.xiaoma.service";
    public static final String CLUB = "com.xiaoma.club";
    public static final String XTING = "com.xiaoma.xting";
    public static final String FACTORY = "com.xiaoma.factoryactivities";
    public static final String LAUNCHER = "com.xiaoma.launcher";
    public static final String SMART_SERVICE = "com.xiaoma.smart.service";
    public static final String SETTING = "com.xiaoma.setting";
    public static final String BLUETOOTH_PHONE = "com.xiaoma.bluetooth.phone";
    public static final String SERVICE_STATUS_SERVICE_CONNECT_ACTION = "com.xiaoma.service.aidl.status";
    public static final String CLUB_STATUS_SERVICE_CONNECT_ACTION = "com.xiaoma.club.aidl.status";
    public static final String FACTORY_STATUS_SERVICE_CONNECT_ACTION = "com.xiaoma.factoryactivities.aidl.status";
    public static final String PLAY_SERVICE_CONNECT_ACTION = "com.xiaoma.xting.service";
    public static final String SMART_SERVICE_CONNECT_ACTION = "com.xiaoma.smart.service";
    public static final String VOICE_SERVICE_CONNECT_ACTION = "com.xiaoma.aidl.voice";
    public static final String WECHAT_SERVICE_CONNECT_ACTION = "com.xiaoma.aidl.wechat";
    public static final String SETTING_SERVICE_CONNECT_ACTION = "com.xiaoma.setting.service";
    public static final String BLUETOOTH_PHONE_SERVICE_CONNECT_ACTION = "com.xiaoma.bluetooth.phone.service";

    /*****************************************车智慧 START********************************************/
    public static class NormalStatus {
        public final static int OPEN = 0;
        public final static int CLOSE = -1;
    }

    public static class AirConditionerStatus {
        public final static int AC_CLOSE = NormalStatus.CLOSE;//空调关闭
        public final static int AC_OPEN = 0;//空调打开
        public final static int AC_AUTO = 1;//auto打开，auto打开则ac也为打开状态
        public final static int AC_DUAL = 2;//dual打开，dual打开则ac也为打开状态
    }

    public static class WindSpeed {
        public final static int LEVLE1 = 0;
        public final static int LEVLE2 = 1;
        public final static int LEVLE3 = 2;
        public final static int LEVLE4 = 3;
        public final static int LEVLE5 = 4;
        public final static int LEVLE6 = 5;
        public final static int LEVLE7 = 6;
        public final static int LEVLE8 = 7;
        public final static int LEVLE9 = 8;
        public final static int LEVLE10 = 9;
        public final static int LEVLE11 = 10;
        public final static int LEVLE12 = 11;
        public final static int CLOSE = NormalStatus.CLOSE;//关闭
    }

    public static class SeatWarm {
        public final static int LOW = 0;//低档
        public final static int MIDDLE = 1;//中档
        public final static int HIGH = 2;//高档
        public final static int CLOSE = NormalStatus.CLOSE;//空挡
    }

    public static class WindModel {
        public final static int FOOT = 0;//脚部
        public final static int BODY = 1;//上身
        public final static int FOOT_BODY = 2;//脚部和上身
        public final static int BODY_WINDOW = 3;//上身和挡风窗
    }


    public static class LooperModel {
        public final static int INNER_LOOPER = 0;//内循环
        public final static int OUTPUT_LOOPER = 1;//外循环,默认为外循环
    }

    /*****************************************车智慧 END********************************************/

    public static class ContansValueCmdId {
        public final static String VOICE_CALL_CMD_ID = "VOICE_CALL_CMD_ID";
        public final static String FM_CMD_ID = "FM_CMD_ID";
    }

}
