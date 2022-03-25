package com.xiaoma.carpark.common.manager;

import com.xiaoma.carpark.common.api.CarParkApi;
import com.xiaoma.carpark.main.model.XMPluginInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 2018/11/5 0005
 * api manager
 */

public class RequestManager {
    private RequestManager() {
    }

    private static class InstanceHolder {
        static final RequestManager sInstance = new RequestManager();
    }

    public static RequestManager getInstance() {
        return InstanceHolder.sInstance;
    }

    /**
     * 获取XMPluginInfo
     *
     * @param callback
     */
    public void getXMPluginInfos(int type, ResultCallback<XMResult<List<XMPluginInfo>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        request(CarParkApi.GET_GAME_LIST, params, callback);
    }

    private <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }
                if (!result.isSuccess()) {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(response.code(), response.getException().getMessage());
            }
        });
    }
}
