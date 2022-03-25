package com.xiaoma.launcher.travel.delicious.vm;

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
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;

public class DeliciousVM extends BaseCollectVM {
    private MutableLiveData<XmResource<List<SearchStoreBean>>> delicioussList;
    private MutableLiveData<XmResource<List<CategoryBean.SubcateBean>>> mDeliciousSort;

    public DeliciousVM(@NonNull Application application) {
        super(application);
    }

    private final int limit = LauncherConstants.PAGE_SIZE;
    private LocationInfo mLocationInfo;

    public MutableLiveData<XmResource<List<SearchStoreBean>>> getDeliciousList() {
        if (delicioussList == null) {
            delicioussList = new MutableLiveData<>();
        }
        return delicioussList;
    }

    public MutableLiveData<XmResource<List<CategoryBean.SubcateBean>>> getmDeliciousSort() {
        if (mDeliciousSort == null) {
            mDeliciousSort = new MutableLiveData<>();
        }
        return mDeliciousSort;
    }

    public boolean isDeliciousLoadEnd() {
        final MutableLiveData<XmResource<List<SearchStoreBean>>> deliciousList = getDeliciousList();
        if (deliciousList != null && deliciousList.getValue() != null) {
            final List<SearchStoreBean> data = deliciousList.getValue().data;
            if (!ListUtils.isEmpty(data)) {
                return data.size() < LauncherConstants.PAGE_SIZE;
            }
        }
        return false;
    }

    /**
     * 查询分类美食详细
     *
     * @param query
     * @param cateid
     * @param offset
     */
    public void searchDelicious(String type, String query, String pos, String city, String cateid, int offset) {

        getDeliciousList().setValue(XmResource.<List<SearchStoreBean>>loading());
        RequestManager.getInstance().conditionSearchStore(type, query, pos, city, cateid, limit, offset, new ResultCallback<XMResult<List<SearchStoreBean>>>() {
            @Override
            public void onSuccess(XMResult<List<SearchStoreBean>> result) {
                getDeliciousList().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getDeliciousList().setValue(XmResource.<List<SearchStoreBean>>failure(msg));
            }
        });
    }

    /**
     * 查询美食分类
     */
    public void queryDeliciousSort(String type, final String query) {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }
        getmDeliciousSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>loading());
        RequestManager.getInstance().queryCategory(type, mLocationInfo.getCity(), mLocationInfo.getLatitude(), mLocationInfo.getLongitude(), new ResultCallback<XMResult<List<CategoryBean>>>() {
            @Override
            public void onSuccess(XMResult<List<CategoryBean>> result) {
                if (!ListUtils.isEmpty(result.getData())) {
                    for (CategoryBean item : result.getData()) {
                        if (item.getName().equals(query)) {
                            getmDeliciousSort().setValue(XmResource.success(item.getSubcate()));
                        }
                    }
                } else {
                    getmDeliciousSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>error("NULL DATA"));
                }


            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == -1) {
                    getmDeliciousSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>error(code, "network_error"));
                } else {
                    getmDeliciousSort().setValue(XmResource.<List<CategoryBean.SubcateBean>>error(code, msg));
                }

            }
        });
    }

    /**
     * 获取美食推荐
     */
    public void fetchRecommendByFood() {
        getDeliciousList().setValue(XmResource.<List<SearchStoreBean>>loading());

        List<SearchStoreBean> foodBeans = TPUtils.getList(AppHolder.getInstance().getAppContext(), LauncherConstants.RecommendExtras.RECOMMEND_FOOD_LIST, SearchStoreBean[].class);

        if (!ListUtils.isEmpty(foodBeans)) {
            getDeliciousList().setValue(XmResource.success(foodBeans));
        } else {
            getDeliciousList().setValue(XmResource.<List<SearchStoreBean>>error(AppHolder.getInstance().getAppContext().getString(R.string.error_msg)));
        }
    }
}
