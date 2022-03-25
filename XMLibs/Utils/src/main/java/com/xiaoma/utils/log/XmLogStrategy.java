package com.xiaoma.utils.log;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.LogStrategy;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by iSun on 2018/9/5 0005.
 */

public class XmLogStrategy implements LogStrategy {
    @NonNull
    private final Handler handler;

    public XmLogStrategy(@NonNull Handler handler) {
        this.handler = LogUtils.checkNotNull(handler);
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        LogUtils.checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(priority, message));
    }

    static class WriteHandler extends Handler {
        private static final String TAG = "KLog";
        StringBuilder cache = new StringBuilder();

        @NonNull
        private final String savePath;
        private int currentLines;
        private LogConfig logConfig;
        private Context mContext;

        WriteHandler(Context context, @NonNull Looper looper, LogConfig logConfig) {
            super(LogUtils.checkNotNull(looper));
            mContext = context;
            this.savePath = LogUtils.checkNotNull(LogUtils.getAppLogPath(logConfig, mContext));
            this.logConfig = logConfig;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;
            if (logConfig.logCacheLines <= 1) {
                writeLog(content);
            } else if (currentLines < logConfig.logCacheLines && !TextUtils.isEmpty(content)) {
                cache.append(content);
                currentLines++;
                return;
            } else {
                currentLines = 0;
                writeLog(cache.toString());
                cache.delete(0, cache.length());
            }

        }

        private void writeLog(String cache) {
            FileWriter fileWriter = null;
            File logFile = getLogFile(savePath);
            try {
                fileWriter = new FileWriter(logFile, true);
                if (logConfig.isShowVerInfo && logFile.length() == 0) {
                    writeLog(fileWriter, getAppVerInfo());
                }
                writeLog(fileWriter, cache.toString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                Log.e("Exception", e.getMessage());
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            LogUtils.checkNotNull(fileWriter);
            LogUtils.checkNotNull(content);

            fileWriter.append(content);
        }

        File logFile;

        private File getLogFile(@NonNull String folderName) {
            LogUtils.checkNotNull(folderName);
            obsoleteLogFile(logConfig, mContext);
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (logFile == null || logFile.length() >= logConfig.logMaxBytes) {
                String newFileName = getNewFileName();
                logFile = new File(folderName, newFileName);
                Log.e(TAG, " Latest Log file " + newFileName);
                return logFile;
            }

            return logFile;
        }

        public String getNewFileName() {
            Date d = new Date();
            String timeStr = LogUtils.dateToString(d, "yyyyMMddHHmmssSSS");
            String name = String.format("%s.%s", timeStr, logConfig.format);
            return name;
        }
    }


    private static boolean obsoleteLogFile(final LogConfig logConfig, Context context) {
        File file = new File(LogUtils.getAppLogPath(logConfig, context));
        File[] files = file.listFiles();
        if (files != null && files.length >= logConfig.logMaxCount) {
            List<File> list = Arrays.asList(files);
            Collections.sort(list, new Comparator<File>() {
                public int compare(File p1, File p2) {
                    if (LogUtils.getLogFileId(logConfig.format, p1) > LogUtils.getLogFileId(logConfig.format, p2)) {
                        return 1;
                    }
                    if (LogUtils.getLogFileId(logConfig.format, p1) == LogUtils.getLogFileId(logConfig.format, p2)) {
                        return 0;
                    }
                    return -1;
                }
            });
            for (int i = 0; i < list.size() - logConfig.logMaxCount; i++) {
                File f = list.get(i);
                FileUtils.delete(f);
            }
        }
        return true;
    }

    private static String getAppVerInfo() {
        String channel = ConfigManager.ApkConfig.getChannelID();
        String server = ConfigManager.ApkConfig.getBuildSerEnv();
        int verCode = ConfigManager.ApkConfig.getBuildVersionCode();
        String verName = ConfigManager.ApkConfig.getBuildVersionName();
        return new StringBuilder().append("appVerCode:").append(verCode)
                .append("  appVerName:").append(verName)
                .append("  channel:").append(channel)
                .append("  server:").append(server)
                .append("\n").toString();
    }

}
