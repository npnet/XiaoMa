package com.xiaoma.pet.common.utils;

import com.xiaoma.pet.common.callback.OnUsedGoodsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/22 0022 18:02
 *   desc:   资源使用注册器
 * </pre>
 */
public final class ResourceUsedRegister {


    private static final String REGISTER_TAG = "REGISTER_TAG";
    private Map<String, OnUsedGoodsCallback> callbackMap = new HashMap<>();


    private ResourceUsedRegister() {
    }

    public static ResourceUsedRegister getInstance() {
        return Holder.RESOURCE_USED_REGISTER;
    }

    public void register(OnUsedGoodsCallback callback) {
        callbackMap.put(REGISTER_TAG, callback);
    }

    public void unregister() {
        callbackMap.clear();
    }

    public OnUsedGoodsCallback getOnUsedGoodsCallback() {
        return callbackMap.get(REGISTER_TAG);
    }

    private static class Holder {
        private static final ResourceUsedRegister RESOURCE_USED_REGISTER = new ResourceUsedRegister();
    }

}
