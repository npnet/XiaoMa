package com.xiaoma.center.logic.agent;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.xiaoma.center.IClient;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.ILinker;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 中继连接器,负责:
 * 1、将本地客户端的请求转发给目标客户端
 * 2、维护本地客户端请求的长时间的状态回调列表
 * 3、当客户端断开时及时清理对应客户端的回调
 *
 * @author youthyJ
 * @date 2019/1/17
 */
class AgentLinker extends ILinker.Stub {
    private static final String TAG = AgentLinker.class.getSimpleName() + "_LOG";

    private static AgentLinker instance;
    private Map<RequestHead, AgentProxy> intentMap = new ConcurrentHashMap<>();

    private AgentLinker() {
        handleClientOut();
    }

    public static AgentLinker getInstance() {
        if (instance == null) {
            synchronized (AgentLinker.class) {
                if (instance == null) {
                    instance = new AgentLinker();
                }
            }
        }
        return instance;
    }

    private void handleClientOut() {
        AgentStateManager.getInstance().addLocalStateListener(new AgentStateManager.StateListener() {
            @Override
            void onClientOut(SourceInfo out) {
                if (out == null) {
                    return;
                }
                if (intentMap == null || intentMap.isEmpty()) {
                    return;
                }
                // 发现有remoteClient断开后
                // 清理AgentLinker中维护的remoteConnect
                Iterator<Map.Entry<RequestHead, AgentProxy>> iterator = intentMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<RequestHead, AgentProxy> entry = iterator.next();
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

    private void removeAgentProxy(RequestHead target) {
        if (intentMap != null) {
            intentMap.remove(target);
        }
    }

    @Override
    public int send(Request request) {
        KLog.d(TAG, "[send] " + request);
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return ErrorCode.CODE_REQUEST_ERROR;
        }
        RequestHead head = request.getIntent();
        SourceInfo remote = head.getRemote();
        IClient remoteClient = ClientManager.getInstance().getClient(remote);
        if (remoteClient == null) {
            return ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND;
        }
        if (!remoteClient.asBinder().isBinderAlive()) {
            return ErrorCode.CODE_REMOTE_CLIENT_DEAD;
        }
        try {
            return remoteClient.send(head.getAction(), request.getExtra());
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    public int request(Request request, IClientCallback callback) {
        KLog.d(TAG, "[request] " + request);
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return ErrorCode.CODE_REQUEST_ERROR;
        }
        RequestHead intent = request.getIntent();
        SourceInfo remote = intent.getRemote();
        IClient remoteClient = ClientManager.getInstance().getClient(remote);
        if (remoteClient == null) {
            return ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND;
        }
        if (!remoteClient.asBinder().isBinderAlive()) {
            return ErrorCode.CODE_REMOTE_CLIENT_DEAD;
        }
        try {
            return remoteClient.request(intent.getAction(), request.getExtra(), callback);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.CODE_EXCEPTION;
        }
    }

    @Override
    public int connect(Request request, IClientCallback callback) {
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return ErrorCode.CODE_REQUEST_ERROR;
        }

        SourceInfo local = request.getLocal();
        RequestHead intent = request.getIntent();
        SourceInfo remote = request.getIntent().getRemote();
        AgentProxy agentProxy = intentMap.get(intent);
        if (agentProxy == null) {
            synchronized (AgentLinker.class) { // 避免并发重复创建实例
                agentProxy = intentMap.get(intent);
                if (agentProxy == null) {
                    // 二级代理回调不存在, 需要先把二级代理注册到目标客户端
                    KLog.d(TAG, "[connect first] " + request);
                    agentProxy = new AgentProxy(local, intent);
                    agentProxy.putCallback(local, callback);
                    IClient remoteClient = ClientManager.getInstance().getClient(remote);
                    if (remoteClient == null) {
                        return ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND;
                    }
                    if (!remoteClient.asBinder().isBinderAlive()) {
                        return ErrorCode.CODE_REMOTE_CLIENT_DEAD;
                    }
                    try {
                        int code = remoteClient.connect(intent.getAction(), request.getExtra(), agentProxy);
                        if (code == ErrorCode.CODE_SUCCESS) {
                            // 连接远程成功后在将业务回调添加到集合中
                            intentMap.put(intent, agentProxy);
                        }
                        return code;
                    } catch (Exception e) {
                        e.printStackTrace();
                        removeAgentProxy(intent);
                        return ErrorCode.CODE_EXCEPTION;
                    }
                } else {
                    KLog.d(TAG, "[connect] " + request);
                    agentProxy.putCallback(local, callback);
                    return ErrorCode.CODE_SUCCESS;
                }
            }
        } else {
            // 二级代理已存在,说明二级代理已经注册到目标客户端
            // 直接添加到分发列表就可以
            KLog.d(TAG, "[connect] " + request);
            agentProxy.putCallback(local, callback);
            return ErrorCode.CODE_SUCCESS;
        }
    }

    @Override
    public int disconnect(Request request, IClientCallback callback) {
        KLog.d(TAG, "[disconnect] " + request);
        if (request == null || request.getIntent() == null || request.getIntent().getRemote() == null) {
            return ErrorCode.CODE_REQUEST_ERROR;
        }
        SourceInfo local = request.getLocal();
        RequestHead intent = request.getIntent();
        SourceInfo remote = intent.getRemote();
        AgentProxy agentProxy = intentMap.get(intent);
        if (agentProxy == null || agentProxy.isEmpty()) {
            // 没有建立过connect, 无需处理
            return ErrorCode.CODE_SUCCESS;
        }
        agentProxy.removeCallback(local);
        if (agentProxy.isEmpty()) {
            removeAgentProxy(intent);
            // 该head的本地callback列表均已移除,可以将回调从目标客户端移除了
            IClient remoteClient = ClientManager.getInstance().getClient(remote);
            if (remoteClient == null) {
                return ErrorCode.CODE_REMOTE_CLIENT_NOT_FOUND;
            }
            if (remoteClient.asBinder().isBinderAlive()) {
                return ErrorCode.CODE_REMOTE_CLIENT_DEAD;
            }
            try {
                return remoteClient.disconnect(intent.getAction(), request.getExtra());
            } catch (Exception e) {
                e.printStackTrace();
                return ErrorCode.CODE_EXCEPTION;
            }
        }
        return ErrorCode.CODE_SUCCESS;
    }

    /**
     * localConnectList,负责:
     * 1、把location不同,但是requestHead相同的localConnect统一管理
     * 2、当remoteClient回调时,将回调分发给所有localConnect
     */
    private static class AgentProxy extends IClientCallback.Stub {
        private RequestHead target;
        private SourceInfo local;
        private Map<SourceInfo, IClientCallback> callbackMap = new HashMap<>();

        private AgentProxy(SourceInfo local, RequestHead target) {
            this.target = target;
            this.local = local;
        }

        @Override
        public void callback(Response response) {
            Log.d(TAG, "AgentLinker callback:" + local);
            // 将目标客户端的回调转发给中心服务中保留的所有一级回调代理
            Collection<IClientCallback> callbacks = callbackMap.values();

            for (IClientCallback callback : callbacks) {
                if (callback == null) {
                    continue;
                }
                if (!callback.asBinder().isBinderAlive()) {
                    // 源客户端已经离线
                    continue;
                }
                try {
                    // 防止某个callback发生异常导致分发中断
                    callback.callback(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean putCallback(SourceInfo local, IClientCallback callback) {
            if (local == null) {
                return false;
            }
            if (callback == null) {
                return false;
            }
            handleCallbackDead(local, callback);
            // LocalLinker已经将head相同的回调进行简并,
            // 也就是说同一个location只会有一个相同的requestHead连接
            // 所以不同location在AgentLinker中只需要各自维护一个localConnect即可
            local.setPort(CenterConstants.LOCAL_LINKER_PORT);
            if (callbackMap.containsKey(local)) {
                return true;
            }
            callbackMap.put(local, callback);
            return true;
        }

        private boolean isEmpty() {
            return callbackMap.isEmpty();
        }

        private void clear() {
            callbackMap.clear();
        }

        private void handleCallbackDead(final SourceInfo local, IClientCallback callback) {
            if (callback == null) {
                return;
            }
            final IBinder binder = callback.asBinder();
            try {
                binder.linkToDeath(new DeathRecipient() {
                    @Override
                    public void binderDied() {
                        binder.unlinkToDeath(this, 0);
                        removeCallback(local);
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private boolean removeCallback(SourceInfo local) {
            if (local == null) {
                return false;
            }
            local.setPort(CenterConstants.LOCAL_LINKER_PORT);
            if (callbackMap.containsKey(local)) {
                KLog.d(TAG, "[remove local connect] " + local.getLocation());
                callbackMap.remove(local);
                if (isEmpty() && target != null) {
                    // 该RequestHead中已经没有任何localConnect了
                    // 可以将AgentLinker注册到remoteClient的remoteConnect移除了
                    AgentLinker.getInstance().removeAgentProxy(target);
                    IClient remoteClient = ClientManager.getInstance().getClient(target.getRemote());
                    if (remoteClient != null && remoteClient.asBinder().isBinderAlive()) {
                        try {
                            KLog.d(TAG, "[disconnect remote connect] " + target);
                            remoteClient.disconnect(target.getAction(), null);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean isBinderAlive() {
            // 当中心服务中的代理回调中已经没有本地回调时,
            // 表示这个中间代理已失效;
            // 重写这个方法是因为Binder中默认会有这个方法的实现,
            // 这样我们继承IClientCallback就只需要实现一个callback接口
            if (callbackMap == null || callbackMap.isEmpty()) {
                return false;
            }
            return super.isBinderAlive();
        }
    }

}
