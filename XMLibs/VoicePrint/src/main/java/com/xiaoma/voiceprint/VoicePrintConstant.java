package com.xiaoma.voiceprint;

/**
 * @author KY
 * @date 11/22/2018
 */
public class VoicePrintConstant {

    private VoicePrintConstant() throws Exception {
        throw new Exception();
    }

    public static class TP {
        public static final String TP_NAME = "VoicePrint";
        public static final String GROUP_ID = "group_id";
    }

    public static class ResultCode {
        public static final String VOICE_REGISTER_CLIENT_ID = "client_id";
    }

    public static class RequestCode {
        public static final int VOICE_REGISTER = 1;
    }


    public static class VoicePrintConfig {
        public static final int VOICE_RECORD_COUNT = 3;
        public static final String VOICE_PRINT_HOST = "https://cloud.voiceaitech.com:8072";
        public static final int VOICE_PRINT_PORT = -1;
        public static final String VOICE_PRINT_APP_ID = "3400bbb1885f439785b9bfab0c082acc";
        public static final String VOICE_PRINT_APP_SECRET = "7abbdcb1babd1b96457adc0bfba37cb8";
        public static final String GROUP_NAME = "xmvoice";
        public static final String GROUP_DESC = "666";
        public static final String USER_DESC = "777";
        public static final int SRC_SAMPLE_RATE = 16000;
        public static final String MODE_TYPE = "model_number";
        public static final int LIMIT_COUNT = 10;
    }
}
