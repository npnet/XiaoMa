package com.xiaoma.music.common.manager;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.kuwo.model.VIPOrderModel;
import com.xiaoma.music.kuwo.model.VIPOrderStateModel;
import com.xiaoma.music.mine.model.OrderStatus;
import com.xiaoma.music.mine.model.PayInfo;
import com.xiaoma.music.mine.model.VipListBean;
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
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class RequestManager {

    private static final String TEST_ENV = "http://111.230.137.157:18082/rest/";

    public static RequestManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final RequestManager instance = new RequestManager();
    }

    private String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getBusiness();
    }

    /**
     * 请求后台配置的所有的音乐偏好标签
     *
     * @param callback
     */
    public void getAllMusicPreference(StringCallback callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.GET_ALL_PREFERENCE;
        XmHttp.getDefault().getString(url, null, callback);
    }

    /**
     * 向后台添加用户选择的标签
     *
     * @param tagIds   "1,2,3"
     * @param callback
     */
    public void addUserMusicPreference(String tagIds, StringCallback callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.ADD_USER_PREFERENCE;
        Map<String, Object> params = new HashMap<>();
        params.put("tagIds", tagIds);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void getLauncherMusicData(String categoryId, int page, StringCallback callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.SEARCH_LAUNCHER_MUSIC_LIST;
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
        XmHttp xmHttp = XmHttp.getDefault();
        xmHttp.setRetryCount(0);
        xmHttp.getString(url, params, callback);
    }


    //1 酷我排行榜歌单
    // 2酷我标签歌单
    // 3 抗疲劳歌单
    public void searchMusicByKeyWord(int type, String keyWord, int page, int pageSize, StringCallback callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.SEARCH_MUSIC_BY_KEY;
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        if (keyWord != null) {
            params.put("keyword", keyWord);
        }
        params.put("page", page);
        params.put("size", pageSize);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void requestMusicTagById(long id, String name, StringCallback callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.REQUEST_MUSIC_TAG_BY_ID;
        Map<String, Object> params = new HashMap<>();
        params.put("songId", id);
        params.put("songName", name);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 获取音乐搜索列表
     *
     * @param name
     * @param callback
     */
    public void getMusicList(String name, ResultCallback<XMResult<List<PlayMusicBean>>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        request(getPreUrl() + MusicConstants.VPConstants.GET_MUSIC_LIST, map, callback);
    }

    /**
     * 获取VIP价格列表
     *
     * @param callback
     */
    public void getKWVIPPriceList(ResultCallback<XMResult<VipListBean>> callback) {
        Map<String, Object> map = new HashMap<>();
        String url = getPreUrl() + MusicConstants.NetWorkConstants.REQUEST_KWVIP_PRICE_LIST;
        request(url, map, callback);
    }

    /**
     * 购买酷我VIP创建订单
     *
     * @param callback
     */
    public void createKWVIPOrder(VIPOrderModel model, ResultCallback<XMResult<PayInfo>> callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.REQUEST_BUY_KWVIP_ORDER;
        request(url, model.getRequestParams(), callback);
    }

    /**
     * 轮询购买酷我VIP订单支付状态
     *
     * @param callback
     */
    public void checkKWVIPOrderState(VIPOrderStateModel model, ResultCallback<XMResult<OrderStatus>> callback) {
        String url = getPreUrl() + MusicConstants.NetWorkConstants.REQUEST_CHECK_KWVIP_ORDER_STATE;
        request(url, model.getRequestParams(), callback);
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
