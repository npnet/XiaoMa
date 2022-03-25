package com.xiaoma.launcher.recommend.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.launcher.recommend.model.Map;
import com.xiaoma.launcher.recommend.model.RecommendType;
import com.xiaoma.launcher.recommend.view.RecommendDialog;
import com.xiaoma.launcher.travel.car.ui.NearByOilParkActivity;
import com.xiaoma.launcher.travel.delicious.ui.DeliciousActivity;
import com.xiaoma.launcher.travel.film.ui.FilmActivity;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.ui.RecomHotelActivity;
import com.xiaoma.launcher.travel.scenic.ui.AttractionsActivity;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.movie.response.FilmsBean;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

/**
 * @author taojin
 * @date 2019/2/22
 * 推荐管理器
 */

public class RecommendDataParserManager {

    private final static String REC = "rec";
    private final static String TYPE = "type";
    private final static String DATAS = "datas";
    private final static String TITLE = "title";
    private final static String ICON = "icon";
    private final static String RGB = "rgb";
    private final String TAG = getClass().getSimpleName();
    private static RecommendDataParserManager reconmondDataParserManager;
    private Context mContext;
    private RecommendDialog recommendDialog;
    private HashMap<Long, String> mRecommendDataMap = new HashMap<>();

    private Context mActivity;

    private RecommendDataParserManager() {
        mContext = AppHolder.getInstance().getAppContext();
    }

    public static RecommendDataParserManager getInstance() {
        if (reconmondDataParserManager == null) {
            synchronized (RecommendDataParserManager.class) {
                if (reconmondDataParserManager == null) {
                    reconmondDataParserManager = new RecommendDataParserManager();
                }
            }
        }
        return reconmondDataParserManager;
    }

    /**
     * 解析推荐数据
     *
     * @param message
     */
    public void parserRecommendData(Context context, String message) {
        mActivity = context;
        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONArray jsonArray = jsonObject.optJSONArray(REC);
            String title = jsonObject.optString(TITLE);
            String icon = jsonObject.optString(ICON);
            String rgb = jsonObject.optString(RGB);
            Log.d(TAG, "rcdata: " + jsonObject.toString());
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject parmer = jsonArray.optJSONObject(0);
                String type = parmer.optString(TYPE);
                JSONArray array = parmer.optJSONArray(DATAS);
                String arrayStr = array.toString();
                switch (RecommendType.matchRecommendType(type)) {
                    case FILM:
                        doHandleFilmData(arrayStr, title, icon, rgb, type);
                        break;
                    case FOOD:
                        doHandleFoodData(arrayStr, title, icon, rgb, type);
                        break;
                    case PARKING:
                        doHandleParkOilData(array, mContext.getString(R.string.recommend_parking), mContext.getString(R.string.recommend_message_parking), type);
                        break;
                    case RADIO:
                        doHandleRadioData(arrayStr, title, icon, rgb, type);
                        break;
                    case SCENERY:
                        doHandleSceneryData(arrayStr, title, icon, rgb, type);
                        break;
                    case HOTEL:
                        doHandleHotelData(arrayStr, title, icon, rgb, type);
                        break;
                    case MAP:
//                        doHandleMapData(arrayStr, type);
                        break;
                    case MUSIC:
                        doHandleMusicData(arrayStr, title, icon, rgb, type);
                        break;
                    case GAS:
                        doHandleParkOilData(array, mContext.getString(R.string.recommend_oil), mContext.getString(R.string.recommend_message_oil), type);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doHandleFilmData(String arrayStr, String title, String icon, String rgb, String type) {
        final List<FilmsBean> films = GsonHelper.fromJsonToList(arrayStr, FilmsBean[].class);
        KLog.d(TAG, "films" + films.toString());
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                TPUtils.putList(mContext, LauncherConstants.RecommendExtras.RECOMMEND_FILM_LIST, films);
                Intent intent = new Intent(mContext, FilmActivity.class);
                intent.putExtra(LauncherConstants.TravelConstants.FILM_DATA_RECOMMEND_TYPE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        recommendDialog.show();
        recommendDialog.setDialogMessage(title);

        recommendDialog.setDialogType(type);
        recommendDialog.setIvRecommend(icon);
        recommendDialog.setMsgColor(rgb);
    }

    private void doHandleMusicData(String arrayStr, String title, String icon, String rgb, String type) {
        //如果播放器正在播放就不弹窗
        if (PlayerConnectHelper.getInstance().isPlayingState()) {
            return;
        }
//        final List<Music> musics = GsonHelper.fromJsonToList(arrayStr, Music[].class);
//        KLog.d(TAG, "music" + musics.toString());
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                //点击音乐
                EventBus.getDefault().post(LauncherConstants.RECOMMEND_MUSIC, LauncherConstants.RECOMMEND_PLAY);
            }
        });
        recommendDialog.show();
        recommendDialog.setDialogMessage(TextUtils.isEmpty(title) ? mContext.getString(R.string.recommend_message_music) : title);
        recommendDialog.setDialogType(type);
        recommendDialog.setIvRecommend(icon);
        recommendDialog.setMsgColor(rgb);
    }

    private void doHandleMapData(String arrayStr, String type) {
        List<Map> maps = GsonHelper.fromJsonToList(arrayStr, Map[].class);
        KLog.d(TAG, "map" + maps.toString());
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.show();
        recommendDialog.setDialogMessage(mContext.getString(R.string.recommend_message_map));
        recommendDialog.setDialogType(type);
    }

    private void doHandleHotelData(String arrayStr, String title, String icon, String rgb, String type) {
        final List<HotelBean> hotels = GsonHelper.fromJsonToList(arrayStr, HotelBean[].class);
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                TPUtils.putList(mContext, LauncherConstants.RecommendExtras.RECOMMEND_HOTEL_LIST, hotels);
                Intent intent = new Intent(mContext, RecomHotelActivity.class);
                intent.putExtra(HotelConstants.HOTEL_DATA_RECOMMEND_TYPE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        recommendDialog.show();
        recommendDialog.setDialogMessage(title);
        recommendDialog.setDialogType(type);
        recommendDialog.setIvRecommend(icon);
        recommendDialog.setMsgColor(rgb);
    }

    private void doHandleSceneryData(String arrayStr, String title, String icon, String rgb, String type) {
        final List<SearchStoreBean> sceneries = GsonHelper.fromJsonToList(arrayStr, SearchStoreBean[].class);
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                TPUtils.putList(mContext, LauncherConstants.RecommendExtras.RECOMMEND_ATTRACTION_LIST, sceneries);
                Intent intent = new Intent(mContext, AttractionsActivity.class);
                intent.putExtra(LauncherConstants.TravelConstants.ATTRACTION_DATA_RECOMMEND_TYPE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        recommendDialog.show();
        recommendDialog.setDialogMessage(title);
        recommendDialog.setDialogType(type);

        recommendDialog.setIvRecommend(icon);
        recommendDialog.setMsgColor(rgb);
    }

    private void doHandleRadioData(String arrayStr, String title, String icon, String rgb, String type) {
        //如果播放器正在播放就不弹窗
        if (PlayerConnectHelper.getInstance().isPlayingState()) {
            return;
        }
//        final List<Radio> radios = GsonHelper.fromJsonToList(arrayStr, Radio[].class);
//        KLog.d(TAG, "radio" + radios.toString());
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                //点击电台
                EventBus.getDefault().post(LauncherConstants.RECOMMEND_RADIO, LauncherConstants.RECOMMEND_PLAY);
            }
        });
        recommendDialog.show();
        recommendDialog.setDialogMessage(TextUtils.isEmpty(title) ? mContext.getString(R.string.recommend_message_radio) : title);
        recommendDialog.setDialogType(type);
        recommendDialog.setIvRecommend(icon);
        recommendDialog.setMsgColor(rgb);

    }

    private void doHandleParkOilData(JSONArray jsonArray, String poiType, String message, String type) {
        JSONObject jsonObject = jsonArray.optJSONObject(0);
        double lat = jsonObject.optDouble("lat");
        double lon = jsonObject.optDouble("lon");
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                Intent intent = new Intent(mContext, NearByOilParkActivity.class);
                intent.putExtra(LauncherConstants.TravelConstants.OIL_PARK_DATA_POI_TYPE, poiType);
                intent.putExtra(LauncherConstants.TravelConstants.OIL_PARK_DATA_RECOMMEND_TYPE, true);
                intent.putExtra(LauncherConstants.TravelConstants.OIL_PARK_DATA_RECOMMEND_LON, lon);
                intent.putExtra(LauncherConstants.TravelConstants.OIL_PARK_DATA_RECOMMEND_LAT, lat);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

        });
        recommendDialog.show();
        recommendDialog.setDialogMessage(message);
        recommendDialog.setDialogType(type);

    }

    private void doHandleFoodData(String arrayStr, String title, String icon, String rgb, String type) {
        final List<SearchStoreBean> foods = GsonHelper.fromJsonToList(arrayStr, SearchStoreBean[].class);
        if (recommendDialog != null && recommendDialog.isShowing()) {
            recommendDialog.dismiss();
        }
        recommendDialog = new RecommendDialog(mActivity);
        recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
            @Override
            public void sureClick() {
                TPUtils.putList(mContext, LauncherConstants.RecommendExtras.RECOMMEND_FOOD_LIST, foods);
                Intent intent = new Intent(mContext, DeliciousActivity.class);
                intent.putExtra(LauncherConstants.TravelConstants.FOOD_DATA_RECOMMEND_TYPE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        recommendDialog.show();
        recommendDialog.setDialogMessage(title);
        recommendDialog.setDialogType(type);
        recommendDialog.setIvRecommend(icon);
        recommendDialog.setMsgColor(rgb);
    }

    public HashMap<Long, String> getRecommendDataMap() {
        return mRecommendDataMap;
    }

    public void resetRecommendDataMap() {
        mRecommendDataMap.clear();
    }

    public void saveRecommendData(String message) {
        mRecommendDataMap.put(System.currentTimeMillis(), message);
    }

    public void delRecommendData(long time) {
        mRecommendDataMap.remove(time);
    }
}
