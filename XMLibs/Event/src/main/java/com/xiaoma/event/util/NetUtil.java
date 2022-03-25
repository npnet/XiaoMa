package com.xiaoma.event.util;

import com.xiaoma.db.DBManager;
import com.xiaoma.event.EventInfo;
import com.xiaoma.event.EventType;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.ZipUtils;
import com.xiaoma.utils.log.KLog;

import java.io.IOException;

/**
 * @author zs
 * @date 2018/9/14 0014.
 */
public class NetUtil {

    private static final String UPLOAD_UTL = "";
    private static final String BATCH_UPLOAD_UTL = "";

    /**
     * 立即上报。如果上报失败，先暂时存入数据库
     */
    public static void upload(final long userId, final String eventKey, final String data) {
        // TODO: 2018/9/14 0014 params
        XmHttp.getDefault().postString(UPLOAD_UTL, null, "", new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d("onSuccess==>" + response.body());
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                KLog.d("onError===>" + response.body());
                DBManager.getInstance().getDBManager().save(new EventInfo(System.currentTimeMillis(), data, eventKey, userId));
            }
        });
    }

    public static void batchUpload(EventType eventType, String data, StringCallback callback) {
        String compressData = getCompressData(data);
        XmHttp.getDefault().postString(BATCH_UPLOAD_UTL, null, "", callback);
        // TODO: 2018/9/14 0014 params
    }

    private static String getCompressData(String data) {
        String compressData;
        boolean compressed;
        try {
            compressData = ZipUtils.compress(data);
            compressed = true;
        } catch (IOException e) {
            e.printStackTrace();
            compressData = data;
            compressed = false;
        }
        if (compressData.length() >= data.length()) {
            compressData = data;
            compressed = false;
        }
        KLog.d("batchUploadEvent ,compressed = " + compressed + ", data len = " + data.length() + ", compress len = " + compressData.length() + ", " +
                "data" + " = " + data);
        return compressData;
    }
}
