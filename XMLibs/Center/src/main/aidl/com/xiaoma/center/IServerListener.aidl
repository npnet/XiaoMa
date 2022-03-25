package com.xiaoma.center;

import com.xiaoma.center.IClient;
import com.xiaoma.center.logic.model.SourceInfo;

/**
 * 中心服务状态监听器
 */
interface IServerListener {
    /**
     * 用于中心服务向本地管理端反馈客户端接入
     *
     * @param source 客户端
     * @throws RemoteException
     */
    void onClientIn(in SourceInfo source);

    /**
     * 用于中心服务向本地管理端反馈客户端退出
     *
     * @param source 客户端
     * @throws RemoteException
     */
    void onClientOut(in SourceInfo source);
}
