package com.xiaoma.xting.online.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.model.XmResource;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.ICallback;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.koala.KoalaRequest;
import com.xiaoma.xting.koala.bean.XMColumnMember;
import com.xiaoma.xting.koala.bean.XMRadioDetailColumnMember;
import com.xiaoma.xting.online.model.AlbumBean;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMAlbumList;
import com.xiaoma.xting.sdk.bean.XMDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class RecommendVM extends AndroidViewModel {

    private List<PlayerInfo> mPlayerInfos;
    private MutableLiveData<XmResource<List<PlayerInfo>>> mRecommendsLiveData;
    private int requestCount = 0;
    public static final int REQ_RETRY_COUNT = 3;

    public RecommendVM(@NonNull Application application) {
        super(application);
        mPlayerInfos = new ArrayList<>();
    }

    public MutableLiveData<XmResource<List<PlayerInfo>>> getRecommends() {
        if (mRecommendsLiveData == null) {
            mRecommendsLiveData = new MutableLiveData<>();
        }
        return mRecommendsLiveData;
    }


    public void fetchREC() {
        SeriesAsyncWorker.create().next(new Work() {

            @Override
            public void doWork(Object lastResult) {
                fetchRECCache(new ICallback<Boolean>() {
                    @Override
                    public void onResult(Boolean hasCache) {
                        doNext(hasCache);
                    }
                });
            }
        }).next(new Work<Boolean>() {

            @Override
            public void doWork(Boolean hasCache) {
                fetchRECNet(hasCache);
            }
        }).start();
    }

    private void fetchRECCache(ICallback<Boolean> callback) {
        List<PlayerInfo> cacheRECList = SharedPrefUtils.getCachedRecommendList(getApplication());
        boolean hasCached = !(ListUtils.isEmpty(cacheRECList));
        if (hasCached) {
            getRecommends().postValue(XmResource.response(cacheRECList));
        }
        if (callback != null) {
            callback.onResult(hasCached);
        }
    }

    private void fetchRECNet(Boolean hasCache) {
        SeriesAsyncWorker.create().next(new Work(Priority.NORMAL) {
            @Override
            public void doWork(Object lastResult) {
                KoalaRequest.getColumnTree(true, null, new XMDataCallback<List<XMColumnMember>>() {
                    @Override
                    public void onSuccess(@Nullable List<XMColumnMember> data) {
                        List<PlayerInfo> list = new ArrayList<>();
                        Log.d("QQ", "{onSuccess}-[] : ");
                        if (data != null) {
                            for (XMColumnMember bean : data) {
                                if (bean instanceof XMRadioDetailColumnMember) {
                                    list.add(BeanConverter.toPlayerInfo((XMRadioDetailColumnMember) bean));
                                }
                            }
                        }
                        doNext(list);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        doNext(new ArrayList<PlayerInfo>());
                    }
                });
            }
        }).next(new Work<List<PlayerInfo>>() {
            @Override
            public void doWork(List<PlayerInfo> lastResult) {
                OnlineFMFactory.getInstance().getSDK().getAlbumList(0, 3, null, 1, new XMDataCallback<XMAlbumList>() {
                    @Override
                    public void onSuccess(@Nullable XMAlbumList data) {
                        List<PlayerInfo> list = new ArrayList<>();
                        if (lastResult != null) {
                            Log.d("Jir", "koala: Success" + lastResult.size());
                            list.addAll(lastResult);
                        }
                        if (data != null) {
                            List<AlbumBean> albums = AlbumBean.convert2Album(data.getAlbums());
                            PlayerInfo playerInfo = null;
                            for (AlbumBean albumBean : albums) {
                                playerInfo = new PlayerInfo();
                                playerInfo.setType(PlayerSourceType.HIMALAYAN);
                                playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
                                playerInfo.setAlbumId(albumBean.getId());
                                playerInfo.setImgUrl(albumBean.getValidCover());
                                playerInfo.setAlbumName(albumBean.getAlbumTitle());
                                playerInfo.setPlayCount(albumBean.getPlayCount());
                                list.add(playerInfo);
                            }
                        }

                        if (list.isEmpty()) {
                            if (!hasCache) {
                                if (requestCount <= REQ_RETRY_COUNT) {
                                    fetchRECNet(false);
                                    requestCount++;
                                } else {
                                    requestCount = 0;
                                    getRecommends().postValue(XmResource.failure(XtingConstants.ErrorMsg.NULL_DATA));
                                }
                            }
                        } else {
                            SharedPrefUtils.cacheRecommendList(getApplication(), list);
                            getRecommends().postValue(XmResource.success(list));
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        List<PlayerInfo> list = null;
                        if (!ListUtils.isEmpty(lastResult)) {
                            list = new ArrayList<>(lastResult);
                        }
                        if (list != null) {
                            SharedPrefUtils.cacheRecommendList(getApplication(), list);
                            getRecommends().postValue(XmResource.success(list));
                        } else {
                            if (!hasCache) {
                                if (requestCount <= REQ_RETRY_COUNT) {
                                    fetchRECNet(false);
                                    requestCount++;
                                } else {
                                    requestCount = 0;
                                    getRecommends().postValue(XmResource.failure(XtingConstants.ErrorMsg.NULL_DATA));
                                }
                            } else {
                                requestCount = 0;
                            }
                        }
                    }
                });
            }
        }).start();
    }
}
