package com.xiaoma.center;

import com.xiaoma.center.IClient;
import android.os.Bundle;
import com.xiaoma.center.logic.model.Response;

/**
 * 中心服务客户端回调接口
 */
interface IClientCallback {
    /**
     * 客户端回调数据的接口
     *
     * @param bundle 回调的数据
     * @throws RemoteException
     */
    void callback(in Response response);

    boolean isBinderAlive();
}
