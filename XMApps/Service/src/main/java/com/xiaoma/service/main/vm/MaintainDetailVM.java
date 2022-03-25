package com.xiaoma.service.main.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.service.common.manager.RequestManager;
import com.xiaoma.service.main.model.MaintainBean;

import java.util.List;

/**
 * Created by ZouShao on 2018/11/16 0016.
 */

public class MaintainDetailVM extends AndroidViewModel {
    private MutableLiveData<XmResource<List<MaintainBean>>> maintainList;

    public MaintainDetailVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<MaintainBean>>> getMaintainList() {
        if (maintainList == null) {
            maintainList = new MutableLiveData<>();
        }
        return maintainList;
    }

    /**
     * 获取推荐保养项目列表
     */
    public void fetchMaintainList(String vin) {
        maintainList.setValue(XmResource.<List<MaintainBean>>loading());
        RequestManager.getInstance().getRecommendUpkeepOptions(vin, new ResultCallback<XMResult<List<MaintainBean>>>() {
            @Override
            public void onSuccess(XMResult<List<MaintainBean>> result) {
                getMaintainList().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getMaintainList().setValue(XmResource.<List<MaintainBean>>error(code, msg));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        maintainList = null;
    }
}
