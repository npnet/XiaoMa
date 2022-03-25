package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.export.model.SearchByTypeMode;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class SearchByCategoryHandler extends BaseRequestInterceptHandler {

    public SearchByCategoryHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {
        String singer = data.getString(AudioConstants.BundleKey.SINGER);
        String keyWord = data.getString(AudioConstants.BundleKey.MUSIC_TYPE);
        if (TextUtils.isEmpty(singer)) {
            ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                @Override
                public void run() {
                    RequestManager.getInstance().searchMusicByKeyWord(KUWO_CATEGORY_TYPE, keyWord, 0, 30, new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            XMResult<SearchByTypeMode> result = GsonHelper.fromJson(response.body(), new TypeToken<XMResult<SearchByTypeMode>>() {
                            }.getType());
                            if (result == null || !result.isSuccess()) {
                                client.dispatcherFailedCallback(action, clientCallback);
                                return;
                            }
                            SearchByTypeMode data = result.getData();
                            if (data == null || ListUtils.isEmpty(data.getSongList())) {
                                client.dispatcherFailedCallback(action, clientCallback);
                                return;
                            }
                            List<SearchByTypeMode.SongListBean> songList = data.getSongList();
                            List<Long> ids = new ArrayList<>();

                            for (SearchByTypeMode.SongListBean searchInfo : songList) {
                                ids.add(searchInfo.getSongId());
                            }
                            OnlineMusicFactory.getKWAudioFetch().fetchMusicByIds(ids, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                                @Override
                                public void onFetchSuccess(List<XMMusic> musicList) {
                                    OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                                    AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                                    KwPlayInfoManager.getInstance().setCurrentPlayInfo(keyWord, KwPlayInfoManager.AlbumType.ASSISTANT);
                                    client.dispatcherSuccessCallback(action, clientCallback);
                                }

                                @Override
                                public void onFetchFailed(String msg) {
                                    client.dispatcherFailedCallback(action, clientCallback);
                                }
                            });
                        }
                    });
                }
            });
        } else {
            OnlineMusicFactory.getKWAudioFetch().search(singer + "的" + keyWord, IKuwoConstant.ISearchType.MUSIC, 0, 30,
                    new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                        @Override
                        public void onFetchSuccess(List<XMMusic> musicList) {
                            OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                            AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                            KwPlayInfoManager.getInstance().setCurrentPlayInfo(keyWord, KwPlayInfoManager.AlbumType.ASSISTANT);
                            client.dispatcherSuccessCallback(action, clientCallback);
                        }

                        @Override
                        public void onFetchFailed(String msg) {
                            client.dispatcherFailedCallback(action, clientCallback);
                        }
                    });
        }
    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}
