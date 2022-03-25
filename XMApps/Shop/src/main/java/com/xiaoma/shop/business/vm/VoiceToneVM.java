package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.model.personalTheme.PagedVoicesBean;
import com.xiaoma.shop.business.repository.impl.VoiceTonesRepository;
import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.shop.common.constant.ThemeContract;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/18
 */
public class VoiceToneVM extends AndroidViewModel {
    private MutableLiveData<Integer> mLoadMoreStateLiveData;
    private MutableLiveData<XmResource<List<SkusBean>>> mSkuLists;
    private ShopRequestProxy.IRequestCallback<PagedVoicesBean> mCallback = new ShopRequestProxy.IRequestCallback<PagedVoicesBean>() {
        @Override
        public void onSuccess(PagedVoicesBean pagedSkinsBean) {
            setSkuLists(XmResource.success(pagedSkinsBean.getSkus()));
        }

        @Override
        public void onFailed() {
            setSkuLists(XmResource.<List<SkusBean>>failure(""));
            setLoadMoreStateLiveData(LoadMoreState.FAIL);
        }

        @Override
        public void onError(int code, String msg) {
            setSkuLists(XmResource.<List<SkusBean>>error(code, msg));
            setLoadMoreStateLiveData(LoadMoreState.FAIL);
        }
    };

    public VoiceToneVM(@NonNull Application application) {
        super(application);
    }

    public void fetchVoiceTones(@ThemeContract.SortRule String sortRule) {
        VoiceTonesRepository.newSingleton().refreshRepositoryInfo();
        ShopRequestProxy.requestVoices(sortRule, 0, mCallback);
    }

    public void loadMore() {
        if (!VoiceTonesRepository.newSingleton().loadMore(mCallback)) {
            setLoadMoreStateLiveData(LoadMoreState.END);
        }
    }

    public MutableLiveData<Integer> getLoadMoreStateLiveData() {
        if (mLoadMoreStateLiveData == null) {
            mLoadMoreStateLiveData = new MutableLiveData<>();
        }
        return mLoadMoreStateLiveData;
    }

    private void setLoadMoreStateLiveData(Integer value) {
        getLoadMoreStateLiveData().setValue(value);
    }

    public MutableLiveData<XmResource<List<SkusBean>>> getSkuLists() {
        if (mSkuLists == null) {
            mSkuLists = new MutableLiveData<>();
        }
        return mSkuLists;
    }

    private void setSkuLists(XmResource<List<SkusBean>> value) {
        getSkuLists().setValue(value);
    }
}
