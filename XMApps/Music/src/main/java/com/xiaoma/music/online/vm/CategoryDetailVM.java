package com.xiaoma.music.online.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.kuwo.model.XMCategoryListInfo;
import com.xiaoma.music.kuwo.model.XMListType;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.music.online.model.Category;
import com.xiaoma.music.online.model.CategoryDetailModel;
import com.xiaoma.utils.ListUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.SongListInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public class CategoryDetailVM extends AndroidViewModel {

    private static final String TAG = "CategoryDetailVM";
    public static final int PAGE_SIZE = 30;
    public static final int PLAY_COUNT = 10;
    public static final int PLAY_TOTAL_COUNT = 2000;
    private MutableLiveData<XmResource<List<CategoryDetailModel>>> mSongList;
    private MutableLiveData<Integer> mPlayPosition;
    private int mCurPage = 0;

    public CategoryDetailVM(@NonNull Application application) {
        super(application);
    }

    public boolean isFirstPage() {
        return mCurPage == 0;
    }

    public void fetchData(Category category, boolean isFirstLoader) {
        if (isFirstLoader) {
            getSongList().postValue(XmResource.loading());
        }
        XMBaseQukuItem item = category.getItem();
        if (item == null) {
            getSongList().postValue(XmResource.<List<CategoryDetailModel>>error(""));
            return;
        }
        item.getRealQukuItem(item, new XMBaseQukuItem.OnCheckQukuListener() {
            @Override
            public void onSongList(XMSongListInfo songListInfo) {
                fetchSongListMusic(songListInfo);
            }

            @Override
            public void onCategoryList(XMCategoryListInfo categoryListInfo) {
                fetchCategoryMusic(categoryListInfo);
            }
        });
    }

    public void fetchCategoryMusic(XMCategoryListInfo info) {
        OnlineMusicFactory.getKWAudioFetch().fetchCategoryListMusic(info, mCurPage, PAGE_SIZE, new OnAudioFetchListener<List<XMSongListInfo>>() {
            @Override
            public void onFetchSuccess(List<XMSongListInfo> songList) {
                postSongList(songList);
            }

            @Override
            public void onFetchFailed(String msg) {
                getSongList().postValue(XmResource.<List<CategoryDetailModel>>error(msg));
            }
        });
    }

    private void postSongList(List<XMSongListInfo> songList) {
        if (songList == null || songList.isEmpty()) {
            if (mCurPage == 0) {
                getSongList().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
            }
            return;
        }
        List<CategoryDetailModel> modelList = new ArrayList<>();
        for (XMSongListInfo xmSongListInfo : songList) {
            if (xmSongListInfo == null || xmSongListInfo.getSDKBean() == null) {
                continue;
            }
            SongListInfo sdkBean = xmSongListInfo.getSDKBean();
            String name = sdkBean.getName();
            String imageUrl = sdkBean.getImageUrl();
            modelList.add(new CategoryDetailModel(name, imageUrl, xmSongListInfo));
        }
        getSongList().postValue(XmResource.response(modelList));
        mCurPage++;
    }

    public void fetchSongListMusic(XMSongListInfo info) {
        OnlineMusicFactory.getKWAudioFetch().fetchSongListMusic(info, mCurPage, PAGE_SIZE, new OnAudioFetchListener<List<XMSongListInfo>>() {
            @Override
            public void onFetchSuccess(List<XMSongListInfo> songList) {
                postSongList(songList);
            }

            @Override
            public void onFetchFailed(String msg) {
                getSongList().postValue(XmResource.<List<CategoryDetailModel>>error(msg));
            }
        });
    }

    public MutableLiveData<XmResource<List<CategoryDetailModel>>> getSongList() {
        if (mSongList == null) {
            mSongList = new MutableLiveData<>();
        }
        return mSongList;
    }

    public MutableLiveData<Integer> getPlayPosition() {
        if (mPlayPosition == null) {
            mPlayPosition = new MutableLiveData<>();
        }
        return mPlayPosition;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSongList = null;
        mPlayPosition = null;
    }

    public void playSongList(int position, List<CategoryDetailModel> modelList) {
        if (!ListUtils.isEmpty(modelList)) {
            CategoryDetailModel categoryDetailModel = modelList.get(position);
            if (categoryDetailModel == null) {
                return;
            }
            final XMSongListInfo songListInfo = categoryDetailModel.getSongListInfo();
            OnlineMusicFactory.getKWAudioFetch().fetchSongListMusic(songListInfo, 0, PLAY_COUNT, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                @Override
                public void onFetchSuccess(List<XMMusic> musicList) {
                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(songListInfo.getSDKBean().getId()
                            + songListInfo.getSDKBean().getName(), KwPlayInfoManager.AlbumType.SONG_LIST);
                    OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                    AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                    getPlayPosition().postValue(position);
                    if (GuideDataHelper.shouldShowGuide(getApplication(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
                        EventBus.getDefault().post("", EventBusTags.MUSIC_FETCH_SUCCESS);
                }

                @Override
                public void onFetchFailed(String msg) {
                    getPlayPosition().postValue(-1);
                }
            });
            OnlineMusicFactory.getKWAudioFetch().fetchSongListMusic(songListInfo, 0, PLAY_TOTAL_COUNT, new OnAudioFetchListener<List<XMMusic>>() {
                @Override
                public void onFetchSuccess(List<XMMusic> musicList) {
                    boolean isPlay = KwPlayInfoManager.getInstance().isCurrentPlayInfo(songListInfo.getSDKBean().getId()
                            + songListInfo.getSDKBean().getName(), KwPlayInfoManager.AlbumType.SONG_LIST);
                    if (!ListUtils.isEmpty(musicList) && musicList.size() > PLAY_COUNT && isPlay) {
                        List<XMMusic> playList = new ArrayList<>(musicList.subList(PLAY_COUNT, musicList.size()));
                        OnlineMusicFactory.getMusicListControl().insertMusic(XMListType.LIST_TEMPORARY.getTypeName(), playList);
                    }
                }

                @Override
                public void onFetchFailed(String msg) {
                }
            });
        }
    }

}
