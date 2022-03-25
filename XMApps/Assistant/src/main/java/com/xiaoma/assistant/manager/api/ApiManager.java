package com.xiaoma.assistant.manager.api;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.vr.tts.OnTtsListener;

import java.util.HashMap;

/**
 * Created by qiuboxiang on 2019/2/28 20:41
 * Desc:
 */
public abstract class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();
    private static final int CODE_NET_ERROR = -1000;
    public Context context;
    private String remoteApp;
    private int localPort;
    private int remotePort;
    private int connectStatus;
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

    private void send(int port, int action, Bundle bundle) {
        Linker.getInstance().send(getRequest(port, action, bundle));
    }

    int request(int action, Bundle bundle, IClientCallback callback) {
        return request(remotePort, action, bundle, callback);
    }

    int request(int port, int action, Bundle bundle, IClientCallback callback) {
        int code = Linker.getInstance().request(getRequest(port, action, bundle), callback);
        Log.d("QBX", "request: " + code);
        if (code == ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND && !TextUtils.isEmpty(getAppName())) {
            Log.d("QBX", "Please open app first : " + remoteApp + " port=" + port);
            addFeedbackAndSpeak(getString(R.string.please_open_app, getAppName()));
        }
        return code;
    }

    int requestWithoutTTS(int port, int action, Bundle bundle, IClientCallback callback) {
        int code = Linker.getInstance().request(getRequest(port, action, bundle), callback);
        Log.d("QBX", "request: " + code);
        return code;
    }

    int requestWithoutTTS(int action, Bundle bundle, IClientCallback callback) {
        return requestWithoutTTS(remotePort, action, bundle, callback);
    }

    int requestWithCheckNet(int action, Bundle bundle, IClientCallback callback) {
        if (!checkNet()) return CODE_NET_ERROR;
        return request(remotePort, action, bundle, callback);
    }

    int requestWithCheckNet(int port, int action, Bundle bundle, IClientCallback callback) {
        if (!checkNet()) return CODE_NET_ERROR;
        return request(port, action, bundle, callback);
    }

    boolean checkNet() {
        if (!NetworkUtils.isConnected(context)) {
            addFeedbackAndSpeak(getString(R.string.network_errors, getAppName()));
            return false;
        }
        return true;
    }

    public void connect(int port, int action, Bundle bundle, IClientCallback callback) {
        connectStatus = Linker.getInstance().connect(getRequest(port, action, bundle), callback);
        Log.d("QBX", "connect: port=" + port + " status=" + connectStatus);
    }

    boolean isConnectFailed() {
        return connectStatus == ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND;
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

    void send(int action, Bundle bundle) {
        send(remotePort, action, bundle);
    }

    public void onClientIn(SourceInfo source) {

    }

    String getAppName() {
        return null;
    }

    public String getRemoteApp() {
        return remoteApp;
    }

    public interface OnTrueListener {
        void onTrue();
    }

    public interface onGetBooleanResultListener {
        void onTrue();

        void onFalse();
    }

    public class SimpleGetBooleanResultListener implements onGetBooleanResultListener {

        @Override
        public void onTrue() {

        }

        @Override
        public void onFalse() {

        }
    }

    public interface onGetStringResultListener {
        void onSuccess(String result);

        void onFailed();
    }

    public interface onGetIntResultListener {
        void onSuccess(int result);
    }

    IClientCallback getBooleanResultClientCallback(final onGetBooleanResultListener listener) {
        return new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(AudioConstants.BundleKey.RESULT);
                if (success) {
                    listener.onTrue();
                } else {
                    listener.onFalse();
                }
            }
        };
    }

    IClientCallback getStringResultClientCallback(final onGetStringResultListener listener) {
        return new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                String content = extra.getString(AudioConstants.BundleKey.RESULT);
                if (!TextUtils.isEmpty(content)) {
                    listener.onSuccess(content);
                } else {
                    listener.onFailed();
                }
            }
        };
    }

    IClientCallback getIntResultClientCallback(final onGetIntResultListener listener) {
        return new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                int content = extra.getInt(AudioConstants.BundleKey.RESULT);
                listener.onSuccess(content);
            }
        };
    }

    protected void addFeedbackAndSpeak(final String text) {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                AssistantManager.getInstance().addFeedBackConversation(text);
                AssistantManager.getInstance().speakContent(text);
            }
        });
    }

    protected void addFeedbackAndSpeak(final String text, OnTtsListener onIatListener) {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                AssistantManager.getInstance().addFeedBackConversation(text);
                AssistantManager.getInstance().speakContent(text, onIatListener);
            }
        });
    }

    protected void addFeedbackAndSpeakMultiTone(final String showtext, final String speaktext, OnTtsListener onIatListener) {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                AssistantManager.getInstance().addFeedBackConversation(showtext);
                AssistantManager.getInstance().speakContent(speaktext, onIatListener);
            }
        });
    }

    protected void addFeedbackAndSpeak(int id) {
        addFeedbackAndSpeak(context.getString(id));
    }

    public String getString(int id, Object... args) {
        return StringUtil.format(context.getString(id), args);
    }

    public void speakContent(String content) {
        AssistantManager.getInstance().speakContent(content);
    }

    public void speakContent(int id) {
        speakContent(context.getString(id));
    }

    public void speakContent(int id, Object... args) {
        speakContent(getString(id, args));
    }

    public void speakContent(int id, final OnTtsListener listener) {
        speakContent(context.getString(id), listener);
    }

    public void speakContent(String content, final OnTtsListener listener) {
        AssistantManager.getInstance().speakContent(content, listener);
    }

    public void closeAfterSpeak(String content) {
        AssistantManager.getInstance().addFeedBackConversation(content);
        speakContent(content, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }
        });
    }

    void closeAfterSpeak(int id) {
        closeAfterSpeak(context.getString(id));
    }

    void closeDialogAndTTS(boolean success, String ttsContent) {
        if (success) {
            if (!TextUtils.isEmpty(ttsContent)) {
                closeAfterSpeak(ttsContent);
            } else {
                AssistantManager.getInstance().closeAssistant();
            }
        } else {
            AssistantManager.getInstance().closeAssistant();
        }
    }

    void setRobAction(int action) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(action);
    }

    void closeAssistant() {
        AssistantManager.getInstance().closeAssistant();
    }

    void speakThenListening(int id) {
        String content = context.getString(id);
        AssistantManager.getInstance().addFeedBackConversation(content);
        AssistantManager.getInstance().speakThenListening(content);
    }

}
