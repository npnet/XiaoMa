package com.xiaoma.xting.common;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.Xting;
import com.xiaoma.xting.assistant.model.AssistantAlbum;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.launcher.model.LauncherAlbum;
import com.xiaoma.xting.welcome.model.PreferenceBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/15
 *     desc   :
 * </pre>
 */
public class RequestManager {

    private RequestManager() {
    }

    private static String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getBusiness();
    }

    public static void requestFMPreferenceTags(ResultCallback<XMResult<List<PreferenceBean>>> callback) {
        String url = getPreUrl() + XtingConstants.URL_SUFFIX.PREFERENCE_QUERY_TAGS;
        request(url, null, callback);
    }

    public static void uploadFMPreferenceTags(String tagIds, ResultCallback<XMResult<String>> callback) {
        String url = getPreUrl() + XtingConstants.URL_SUFFIX.PREFERENCE_UPDATE_TAGS;
        Map<String, Object> params = new HashMap<>();
        params.put("tagIds", tagIds);
        request(url, params, callback);
    }

    public static void requestAudioList(String categoryId, int page, final ResultCallback<XMResult<LauncherAlbum>> callback) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_XM);
        String url = getPreUrl() + XtingConstants.URL_SUFFIX.AUDIO_LIST;
        LocationInfo location = LocationManager.getInstance().getCurrentLocation();
        String lat = null;
        String lng = null;
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lon", lng);
        params.put("audioId", categoryId);
        params.put("page", page);
        //暂时设定为100首 全量拉取
        params.put("size", 100);
        request(url, params, callback);
    }

    public static void requestRecommendList(int page, final ResultCallback<XMResult<AssistantAlbum>> callback) {
//        OnlineFetchInfoRepository.newSingleton().updateCurDataSource(AudioConstants.OnlineInfoSource.LAUNCHER);
//        OnFMInfoChangeManager.newSingleton().onAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_XM);
        String url = getPreUrl() + XtingConstants.URL_SUFFIX.ADUIO_RECOMMEND;
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", 20);
        request(url, params, callback);

    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtils.isConnected(Xting.getInstance().getApplication())) {
            callback.onFailure(-1, "error");
            return;
        }
        XmHttp xmHttp = XmHttp.getDefault();
        xmHttp.setRetryCount(0);
        xmHttp.getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null || !result.isSuccess()) {
                    callback.onFailure(-1, "result parse failure");
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(-1, "error");
                super.onError(response);
            }
        });
    }
}
