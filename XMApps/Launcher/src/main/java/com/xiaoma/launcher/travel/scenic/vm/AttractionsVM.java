package com.xiaoma.launcher.travel.scenic.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.category.response.CategoryBean;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;

public class AttractionsVM extends BaseCollectVM {
    private MutableLiveData<XmResource<List<SearchStoreBean>>> attractionsList;
    private MutableLiveData<XmResource<List<CategoryBean.SubcateBean>>> attractionsSort;
    private final int limit = LauncherConstants.PAGE_SIZE;
    private LocationInfo mLocationInfo;

    public AttractionsVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<List<SearchStoreBean>>> getAttractionsList() {
        if (attractionsList == null) {
            attractionsList = new MutableLiveData<>();
        }
        return attractionsList;
    }

    public MutableLiveData<XmResource<List<CategoryBean.SubcateBean>>> getAttractionsSort() {
        if (attractionsSort == null) {
            attractionsSort = new MutableLiveData<>();
        }
        return attractionsSort;
    }

    public boolean isAttractionsLoadEnd() {
        final MutableLiveData<XmResource<List<SearchStoreBean>>> deliciousList = getAttractionsList();
        if (deliciousList != null && deliciousList.getValue() != null) {
            final List<SearchStoreBean> data = deliciousList.getValue().data;
            if (!ListUtils.isEmpty(data)) {
                return data.size() < LauncherConstants.PAGE_SIZE;
            }

        }
        return false;
    }

    /**
     * 查询分类景点详细
     *
     * @param query
     * @param cateid
     * @param offset
     */
    public void searchAttractions(String type,String query, String pos, String city, String cateid, int offset) {
        getAttractionsList().setValue(XmResource.<List<SearchStoreBean>>loading());
        RequestManager.getInstance().conditionSearchStore(type,query, pos, city, cateid, limit, offset, new ResultCallback<XMResult<List<SearchStoreBean>>>() {
            @Override
            public void onSuccess(XMResult<List<SearchStoreBean>> result) {
                getAttractionsList().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getAttractionsList().setValue(XmResource.<List<SearchStoreBean>>failure(msg));
            }
        });
    }

    /**
     * 获取景点推荐
     */
    public void fetchRecommendByAttraction() {
        getAttractionsList().setValue(XmResource.<List<SearchStoreBean>>loading());

        List<SearchStoreBean> attractionBeans = TPUtils.getList(AppHolder.getInstance().getAppContext(), LauncherConstants.RecommendExtras.RECOMMEND_ATTRACTION_LIST, SearchStoreBean[].class);

        if (!ListUtils.isEmpty(attractionBeans)) {
            getAttractionsList().setValue(XmResource.success(attractionBeans));
        } else {
            getAttractionsList().setValue(XmResource.<List<SearchStoreBean>>error(AppHolder.getInstance().getAppContext().getString(R.string.error_msg)));
        }
    }

    public void queryAttractiousSort(String type,final String query) {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }
        getAttractionsSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>loading());
        RequestManager.getInstance().queryCategory(type,mLocationInfo.getCity(), mLocationInfo.getLatitude(),mLocationInfo.getLongitude(),new ResultCallback<XMResult<List<CategoryBean>>>() {
            @Override
            public void onSuccess(XMResult<List<CategoryBean>> result) {
                if (!ListUtils.isEmpty(result.getData())) {
                    for (CategoryBean item : result.getData()) {
                        if (item.getName().equals(query)) {
                            getAttractionsSort().setValue(XmResource.success(item.getSubcate()));
                        }
                    }
                } else {
                    getAttractionsSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>error("NULL DATA"));
                }


            }

            @Override
            public void onFailure(int code, String msg) {
//                if (code == -1) {
//                    getAttractionsSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>error(code, "network_error"));
//                } else {
                    getAttractionsSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>failure(msg));
//                }
            }
        });
    }
}
