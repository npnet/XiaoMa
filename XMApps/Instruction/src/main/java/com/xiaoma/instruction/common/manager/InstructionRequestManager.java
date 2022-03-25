package com.xiaoma.instruction.common.manager;


import com.xiaoma.instruction.api.InstructionAPI;
import com.xiaoma.instruction.mode.ManualBean;
import com.xiaoma.instruction.mode.ManualItemBean;
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

public class InstructionRequestManager {

    private InstructionRequestManager() {
    }

    private static class InstanceHolder {
        static final InstructionRequestManager sInstance = new InstructionRequestManager();
    }

    public static InstructionRequestManager getInstance() {
        return InstanceHolder.sInstance;
    }

    /**
     * 用户手册应用列表接口
     *
     * @param callback
     */
    public void getManualInfo(ResultCallback<XMResult<List<ManualBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        request(InstructionAPI.GET_MANUAL_LIST, params, callback);
    }

    /**
     * 用户手册应用详情接口
     *
     * @param callback
     */
    public void getManualItemInfo(String id , ResultCallback<XMResult<List<ManualItemBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        request(InstructionAPI.GET_MANUAL_ITEM_LIST, params, callback);
    }
    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().postString(url, params, new StringCallback() {
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
