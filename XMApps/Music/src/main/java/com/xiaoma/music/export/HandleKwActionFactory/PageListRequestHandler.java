package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.MusicPageManager;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class PageListRequestHandler extends BaseRequestInterceptHandler {


    public PageListRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        if (data == null) {
            client.shareAudioList(AudioConstants.AudioResponseCode.ERROR, null, null, 0, clientCallback);
            return;
        }
        final int page = data.getInt(AudioConstants.BundleKey.CURRENT_PAGE);
        ArrayList<AudioInfo> audioInfos = (ArrayList<AudioInfo>) MusicPageManager.getInstance().searchListByPage(page);
        if (ListUtils.isEmpty(audioInfos)) {
            client.shareAudioList(AudioConstants.AudioResponseCode.ERROR, null, null, 0, clientCallback);
            return;
        }
        int totalPage = MusicPageManager.getInstance().getTotalPage();
        final int[] pageInfo = {page, totalPage, audioInfos.size()};
        int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
        client.shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, audioInfos, pageInfo, index, clientCallback);
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}
