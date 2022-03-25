package com.xiaoma.xting.assistant;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/4
 */
public class AssistantClient extends Client {

    private static final String TAG = AssistantClient.class.getSimpleName();

    private static AssistantClient sClient;
    private ClientCallback mConnectCallback;

    private AssistantClient(Context context) {
        super(context, CenterConstants.XTING_PORT);
    }

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

    @Override
    protected void onReceive(int action, Bundle data) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        AssistantHandler.handleAssistantAction(action, data, callback);
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        mConnectCallback = callback;
    }

    public void shareOnlineInfo(@AudioConstants.OnlineInfoSource int dataSource) {
        Log.d(TAG, "[AudioApp] shareAudioSourceToAssistant "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * dataSource: " + dataSource);
        if (mConnectCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putBoolean(
                    AudioConstants.BundleKey.AUDIO_DATA_SOURCE,
                    (dataSource == AudioConstants.OnlineInfoSource.XMLY));
            mConnectCallback.setData(callbackData);
            mConnectCallback.callback();
        }
    }
}
