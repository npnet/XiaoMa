package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.List;

/**
 * @author zs
 * @date 2018/12/17 0017.
 */
public class OnlinePlayListVM extends BaseViewModel {

    private MutableLiveData<List<XMMusic>> mPlayList;

    public OnlinePlayListVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<XMMusic>> getPlayList() {
        if (mPlayList == null) {
            mPlayList = new MutableLiveData<>();
        }
        return mPlayList;
    }

    public void fetchOnlinePlaylist() {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                if (nowPlayingList == null) {
                    return;
                }
                List<XMMusic> musicList = nowPlayingList.toList();
                getPlayList().postValue(musicList);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPlayList = null;
    }

}
