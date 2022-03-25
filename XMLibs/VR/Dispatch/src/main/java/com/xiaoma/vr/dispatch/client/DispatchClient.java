package com.xiaoma.vr.dispatch.client;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.model.Command;
import com.xiaoma.vr.dispatch.model.Result;

import java.util.HashMap;
import java.util.Map;

import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_ACTION_DISPATCH;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_PORT_DISPATCH;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_FOLLOW_VOICE;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_HANDLE_DETAIL;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_PACKAGE_NAME;

/**
 * 管理语音指令分发业务
 * 1、接受App注册连接,并持有连接的回调
 * 2、处理指令分发
 * 3、
 *
 * @author youthyJ
 * @date 2019/3/12
 */
public class DispatchClient extends Client {
    private static final String TAG = DispatchClient.class.getSimpleName() + "_LOG";
    private Map<Command, Bundle> handleState = new HashMap<>();
    private ClientCallback businessCallback;

    private DispatchClient(Context context) {
        super(context, BUSINESS_PORT_DISPATCH);
    }

    public static DispatchClient register(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        Context appContext = context.getApplicationContext();
        DispatchClient client = new DispatchClient(appContext);
        boolean register = Center.getInstance().register(client);
        KLog.d(TAG, "[assistant] register client: " + register);
        return client;
    }

    @Override
    protected void onReceive(int action, Bundle data) {
        if (action == BUSINESS_ACTION_DISPATCH) {
            data.setClassLoader(Command.class.getClassLoader());
            Command command = data.getParcelable(Command.class.getName());
            handleState.put(command, data);
            KLog.d(TAG, "[assistant] receive handle result: " + command);
        }
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {

    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        if (BUSINESS_ACTION_DISPATCH != action) {
            KLog.d(TAG, "[assistant] new connect, action no match: action:" + action);
            return;
        }
        KLog.d(TAG, "[assistant] new connect: " + data.getString("source"));
        businessCallback = callback;
    }

    public Result dispatchCommand(Command command) {
        KLog.d(TAG, "[assistant] start dispatch: " + command);

        Result result = new Result(command);

        if (command == null) {
            KLog.w(TAG, "[assistant] command is null");
            result.setState(Result.State.Empty);
            result.setHandleApp(Result.NO_HANDLE_APP);
            return result;
        }

        if (businessCallback == null) {
            KLog.w(TAG, "[assistant] command not handled: " + command);
            result.setState(Result.State.No_Handle);
            result.setHandleApp(Result.NO_HANDLE_APP);
            return result;
        }

        Bundle callbackData = new Bundle();
        callbackData.putParcelable(Command.class.getName(), command);

        businessCallback.setData(callbackData);
        try {
            int state = businessCallback.callback();
            if (state != ErrorCode.CODE_SUCCESS) {
                KLog.d(TAG, "* [assistant] callback app failure, remove callback!"
                        + "\n* state: " + state
                );
                result.setState(Result.State.No_Handle);
                result.setHandleApp(Result.NO_HANDLE_APP);
                businessCallback = null;
                return result;
            }
            if (isCommandHandled(command)) {
                Bundle resultData = handleState.remove(command);
                if (resultData != null) {
                    result.setState(Result.State.OK);
                    result.setHandleApp(resultData.getString(DATA_KEY_PACKAGE_NAME));
                    result.setHandleDetail(resultData.getString(DATA_KEY_HANDLE_DETAIL));
                    result.setFollowVoice(resultData.getString(DATA_KEY_FOLLOW_VOICE));
                    KLog.d(TAG, "* [assistant] command has been handled!"
                            + "\n* " + command
                            + "\n* " + result
                    );
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e(TAG, "* [assistant] exception!"
                    + "\n" + e.getMessage());
        }
        result.setState(Result.State.Empty);
        result.setHandleApp(Result.NO_HANDLE_APP);
        return result;
    }

    private boolean isCommandHandled(Command command) {
        if (command == null) {
            return false;
        }
        return handleState.containsKey(command);
    }
}
