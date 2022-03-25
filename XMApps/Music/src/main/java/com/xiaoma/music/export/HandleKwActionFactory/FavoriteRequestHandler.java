package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class FavoriteRequestHandler extends BaseRequestInterceptHandler {


    public FavoriteRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        ArrayList<AudioInfo> audioInfos = new ArrayList<>();
        final List<XMMusic> musicHistory = MusicDbManager.getInstance().queryCollectionMusic();
        if (ListUtils.isEmpty(musicHistory)) {
            client.shareAudioList(AudioConstants.AudioResponseCode.SUCCESS,
                    new ArrayList<>(), new int[]{0, 1, 100}, 0, clientCallback);
            return;
        }
        for (XMMusic xmMusic : musicHistory) {
            if (xmMusic == null) {
                continue;
            }
            final AudioInfo audioInfo = new AudioInfo();
            audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
            audioInfo.setTitle(xmMusic.getName());
            audioInfo.setSubTitle(xmMusic.getArtist());
            audioInfo.setUniqueId(xmMusic.getRid());
            audioInfo.setHistory(false);
            audioInfos.add(audioInfo);
        }
        Collections.reverse(audioInfos);
        client.shareAudioList(AudioConstants.AudioResponseCode.SUCCESS,
                audioInfos, new int[]{0, 1, 100}, 0, clientCallback);
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}
