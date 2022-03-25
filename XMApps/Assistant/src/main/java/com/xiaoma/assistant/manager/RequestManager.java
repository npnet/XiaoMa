package com.xiaoma.assistant.manager;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.assistant.BuildConfig;
import com.xiaoma.assistant.model.semantic.OpenSemantic;
import com.xiaoma.assistant.model.result.SpecialResult;
import com.xiaoma.assistant.model.result.SpecialResultCallback;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.pratice.NewsBean;
import com.xiaoma.model.pratice.ProvinceBean;
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
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/5
 * Desc：网络请求
 */
public class RequestManager {

    public interface URL_SUFFIX {
        String Xm_nlu = "nlu/v2.0/parser.action";
        String Search_oil = "query/oilpriceV3";
        String Fetch_Litmit = "query/trafficControlV2.action";
        String Fetch_news = "query/newsList.action";
        String Fetch_nearby_park = "query/nearPark.action";
        String Query_weather = "/app/weatherVoiceV3.action";
        String Search_constellation = "/query/HoroscopeFortuneV3.action";
        String Search_flight = "/query/flightTrackByLineV2.action";
        String Search_traffic_info = "/query/trafficStatusV2.action";
        String Search_flow = "/simcard/leftInfo.action";
        String Search_train = "/query/railwayTicketByLineV2.action";
        String Search_train_by_name = "query/railwayTicketByNumber.action";
        String Get_translate = "query/translate";

        //搜索美食、景点、停车场
        String SearchStoreOrParking = "besturnGoing/voiceSearchStoreOrParking";

        //搜索电影、影院
        String QueryFilm = "/voiceQueryFilm";

        //搜索酒店
        String SearchHotel = "/hotel/searchNearBy.action";

        //获取前置拦截语义列表
        String GetOpenSemanticsList = "getOpenSemanticsList";

        String ACTION_HOLO_MAN_INFO = "holo/getAllSkillAndCloseByHoloId";
    }

    private interface VrPractisceApis {
        //获取城市列表
        String GET_PROVINCE_AND_CITY = RequestManager.newSingleton().getPreUrl() + "chinaRegion/getAllRegion";
        //获取新闻列表
        String GET_NEWS_CHANNEL = RequestManager.newSingleton().getPreUrl() + "query/newsChannel.action";
    }

    private static final String TYPE_DELICACY_OR_FEATURE_SPOT = "1";
    private static final String TYPE_PARKING_LOT = "2";
    private static final String TYPE_MICHELIN_RESTAURANT = "3";

    private RequestManager() {
    }

    public static RequestManager newSingleton() {
        return Holder.sINSTANCE;
    }

    interface Holder {
        RequestManager sINSTANCE = new RequestManager();
    }

    private String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getBusiness();
    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }
        AssistantManager.getInstance().showLoadingView(true);
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
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
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                callback.onFailure(-1, response.getException().getMessage());
            }
        });
    }

    private static <Bean> void post(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        post(url, params, callback, true);
    }

    private static <Bean> void post(String url, Map params, final ResultCallback<XMResult<Bean>> callback, boolean showLoadingView) {
        if (callback == null) {
            return;
        }
        if (showLoadingView) {
            AssistantManager.getInstance().showLoadingView(true);
        }
        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (showLoadingView) {
                    AssistantManager.getInstance().showLoadingView(false);
                }
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
                if (showLoadingView) {
                    AssistantManager.getInstance().showLoadingView(false);
                }
                super.onError(response);
                callback.onFailure(-1, response.getException().getMessage());
            }
        });
    }

    public void xmNlu(String voiceText, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Xm_nlu;
        Map<String, Object> params = new HashMap<>();
        params.put("text", voiceText);
        XmHttp.getDefault().getString(url, params, callback);
    }


    public void queryOil(String cityName, String when, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_oil;
        Map<String, Object> params = new HashMap<>();
        params.put("cityName", cityName);
        params.put("when", when);
        XmHttp.getDefault().getString(url, params, callback);
    }


    public void fetchLimit(String city, int type, String date, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Fetch_Litmit;
        Map<String, Object> params = new HashMap<>();
        params.put("city", city);
        params.put("type", type);
        params.put("date", date);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void fetchNewsListInfo(String channelName, String title, int page, int size, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Fetch_news;
        HashMap<String, Object> params = new HashMap<>();
        params.put("channelName", channelName);
        params.put("page", page);
        params.put("title", title);
        params.put("maxResult", size);
        XmHttp.getDefault().getString(url, params, callback);
    }


    public void fetchNearbyPark(double lat, double lon, String city, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Fetch_nearby_park;
        HashMap<String, Object> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("city", city);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void queryCityWeather(String city, String date, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Query_weather;
        Map<String, Object> params = new HashMap<>();
        params.put("city", city);
        params.put("date", date);
        XmHttp.getDefault().getString(url, params, callback);
    }


    public void searchConstellation(String userId, String horoscope, String type, String dateTime, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_constellation;
        Map<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        params.put("horoscope", horoscope);
        params.put("type", type);
        params.put("dateTime", dateTime);
        XmHttp.getDefault().getString(url, params, callback);
    }


    public void searchFlight(String startCity, String endCity, String date, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_flight;
        Map<String, Object> params = new HashMap<>();
        params.put("start", startCity);
        params.put("end", endCity);
        params.put("date", date);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void queryFlow(StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_flow;
        Map<String, Object> params = new HashMap<>();
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void requestHoloManInfo(long holoId, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.ACTION_HOLO_MAN_INFO;
        HashMap<String, Object> params = new HashMap<>();
        params.put("holoId", holoId);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void fetchTrafficInfo(String city, String street, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_traffic_info;
        Map<String, Object> params = new HashMap<>();
        params.put("city", city);
        params.put("road", street);
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void searchTrain(String start, String end, String date, String category, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_train;
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("date", date);
        if (!TextUtils.isEmpty(category)) {
            params.put("trainType", category);
        }
        XmHttp.getDefault().getString(url, params, callback);
    }

    public void searchTrainByName(String name, String start, String end, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Search_train_by_name;
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("start", start);
        params.put("end", end);
        XmHttp.getDefault().getString(url, params, callback);
    }


    public void getTranslate(String from, String to, String text, StringCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.Get_translate;
        Map<String, Object> params = new HashMap<>();
        params.put("from", from);
        params.put("to", to);
        params.put("text", text);
        XmHttp.getDefault().getString(url, params, callback);
    }

    /**
     * 搜索美食、景点、停车场
     */
    public <T> void searchStoreOrParking(String poi, String address, String content, String type, ResultCallback<XMResult<List<T>>> callback) {
        String url = getPreUrl() + URL_SUFFIX.SearchStoreOrParking;
        Map<String, Object> params = new HashMap<>();
        if (BuildConfig.DEBUG) {
            params.put("poi", !TextUtils.isEmpty(poi) ? poi : LocationManager.DebugLocation.location);
        } else {
            params.put("poi", poi);
        }
        params.put("address", !TextUtils.isEmpty(address) ? address : "");
        params.put("content", !TextUtils.isEmpty(content) ? content : "");
        params.put("type", type);
        request(url, params, callback);
    }

    public <T> void searchStoreOrFeatureSpot(String poi, String address, String content, ResultCallback<XMResult<List<T>>> callback) {
        searchStoreOrParking(poi, address, content, TYPE_DELICACY_OR_FEATURE_SPOT, callback);
    }

    public <T> void searchParking(String poi, String address, String content, ResultCallback<XMResult<List<T>>> callback) {
        searchStoreOrParking(poi, address, content, TYPE_PARKING_LOT, callback);
    }

    public <T> void searchMichelinRestaurant(String poi, String address, String content, ResultCallback<XMResult<List<T>>> callback) {
        searchStoreOrParking(poi, address, content, TYPE_MICHELIN_RESTAURANT, callback);
    }

    /**
     * 搜索影院或电影
     */
    public void queryFilm(String city, String countyName, String cinemaName, int pageNum, int pageSize, String filmName, String dataStyle, String lat, String lon, SpecialResultCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.QueryFilm;
        Map<String, Object> params = new HashMap<>();
        params.put("countyName", !TextUtils.isEmpty(countyName) ? countyName : "");
        params.put("cinemaName", !TextUtils.isEmpty(cinemaName) ? cinemaName : "");
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("filmName", !TextUtils.isEmpty(filmName) ? filmName : "");
        params.put("dataStyle", !TextUtils.isEmpty(dataStyle) ? dataStyle : "");
        if (BuildConfig.DEBUG) {
            params.put("lat", !TextUtils.isEmpty(lat) ? lat : LocationManager.DebugLocation.lat);
            params.put("lon", !TextUtils.isEmpty(lon) ? lon : LocationManager.DebugLocation.lon);
            params.put("city", !TextUtils.isEmpty(city) ? city : LocationManager.DebugLocation.city);
        } else {
            params.put("lat", lat);
            params.put("lon", lon);
            params.put("city", city);
        }
        postSpecical(url, params, callback);
    }

    /**
     * 搜索酒店
     */
    public void searchHotel(String star, String hotelName, String city, int pageNo, int pageSize, String checkIn, String checkOut, String sortCode, String sortType, String lat, String lon, String distance, String price, final SpecialResultCallback callback) {
        String url = getPreUrl() + URL_SUFFIX.SearchHotel;
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);
        params.put("star", star);
        params.put("hotelName", hotelName);
        Log.d("QBX", "searchHotel: " + hotelName);
        params.put("sortCode", !TextUtils.isEmpty(sortCode) ? sortCode : "");
        params.put("sortType", !TextUtils.isEmpty(sortType) ? sortType : "");
        params.put("distance", !TextUtils.isEmpty(distance) ? distance : "");
        params.put("price", !TextUtils.isEmpty(price) ? price : "");
        if (BuildConfig.DEBUG) {
            params.put("lat", !TextUtils.isEmpty(lat) ? lat : LocationManager.DebugLocation.lat);
            params.put("lon", !TextUtils.isEmpty(lon) ? lon : LocationManager.DebugLocation.lon);
            params.put("city", !TextUtils.isEmpty(city) ? city : LocationManager.DebugLocation.city);
        } else {
            params.put("lat", lat);
            params.put("lon", lon);
            params.put("city", city);
        }
        postSpecical(url, params, callback);
    }

    public void getOpenSemanticsList(ResultCallback<XMResult<OpenSemantic>> callback) {
        String url = getPreUrl() + URL_SUFFIX.GetOpenSemanticsList;
        Map<String, Object> params = new HashMap<>();
        params.put("channelId", BuildConfig.CAR_CHANNEL_ID);
        post(url, params, callback, false);
    }

    //------------vrpractice start----------

    /**
     * 获取城市列表
     *
     * @param callback
     */
    public void getProvinceAndCity(ResultCallback<XMResult<List<ProvinceBean>>> callback) {
        request(VrPractisceApis.GET_PROVINCE_AND_CITY, null, callback);
    }


    /**
     * 获取新闻频道列表
     *
     * @param callback
     */
    public void getNewsChannel(ResultCallback<XMResult<NewsBean>> callback) {
        request(VrPractisceApis.GET_NEWS_CHANNEL, null, callback);
    }


    //------------vrpractice end----------

    private static void postSpecical(String url, Map params, final SpecialResultCallback callback) {
        if (callback == null) {
            return;
        }
        AssistantManager.getInstance().showLoadingView(true);
        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                SpecialResult specialResult = GsonHelper.fromJson(data, type);
                if (specialResult == null) {
                    callback.onFailure(-1, "result parse failure");
                    return;
                }
                if (!specialResult.isSuccess() || specialResult.isEmptyData()) {
                    callback.onFailure(Integer.parseInt(specialResult.resultCode), "result parse failure");
                    return;
                }
                callback.onSuccess(specialResult);
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                callback.onFailure(-1, response.getException().getMessage());
            }
        });
    }
}
