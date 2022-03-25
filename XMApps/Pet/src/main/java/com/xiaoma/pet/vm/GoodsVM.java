package com.xiaoma.pet.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.model.RepositoryInfo;
import com.xiaoma.pet.model.StoreGoodsInfo;

import java.util.List;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc: 获取宠物食物
 */
public class GoodsVM extends BaseViewModel {

    public GoodsVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<StoreGoodsInfo>>> getStoreFoodList(String goodsType) {
        final MutableLiveData<XmResource<List<StoreGoodsInfo>>> liveData = new MutableLiveData<>();
        RequestManager.getShopPetGoods(goodsType, new ResultCallback<XMResult<List<StoreGoodsInfo>>>() {
            @Override
            public void onSuccess(XMResult<List<StoreGoodsInfo>> result) {
                liveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                liveData.setValue(XmResource.<List<StoreGoodsInfo>>failure(msg));
            }
        });
        return liveData;
    }


    public MutableLiveData<XmResource<List<RepositoryInfo>>> getRepositoryFoodList(String goodsType) {
        final MutableLiveData<XmResource<List<RepositoryInfo>>> liveData = new MutableLiveData<>();
        RequestManager.getRepositoryPetGoods(goodsType, new ResultCallback<XMResult<List<RepositoryInfo>>>() {
            @Override
            public void onSuccess(XMResult<List<RepositoryInfo>> result) {
                liveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                liveData.setValue(XmResource.<List<RepositoryInfo>>failure(msg));
            }
        });
        return liveData;
    }


}
