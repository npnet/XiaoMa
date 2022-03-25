package com.xiaoma.autotracker.upload;

import com.xiaoma.config.ConfigManager;

/**
 * @author taojin
 * @date 2018/12/10
 */
public interface AutoTrackAPI {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getLog();
    String BASE_URL_UPLOAD = ConfigManager.EnvConfig.getEnv().getBusiness();
    String BASE_URL_FILE = ConfigManager.EnvConfig.getEnv().getFile();

    //---------------上报相关-------------------
    //批量上报
    String BATCH_UPLOAD_URL = BASE_URL + "log/batchNewVersionAddOperationLog.action";

    //直接上报
    String UPLOAD_URL = BASE_URL + "log/newVersionAddOperationLogJsonParams.action";

    //埋点事件上报
    String UPLOAD_EVENT_URL = BASE_URL_UPLOAD + "score/singleUpload";

    //上传截屏
    String UPLOAD_LOG_FILE_URL = BASE_URL_FILE + "wnsin/uploadUserLogFile.action";
}
