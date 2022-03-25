package com.xiaoma.utils.log;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.orhanobut.logger.Logger;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iSun on 2018/9/7 0007.
 */

public class LogConfig implements Serializable {
    public boolean isShowLog = true;
    public boolean isSaveLog = true;

    public boolean isChangeConfigFromSD = true;
    public boolean isDebugFilter = false;
    public boolean isShowVerInfo = true;

    public int logMaxBytes = 1024 * 1024;// *KB
    public int logMaxCount = 10;
    public int printLogLevel = Logger.ERROR;//1-7
    public int fileLogLevel = Logger.ERROR;//1-7
    public int logCacheLines = 3;//0-10
    public int threadPriority = Thread.MIN_PRIORITY;
    public int methodCount = 2;//调用链显示方法数

    public String logFilePath = ConfigManager.FileConfig.getLogFolder().getAbsolutePath();
    public String configName = ConfigManager.FileConfig.getGlobalConfigFolder() + "/logConfig.json";
    public String tag = "XM:" + ConfigManager.ApkConfig.getBuildVersionCode();//不能为空
    public String format = "txt";


    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public boolean isDebugFilter() {
        return isDebugFilter;
    }

    public void setDebugFilter(boolean debugFilter) {
        this.isDebugFilter = debugFilter;
    }


    public LogConfig setIsShowLog(boolean isShowLog) {
        this.isShowLog = isShowLog;
        return this;
    }


    public LogConfig setIsSaveLog(boolean isSaveLog) {
        this.isSaveLog = isSaveLog;
        return this;
    }


    public LogConfig setLogMaxBytes(int logMaxBytes) {
        this.logMaxBytes = logMaxBytes;
        return this;
    }


    public LogConfig setLogMaxCount(int logMaxCount) {
        this.logMaxCount = logMaxCount;
        return this;
    }

    public LogConfig setFormat(String format) {
        this.format = format;
        return this;
    }


    public LogConfig setPrintLogLevel(int printLogLevel) {
        this.printLogLevel = printLogLevel;
        return this;
    }


    public LogConfig setFileLogLevel(int diskLogLevel) {
        fileLogLevel = diskLogLevel;
        return this;
    }


    public LogConfig setLogCacheLines(int logCacheLines) {
        this.logCacheLines = logCacheLines;
        return this;
    }

    public LogConfig setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
        return this;
    }


    public void setMethodCount(int methodCount) {
        this.methodCount = methodCount;
    }

    public static LogConfig getDefaultConfig() {
        return new LogConfig();
    }

    public static LogConfig parseFormJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        LogConfig logConfig = null;
        try {
            Gson gson = new Gson();
            logConfig = gson.fromJson(json, LogConfig.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return logConfig;
    }

    //该方法需在子线程执行，adb命令进行了阻塞操作
    public static String zipLogFile(Context context, boolean isAdbLog, String packageName) {
        //先清空日志压缩目录，防止文件累加
        String zipCacheFilePath = ConfigManager.FileConfig.getLogUploadFolderPath();
        File zipFileDir = new File(zipCacheFilePath);
        if (zipFileDir != null && zipFileDir.exists()) {
            FileUtils.delete(zipFileDir);
        }
        StringBuffer fileName = new StringBuffer(ConfigManager.FileConfig.getLogUploadFolder().getAbsolutePath());
        fileName.append(File.separator);
        fileName.append(new SimpleDateFormat("yyyyMMddHHmmss").format((new Date(System.currentTimeMillis()))));
        fileName.append("-");
        fileName.append(ConfigManager.DeviceConfig.getICCID(context));
        String logFile = "";
        String zipFile = fileName.toString() + ".zip";
        if (isAdbLog) {
            try {
                logFile = fileName.toString() + ".log";
                Process process = Runtime.getRuntime().exec("logcat -d -f " + logFile);
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (TextUtils.isEmpty(packageName)) return "";
            logFile = ConfigManager.FileConfig.getLogFolder().getAbsolutePath() + File.separator + packageName;
        }
        try {
            if (ZipUtils.zipFile(logFile, zipFile)) {
                File file = new File(zipFile);
                if (file.exists() && file.length() > 0) return zipFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
