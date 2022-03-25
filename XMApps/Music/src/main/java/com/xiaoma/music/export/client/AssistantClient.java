package com.xiaoma.music.export.client;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.export.HandleKwActionFactory.BaseRequestInterceptHandler;
import com.xiaoma.music.export.HandleKwActionFactory.MusicRequestActionDispatcher;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.log.KLog;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class AssistantClient extends Client {
    private Context mContext;

    private static AssistantClient sClient;

    public static AssistantClient newSingleton(Context context) {
        if (sClient == null) {
            synchronized (AssistantClient.class) {
                if (sClient == null) {
                    sClient = new AssistantClient(context);
                }
            }
        }
        return sClient;
    }

    public AssistantClient(Context context) {
        super(context, CenterConstants.MUSIC_PORT);
        this.mContext = context.getApplicationContext();
    }

    @Override
    protected void onReceive(int action, Bundle data) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        KLog.d("MrMine", "onSearchRequest: " + action);
        BaseRequestInterceptHandler handler = MusicRequestActionDispatcher.getInstance().dispatcherAssistantAction(mContext, action, callback);
        handler.handlerAssistant(this, action, data);
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {

    }

    public void dispatcherSuccessCallback(int action, ClientCallback clientCallback) {
        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putInt(AudioConstants.BundleKey.ACTION, action);
            callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, AudioConstants.AudioResponseCode.SUCCESS);
            callbackData.putBoolean(AudioConstants.BundleKey.RESULT,true);
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }

    public void dispatcherFailedCallback(int action, ClientCallback clientCallback) {
        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putInt(AudioConstants.BundleKey.ACTION, action);
            callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, AudioConstants.AudioResponseCode.ERROR);
            callbackData.putBoolean(AudioConstants.BundleKey.RESULT,false);
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }
}
