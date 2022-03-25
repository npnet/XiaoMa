package com.xiaoma.center.logic.agent;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoma.center.IClient;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.model.SourceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端管理器,负责
 * 1、保存注册到中心服务的客户端
 * 2、客户端断开时及时清理失效客户端
 * 3、提供客户端获取功能
 * 4、提供客户端活跃状态查询功能
 *
 * @author youthyJ
 * @date 2019/1/17
 */
class ClientManager {
    private static ClientManager instance;
    private Map<SourceInfo, IClient> sourceMap = new HashMap<>();

    private ClientManager() {
    }

    public static ClientManager getInstance() {
        if (instance == null) {
            synchronized (ClientManager.class) {
                if (instance == null) {
                    instance = new ClientManager();
                }
            }
        }
        return instance;
    }

    public int registerClient(SourceInfo source, IClient client) throws RemoteException {
        if (source == null) {
            return ErrorCode.CODE_SOURCE_INFO_ILLEGAL;
        }
        String location = source.getLocation();
        int port = source.getPort();
        if (TextUtils.isEmpty(location) || location.trim().isEmpty()) {
            // location不合法
            return ErrorCode.CODE_LOCATION_ILLEGAL;
        }
        if (port < 0) {
            // port不合法
            return ErrorCode.CODE_PORT_ILLEGAL;
        }
        if (sourceMap.containsKey(source)) {
            // 已经注册了的源客户端,需要移除掉旧的源客户端再重新注册
            // 或者使用其他端口号
            return ErrorCode.CODE_PORT_OCCUPIED;
        } else {
            sourceMap.put(source, client);
        }
        handleClientDead(source);
        return ErrorCode.CODE_SUCCESS;
    }

    @Nullable
    IClient getClient(SourceInfo source) {
        if (source == null) {
            return null;
        }
        if (!isClientAlive(source)) {
            return null;
        }
        return sourceMap.get(source);
    }

    boolean isClientAlive(SourceInfo source) {
        if (source == null) {
            return false;
        }

        IClient client = sourceMap.get(source);
        if (client == null) {
            return false;
        }
        return client.asBinder().isBinderAlive();
    }

    private void handleClientDead(final SourceInfo source) throws RemoteException {
        if (source == null) {
            return;
        }
        String sourceLocation = source.getLocation();
        int sourcePort = source.getPort();
        if (TextUtils.isEmpty(sourceLocation) || sourceLocation.trim().isEmpty()) {
            return;
        }
        if (sourcePort < 0) {
            return;
        }
        IClient client = getClient(source);
        if (client == null) {
            return;
        }
        final IBinder binder = client.asBinder();
        binder.linkToDeath(new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                removeClient(source);
                AgentStateManager.getInstance().onClientOut(source);
            }
        }, 0);
    }

    private boolean removeClient(SourceInfo source) {
        if (source == null) {
            return false;
        }
        sourceMap.remove(source);
        return true;
    }
}
