package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public abstract class BaseRequestInterceptHandler {
    protected static final int KUWO_BILLBOARD_TYPE = 1;
    protected static final int KUWO_CATEGORY_TYPE = 2;
    protected Context mContext;
    protected ClientCallback clientCallback;

    public BaseRequestInterceptHandler(Context mContext, ClientCallback clientCallback) {
        this.mContext = mContext;
        this.clientCallback = clientCallback;
    }

    public abstract void handler(AbsAudioClient client, Bundle data);

    public abstract void searchInKw(AssistantClient client, int action, Bundle data);

    public abstract void searchInUsb(AssistantClient client, int action, Bundle data);

    public void handlerAssistant(AssistantClient client, int action, Bundle data) {
        int searchType = data.getInt(AudioConstants.BundleKey.AUDIO_TYPE);
        if (searchType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            searchInUsb(client, action, data);
        } else if (searchType == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO) {
            searchInKw(client, action, data);
        } else if (searchType == AudioConstants.AudioTypes.NONE){
            searchInKw(client, action, data);
        }
    }
}
