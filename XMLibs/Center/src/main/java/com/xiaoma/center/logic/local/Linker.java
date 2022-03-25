package com.xiaoma.center.logic.local;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;

import com.xiaoma.center.ICenterServer;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.ILinker;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaoma.center.logic.ErrorCode.CODE_REQUEST_ERROR;

/**
 * 本地中继连接器,负责:
 * 1、发送数据给目标客户端
 * 2、从客户端请求数据
 * 3、与客户端建立连接
 * 4、断开与客户端的连接
 * 5、客户端断开时及时清理连接
 *
 * @author youthyJ
 * @date 2019/1/18
 */
public class Linker extends ILinker.Stub {
    private static final String TAG = Linker.class.getSimpleName() + "_LOG";
    private static Linker instance;
    private ILinker linker;
    private Map<RequestHead, LocalProxy> intentMap = new ConcurrentHashMap<>();

    private Linker() {
        prepareLinker();
        handleServerDead();
        handleClientOut();
    }

    public static Linker getInstance() {
        if (instance == null) {
            synchronized (Linker.class) {
                if (instance == null) {
                    instance = new Linker();
                }
            }
        }
        return instance;
    }

    private void handleClientOut() {
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientOut(SourceInfo out) {
                if (out == null) {
                    return;
                }
                if (intentMap == null || intentMap.isEmpty()) {
                    return;
                }
                Iterator<Map.Entry<RequestHead, LocalProxy>> iterator = intentMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<RequestHead, LocalProxy> entry = iterator.next();
                    RequestHead head = entry.getKey();
                    if (head == null) {
                        continue;
                    }
                    if (out.equals(head.getRemote())) {
                        KLog.d(TAG, "[remove remote client] " + head);
                        iterator.remove();
                    }
                }
            }
        });
    }

    private void handleServerDead() {
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onDisconnected() {
                super.onDisconnected();
                linker = null;
            }
        });
    }

    private boolean prepareLinker() {
        if (!Center.getInstance().isConnected()) {
            return false;
        }
        ICenterServer centerServer = Center.getInstance().getCenterServer();
        try {
            linker = centerServer.getLinker();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void removeLocalProxy(RequestHead target) {
        if (intentMap != null) {
            intentMap.remove(target);
        }
    }

    @Override
    public int send(Request request) {
        KLog.d(TAG, "[send] " + request);
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return CODE_REQUEST_ERROR;
        }
        if (!Center.getInstance().isConnected()) {
            return ErrorCode.CODE_SERVER_DISCONNECTED;
        }
        if (linker == null) {
            boolean success = prepareLinker();
            if (!success) {
                return ErrorCode.CODE_LINKER_NOT_FOUND;
            }
        }
        try {
            return linker.send(request);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    public int request(Request request, IClientCallback callback) {
        KLog.d(TAG, "[request] " + request);
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return CODE_REQUEST_ERROR;
        }
        if (!Center.getInstance().isConnected()) {
            return ErrorCode.CODE_SERVER_DISCONNECTED;
        }
        if (linker == null) {
            boolean success = prepareLinker();
            if (!success) {
                return ErrorCode.CODE_LINKER_NOT_FOUND;
            }
        }
        try {
            return linker.request(request, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    public int connect(Request request, IClientCallback callback) {
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return CODE_REQUEST_ERROR;
        }
        if (!Center.getInstance().isConnected()) {
            return ErrorCode.CODE_SERVER_DISCONNECTED;
        }
        if (linker == null) {
            boolean success = prepareLinker();
            if (!success) {
                return ErrorCode.CODE_LINKER_NOT_FOUND;
            }
        }
        RequestHead intent = request.getIntent();
        LocalProxy localProxy = intentMap.get(intent);
        if (localProxy == null) {
            synchronized (Linker.class) { // 避免并发重复创建实例
                localProxy = intentMap.get(intent);
                if (localProxy == null) {
                    KLog.d(TAG, "[connect first] " + request);
                    // 一级代理不存在
                    localProxy = new LocalProxy();
                    localProxy.addCallback(callback);
                    try {
                        int code = linker.connect(request, localProxy);
                        if (code == ErrorCode.CODE_SUCCESS) {
                            // 连接远程成功后在将业务回调添加到集合中
                            intentMap.put(intent, localProxy);
                        }
                        return code;
                    } catch (Exception e) {
                        e.printStackTrace();
                        removeLocalProxy(intent);
                        return ErrorCode.CODE_EXCEPTION;
                    }
                } else {
                    KLog.d(TAG, "[connect] " + request);
                    localProxy.addCallback(callback);
                    return ErrorCode.CODE_SUCCESS;
                }
            }
        } else {
            KLog.d(TAG, "[connect] " + request);
            localProxy.addCallback(callback);
            return ErrorCode.CODE_SUCCESS;
        }
    }

    @Override
    public int disconnect(Request request, IClientCallback callback) {
        KLog.d(TAG, "[disconnect] " + request);
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return CODE_REQUEST_ERROR;
        }
        if (!Center.getInstance().isConnected()) {
            return ErrorCode.CODE_SERVER_DISCONNECTED;
        }
        if (linker == null) {
            boolean success = prepareLinker();
            if (!success) {
                return ErrorCode.CODE_LINKER_NOT_FOUND;
            }
        }
        RequestHead intent = request.getIntent();
        LocalProxy localProxy = intentMap.get(intent);
        if (localProxy == null || localProxy.isEmpty()) {
            // 没有建立过connect, 无需处理
            return ErrorCode.CODE_SUCCESS;
        }
        localProxy.removeCallback(callback);
        if (localProxy.isEmpty()) {
            // 该head的本地callback列表均已移除,可以将回调从中心服务移除了
            removeLocalProxy(intent);
            try {
                return linker.disconnect(request, localProxy);
            } catch (Exception e) {
                e.printStackTrace();
                return ErrorCode.CODE_EXCEPTION;
            }
        }
        return ErrorCode.CODE_SUCCESS;
    }

    private static class LocalProxy extends IClientCallback.Stub {
        private List<IClientCallback> callbackList = new ArrayList<>();

        private boolean addCallback(IClientCallback callback) {
            if (callback == null) {
                return false;
            }
            if (callbackList.contains(callback)) {
                return true;
            }
            return callbackList.add(callback);
        }

        private boolean removeCallback(IClientCallback callback) {
            if (callback == null) {
                return false;
            }
            return callbackList.remove(callback);
        }

        private boolean isEmpty() {
            return callbackList.isEmpty();
        }

        private void clear() {
            callbackList.clear();
        }

        @Override
        public void callback(Response response) {
            // 将中心服务的回调转发给本地回调
            for (IClientCallback callback : callbackList) {
                if (callback == null) {
                    continue;
                }
                try {
                    // 防止某个callback发生异常导致分发中断
                    callback.callback(response);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        static final int DEFAULT_LOCAL_PORT = 1080;
        private SourceInfo remote;
        private SourceInfo local;
        private int action = -1;
        private Bundle extra;
        private IClientCallback callback;
        private int type;

        public Builder() {
        }

        public Builder remote(String location, int port) {
            this.remote = new SourceInfo(location, port);
            return this;
        }

        public Builder defaultLocal(Context context) {
            this.local = new SourceInfo(context.getPackageName(), DEFAULT_LOCAL_PORT);
            return this;
        }

        public Builder local(String location, int port) {
            this.local = new SourceInfo(location, port);
            return this;
        }

        public Builder action(int action) {
            this.action = action;
            return this;
        }

        public Builder params(Bundle bundle) {
            extra = bundle;
            return this;
        }

        public Builder addParams(String key, boolean value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putBoolean(key, value);
            return this;
        }

        public Builder addParams(String key, byte value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putByte(key, value);
            return this;
        }

        public Builder addParams(String key, char value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putChar(key, value);
            return this;
        }

        public Builder addParams(String key, short value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putShort(key, value);
            return this;
        }

        public Builder addParams(String key, int value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putInt(key, value);
            return this;
        }

        public Builder addParams(String key, long value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putLong(key, value);
            return this;
        }

        public Builder addParams(String key, float value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putFloat(key, value);
            return this;
        }

        public Builder addParams(String key, double value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putDouble(key, value);
            return this;
        }

        public Builder addParams(String key, String value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putString(key, value);
            return this;
        }

        public Builder addParams(String key, CharSequence value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putCharSequence(key, value);
            return this;
        }

        public Builder addParams(String key, Parcelable value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putParcelable(key, value);
            return this;
        }

        public Builder addParams(String key, Parcelable[] value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putParcelableArray(key, value);
            return this;
        }

        public <T extends Parcelable> Builder addParams(String key, ArrayList<T> value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putParcelableArrayList(key, value);
            return this;
        }

        public <T> Builder addParams(String key, T[] value) {
            if (extra == null) {
                extra = new Bundle();
            }

            Class<?> type = value.getClass().getComponentType();

            if (type == null) {
                throw new RuntimeException("传入参数有误，获取不到参数类型");
            }

            if (type.equals(byte.class)) {
                extra.putByteArray(key, (byte[]) ((Object) value));
            } else if (type.equals(short.class)) {
                extra.putShortArray(key, (short[]) ((Object) value));
            } else if (type.equals(char.class)) {
                extra.putCharArray(key, (char[]) ((Object) value));
            } else if (type.equals(int.class)) {
                extra.putIntArray(key, (int[]) ((Object) value));
            } else if (type.equals(long.class)) {
                extra.putLongArray(key, (long[]) ((Object) value));
            } else if (type.equals(float.class)) {
                extra.putFloatArray(key, (float[]) ((Object) value));
            } else if (type.equals(double.class)) {
                extra.putDoubleArray(key, (double[]) ((Object) value));
            } else if (type.equals(String.class)) {
                extra.putStringArray(key, (String[]) value);
            } else if (type.equals(CharSequence.class)) {
                extra.putCharSequenceArray(key, (CharSequence[]) value);
            }
            return this;
        }

        public Builder addParams(String key, Serializable value) {
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putSerializable(key, value);
            return this;
        }


        public int send() {
            this.type = 1;
            return buildAndLink();
        }

        public int request(IClientCallback callback) {
            this.type = 2;
            this.callback = callback;
            return buildAndLink();
        }

        public int connect(IClientCallback callback) {
            this.type = 3;
            this.callback = callback;
            return buildAndLink();
        }

        private int buildAndLink() {
            if (local == null || remote == null || action == -1) {
                throw new RuntimeException("Params invalid");
            }

            Request request = new Request(local, new RequestHead(remote, action), extra);

            Linker instance = Linker.getInstance();
            switch (type) {
                case 1:
                    return instance.send(request);
                case 2:
                    return instance.request(request, callback);
                case 3:
                    return instance.connect(request, callback);
                default:
                    return CODE_REQUEST_ERROR;
            }
        }
    }
}
