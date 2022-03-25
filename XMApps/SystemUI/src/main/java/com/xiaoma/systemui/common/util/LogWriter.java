package com.xiaoma.systemui.common.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.xiaoma.systemui.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogWriter {
    private static final boolean WRITE_LOG = BuildConfig.DEBUG;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;
    private static final DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    private File mLogFile;
    private FileWriter sWriter;
    private Context mContext;

    public LogWriter(Context context, String logFileName) {
        mContext = context;
        if (WRITE_LOG) {
            mLogFile = new File(Environment.getExternalStorageDirectory(), logFileName);
            if (mLogFile.exists() && mLogFile.length() > MAX_FILE_SIZE) {
                mLogFile.delete();
            }
            try {
                sWriter = new FileWriter(mLogFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startup() {
        if (WRITE_LOG) {
            try {
                sWriter = new FileWriter(mLogFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printLog(String format, Object... args) {
        printLog(String.format(format, args));
    }

    public void printLog(final String log) {
        if (WRITE_LOG) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String pkgVersion = "";
                        try {
                            pkgVersion = mContext.getPackageManager()
                                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sWriter.write(String.format("%s / %s : %s\n",
                                sDateFormat.format(System.currentTimeMillis()), pkgVersion, log));
                        sWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
