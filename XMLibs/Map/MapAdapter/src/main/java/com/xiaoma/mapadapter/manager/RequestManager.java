package com.xiaoma.mapadapter.manager;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.utils.ZipUtils;
import com.xiaoma.utils.log.KLog;

import java.io.IOException;
import java.util.HashMap;


public class RequestManager {

    private static final String ADDRESS_UPLOAD_SIMPLE = "address/uploadSimple.action";
    private static final String COMPRESS_COMMON = "compress/common.action";

    public static RequestManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final RequestManager instance = new RequestManager();
    }

    private String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getLog();
    }

    public void uploadUserLocation(double lat, double lon, String province, String city,
                                   float speed, float acc, float bearing, String district,
                                   int satellites, int gpsAccuracyStatus, int locationType,
                                   double altitude, StringCallback callback) {
        String url = getPreUrl() + ADDRESS_UPLOAD_SIMPLE;
        HashMap<String, Object> params = new HashMap<>();
        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));
        params.put("p", province);
        params.put("c", city);
        params.put("s", String.valueOf(speed));
        params.put("ac", String.valueOf(acc));
        params.put("be", String.valueOf(bearing));
        params.put("dis", district);
        params.put("gac", String.valueOf(gpsAccuracyStatus));
        params.put("sa", String.valueOf(satellites));
        params.put("lt", String.valueOf(locationType));
        params.put("al", String.valueOf(altitude));
        XmHttp.getDefault().getString(url, params, callback);
    }

    //type  位置上报  1； 操作日志上报 2； 流量统计压缩批量上报 3；
    public void batchUploadCompressStr(String data, int type, StringCallback callback) {
        String url = getPreUrl() + COMPRESS_COMMON;
        String compress;
        boolean compressed;
        try {
            compress = ZipUtils.compress(data);
            compressed = true;
        } catch (IOException e) {
            e.printStackTrace();
            compress = data;
            compressed = false;
        }
        if (compress.length() >= data.length()) {
            compress = data;
            compressed = false;
        }
        KLog.d("batchUploadCompressStr ,compressed = " + String.valueOf(compressed) + ", data len = " + data.length() + ", compress len = " + compress.length() + ", data = " + data);
        HashMap<String, Object> params = new HashMap<>();
        params.put("logData", compress);
        params.put("type", String.valueOf(type));
        params.put("compressed", String.valueOf(compressed));
        XmHttp.getDefault().getString(url, params, callback);
    }

}
