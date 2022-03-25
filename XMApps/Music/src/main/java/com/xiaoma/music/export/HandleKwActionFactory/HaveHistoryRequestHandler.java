package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.player.AbsAudioClient;

/**
 * Created by ZYao.
 * Date ：2019/5/17 0017
 */
class HaveHistoryRequestHandler extends BaseRequestInterceptHandler {
    public HaveHistoryRequestHandler(Context context, ClientCallback callback) {
        super(context, callback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        client.shareHaveHistory(nowPlayingMusic != null, clientCallback);
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}
