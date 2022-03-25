package com.xiaoma.launcher.service.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.service.model.NewServiceBean;
import com.xiaoma.launcher.service.model.ServiceBean;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/1/10
 */
public class ServiceFragmentVM extends BaseViewModel {
    private final static String TAG = "[ServiceFragmentVM]";
    public static final String CACHE_SERVICE_LIST_KEY = "cache_service_list_key";
    private MutableLiveData<XmResource<List<ServiceBean>>> mServices;
    private MutableLiveData<XmResource<List<NewServiceBean>>> mNewServiceBean;
    private String[] cars;
    private String[] foods;
    private String[] movies;
    private String[] hotels;
    private String[] attractions;

    public ServiceFragmentVM(@NonNull Application application) {
        super(application);
        cars = application.getResources().getStringArray(R.array.travel_car);
        foods = application.getResources().getStringArray(R.array.travel_food);
        movies = application.getResources().getStringArray(R.array.travel_movie);
        hotels = application.getResources().getStringArray(R.array.travel_hotel);
        attractions = application.getResources().getStringArray(R.array.travel_attractions);
    }

    public MutableLiveData<XmResource<List<ServiceBean>>> getmServices() {
        if (mServices == null) {
            mServices = new MutableLiveData<>();
        }
        return mServices;
    }

    public MutableLiveData<XmResource<List<NewServiceBean>>> getmServicesList() {
        if (mNewServiceBean == null) {
            mNewServiceBean = new MutableLiveData<>();
        }
        return mNewServiceBean;
    }

    public void fetchServices() {
        getmServices().setValue(XmResource.<List<ServiceBean>>loading());
        initTravels();
    }

    private void initTravels() {
        List<ServiceBean> serviceBeans = new ArrayList<>();
        boolean isNeedPeriod = TPUtils.get(getApplication(), LauncherConstants.IS_NEED_PERIOD, false);
        if (isNeedPeriod) {
            cars[4] = getApplication().getString(R.string.fragment_car_service_need_period);
        } else {
            cars[4] = getApplication().getString(R.string.fragment_car_service_no_need_period);
        }
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_CAR_MAINTENANCE,
                cars[4],
                EventConstants.PagePath.ServiceFragment,
                EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
        serviceBeans.add(new ServiceBean(cars[0], R.drawable.bg_car, cars[1], R.drawable.bg_nearby_oil, R.drawable.bg_nearby_park, R.drawable.bg_car_keep, -1, cars[2], cars[3], cars[4], "", 0, -1));
        serviceBeans.add(new ServiceBean(foods[0], R.drawable.bg_food, foods[1], R.drawable.bg_nearby_food, R.drawable.bg_food_sort, R.drawable.bg_food_collect, -1, foods[2], foods[3], foods[4], "", 2, 1));
        serviceBeans.add(new ServiceBean(movies[0], R.drawable.bg_movie, movies[1], R.drawable.bg_nearby_cinema, R.drawable.bg_hot_movie, R.drawable.bg_film_order, R.drawable.bg_film_collect, movies[2], movies[3], movies[4], movies[5], 1, -1));
        serviceBeans.add(new ServiceBean(hotels[0], R.drawable.bg_hotel, hotels[1], R.drawable.bg_nearby_hotel, R.drawable.bg_hotel_order, R.drawable.bg_hotel_collect, -1, hotels[2], hotels[3], hotels[4], "", 2, 2));
        serviceBeans.add(new ServiceBean(attractions[0], R.drawable.bg_scenic, attractions[1], R.drawable.bg_nearby_scenic, R.drawable.bg_scenic_sort, R.drawable.bg_scenic_collect, -1, attractions[2], attractions[3], attractions[4], "", 2, 3));

        getmServices().setValue(XmResource.success(serviceBeans));
    }

    public void fetchServiceList() {
        getmServicesList().setValue(XmResource.<List<NewServiceBean>>loading());
        loadLocalData();
        RequestManager.getInstance().pushServiceList(new ResultCallback<XMResult<List<NewServiceBean>>>() {

            @Override
            public void onSuccess(XMResult<List<NewServiceBean>> result) {
                KLog.e(TAG, "fetchServiceList() onSuccess() result.data=" + result.getData());
                try {
                    if (!ListUtils.isEmpty(result.getData())) {
                        getmServicesList().setValue(XmResource.success(result.getData()));
                        TPUtils.put(getApplication(), CACHE_SERVICE_LIST_KEY, GsonHelper.toJson(result.getData()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loadLocalData();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.e(TAG, "fetchServiceList() code=" + code + " , msg=" + msg);
                loadLocalData(msg);
            }
        });
    }

    private void loadLocalData() {
        loadLocalData("");
    }

    private void loadLocalData(String msg) {
        try {
            String json = TPUtils.get(getApplication(), CACHE_SERVICE_LIST_KEY, "");
            if (!TextUtils.isEmpty(json)) {
                List<NewServiceBean> data = GsonHelper.fromJsonToList(json, NewServiceBean[].class);
                if (!ListUtils.isEmpty(data)) {
                    getmServicesList().setValue(XmResource.success(data));
                    return;
                }
            }
            String moreAppPath = "config/ServiceList.json";
            String textFromAsset = AssetUtils.getTextFromAsset(getApplication(), moreAppPath);
            XMResult<List<NewServiceBean>> result = GsonHelper.fromJson(textFromAsset, new TypeToken<XMResult<List<NewServiceBean>>>() {
            }.getType());
            if (result != null && !ListUtils.isEmpty(result.getData())) {
                getmServicesList().setValue(XmResource.success(result.getData()));
                TPUtils.put(getApplication(), CACHE_SERVICE_LIST_KEY, GsonHelper.toJson(result.getData()));
            } else {
                getmServicesList().setValue(XmResource.failure(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            getmServicesList().setValue(XmResource.failure(msg));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mServices = null;
    }
}
