package com.xiaoma.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.model.NetworkChangedEvent;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * 网络变化监听
 * Created by zhushi.
 * Date: 2018/11/30
 */
public class NetworkChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnect = NetworkUtils.isConnected(context);
        KLog.d("zs", "NetworkChangedReceiver isConnect:" + isConnect);
        NetworkChangedEvent event = new NetworkChangedEvent();
        event.setConnect(isConnect);
        //网络连接
        if (isConnect) {
            //获取失败的下载任务
            List<DownloadTask> errorTasks = OkDownload.restore(DownloadManager.getInstance().getError());
            if (errorTasks != null && errorTasks.size() > 0) {
                event.setDownloadTaskList(errorTasks);
            }
        }
        EventBus.getDefault().post(event, AppStoreConstants.MSG_NETWORK_CHANGED);
    }
}
