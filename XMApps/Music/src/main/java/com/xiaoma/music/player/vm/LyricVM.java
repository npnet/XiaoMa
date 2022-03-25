package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.OnChargeQualityListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.player.model.MusicQualityModel;

/**
 * Created by ZYao.
 * Date ：2018/10/18 0018
 */
public class LyricVM extends AndroidViewModel {
    private MutableLiveData<String> lyric;
    private MutableLiveData<MusicQualityModel> currentQualityModels;

    public LyricVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getLyric() {
        if (lyric == null) {
            lyric = new MutableLiveData<>();
        }
        return lyric;
    }

    public MutableLiveData<MusicQualityModel> getCurrentQualityModels() {
        if (currentQualityModels == null) {
            currentQualityModels = new MutableLiveData<>();
        }
        return currentQualityModels;
    }

    public void getCurrLyric(XMMusic music) {
        OnlineMusicFactory.getKWAudioFetch().fetchLyric(music, new OnAudioFetchListener<String>() {
            @Override
            public void onFetchSuccess(String s) {
                getLyric().postValue(s);
            }

            @Override
            public void onFetchFailed(String msg) {
                getLyric().postValue("");
            }
        });
    }

    public void fetchCurrentQuality(int quality) {
        //电台类节目获取最高音质为-1，故电台类节目音质选择功能置灰不可用
        int maxQuality = OnlineMusicFactory.getKWPlayer().getNowPlayMusicBestQuality();
        OnlineMusicFactory.getKWPlayer().chargeNowPlayMusic(quality, new OnChargeQualityListener() {
            @Override
            public void onSuccess(MusicQualityModel model) {
                if (model != null) {
                    model.setRadio(maxQuality == -1);
                    getCurrentQualityModels().postValue(model);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        lyric = null;
    }
}
