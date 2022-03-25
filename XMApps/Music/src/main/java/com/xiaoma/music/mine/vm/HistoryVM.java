package com.xiaoma.music.mine.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.model.LocalModel;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.xiaoma.music.common.manager.MusicDbManager.MAX_HISTORY_MUSIC;

/**
 * Created by ZYao.
 * Date ：2018/10/11 0011
 */
public class HistoryVM extends AndroidViewModel {
    private MutableLiveData<XmResource<List<LocalModel>>> musicInfoList;

    public HistoryVM(@NonNull Application application) {
        super(application);
    }

    public void initMusicInfoData() {
        getHistoryMusicList().setValue(XmResource.<List<LocalModel>>loading());
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                List<XMMusic> musicHistory = MusicDbManager.getInstance().queryHistoryMusic();
                if (ListUtils.isEmpty(musicHistory)) {
                    List<LocalModel> localModels = new ArrayList<>();
                    getHistoryMusicList().postValue(XmResource.response(localModels));
                    return;
                }
                List<LocalModel> localModels = new ArrayList<>();
                for (XMMusic xmMusic : musicHistory) {
                    if (xmMusic == null) {
                        continue;
                    }
                    localModels.add(new LocalModel(xmMusic));
                }
                Collections.reverse(localModels);
                getHistoryMusicList().postValue(XmResource.response(localModels));
            }
        });
    }

    public MutableLiveData<XmResource<List<LocalModel>>> getHistoryMusicList() {
        if (musicInfoList == null) {
            musicInfoList = new MutableLiveData<>();
        }
        return musicInfoList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        musicInfoList = null;
    }

    public void clearAllHistory() {
        MusicDbManager.getInstance().clearAllHistoryMusic();
    }

    public void postEmptyData() {
        List<LocalModel> localModels = new ArrayList<>();
        getHistoryMusicList().postValue(XmResource.response(localModels));
    }

    public void addMusicHistory(XMMusic music) {
        final XmResource<List<LocalModel>> xmResource = getHistoryMusicList().getValue();
        List<LocalModel> musicList = new ArrayList<>();
        if (xmResource != null) {
            musicList.addAll(xmResource.data);
        }
        boolean contain = false;
        for (LocalModel xmMusic : musicList) {
            if (xmMusic == null || xmMusic.getXmMusic() == null) {
                continue;
            }
            if (xmMusic.getXmMusic().getRid() == music.getRid()) {
                contain = true;
            }
        }
        if (!contain) {
            musicList.add(0, new LocalModel(music));
        }
        if (musicList.size() > MAX_HISTORY_MUSIC) {
            getHistoryMusicList().postValue(XmResource.response(musicList.subList(0, MAX_HISTORY_MUSIC)));
        } else {
            getHistoryMusicList().postValue(XmResource.response(musicList));
        }
    }
}
