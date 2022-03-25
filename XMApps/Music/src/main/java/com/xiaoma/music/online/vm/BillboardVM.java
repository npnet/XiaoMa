package com.xiaoma.music.online.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.ICallBack;
import com.xiaoma.music.common.manager.CacheControlManager;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMBillboardInfoCache;
import com.xiaoma.music.kuwo.model.XMListType;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.online.model.BillboardBean;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.BillboardInfo;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class BillboardVM extends BaseViewModel {

    public static final int PAGE_COUNT = 300;
    public static final int PLAY_COUNT = 5;
    private MutableLiveData<XmResource<List<BillboardBean>>> mBillboards;
    private MutableLiveData<Integer> mPlayPosition;

    public BillboardVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<BillboardBean>>> getBillboards() {
        if (mBillboards == null) {
            mBillboards = new MutableLiveData<>();
        }
        return mBillboards;
    }

    public MutableLiveData<Integer> getPlayPosition() {
        if (mPlayPosition == null) {
            mPlayPosition = new MutableLiveData<>();
        }
        return mPlayPosition;
    }

    public void playBillboard(int position) {
        BillboardBean billboardBean = null;
        final XmResource<List<BillboardBean>> value = getBillboards().getValue();
        if (value != null) {
            final List<BillboardBean> billboardBeans = value.data;
            if (billboardBeans != null) {
                billboardBean = billboardBeans.get(position);
            }
        }
        if (billboardBean != null) {
            final XMBillboardInfo xmBillboardInfo = billboardBean.getXmBillboardInfo();
            final BillboardInfo billboardInfo = xmBillboardInfo.getSDKBean();
            OnlineMusicFactory.getKWAudioFetch().fetchBillboardMusic(xmBillboardInfo, 0, PLAY_COUNT,
                    new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                        @Override
                        public void onFetchSuccess(final List<XMMusic> xmMusics) {
                            KwPlayInfoManager.getInstance().setCurrentPlayInfo(billboardInfo.getId()
                                    + billboardInfo.getName(), KwPlayInfoManager.AlbumType.BILLBOARD);
                            OnlineMusicFactory.getKWPlayer().play(xmMusics, 0);
                            AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                            getPlayPosition().postValue(position);
                        }

                        @Override
                        public void onFetchFailed(String msg) {
                            getPlayPosition().postValue(-1);
                        }
                    });
            OnlineMusicFactory.getKWAudioFetch().fetchBillboardMusic(xmBillboardInfo, 0, PAGE_COUNT,
                    new OnAudioFetchListener<List<XMMusic>>() {
                        @Override
                        public void onFetchSuccess(final List<XMMusic> xmMusics) {
                            boolean isPlay = KwPlayInfoManager.getInstance().isCurrentPlayInfo(billboardInfo.getId()
                                    + billboardInfo.getName(), KwPlayInfoManager.AlbumType.BILLBOARD);
                            if (!ListUtils.isEmpty(xmMusics) && xmMusics.size() > PLAY_COUNT && isPlay) {
                                List<XMMusic> playList = new ArrayList<>(xmMusics.subList(PLAY_COUNT, xmMusics.size()));
                                OnlineMusicFactory.getMusicListControl().insertMusic(XMListType.LIST_TEMPORARY.getTypeName(), playList);
                            }
                        }

                        @Override
                        public void onFetchFailed(String msg) {
                        }
                    });
        }
    }

    public void fetchData() {
        getBillboards().setValue(XmResource.<List<BillboardBean>>loading());
        SeriesAsyncWorker.create().next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                fetchCache(new ICallBack() {
                    @Override
                    public void onSuccess(boolean haveCache) {
                        doNext(haveCache);
                    }

                    @Override
                    public void onFailed(boolean haveCache) {
                        doNext(haveCache);
                    }
                });

            }
        }).next(new Work<Boolean>() {
            @Override
            public void doWork(Boolean lastResult) {
                requestFromKW(lastResult);
            }
        }).start();
    }

    private void requestFromKW(boolean haveCache) {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                OnlineMusicFactory.getKWAudioFetch().fetchBillBroad(new OnAudioFetchListener<List<XMBillboardInfo>>() {
                    @Override
                    public void onFetchSuccess(List<XMBillboardInfo> xmBillboardInfoList) {
                        if (xmBillboardInfoList != null && !xmBillboardInfoList.isEmpty()) {
                            KLog.i("Billboard from kw success: " + xmBillboardInfoList.size());
                            getBillboards().setValue(XmResource.response(BillboardBean.convert2Billboard(xmBillboardInfoList)));
                            saveNewCache(xmBillboardInfoList);
                        } else {
                            getBillboards().setValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                        }
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        KLog.d(msg);
                        if (!haveCache) {
                            getBillboards().setValue(XmResource.<List<BillboardBean>>error(msg));
                        }
                    }
                });
            }
        });
    }

    private void saveNewCache(final List<XMBillboardInfo> xmBillboardInfoList) {
        CacheControlManager.<XMBillboardInfoCache, XMBillboardInfo>getInstance().saveCache(xmBillboardInfoList, new XMBillboardInfoCache(), new
                ArrayList<XMBillboardInfoCache>());
    }

    private void fetchCache(ICallBack callBack) {
        CacheControlManager.<XMBillboardInfoCache, XMBillboardInfo>getInstance().getCache(new CacheControlManager.CacheCallBack<XMBillboardInfo>() {
            @Override
            public void onSuccess(List<XMBillboardInfo> list) {
                if (list != null && !list.isEmpty()) {
                    getBillboards().postValue(XmResource.response(BillboardBean.convert2Billboard(list)));
                    callBack.onSuccess(true);
                }
            }

            @Override
            public void onFailed() {
                callBack.onFailed(false);
            }
        }, XMBillboardInfoCache.class);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mBillboards = null;
        mPlayPosition = null;
    }
}
