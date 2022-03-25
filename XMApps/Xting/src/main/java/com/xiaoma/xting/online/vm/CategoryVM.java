package com.xiaoma.xting.online.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMCategoryList;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRadioCategoryList;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class CategoryVM extends AndroidViewModel {

    private MutableLiveData<XmResource<List<CategoryBean>>> mAlbumCategories;
    private MutableLiveData<XmResource<List<CategoryBean>>> mRadioCategories;
    private static final String CACHE_PREFIX = "CategoryVM";
    private static final String CATEGORY_ALBUM_CACHE_KEY = CACHE_PREFIX + "album_category";
    private static final String CATEGORY_RADIO_CACHE_KEY = CACHE_PREFIX + "radio_category";
    private ViewLayoutCallBack viewLayoutCallBack;
    private boolean cacheDataEmpty;

    public CategoryVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<CategoryBean>>> getAlbumCategories() {
        if (mAlbumCategories == null) {
            mAlbumCategories = new MutableLiveData<>();
        }
        return mAlbumCategories;
    }

    public MutableLiveData<XmResource<List<CategoryBean>>> getRadioCategories() {
        if (mRadioCategories == null) {
            mRadioCategories = new MutableLiveData<>();
        }
        return mRadioCategories;
    }

    public void fetchAlbumCategory() {
        getAlbumCategories().setValue(XmResource.<List<CategoryBean>>loading());
        List<CategoryBean> caches = TPUtils.getList(getApplication(), CATEGORY_ALBUM_CACHE_KEY, CategoryBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getAlbumCategories().setValue(XmResource.success(caches));
            if (NetworkUtils.isConnected(getApplication())) {
                fetchNetAlbumCategory();
            } else {
                if (viewLayoutCallBack != null)
                    viewLayoutCallBack.getDataUpdateCount(1);
            }
        } else {
            cacheDataEmpty = true;
            if (NetworkUtils.isConnected(getApplication())) {
                fetchNetAlbumCategory();
            } else {
                getAlbumCategories().setValue(XmResource.<List<CategoryBean>>error(XtingConstants.ErrorMsg.NO_NETWORK));
            }
        }
    }

    private void fetchNetAlbumCategory() {
        OnlineFMFactory.getInstance().getSDK().getCategories(new XMDataCallback<XMCategoryList>() {
            @Override
            public void onSuccess(@Nullable XMCategoryList data) {
                if (data != null) {
                    List<CategoryBean> albums = CategoryBean.convertFromAlbum(data);
                    getAlbumCategories().setValue(XmResource.success(albums));
                    TPUtils.putList(getApplication(), CATEGORY_ALBUM_CACHE_KEY, albums);
                    if (viewLayoutCallBack!=null){
                        if (!cacheDataEmpty)
                            viewLayoutCallBack.getDataUpdateCount(2);
                        else
                            viewLayoutCallBack.getDataUpdateCount(1);
                    }
                } else {
                    getAlbumCategories().setValue(XmResource.<List<CategoryBean>>success(null));
                    if (viewLayoutCallBack != null) {
                        if (!cacheDataEmpty)
                            viewLayoutCallBack.getDataUpdateCount(1);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                getAlbumCategories().setValue(XmResource.<List<CategoryBean>>error(code, msg));
            }
        });
    }

    public void fetchRadioCategory() {
        getRadioCategories().setValue(XmResource.<List<CategoryBean>>loading());
        List<CategoryBean> caches = TPUtils.getList(getApplication(), CATEGORY_RADIO_CACHE_KEY, CategoryBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getRadioCategories().setValue(XmResource.success(caches));
            if (NetworkUtils.isConnected(getApplication())) {
                fetchNetRadioCategory();
            }
        } else {
            if (NetworkUtils.isConnected(getApplication())) {
                fetchNetRadioCategory();
            } else {
                getRadioCategories().setValue(XmResource.<List<CategoryBean>>error(XtingConstants.ErrorMsg.NO_NETWORK));
            }
        }
    }

    private void fetchNetRadioCategory() {
        OnlineFMFactory.getInstance().getSDK().getRadioCategory(new XMDataCallback<XMRadioCategoryList>() {
            @Override
            public void onSuccess(@Nullable XMRadioCategoryList data) {
                if (data != null && data.getRadioCategories() != null && data.getRadioCategories().size() > 0) {
                    List<CategoryBean> convertFromRadio = CategoryBean.convertFromRadio(data);
                    convertFromRadio.add(0, new CategoryBean(getApplication().getResources().getString(R.string.radio_network), 0L, 1));
                    convertFromRadio.add(0, new CategoryBean(getApplication().getResources().getString(R.string.radio_province), 0L, 1));
                    convertFromRadio.add(0, new CategoryBean(getApplication().getResources().getString(R.string.radio_country), 0L, 1));
                    TPUtils.putList(getApplication(), CATEGORY_RADIO_CACHE_KEY, convertFromRadio);
                    getRadioCategories().setValue(XmResource.success(convertFromRadio));
                } else {
                    getRadioCategories().setValue(XmResource.<List<CategoryBean>>failure(XtingConstants.ErrorMsg.NULL_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getRadioCategories().setValue(XmResource.<List<CategoryBean>>error(code, msg));
            }
        });
    }

    public interface ViewLayoutCallBack {
        void getDataUpdateCount(int count);
    }

    public void setViewLayoutCallBack(ViewLayoutCallBack callBack) {
        this.viewLayoutCallBack = callBack;
    }
}



