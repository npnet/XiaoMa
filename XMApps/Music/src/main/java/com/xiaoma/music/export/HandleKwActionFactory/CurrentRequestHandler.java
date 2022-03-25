package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.MusicPageManager;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class CurrentRequestHandler extends BaseRequestInterceptHandler {


    public CurrentRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        final XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
        if (nowPlayingList != null) {
            final List<XMMusic> musicList = nowPlayingList.toList();
            ArrayList<AudioInfo> audioInfoList = new ArrayList<>();
            for (XMMusic xmMusic : musicList) {
                AudioInfo audioInfo = new AudioInfo();
                audioInfo.setUniqueId(xmMusic.getRid());
                audioInfo.setAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                audioInfo.setHistory(false);
                audioInfo.setTitle(xmMusic.getName());
                audioInfo.setSubTitle(xmMusic.getArtist());
                audioInfoList.add(audioInfo);
            }
            MusicPageManager.getInstance().paging(20, audioInfoList);
            int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
            int currentPage = MusicPageManager.getInstance().getCurrentPageByIndex(index);
            int totalPage = MusicPageManager.getInstance().getTotalPage();
            ArrayList<AudioInfo> audioInfos = (ArrayList<AudioInfo>) MusicPageManager.getInstance().searchListByPage(currentPage);
            final int[] pageInfo = {currentPage, totalPage, audioInfoList.size()};
            client.shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, audioInfos, pageInfo, index,clientCallback);
        } else {
            client.shareAudioList(AudioConstants.AudioResponseCode.ERROR, null, null, 0,clientCallback);
        }
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }

}
