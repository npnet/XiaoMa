package com.qiming.fawcard.synthesize.base.util;

import com.xiaoma.utils.log.KLog;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Administrator on 2018/5/4.
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private StringBuilder mMessage = new StringBuilder();

    @Override
    public void log(String message) {
        // 请求或者响应开始
        if (message.startsWith("--> POST")||message.startsWith("--> GET")) {
            mMessage.setLength(0);
        }

        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}")) || (message.startsWith("[") &&
                message.endsWith("]"))) {
            mMessage.append(message.concat("\n\n"));//打印格式化之前的整条json
            message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
        }
        mMessage.append(message.concat("\n"));
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            KLog.d(mMessage.toString());
        }
    }
}
