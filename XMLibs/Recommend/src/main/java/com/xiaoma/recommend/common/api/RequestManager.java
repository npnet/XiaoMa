package com.xiaoma.recommend.common.api;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: iSun
 * @date: 2018/12/5 0005
 */
public class RequestManager {

    public static RequestManager instance;

    public static RequestManager getInstance() {
        if (instance == null) {
            synchronized (RequestManager.class) {
                if (instance == null) {
                    instance = new RequestManager();
                }
            }
        }
        return instance;
    }

    private <Bean> void request(String url, Map params, final ResultCallback<XMResult<?>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null || !result.isSuccess() || result.getData() == null) {
                    callback.onFailure(-1, "result parse failure");
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });
    }

    public void getRecommendList(ResultCallback callback, Long uid, String lat, String lon, String modeType) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("lat", lat);
        map.put("lon", lon);
        map.put("modeType", modeType);
        request(RecommendAPI.GET_RECOMMEND_LIST, map, callback);
    }
}
