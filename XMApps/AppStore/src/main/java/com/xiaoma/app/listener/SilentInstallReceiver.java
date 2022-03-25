package com.xiaoma.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.app.SilentInstallManager;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.utils.log.KLog;

import java.io.File;

/**
 * 静默下载安装广播receiver
 */
public class SilentInstallReceiver extends BroadcastReceiver {

    private static final String INSTALL_PATH = "install_path";
    private final static String INSTALL_ACTION = "com.xiaoma.install.action";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (!INSTALL_ACTION.equals(intent.getAction())) {
            return;
        }
        String path = intent.getStringExtra(INSTALL_PATH);
        DownloadTask task = OkDownload.request(path, OkGo.<File>get(path))
                .save()
                .register(new AppDownloadListener(path) {
                    @Override
                    public void onStart(Progress progress) {
                        KLog.d("SilentInstallReceiver onStart");
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        KLog.d("SilentInstallReceiver onProgress");
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        KLog.d("SilentInstallReceiver onFinish");
                        if (SilentInstallManager.getInstance().isBindService()) {
                            ApkUtils.silentInstall(progress.filePath);

                        } else {
                            ApkUtils.installApkFile(context, progress.filePath);
                        }
                    }
                });

        task.start();
    }
}
