package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/18 0018 19:20
 *   desc:   我的购买业务
 * </pre>
 */
public class MineBoughtVm extends AndroidViewModel {


    public MineBoughtVm(@NonNull Application application) {
        super(application);
    }

    int test = 0;

    public MutableLiveData<XmResource<MineBought>> getMineBought(final @ResourceType int type, long uid, int page, int size) {
        final MutableLiveData<XmResource<MineBought>> mutableLiveData = new MutableLiveData<>();
        RequestManager.boughtList(type, uid, page, size, new ResultCallback<XMResult<MineBought>>() {
            @Override
            public void onSuccess(XMResult<MineBought> result) {
                mutableLiveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w("code: " + code + "\nmsg: " + msg);
                mutableLiveData.setValue(XmResource.<MineBought>error(msg));
            }
        });
        return mutableLiveData;
    }

}
