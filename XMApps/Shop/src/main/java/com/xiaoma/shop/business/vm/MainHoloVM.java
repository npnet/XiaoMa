package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.personalTheme.PagedHologramBean;
import com.xiaoma.shop.business.repository.impl.HologramRepository;
import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.shop.common.constant.ThemeContract;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/20
 */
public class MainHoloVM extends AndroidViewModel {
    private MutableLiveData<XmResource<List<HoloListModel>>> mHoloListLiveData;
    private MutableLiveData<Integer> mLoadMoreStateLiveData;
    private ShopRequestProxy.IRequestCallback<PagedHologramBean> mCallback = new ShopRequestProxy.IRequestCallback<PagedHologramBean>() {
        @Override
        public void onSuccess(PagedHologramBean pagedSkinsBean) {
            Log.d("Jir", "onSuccess: ");
            setHoloListLiveData(XmResource.success(pagedSkinsBean.getSkinVersions()));
        }

        @Override
        public void onFailed() {
            setHoloListLiveData(XmResource.<List<HoloListModel>>failure(""));
            setLoadMoreStateLiveData(LoadMoreState.FAIL);
        }

        @Override
        public void onError(int code, String msg) {
            setHoloListLiveData(XmResource.<List<HoloListModel>>error(code, msg));
            setLoadMoreStateLiveData(LoadMoreState.FAIL);
        }
    };

    public MainHoloVM(@NonNull Application application) {
        super(application);
    }

    public void fetchHolograms(@ThemeContract.SortRule String sortRule, int type) {
        HologramRepository.newSingleton().refreshRepositoryInfo();
        ShopRequestProxy.requestHolograms(sortRule, 0, type, mCallback);
    }

    public void loadMore() {
        if (!HologramRepository.newSingleton().loadMore(mCallback)) {
            setLoadMoreStateLiveData(LoadMoreState.END);
        }
    }

    public MutableLiveData<XmResource<List<HoloListModel>>> getHoloListLiveData() {
        if (mHoloListLiveData == null) {
            mHoloListLiveData = new MutableLiveData<>();
        }
        return mHoloListLiveData;
    }

    private void setHoloListLiveData(XmResource<List<HoloListModel>> value) {
        getHoloListLiveData().setValue(value);
    }

    public MutableLiveData<Integer> getLoadMoreStateLiveData() {
        if (mLoadMoreStateLiveData == null) {
            mLoadMoreStateLiveData = new MutableLiveData<>();
        }
        return mLoadMoreStateLiveData;
    }

    private void setLoadMoreStateLiveData(Integer state) {
        getLoadMoreStateLiveData().setValue(state);
    }
}
