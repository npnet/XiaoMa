package com.xiaoma.dualscreen.manager;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;

import java.util.HashMap;

/**
 * Created by Lai on 2019/5/16 20:41
 * Desc:
 */
public abstract class DualApiManager {

    public Context context;
    private String remoteApp;
    private int localPort;
    private int remotePort;
    private HashMap<String, SourceInfo> sourceInfoMap = new HashMap<>();

    public abstract void initRemote();

    public void init(Context context, int localPort) {
        this.context = context;
        this.localPort = localPort;
        initRemote();
    }

    void initRemote(String remoteApp, int remotePort) {
        this.remoteApp = remoteApp;
        this.remotePort = remotePort;
    }

    public void init() {

    }

    public void init(Context context){
        this.context = context;
    }

    int request(int action, Bundle bundle, IClientCallback callback) {
        return request(remotePort, action, bundle, callback);
    }

    int request(int port, int action, Bundle bundle, IClientCallback callback) {
        int code = Linker.getInstance().request(getRequest(port, action, bundle), callback);
        KLog.d("request: " + code);
        if (code == ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND && !TextUtils.isEmpty(getAppName())) {
            KLog.d("Please open app first : " + remoteApp + " port=" + port);
        }
        return code;
    }

    int requestWithoutTTS(int port, int action, Bundle bundle, IClientCallback callback) {
        int code = Linker.getInstance().request(getRequest(port, action, bundle), callback);
        KLog.d("request: " + code);
        return code;
    }

    int requestWithoutTTS(int action, Bundle bundle, IClientCallback callback) {
        return requestWithoutTTS(remotePort, action, bundle, callback);
    }

    private Request getRequest(int port, int action, Bundle bundle) {
        return new Request(getSourceInfo(context.getPackageName(), localPort), new RequestHead(getSourceInfo(remoteApp, port), action), bundle);
    }

    private SourceInfo getSourceInfo(String app, int port) {
        String id = app + "/" + port;
        if (!sourceInfoMap.containsKey(id)) {
            sourceInfoMap.put(id, new SourceInfo(app, port));
        }
        return sourceInfoMap.get(id);
    }

    String getAppName() {
        return null;
    }

    public interface OnTrueListener {
        void onTrue();
        void onFalse();
    }

    public interface onGetBooleanResultListener {
        void onTrue();

        void onFalse();
    }

    public interface onGetStringResultListener {
        void onSuccess(String result);

        void onFailed();
    }
}
