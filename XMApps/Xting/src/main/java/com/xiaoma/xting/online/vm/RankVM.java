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
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.online.model.AlbumBean;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRankAlbumList;
import com.xiaoma.xting.sdk.bean.XMRankCategory;
import com.xiaoma.xting.sdk.bean.XMRankList;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/17
 */
public class RankVM extends AndroidViewModel {

    private MutableLiveData<XmResource<List<XMRankCategory>>> mRanks;
    private static final String RANKS_CACHE_KEY = "ranks";

    public RankVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<XMRankCategory>>> getBoards() {
        if (mRanks == null) {
            mRanks = new MutableLiveData<>();
        }
        return mRanks;
    }

    public void fetchRankList() {
        getBoards().setValue(XmResource.<List<XMRankCategory>>loading());
        List<XMRankCategory> caches = TPUtils.getList(getApplication(), RANKS_CACHE_KEY, XMRankCategory[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getBoards().setValue(XmResource.success(caches));
            if (NetworkUtils.isConnected(getApplication())) {
                fetchNetRankList();
            }
        } else {
            if (NetworkUtils.isConnected(getApplication())) {
                fetchNetRankList();
            } else {
                getBoards().setValue(XmResource.<List<XMRankCategory>>error(XtingConstants.ErrorMsg.NO_NETWORK));
            }
        }
    }

    private void fetchNetRankList() {
        OnlineFMFactory.getInstance().getSDK().getRankList(1, new XMDataCallback<XMRankList>() {
            @Override
            public void onSuccess(@Nullable XMRankList data) {
                if (data != null) {
                    List<XMRankCategory> rankCategoryBeans = XMRankCategory.convert(data.getRankList());
                    TPUtils.putList(getApplication(), RANKS_CACHE_KEY, rankCategoryBeans);
                    getBoards().setValue(XmResource.success(rankCategoryBeans));
                } else {
                    getBoards().setValue(XmResource.<List<XMRankCategory>>failure(XtingConstants.ErrorMsg.NULL_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getBoards().setValue(XmResource.<List<XMRankCategory>>error(code, msg));
            }
        });
    }

    private MutableLiveData<XmResource<List<PlayerInfo>>> mRankBoardLiveData;

    public void fetchRank(final long rankListId) {
        getRankDetails().setValue(XmResource.<List<PlayerInfo>>loading());
        final List<PlayerInfo> caches = SharedPrefUtils.getCachedRankList(getApplication(), rankListId);
        if (!CollectionUtil.isListEmpty(caches)) {
            getRankDetails().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getRankAlbumList(rankListId, 1, new XMDataCallback<XMRankAlbumList>() {
            @Override
            public void onSuccess(@Nullable XMRankAlbumList data) {
                if (data != null) {
                    List<AlbumBean> albums = AlbumBean.convert2Album(data.getRankAlbumList());
                    caches.clear();
                    PlayerInfo playerInfo = null;
                    for (AlbumBean albumBean : albums) {
                        playerInfo = new PlayerInfo();
                        playerInfo.setType(PlayerSourceType.HIMALAYAN);
                        playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
                        playerInfo.setAlbumId(albumBean.getId());
                        playerInfo.setImgUrl(albumBean.getValidCover());
                        playerInfo.setAlbumName(albumBean.getAlbumTitle());
                        playerInfo.setPlayCount(albumBean.getPlayCount());

                        caches.add(playerInfo);
                    }
                    SharedPrefUtils.cacheRankList(getApplication(), rankListId, caches);
                    getRankDetails().setValue(XmResource.success(caches));
                } else {
                    getRankDetails().setValue(XmResource.<List<PlayerInfo>>failure(XtingConstants.ErrorMsg.NULL_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getRankDetails().setValue(XmResource.<List<PlayerInfo>>error(code, msg));
            }
        });
    }

    public MutableLiveData<XmResource<List<PlayerInfo>>> getRankDetails() {
        if (mRankBoardLiveData == null) {
            mRankBoardLiveData = new MutableLiveData<>();
        }
        return mRankBoardLiveData;
    }
}
