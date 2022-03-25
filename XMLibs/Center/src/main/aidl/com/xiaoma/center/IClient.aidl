package com.xiaoma.center;

import com.xiaoma.center.IClientCallback;
import android.os.Bundle;

/**
 * 中心服务客户端顶层接口
 */
interface IClient {
    /**
     * 用于向客户端发送数据
     *
     * @param bundle 发送给客户端的数据
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int send(int action, in Bundle data);

    /**
     * 用于向客户端请求数据,单次有效,用后即弃
     *
     * @param bundle 发送给客户端的数据
     * @param callback 客户端处理后的反馈
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int request(int action, in Bundle data, IClientCallback callback);

    /**
     * 用于向客户端添加监听,长时间有效,需要手动断开连接
     *
     * @param bundle 发送给客户端的数据
     * @param callback 客户端条件触发时的反馈
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int connect(int action, in Bundle data, IClientCallback callback);

    /**
     * 用于移除客户端中的监听
     *
     * @param bundle 发送给客户端的数据
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int disconnect(int action, in Bundle data);
}
