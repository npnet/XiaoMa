package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;

import java.util.List;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
public class SimilarVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<XMMusic>>> mSimilarList;

    public SimilarVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<XMMusic>>> getSimilarList() {
        if (mSimilarList == null) {
            mSimilarList = new MutableLiveData<>();
        }
        return mSimilarList;
    }

    public void initVM() {
        getSimilarList().setValue(XmResource.<List<XMMusic>>loading());
        final XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        if (!XMMusic.isEmpty(nowPlayingMusic)) {
            OnlineMusicFactory.getKWAudioFetch().fetchSimilarSong(nowPlayingMusic, 50, new OnAudioFetchListener<List<XMMusic>>() {
                @Override
                public void onFetchSuccess(List<XMMusic> musicList) {
                    if (musicList == null || musicList.isEmpty()) {
                        getSimilarList().setValue(XmResource.<List<XMMusic>>failure(getApplication().getString(R.string.data_empty_music)));
                        return;
                    }
                    getSimilarList().setValue(XmResource.response(musicList));
                }

                @Override
                public void onFetchFailed(String msg) {
                    getSimilarList().setValue(XmResource.<List<XMMusic>>error(msg));
                }
            });
        } else {
            getSimilarList().setValue(XmResource.<List<XMMusic>>failure(getApplication().getString(R.string.data_empty_music)));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSimilarList = null;
    }
}
