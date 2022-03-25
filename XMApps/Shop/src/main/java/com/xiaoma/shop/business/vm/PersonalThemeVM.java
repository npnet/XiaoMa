package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.model.personalTheme.PagedSkinsBean;
import com.xiaoma.shop.business.repository.ILoadDataListener;
import com.xiaoma.shop.business.repository.impl.SystemSkinsRepository;
import com.xiaoma.shop.common.ShopRequestProxy;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.shop.common.constant.ThemeContract;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/10
 */
public class PersonalThemeVM extends AndroidViewModel implements ILoadDataListener {

    private static final String TAG = PersonalThemeVM.class.getSimpleName();

    private MutableLiveData<XmResource<List<SkinVersionsBean>>> mSystemSkinsLiveData;
    private MutableLiveData<Integer> mLoadMoreStateLiveData;

    private ShopRequestProxy.IRequestCallback<PagedSkinsBean> mSkinsCallback = new ShopRequestProxy.IRequestCallback<PagedSkinsBean>() {
        @Override
        public void onSuccess(PagedSkinsBean pagedSkinsBean) {
            setSystemSkinsLiveData(XmResource.success(pagedSkinsBean.getSkinVersions()));
        }

        @Override
        public void onFailed() {
            setSystemSkinsLiveData(XmResource.<List<SkinVersionsBean>>failure(""));
            setLoadMoreStateLiveData(LoadMoreState.FAIL);
        }

        @Override
        public void onError(int code, String msg) {
            setSystemSkinsLiveData(XmResource.<List<SkinVersionsBean>>error(code, msg));
            setLoadMoreStateLiveData(LoadMoreState.FAIL);
        }
    };

    public PersonalThemeVM(@NonNull Application application) {
        super(application);
        SystemSkinsRepository.newSingleton().setOnLoadStateListener(this);
    }

    public void fetchSystemSkins(@ThemeContract.SortRule String sortRule, int type) {
        SystemSkinsRepository.newSingleton().refreshRepositoryInfo();
        ShopRequestProxy.requestSkins(sortRule, 0, type, mSkinsCallback);
    }

    public void loadMore() {
        if (!SystemSkinsRepository.newSingleton().loadMore(mSkinsCallback)) {
            setLoadMoreStateLiveData(LoadMoreState.END);
        }
    }

    public MutableLiveData<XmResource<List<SkinVersionsBean>>> getSystemSkinsLiveData() {
        if (mSystemSkinsLiveData == null) {
            mSystemSkinsLiveData = new MutableLiveData<>();
        }
        return mSystemSkinsLiveData;
    }

    public MutableLiveData<Integer> getLoadMoreStateLiveData() {
        if (mLoadMoreStateLiveData == null) {
            mLoadMoreStateLiveData = new MutableLiveData<>();
        }
        return mLoadMoreStateLiveData;
    }

    private void setSystemSkinsLiveData(XmResource<List<SkinVersionsBean>> value) {
        getSystemSkinsLiveData().setValue(value);
    }

    private void setLoadMoreStateLiveData(Integer state) {
        getLoadMoreStateLiveData().setValue(state);
    }

    @Override
    public void loadStateChanged(int loadState) {
        setLoadMoreStateLiveData(loadState);
    }
}
