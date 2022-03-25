package com.xiaoma.update.common;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.update.api.AppUpdateAPI;
import com.xiaoma.update.model.ApkVersionInfo;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Thomas on 2018/10/18 0018
 */

public class RequestManager {

    private RequestManager() {

    }

    public static void checkAppUpdate(Map urlMaps, ResultCallback<XMResult<ApkVersionInfo>> callback) {
        request(AppUpdateAPI.GET_LASTED_VERSION, urlMaps, callback);
    }

    private static <Bean> void request(String url, Map urlMaps, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp.getDefault().getString(url, urlMaps, new StringCallback() {
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
                callback.onFailure(-1, response.getException().getMessage());
            }
        });
    }

}
