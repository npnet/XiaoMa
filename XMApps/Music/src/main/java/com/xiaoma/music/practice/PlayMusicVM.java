package com.xiaoma.music.practice;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.proxy.XMKWAudioFetchProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/11
 *     desc   :
 * </pre>
 */
public class PlayMusicVM extends BaseViewModel {
    public static final String TAG = "[PlayMusicVM]";
    private final int ITEM_CONT = 30;
    private final int PAGE = 0;

    private MutableLiveData<XmResource<List<PlayMusicBean>>> playMusics;
    private List<XMMusic> musicList = new ArrayList<>();

    public PlayMusicVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<PlayMusicBean>>> getPlayMusics() {
        if (playMusics == null) {
            playMusics = new MutableLiveData<>();
        }
        return playMusics;
    }

    public List<XMMusic> getMusicList() {
        return musicList;
    }

    public void fetchPlayMusicList(String values) {
        getPlayMusics().setValue(XmResource.<List<PlayMusicBean>>loading());

        XMKWAudioFetchProxy.getInstance().search(values, IKuwoConstant.ISearchType.MUSIC, PAGE, ITEM_CONT, new OnAudioFetchListener<List<XMMusic>>() {
            @Override
            public void onFetchSuccess(List<XMMusic> musicInfos) {
                if (musicInfos == null || musicInfos.isEmpty()) {
                    getPlayMusics().postValue(XmResource.failure(getApplication().getString(R.string.data_empty_music)));
                    return;
                }
                getPlayMusics().setValue(XmResource.response(convertData(musicInfos)));

                musicList.clear();
                musicList.addAll(musicInfos);
            }

            @Override
            public void onFetchFailed(String msg) {
                getPlayMusics().setValue(XmResource.failure(msg));
            }
        });

    }


    private List convertData(List<XMMusic> musicInfos) {


        if (musicInfos == null) {
            return null;
        }

        List<PlayMusicBean> playMusicBeans = new ArrayList<>();

        for (int i = 0; i < musicInfos.size(); i++) {

            XMMusic xmMusic = musicInfos.get(i);

            PlayMusicBean playMusicBean = new PlayMusicBean();
            playMusicBean.setSingerName(xmMusic.getArtist());
            playMusicBean.setName(xmMusic.getName());
            playMusicBean.setSongId(xmMusic.getRid());

            playMusicBeans.add(playMusicBean);
        }

        return playMusicBeans;

    }

}
