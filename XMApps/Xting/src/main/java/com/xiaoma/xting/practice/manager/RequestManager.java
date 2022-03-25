package com.xiaoma.xting.practice.manager;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.pratice.RadioBean;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.xting.common.XtingConstants;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 2018/11/9 0009
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
     * 获取电台搜索列表
     *
     * @param title
     * @param callback
     */
    public void getRadioList(String title,ResultCallback<XMResult<RadioBean>> callback){
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        request(XtingConstants.GET_RADIO_LIST,map,callback);
    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
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
