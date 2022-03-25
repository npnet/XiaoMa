package com.xiaoma.launcher.travel.parking.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.parking.response.ParkingInfoBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

public class ParkingVM extends BaseViewModel {
    private MutableLiveData<XmResource<List<ParkingInfoBean>>> parkingList;
    private List<ParkingInfoBean> mlist = new ArrayList<>();
    private final int limit = LauncherConstants.PAGE_SIZE;
    private LocationInfo mLocationInfo;

    public ParkingVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<ParkingInfoBean>>> getParkingList() {
        if (parkingList == null) {
            parkingList = new MutableLiveData<>();
        }
        return parkingList;
    }

    public boolean isParkingLoadEnd() {
        final MutableLiveData<XmResource<List<ParkingInfoBean>>> parkingList = getParkingList();
        if (parkingList != null && parkingList.getValue() != null) {
            final List<ParkingInfoBean> data = parkingList.getValue().data;
            if (!ListUtils.isEmpty(data)) {
                return data.size() < LauncherConstants.PAGE_SIZE;
            }
        }
        return true;
    }


    public void SearchParkingInfo(int offset) {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }
        getParkingList().setValue(XmResource.<List<ParkingInfoBean>>loading());
        RequestManager.getInstance().queryParkingInfo(mLocationInfo.getCity(), "", "", mLocationInfo.getLongitude() + "," + mLocationInfo.getLatitude(), "50000", "", limit, offset, new ResultCallback<XMResult<List<ParkingInfoBean>>>() {
            @Override
            public void onSuccess(final XMResult<List<ParkingInfoBean>> result) {
                if (!ListUtils.isEmpty(result.getData())) {
                    getParkingList().setValue(XmResource.success(result.getData()));
                } else {
                    getParkingList().setValue(XmResource.<List<ParkingInfoBean>>error(getApplication().getString(R.string.not_data)));
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                getParkingList().setValue(XmResource.<List<ParkingInfoBean>>error(msg));
            }
        });
    }

    /**
     * 获取停车场推荐
     */
    public void fetchRecommendByParking() {
        getParkingList().setValue(XmResource.<List<ParkingInfoBean>>loading());

        final List<ParkingInfoBean> parkingBeans = TPUtils.getList(AppHolder.getInstance().getAppContext(), LauncherConstants.RecommendExtras.RECOMMEND_PARKING_LIST, ParkingInfoBean[].class);

        if (!ListUtils.isEmpty(parkingBeans)) {
            getParkingList().setValue(XmResource.success(parkingBeans));
        } else {
            getParkingList().setValue(XmResource.<List<ParkingInfoBean>>error(AppHolder.getInstance().getAppContext().getString(R.string.error_msg)));
        }
    }
}
