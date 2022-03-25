package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.location.Location;
import android.support.annotation.NonNull;

import com.amap.api.services.core.PoiItem;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.PageListUtil;
import com.xiaoma.club.common.viewmodel.ViewState;
import com.xiaoma.club.msg.chat.datasource.PoiSearchDS;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.utils.StringUtil;

/**
 * Created by LKF on 2018/10/12 0012.
 */
public class PoiSearchVM extends BaseViewModel {
    private static final String TAG = "PoiSearchVM";

    private static final int POI_PAGE_SIZE = 50;
    private static final int POI_SEARCH_RADIUS = 5000;

    private final MutableLiveData<Integer> mSelectPoiIndex = new MutableLiveData<>();
    private LiveData<PagedList<PoiItem>> mPoiItemPageList;
    private final MutableLiveData<LocationSearch> mLocationSearch = new MutableLiveData<>();
    private final MutableLiveData<ViewState> mViewState = new MutableLiveData<>();

    @NonNull
    public MutableLiveData<Integer> getSelectPoiIndex() {
        return mSelectPoiIndex;
    }

    public MutableLiveData<LocationSearch> getLocationSearch() {
        return mLocationSearch;
    }

    @NonNull
    public LiveData<PagedList<PoiItem>> getPoiItemPageList() {
        if (mPoiItemPageList == null) {
            final DataSource.Factory<Integer, PoiItem> factory = new PoiDSFactory();
            final PagedList.Config pagedListCfg = new PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(POI_PAGE_SIZE)
                    .setPageSize(POI_PAGE_SIZE)
                    .build();
            mPoiItemPageList = new LivePagedListBuilder<>(factory, pagedListCfg)
                    .setInitialLoadKey(1)
                    .setBoundaryCallback(new PagedList.BoundaryCallback<PoiItem>() {
                        @Override
                        public void onZeroItemsLoaded() {
                            super.onZeroItemsLoaded();
                            LogUtil.logI(TAG, StringUtil.format("onZeroItemsLoaded -> "));
                        }

                        @Override
                        public void onItemAtFrontLoaded(@NonNull PoiItem itemAtFront) {
                            super.onItemAtFrontLoaded(itemAtFront);
                            LogUtil.logI(TAG, StringUtil.format("onItemAtFrontLoaded -> [ itemAtFront: %s ]", itemAtFront));
                        }

                        @Override
                        public void onItemAtEndLoaded(@NonNull PoiItem itemAtEnd) {
                            super.onItemAtEndLoaded(itemAtEnd);
                            LogUtil.logI(TAG, StringUtil.format("onItemAtEndLoaded -> [ itemAtEnd: %s ]", itemAtEnd));
                            PageListUtil.setLoadMoreEnable(mPoiItemPageList.getValue(), true);
                        }
                    }).build();
        }
        return mPoiItemPageList;
    }

    public MutableLiveData<ViewState> getViewState() {
        return mViewState;
    }

    public PoiSearchVM(@NonNull final Application application) {
        super(application);
    }

    /*public void updateLocation(String keyword, Location location) {
        try {
            if (mPoiItemPageList.getValue() != null)
                mPoiItemPageList.getValue().getDataSource().invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtil.logI(TAG, "onCleared -> ");
    }

    private class PoiDSFactory extends DataSource.Factory<Integer, PoiItem> {
        @Override
        public DataSource<Integer, PoiItem> create() {
            String keyword = null;
            Location location = null;
            final LocationSearch search = getLocationSearch().getValue();
            if (search != null) {
                keyword = search.getKeyword();
                location = search.getLocation();
            }
            LogUtil.logI(TAG, "DataSource.Factory -> create [ mKeyword: %s, location: %s ]", keyword, location);
            return new PoiSearchDS(getApplication(), StringUtil.optString(keyword), location, POI_SEARCH_RADIUS);
        }
    }

    public static class LocationSearch {
        private String mKeyword;
        private Location mLocation;

        public LocationSearch(String keyword, Location location) {
            mKeyword = keyword;
            mLocation = location;
        }

        public String getKeyword() {
            return mKeyword;
        }

        public Location getLocation() {
            return mLocation;
        }
    }
}