package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.search.manager.ResultCallBack;
import com.xiaoma.music.search.manager.SearchMusicManager;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/8/5 0005
 */
class SearchRequestHandler extends BaseRequestInterceptHandler {
    public SearchRequestHandler(Context context, ClientCallback callback) {
        super(context, callback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        String singerName = data.getString(AudioConstants.BundleKey.SINGER);
        String musicName = data.getString(AudioConstants.BundleKey.SONG);
        SearchMusicManager.getInstance().searchNameAndArtistFromKw(singerName, musicName, new ResultCallBack<List<XMMusic>>() {
            @Override
            public void onSuccess(List<XMMusic> xmMusics) {
                if (!ListUtils.isEmpty(xmMusics)) {
                    AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                    OnlineMusicFactory.getKWPlayer().play(xmMusics, 0);
                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(singerName + musicName, KwPlayInfoManager.AlbumType.ASSISTANT);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}
