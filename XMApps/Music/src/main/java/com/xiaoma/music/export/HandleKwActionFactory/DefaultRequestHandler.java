package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.player.AbsAudioClient;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class DefaultRequestHandler extends BaseRequestInterceptHandler {


    public DefaultRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }

}
