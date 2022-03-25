package com.xiaoma.vr.dispatch.client;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.model.WakeupWord;

import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_ACTION_CANCEL_WAKEUP_WORD;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_PORT_WAKEUP_WORD;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_WAKEUP_WORD;

/**
 * @author youthyJ
 * @date 2019/3/23
 */
public class WakeupWordClient extends Client {
    private static final String TAG = WakeupWordClient.class.getSimpleName() + "_LOG";
    private Handler wakeupWordHandler;

    private WakeupWordClient(Context context) {
        super(context, BUSINESS_PORT_WAKEUP_WORD);
    }

    public static WakeupWordClient register(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        Context appContext = context.getApplicationContext();
        WakeupWordClient client = new WakeupWordClient(appContext);
        boolean register = Center.getInstance().register(client);
        KLog.d(TAG, "[assistant] register client: " + register);
        return client;
    }

    @Override
    protected void onReceive(int action, Bundle data) {
        if (BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD == action) {
            KLog.d(TAG, "[assistant] onReceive: BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD");
            data.setClassLoader(WakeupWord.class.getClassLoader());
            WakeupWord wakeupWord = data.getParcelable(DATA_KEY_WAKEUP_WORD);
            handleWakeupWordRegister(wakeupWord);
        }
        if (BUSINESS_ACTION_CANCEL_WAKEUP_WORD == action) {
            KLog.d(TAG, "[assistant] onReceive: BUSINESS_ACTION_CANCEL_WAKEUP_WORD");
            data.setClassLoader(WakeupWord.class.getClassLoader());
            WakeupWord wakeupWord = data.getParcelable(DATA_KEY_WAKEUP_WORD);
            handleWakeupWordRemove(wakeupWord);
        }
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {

    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {

    }

    private void handleWakeupWordRegister(WakeupWord word) {
        if (word == null) {
            return;
        }
        if (wakeupWordHandler != null) {
            wakeupWordHandler.obtainMessage(BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD, word);
        }
    }


    private void handleWakeupWordRemove(WakeupWord word) {
        if (word == null) {
            return;
        }
        if (wakeupWordHandler != null) {
            wakeupWordHandler.obtainMessage(BUSINESS_ACTION_CANCEL_WAKEUP_WORD, word);
        }
    }

    public void setWakeupWordHandler(Handler handler) {
        if (handler == null) {
            return;
        }
        this.wakeupWordHandler = handler;
    }
}
