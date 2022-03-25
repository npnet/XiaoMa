package com.xiaoma.center.logic.remote;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.IClient;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.model.SourceInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 客户端基类,负责:
 * 1、接收其他客户端发送过来的数据
 * 2、响应其他客户端数据请求
 * 3、与其他客户端建立连接
 * 4、维护建立连接的客户端列表
 *
 * @author youthyJ
 * @date 2019/1/16
 */
public abstract class Client extends IClient.Stub {
    private Context appContext;
    private SourceInfo source;
    private List<Integer> connectActionList = new ArrayList<>();

    public Client(Context context, int port) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (port < 0) {
            throw new IllegalArgumentException("port illegal");
        }
        appContext = context.getApplicationContext();
        String packageName = appContext.getPackageName();
        source = new SourceInfo(packageName, port);
    }

    public SourceInfo getSource() {
        return source;
    }

    @Override
    final public int send(int action, Bundle data) {
        try {
            onReceive(action, data);
            return ErrorCode.CODE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    final public int request(int action, Bundle data, IClientCallback callback) {
        ClientCallback callbackProxy = new CallbackBuilder().with(this)
                .ori(callback)
                .type(CallbackBuilder.Type.Request)
                .action(action)
                .build();
        try {
            onRequest(action, data, callbackProxy);
            return ErrorCode.CODE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    final public int connect(int action, Bundle data, IClientCallback callback) {
        Log.d("AbsAudioClient", "onConnect,action=  client " + action+",callback:"+callback.getClass());
        connectActionList.add(action);
        ClientCallback callbackProxy = new CallbackBuilder().with(this)
                .ori(callback)
                .type(CallbackBuilder.Type.Connect)
                .action(action)
                .build();
        try {
            onConnect(action, data, callbackProxy);
            return ErrorCode.CODE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    final public int disconnect(int action, Bundle data) {
        connectActionList.remove((Integer) action);
        return ErrorCode.CODE_SUCCESS;
    }

    public boolean isConnectActionValid(int action) {
        return connectActionList.contains(action);
    }

    protected abstract void onReceive(int action, Bundle data);

    protected abstract void onRequest(int action, Bundle data, ClientCallback callback);

    protected abstract void onConnect(int action, Bundle data, ClientCallback callback);
}
