package com.xiaoma.plugin.download;


import android.text.TextUtils;

import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.StringUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class DownLoadTask implements Runnable {
    public interface IDownLoadListen {

        void onFailed(String filePath);

        void onFinished(String filePath);
    }

    public static final String TEMP_SUFFIX = ".temp";
    private String downLoadUrl;
    private String localDir;
    private IDownLoadListen onDownFileListener;
    private long serverFileSize = -1;
    private String suffix = TEMP_SUFFIX;


    public DownLoadTask(String downLoadUrl, String localDir) {
        this.downLoadUrl = downLoadUrl;
        this.localDir = localDir;
        this.suffix = StringUtil.getFileSuffixByUrl(downLoadUrl);
    }

    public DownLoadTask(String downLoadUrl, String localDir, long serverFileSize, IDownLoadListen listener) {
        this(downLoadUrl, localDir);
        this.onDownFileListener = listener;
        this.serverFileSize = serverFileSize;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(localDir)) {
            sendProgressMsg(DownLoadState.FAILED);
            return;
        }
        if (!localDir.endsWith("/")) localDir += "/";
        mkLocalDirs(localDir);
        final File finishFile = new File(StringUtil.getRealFilePath(localDir, downLoadUrl, suffix));
        if (checkFileSize(finishFile, serverFileSize)) {
            sendProgressMsg(DownLoadState.FINISH);
            return;
        } else {
            finishFile.delete();
        }
        //报错，暂时注释
        //XmHttp.getDefault().updateCommonParams(null);
        XmHttp.getDefault().getFile(downLoadUrl, new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                File body = response.body();
                FileUtils.copy(body, finishFile);
                sendProgressMsg(DownLoadState.FINISH);
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                sendProgressMsg(DownLoadState.FAILED);
            }
        });
    }

    private boolean checkFileSize(File file, long serverFileSize) {
        if (file != null && file.exists()) {
            return ((serverFileSize > 0 && file.length() == serverFileSize) || serverFileSize <= 0);
        }
        return false;
    }


    private void mkLocalDirs(String dirs) {
        File dir = new File(dirs);
        if (!dir.exists()) dir.mkdirs();
    }


    /**
     * 发送状态信息到主线程
     *
     * @param state
     */
    private void sendProgressMsg(final DownLoadState state) {
        if (onDownFileListener == null) return;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
              if (state == DownLoadState.FINISH) {
                    onDownFileListener.onFinished(StringUtil.getRealFilePath(localDir, downLoadUrl, suffix));
                } else if (state == DownLoadState.FAILED) {
                    onDownFileListener.onFailed(StringUtil.getRealFilePath(localDir, downLoadUrl, TEMP_SUFFIX));
                }
            }
        });

    }


}
