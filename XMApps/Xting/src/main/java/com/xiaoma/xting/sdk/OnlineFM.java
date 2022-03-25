package com.xiaoma.xting.sdk;

import android.content.Context;

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

import java.util.List;

/**
 * @author youthyJ
 * @date 2018/9/29
 */
public interface OnlineFM {
    void init(Context context);

    // 点播相关接口 ↓
    void getCategories(XMDataCallback<XMCategoryList> callback);

    void getTags(long categoryId, int type, XMDataCallback<XMTagList> callback);

    void getAlbumList(long categoryId, int calcDimension, String tagName, int page, XMDataCallback<XMAlbumList> callback);

    void getTracks(long albumId, String sort, int page, AbsXMDataCallback<XMTrackList> callback);

    void getBatch(List<String> albumIds, XMDataCallback<XMBatchAlbumList> callback);

    void getUpdateBatch(List<String> albumIds, XMDataCallback<XMUpdateBatchList> callback);

    void getHotTracks(long categoryId, String tagName, int page, XMDataCallback<XMTrackHotList> callback);

    void getBatchTracks(List<String> trackIds, XMDataCallback<XMBatchTrackList> callback);

    void getLastPlayTracks(long albumId, long trackId, String sort, XMDataCallback<XMLastPlayTrackList> callback);

    void getMetadataList(long categoryId, XMDataCallback<XMMetaDataList> callback);
    // 点播相关接口 ↑

    // 直播相关接口 ↓
    void getProvinces(final XMDataCallback<XMProvinceList> callback);

    void getCitys(long provinceCode, XMDataCallback<XMCityList> callback);

    void getCountryRadios(int page, final XMDataCallback<XMRadioList> callback);

    void getProvinceRadios(long provinceCode, int page, XMDataCallback<XMRadioList> callback);

    void getNetworkRadios(int page, final XMDataCallback<XMRadioList> callback);

    void getTodaySchedules(long radioId, AbsXMDataCallback<XMScheduleList> callback);

    void getWeekdaySchedules(long radioId, int weekday, XMDataCallback<XMScheduleList> callback);

    void getRadiosByIds(List<String> radioIds, XMDataCallback<XMRadioListById> callback);

    void getRadioById(long radio, AbsXMDataCallback<XMRadioListById> callback);

    void getRadiosByCity(int cityCode, int page, XMDataCallback<XMRadioList> callback);

    void getRadioCategory(XMDataCallback<XMRadioCategoryList> callback);

    void getRadiosByCategory(long categoryId, int page, XMDataCallback<XMRadioListByCategory> callback);
    // 直播相关接口↑

    // 排行榜相关接口↓
    void getRankList(int rankType, XMDataCallback<XMRankList> callback);

    void getRankAlbumList(long rankListId, int page, XMDataCallback<XMRankAlbumList> callback);

    void getRankTrackList(String rankKey, int page, XMDataCallback<XMRankTrackList> callback);

    void getRankRadios(int radioCount, final XMDataCallback<XMRadioList> callback);
    // 排行榜相关接口↑

    // 听单数据相关接口↓
    void getColumnList(int page, XMDataCallback<XMColumnList> callback);

    void getComlumnDetail(long columnId, XMDataCallback<XMColumnDetail> callback);
    // 听单数据相关接口↑

    // 搜索相关接口↓
    void getSearchedAlbums(String searchKey, long categoryId, int calcDimension, int page, int count, XMDataCallback<XMSearchAlbumList> callback);

    void getSearchedTracks(String searchKey, long categoryId, int calcDimension, int page, int count, XMDataCallback<XMSearchTrackList> callback);

    void getSearchedRadios(String searchKey, long categoryId, int page, int count, XMDataCallback<XMRadioList> callback);

    void getHotWords(int count, XMDataCallback<XMHotWordList> callback);

    void getSuggestWord(String searchKey, XMDataCallback<XMSuggestWords> callback);

    void getSearchAnnouncers(String searchKey, int calcDimension, int page, int count, XMDataCallback<XMAnnouncerList> callback);

    void getSearchAll(String searchKey, int page, int count, XMDataCallback<XMSearchAll> callback);
    // 搜索相关接口↑

    // 显示推荐相关接口↓
    void getRelativeAlbums(long albumId, XMDataCallback<XMRelativeAlbums> callback);

    void getRelativeAlbumsUseTrackId(long trackId, XMDataCallback<XMRelativeAlbums> callback);

    void getGuessLikeAlbum(int count, XMDataCallback<XMGussLikeAlbumList> callback);
    // 显示推荐相关接口↑
}
