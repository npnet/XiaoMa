package com.xiaoma.shop.business.ui.bought;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.URLUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.VoiceToneAdapter;
import com.xiaoma.shop.business.adapter.bought.BoughtVoiceToneAdapter;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.tts.TTSDownload;
import com.xiaoma.shop.business.tts.TTSUsing;
import com.xiaoma.shop.business.ui.theme.TTSAdapterCallbackImpl;
import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.ResourceCounter;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.ListUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/29
 *     desc   : 已购买语音音色
 * </pre>
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_BUYED_VOICE)
public class BoughtVoiceToneFragment extends AbsBoughtFragment<SkusBean, BoughtVoiceToneAdapter> {
    private TTSAdapterCallbackImpl mTTSAdapterCallback;
    private DownloadListener downloadListener;

    public static BoughtVoiceToneFragment newInstance() {
        return new BoughtVoiceToneFragment();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TTSDownload.getInstance().addDownloadListener(downloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(@Nullable DownloadStatus downloadStatus) {
                if (isDestroy() || getAdapter() == null || downloadStatus == null) return;
                int position = VoiceToneAdapter.findPositionByUrl(getAdapter().getData(), downloadStatus.downUrl);
                if (position >= 0) {
                    getAdapter().notifyItemChanged(position);
                }
                if (downloadStatus.status == DownloadManager.STATUS_SUCCESSFUL) {
                    if (getParentFragment() instanceof BoughtMainFragment) {
                        if (((BoughtMainFragment) getParentFragment()).isAllSelected()) {
                            ((BoughtMainFragment) getParentFragment()).selectAll(true);
                        } else {
                            ((BoughtMainFragment) getParentFragment()).refreshCacheSize();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        TTSDownload.getInstance().removeDownloadListener(downloadListener);
        super.onDestroyView();
    }

    @Override
    protected BoughtVoiceToneAdapter createAdapter() {
        return new BoughtVoiceToneAdapter(ImageLoader.with(this), mTTSAdapterCallback = new TTSAdapterCallbackImpl(this) {
            @Override
            protected void onEventReport(String content, String eventAction) {
                XmAutoTracker.getInstance().onEvent(eventAction, content,
                        getClass().getName(),
                        EventConstant.PageDesc.ACTIVITY_MY_BUY);
            }

            @Override
            protected void onNotifyDataSetChanged() {
                if (isDestroy())
                    return;
                RecyclerView.Adapter adapter = getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onAfterBuyed(SkusBean item) {
                // 已购买页面,此方法不会回调过来
            }

            @Override
            public boolean onUseTTS(SkusBean item) {
                boolean useTTS = super.onUseTTS(item);
                if (useTTS) {
                    item.setSelect(false);
                    refreshCacheSize();
                    callNotifyDataSetChanged();
                }
                return useTTS;
            }
        });
    }

    private void refreshCacheSize() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (getParentFragment() instanceof BoughtMainFragment) {
                    if (((BoughtMainFragment) getParentFragment()).isAllSelected()) {
                        ((BoughtMainFragment) getParentFragment()).selectAll(true);
                    } else {
                        ((BoughtMainFragment) getParentFragment()).refreshCacheSize();
                    }
                }
            }
        });
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.ASSISTANT;
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
    protected void obtainActuallyData(boolean more, MineBought data) {
        if (data.getSkus().size() == 0) {
            showEmptyView();
            return;
        }
        if (more) {
            getAdapter().addData(data.getSkus());
        } else {
            getAdapter().setNewData(data.getSkus());
        }
    }


    @Override
    protected void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_select_cache:
                XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_CACHE_INCLUDE,
                        getClass().getName(),
                        EventConstant.PageDesc.FRAGMENT_BUYED_VOICE);
                selectCache(position);
                break;

            case R.id.bought_operation_bt:
                XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_USE,
                        getClass().getName(),
                        EventConstant.PageDesc.FRAGMENT_BUYED_VOICE);
                //TODO 使用
                break;
        }
    }

    private void selectCache(int position) {
        boolean select = getAdapter().selectOnlyOne(position);
        SkusBean bean = getAdapter().getData().get(position);
        DownloadStatus downloadStatus = TTSDownload.getInstance().getDownloadStatus(bean);
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
    protected String getItemDownloadUrl(SkusBean bean) {
        return bean != null ? bean.getTtsResDownloadUrl() : "";
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTTSAdapterCallback != null) {
            mTTSAdapterCallback.onStop();
        }
    }

    private void callNotifyDataSetChanged() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                if (getAdapter() != null) {
                    getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public String selectAll(boolean select) {
        return getAdapter().selectAll(select);
    }

    @Override
    public List<SkusBean> getBoughtData() {
        return getAdapter() != null ? getAdapter().getData() : null;
    }

    @Override
    public long calcuCacheSize() {
        List<SkusBean> boughtData = getBoughtData();
        String dir = TTSDownload.getInstance().getDownloadDir();
        File skusFolder = new File(dir);
        if (ListUtils.isEmpty(boughtData) || skusFolder == null || !skusFolder.isDirectory())
            return 0;
        File[] files = skusFolder.listFiles();
        long size = 0;
        for (SkusBean boughtDatum : boughtData) {
            String url = TTSDownload.getInstance().getDownloadUrl(boughtDatum);
            if (!URLUtil.isValidUrl(url)) {
                continue;
            }
            for (File file : files) {
                if (file.getName().equals(TTSDownload.getInstance().getDownloadFileName(boughtDatum))) {//只计算已购买的占用内存大小
                    size += file.length();
                }
            }
        }
        return size;
    }

    @Override
    public int calcuCanDeleteFileNum() {
        if (ListUtils.isEmpty(getBoughtData())) return 0;
        int num = 0;
        for (SkusBean bean : getBoughtData()) {
            // 不是正在使用的、已经下载的、已经选择的
            if (!TTSUsing.isTTSUsing(bean) && TTSDownload.getInstance().getDownloadFile(bean) != null) {
                num++;
            }
        }
        return num;
    }

    @Override
    public int calcuSelectedNum() {
        if (ListUtils.isEmpty(getBoughtData())) return 0;
        int num = 0;
        for (SkusBean bean : getBoughtData()) {
            if (bean.isSelect() && TTSDownload.getInstance().getDownloadFile(bean) != null) {
                num++;
            }
        }
        return num;
    }

    @Override
    public long calcuSelectedCacheSize() {
        if (ListUtils.isEmpty(getBoughtData())) return 0;
        long size = 0;
        for (SkusBean bean : getBoughtData()) {
            if (bean.isSelect() && TTSDownload.getInstance().getDownloadFile(bean) != null && !TTSUsing.isTTSUsing(bean)) {
                size += TTSDownload.getInstance().getDownloadFile(bean).length();
            }
        }
        return size;
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
                    List<SkusBean> skuList = new ArrayList<>(getAdapter().getData());
                    SkusBean[] rmSkus = new SkusBean[skuList.size()];
                    int count = 0;
                    for (final SkusBean sku : skuList) {
                        if (sku.isSelect()) {
                            rmSkus[count] = sku;
                            ++count;
                        }
                    }
                    if (count > 0)
                        TTSDownload.getInstance().remove(Arrays.copyOf(rmSkus, count));
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
    }
}
