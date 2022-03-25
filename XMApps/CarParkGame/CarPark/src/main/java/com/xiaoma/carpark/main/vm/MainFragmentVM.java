package com.xiaoma.carpark.main.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.carpark.common.manager.RequestManager;
import com.xiaoma.carpark.main.model.XMPluginInfo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;

import java.util.List;

/**
 * Created by zhushi.
 * Date: 2019/4/11
 */
public class MainFragmentVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<XMPluginInfo>>> mPluginInfoList;

    public MainFragmentVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<XMPluginInfo>>> getPluginInfoList() {
        if (mPluginInfoList == null) {
            mPluginInfoList = new MutableLiveData<>();
        }
        return mPluginInfoList;
    }

    public void fetchPluginInfoList(int type) {
        getPluginInfoList().setValue(XmResource.<List<XMPluginInfo>>loading());
        RequestManager.getInstance().getXMPluginInfos(type, new ResultCallback<XMResult<List<XMPluginInfo>>>() {
            @Override
            public void onSuccess(XMResult<List<XMPluginInfo>> result) {
                getPluginInfoList().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getPluginInfoList().setValue(XmResource.<List<XMPluginInfo>>error(code, msg));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPluginInfoList = null;
    }
}
