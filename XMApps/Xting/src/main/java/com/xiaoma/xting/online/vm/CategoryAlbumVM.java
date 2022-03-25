package com.xiaoma.xting.online.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.online.model.AlbumBean;
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.online.model.TagBean;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMAlbumList;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMTagList;

import java.util.List;

/**
 * @author KY
 * @date 2018/11/1
 */
public class CategoryAlbumVM extends AndroidViewModel {
    private static final String CACHE_PREFIX = "CategoryAlbumVM";
    private static final String CHILD_CATEGORY_CACHE_KEY_PREFIX = CACHE_PREFIX + "child_category";
    private static final String CHILD_CATEGORY_ALBUM_CACHE_KEY_PREFIX = CACHE_PREFIX + "child_category_album";
    private MutableLiveData<XmResource<List<TagBean>>> mTags;
    private MutableLiveData<XmResource<List<AlbumBean>>> mChildCategoryAlbums;
    private ViewLayoutCallBack viewLayoutCallBack;
    private boolean cacheDataEmpty;

    public CategoryAlbumVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<TagBean>>> getTags() {
        if (mTags == null) {
            mTags = new MutableLiveData<>();
        }
        return mTags;
    }

    public MutableLiveData<XmResource<List<AlbumBean>>> getChildCategoryAlbums() {
        if (mChildCategoryAlbums == null) {
            mChildCategoryAlbums = new MutableLiveData<>();
        }
        return mChildCategoryAlbums;
    }

    public void fetchChildCategoryList(final CategoryBean categoryBean) {
        getTags().setValue(XmResource.<List<TagBean>>loading());
        final String cacheKey = CHILD_CATEGORY_CACHE_KEY_PREFIX + categoryBean.getId();
        List<TagBean> caches = TPUtils.getList(getApplication(), cacheKey, TagBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getTags().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getTags(categoryBean.getId(), 0, new XMDataCallback<XMTagList>() {
            @Override
            public void onSuccess(@Nullable XMTagList data) {
                if (data != null) {
                    TPUtils.putList(getApplication(), cacheKey, data.getTagList());
                    getTags().setValue(XmResource.success(TagBean.convert(data.getTagList())));
                } else {
                    getTags().setValue(XmResource.<List<TagBean>>success(null));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getTags().setValue(XmResource.<List<TagBean>>error(code, msg));
            }
        });
    }

    public void fetchChildCategoryAlbums(long categoryId, final TagBean childCategoryBean) {
        getChildCategoryAlbums().setValue(XmResource.<List<AlbumBean>>loading());
        final String cacheKey = CHILD_CATEGORY_ALBUM_CACHE_KEY_PREFIX + categoryId + childCategoryBean.getTagName();
        List<AlbumBean> caches = TPUtils.getList(getApplication(), cacheKey, AlbumBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getChildCategoryAlbums().setValue(XmResource.success(caches));
            cacheDataEmpty = false;
        } else {
            cacheDataEmpty = true;
        }
        OnlineFMFactory.getInstance().getSDK().getAlbumList(categoryId, 3, childCategoryBean.getTagName(), 1, new XMDataCallback<XMAlbumList>() {
            @Override
            public void onSuccess(@Nullable XMAlbumList data) {
                if (data != null) {
                    List<AlbumBean> albums = AlbumBean.convert2Album(data.getAlbums());
                    TPUtils.putList(getApplication(), cacheKey, albums);
                    getChildCategoryAlbums().setValue(XmResource.response(albums));
                    if (viewLayoutCallBack!=null){
                        if (!cacheDataEmpty)
                            viewLayoutCallBack.getDataUpdateCount(2);
                        else
                            viewLayoutCallBack.getDataUpdateCount(1);
                    }
                } else {
                    getChildCategoryAlbums().setValue(XmResource.<List<AlbumBean>>failure("null"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getChildCategoryAlbums().setValue(XmResource.<List<AlbumBean>>error(code, msg));
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
