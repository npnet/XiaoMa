package com.xiaoma.vr.dispatch;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.model.WakeupWord;

import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_ACTION_CANCEL_WAKEUP_WORD;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_PORT_WAKEUP_WORD;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.CLIENT_LOCATION;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_WAKEUP_WORD;

/**
 * @author youthyJ
 * @date 2019/3/24
 */
public final class Wakeup {
    private static final String TAG = Wakeup.class.getSimpleName() + "_LOG";

    private Wakeup() throws Exception {
        throw new Exception();
    }

    public static boolean register(Context context, String word) {
        KLog.d(TAG, "[app] register:" + word);
        if (context == null) {
            return false;
        }
        String packageName = context.getPackageName();
        WakeupWord wakeupWord = new WakeupWord(packageName, word);
        SourceInfo local = new SourceInfo(packageName, BUSINESS_PORT_WAKEUP_WORD);
        SourceInfo remote = new SourceInfo(CLIENT_LOCATION, BUSINESS_PORT_WAKEUP_WORD);
        RequestHead head = new RequestHead(remote, BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD);
        Bundle extra = new Bundle();
        extra.putParcelable(DATA_KEY_WAKEUP_WORD, wakeupWord);
        Request request = new Request(local, head, extra);
        int send = Linker.getInstance().send(request);
        return ErrorCode.CODE_SUCCESS == send;
    }

    public static boolean remove(Context context, String word) {
        KLog.d(TAG, "[app] remove:" + word);
        if (context == null) {
            return false;
        }
        String packageName = context.getPackageName();
        WakeupWord wakeupWord = new WakeupWord(packageName, word);
        SourceInfo local = new SourceInfo(packageName, BUSINESS_PORT_WAKEUP_WORD);
        SourceInfo remote = new SourceInfo(CLIENT_LOCATION, BUSINESS_PORT_WAKEUP_WORD);
        RequestHead head = new RequestHead(remote, BUSINESS_ACTION_CANCEL_WAKEUP_WORD);
        Bundle extra = new Bundle();
        extra.putParcelable(DATA_KEY_WAKEUP_WORD, wakeupWord);
        Request request = new Request(local, head, extra);
        int send = Linker.getInstance().send(request);
        return ErrorCode.CODE_SUCCESS == send;
    }
}
