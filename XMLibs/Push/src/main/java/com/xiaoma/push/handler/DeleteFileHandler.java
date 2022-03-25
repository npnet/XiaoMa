package com.xiaoma.push.handler;


import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.log.KLog;

import java.io.File;

/**
 * Created by Administrator on 2017/6/8 0008.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
class DeleteFileHandler implements IPushHandler {

    @Override
    public void handle(PushMessage pm) {
        String path = pm.getData();
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            KLog.i(String.format("删除文件：%s", file.toString()));
        } else {
            deleteDir(file.getPath());
        }
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_DELETE_FILE;
    }

    /**
     * 删除文件夹和文件夹里面的文件
     *
     * @param filePath filePath
     */
    private static void deleteDir(String filePath) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                // 删除所有文件
                file.delete();
            } else if (file.isDirectory()) {
                // 递规的方式删除文件夹
                deleteDir(file.getPath());
            }
        }
        dir.delete();// 删除目录本身
        KLog.i(String.format("删除文件夹：%s", filePath));
    }
}
