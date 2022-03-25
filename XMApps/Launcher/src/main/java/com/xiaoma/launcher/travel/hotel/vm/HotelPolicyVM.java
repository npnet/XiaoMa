package com.xiaoma.launcher.travel.hotel.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.hotel.response.HotelPolicyBean;
import com.xiaoma.utils.log.KLog;

/**
 * @author taojin
 * @date 2019/2/25
 */
public class HotelPolicyVM extends BaseViewModel {
    private static String TAG="[HotelPolicyVM]";
    private MutableLiveData<XmResource<HotelPolicyBean>> hotelPolicyData;


    public MutableLiveData<XmResource<HotelPolicyBean>> getHotelPolicyData() {
        if (hotelPolicyData == null) {
            hotelPolicyData = new MutableLiveData<>();
        }

        return hotelPolicyData;
    }

    public HotelPolicyVM(@NonNull Application application) {
        super(application);
    }


    public void fetchHotelPolicy(String hotelId) {
        getHotelPolicyData().setValue(XmResource.<HotelPolicyBean>loading());

        RequestManager.getInstance().getHotelPolicy(hotelId, new ResultCallback<XMResult<HotelPolicyBean>>() {
            @Override
            public void onSuccess(XMResult<HotelPolicyBean> result) {
                getHotelPolicyData().setValue(XmResource.success(result.getData()));
                KLog.e(TAG,"onSuccess() result.getData()= "+result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.e(TAG,"onFailure() code= "+code+" , msg="+msg);
                getHotelPolicyData().setValue(XmResource.<HotelPolicyBean>error(msg));
            }
        });

    }
}
