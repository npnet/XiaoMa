package com.xiaoma.xting.sdk;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;
import com.xiaoma.xting.sdk.bean.XMAlbumList;
import com.xiaoma.xting.sdk.bean.XMAnnouncerList;
import com.xiaoma.xting.sdk.bean.XMBatchAlbumList;
import com.xiaoma.xting.sdk.bean.XMBatchTrackList;
import com.xiaoma.xting.sdk.bean.XMCategoryList;
import com.xiaoma.xting.sdk.bean.XMCityList;
import com.xiaoma.xting.sdk.bean.XMColumnDetail;
import com.xiaoma.xting.sdk.bean.XMColumnList;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMGussLikeAlbumList;
import com.xiaoma.xting.sdk.bean.XMHotWordList;
import com.xiaoma.xting.sdk.bean.XMLastPlayTrackList;
import com.xiaoma.xting.sdk.bean.XMMetaDataList;
import com.xiaoma.xting.sdk.bean.XMProvinceList;
import com.xiaoma.xting.sdk.bean.XMRadioCategoryList;
import com.xiaoma.xting.sdk.bean.XMRadioList;
import com.xiaoma.xting.sdk.bean.XMRadioListByCategory;
import com.xiaoma.xting.sdk.bean.XMRadioListById;
import com.xiaoma.xting.sdk.bean.XMRankAlbumList;
import com.xiaoma.xting.sdk.bean.XMRankList;
import com.xiaoma.xting.sdk.bean.XMRankTrackList;
import com.xiaoma.xting.sdk.bean.XMRelativeAlbums;
import com.xiaoma.xting.sdk.bean.XMScheduleList;
import com.xiaoma.xting.sdk.bean.XMSearchAlbumList;
import com.xiaoma.xting.sdk.bean.XMSearchAll;
import com.xiaoma.xting.sdk.bean.XMSearchTrackList;
import com.xiaoma.xting.sdk.bean.XMSuggestWords;
import com.xiaoma.xting.sdk.bean.XMTagList;
import com.xiaoma.xting.sdk.bean.XMTrackHotList;
import com.xiaoma.xting.sdk.bean.XMTrackList;
import com.xiaoma.xting.sdk.bean.XMUpdateBatchList;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.UpdateBatchList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.column.ColumnDetail;
import com.ximalaya.ting.android.opensdk.model.column.ColumnList;
import com.ximalaya.ting.android.opensdk.model.live.provinces.ProvinceList;
import com.ximalaya.ting.android.opensdk.model.live.radio.CityList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListByCategory;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaDataList;
import com.ximalaya.ting.android.opensdk.model.ranks.RankAlbumList;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;
import com.ximalaya.ting.android.opensdk.model.ranks.RankTrackList;
import com.ximalaya.ting.android.opensdk.model.search.SearchAll;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.BatchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youthyJ
 * @date 2018/10/10
 */
public class XmlySDK implements OnlineFM {
    private static final String KEY_SECRET = "4749ba4fd2931e96744688aa1714b0cd";
    private static final int DEFAULT_PAGE_SIZE = 20;
    private Context appContext;
    private CommonRequest sdkInstance;

    @Override
    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        if (sdkInstance == null) {
            sdkInstance = CommonRequest.getInstanse();
            sdkInstance.init(appContext, KEY_SECRET);
            sdkInstance.setHttpConfig(null);
//            sdkInstance.setUseHttps(true);
            sdkInstance.mNoSupportHttps.add("http://www.baidu.com/request");
            sdkInstance.setDefaultPagesize(DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    public void getCategories(final XMDataCallback<XMCategoryList> callback) {
        if (callback == null) {
            return;
        }

        // 获取喜马拉雅内容分类
        CommonRequest.getCategories(new HashMap<String, String>(), new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
                callback.onSuccess(new XMCategoryList(categoryList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getTags(long categoryId, int type, final XMDataCallback<XMTagList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        params.put(DTransferConstants.TYPE, String.valueOf(type));

        // 获取专辑标签或者声音标签
        //  * categoryId: 分类ID,指定分类 [ 0:表示热门分类 ]
        //  * type: 指定查询的是专辑标签还是声音标签 [ 0:专辑标签 1:声音标签 ]
        CommonRequest.getTags(params, new IDataCallBack<TagList>() {
            @Override
            public void onSuccess(@Nullable TagList tagList) {
                callback.onSuccess(new XMTagList(tagList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getAlbumList(long categoryId, final int calcDimension, String tagName, int page, final XMDataCallback<XMAlbumList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        map.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
        if (!TextUtils.isEmpty(tagName)) {
            map.put(DTransferConstants.TAG_NAME, tagName);
        }
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 根据分类和标签获取某个分类某个标签下的专辑列表（最火/最新/最多播放）
        //  * categoryId: 分类ID,指定分类 [ 0:表示热门分类 ]
        //  * calcDimension: 计算纬度 [ 1:最火 2:最新 3:经典或播放最多 ]
        //  - tagName: 分类下对应的专辑标签，不填则为热门分类
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - pageSize: 每页多少条,默认为20条,最多不超过200
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(@Nullable AlbumList albumList) {
                callback.onSuccess(new XMAlbumList(albumList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getTracks(long albumId, String sort, int page, final AbsXMDataCallback<XMTrackList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        if (!TextUtils.isEmpty(sort)) {
            map.put(DTransferConstants.SORT, sort);
        }
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 专辑浏览，根据专辑ID获取专辑下的声音列表
        //  * albumId: 专辑ID
        //  - sort: 排序方式,默认为"asc" [ "asc":默认正序 "desc":默认倒序 "time_asc":时间升序 "time_desc":时间降序 ]
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - pageSize: 每页多少条,默认为20条,最多不超过200
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                callback.onSuccess(albumId, new XMTrackList(trackList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(albumId, i, s);
            }
        });
    }

    @Override
    public void getBatch(List<String> albumIds, final XMDataCallback<XMBatchAlbumList> callback) {
        if (callback == null) {
            return;
        }
        StringBuilder paramAlbumIds = new StringBuilder();
        if (albumIds != null) {
            for (int i = 0; i < albumIds.size(); i++) {
                paramAlbumIds.append(albumIds.get(i));
                if (i != (albumIds.size() - 1)) {
                    paramAlbumIds.append(",");
                }
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_IDS, paramAlbumIds.toString());

        // 批量获取专辑列表
        //  * albumIds: 专辑ID列表,用','分隔(示例: "1000,1010"),最大ID数量为200个,超过200的ID将忽略
        CommonRequest.getBatch(map, new IDataCallBack<BatchAlbumList>() {
            @Override
            public void onSuccess(@Nullable BatchAlbumList batchAlbumList) {
                callback.onSuccess(new XMBatchAlbumList(batchAlbumList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getUpdateBatch(List<String> albumIds, final XMDataCallback<XMUpdateBatchList> callback) {
        if (callback == null) {
            return;
        }
        StringBuilder paramAlbumIds = new StringBuilder();
        if (albumIds != null) {
            for (int i = 0; i < albumIds.size(); i++) {
                paramAlbumIds.append(albumIds.get(i));
                if (i != (albumIds.size() - 1)) {
                    paramAlbumIds.append(",");
                }
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_IDS, paramAlbumIds.toString());

        // 根据专辑ID列表批获取量专辑更新提醒信息列表
        //  * albumIds: 专辑ID列表,用','分隔(示例: "1000,1010"),最大ID数量为200个,超过200的ID将忽略
        CommonRequest.getUpdateBatch(map, new IDataCallBack<UpdateBatchList>() {
            @Override
            public void onSuccess(@Nullable UpdateBatchList updateBatchList) {
                callback.onSuccess(new XMUpdateBatchList(updateBatchList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getHotTracks(long categoryId, String tagName, int page, final XMDataCallback<XMTrackHotList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        if (!TextUtils.isEmpty(tagName)) {
            map.put(DTransferConstants.TAG_NAME, tagName);
        }
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 根据分类和标签获取热门声音列表
        //  * categoryId: 分类ID,指定分类 [ 0:表示热门分类 ]
        //  - tagName: 分类下对应的专辑标签，不填则为热门分类
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getHotTracks(map, new IDataCallBack<TrackHotList>() {
            @Override
            public void onSuccess(@Nullable TrackHotList trackHotList) {
                callback.onSuccess(new XMTrackHotList(trackHotList));
            }

            @Override
            public void onError(int i, String s) {
                onError(i, s);
            }
        });
    }

    @Override
    public void getBatchTracks(List<String> trackIds, final XMDataCallback<XMBatchTrackList> callback) {
        if (callback == null) {
            return;
        }
        StringBuilder paramTrackIds = new StringBuilder();
        if (trackIds != null) {
            for (int i = 0; i < trackIds.size(); i++) {
                paramTrackIds.append(trackIds.get(i));
                if (i != (trackIds.size() - 1)) {
                    paramTrackIds.append(",");
                }
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TRACK_IDS, paramTrackIds.toString());

        // 批量获取声音列表
        //  * trackIds: 声音ID列表,用','分隔(示例: "1000,1010"),最大ID数量为200个,超过200的ID将忽略
        CommonRequest.getBatchTracks(map, new IDataCallBack<BatchTrackList>() {
            @Override
            public void onSuccess(@Nullable BatchTrackList batchTrackList) {
                callback.onSuccess(new XMBatchTrackList(batchTrackList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getLastPlayTracks(long albumId, long trackId, String sort, final XMDataCallback<XMLastPlayTrackList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.TRACK_ID, String.valueOf(trackId));
        if (!TextUtils.isEmpty(sort)) {
            map.put(DTransferConstants.SORT, sort);
        }
        map.put(DTransferConstants.CONTAINS_PAID, String.valueOf(false));

        // 根据上一次所听声音的id，获取此声音所在那一页的声音
        //  * albumId: 专辑ID
        //  * trackId: 专辑ID
        //  - sort: 排序方式,默认为"asc" [ "asc":默认正序 "desc":默认倒序 "time_asc":时间升序 "time_desc":时间降序 ]
        //  - containsPaid: 是否是付费声音
        CommonRequest.getLastPlayTracks(map, new IDataCallBack<LastPlayTrackList>() {
            @Override
            public void onSuccess(@Nullable LastPlayTrackList lastPlayTrackList) {
                callback.onSuccess(new XMLastPlayTrackList(lastPlayTrackList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getMetadataList(long categoryId, final XMDataCallback<XMMetaDataList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));

        // 获取某个分类下的元数据列表
        //  * categoryId: 分类ID,指定分类 [ 0:表示热门分类 ]
        CommonRequest.getMetadataList(map, new IDataCallBack<MetaDataList>() {
            @Override
            public void onSuccess(@Nullable MetaDataList metaDataList) {
                callback.onSuccess(new XMMetaDataList(metaDataList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getProvinces(final XMDataCallback<XMProvinceList> callback) {
        if (callback == null) {
            return;
        }

        // 获取直播省市列表
        CommonRequest.getProvinces(null, new IDataCallBack<ProvinceList>() {
            @Override
            public void onSuccess(@Nullable ProvinceList provinceList) {
                callback.onSuccess(new XMProvinceList(provinceList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getCitys(long provinceCode, final XMDataCallback<XMCityList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.PROVINCECODE, String.valueOf(provinceCode));

        // 获取某省份城市列表
        //  * provinceCode: 省份code (国家行政规划的省代码)
        CommonRequest.getCitys(map, new IDataCallBack<CityList>() {
            @Override
            public void onSuccess(@Nullable CityList cityList) {
                callback.onSuccess(new XMCityList(cityList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getCountryRadios(int page, XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        getRadios(1, -1, page, callback);
    }

    @Override
    public void getProvinceRadios(long provinceCode, int page, XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        getRadios(2, provinceCode, page, callback);
    }

    @Override
    public void getNetworkRadios(int page, XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        getRadios(3, -1, page, callback);
    }

    private void getRadios(int radioType, long provinceCode, int page, final XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIOTYPE, String.valueOf(radioType));
        if (provinceCode > 0) {
            map.put(DTransferConstants.PROVINCECODE, String.valueOf(provinceCode));
        }
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 获取直播电台
        //  * radioType: 电台类型 [ 1:国家台，2:省市台，3:网络台 ]
        //  - provinceCode: 省份代码 radioType为2时不能为空
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                callback.onSuccess(new XMRadioList(radioList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getTodaySchedules(long radioId, AbsXMDataCallback<XMScheduleList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIOID, String.valueOf(radioId));

        // 获取直播电台某一天的节目排期表
        //  * radioId: 直播电台ID
        //  - weekday: 表示星期几,不填则取今天的星期 [ 0:周日 1:周一 2:周二 3:周三 4:周四 5:周五 6:周六 ]
        CommonRequest.getSchedules(map, new IDataCallBack<ScheduleList>() {
            @Override
            public void onSuccess(@Nullable ScheduleList scheduleList) {
                callback.onSuccess(radioId, new XMScheduleList(scheduleList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(radioId, i, s);
            }
        });
    }

    @Override
    public void getWeekdaySchedules(long radioId, int weekday, final XMDataCallback<XMScheduleList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIOID, String.valueOf(radioId));
        if (weekday >= 0 && weekday <= 6) {
            map.put(DTransferConstants.WEEKDAY, String.valueOf(weekday));
        }

        // 获取直播电台某一天的节目排期表
        //  * radioId: 直播电台ID
        //  - weekday: 表示星期几,不填则取今天的星期 [ 0:周日 1:周一 2:周二 3:周三 4:周四 5:周五 6:周六 ]
        CommonRequest.getSchedules(map, new IDataCallBack<ScheduleList>() {
            @Override
            public void onSuccess(@Nullable ScheduleList scheduleList) {
                callback.onSuccess(new XMScheduleList(scheduleList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRadiosByIds(List<String> radioIds, final XMDataCallback<XMRadioListById> callback) {
        if (callback == null) {
            return;
        }
        StringBuilder paramRadioIds = new StringBuilder();
        if (radioIds != null) {
            for (int i = 0; i < radioIds.size(); i++) {
                paramRadioIds.append(radioIds.get(i));
                if (i != (radioIds.size() - 1)) {
                    paramRadioIds.append(",");
                }
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIO_IDS, paramRadioIds.toString());

        // 批量获取电台接口
        //  * radioIds: 电台ID列表,用','分隔(示例: "1000,1010"),最大ID数量为200个,超过200的ID将忽略
        CommonRequest.getRadiosByIds(map, new IDataCallBack<RadioListById>() {
            @Override
            public void onSuccess(@Nullable RadioListById radioListById) {
                callback.onSuccess(new XMRadioListById(radioListById));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRadioById(long radioIds, AbsXMDataCallback<XMRadioListById> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIO_IDS, String.valueOf(radioIds));

        // 批量获取电台接口
        //  * radioIds: 电台ID列表,用','分隔(示例: "1000,1010"),最大ID数量为200个,超过200的ID将忽略
        CommonRequest.getRadiosByIds(map, new IDataCallBack<RadioListById>() {
            @Override
            public void onSuccess(@Nullable RadioListById radioListById) {
                callback.onSuccess(radioIds, new XMRadioListById(radioListById));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(radioIds, i, s);
            }
        });
    }

    @Override
    public void getRadiosByCity(int cityCode, int page, final XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }

        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CITY_CODE, String.valueOf(cityCode));
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 获取某个城市下的电台列表
        //  * cityCode: 城市code(国家行政规划的城市代码)
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getRadiosByCity(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                callback.onSuccess(new XMRadioList(radioList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRadioCategory(final XMDataCallback<XMRadioCategoryList> callback) {
        if (callback == null) {
            return;
        }
        // 获取直播电台的分类
        CommonRequest.getRadioCategory(new HashMap<String, String>(), new IDataCallBack<RadioCategoryList>() {
            @Override
            public void onSuccess(@Nullable RadioCategoryList radioCategoryList) {
                callback.onSuccess(new XMRadioCategoryList(radioCategoryList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRadiosByCategory(long radioCategoryId, int page, final XMDataCallback<XMRadioListByCategory> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }
        Map<String, String> maps = new HashMap<>();
        maps.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(radioCategoryId));
        maps.put(DTransferConstants.PAGE, String.valueOf(page));

        // 根据电台分类获取直播电台数据
        //  * radioCategoryId: 直播分类ID
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getRadiosByCategory(maps, new IDataCallBack<RadioListByCategory>() {
            @Override
            public void onSuccess(@Nullable RadioListByCategory radioListByCategory) {
                callback.onSuccess(new XMRadioListByCategory(radioListByCategory));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRankList(int rankType, final XMDataCallback<XMRankList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RANK_TYPE, String.valueOf(rankType));

        // 根据榜单类型获取榜单首页的榜单列表
        //  * rankType: 榜单类型. [ 1:节目榜单 ]
        CommonRequest.getRankList(map, new IDataCallBack<RankList>() {
            @Override
            public void onSuccess(@Nullable RankList rankList) {
                callback.onSuccess(new XMRankList(rankList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRankAlbumList(long rankListId, int page, final XMDataCallback<XMRankAlbumList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put("rank_list_id", String.valueOf(rankListId));
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 根据rank_key获取某个榜单下的专辑列表
        //  * rankKey: 用于获取具体榜单内容的key,可通过getRankList接口获取
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getRankAlbumListNew(map, new IDataCallBack<RankAlbumList>() {
            @Override
            public void onSuccess(@Nullable RankAlbumList rankAlbumList) {
                callback.onSuccess(new XMRankAlbumList(rankAlbumList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRankTrackList(String rankKey, int page, final XMDataCallback<XMRankTrackList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RANK_KEY, rankKey);
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 根据rank_key获取某个榜单下的声音列表
        //  * rankKey: 用于获取具体榜单内容的key,可通过getRankList接口获取
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getRankTrackList(map, new IDataCallBack<RankTrackList>() {
            @Override
            public void onSuccess(@Nullable RankTrackList rankTrackList) {
                callback.onSuccess(new XMRankTrackList(rankTrackList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRankRadios(int radioCount, final XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.RADIO_COUNT, String.valueOf(radioCount));

        // 获取直播电台排行榜
        //  * radioCount: 需要获取排行榜中的电台数目.
        CommonRequest.getRankRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                callback.onSuccess(new XMRadioList(radioList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getColumnList(int page, final XMDataCallback<XMColumnList> callback) {
        if (callback == null) {
            return;
        }
        if (page <= 0) {
            page = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.PAGE, String.valueOf(page));

        // 获取精品听单内容
        //  - page: 返回第几页,必须大于等于1,默认为1页
        CommonRequest.getColumnList(map, new IDataCallBack<ColumnList>() {
            @Override
            public void onSuccess(@Nullable ColumnList columnList) {
                callback.onSuccess(new XMColumnList(columnList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getComlumnDetail(long columnId, final XMDataCallback<XMColumnDetail> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ID, String.valueOf(columnId));

        // 获取某个听单详情，每个听单包含听单简介信息和专辑或声音的列表
        //  * columnId: 听单ID
        CommonRequest.getComlumnDetail(map, new IDataCallBack<ColumnDetail>() {
            @Override
            public void onSuccess(ColumnDetail columnDetail) {
                callback.onSuccess(new XMColumnDetail(columnDetail));
            }

            @Override
            public void onError(int code, String message) {
                callback.onError(code, message);
            }
        });
    }

    @Override
    public void getSearchedAlbums(String searchKey, long categoryId, int calcDimension, int page, int count, final XMDataCallback<XMSearchAlbumList> callback) {
        if (callback == null) {
            return;
        }
        if (searchKey == null) {
            searchKey = "";
        }
        if (page <= 0) {
            page = 1;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 200) {
            count = 200;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        map.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(count));
        // 搜索专辑
        //  * searchKey: 搜索关键词
        //  - categoryId: 分类ID，不填或者为0检索全库
        //  - calcDimension: 排序条件 [ 2:最新 3:最多播放 4:最相关(默认) ]
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - count: 分页请求参数,表示每页多少条记录,默认20,最多不超过200
        CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                callback.onSuccess(new XMSearchAlbumList(searchAlbumList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getSearchedTracks(String searchKey, long categoryId, int calcDimension, int page, int count, final XMDataCallback<XMSearchTrackList> callback) {
        if (callback == null) {
            return;
        }
        if (searchKey == null) {
            searchKey = "";
        }
        if (page <= 0) {
            page = 1;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 200) {
            count = 200;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        map.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(count));
        // 搜索声音
        //  * searchKey: 搜索关键词
        //  - categoryId: 分类ID，不填或者为0检索全库
        //  - calcDimension: 排序条件 [ 2:最新 3:最多播放 4:最相关(默认) ]
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - count: 分页请求参数,表示每页多少条记录,默认20,最多不超过200
        CommonRequest.getSearchedTracks(map, new IDataCallBack<SearchTrackList>() {
            @Override
            public void onSuccess(@Nullable SearchTrackList searchTrackList) {
                callback.onSuccess(new XMSearchTrackList(searchTrackList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getSearchedRadios(String searchKey, long categoryId, int page, int count, final XMDataCallback<XMRadioList> callback) {
        if (callback == null) {
            return;
        }
        if (searchKey == null) {
            searchKey = "";
        }
        if (page <= 0) {
            page = 1;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 200) {
            count = 200;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(categoryId));
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(count));

        // 搜索直播
        //  * searchKey: 搜索关键词
        //  - categoryId: 直播分类ID
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - count: 分页请求参数,表示每页多少条记录,默认20,最多不超过200
        CommonRequest.getSearchedRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                callback.onSuccess(new XMRadioList(radioList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getHotWords(int count, final XMDataCallback<XMHotWordList> callback) {
        if (callback == null) {
            return;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 20) {
            count = 20;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, String.valueOf(count));

        // 获取最新热搜词
        //  * count: 获取前top长度的热搜词, 1 <= count <= 20, 目前top只支持最多20个
        CommonRequest.getHotWords(map, new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                callback.onSuccess(new XMHotWordList(hotWordList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getSuggestWord(String searchKey, final XMDataCallback<XMSuggestWords> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);

        // 获取某个关键词的联想词
        //  * searchKey: 搜索查询词参数
        CommonRequest.getSuggestWord(map, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                callback.onSuccess(new XMSuggestWords(suggestWords));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getSearchAnnouncers(String searchKey, int calcDimension, int page, int count, final XMDataCallback<XMAnnouncerList> callback) {
        if (callback == null) {
            return;
        }
        if (searchKey == null) {
            searchKey = "";
        }
        if (page <= 0) {
            page = 1;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 200) {
            count = 200;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(count));

        // 搜索主播
        //  * searchKey: 搜索查询词参数
        //  - calcDimension: 排序条件 [ 2:最新 3:最多播放 4:最相关(默认) ]
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - count: 分页请求参数,表示每页多少条记录,默认20,最多不超过200
        CommonRequest.getSearchAnnouncers(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(@Nullable AnnouncerList announcerList) {
                callback.onSuccess(new XMAnnouncerList(announcerList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getSearchAll(String searchKey, int page, int count, final XMDataCallback<XMSearchAll> callback) {
        if (callback == null) {
            return;
        }
        if (searchKey == null) {
            searchKey = "";
        }
        if (page <= 0) {
            page = 1;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 200) {
            count = 200;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(count));

        // 获取指定数量直播，声音，专辑的内容
        //  * searchKey: 搜索查询词参数
        //  - page: 返回第几页,必须大于等于1,默认为1页
        //  - count: 分页请求参数,表示每页多少条记录,默认20,最多不超过200
        CommonRequest.getSearchAll(map, new IDataCallBack<SearchAll>() {
            @Override
            public void onSuccess(@Nullable SearchAll searchAll) {
                callback.onSuccess(new XMSearchAll(searchAll));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRelativeAlbums(long albumId, final XMDataCallback<XMRelativeAlbums> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUMID, String.valueOf(albumId));

        // 获取某个专辑的相关推荐
        //  * albumId: 专辑ID
        CommonRequest.getRelativeAlbums(map, new IDataCallBack<RelativeAlbums>() {
            @Override
            public void onSuccess(RelativeAlbums relativeAlbums) {
                callback.onSuccess(new XMRelativeAlbums(relativeAlbums));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getRelativeAlbumsUseTrackId(long trackId, final XMDataCallback<XMRelativeAlbums> callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TRACKID, String.valueOf(trackId));

        // 获取某个声音的相关推荐专辑
        //  * trackId: 声音ID
        CommonRequest.getRelativeAlbumsUseTrackId(map, new IDataCallBack<RelativeAlbums>() {
            @Override
            public void onSuccess(RelativeAlbums relativeAlbums) {
                callback.onSuccess(new XMRelativeAlbums(relativeAlbums));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    @Override
    public void getGuessLikeAlbum(int count, final XMDataCallback<XMGussLikeAlbumList> callback) {
        if (callback == null) {
            return;
        }
        if (count <= 0) {
            count = 1;
        }
        if (count > 50) {
            count = 50;
        }
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, String.valueOf(count));

        // 获取猜你喜欢专辑
        //  * count: 返回数量, 取值范围[1 - 50], 默认值为:3
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                callback.onSuccess(new XMGussLikeAlbumList(gussLikeAlbumList));
            }

            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }
        });
    }
}
