package com.xiaoma.center;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.model.Request;

/**
 * 中心服务向客户端提供的连接器
 */
interface ILinker {
    /**
     * 用于向客户端发送数据
     *
     * @param request 发送的请求
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int send(in Request request);

    /**
     * 用于向客户端请求数据,单次有效,用后即弃
     *
     * @param request 发送的请求
     * @param callback 客户端处理后的反馈
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int request(in Request request, IClientCallback callback);

    /**
     * 用于向客户端添加监听,长时间有效,需要手动断开连接
     *
     * @param request 发送的请求
     * @param callback 客户端条件触发时的反馈
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int connect(in Request request, IClientCallback callback);

    /**
     * 用于移除客户端中的监听,对于客户端来说,同一个action,
     * 不同的目标客户端只需注册一个
     *
     * @param request 发送的请求
     * @param callback 需要移除的回调对象
     * @return 返回客户端执行的状态码
     * @throws RemoteException
     */
    int disconnect(in Request request, IClientCallback callback);
}
