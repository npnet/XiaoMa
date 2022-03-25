package com.xiaoma.music.mine.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.mine.model.VipListBean;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/25 0025 16:55
 *   desc:   获取vip项
 * </pre>
 */
public class VipOptionVm extends BaseViewModel {

    public VipOptionVm(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<VipListBean>> getVipOptions() {
        MutableLiveData<XmResource<VipListBean>> mutableLiveData = new MutableLiveData<>();

        RequestManager.getInstance().getKWVIPPriceList(new ResultCallback<XMResult<VipListBean>>() {
            @Override
            public void onSuccess(XMResult<VipListBean> result) {
                mutableLiveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                mutableLiveData.setValue(XmResource.error(code, msg));
            }
        });
        return mutableLiveData;
    }
}
