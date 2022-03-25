package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.export.model.RankInfo;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;

import java.util.List;

import cn.kuwo.base.bean.quku.BillboardInfo;

/**
 * Created by ZYao.
 * Date ：2019/3/19 0019
 */
class SearchByBillboardHandler extends BaseRequestInterceptHandler {
    public SearchByBillboardHandler(Context context, ClientCallback callback) {
        super(context, callback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {
        String keyWord = data.getString(AudioConstants.BundleKey.RANKING_LIST_TYPE);
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                RequestManager.getInstance().searchMusicByKeyWord(KUWO_BILLBOARD_TYPE, keyWord, 0, 30, new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        XMResult<RankInfo> result = GsonHelper.fromJson(response.body(), new TypeToken<XMResult<RankInfo>>() {
                        }.getType());
                        if (result == null || !result.isSuccess()) {
                            client.dispatcherFailedCallback(action, clientCallback);
                            return;
                        }
                        RankInfo rankInfo = result.getData();
                        BillboardInfo billboardInfo = new BillboardInfo();
                        billboardInfo.setId(String.valueOf(rankInfo.getRankId()));
                        billboardInfo.setName(rankInfo.getRankName());
                        billboardInfo.setDigest(String.valueOf(rankInfo.getDigest()));
                        XMBillboardInfo xmBillboardInfo = new XMBillboardInfo(billboardInfo);
                        OnlineMusicFactory.getKWAudioFetch().fetchBillboardMusic(xmBillboardInfo, 0, 30, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                            @Override
                            public void onFetchSuccess(List<XMMusic> musicList) {
                                OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                                KwPlayInfoManager.getInstance().setCurrentPlayInfo(xmBillboardInfo.getSDKBean().getName()
                                        + xmBillboardInfo.getSDKBean().getId(), KwPlayInfoManager.AlbumType.ASSISTANT);
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
    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}
