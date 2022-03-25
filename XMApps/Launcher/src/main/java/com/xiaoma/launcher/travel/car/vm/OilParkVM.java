package com.xiaoma.launcher.travel.car.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.parking.response.ParkInfoBean;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * @author taojin
 * @date 2019/5/8
 */
public class OilParkVM extends BaseCollectVM {
    private static final String TAG="[OilParkVM]";
    private MutableLiveData<XmResource<List<ParkInfoBean>>> oilList;
    private MutableLiveData<XmResource<List<ParkInfoBean>>> parkList;
    private final String OIL = "加油站";
    private final String PARK = "停车场";
    private final String FAILURE_MSG = "搜索失败";
    private final int limit = LauncherConstants.PAGE_SIZE;

    public OilParkVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<ParkInfoBean>>> getOilList() {
        if (oilList == null) {
            oilList = new MutableLiveData<>();
        }
        return oilList;
    }

    public MutableLiveData<XmResource<List<ParkInfoBean>>> getParkList() {

        if (parkList == null) {
            parkList = new MutableLiveData<>();
        }

        return parkList;
    }

    public void queryNearByOilPark(String loaction,String city,String poiType,String type,int offset) {
        if (OIL.equals(poiType)) {
            searchGasStationInfo(loaction,type,offset);
        } else if (PARK.equals(poiType)) {
            searchParkingInfo(loaction,city,type,offset);
        }
    }

    private void searchParkingInfo(String loaction,String city,String type,int offset) {
        getParkList().setValue(XmResource.<List<ParkInfoBean>>loading());
        RequestManager.getInstance().queryParkingInfoNew(type,city, "", PARK, loaction, "50000", "", limit, offset, new ResultCallback<XMResult<List<ParkInfoBean>>>() {
            @Override
            public void onSuccess(final XMResult<List<ParkInfoBean>> result) {
                if (!ListUtils.isEmpty(result.getData())) {
                    KLog.e(TAG,"searchParkingInfo() onSuccess result="+result.getData());
                    getParkList().setValue(XmResource.success(result.getData()));
                } else {
                    getParkList().setValue(XmResource.<List<ParkInfoBean>>failure(getApplication().getString(R.string.not_data)));
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.e(TAG,"searchParkingInfo() onFailure code="+code+" ,msg="+msg);
                getParkList().setValue(XmResource.<List<ParkInfoBean>>failure(msg));
            }
        });
    }

    private void searchGasStationInfo(String loaction,String type,int offset){
        getOilList().setValue(XmResource.<List<ParkInfoBean>>loading());
        RequestManager.getInstance().queryGasStationInfo(type,OIL,loaction,"50000",limit, offset, new ResultCallback<XMResult<List<ParkInfoBean>>>() {
            @Override
            public void onSuccess(final XMResult<List<ParkInfoBean>> result) {
                if (!ListUtils.isEmpty(result.getData())) {
                    getOilList().setValue(XmResource.success(result.getData()));
                } else {
                    getOilList().setValue(XmResource.<List<ParkInfoBean>>failure(getApplication().getString(R.string.not_data)));
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                getOilList().setValue(XmResource.<List<ParkInfoBean>>failure(msg));
            }
        });

    }

    public boolean isDeliciousLoadEnd(String poiType) {
        MutableLiveData<XmResource<List<ParkInfoBean>>> deliciousList;
        if(OIL.equals(poiType)){
            deliciousList = getOilList();
        }else{
            deliciousList = getParkList();
        }
        if (deliciousList != null && deliciousList.getValue() != null) {
            final List<ParkInfoBean> data = deliciousList.getValue().data;
            if (!ListUtils.isEmpty(data)) {
                return data.size() < LauncherConstants.PAGE_SIZE;
            }
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        oilList = null;
        parkList = null;
    }
}
