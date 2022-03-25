package com.xiaoma.club.msg.chat.datasource;

import android.arch.paging.PageKeyedDataSource;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LKF on 2018/10/13 0013.
 */
public class PoiSearchDS extends PageKeyedDataSource<Integer, PoiItem> {
    private static final String TAG = "PoiSearchDataSource";

    private final String mKeyword;
    private final Context mContext;
    private final Location mLocation;
    private final int mRadius;

    public PoiSearchDS(Context context, String keyword, Location location, int radius) {
        mContext = context;
        mKeyword = keyword;
        mLocation = location;
        mRadius = radius;
    }

    @Override
    public void loadInitial(@NonNull PageKeyedDataSource.LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, PoiItem> callback) {
        List<PoiItem> appendList;
        if (mLocation != null
                && (appendList = searchPoiByLocation(mKeyword, mLocation, mRadius, 1, params.requestedLoadSize)) != null
                && !appendList.isEmpty()) {
            LogUtil.logI(TAG, StringUtil.format("loadInitial -> appendList: %s", appendList));
            callback.onResult(appendList, null, 2);
        } else {
            LogUtil.logI(TAG, "loadInitial -> appendList: empty");
            callback.onResult(Collections.<PoiItem>emptyList(), null, 1);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PoiItem> Loglback) {
        Log.i(TAG, StringUtil.format("loadBefore -> key: %d, requestedLoadSize: %d", params.key, params.requestedLoadSize));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PoiItem> callback) {
        final int currPage = params.key;
        // 先展示placeholder
        if (mLocation != null) {
            List<PoiItem> appendList = searchPoiByLocation(mKeyword, mLocation, mRadius, currPage, params.requestedLoadSize);
            if (appendList != null) {
                LogUtil.logI(TAG, "loadAfter -> appendList: %s", appendList);
                callback.onResult(appendList, appendList.isEmpty() ? null : currPage + 1);
            } else {
                LogUtil.logI(TAG, "loadAfter -> appendList: empty");
                callback.onResult(Collections.<PoiItem>emptyList(), currPage);
                //EventBus.getDefault().post(this, EventTag.LOAD_ERR);
            }
        } else {
            LogUtil.logI(TAG, "loadAfter -> [ appendList: empty ] Location is null");
            callback.onResult(Collections.<PoiItem>emptyList(), currPage);
            //EventBus.getDefault().post(this, EventTag.LOAD_ERR);
        }
    }

    @Nullable
    private List<PoiItem> searchPoiByLocation(String keyword, Location location, int radius, int pageNum, int pageSize) {
        //Log.i(TAG, StringUtil.format("searchPoiByLocation -> [ radius: %d, pageSize: %d ] -> pageNum: %d", radius, pageSize, pageNum));
        AMapLocation aMapLocation = null;
        if (location instanceof AMapLocation) {
            aMapLocation = (AMapLocation) location;
        }
        // 当前点
        final LatLonPoint centerPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        // 城市代码
        final String cityCode = aMapLocation != null ? aMapLocation.getCityCode() : "";
        // 查询参数
        final PoiSearch.Query query = new PoiSearch.Query(StringUtil.optString(keyword), "", cityCode);
        query.setLocation(centerPoint);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setDistanceSort(true);
        // 查询接口
        final PoiSearch search = new PoiSearch(mContext, query);
        // 查询范围
        final PoiSearch.SearchBound bound = new PoiSearch.SearchBound(centerPoint, radius);
        search.setBound(bound);
        // 执行查询操作
        List<PoiItem> poiItemList = null;
        try {
            poiItemList = search.searchPOI().getPois();
            if (poiItemList == null) {
                poiItemList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return poiItemList;
    }
}