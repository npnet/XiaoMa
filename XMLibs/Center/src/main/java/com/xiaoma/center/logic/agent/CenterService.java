package com.xiaoma.center.logic.agent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.xiaoma.center.ICenterServer;
import com.xiaoma.center.IClient;
import com.xiaoma.center.ILinker;
import com.xiaoma.center.IServerListener;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;

/**
 * 中心服务,负责:
 * 1、响应本地进程的绑定请求
 * 2、将中心服务进程的基本状态反馈给本地进程
 * 3、将本地进程注册进来的本地客户端保存
 * 4、提供中继连接器给本地客户端
 * 5、提供客户端活跃状态查询功能
 *
 * @author youthyJ
 * @date 2019/1/14
 */
public class CenterService extends Service {
    private static final String TAG = CenterService.class.getSimpleName() + "_LOG";
    private static CenterService instance;
    private ICenterServer centerService = new ICenterServer.Stub() {
        @Override
        public boolean addServerListener(IServerListener listener) throws RemoteException {
            KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * addServerListener");
            return AgentStateManager.getInstance().handleClientServiceListener(listener);
        }

        @Override
        public int register(SourceInfo source, IClient client) throws RemoteException {
            KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * register client: " + source);
            if (source == null) {
                return ErrorCode.CODE_SOURCE_INFO_ILLEGAL;
            }
            int code = ClientManager.getInstance().registerClient(source, client);
            KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * register client state: " + code + ",source:" + source);
            if (code == ErrorCode.CODE_SUCCESS) {
                AgentStateManager.getInstance().onClientIn(source);
            }
            return code;
        }

        @Override
        public boolean isClientAlive(SourceInfo source) {
            return ClientManager.getInstance().isClientAlive(source);
        }

        @Override
        public ILinker getLinker() {
            return AgentLinker.getInstance();
        }
    };

    @Nullable
    static CenterService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * onBind");
        return centerService.asBinder();
    }

}
