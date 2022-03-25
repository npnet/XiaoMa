package com.xiaoma.smarthome.common.manager;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.smarthome.common.api.SmartHomeApi;
import com.xiaoma.smarthome.common.model.MiUserInfo;
import com.xiaoma.smarthome.common.model.XiaoMiTtsBean;
import com.xiaoma.smarthome.login.model.CMUserInfo;
import com.xiaoma.smarthome.login.model.CloudMIBean;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taojin
 * @date 2019/2/27
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

    //--------------云米------------------

    /**
     * 获取云米登录二维码
     *
     * @param resultCallback
     */
    public void getCMLoginCode(ResultCallback<XMResult<String>> resultCallback) {
        postRequest(SmartHomeApi.GET_LOGIN_CLOUDMI_CODE, null, resultCallback);
    }

    /**
     * 获取云米用户登录状态
     *
     * @param resultCallback
     */
    public void getCMLoginState(ResultCallback<XMResult<String>> resultCallback) {
        postRequest(SmartHomeApi.QUERY_CM_USER_LOGIN_STATE, null, resultCallback);
    }

    /**
     * 获取云米登陆结果
     *
     * @param resultCallback
     */
    public void queryCMLoginResult(ResultCallback<XMResult<CloudMIBean>> resultCallback) {
        postRequest(SmartHomeApi.QUERY_LOGIN_CLOUDMI_RESULT, null, resultCallback);
    }

    /**
     * 获取云米场景列表
     *
     * @param resultCallback
     */
    public void fetchCMSceneList(ResultCallback<XMResult<List<SceneBean>>> resultCallback) {
        postRequest(SmartHomeApi.GET_CLOUDMI_SCENE, null, resultCallback);
    }

    /**
     * 执行云米场景
     *
     * @param resultCallback
     */
    public void excuteCMScene(String sceneId, ResultCallback<XMResult<String>> resultCallback) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneId", sceneId);
        postRequest(SmartHomeApi.EXCUTE_CLOUDMI_SCENE, params, resultCallback);
    }

    /**
     * 执行云米场景(按名字)
     *
     * @param resultCallback
     */
    public void excuteCMSceneByName(String sceneName, ResultCallback<XMResult<String>> resultCallback) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", sceneName);
        postRequest(SmartHomeApi.EXCUTE_CLOUDMI_SCENE_BY_NAME, params, resultCallback);
    }


    /**
     * 云米退出登录
     *
     * @param resultCallback
     */
    public void logoutCM(ResultCallback<XMResult<String>> resultCallback) {
        postRequest(SmartHomeApi.LOGOUT_CM_USER, null, resultCallback);
    }

    /**
     * 查询用户信息
     *
     * @param resultCallback
     */
    public void fetchUserInfo(ResultCallback<XMResult<CMUserInfo>> resultCallback) {
        postRequest(SmartHomeApi.QUERY_CM_USER_INFO, null, resultCallback);
    }

    /**
     * 更新场景设置条件
     *
     * @param autoConditionBean
     * @param resultCallback
     */
    public void updateSceneCon(SceneBean.AutoConditionBean autoConditionBean, ResultCallback<XMResult<String>> resultCallback) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneId", autoConditionBean.getSceneId());
        params.put("autoFlag", autoConditionBean.getAutoFlag());
        params.put("ruleFlag", autoConditionBean.getRuleFlag());
        params.put("radius", autoConditionBean.getRadius());
        postRequest(SmartHomeApi.UPDATE_CM_SCENE_DATA, params, resultCallback);
    }

    //--------------小米------------------

    /**
     * 检测用户是否绑定小米账户
     *
     * @param callback
     */
    public void checkUserBindXiaomi(ResultCallback<XMResult<String>> callback) {
        getRequest(SmartHomeApi.CHECK_USER_BIND_MI, null, callback);
    }

    /**
     * 小米授权
     *
     * @param callback
     */
    public void postMiAuth(ResultCallback<XMResult<String>> callback) {
        getRequest(SmartHomeApi.SMART_HOME_MI_AUTH, null, callback);
    }

    /**
     * 小米账户信息
     *
     * @param callback
     */
    public void postMiUserInfo(ResultCallback<XMResult<MiUserInfo>> callback) {
        getRequest(SmartHomeApi.SMART_HOME_MI_USER, null, callback);
    }

    /**
     * 退出小米账户
     *
     * @param callback
     */
    public void logOutMi(ResultCallback<XMResult<String>> callback) {
        getRequest(SmartHomeApi.LOGOUT_MI, null, callback);
    }

    /**
     * 小米垂类  执行语句指令
     *
     * @param voiceText
     * @param lat
     * @param lon
     * @param callback
     */
    public void textParserMI(String voiceText, double lat, double lon, ResultCallback<XMResult<XiaoMiTtsBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("text", voiceText);
        params.put("lat", lat);
        params.put("lon", lon);
        postRequest(SmartHomeApi.SMART_HOME_MI_TEXT_PARSER, params, callback);
    }

    private <T> void postRequest(String url, Map<String, Object> params, final ResultCallback<XMResult<T>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                XMResult<T> result = GsonHelper.fromJson(response.body(), type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }
                if (result.isSuccess()) {
                    callback.onSuccess(result);

                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    private <T> void getRequest(String url, Map<String, Object> params, final ResultCallback<XMResult<T>> callback) {
        if (callback == null) {
            return;
        }
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                XMResult<T> result = GsonHelper.fromJson(response.body(), type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }
                if (result.isSuccess()) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }
}
