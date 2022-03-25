package com.xiaoma.center.logic.agent;

import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.center.IServerListener;
import com.xiaoma.center.logic.model.SourceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 中心服务状态分发器
 * 1、接管中心服务中客户端注册状态监听的业务
 * 2、为中心服务进程中其他对象分发客户端接入/退出状态
 *
 * @author youthyJ
 * @date 2019/1/18
 */
class AgentStateManager {
    private static AgentStateManager instance;
    private List<IServerListener> remoteListenerList = new ArrayList<>();
    private List<StateListener> localListenerList = new ArrayList<>();

    static AgentStateManager getInstance() {
        if (instance == null) {
            synchronized (AgentStateManager.class) {
                if (instance == null) {
                    instance = new AgentStateManager();
                }
            }
        }
        return instance;
    }

    boolean addLocalStateListener(StateListener listener) {
        if (listener == null) {
            return false;
        }
        if (localListenerList.contains(listener)) {
            return true;
        }
        return localListenerList.add(listener);
    }

    boolean removeLocalStateListener(StateListener listener) {
        return localListenerList.remove(listener);
    }

    boolean handleClientServiceListener(IServerListener listener) throws RemoteException {
        if (listener == null) {
            return false;
        }
        if (!listener.asBinder().isBinderAlive()) {
            return false;
        }
        handleListenerDead(listener);
        if (remoteListenerList.contains(listener)) {
            return true;
        }
        return remoteListenerList.add(listener);
    }

    void onClientIn(SourceInfo source) {
        // 分发给本地客户端
        for (int i = remoteListenerList.size() - 1; i >= 0; i--) {
            IServerListener listener = remoteListenerList.get(i);
            if (listener == null) {
                continue;
            }
            if (!listener.asBinder().isBinderAlive()) {
                continue;
            }
            try {
                listener.onClientIn(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 分发给中心服务进程
        for (int i = localListenerList.size() - 1; i >= 0; i--) {
            StateListener listener = localListenerList.get(i);
            if (listener == null) {
                continue;
            }
            try {
                listener.onClientIn(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void onClientOut(SourceInfo source) {
        // 分发给本地客户端
        for (int i = remoteListenerList.size() - 1; i >= 0; i--) {
            IServerListener listener = remoteListenerList.get(i);
            if (listener == null) {
                continue;
            }
            if (!listener.asBinder().isBinderAlive()) {
                continue;
            }
            try {
                listener.onClientOut(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 分发给中心服务进程
        for (int i = localListenerList.size() - 1; i >= 0; i--) {
            StateListener listener = localListenerList.get(i);
            if (listener == null) {
                continue;
            }
            try {
                listener.onClientOut(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleListenerDead(final IServerListener listener) throws RemoteException {
        if (listener == null) {
            return;
        }
        final IBinder binder = listener.asBinder();
        binder.linkToDeath(new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                remoteListenerList.remove(listener);
            }
        }, 0);
    }

    static class StateListener {
        void onClientIn(SourceInfo in) {
        }

        void onClientOut(SourceInfo out) {
        }
    }
}
