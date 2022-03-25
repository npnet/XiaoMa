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
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMRadioInfo;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.online.model.RecommendModel;
import com.xiaoma.music.online.model.RecommendModelCache;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.AlbumInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.RadioInfo;
import cn.kuwo.base.bean.quku.SongListInfo;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class RecommendVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<RecommendModel>>> mRecommends;
    private MutableLiveData<Integer> playSuccess;
    private int requestCount = 0;

    public RecommendVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<RecommendModel>>> getRecommends() {
        if (mRecommends == null) {
            mRecommends = new MutableLiveData<>();
        }
        return mRecommends;
    }

    public MutableLiveData<Integer> getPlaySuccess() {
        if (playSuccess == null) {
            playSuccess = new MutableLiveData<>();
        }
        return playSuccess;
    }

    public void fetchRecommend() {
        getRecommends().setValue(XmResource.<List<RecommendModel>>loading());
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
            public void doWork(Boolean haveCache) {
                requestFromKW(haveCache);
            }
        }).start();
    }

    private void requestFromKW(Boolean haveCache) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                OnlineMusicFactory.getKWAudioFetch().fetchRecommendSongList(new OnAudioFetchListener<List<XMBaseQukuItem>>() {
                    @Override
                    public void onFetchSuccess(List<XMBaseQukuItem> xmBaseQukuItems) {
                        if (xmBaseQukuItems == null || xmBaseQukuItems.isEmpty()) {
                            if (!haveCache) {
                                if (requestCount <= 3) {
                                    requestFromKW(haveCache);
                                    requestCount++;
                                } else {
                                    requestCount = 0;
                                    getRecommends().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                                }
//                                getRecommends().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                            }
                            return;
                        }
                        requestCount = 0;
                        List<RecommendModel> modelList = new ArrayList<>();
                        for (XMBaseQukuItem xmBaseQukuItem : xmBaseQukuItems) {
                            if (xmBaseQukuItem == null || xmBaseQukuItem.getSDKBean() == null) {
                                continue;
                            }
                            BaseQukuItem sdkBean = xmBaseQukuItem.getSDKBean();
                            String name = sdkBean.getName();
                            String imageUrl = sdkBean.getImageUrl();
                            modelList.add(new RecommendModel(name, imageUrl, xmBaseQukuItem));
                        }
                        getRecommends().postValue(XmResource.response(modelList));
                        saveNewCache(modelList);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        if (!haveCache) {
                            if (requestCount <= 3) {
                                requestFromKW(haveCache);
                                requestCount++;
                            } else {
                                requestCount = 0;
                                getRecommends().postValue(XmResource.<List<RecommendModel>>error(msg));
                            }
//                            getRecommends().postValue(XmResource.<List<RecommendModel>>error(msg));
                        } else {
                            requestCount = 0;
                        }
                    }
                });
            }
        });
    }

    private void saveNewCache(List<RecommendModel> modelList) {
        CacheControlManager.<RecommendModelCache, RecommendModel>getInstance().saveCache(modelList, new RecommendModelCache(), new
                ArrayList<RecommendModelCache>());
    }

    private void fetchCache(ICallBack callBack) {
        CacheControlManager.<RecommendModelCache, RecommendModel>getInstance().getCache(new CacheControlManager.CacheCallBack<RecommendModel>() {
            @Override
            public void onSuccess(List<RecommendModel> list) {
                if (list != null && !list.isEmpty()) {
                    getRecommends().postValue(XmResource.response(list));
                    callBack.onSuccess(true);
                }
            }

            @Override
            public void onFailed() {
                callBack.onFailed(false);
            }
        }, RecommendModelCache.class);
    }


    public void play(int position) {
        final XmResource<List<RecommendModel>> value = getRecommends().getValue();
        if (value != null && value.data != null) {
            RecommendModel categoryDetailModel = value.data.get(position);
            if (categoryDetailModel == null) {
                return;
            }
            XMBaseQukuItem info = categoryDetailModel.getBaseQukuInfo();
            if (info == null || info.getSDKBean() == null) {
                return;
            }
            String qukuItemType = info.getQukuItemType();
            switch (qukuItemType) {
                case BaseQukuItem.TYPE_ALBUM:
                    playAlbum(info, position);
                    break;
                case BaseQukuItem.TYPE_RADIO:
                    playRadio(info, position);
                    break;
                case BaseQukuItem.TYPE_SONGLIST:
                    playSongList(info, position);
                    break;
                default:
                    getPlaySuccess().postValue(-1);
                    break;
            }
        }
    }

    private void playSongList(final XMBaseQukuItem info, int position) {
        XMSongListInfo xmSongListInfo = XMBaseQukuItem.convertSongListInfo(info);
        OnlineMusicFactory.getKWAudioFetch().fetchSongListMusic(xmSongListInfo, 0, 300, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
            @Override
            public void onFetchSuccess(List<XMMusic> musicList) {
                if (xmSongListInfo != null && xmSongListInfo.getSDKBean() != null) {
                    SongListInfo sdkBean = xmSongListInfo.getSDKBean();
                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(sdkBean.getId() + sdkBean.getName(),
                            KwPlayInfoManager.AlbumType.SONG_LIST);
                }
                OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                getPlaySuccess().postValue(position);
            }

            @Override
            public void onFetchFailed(String msg) {
                XMToast.toastException(getApplication(), getApplication().getString(R.string.net_error));
                getPlaySuccess().postValue(-1);
            }
        });
    }

    private void playRadio(XMBaseQukuItem info, int position) {
        XMRadioInfo xmRadioInfo = XMBaseQukuItem.convertRadioInfo(info);
        if (xmRadioInfo != null && xmRadioInfo.getSDKBean() != null) {
            RadioInfo sdkBean = xmRadioInfo.getSDKBean();
            KwPlayInfoManager.getInstance().setCurrentPlayInfo(sdkBean.getCid() + sdkBean.getName(),
                    KwPlayInfoManager.AlbumType.RADIO);
            AudioShareManager.getInstance().shareKwRadioDataSourceChanged();
            OnlineMusicFactory.getKWPlayer().playRadio(xmRadioInfo.getCid(), xmRadioInfo.getName());
            getPlaySuccess().postValue(position);
        }
    }

    private void playAlbum(final XMBaseQukuItem info, int position) {
        XMAlbumInfo xmAlbumInfo = XMBaseQukuItem.convertAlbumInfo(info);
        OnlineMusicFactory.getKWAudioFetch().fetchAlbumMusic(xmAlbumInfo, 0, 300, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
            @Override
            public void onFetchSuccess(List<XMMusic> xmMusicList) {
                if (xmAlbumInfo != null && xmAlbumInfo.getSDKBean() != null) {
                    AlbumInfo sdkBean = xmAlbumInfo.getSDKBean();
                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(sdkBean.getId() + sdkBean.getName(),
                            KwPlayInfoManager.AlbumType.ALBUM);
                }
                OnlineMusicFactory.getKWPlayer().play(xmMusicList, 0);
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                getPlaySuccess().postValue(position);
            }

            @Override
            public void onFetchFailed(String msg) {
                if (msg.contains("time out")) {
                    XMToast.toastException(getApplication(), getApplication().getString(R.string.no_network));
                    return;
                }
                XMToast.toastException(getApplication(), getApplication().getString(R.string.net_error));
                getPlaySuccess().postValue(-1);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRecommends = null;
        playSuccess = null;
    }
}
