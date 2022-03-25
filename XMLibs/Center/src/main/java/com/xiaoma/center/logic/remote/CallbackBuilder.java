package com.xiaoma.center.logic.remote;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.model.Response;

/**
 * @author youthyJ
 * @date 2019/1/20
 */
class CallbackBuilder {
    private BuildInfo info;

    public CallbackBuilder() {
        info = new BuildInfo();
    }

    public CallbackBuilder with(Client client) {
        info.client = client;
        return this;
    }

    public CallbackBuilder type(Type type) {
        info.type = type;
        return this;
    }

    public CallbackBuilder ori(IClientCallback callback) {
        info.callback = callback;
        return this;
    }

    public CallbackBuilder action(int action) {
        info.action = action;
        return this;
    }

    public ClientCallback build() {
        ClientCallbackProxy proxy = new ClientCallbackProxy();
        proxy.setInfo(info);
        return proxy;
    }

    public enum Type {
        Request,
        Connect,
    }

    private static class ClientCallbackProxy extends ClientCallback {
        BuildInfo info;

        private void setInfo(BuildInfo info) {
            this.info = info;
        }

        @Override
        public int callback() {
            if (info.callback == null) {
                return ErrorCode.CODE_CALLBACK_NOT_FOUND;
            }
            if (info.client == null) {
                return ErrorCode.CODE_SOURCE_INFO_ILLEGAL;
            }
            if (info.type == null) {
                return ErrorCode.CODE_CALLBACK_TYPE_ILLEGAL;
            }
            if (info.type.equals(Type.Connect)) {
                if (!info.client.isConnectActionValid(info.action)) {
                    return ErrorCode.CODE_CALLBACK_ACTION_INVALID;
                }
            }
            if (!info.callback.asBinder().isBinderAlive()) {
                return ErrorCode.CODE_REMOTE_CLIENT_DEAD;
            }
            Response response = new Response(info.client.getSource(), getData());
            try {
                if (!info.callback.isBinderAlive()) {
                    return ErrorCode.CODE_CALLBACK_EMPTY;
                }
                info.callback.callback(response);
                setData(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ErrorCode.CODE_SUCCESS;
        }
    }

    private class BuildInfo {
        int action;
        Type type;
        Client client;
        IClientCallback callback;
    }
}
