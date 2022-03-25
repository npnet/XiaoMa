package com.xiaoma.login.common;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.utils.ConfigFileUtils;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.thread.ThreadDispatcher;

import java.io.File;

public class ActivationUtils {

    //上传激活信息
    public static void uploadActiveStatus() {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                final File activeFile = ConfigManager.FileConfig.getActiveFile();
                String activeStatus = ConfigFileUtils.read(activeFile);
                if (!"true".equals(activeStatus)) {
                    RequestManager.activeCar(new ResultCallback<XMResult<String>>() {
                        @Override
                        public void onSuccess(XMResult<String> result) {
                            ConfigFileUtils.writeCover("true", activeFile);
                        }

                        @Override
                        public void onFailure(int code, String msg) {

                        }
                    });
                }
            }
        });
    }
}
