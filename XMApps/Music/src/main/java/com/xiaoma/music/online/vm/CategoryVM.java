package com.xiaoma.music.online.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.manager.CacheControlManager;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.online.model.Category;
import com.xiaoma.music.online.model.CategoryCache;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.TemplateAreaInfo;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class CategoryVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<Category>>> mCategoryList;

    private boolean haveCache;

    public CategoryVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<Category>>> getCategoryList() {
        if (mCategoryList == null) {
            mCategoryList = new MutableLiveData<>();
        }
        return mCategoryList;
    }

    public void initVM() {
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                CacheControlManager.<CategoryCache, Category>getInstance().getCache(new CacheControlManager.CacheCallBack<Category>() {
                    @Override
                    public void onSuccess(List<Category> list) {
                        if (list != null && !list.isEmpty()) {
                            getCategoryList().postValue(XmResource.response(list));
                            haveCache = true;
                        }
                        doNext();
                    }

                    @Override
                    public void onFailed() {
                        haveCache = false;
                        getCategoryList().postValue(XmResource.<List<Category>>loading());
                        doNext();
                    }
                }, CategoryCache.class);

            }
        }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                requestFromKW();
            }
        }).start();
    }

    private void requestFromKW() {
        OnlineMusicFactory.getKWAudioFetch().fetchMusicCategories(new OnAudioFetchListener<List<XMBaseQukuItem>>() {
            @Override
            public void onFetchSuccess(List<XMBaseQukuItem> listInfo) {
                if (listInfo == null || listInfo.isEmpty()) {
                    if (!haveCache) {
                        getCategoryList().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                    } else {
                        if (GuideDataHelper.shouldShowGuide(getApplication(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
                            EventBus.getDefault().post(1, "music_data_update");
                    }
                    return;
                }
                List<Category> categoryList = new ArrayList<>();
                for (XMBaseQukuItem xmBaseQukuItem : listInfo) {
                    if (xmBaseQukuItem == null) {
                        continue;
                    }
                    BaseQukuItem sdkBean = xmBaseQukuItem.getSDKBean();
                    if (sdkBean instanceof TemplateAreaInfo) {
                        continue;
                    }
                    categoryList.add(new Category(xmBaseQukuItem.getName(), xmBaseQukuItem));
                }
                getCategoryList().postValue(XmResource.response(categoryList));
                CacheControlManager.<CategoryCache, Category>getInstance().saveCache(categoryList, new CategoryCache(), new ArrayList<CategoryCache>());
                if (GuideDataHelper.shouldShowGuide(getApplication(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false)) {
                    if (haveCache)
                        EventBus.getDefault().post(2, "music_data_update");
                    else
                        EventBus.getDefault().post(1, "music_data_update");
                }
            }

            @Override
            public void onFetchFailed(String msg) {
                if (!haveCache) {
                    getCategoryList().postValue(XmResource.<List<Category>>error(msg));
                }
                EventBus.getDefault().post(0, "finish_guide");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCategoryList = null;
    }
}
