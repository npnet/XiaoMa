package com.xiaoma.center;

import com.xiaoma.center.IClient;
import com.xiaoma.center.ILinker;
import com.xiaoma.center.IServerListener;
import com.xiaoma.center.logic.model.SourceInfo;

/**
 * 中心服务顶层接口
 */
interface ICenterServer {
    /**
     * 用于先中心服务注册客户端
     *
     * @param packageName 声明客户端归属的应用包名
     * @param client 客户端实例
     * @return 返回注册客户端状态码
     * @throws RemoteException
     */
     int register(in SourceInfo source, IClient client);

    /**
     * 用于本地添加中心服务状态的监听
     *
     * @param listener 监听器
     * @return 返回添加监听器的状态,成功返回true, 否则返回false
     * @throws RemoteException
     */
     boolean addServerListener(IServerListener listener);

    /**
     * 用于判断客户端是否存在
     *
     * @param location 客户端地址
     * @param port 客户端端口
     * @return 返回客户端是否活跃,活跃返回true, 否则返回false
     * @throws RemoteException
     */
     boolean isClientAlive(in SourceInfo source);

    /**
     * 用于本地客户端获取连接器
     *
     * @return 连接器对象,应该在服务成功连接后再获取,一旦服务断开本地应该销毁
     * @throws RemoteException
     */
     ILinker getLinker();

}
