package com.xiaoma.music.search.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMArtistInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.music.kuwo.proxy.XMKWAudioFetchProxy;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class SearchResultVM extends AndroidViewModel {

    private MutableLiveData<XmResource<List<XMMusic>>> musicList;
    private MutableLiveData<XmResource<List<XMArtistInfo>>> singerList;
    private MutableLiveData<XmResource<List<XMSongListInfo>>> songLists;
    private final int ITEM_CONT = 30;
    private final int PAGE = 0;


    public SearchResultVM(@NonNull Application application) {
        super(application);
    }

    public void getMusicData(String keyWord ,boolean isFirstLoad) {
        if (!isFirstLoad) {
            getMusicList().setValue(XmResource.<List<XMMusic>>loading());
        }
        KLog.d("MrMine","getMusicData: "+"");
        XMKWAudioFetchProxy.getInstance().search(keyWord, IKuwoConstant.ISearchType.MUSIC, PAGE, ITEM_CONT, new OnAudioFetchListener<List<XMMusic>>() {
            @Override
            public void onFetchSuccess(List<XMMusic> musicInfos) {
                if (musicInfos == null || musicInfos.isEmpty()) {
                    getMusicList().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                    return;
                }
                getMusicList().postValue(XmResource.response(musicInfos));
            }

            @Override
            public void onFetchFailed(String msg) {
                getMusicList().postValue(XmResource.<List<XMMusic>>error(msg == null ? "" : msg));
            }
        });

    }

    public void getSingerData(String keyWord,boolean isFirstLoad) {
        if (!isFirstLoad) {
            getSingerList().setValue(XmResource.<List<XMArtistInfo>>loading());
        }
        KLog.d("MrMine","getSingerData: "+"");
        XMKWAudioFetchProxy.getInstance().search(keyWord, IKuwoConstant.ISearchType.ARTIST, PAGE, ITEM_CONT, new OnAudioFetchListener<List<XMArtistInfo>>() {
            @Override
            public void onFetchSuccess(List<XMArtistInfo> artistInfos) {
                if (artistInfos == null || artistInfos.isEmpty()) {
                    getSingerList().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                    return;
                }
                getSingerList().postValue(XmResource.response(artistInfos));
            }

            @Override
            public void onFetchFailed(String msg) {
                getSingerList().postValue(XmResource.<List<XMArtistInfo>>error(msg == null ? "" : msg));
            }
        });

    }



    public void getSongListData(String keyWord,boolean isFirstLoad) {
        if (!isFirstLoad) {
            getSongLists().setValue(XmResource.<List<XMSongListInfo>>loading());
        }
        KLog.d("MrMine","getSongListData: "+"");
        XMKWAudioFetchProxy.getInstance().search(keyWord, IKuwoConstant.ISearchType.SONGLIST, PAGE, ITEM_CONT, new OnAudioFetchListener<List<XMSongListInfo>>() {
            @Override
            public void onFetchSuccess(List<XMSongListInfo> songListInfos) {
                if (songListInfos == null || songListInfos.isEmpty()) {
                    getSongLists().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                    return;
                }
                getSongLists().postValue(XmResource.response(songListInfos));
            }

            @Override
            public void onFetchFailed(String msg) {
                getSongLists().postValue(XmResource.<List<XMSongListInfo>>error(msg == null ? "" : msg));
            }
        });
    }

    public MutableLiveData<XmResource<List<XMMusic>>> getMusicList() {
        if (musicList == null)
            musicList = new MutableLiveData<>();
        return musicList;
    }

    public MutableLiveData<XmResource<List<XMArtistInfo>>> getSingerList() {
        if (singerList == null) {
            singerList = new MutableLiveData<>();
        }
        return singerList;
    }

    public MutableLiveData<XmResource<List<XMSongListInfo>>> getSongLists() {
        if (songLists == null) {
            songLists = new MutableLiveData<>();
        }
        return songLists;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        musicList = null;
        singerList = null;
    }
}
