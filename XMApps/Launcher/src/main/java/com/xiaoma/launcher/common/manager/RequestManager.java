package com.xiaoma.launcher.common.manager;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.app.model.WhiteListBean;
import com.xiaoma.launcher.common.api.LauncherAPI;
import com.xiaoma.launcher.common.model.Weather;
import com.xiaoma.launcher.message.wechat.model.QrCodeBean;
import com.xiaoma.launcher.player.model.AudioInfoBean;
import com.xiaoma.launcher.recmusic.model.MusicRecord;
import com.xiaoma.launcher.recommend.manager.NotificationDataParseHandler;
import com.xiaoma.launcher.schedule.model.PhoneScheduleInfo;
import com.xiaoma.launcher.schedule.model.UploadScheduleResult;
import com.xiaoma.launcher.service.model.NewServiceBean;
import com.xiaoma.model.ModelConstants;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.mqtt.model.MqttInfo;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 2018/12/29 0029
 * net manager
 */

public class RequestManager {
    private static final String TAG = "[RequestManager]";
    private static final String GET_MSG_LIST_TAG = "get_msg_list_tag";
    private static final String NOTIFY_ID_LIST_TAG = "notify_id_list_tag";

    private RequestManager() {
    }

    private static class InstanceHolder {
        static final RequestManager sInstance = new RequestManager();
    }

    public static RequestManager getInstance() {
        return InstanceHolder.sInstance;
    }

    /**
     * 检查微信绑定状态
     */
    public void fetchUserBindStatus(ResultCallback<XMResult<String>> callback) {
        request(LauncherAPI.CHECK_USER_BIND, null, callback);
    }

    /**
     * 获得微信绑定二维码
     */
    public void fetchBindQRCode(ResultCallback<XMResult<QrCodeBean>> callback) {
        request(LauncherAPI.GET_BIND_QR_CODE, null, callback);
    }

    /**
     * 解除微信绑定
     */
    public void fetchUnBind(ResultCallback<XMResult<String>> callback) {
        request(LauncherAPI.UNBIND_DEVCIE, null, callback);
    }

    /**
     * 获取应用白名单
     *
     * @param callback
     */
    public void getWhiteApp(ResultCallback<XMResult<WhiteListBean>> callback) {
        request(LauncherAPI.GET_WHITE_APP, null, callback);
    }

    public void getWhiteApp(StringCallback callback) {
        XmHttp.getDefault().getString(LauncherAPI.GET_WHITE_APP, null, callback);
    }

    public void pushMessageToPhone(String uid, String lon, String lat, String poi, String location, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("uid", uid);
        params.put("lon", lon);
        params.put("lat", lat);
        params.put("poi", poi);
        params.put("location", location);
        request(LauncherAPI.SEND_NAVIGATE_MSG_TO_MOBILE, params, callback);

    }

    public void pushServiceList(ResultCallback<XMResult<List<NewServiceBean>>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        KLog.e(TAG, "pushServiceList() url=" + LauncherAPI.SERVICE_LIST_QUERY + " , params=" + params);
        request(LauncherAPI.SERVICE_LIST_QUERY, params, callback);
    }


    public void getRecMusicImgAndSongId(String name, String singer, ResultCallback<XMResult<MusicRecord>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("name", name);
        params.put("singer", singer);
        request(LauncherAPI.SEARCH_BY_NAME_AND_SINGER, params, callback);
    }

    /**
     * 获取桌面音频数据
     *
     * @param callback
     */
    public void getLauncherAudioList(ResultCallback<XMResult<AudioInfoBean>> callback) {
        request(LauncherAPI.GET_LAUNCHER_AUDIO, null, callback);
    }

    /**
     * 上传日程
     *
     * @param createDate
     * @param remindDate
     * @param remindBeginTime
     * @param remindEndTime
     * @param content
     * @param callback
     */
    public void postUploadSch(long createDate, String remindDate, String remindBeginTime, String
            remindEndTime, String content, ResultCallback<XMResult<UploadScheduleResult>> callback) {
        Map<String, String> params = new ArrayMap<>();
        params.put("createDate", String.valueOf(createDate));
        params.put("remindDate", remindDate);
        params.put("remindBeginTime", remindBeginTime);
        params.put("remindEndTime", remindEndTime);
        params.put("content", content);
        request(LauncherAPI.POST_UPLOAD_SCH, params, callback);
    }

    public void postDelSch(String id, ResultCallback<XMResult<String>> callback) {
        Map<String, String> params = new ArrayMap<>();
        params.put("id", id);
        request(LauncherAPI.POST_DEL_SCH, params, callback);
    }

    public void fetchPhoneSchs(ResultCallback<XMResult<List<PhoneScheduleInfo>>> callback) {
        request(LauncherAPI.POST_FETCH_PHONE_ALLSCH, null, callback);
    }

    /**
     * 获取照片状态
     *
     * @param callback
     */
    public void getPhotoType(String phontId, ResultCallback<XMResult<Boolean>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("photoId", phontId);
        request(LauncherAPI.GET_PHOTO_UPLOAD_STATUS, params, callback);
    }

    /**
     * 获取消息列表
     */
    public void getMsgList(final Context context) {
        //一天内不重复请求
        String time = TPUtils.get(context, GET_MSG_LIST_TAG, "");
        if (time.equals(StringUtil.getDateByYMD())) {
            return;
        }

        XmHttp.getDefault().getString(LauncherAPI.GET_HEAD_MSG_LIST, null, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String string = response.body();
                try {
                    TPUtils.put(context, GET_MSG_LIST_TAG, StringUtil.getDateByYMD());

                    JSONObject jsonObject = new JSONObject(string);
                    int result = jsonObject.optInt("resultCode");
                    if (result == ModelConstants.ResultCode.RESULT_OK) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray == null) {
                            return;
                        }
                        List<Integer> idList = new ArrayList<>();
                        List<Integer> cacheIdList = TPUtils.getList(context, NOTIFY_ID_LIST_TAG, Integer[].class);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            //推送id
                            Integer id = object.getInt("id");
                            idList.add(id);

                            if (!cacheIdList.contains(id)) {
                                //通知栏提醒
                                NotificationDataParseHandler.getInstance().parserNotificationData(context.getApplicationContext(),
                                        object.toString());
                            }
                        }

                        //缓存推送id List
                        TPUtils.putList(context, NOTIFY_ID_LIST_TAG, idList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCityWeather(String city, WeatherResultCallBack<Weather> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("city", city);
        requestWeather(LauncherAPI.GET_CURRENT_CITY_WEATHER, params, callback);
    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        KLog.e(TAG, "request() url=" + url + " ,params=" + params);
        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                KLog.e(TAG, "request() data= " + data);
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


    private static <Bean> void requestWeather(String url, Map params, final WeatherResultCallBack callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                Bean result = GsonHelper.fromJson(data, type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
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

    public void pushOilConsume(long uid, String channelId, double oilConsumeValue, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("uid", uid);
        params.put("channelId", channelId);
        params.put("oilConsumeValue", oilConsumeValue);
        request(LauncherAPI.POST_OIL_CONSUME, params, callback);
    }

    public void getMqttInfo(ResultCallback<XMResult<MqttInfo>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("channelId", ConfigManager.ApkConfig.getChannelID());
        request(LauncherAPI.GET_MQTT_INFO, params, callback);
    }

    public void getMqttOil(ResultCallback<XMResult<MqttInfo>> callback) {
        Map<String, Object> params = new ArrayMap<>();
        request(LauncherAPI.GET_MQTT_OIL, params, callback);
    }

}
