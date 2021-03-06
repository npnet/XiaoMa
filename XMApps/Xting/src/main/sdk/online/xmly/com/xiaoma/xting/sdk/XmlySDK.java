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

        // ??????????????????????????????
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

        // ????????????????????????????????????
        //  * categoryId: ??????ID,???????????? [ 0:?????????????????? ]
        //  * type: ???????????????????????????????????????????????? [ 0:???????????? 1:???????????? ]
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

        // ??????????????????????????????????????????????????????????????????????????????/??????/???????????????
        //  * categoryId: ??????ID,???????????? [ 0:?????????????????? ]
        //  * calcDimension: ???????????? [ 1:?????? 2:?????? 3:????????????????????? ]
        //  - tagName: ?????????????????????????????????????????????????????????
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - pageSize: ???????????????,?????????20???,???????????????200
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

        // ???????????????????????????ID??????????????????????????????
        //  * albumId: ??????ID
        //  - sort: ????????????,?????????"asc" [ "asc":???????????? "desc":???????????? "time_asc":???????????? "time_desc":???????????? ]
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - pageSize: ???????????????,?????????20???,???????????????200
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

        // ????????????????????????
        //  * albumIds: ??????ID??????,???','??????(??????: "1000,1010"),??????ID?????????200???,??????200???ID?????????
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

        // ????????????ID????????????????????????????????????????????????
        //  * albumIds: ??????ID??????,???','??????(??????: "1000,1010"),??????ID?????????200???,??????200???ID?????????
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

        // ?????????????????????????????????????????????
        //  * categoryId: ??????ID,???????????? [ 0:?????????????????? ]
        //  - tagName: ?????????????????????????????????????????????????????????
        //  - page: ???????????????,??????????????????1,?????????1???
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

        // ????????????????????????
        //  * trackIds: ??????ID??????,???','??????(??????: "1000,1010"),??????ID?????????200???,??????200???ID?????????
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

        // ??????????????????????????????id??????????????????????????????????????????
        //  * albumId: ??????ID
        //  * trackId: ??????ID
        //  - sort: ????????????,?????????"asc" [ "asc":???????????? "desc":???????????? "time_asc":???????????? "time_desc":???????????? ]
        //  - containsPaid: ?????????????????????
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

        // ???????????????????????????????????????
        //  * categoryId: ??????ID,???????????? [ 0:?????????????????? ]
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

        // ????????????????????????
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

        // ???????????????????????????
        //  * provinceCode: ??????code (??????????????????????????????)
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

        // ??????????????????
        //  * radioType: ???????????? [ 1:????????????2:????????????3:????????? ]
        //  - provinceCode: ???????????? radioType???2???????????????
        //  - page: ???????????????,??????????????????1,?????????1???
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

        // ?????????????????????????????????????????????
        //  * radioId: ????????????ID
        //  - weekday: ???????????????,??????????????????????????? [ 0:?????? 1:?????? 2:?????? 3:?????? 4:?????? 5:?????? 6:?????? ]
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

        // ?????????????????????????????????????????????
        //  * radioId: ????????????ID
        //  - weekday: ???????????????,??????????????????????????? [ 0:?????? 1:?????? 2:?????? 3:?????? 4:?????? 5:?????? 6:?????? ]
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

        // ????????????????????????
        //  * radioIds: ??????ID??????,???','??????(??????: "1000,1010"),??????ID?????????200???,??????200???ID?????????
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

        // ????????????????????????
        //  * radioIds: ??????ID??????,???','??????(??????: "1000,1010"),??????ID?????????200???,??????200???ID?????????
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

        // ????????????????????????????????????
        //  * cityCode: ??????code(?????????????????????????????????)
        //  - page: ???????????????,??????????????????1,?????????1???
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
        // ???????????????????????????
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

        // ??????????????????????????????????????????
        //  * radioCategoryId: ????????????ID
        //  - page: ???????????????,??????????????????1,?????????1???
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

        // ???????????????????????????????????????????????????
        //  * rankType: ????????????. [ 1:???????????? ]
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

        // ??????rank_key????????????????????????????????????
        //  * rankKey: ?????????????????????????????????key,?????????getRankList????????????
        //  - page: ???????????????,??????????????????1,?????????1???
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

        // ??????rank_key????????????????????????????????????
        //  * rankKey: ?????????????????????????????????key,?????????getRankList????????????
        //  - page: ???????????????,??????????????????1,?????????1???
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

        // ???????????????????????????
        //  * radioCount: ???????????????????????????????????????.
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

        // ????????????????????????
        //  - page: ???????????????,??????????????????1,?????????1???
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

        // ??????????????????????????????????????????????????????????????????????????????????????????
        //  * columnId: ??????ID
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
        // ????????????
        //  * searchKey: ???????????????
        //  - categoryId: ??????ID??????????????????0????????????
        //  - calcDimension: ???????????? [ 2:?????? 3:???????????? 4:?????????(??????) ]
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - count: ??????????????????,???????????????????????????,??????20,???????????????200
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
        // ????????????
        //  * searchKey: ???????????????
        //  - categoryId: ??????ID??????????????????0????????????
        //  - calcDimension: ???????????? [ 2:?????? 3:???????????? 4:?????????(??????) ]
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - count: ??????????????????,???????????????????????????,??????20,???????????????200
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

        // ????????????
        //  * searchKey: ???????????????
        //  - categoryId: ????????????ID
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - count: ??????????????????,???????????????????????????,??????20,???????????????200
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

        // ?????????????????????
        //  * count: ?????????top??????????????????, 1 <= count <= 20, ??????top???????????????20???
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

        // ?????????????????????????????????
        //  * searchKey: ?????????????????????
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

        // ????????????
        //  * searchKey: ?????????????????????
        //  - calcDimension: ???????????? [ 2:?????? 3:???????????? 4:?????????(??????) ]
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - count: ??????????????????,???????????????????????????,??????20,???????????????200
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

        // ???????????????????????????????????????????????????
        //  * searchKey: ?????????????????????
        //  - page: ???????????????,??????????????????1,?????????1???
        //  - count: ??????????????????,???????????????????????????,??????20,???????????????200
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

        // ?????????????????????????????????
        //  * albumId: ??????ID
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

        // ???????????????????????????????????????
        //  * trackId: ??????ID
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

        // ????????????????????????
        //  * count: ????????????, ????????????[1 - 50], ????????????:3
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
