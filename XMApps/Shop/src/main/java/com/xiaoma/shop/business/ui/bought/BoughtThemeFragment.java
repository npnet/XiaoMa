package com.xiaoma.shop.business.ui.bought;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.bought.BoughtThemeAdapter;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.skin.SkinUsing;
import com.xiaoma.shop.business.ui.theme.ThemeDetailsFragment;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.ResourceCounter;
import com.xiaoma.shop.common.util.SkinHelper;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/29
 *     desc   : 已购买主题
 * </pre>
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_BUYED_SKIN)
public class BoughtThemeFragment extends AbsBoughtFragment<SkinVersionsBean, BoughtThemeAdapter> {

    private static final String TAG = BoughtThemeFragment.class.getSimpleName();
    private SkinHelper skinHelper;
    private OnRefreshCallback onRefreshCallback;

    public static BoughtThemeFragment newInstance() {
        return new BoughtThemeFragment();
    }

    @Override
    protected String getItemDownloadUrl(SkinVersionsBean skin) {
        return skin != null ? skin.getUrl() : "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skinHelper = new SkinHelper();
        registerRefreshCallback();
    }

    private void registerRefreshCallback() {
        SkinDownload.getInstance().addRefreshCallback(onRefreshCallback = new OnRefreshCallback() {
            @Override
            public void onSingleRefresh(long id, final String filePath) {
                if (isDestroy())
                    return;
                final BoughtThemeAdapter adapter = getAdapter();
                if (adapter == null)
                    return;
                final List<SkinVersionsBean> skinList = adapter.getData();
                SeriesAsyncWorker.create().next(new Work(Priority.NORMAL) {
                    @Override
                    public void doWork(Object lastResult) {
                        if (isDestroy())
                            return;
                        int pos = skinHelper.findPositionByUrl(skinList, filePath);
                        if (pos >= 0 && pos < skinList.size()) {
                            SkinVersionsBean skin = skinList.get(pos);
                            skin.setUsedNum(skin.getUsedNum() + 1);
                            doNext(pos);
                        }
                    }
                }).next(new Work<Integer>() {
                    @Override
                    public void doWork(Integer pos) {
                        if (isDestroy())
                            return;
                        adapter.notifyItemChanged(pos);
                    }
                }).start();
            }

            @Override
            public void onRefreshAll() {

            }
        });
    }

    @Override
    public void onDestroy() {
        SkinDownload.getInstance().removeRefreshCallback(onRefreshCallback);
        super.onDestroy();
    }

    @Override
    protected BoughtThemeAdapter createAdapter() {
        return new BoughtThemeAdapter(ImageLoader.with(this), new BoughtThemeAdapter.Callback() {
            @Override
            public void onDownloadSkin(final SkinVersionsBean skin) {
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.no_network);
                    return;
                }
                if (!skinHelper.checkCanDownload(SkinDownload.getInstance(), mActivity, false))
                    return;
                RequestManager.addSkuToBuyList(skin.getId(), ResourceType.SKIN, new ResultCallback<XMResult<Object>>() {
                    @Override
                    public void onSuccess(XMResult<Object> result) {
                        if (isDestroy())
                            return;
                        if (result != null && result.isSuccess()) {
                            SkinDownload.getInstance().start(skin);
                        } else {
                            showToastException(R.string.hint_download_error);
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        if (isDestroy())
                            return;
                        showToastException(R.string.hint_download_error);
                    }
                });
            }

            @Override
            public void onUseSkin(SkinVersionsBean skin) {
                SkinUsing.useSkin(BoughtThemeFragment.this, skin);
            }
        });
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.SKIN;
    }

    @Override
    protected int cacheBindStatus() {
        return CacheBindStatus.CLEAN_CACHE;
    }


    @Override
    public void cleanCacheOperation(boolean open) {
        getAdapter().setCleanCache(open);
    }


    @Override
    public boolean isExecutingCleanCache() {
        return getAdapter() != null && getAdapter().isCleanCache();
    }


    @Override
    public void executeCleanCache() {
        if (getContext() == null)
            return;
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                if (getOneKeyCleanCacheCallback() != null) {
                    getOneKeyCleanCacheCallback().startClean();
                }
                doNext();
            }
        }).next(new Work(Priority.HIGH) {
            @Override
            public void doWork(Object lastResult) {
                if (getContext() != null) {
                    List<SkinVersionsBean> skinList = new ArrayList<>(getAdapter().getData());
                    SkinVersionsBean[] rmSkins = new SkinVersionsBean[skinList.size()];
                    int count = 0;
                    for (final SkinVersionsBean skin : skinList) {
                        if (skin.isSelect()) {
                            rmSkins[count] = skin;
                            ++count;
                        }
                    }
                    if (count > 0)
                        SkinDownload.getInstance().remove(Arrays.copyOf(rmSkins, count));
                }
                doNext();
            }
        }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                getOneKeyCleanCacheCallback().completeClean();
                getAdapter().notifyDataSetChanged();
            }
        }).start();


        /*for (String fileName : fileNameList) {
            File file = new File(ConfigManager.FileConfig.getShopSkinDownloadFolder(), fileName);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                for (File child : files) {
                    child.delete();
                }
                file.delete();
            } else {
                file.delete();
            }
        }

        if (getOneKeyCleanCacheCallback() != null) {
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    getOneKeyCleanCacheCallback().completeClean();
                }
            }, 3000);

        }*/
    }


    @Override
    public String selectAll(boolean select) {
        return getAdapter().selectAll(select);
    }


    @Override
    protected void obtainActuallyData(boolean more, MineBought data) {
        if (data.getSkinVersions().size() == 0) {
            showEmptyView();
            return;
        }
        // 判断当前是否是全选状态
        if (((BoughtMainFragment) getParentFragment()).isAllSelected()) {
            for (SkinVersionsBean bean : data.getSkinVersions()) {
                bean.setSelect(true);
            }
        }
        if (more) {
            getAdapter().addData(data.getSkinVersions());
        } else {
            getAdapter().setNewData(data.getSkinVersions());
        }

        ((BoughtMainFragment) getParentFragment()).refreshCacheSize();
    }

    @Override
    protected void onItemClick(BaseQuickAdapter adapter, final View view, final int position) {
        if (getChangeFragmentCallback() != null) {
            XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_TO_SKIN_DETAILS,
                    this.getClass().getName(),
                    EventConstant.PageDesc.FRAGMENT_BUYED_SKIN);
            final SkinVersionsBean skinVersionsBean = getAdapter().getData().get(position);
            skinVersionsBean.setIsBuy(true);
            ThemeDetailsFragment detailsFragment = ThemeDetailsFragment.newInstance(skinVersionsBean);
            /*detailsFragment.setOnDownloadingListener(new ThemeDetailsFragment.IOnDownloadingListener() {
                @Override
                public void onStartDownload() {
                    ProgressButton progressButton = view.findViewById(R.id.bought_operation_bt);
                    resourceOperation(skinVersionsBean, progressButton, position);
                }

                @Override
                public void useThisSkin(boolean isBuy, int skinId) {
                    if (isBuy) {
                        //遍历skiID
                    }
                }

                @Override
                public void unzipThisSkin(File file) {
                    // TODO: 2019/5/14 进行解压 并进行界面显示
                }
            });*/
            getChangeFragmentCallback().changeFragment(detailsFragment);
        }
    }


    @Override
    protected void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_select_cache:
                selectCache(position);
                break;

            /*case R.id.bought_operation_bt:
                SkinVersionsBean bean = getAdapter().getData().get(position);
                ProgressButton progressButton = null;
                if (view instanceof ProgressButton) {
                    progressButton = (ProgressButton) view;
                }
                resourceOperation(bean, progressButton, position);
                break;*/
        }
    }


    private void selectCache(int position) {
        boolean select = getAdapter().selectOnlyOne(position);
        XmAutoTracker.getInstance().onEvent(select
                        ? EventConstant.NormalClick.ACTION_CACHE_INCLUDE + position
                        : EventConstant.NormalClick.ACTION_CACHE_SELECT_EXCLUDE + position,
                getClass().getName(),
                EventConstant.PageDesc.FRAGMENT_BUYED_SKIN);
        SkinVersionsBean bean = getAdapter().getData().get(position);
        DownloadStatus downloadStatus = SkinDownload.getInstance().getDownloadStatus(bean);
        File downFilePath;
        if (downloadStatus != null
                && (downFilePath = new File(downloadStatus.downFilePath)).exists()) {
            String content = ResourceCounter.getInstance().calculationSelectedFileCache(select, downFilePath);
            if (getOneKeyCleanCacheCallback() != null) {
//                getOneKeyCleanCacheCallback().selectedCacheSize(content);
                getOneKeyCleanCacheCallback().refreshCacheSize();
            }
        }
    }

    @Override
    public long calcuCacheSize() {
        List<SkinVersionsBean> boughtData = getBoughtData();
        if (ListUtils.isEmpty(boughtData)) return 0;
        File shopFolder = ConfigManager.FileConfig.getShopSkinDownloadFolder();
        if (shopFolder == null || !shopFolder.exists() || !shopFolder.isDirectory()) return 0;
        File[] files = shopFolder.listFiles();
        long size = 0;
        for (SkinVersionsBean boughtDatum : boughtData) {
            for (File file : files) {
                if (file.getName().equals(SkinDownload.getInstance().getDownloadFileName(boughtDatum))) {//只计算已购买的占用内存大小
                    size += file.length();
                }
            }
        }
        return size;
    }

    @Override
    public List<SkinVersionsBean> getBoughtData() {
        return getAdapter() != null ? getAdapter().getData() : null;
    }

    @Override
    public int calcuCanDeleteFileNum() {
        if (ListUtils.isEmpty(getBoughtData())) return 0;
        int num = 0;
        for (SkinVersionsBean bean : getBoughtData()) {
            // 不是正在使用的、已经下载的、已经选择的
            if (!SkinUsing.isUsing(bean) && SkinDownload.getInstance().getDownloadFile(bean) != null) {
                num++;
            }
        }
        return num;
    }

    @Override
    public int calcuSelectedNum() {
        if (ListUtils.isEmpty(getBoughtData())) return 0;
        int num = 0;
        for (SkinVersionsBean bean : getBoughtData()) {
            if (bean.isSelect() && SkinDownload.getInstance().getDownloadFile(bean) != null) {
                num++;
            }
        }
        return num;
    }

    @Override
    public long calcuSelectedCacheSize() {
        if (ListUtils.isEmpty(getBoughtData())) return 0;
        long size = 0;
        for (SkinVersionsBean bean : getBoughtData()) {
            if (bean.isSelect() && SkinDownload.getInstance().getDownloadFile(bean) != null && !SkinUsing.isUsing(bean)) {
                size+= SkinDownload.getInstance().getDownloadFile(bean).length();
            }
        }
        return size;
    }
}
