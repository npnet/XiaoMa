package com.xiaoma.utils.log;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;

/**
 * Created by iSun on 2018/9/7 0007.
 */

public class LogUtils {

    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    @NonNull
    static <T> T checkNotNull(@Nullable final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static long getLogFileId(String fileFormat, File file) {
        long fileId = 0l;
        if (file != null && file.getName() != null) {
            String path = file.getName();
            try {
                fileId = Long.valueOf(path.replace(String.format(".%s", fileFormat), ""));
            } catch (Exception e) {
                FileUtils.delete(file);//文件名不符合规则
                e.printStackTrace();
            }
        }
        return fileId;
    }


    public static LogConfig readConfigFromSDcard(File configFile) {
        String fileContent = FileUtils.read(configFile);
        LogConfig logConfig = LogConfig.parseFormJson(fileContent);
        if (logConfig == null) {
            return LogConfig.getDefaultConfig();
        } else {
            return logConfig;
        }
    }

    static String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "VERBOSE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            case ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }


    //合法性检测
    public static LogConfig checkConfigLegality(LogConfig logConfig) {
        long logMinBytes = 100 * 1024;//100K
        long logMaxBytes = 20 * 1024 * 1024;//20M
        if (logConfig == null) {
            return LogConfig.getDefaultConfig();
        }
        if (logConfig.logMaxCount < 1 || logConfig.logMaxCount > 100) {
            logConfig.logMaxCount = LogConfig.getDefaultConfig().logMaxCount;
        }
        if (logConfig.logMaxBytes < logMinBytes || logConfig.logMaxBytes > logMaxBytes) {
            logConfig.logMaxBytes = LogConfig.getDefaultConfig().logMaxBytes;
        }
        if (logConfig.printLogLevel < 1 || logConfig.printLogLevel > 7) {
            logConfig.printLogLevel = LogConfig.getDefaultConfig().printLogLevel;
        }
        if (logConfig.fileLogLevel < 1 || logConfig.fileLogLevel > 7) {
            logConfig.fileLogLevel = LogConfig.getDefaultConfig().fileLogLevel;
        }
        if (logConfig.logCacheLines < 1 || logConfig.logCacheLines > 50) {
            logConfig.logCacheLines = LogConfig.getDefaultConfig().logCacheLines;
        }
        if (logConfig.threadPriority < 1 || logConfig.threadPriority > 10) {
            logConfig.threadPriority = LogConfig.getDefaultConfig().threadPriority;
        }
        return logConfig;
    }

    public static void reSaveDefaultConfigToSdcard() {
        reSaveDefaultConfigToSdcard(LogConfig.getDefaultConfig());
    }

    //重新初始化本地日志配置
    public static void reSaveDefaultConfigToSdcard(LogConfig config) {
        String s = new Gson().toJson(config);
        FileUtils.write(s, new File(config.configName), false);
    }

    //clearAllData all
    public static void clearAllLogFile(LogConfig logConfig) {
        if (logConfig != null && !TextUtils.isEmpty(logConfig.logFilePath)) {
            FileUtils.delete(logConfig.logFilePath);
        }
    }


    public static boolean getLogcatLog() {
        boolean result = false;
        Date d = new Date();
        String timeStr = LogUtils.dateToString(d, "yyyyMMddHHmmss");
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!TextUtils.isEmpty(absolutePath)) {
            result = getLogcatLog(String.format("%s/xm_log_%s.txt", absolutePath, timeStr));
        }
        return result;
    }

    //获取LogCat日志并保存
    public static boolean getLogcatLog(String fileName) {
        boolean result = false;
        try {
            ArrayList commandLine = new ArrayList();
            commandLine.add("logcat");
            commandLine.add("-v");
            commandLine.add("time");
            commandLine.add("-f");
            commandLine.add(fileName);
            String[] objects = (String[]) commandLine.toArray(new String[commandLine.size()]);
            result = adbShell(objects);
        } catch (Exception e) {
            Log.e("get LogCat Error", e.getMessage());
        }
        return result;
    }

    public static String getAppLogPath(LogConfig logConfig, Context context) {
        if (context == null) {
            return logConfig.logFilePath;
        } else {
            return logConfig.logFilePath + "/" + context.getPackageName();
        }
    }

    //最近日志文件
    public static File getLastLogFile(final LogConfig logConfig, Context context) {
        File file = new File(LogUtils.getAppLogPath(logConfig, context));
        File[] files = file.listFiles();
        if (files.length > 0) {
            List<File> list = Arrays.asList(files);
            Collections.sort(list, new Comparator<File>() {
                public int compare(File p1, File p2) {
                    if (getLogFileId(logConfig.format, p1) > getLogFileId(logConfig.format, p2)) {
                        return 1;
                    }
                    if (LogUtils.getLogFileId(logConfig.format, p1) == LogUtils.getLogFileId(logConfig.format, p2)) {
                        return 0;
                    }
                    return -1;
                }
            });
            return list.get(list.size() - 1);
        }
        return null;
    }


    public static boolean adbShell(String[] shell) {
        boolean result = true;
        try {
            Runtime.getRuntime().exec(shell);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    public static void adbShell(String shellStr) {
        try {
            adbShell(shellStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
